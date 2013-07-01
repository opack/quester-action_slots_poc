package com.slamdunk.quester.model.data;

import com.slamdunk.quester.model.map.MapElements;

public class PathMarkerData extends WorldElementData {
	public boolean isReachable;
	public boolean isLastMarker;
	
	public PathMarkerData(boolean isReachable, boolean isLastMarker) {
		this();
		this.isReachable = isReachable;
		this.isLastMarker = isLastMarker;
	}

	public PathMarkerData() {
		super(MapElements.PATH_MARKER);
	}
}
