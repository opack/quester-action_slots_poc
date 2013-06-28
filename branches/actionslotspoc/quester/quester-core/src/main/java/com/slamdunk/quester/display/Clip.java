package com.slamdunk.quester.display;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Clip extends Animation {
	/**
	 * Indique quelle proportion de l'espace restant doit �tre
	 * laiss�e � gauche du dessin de la frame.
	 * Mettre 0.0 indique qu'il faut aligner la frame � gauche de
	 * la zone de dessin du clip. Mettre 1.0 indique qu'il faut
	 * aligner � droite.
	 */
	public float alignX;
	/**
	 * Indique quelle proportion de l'espace restant doit �tre
	 * laiss�e en bas du dessin de la frame.
	 * Mettre 0.0 indique qu'il faut aligner la frame en bas de
	 * la zone de dessin du clip. Mettre 1.0 indique qu'il faut
	 * aligner en haut.
	 */
	public float alignY;
	private TextureRegion currentFrame;
	/**
	 * Zone de dessin dans laquelle seront dessin�es les frames
	 */
	public Rectangle drawArea;
	/**
	 * Indique si la frame doit �tre invers�e horizontalement
	 */
	public boolean flipH;
	/**
	 * Indique si la frame doit �tre invers�e verticalement
	 */
	public boolean flipV;
	private Runnable[] keyFrameRunnables;
	/**
	 * Indique un offset par rapport � la gauche, en pourcentage
	 * de la largeur de la zone de dessin.
	 */
	public float offsetX;
	/**
	 * Indique un offset par rapport au bas, en pourcentage
	 * de la hauteur de la zone de dessin.
	 */
	public float offsetY;
	/**
	 * Facteur d'�chelle � appliquer en largeur
	 */
	public float scaleX;
	/**
	 * Facteur d'�chelle � appliquer en hauteur
	 */
	public float scaleY;
	
	
	public Clip(float frameDuration, Array<? extends TextureRegion> keyFrames) {
		super(frameDuration, keyFrames);
		init(keyFrames.size);
	}
	
	public Clip(float frameDuration, Array<? extends TextureRegion> keyFrames, int playType) {
		super(frameDuration, keyFrames, playType);
		init(keyFrames.size);
	}
	
	public Clip(float frameDuration, TextureRegion[] keyFrames) {
		super(frameDuration, keyFrames);
		init(keyFrames.length);
	}
	
	public int getFrameCount() {
		return keyFrameRunnables.length;
	}
	
	/**
	 * Initialise le clip avec des valeurs par d�faut
	 * @param frameCount
	 */
	private void init(int frameCount) {
		keyFrameRunnables = new Runnable[frameCount];
		
		// Taille de la zone d'affichage inconnue
		drawArea = new Rectangle(0, 0, 1, 1);
		
		// Pas de mise � l'�chelle
		scaleX = 1.0f;
		scaleY = 1.0f;
		
		// Par d�faut, pas de flip
		flipH = false;
		flipV = false;
		
		// Alignement en bas � gauche
		alignX = 0.0f;
		alignY = 0.0f;
		
		// Pas de d�calage
		offsetX = 0.0f;
		offsetY = 0.0f;
	}
	
	/**
	 * Dessine la frame courante et r�alise l'actions associ�e
	 * � cette frame (son...)
	 */
	public void play(float stateTime, SpriteBatch batch) {
		// R�cup�re la trame courante
		currentFrame = getKeyFrame(stateTime, true);
		
		// D�termine la taille du dessin
		float frameWidth = currentFrame.getRegionWidth() * scaleX;
		float frameHeight = currentFrame.getRegionHeight() * scaleY;
		
		// D�termine les flips
		if (flipH) {
			frameWidth *= -1;
		}
		if (flipV) {
			frameHeight *= -1;
		}
		
		// D�termine la position de la frame
		float posX = drawArea.x + (drawArea.width - frameWidth) * alignX + drawArea.width * offsetX;
		float posY = drawArea.y + (drawArea.height - frameHeight) * alignY + drawArea.height * offsetY;
		
		// Dessine la frame
		batch.draw(
			currentFrame,
			posX,
			posY,
			frameWidth,
			frameHeight);
		
		// R�alise l'action associ�e
		final int index = getKeyFrameIndex(stateTime);
		final Runnable runnable = keyFrameRunnables[index];
		if (runnable != null) {
			runnable.run();
		}
	}
	
	/**
	 * Raccourci vers setKeyFrameRunnable() pour ajouter le runnable
	 * sur la premi�re frame
	 * @param runnable
	 */
	public void setFirstKeyFrameRunnable(Runnable runnable) {
		setKeyFrameRunnable(0, runnable);
	}

	public void setKeyFrameRunnable(int frame, Runnable runnable) {
		if (frame > -1 && frame < keyFrameRunnables.length) {
			keyFrameRunnables[frame] = runnable;
		}
	}
	
	/**
	 * Raccourci vers setKeyFrameRunnable() pour ajouter le runnable
	 * sur la derni�re frame
	 * @param runnable
	 */
	public void setLastKeyFrameRunnable(Runnable runnable) {
		setKeyFrameRunnable(keyFrameRunnables.length - 1, runnable);
	}
}
