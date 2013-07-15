package com.slamdunk.quester2.puzzle;

import static com.slamdunk.quester2.Quester2.screenHeight;
import static com.slamdunk.quester2.Quester2.screenWidth;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.quester.utils.Config;

/**
 * Gère l'UI (affichage, déplacement des items...) d'un puzzle
 */
public class PuzzleStage extends Stage {
	private Puzzle puzzle;
	
	public PuzzleStage(Puzzle puzzle) {
		this.puzzle = puzzle;
		initActors();
		
		// Création de la caméra
		OrthographicCamera camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		setCamera(camera);
	}

	/**
	 * Crée les acteurs du stage représentant le puzzle
	 */
	private void initActors() {
		final int imageWidth = Config.asInt("puzzle.item.width", 48);
		final int imageHeight = Config.asInt("puzzle.item.height", 48);
		for (int y = puzzle.getHeight() - 1; y > -1; y ++) {
			for (int x = 0; x < puzzle.getWidth(); x ++) {
				// Récupération de l'attribut
				PuzzleAttributes attribute = puzzle.get(x, y);
				
				// Création d'une image
				Image image = new Image(attribute.getImage());
				
				// Layout de l'image comme il faut
				image.setX(x * imageWidth);
				image.setY(y * imageHeight);
				image.setWidth(imageWidth);
				image.setHeight(imageHeight);
			}
		}
	}

	public void render(float delta) {
		act(delta);
		draw();
	}
}
