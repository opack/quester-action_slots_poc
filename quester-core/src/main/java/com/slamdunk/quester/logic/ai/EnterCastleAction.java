package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.Quester;
import com.slamdunk.quester.logic.controlers.CastleControler;
import com.slamdunk.quester.model.data.CastleData;

/**
 * Fait entrer le joueur dans le château indiqué.
 */
public class EnterCastleAction extends AbstractAIAction {
	private CastleControler castle;
	
	public EnterCastleAction(CastleControler castle) {
		this.castle = castle;
	}
	
	@Override
	public void act() {
		CastleData castleData = castle.getData();
		Quester.getInstance().enterDungeon(
			castleData.dungeonWidth, castleData.dungeonHeight,
			castleData.roomMinSize, castleData.roomMaxSize,
			castleData.difficulty);
		
		// L'action est consommée : réalisation de la prochaine action
		ai.nextAction();
	}
	

	@Override
	public QuesterActions getAction() {
		return QuesterActions.ENTER_CASTLE;
	}

}
