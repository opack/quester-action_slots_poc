package com.slamdunk.quester.model.map.decorators;

import com.slamdunk.quester.model.map.MapArea;

/**
 * D�core une zone en y ajoutant des murs, des arbres ou n'importe quoi d'autres, d'apr�s
 * un algorithme bien particulier.
 */
public interface AreaDecorator {
	/**
	 * D�core la zone d'apr�s l'algorithme de ce d�corateur
	 */
	void decorate(MapArea area);
}
