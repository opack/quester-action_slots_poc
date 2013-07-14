package com.slamdunk.quester.model.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe permet de gérer un tableau à double entrée dont chaque entrée
 * est de type EntryType, et dont la valeur est de type ValueType.
 */
public class DoubleEntryArray<EntryType, ValueType> {
	private Map<EntryType, Map<EntryType, ValueType>> data;
	
	public DoubleEntryArray() {
		data = new HashMap<EntryType, Map<EntryType, ValueType>>();
	}
	
	public void put(EntryType entry1, EntryType entry2, ValueType value) {
		Map<EntryType, ValueType> values = data.get(entry1);
		if (values == null) {
			values = new HashMap<EntryType, ValueType>();
			data.put(entry1, values);
		}
		values.put(entry2, value);
	}
	
	public ValueType get(EntryType entry1, EntryType entry2) {
		Map<EntryType, ValueType> values = data.get(entry1);
		if (values == null) {
			return null;
		}
		return values.get(entry2);
	}
}
