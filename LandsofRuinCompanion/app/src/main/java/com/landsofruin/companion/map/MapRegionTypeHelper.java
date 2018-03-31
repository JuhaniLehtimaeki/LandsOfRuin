package com.landsofruin.companion.map;

import com.landsofruin.companion.state.MapState;

public abstract class MapRegionTypeHelper {

	private static final int STREET_BACKGROUND = 0xFF111111;
	private static final int STREET_OUTLINE = 0x6693918f;

	private static final int BUILDING_BLOCK_BACKGROUND = 0xFF393939;
	private static final int BUILDING_BLOCK_OUTLINE = 0x6693918f;

	private static final int DRAG_OUT_SECTION_BACKGROUND = 0x00030303;
	private static final int DRAG_OUT_SECTION_OUTLINE = 0x00000000;
	
	private static final int STREET_BASE_DANGER_LEVEL = 2;
	private static final int BUILDING_BLOCK_BASE_DANGER_LEVEL = 7;
	

	public static int getBackgroundColor(int reqionType) {
        return 0x0000000;

//		switch (reqionType) {
//		case MAP_REGION_TYPE_STREET:
//			return STREET_BACKGROUND;
//
//		case MAP_REGION_TYPE_BUILDING_BLOCK:
//			return BUILDING_BLOCK_BACKGROUND;
//
//		case MAP_REGION_TYPE_DRAG_OUT:
//			return DRAG_OUT_SECTION_BACKGROUND;
//		default:
//			break;
//		}
//		return BUILDING_BLOCK_BACKGROUND;
	}

	public static int getOutlineColor(int reqionType) {
		switch (reqionType) {
		case MapState.MAP_REGION_TYPE_STREET:
			return STREET_OUTLINE;

		case MapState.MAP_REGION_TYPE_BUILDING_BLOCK:
			return BUILDING_BLOCK_OUTLINE;

		case MapState.MAP_REGION_TYPE_DRAG_OUT:
			return DRAG_OUT_SECTION_OUTLINE;
		default:
			break;
		}
		return BUILDING_BLOCK_OUTLINE;
	}

	
	
	public static int getBaseDangerLevel(int reqionType) {
		switch (reqionType) {
		case MapState.MAP_REGION_TYPE_STREET:
			return STREET_BASE_DANGER_LEVEL;

		case MapState.MAP_REGION_TYPE_BUILDING_BLOCK:
			return BUILDING_BLOCK_BASE_DANGER_LEVEL;

		case MapState.MAP_REGION_TYPE_DRAG_OUT:
			return 0;
		default:
			break;
		}
		return 0;
	}

	
	
	
}
