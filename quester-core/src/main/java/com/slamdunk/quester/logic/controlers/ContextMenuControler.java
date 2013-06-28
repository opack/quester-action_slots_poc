package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.QuesterActions.ATTACK;
import static com.slamdunk.quester.logic.ai.QuesterActions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.QuesterActions.MOVE;
import static com.slamdunk.quester.logic.ai.QuesterActions.PLACE_TORCH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.display.actors.ContextMenuActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.ContextMenuData;
import com.slamdunk.quester.model.map.MapLevels;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

public class ContextMenuControler extends WorldElementControler {
	// Vitesse d'ouverture du menu (en secondes)
	public static final float MENU_OPEN_SPEED = 0.1f;
	public static ContextMenuControler openedMenu;
	
	/**
	 * Contr�leurs sur lesquels utiliser l'action indiqu�e par le menuitem
	 */
	private Map<QuesterActions, WorldElementControler> menuItemsActionControlers;
	private List<ContextMenuActor> menuItemsActors;
	
	private MapLayer overlay;

	public ContextMenuControler(ContextMenuData data) {
		super(data);
		menuItemsActionControlers = new HashMap<QuesterActions, WorldElementControler>();
		createMenuItems();
	}
	
	@Override
	public void act(float delta) {
		for (WorldElementActor actor : menuItemsActors) {
			actor.act(delta);
		}
	}

	public void closeMenu() {
		openedMenu = null;
		for (WorldElementActor actor : menuItemsActors) {
    		overlay.removeActor(actor);
    	}
	}

	private void createMenuItems() {
		ContextMenuData contextMenuData = (ContextMenuData)data;
		final PlayerControler playerControler = GameControler.instance.getPlayer();
		final WorldElementActor playerActor = playerControler.getActor();

		// R�cup�re les diff�rents objets pr�sents sur cette case
		List<WorldElementControler> controlers = getSourceControler(contextMenuData.sourceX, contextMenuData.sourceY);
		
		// Initialise les flags qui permettront de d�cider des options de menu � afficher et activer
		boolean isTooFar = ActorMap.distance(playerActor.getWorldX(), playerActor.getWorldY(), contextMenuData.sourceX, contextMenuData.sourceY) > 1.0;
		boolean containsBlockingObject = false;
		boolean containsDoor = false;
		boolean containsDamageable = false;
		boolean containsWalkable = false;
		for (WorldElementControler controler : controlers) {
			// Si au moins un objet est bloquant, alors cette case en contient un
			containsBlockingObject &= controler.data.isSolid;
			
			switch (controler.data.element) {
				case DUNGEON_EXIT_DOOR:
				case COMMON_DOOR:
					containsDoor = true;
					menuItemsActionControlers.put(CROSS_PATH, controler);
					break;
				case GROUND:
					containsWalkable = true;
					menuItemsActionControlers.put(MOVE, controler);
					break;
				case RABITE:
					containsDamageable = true;
					menuItemsActionControlers.put(ATTACK, controler);
					break;
			}
		}
		
		// Cr�ation des items du menu, en prenant soin de d�sactiver les menus
		// indisponibles
		menuItemsActors = new ArrayList<ContextMenuActor>();
		// On met toujours l'�l�ment permettant de fermer le menu
		ContextMenuActor closeMenu = new ContextMenuActor(Assets.menu_close, QuesterActions.NONE);
		closeMenu.getImage().setScaling(Scaling.none);
		menuItemsActors.add(closeMenu);
		// On peut mettre une torche si l'emplacement peut, � la base, �tre parcouru... 
		if (containsWalkable
		// ... et ne contient pas d'objet bloquant ou que cet objet est cassable
		&& (!containsBlockingObject || containsDamageable)) {
			// TODO D�sactiver s'il y a quelque chose de d�molissable ou que c'est trop loin
			if (isTooFar || containsDamageable) {
				// TODO Mettre l'image gris�e ad�quate
				menuItemsActors.add(new ContextMenuActor(Assets.menu_torch_disabled, QuesterActions.NONE));
			} else {
				menuItemsActors.add(new ContextMenuActor(Assets.menu_torch, PLACE_TORCH));
			}
		}
		// On peut se d�placer si la zone est parcourable
		if (containsWalkable) {
			// D�sactiver si la zone n'est pas �clair�e ou est trop loin
			List<Point> lightPath = GameControler.instance.getScreen().getMap().getPathfinder().findPath(contextMenuData.sourceX, contextMenuData.sourceY, playerActor.getWorldX(), playerActor.getWorldY(), true);
//			if ((lightPath != null && lightPath.size() > playerControler.characterData.actionsLeft)) {
//				// TODO Mettre l'image gris�e ad�quate
//				menuItemsActors.add(new ContextMenuActor(Assets.menu_move_disabled, QuesterActions.NONE));
//			} else {
				menuItemsActors.add(new ContextMenuActor(Assets.menu_move, MOVE));
//			}
		}
		// On peut ouvrir une porte
		if (containsDoor) {
			if (isTooFar) {
				// TODO Mettre l'image gris�e ad�quate
				menuItemsActors.add(new ContextMenuActor(Assets.cross, QuesterActions.NONE));
			} else {
				menuItemsActors.add(new ContextMenuActor(Assets.commonDoor, CROSS_PATH));
			}
		}
		// On peut attaquer s'il y a un truc qu'on peut d�truire
		if (containsDamageable) {
			if (isTooFar || !playerControler.canAttack(menuItemsActionControlers.get(ATTACK))) {
				// TODO Mettre l'image gris�e ad�quate
				menuItemsActors.add(new ContextMenuActor(Assets.menu_attack_disabled, QuesterActions.NONE));
			} else {
				menuItemsActors.add(new ContextMenuActor(Assets.menu_attack, ATTACK));
			}
		}
	}

	private List<WorldElementControler> getSourceControler(int x, int y) {
		final List<WorldElementActor> actors = GameControler.instance.getScreen().getMap().getElementsAt(x, y);
		final List<WorldElementControler> controlers = new ArrayList<WorldElementControler>();
		for (WorldElementActor actor : actors) {
			controlers.add(actor.getControler());
		}
		return controlers;
	}

	private void layoutItem(WorldElementActor itemActor, float menuCenterX, float menuCenterY, float itemX, float itemY) {
		itemActor.setControler(this);
		itemActor.setX(menuCenterX);
		itemActor.setY(menuCenterY);
		overlay.addActor(itemActor);
		itemActor.addAction(Actions.moveTo(itemX, itemY, MENU_OPEN_SPEED));
	}
	
	public void layoutItems() {
		openedMenu = this;
		
		ActorMap map = GameControler.instance.getScreen().getMap();
		overlay = map.getLayer(MapLevels.OVERLAY);
		
		// Calcul du centre du menu contextuel
		ContextMenuData contextMenuData = (ContextMenuData)data;
		float centerX = contextMenuData.sourceX * map.getCellWidth();
		float centerY = contextMenuData.sourceY * map.getCellHeight();
		
		final int menuItemCount = menuItemsActors.size() - 1; // -1 car le premier �l�ment est celui permettant la fermeture du menu
		// Chaque acteur sera espac� �galement
		double marginAngle = 2 * Math.PI / menuItemCount;
		
		// D�termine la position de chaque acteur
		layoutItem(menuItemsActors.get(0), centerX, centerY, centerX, centerY);
		for (int index = 1; index < menuItemsActors.size(); index++) {
			// Ajoute ce contr�leur pour recevoir les clicks
			WorldElementActor curActor = menuItemsActors.get(index);
			curActor.setControler(this);

			double curAngle = marginAngle * index;
			float itemX = (float)(centerX + contextMenuData.radius * Math.cos(curAngle));
			float itemY = (float)(centerY + contextMenuData.radius * Math.sin(curAngle));
			
			layoutItem(curActor, centerX, centerY, itemX, itemY);
		}
	}
	
	public void onMenuItemClicked(QuesterActions action) {
		ContextMenuData contextMenuData = (ContextMenuData)data;
		
		// Effectue l'action demand�e
    	PlayerControler player = GameControler.instance.getPlayer();
    	WorldElementControler targetControler = menuItemsActionControlers.get(action);
    	switch (action) {
	    	case ATTACK:
	    		player.ai.clearActions();
				player.prepareAttack(targetControler);
				break;
	    	case CROSS_PATH:
	    		player.ai.clearActions();
	    		player.crossPath((PathToAreaControler)targetControler);
	    		break;
    		case MOVE:
    			player.ai.clearActions();
    			player.prepareMoveTo(contextMenuData.sourceX, contextMenuData.sourceY);
    			break;
    		default:
    			// Rien � faire : fermeture du menu
    	}
    	
    	// Ferme le menu
    	closeMenu();
	}
}
