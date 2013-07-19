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
	
	public PuzzleMatchData() {
		attributes = new HashSet<AttributeData>();
		typesMeter = new HashMap<AttributeTypes, Integer>();
	}
	
	public boolean setSource(AttributeData data) {
		if (add(data, false)) {
			source = data;
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
		attributes.add(data);
		
		// Mise à jour de l'orientation
		if (updateOrientation) {
			AlignmentOrientation thisOrientation = AlignmentOrientation.WHOLE;
			Point thisPosition = data.position;
			if (thisPosition.getX() == source.position.getX()) {
				thisOrientation = AlignmentOrientation.VERTICAL;
			} else if (thisPosition.getY() == source.position.getY()) {
				thisOrientation = AlignmentOrientation.HORIZONTAL;
			}
			if ( (orientation == AlignmentOrientation.VERTICAL && thisOrientation == AlignmentOrientation.HORIZONTAL)
			|| (orientation == AlignmentOrientation.HORIZONTAL && thisOrientation == AlignmentOrientation.VERTICAL) ) {
				// Si on était déjà vertical ou horizontal et que là on est dans l'autre cas
				// alors finalement on est en corner
				orientation = AlignmentOrientation.CROSS;
			} else {
				// Dans les autres cas, l'orientation globale est celle entre cet élément et la source
				orientation = thisOrientation;
			}
		}
		
		// Mise à jour du compteur de types
		AttributeTypes type = data.attribute.getType();
		typesMeter.put(type, getMatchCount(type) + 1);
		return true;
	}
	
	public PuzzleMatchEffect buildMatchEffect() {
		switch (orientation) {
		case HORIZONTAL:
		case VERTICAL:
			// S'il n'y a que des supers, on a un match entre 2 supers
			// Sinon on fait a un simple match en ligne (contenant éventuellement des supers)
			if (getMatchCount(AttributeTypes.SUPER) > 0 && getMatchCount(AttributeTypes.BASE) == 0) {
				System.out.println("DBG PuzzleMatchEffectBuilder.build() SuperMatchEffect !");
//				return new SuperMatchEffect();
			} else {
				return new LineMatchEffect();
			}
			break;
		case CROSS:
			System.out.println("DBG PuzzleMatchEffectBuilder.build() CrossMatchEffect !");
//			return new CrossMatchEffect();
			return null;
		case WHOLE:
			PuzzleAttributes baseAttribute = source.attribute.getBaseAttribute();
			if (baseAttribute == null) {
				baseAttribute = source.attribute;
			}
			System.out.println("DBG PuzzleMatchEffectBuilder.build() WholeGridMatchEffect ! " + baseAttribute);
//			return new WholeGridMatchEffect(baseAttribute);
			return null;
		case SELF:
			return null;
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