package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.Quester;
import com.slamdunk.quester.logic.controlers.CastleControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;
import com.slamdunk.quester.model.data.CastleData;

/**
 * Fait entrer le joueur dans le ch�teau indiqu�.
 */
public class EnterCastleAction implements AIAction {
	private PlayerControler player;
	private CastleControler castle;
	
	public EnterCastleAction(PlayerControler player, CastleControler castle) {
		this.player = player;
		this.castle = castle;
	}
	
	@Override
	public void act() {
		CastleData castleData = castle.getData();
		Quester.getInstance().enterDungeon(
			castleData.dungeonWidth, castleData.dungeonHeight,
			castleData.roomWidth, castleData.roomHeight,
			castleData.difficulty);
		
		// L'action est consomm�e : r�alisation de la prochaine action
		player.getAI().nextAction();
	}
	

	@Override
	public QuesterActions getAction() {
		return QuesterActions.ENTER_CASTLE;
	}

}
