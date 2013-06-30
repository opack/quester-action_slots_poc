package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.ai.QuesterActions.ATTACK;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.Damageable;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

/**
 * Fait attaquer la cible target par l'attacker, fournis lors de la cr�ation
 * de l'action. 
 */
public class AttackAction extends AbstractAIAction {
	private Damageable target;
	
	public AttackAction(Damageable target) {
		this.target = target;
	}
	
	public void act() {
		WorldElementControler targetControler = ((WorldElementControler)target);
		
//DBG		// Avant d'attaquer, on s'assure que la cible vis�e est toujours � port�e.
//		if (!GameControler.instance.getScreen().getMap().isWithinRangeOf(attacker.getActor(), targetControler.getActor(), attacker.getData().weaponRange)) {
//			attacker.prepareThink();
//			attacker.getAI().setNextAction(new EndTurnAction(attacker));
//			return;
//		}
		
		CharacterControler attacker = ai.controler;
		
		// Lance l'animation de l'attaque
		attacker.getActor().setCurrentAction(ATTACK, targetControler.getActor().getWorldX());
		
		// Fait un bruit d'�p�e
		//DBGAssets.playSound(attacker.getAttackSound());
		
		// Retire des PV � la cible
		target.receiveDamage(attacker.getData().attack);
		
		// L'action est consomm�e : r�alisation de la prochaine action
		ai.nextAction();
//DBG		attacker.getAI().setNextActions(new WaitCompletionAction(attacker), new EndTurnAction(attacker));
		ai.setNextAction(new WaitCompletionAction());
	}

	@Override
	public QuesterActions getAction() {
		return ATTACK;
	}
}
