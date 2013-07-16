package com.slamdunk.quester2.puzzle;

import static com.slamdunk.quester2.Quester2.screenHeight;
import static com.slamdunk.quester2.Quester2.screenWidth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.utils.Config;
import com.slamdunk.quester2.puzzle.Puzzle.PuzzleChangeListener;

/**
 * G�re l'UI (affichage, d�placement des items...) d'un puzzle
 */
public class PuzzleStage extends Stage implements PuzzleChangeListener {
	private static final float SWITCH_SPEED = Config.asFloat("puzzle.switchSpeed", 0.2f);
	
	private Puzzle puzzle;
	private PuzzleImage[][]images;
	private Vector2[][]tablePositions;
	private Table puzzleTable;
	
	public PuzzleStage(Puzzle puzzle) {
		// D�finition du puzzle
		this.puzzle = puzzle;
		puzzle.setListener(this);
		
		// Cr�ation des images des attributs
		initActors();
		
		// Cr�ation de la cam�ra
		OrthographicCamera camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		setCamera(camera);
 		
 		// D�finition du stage comme g�rant lui-m�me les input
 		PuzzleSwitchInputProcessor processor = new PuzzleSwitchInputProcessor(puzzleTable);
 		Gdx.input.setInputProcessor(processor);
 		
 		// Ajout du Stage et du puzzle comme listeners de switch
 		processor.addListener(puzzle);
	}

	/**
	 * Cr�e les acteurs du stage repr�sentant le puzzle
	 */
	private void initActors() {
		// Cr�ation de la table
		puzzleTable = new Table();
		addActor(puzzleTable);
		
		// Cr�ation des images qui remplissent la table
		images = new PuzzleImage[puzzle.getWidth()][puzzle.getHeight()];
		final int imageWidth = Config.asInt("puzzle.item.width", 48);
		final int imageHeight = Config.asInt("puzzle.item.height", 48);
		PuzzleAttributes attribute;
		PuzzleImage image;
		for (int y = puzzle.getHeight() - 1; y > -1; y --) {
			for (int x = 0; x < puzzle.getWidth(); x ++) {
				// R�cup�ration de l'attribut
				attribute = puzzle.get(x, y);
				
				// Cr�ation d'une image
				image = new PuzzleImage(attribute);
				image.setPuzzleX(x);
				image.setPuzzleY(y);
				image.setScaling(Scaling.fit);
				
				// Ajout de l'image au stage
				images[x][y] = image;
				puzzleTable.add(image).size(imageWidth, imageHeight).pad(2);
			}
			puzzleTable.row();
		}
		puzzleTable.pack();
		
		// Stockage des positions des images pour faciliter les animations
		tablePositions = new Vector2[puzzle.getWidth()][puzzle.getHeight()];
		for (int y = puzzle.getHeight() - 1; y > -1; y --) {
			for (int x = 0; x < puzzle.getWidth(); x ++) {
				image = images[x][y];
				tablePositions[x][y] = new Vector2(image.getX(), image.getY());
			}
		}
	}

	public void render(float delta) {
		// Efface l'�cran
		//Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Met � jour les acteurs
		act(delta);
		
		// Dessine le r�sultat
		draw();
	}

	@Override
	public void onAttributesSwitched(int firstX, int firstY, int secondX, int secondY) {
		// R�cup�ration des images et de la position des cases dans lequelles ont doit les placer
		PuzzleImage firstImage = images[firstX][firstY];
		Vector2 firstPos = tablePositions[firstX][firstY];
		PuzzleImage secondImage = images[secondX][secondY];
		Vector2 secondPos = tablePositions[secondX][secondY];

		// Faire une animation �changeant les images
		firstImage.addAction(Actions.moveTo(secondPos.x, secondPos.y, SWITCH_SPEED));
		secondImage.addAction(Actions.moveTo(firstPos.x, firstPos.y, SWITCH_SPEED));
		
		// Inversion effective des images dans le tableau d'images
		images[firstX][firstY] = secondImage;
		secondImage.setPuzzleX(firstX);
		secondImage.setPuzzleY(firstY);
		
		images[secondX][secondY] = firstImage;
		firstImage.setPuzzleX(secondX);
		firstImage.setPuzzleY(secondY);
	}
	
	@Override
	public void onAttributeRemoved(int x, int y) {
		// DBG Pour l'instant, on se contente de cacher l'image. En v�rit�, elle sera quasiment imm�diatement remplac�e.
		images[x][y].setAttribute(null);
		images[x][y].addAction(Actions.alpha(0, 0.3f, Interpolation.exp5));
	}
}
