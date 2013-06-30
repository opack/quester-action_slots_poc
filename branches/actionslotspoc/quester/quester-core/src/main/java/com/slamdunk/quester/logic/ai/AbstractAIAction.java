package com.slamdunk.quester.logic.ai;

public abstract class AbstractAIAction implements AIAction {
	protected AI ai;

	public AI getAI() {
		return ai;
	}

	public void setAI(AI ai) {
		this.ai = ai;
	}
}
