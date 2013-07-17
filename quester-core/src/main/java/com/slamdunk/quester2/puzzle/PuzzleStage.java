package com.slamdunk.quester2.puzzle;

import static com.slamdunk.quester2.Quester2.screenHeight;
import static com.slamdunk.quester2.Quester2.screenWidth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
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
	private static final float SWITCH_SPEED = 1;//DBGConfig.asFloat("puzzle.switchSpeed", 0.2f);
	
	private int puzzleWidth;
	private int puzzleHeight;
	private PuzzleLogic puzzleLogic;
	private PuzzleImage[][]puzzleImages;
	private Vector2[][]tablePositions;
	private Table puzzleTable;
	
	/**
	 * Indique si le stage est dans un état stable. Si false, c'est qu'il y a une
	 * animation en cours (apparition d'un attribut, chute ou switch d'un attribut...)
	 */
	private boolean isSteady;
	/**
	 * Indique si un switch à l'initiative de l'utilisateur est en cours
	 */
	private boolean isUserSwitching;
	private int[] userSwitchingPos;
	
	public PuzzleStage(int puzzleWidth, int puzzleHeight) {
		// Définition du puzzle
		this.puzzleWidth = puzzleWidth;
		this.puzzleHeight = puzzleHeight;
		puzzleImages = new PuzzleImage[puzzleWidth][puzzleHeight];
		this.puzzleLogic = new PuzzleLogic(this);
		
		// Création de la table
		puzzleTable = new Table();
		addActor(puzzleTable);
		
		// Création de la caméra
		OrthographicCamera camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		setCamera(camera);
 		
 		// Définition du stage comme gérant lui-même les input
 		PuzzleSwitchInputProcessor processor = new PuzzleSwitchInputProcessor(puzzleTable);
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
		final int imageWidth = Config.asInt("puzzle.item.width", 48);
		final int imageHeight = Config.asInt("puzzle.item.height", 48);
		PuzzleAttributes attribute = PuzzleAttributes.UNKNOWN;
		PuzzleImage image;
		for (int y = puzzleHeight - 1; y > -1; y --) {
			for (int x = 0; x < puzzleWidth; x ++) {
				// Récupération de l'attribut
				attribute = puzzleLogic.initAttribute(x, y);
				
				// Création d'une image
				image = new PuzzleImage(attribute);
				image.setScaling(Scaling.fit);

				// Ajout de l'image au stage
				puzzleTable.add(image).size(imageWidth, imageHeight).pad(2);
				setPuzzleImage(x, y, image);
			}
			puzzleTable.row();
		}
		puzzleTable.pack();
		
		// Stockage des positions des images pour faciliter les animations
		tablePositions = new Vector2[puzzleWidth][puzzleHeight];
		for (int y = puzzleHeight - 1; y > -1; y --) {
			for (int x = 0; x < puzzleWidth; x ++) {
				image = puzzleImages[x][y];
				tablePositions[x][y] = new Vector2(image.getX(), image.getY());
			}
		}
	}

	public void render(float delta) {
		// Efface l'écran
		//Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Met à jour les acteurs
		act(delta);
		
		// Si des animations sont en cours, on regarde si elles sont finies
		if (!isSteady) {
			isSteady = checkSteady();
			if (isSteady) {
				// Si le stage est de nouveau stable, on avertit le puzzle
				updatePuzzle();
			}
		}
		
		// Dessine le résultat
		draw();
	}

	private void updatePuzzle() {
		if (isUserSwitching) {
			if (!puzzleLogic.switchAttributes(userSwitchingPos[0], userSwitchingPos[1], userSwitchingPos[2], userSwitchingPos[3])) {
				// Si le switch a été interdit, on replace les éléments dans leur ordre original
				switchAttributes(userSwitchingPos[0], userSwitchingPos[1], userSwitchingPos[2], userSwitchingPos[3]);
			}
			isUserSwitching = false;
		} else {
			puzzleLogic.updatePuzzle();
		}
	}

	/**
	 * Vérifie si tous les acteurs ont achevé leur action et met la variable
	 * isSteady à jour en conséquence.
	 */
	private boolean checkSteady() {
		for (Actor actor : getActors()) {
			if (actor.getActions().size > 0) {
				// Si au moins un acteur n'a pas fini, alors le stage n'est pas stable.
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
		// Récupération des images et de la position des cases dans lequelles ont doit les placer
		PuzzleImage firstImage = puzzleImages[firstX][firstY];
		Vector2 firstPos = tablePositions[firstX][firstY];
		PuzzleImage secondImage = puzzleImages[secondX][secondY];
		Vector2 secondPos = tablePositions[secondX][secondY];

		// Faire une animation échangeant les images
		firstImage.addAction(Actions.moveTo(secondPos.x, secondPos.y, SWITCH_SPEED));
		secondImage.addAction(Actions.moveTo(firstPos.x, firstPos.y, SWITCH_SPEED));
		isSteady = false;
		
		// Inversion effective des images dans le tableau d'images
		setPuzzleImage(firstX, firstY, secondImage);
		setPuzzleImage(secondX, secondY, firstImage);
	}
	
	private void setPuzzleImage(int x, int y, PuzzleImage image) {
		puzzleImages[x][y] = image;
		image.setPuzzleX(x);
		image.setPuzzleY(y);
	}

	public PuzzleAttributes removeAttribute(int x, int y) {
		PuzzleImage image = puzzleImages[x][y];
		// DBG Pour l'instant, on se contente de cacher l'image. En vérité, elle sera quasiment immédiatement remplacée.
		image.setAttribute(PuzzleAttributes.UNKNOWN);
		image.addAction(Actions.alpha(0, 0.5f, Interpolation.exp5));
		isSteady = false;
		return image.getAttribute();
	}

	public void createAttribute(int x, int y, PuzzleAttributes attribute) {
		PuzzleImage image = puzzleImages[x][y];
		
		// Affectation de l'attribut, et donc de l'image
		image.setAttribute(attribute);
		
		// Jolie animation
		image.getColor().a = 0;
		image.addAction(Actions.alpha(1, 0.2f, Interpolation.exp5));
		isSteady = false;
	}
}
