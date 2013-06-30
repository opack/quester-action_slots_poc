package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.PathToAreaControler;

/**
 * Traverse le chemin indiqu�.
 */
public class CrossPathAction extends AbstractAIAction {
	private PathToAreaControler path;
	
	public CrossPathAction(PathToAreaControler path) {
		this.path = path;
	}
	
	@Override
	public void act() {
		// Ouverture de la porte
		path.open();

		// L'action est consomm�e : r�alisation de la prochaine action
		ai.nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.CROSS_PATH;
	}

}
