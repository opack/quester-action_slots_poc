package com.slamdunk.quester2.puzzle;

import static com.slamdunk.quester2.Quester2.screenHeight;
import static com.slamdunk.quester2.Quester2.screenWidth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.utils.Config;
import com.slamdunk.quester2.puzzle.PuzzleSwitchInputProcessor.SwitchListener;

/**
 * Gère l'UI (affichage, déplacement des items...) d'un puzzle
 */
public class PuzzleStage extends Stage implements SwitchListener {
	private static final float SWITCH_SPEED = Config.asFloat("puzzle.switchSpeed", 0.2f);
	private static final float FALL_SPEED = Config.asFloat("puzzle.fallSpeed", 0.2f);
	private static final float REMOVE_SPEED = Config.asFloat("puzzle.removeSpeed", 0.2f);
	
	private int puzzleWidth;
	private int puzzleHeight;
	private PuzzleLogic puzzleLogic;
	private PuzzleImage[][]puzzleImages;
	private Table puzzleTable;
	
	/**
	 * Indique si le stage est dans un état stable. Si false, c'est qu'il y a une
	 * animation en cours (apparition d'un attribut, chute ou switch d'un attribut...)
	 */
	private boolean isFallRequested;
	private boolean isMatchRequested;
	
	/**
	 * Indique si un switch à l'initiative de l'utilisateur est en cours
	 */
	private boolean isUserSwitching;
	private int[] userSwitchingPos;

	private float tableRowHeight;

	private float tableColWidth;

	private final int puzzleItemWidth;
	private final int puzzleItemHeight;
	
	public PuzzleStage(int puzzleWidth, int puzzleHeight) {
		// Définition du puzzle
		this.puzzleWidth = puzzleWidth;
		this.puzzleHeight = puzzleHeight;
		puzzleImages = new PuzzleImage[puzzleWidth][puzzleHeight];
		this.puzzleLogic = new PuzzleLogic(this);
		
		// Création de la table
		puzzleItemWidth = Config.asInt("puzzle.item.width", 48);
		puzzleItemHeight = Config.asInt("puzzle.item.height", 48);
		puzzleTable = new Table();
		addActor(puzzleTable);
		
		// Création de la caméra
		OrthographicCamera camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		setCamera(camera);
 		
 		// Définition du stage comme gérant lui-même les input
 		PuzzleSwitchInputProcessor processor = new PuzzleSwitchInputProcessor(this, puzzleTable);
 		Gdx.input.setInputProcessor(processor);
 		
 		// Ajout du Stage et du puzzle comme listeners de switch
 		processor.addListener(this);
 		
 		//
 		isUserSwitching = false;
 		userSwitchingPos = new int[4];
	}
	
	public int getPuzzleWidth() {
		return puzzleWidth;
	}

	public void setPuzzleWidth(int puzzleWidth) {
		this.puzzleWidth = puzzleWidth;
	}

	public int getPuzzleHeight() {
		return puzzleHeight;
	}

	public void setPuzzleHeight(int puzzleHeight) {
		this.puzzleHeight = puzzleHeight;
	}
	
	public PuzzleImage[][] getPuzzleImages() {
		return puzzleImages;
	}

	/**
	 * Crée les acteurs du stage représentant le puzzle
	 */
	public void initPuzzle() {
		puzzleTable.clear();
		
		// Création des images qui remplissent la table
		PuzzleAttributes attribute = PuzzleAttributes.EMPTY;
		PuzzleImage image;
		for (int y = puzzleHeight - 1; y > -1; y --) {
			for (int x = 0; x < puzzleWidth; x ++) {
				// Récupération de l'attribut
				attribute = puzzleLogic.initAttribute(x, y);
				//DBG attribute = PuzzleAttributes.valueOf(Config.asString("puzzle." + x + "." + y, "EMPTY"));
				
				// Création d'une image
				image = createPuzzleImage(x, y, attribute, false);

				// Ajout de l'image au stage
				puzzleTable.add(image).size(puzzleItemWidth, puzzleItemHeight).pad(2);
				puzzleImages[x][y] = image;
			}
			puzzleTable.row();
		}
		puzzleTable.pack();
		
		// Stockage des positions des images pour faciliter les animations
		tableColWidth = puzzleImages[1][0].getX() - puzzleImages[0][0].getX() ;
		tableRowHeight = puzzleImages[0][1].getY() - puzzleImages[0][0].getY();
	}

	public void render(float delta) {
		// Efface l'écran
		//Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Met à jour les acteurs
		act(delta);
		
		if (checkSteady()) {
			// Si le tableau est stable, alors on regarde s'il faut le mettre à jour
			if (isUserSwitching) {
				if (!puzzleLogic.switchAttributes(userSwitchingPos[0], userSwitchingPos[1], userSwitchingPos[2], userSwitchingPos[3])) {
					// Si le switch a été interdit, on replace les éléments dans leur ordre original
					switchAttributes(userSwitchingPos[0], userSwitchingPos[1], userSwitchingPos[2], userSwitchingPos[3]);
				}
				// Reset le flag APRES l'appel à puzzleLogic, car si un MatchEffect est déclenché
				// il voudra peut-être savoir si l'utilisateur est à l'origine du match ou si
				// c'est à cause d'une chute
				isUserSwitching = false;
			} else if (isFallRequested) {
				isFallRequested = false;
				// Il faut faire un match si des attributs sont tombés ou qu'on avait déjà demandé d'en faire un
				isMatchRequested |= puzzleLogic.fall();
			} else if (isMatchRequested) {
				isMatchRequested = false;
				puzzleLogic.match();
			}
		}
		
		// Dessine le résultat
		draw();
	}

	/**
	 * Vérifie si tous les acteurs ont achevé leur action
	 */
	public boolean checkSteady() {
		// Vérifie si au moins un acteur du stage est en action
		for (Actor actor : getActors()) {
			if (actor.getActions().size > 0) {
				return false;
			}
		}
		// Vérifie si au moins un acteur de la table est en action
		for (Actor actor : puzzleTable.getChildren()) {
			if (actor.getActions().size > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onPuzzleSwitch(int firstX, int firstY, int secondX, int secondY) {
		// Switch requis par l'utilisateur
		isUserSwitching = true;
		userSwitchingPos[0] = firstX;
		userSwitchingPos[1] = firstY;
		userSwitchingPos[2] = secondX;
		userSwitchingPos[3] = secondY;
		switchAttributes(firstX, firstY, secondX, secondY);
	}
	
	public void switchAttributes(int firstX, int firstY, int secondX, int secondY) {
		// Récupère les 2 images
		PuzzleImage firstImage = puzzleImages[firstX][firstY];
		PuzzleImage secondImage = puzzleImages[secondX][secondY];
		
		// Anime l'échange
		createSwitchAnimation(firstImage, secondImage);
		
		// Effectue l'échange des attributs
		PuzzleAttributes tmp = firstImage.getAttribute();
		firstImage.setAttribute(secondImage.getAttribute());
		secondImage.setAttribute(tmp);
	}
	
	public void removeAttribute(int x, int y) {
		createRemoveAnimation(puzzleImages[x][y]);
	}

	private void createRemoveAnimation(final PuzzleImage image) {
		image.setVisible(false);
		final PuzzleImage animImage = createPuzzleImage(image, true);
		animImage.addAction(Actions.sequence(
			Actions.alpha(0, REMOVE_SPEED),
			new Action() {
				@Override
				public boolean act(float delta) {
					System.out
							.println("PuzzleStage.createRemoveAnimation(...).new Action() {...}.act()");
					image.setVisible(true);
					animImage.remove();
					return true;
				}
			}
		));
		addActor(animImage);
	}

	public void createAttribute(int x, int y, final PuzzleAttributes attribute) {
		final PuzzleImage image = puzzleImages[x][y];
		
		// Affectation de l'attribut, et donc de l'image
		image.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				// Affecte l'image
				image.setAttribute(attribute);
				
				// Cache l'image, pour la faire apparaître avec un joli alpha
				image.getColor().a = 0;
				return true;
			}
		});
		
		// Jolie animation
		image.addAction(Actions.alpha(1, 0.2f, Interpolation.exp5));
	}

	/**
	 * Crée un PuzzleImage et le fait descendre vers sa destination. Une fois arrivé,
	 * il assigne son attribut
	 * @param x
	 * @param yFall
	 * @param yEmpty
	 */
	public void createFallAnimation(int x, int yFall, int yEmpty) {
		final PuzzleImage animImage;
		if (yFall < puzzleHeight) {
			// Création de l'image avec l'attribut qui va tomber
			// au-dessus de l'image actuelle avec vidage de cette image
			animImage = createPuzzleImage(puzzleImages[x][yFall], true);
		} else {
			// Création de l'image avec un attribut au hasard
			animImage = createPuzzleImage(x, yFall, null, true);
			animImage.setPosition(x * tableColWidth, yFall * tableRowHeight);
		}
		
		// Animation de l'image vers la position à combler
		final PuzzleImage emptyImage = puzzleImages[x][yEmpty];
		animImage.addAction(Actions.sequence(
			Actions.moveTo(emptyImage.getX(), emptyImage.getY(), FALL_SPEED * (yFall - yEmpty)),
			new Action() {
				@Override
				public boolean act(float arg0) {
					// Suppression de l'image
					animImage.remove();
					
					// Assignation de l'attribut tombé à l'image destination
					emptyImage.setAttribute(animImage.getAttribute());
					return true;
				}
			})
		);
		addActor(animImage);
	}
	
	public void createSwitchAnimation(final PuzzleImage firstImage, final PuzzleImage secondImage) {
		firstImage.setVisible(false);
		final PuzzleImage firstAnim = createPuzzleImage(firstImage, false);
		firstAnim.addAction(Actions.sequence(
			Actions.moveTo(secondImage.getX(), secondImage.getY(), SWITCH_SPEED),
			new Action() {
				@Override
				public boolean act(float delta) {
					firstImage.setVisible(true);
					firstAnim.remove();
					return true;
				}
			}
		));
		addActor(firstAnim);

		secondImage.setVisible(false);
		final PuzzleImage secondAnim = createPuzzleImage(secondImage, false);
		secondAnim.addAction(Actions.sequence(
			Actions.moveTo(firstImage.getX(), firstImage.getY(), SWITCH_SPEED),
			new Action() {
				@Override
				public boolean act(float delta) {
					secondImage.setVisible(true);
					secondAnim.remove();
					return true;
				}
			}
		));
		addActor(secondAnim);
	}
	
	/**
	 * Crée un nouveau PuzzleImage en en copiant un autre. La position et la taille sont
	 * également copiés.
	 * @param model
	 * @param shouldEmptyModel
	 * @return
	 */
	private PuzzleImage createPuzzleImage(PuzzleImage model, boolean shouldEmptyModel) {
		PuzzleImage image = new PuzzleImage(model.getAttribute());
		
		image.setPuzzleX(model.getPuzzleX());
		image.setPuzzleY(model.getPuzzleY());
		image.setX(model.getX());
		image.setY(model.getY());
		
		image.setWidth(model.getWidth());
		image.setHeight(model.getHeight());
		image.setScaling(Scaling.fit);
		
		if (shouldEmptyModel) {
			model.setAttribute(PuzzleAttributes.EMPTY);
		}
		return image;
	}
	
	/**
	 * Crée un nouveau PuzzleImage à partir d'un attribut.
	 * @param x
	 * @param y
	 * @param attribute Si null, un attribut de base est choisit aléatoirement
	 * @param shouldAnimateApperance Si true, l'image apparaît progressivement
	 * @return
	 */
	private PuzzleImage createPuzzleImage(int x, int y,	PuzzleAttributes attribute, boolean shouldAnimateApperance) {
		// Si aucun attribut n'a été spécifié, on en choisit un aléatoirement
		if (attribute == null) {
			attribute = PuzzleAttributesHelper.getRandomBaseAttribute();
		}
		
		PuzzleImage image = new PuzzleImage(attribute);
		
		image.setPuzzleX(x);
		image.setPuzzleY(y);
		
		image.setScaling(Scaling.fit);
		image.setSize(puzzleItemWidth, puzzleItemHeight);
		
		// Animation d'apparition
		if (shouldAnimateApperance) {
			image.getColor().a = 0;
			image.addAction(Actions.alpha(1, 0.2f, Interpolation.exp5));
		}
		return image;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y=puzzleHeight-1; y > -1; y--) {
			for (int x = 0; x < puzzleWidth; x++) {
				sb.append(puzzleImages[x][y].getAttribute().toString().substring(0, 3)).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public void fall() {
		isFallRequested = true;
	}

	public PuzzleAttributes getAttribute(int x, int y) {
		return puzzleImages[x][y].getAttribute();
	}
}
