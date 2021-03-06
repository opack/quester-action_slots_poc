package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.map.MapRenderer;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.SwitchControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

/**
 * Contient l'ensemble des comportements communs � tous les
 * �l�ments du monde, sans aucune logique de jeu.
 * @author Didier
 *
 */
public class WorldElementActor extends Group {
	/**
	 * Contr�leur (cerveau) de l'acteur
	 */
	protected WorldElementControler controler;
	
	/**
	 * Indique ce que fait l'acteur, pour choisir l'animation � dessiner
	 */
	protected QuesterActions currentAction;
	/**
	 * Objet qui sert d'interm�diaire avec la map
	 */
	private Image image;
	
	/**
	 * Indique que l'acteur est d�placement vers la gauche
	 */
	protected boolean isLookingLeft;
	
	/**
	 * Le monde dans lequel �volue l'Actor
	 */
//	private ActorMap map;
	
	/**
	 * Compteur utilis� pour cadencer les animations
	 */
	protected float stateTime;
	
	/**
	 * Position logique de l'�l�ment dans le monde
	 */
	private int worldX;
	
	private int worldY;
	
	public WorldElementActor(TextureRegion texture) {
		if (texture != null) {
			MapRenderer mapRenderer = GameControler.instance.getScreen().getMapRenderer();
			image = new Image(texture);
			image.setScaling(Scaling.stretch);
			image.setWidth(mapRenderer.getCellWidth());
			image.setHeight(mapRenderer.getCellHeight());
			addActor(image);
		}
		
		currentAction = QuesterActions.NONE;
	}
	
//DBG	public void centerCameraOnSelf() {
//		addAction(new CameraMoveToAction(
//				getX() + map.getCellWidth() / 2,
//				getY() + map.getCellHeight() / 2,
//				1.0f));
//	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		drawSpecifics(batch);
	}
	
	/**
	 * Dessine le clip ad�quat en fonction de l'action courante
	 * @param batch
	 */
	protected void drawClip(SpriteBatch batch) {
		Clip clip = getClip(currentAction);
		if (clip == null) {
			return;
		}
		stateTime += Gdx.graphics.getDeltaTime();
		clip.drawArea.x = getX();
		clip.drawArea.y = getY();
		clip.flipH = isLookingLeft;
		if (controler.isEnabled()) {
			clip.play(stateTime, batch);
		} else {
			// Si le personnage n'est pas activ�, on ne l'anime pas.
			// TODO : L'id�al serait d'afficher une image de lui dormant, ou de jouer une animation de lui qui dort
			clip.play(0, batch);
		}
	}

	/**
	 * Appel�e pendant le draw pour dessiner les particularit�s
	 * de ce WorldElement.
	 * @param batch
	 */
	protected void drawSpecifics(SpriteBatch batch) {
	}

	/**
	 * Retourne le clip � jouer lors de l'action sp�cifi�e
	 * Cette fonction doit �tre red�finie.
	 * @return
	 */
	public Clip getClip(QuesterActions action) {
		return null;
	}

	public WorldElementControler getControler() {
		return controler;
	}

	public QuesterActions getCurrentAction() {
		return currentAction;
	}

	public Image getImage() {
		return image;
	}

	/**
	 * Retourne le X exprim� en unit� de la map et pas en pixels
	 * @return
	 */
	public int getWorldX() {
		return worldX;
	}
	
	/**
	 * Retourne le Y exprim� en unit� de la map et pas en pixels
	 * @return
	 */
	public int getWorldY() {
		return worldY;
	}
	
	public void moveTo(int destinationX, int destinationY, float duration) {
		moveTo(destinationX, destinationY, duration, null);
	}
	
	public void moveTo(int destinationX, int destinationY, float duration, final SwitchControler controlerToNotify) {
		ActorMap map = GameControler.instance.getScreen().getMap();
		currentAction = QuesterActions.MOVE;
		isLookingLeft = destinationX <= worldX;
			
		setPositionInWorld(destinationX, destinationY);
//		if (controler.getData().element == MapElements.PLAYER) {
//				addAction(Actions.sequence(
//						// On d�place le joueur et la cam�ra
//						Actions.parallel(
//							Actions.moveTo(
//								destinationX * map.getCellWidth(),
//								destinationY * map.getCellHeight(),
//								duration),
//							new CameraMoveToAction(
//								destinationX * map.getCellWidth() + map.getCellWidth() / 2,
//								destinationY * map.getCellHeight() + map.getCellHeight() / 2,
//								duration)),
//						new Action() {
//							@Override
//							public boolean act(float delta) {
//								WorldElementActor.this.currentAction = QuesterActions.NONE;
//								return true;
//							}
//						}
//					)
//				);
//			}
//		} else {
			addAction(Actions.sequence(
				Actions.moveTo(
					destinationX * map.getCellWidth(),
					destinationY * map.getCellHeight(),
					duration),
				new Action() {
					@Override
					public boolean act(float delta) {
						WorldElementActor.this.currentAction = QuesterActions.NONE;
						if (controlerToNotify != null) {
							controlerToNotify.checkAlignments();
						}
						return true;
					}
				}
			));
//		}
	}
	
	public void setControler(WorldElementControler controler) {
		this.controler = controler;
	}

	public void setCurrentAction(QuesterActions action, int targetX) {
		// Si l'action change, on RAZ le compteur pour les animations
		if (action != currentAction) {
			stateTime = 0f;
		}
		this.currentAction = action;
		isLookingLeft = targetX <= worldX;
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		if (image != null) {
			image.setHeight(height);
		}
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Place l'acteur dans la case sp�cifi�e par la colonne
	 * et la ligne indiqu�es. Cette m�thode se charge simplement
	 * de convertir une unit� logiques (col/row) en unit� r�elle
	 * (x/y en pixels) et de mettre � jour le monde.
	 * @param worldX
	 * @param worldY
	 */
	public void setPositionInWorld(int newX, int newY) {
//		GameControler.instance.getScreen().getMap().updateMapPosition(
//			this,
//			worldX, worldY,
//			newX, newY);
		setWorldX(newX);
		setWorldY(newY);
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		if (image != null) {
			image.setSize(width, height);
		}
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		if (image != null) {
			image.setWidth(width);
		}
	}

	private void setWorldX(int worldX) {
		this.worldX = worldX;
	}

	private void setWorldY(int worldY) {
		this.worldY = worldY;
	}
}
