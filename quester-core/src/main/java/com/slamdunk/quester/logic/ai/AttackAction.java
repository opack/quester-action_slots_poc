package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.ai.QuesterActions.ATTACK;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.Damageable;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

/**
 * Fait attaquer la cible target par l'attacker, fourni lors de la création
 * de l'action. 
 */
public class AttackAction extends AbstractAIAction {
	private Damageable target;
	private boolean ignoreWeaponRange;
	
	public AttackAction(Damageable target) {
		this(target, false);
	}
	

	public AttackAction(Damageable target, boolean ignoreWeaponRange) {
		this.target = target;
		this.ignoreWeaponRange = ignoreWeaponRange;
	}
	
	public void act() {
		WorldElementControler targetControler = ((WorldElementControler)target);
		CharacterControler attacker = ai.controler;
		
		if (ignoreWeaponRange
		|| GameControler.instance.getScreen().getMap().isWithinRangeOf(attacker.getActor(), ((WorldElementControler)target).getActor(), attacker.getData().weaponRange)) {
			// Lance l'animation de l'attaque
			attacker.getActor().setCurrentAction(ATTACK, targetControler.getActor().getWorldX());
			
			// Fait un bruit d'épée
			//DBGAssets.playSound(attacker.getAttackSound());
			
			// Retire des PV à la cible
			target.receiveDamage(attacker.getData().attack);
		}
		
		// L'action est consommée : réalisation de la prochaine action
		ai.nextAction();
		ai.setNextAction(new WaitCompletionAction());
	}

	@Override
	public QuesterActions getAction() {
		return ATTACK;
	}
}
