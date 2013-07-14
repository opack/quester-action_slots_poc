package com.slamdunk.quester.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slamdunk.quester.display.hud.actionslots.SlotData;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.map.MapElements;
import com.slamdunk.quester.utils.Assets;
import com.slamdunk.quester.utils.Config;

public class ActionItemHelper {
	private static final float APPEAR_RATE_TOTAL = Config.asFloat("action.appearRate.total", 6f);
	private static final List<QuesterActions> NEXT_ACTIONS;
	public static final Map<QuesterActions, SlotData> SLOT_DATAS;
	private static final DoubleEntryArray<MapElements, Boolean> MATCHABLES;
	private static final Map<String, AlignmentEffect> ALIGNMENT_EFFECTS;
	
	static {
		// Création des données des slots pour chaque action possible
		SLOT_DATAS = new HashMap<QuesterActions, SlotData>();
		SLOT_DATAS.put(
			QuesterActions.ATTACK,
			new SlotData(
				QuesterActions.ATTACK,
				WorldElementData.SWORD_DATA,
				Config.asFloat("action.appearRate.attack", 1),
				Assets.action_attack));
		SLOT_DATAS.put(
			QuesterActions.MOVE,
			new SlotData(
				QuesterActions.MOVE, 
				WorldElementData.MOVE_DATA,
				Config.asFloat("action.appearRate.move", 0),
				Assets.menu_move));
		SLOT_DATAS.put(
			QuesterActions.PROTECT,
			new SlotData(
				QuesterActions.PROTECT, 
				WorldElementData.SHIELD_DATA,
				Config.asFloat("action.appearRate.shield", 1),
				Assets.action_shield));
		SLOT_DATAS.put(
			QuesterActions.CHEST,
			new SlotData(
				QuesterActions.CHEST, 
				WorldElementData.CHEST_DATA,
				Config.asFloat("action.appearRate.chest", 1),
				Assets.action_chest));
		SLOT_DATAS.put(
			QuesterActions.TECHSPE,
			new SlotData(
				QuesterActions.TECHSPE, 
				WorldElementData.STAR_DATA,
				Config.asFloat("action.appearRate.techspe", 1),
				Assets.action_techspe));
		SLOT_DATAS.put(
			QuesterActions.HEAL,
			new SlotData(
				QuesterActions.HEAL, 
				WorldElementData.HEAL_DATA,
				Config.asFloat("action.appearRate.heal", 1),
				Assets.action_heal));

		// Création de la matrice d'items matchables
		MATCHABLES = new DoubleEntryArray<MapElements, Boolean>();
		addMatchables(MapElements.SWORD, MapElements.SWORD);
		addMatchables(MapElements.SWORD, MapElements.RABITE);
		addMatchables(MapElements.SWORD, MapElements.ENNEMY);
		addMatchables(MapElements.MOVE, MapElements.MOVE);
		addMatchables(MapElements.SHIELD, MapElements.SHIELD);
		addMatchables(MapElements.CHEST, MapElements.CHEST);
		addMatchables(MapElements.STAR, MapElements.STAR);
		addMatchables(MapElements.HEAL, MapElements.HEAL);
		
		// Création de la table des recettes d'alignement
		ALIGNMENT_EFFECTS = new HashMap<String, AlignmentEffect>();
		ALIGNMENT_EFFECTS.put(
			AlignmentEffect.buildRecipe(MapElements.SWORD, MapElements.SWORD, MapElements.SWORD),
			new AttackAlignmentEffect());
		ALIGNMENT_EFFECTS.put(
			AlignmentEffect.buildRecipe(new MapElements[]{MapElements.SHIELD, MapElements.SHIELD, MapElements.SHIELD}),
			new ShieldAlignmentEffect());
		
		// Création de la liste des prochaines actions
		NEXT_ACTIONS = new ArrayList<QuesterActions>();
		chooseNextActions();
	}
	
	/**
	 * Remplit la liste des prochaines actions avec un paquet d'actions
	 * choisies en fonction des taux d'apparition de chaque action,
	 * et mélangé aléatoirement.
	 */
	private static void chooseNextActions() {
		final int nbActionsToCreate = Config.asInt("action.appearRate.foreseeSize", (int)(APPEAR_RATE_TOTAL + 1));
		NEXT_ACTIONS.clear();
		for (SlotData data : SLOT_DATAS.values()) {
			final int nbOccurrences = (int)(data.rate * nbActionsToCreate / APPEAR_RATE_TOTAL);
			for (int count = 0; count < nbOccurrences; count++) {
				NEXT_ACTIONS.add(data.action);
			}
		}
		Collections.shuffle(NEXT_ACTIONS);
	}
	
	private static void addMatchables(MapElements element1, MapElements element2) {
		MATCHABLES.put(element1, element2, Boolean.TRUE);
		MATCHABLES.put(element2, element1, Boolean.TRUE);
	}

	/**
	 * Remplit ce slot avec la prochaine action prévue
	 */
	public static QuesterActions getNextAction() {
		if (NEXT_ACTIONS.isEmpty()) {
			chooseNextActions();
		}
		return NEXT_ACTIONS.remove(0);
	}

	public static boolean areMatchable(MapElements element1, MapElements element2) {
		Boolean matchable = MATCHABLES.get(element1, element2);
		return matchable != null && matchable.booleanValue();
	}

	public static AlignmentEffect getAlignmentEffect(List<MapElements> alignedElements) {
		String recipe = AlignmentEffect.buildRecipe(alignedElements);
		return ALIGNMENT_EFFECTS.get(recipe);
	}
}
