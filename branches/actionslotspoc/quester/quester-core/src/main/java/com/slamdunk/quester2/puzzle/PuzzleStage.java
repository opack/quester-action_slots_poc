package com.slamdunk.quester2.puzzle;

import static com.slamdunk.quester2.Quester2.screenHeight;
import static com.slamdunk.quester2.Quester2.screenWidth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.utils.Config;
import com.slamdunk.quester2.puzzle.Puzzle.PuzzleChangeListener;

/**
 * Gère l'UI (affichage, déplacement des items...) d'un puzzle
 */
public class PuzzleStage extends Stage implements PuzzleChangeListener {
	private static final float SWITCH_SPEED = Config.asFloat("puzzle.switchSpeed", 0.2f);
	
	private Puzzle puzzle;
	private PuzzleImage[][]images;
	private Table puzzleTable;
	
	public PuzzleStage(Puzzle puzzle) {
		// Définition du puzzle
		this.puzzle = puzzle;
		puzzle.setListener(this);
		
		// Création des images des attributs
		initActors();
		
		// Création de la caméra
		OrthographicCamera camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		setCamera(camera);
 		
 		// Définition du stage comme gérant lui-même les input
 		PuzzleSwitchInputProcessor processor = new PuzzleSwitchInputProcessor(puzzleTable);
 		Gdx.input.setInputProcessor(processor);
 		
 		// Ajout du Stage et du puzzle comme listeners de switch
 		processor.addListener(puzzle);
	}

	/**
	 * Crée les acteurs du stage représentant le puzzle
	 */
	private void initActors() {
		// Création de la table
		puzzleTable = new Table();
		addActor(puzzleTable);
		
		// Création des images qui remplissent la table
		images = new PuzzleImage[puzzle.getWidth()][puzzle.getHeight()];
		final int imageWidth = Config.asInt("puzzle.item.width", 48);
		final int imageHeight = Config.asInt("puzzle.item.height", 48);
		for (int y = puzzle.getHeight() - 1; y > -1; y --) {
			for (int x = 0; x < puzzle.getWidth(); x ++) {
				// Récupération de l'attribut
				PuzzleAttributes attribute = puzzle.get(x, y);
				
				// Création d'une image
				PuzzleImage image = new PuzzleImage(attribute);
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
	}

	public void render(float delta) {
		// Efface l'écran
		//Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Met à jour les acteurs
		act(delta);
		
		// Dessine le résultat
		draw();
	}

	@Override
	public void onAttributesSwitched(int firstX, int firstY, int secondX, int secondY) {
		// Faire une animation échangeant les images
		PuzzleImage firstImage = images[firstX][firstY];
		PuzzleImage secondImage = images[secondX][secondY];
		firstImage.addAction(Actions.moveTo(
			secondImage.getX(), secondImage.getY(),
			SWITCH_SPEED));
		secondImage.addAction(Actions.moveTo(
			firstImage.getX(), firstImage.getY(),
			SWITCH_SPEED));
	}
}
