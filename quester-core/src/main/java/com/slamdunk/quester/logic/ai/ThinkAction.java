package com.slamdunk.quester.logic.ai;

public class ThinkAction extends AbstractAIAction {
	@Override
	public void act() {
		ai.think();
		ai.nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.THINK;
	}

}
