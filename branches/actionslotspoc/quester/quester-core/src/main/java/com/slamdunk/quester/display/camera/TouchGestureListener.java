package com.slamdunk.quester.display.camera;

import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.display.map.MapRenderer;

/**
 * G�re le zoom, le pan et transmet le tap au Stage pour qu'il le g�re.
 * @author Didier
 *
 */
public class TouchGestureListener extends GestureAdapter {
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	private static final float ZOOM_STEPS_IN_WIDTH = 10;
	private OrthographicCamera camera; 
	private float initialZoom;
	
	private float lastInitialDistance;
	private Stage stage;
	
	// Le zoom max permet d'afficher toute la largeur de la carte
	private final float zoomMax;
	// Le zoom max permet d'afficher 2 cases
	private final float zoomMin;
	
	public TouchGestureListener(MapRenderer mapRenderer) {
		this.camera = mapRenderer.getCamera();
		this.stage = mapRenderer.getStage();
		lastInitialDistance = -1;
		
		zoomMin = 2 * mapRenderer.getMap().getCellWidth() / screenWidth;
		zoomMax = mapRenderer.getMap().getMapWidth() * mapRenderer.getMap().getCellWidth() / screenWidth + ZOOM_STEP * 2;
	}
	
	public float getZoomMin() {
		return zoomMin;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// Modification de la position
		camera.position.add(-deltaX, deltaY, 0);
		return true;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// Un tap : on simule un touchDown puis un touchUp
		stage.touchDown((int)x, (int)y, count, button);
		if (stage.touchUp((int)x, (int)y, count, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (lastInitialDistance != initialDistance) {
			// D�but d'un nouveau zoom
			lastInitialDistance = initialDistance;
			initialZoom = camera.zoom;
			return true;
		} else {
			float newZoom = initialZoom + ((initialDistance - distance) / screenWidth * ZOOM_STEPS_IN_WIDTH * ZOOM_STEP);
			if (newZoom >= zoomMin && newZoom <= zoomMax) {
				camera.zoom = newZoom;
				return true;
			} else {
				return false;
			}
		}
		
	}
}