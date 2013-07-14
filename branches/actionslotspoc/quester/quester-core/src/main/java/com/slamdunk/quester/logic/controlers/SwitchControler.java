package com.slamdunk.quester.logic.controlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.map.LayerCell;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.ActionItemHelper;
import com.slamdunk.quester.model.data.AlignmentEffect;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.MapElements;
import com.slamdunk.quester.model.map.MapLevels;
import com.slamdunk.quester.model.points.Point;

public class SwitchControler extends WorldElementControler {
	private static SwitchControler firstSwitchElement;
	private static WorldElementActor firstActor;
	private static WorldElementActor secondActor;
	
	private static Set<WorldElementActor> candidatesActors = new HashSet<WorldElementActor>();
	private static Set<WorldElementActor> switchingActors = new HashSet<WorldElementActor>();
	private static Set<WorldElementActor> finishedActors = new HashSet<WorldElementActor>();

	private boolean checkDone;
	
	public SwitchControler(WorldElementData data, WorldElementActor actor) {
		super(data, actor);
	}

	public void setSwitchElement() {
		if (firstSwitchElement == null) {
			firstSwitchElement = this;
		} else {
			performSwitch();
			firstSwitchElement = null;
		}
	}

	public void performSwitch() {
		firstActor = firstSwitchElement.actor;
		secondActor = actor;
		
		// Demande l'animation d'inversion des éléments
		switchActors(firstActor, secondActor, this);
	}
		
	public void checkAlignments() {
		// Vérification de la fin du mouvement des 2 acteurs
		System.out.println("SwitchControler.checkAlignments() checkDone=" + checkDone);
		if (firstActor.getCurrentAction() != QuesterActions.NONE
		|| secondActor.getCurrentAction() != QuesterActions.NONE
		|| checkDone) {
			System.out.println("SwitchControler.checkAlignments() PAS ENCORE...");
			return;
		}
		checkDone = true;
		
		final MapElements firstElement = firstActor.getControler().data.element;
		final MapElements secondElement = secondActor.getControler().data.element;
		System.out.println("SwitchControler.checkAlignments() CHECKING " + firstElement + " / " + secondElement);
		
		// Détermine si l'un des deux éléments crée un alignement possible
		boolean firstCreateAlignment = resolveAlignment(firstActor.getWorldX(), firstActor.getWorldY(), firstElement);
		boolean secondCreateAlignment = resolveAlignment(secondActor.getWorldX(), secondActor.getWorldY(), secondElement);
		
		// Si non, on replace les items à leur position initiale
		if (!firstCreateAlignment && !secondCreateAlignment) {
			switchActors(firstActor, secondActor, null);
		}
	}

	private boolean resolveAlignment(int col, int row, MapElements element) {
		ActorMap map = GameControler.instance.getScreen().getMap();
		int width = map.getMapWidth();
		int height = map.getMapWidth();
		List<Point> hAlignedPos = new ArrayList<Point>();
		List<MapElements> hAlignedElements = new ArrayList<MapElements>();
		List<Point> vAlignedPos = new ArrayList<Point>();
		List<MapElements> vAlignedElements = new ArrayList<MapElements>();
		
		// Vérifie si l'item participe à un alignement horizontal
		for (int curCol = col; curCol > -1; curCol--) {
			if (!match(curCol, row, element, map, hAlignedPos, hAlignedElements)) {
				break;
			}
		}
		// On commence à col+1 pour ne pas compter 2 fois la cellule à col;row
		for (int curCol = col + 1; curCol < width; curCol++) {
			if (!match(curCol, row, element, map, hAlignedPos, hAlignedElements)) {
				break;
			}
		}
		
		// Vérifie si l'item participe à un alignement vertical
		for (int curRow = row; curRow > -1; curRow--) {
			if (!match(col, curRow, element, map, vAlignedPos, vAlignedElements)) {
				break;
			}
		}
		// On commence à row+1 pour ne pas compter 2 fois la cellule à col;row
		for (int curRow = row + 1; curRow < height; curRow++) {
			if (!match(col, curRow, element, map, vAlignedPos, vAlignedElements)) {
				break;
			}
		}
		
		// On résoud l'alignement en privilégiant le plus long.
		if (hAlignedPos.isEmpty() && vAlignedPos.isEmpty()) {
			return false;
		}
		int hCount = hAlignedPos.size();
		int vCount = vAlignedPos.size();
		if (hCount == vCount) {
			// Formation en coin
			// ...
			return false;
		} else if (hCount > vCount) {
			// Simple ligne horizontale
			return resolveLineAlignment(hAlignedPos, hAlignedElements);
		} else {
			// Simple ligne verticale
			return resolveLineAlignment(vAlignedPos, vAlignedElements);
		}
	}

	private boolean match(int curCol, int row, MapElements element, ActorMap map, List<Point> alignedPosList, List<MapElements> alignedElementsList) {
		WorldElementActor neighbor = map.getTopElementAt(curCol, row, MapLevels.OBJECTS);
		if (neighbor != null) {
			MapElements neighborElement = neighbor.getControler().getData().element;
			if (ActionItemHelper.areMatchable(neighborElement, element)) {
				alignedPosList.add(new Point(curCol, row));
				alignedElementsList.add(neighborElement);
				return true;
			}
		}
		return false;
	}

	private boolean resolveLineAlignment(List<Point> alignedPos, List<MapElements> alignedElements) {
		// Déclenchement de l'effet adéquat
		AlignmentEffect effect = ActionItemHelper.getAlignmentEffect(alignedElements);
		if (effect == null) {
			return false;
		}
		effect.perform();
		
		// Suppression des éléments alignés
		// ...
		
		// Chute des éléments supérieurs
		// ...
		
		// Ajout de nouveaux éléments
		// ...
		
		return true;
	}

	private void switchActors(WorldElementActor firstActor, WorldElementActor secondActor, SwitchControler controlerToNotify) {
		checkDone = false;
		int firstX = firstActor.getWorldX();
		int firstY = firstActor.getWorldY();
		MapLayer layer = GameControler.instance.getScreen().getMap().getLayer(MapLevels.OBJECTS);
		LayerCell firstCell = layer.getCell(firstX,  firstY);
		LayerCell secondCell = layer.getCell(secondActor.getWorldX(), secondActor.getWorldY());
		
		// Déplace physiquement les acteurs
		firstActor.moveTo(secondActor.getWorldX(), secondActor.getWorldY(), 0.2f, controlerToNotify);
		secondActor.moveTo(firstX, firstY, 0.2f, controlerToNotify);
		candidatesActors.add(firstActor);
		candidatesActors.add(secondActor);
		
		// Déplace logiquement les acteurs
		layer.switchCells(firstCell, secondCell, false);
	}

	public static Set<WorldElementActor> getSwitchingActors() {
		switchingActors.clear();
		finishedActors.clear();
		for (WorldElementActor actor : candidatesActors) {
			if (actor.getCurrentAction() == QuesterActions.MOVE) {
				switchingActors.add(actor);
			} else {
				finishedActors.add(actor);
			}
		}
		candidatesActors.removeAll(finishedActors);
		return switchingActors;
	}

}
