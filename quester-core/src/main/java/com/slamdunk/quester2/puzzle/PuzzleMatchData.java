package com.slamdunk.quester2.puzzle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AttributeData;

public class PuzzleMatchData {
	private AttributeData source;
	private AlignmentOrientation orientation;
	private Set<AttributeData> attributes;
	private Map<AttributeTypes, Integer> typesMeter;
	private int countHorizontals;
	private int countVerticals;
	
	public PuzzleMatchData() {
		attributes = new HashSet<AttributeData>();
		typesMeter = new HashMap<AttributeTypes, Integer>();
	}
	
	public boolean setSource(AttributeData data) {
		if (add(data, false)) {
			source = data;
			// Dès qu'il y a un attribut, il y a forcément 1 attribut en vertical et 1 en horizontal
			countHorizontals = 1;
			countVerticals = 1;
			return true;
		}
		return false;
	}

	public boolean add(AttributeData data) {
		if (source == null) {
			System.err.println("No source attribute defined. Call setSource() before add().");
			return false;
		}
		return add(data, true);
	}
	
	private boolean add(AttributeData data, boolean updateOrientation) {
		if (data == null || data.position == null || data.attribute == null) {
			return false;
		}
		
		// Ajout de l'attribut
		if (attributes.add(data)) {
			// Mise à jour de l'orientation
			if (updateOrientation) {
				AlignmentOrientation thisOrientation = AlignmentOrientation.WHOLE;
				Point thisPosition = data.position;
				if (thisPosition.getX() == source.position.getX()) {
					thisOrientation = AlignmentOrientation.VERTICAL;
					countVerticals++;
				} else if (thisPosition.getY() == source.position.getY()) {
					thisOrientation = AlignmentOrientation.HORIZONTAL;
					countHorizontals++;
				}
				if (orientation == null) {
					// L'orientation globale est celle entre cet élément et la source
					orientation = thisOrientation;
				} else if ((orientation == AlignmentOrientation.VERTICAL && thisOrientation == AlignmentOrientation.HORIZONTAL)
						|| (orientation == AlignmentOrientation.HORIZONTAL && thisOrientation == AlignmentOrientation.VERTICAL)) {
					// Si on était déjà vertical ou horizontal et que là on est dans l'autre cas
					// alors finalement on est en corner
					orientation = AlignmentOrientation.CROSS;
				}
			}
			
			// Mise à jour du compteur de types
			AttributeTypes type = data.attribute.getType();
			typesMeter.put(type, getMatchCount(type) + 1);
			
			return true;
		}
		return false;
	}
	
	/**
	 * Crée un PuzzleMatchEffect qui sera chargé de faire ce qu'il faut en
	 * fonction des attributs matchables ajoutés via add().
	 * @return
	 */
	public PuzzleMatchEffect buildMatchEffect() {
		if (orientation != null) {
			int count = count();
			switch (orientation) {
			case HORIZONTAL:
			case VERTICAL:
				// 2 attributs alignés
				if (count == 2) {
					// 2 Super
					if (getMatchCount(AttributeTypes.SUPER) == 2){
						return new SupersMatchEffect();
					}
					// 1 Hyper + 1 autre
					else if (getMatchCount(AttributeTypes.HYPER) > 0){
						return new HyperMatchEffect();
					}
				}
				// Au moins 3 attributs en ligne
				else if (count >= 3){
					return new LineMatchEffect();
				}
				break;
			case CROSS:
				if (countHorizontals == 3 && countVerticals == 3) {
					return new CrossMatchEffect();
				}
				break;
			default:
				return null;
			}
		}
		return null;
	}

	private int getMatchCount(AttributeTypes type) {
		Integer count = typesMeter.get(type);
		if (count == null) {
			return 0;
		}
		return count;
	}

	public void add(List<AttributeData> attributes) {
		for (AttributeData data : attributes) {
			add(data);
		}
	}

	public Set<AttributeData> getAttributes() {
		return attributes;
	}

	public int count() {
		return attributes.size();
	}

	public AttributeData getSource() {
		return source;
	}

	public AlignmentOrientation getOrientation() {
		return orientation;
	}
}