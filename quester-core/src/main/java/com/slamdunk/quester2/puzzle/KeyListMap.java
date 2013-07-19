package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Table de hachage stockant une liste de valeurs pour chaque clé
 */
public class KeyListMap<KeyType, ValueType> extends HashMap<KeyType, List<ValueType>> {
	private static final long serialVersionUID = 2307777834556752856L;

	@Override
	public boolean containsValue(Object value) {
		for (List<ValueType> list : values()) {
			if (list.contains(value)) {
				return true;
			}
		}
		return false;
	}

	public void putValue(KeyType key, ValueType value) {
		List<ValueType> list = get(key);
		if (list == null) {
			list = new ArrayList<ValueType>();
			put(key, list);
		}
		list.add(value);
	}
}