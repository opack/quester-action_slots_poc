package com.slamdunk.quester.model.map.decorators;

import com.slamdunk.quester.model.map.MapArea;

/**
 * Décore une zone en y ajoutant des murs, des arbres ou n'importe quoi d'autres, d'après
 * un algorithme bien particulier.
 */
public interface AreaDecorator {
	/**
	 * Décore la zone d'après l'algorithme de ce décorateur
	 */
	void decorate(MapArea area);
}
