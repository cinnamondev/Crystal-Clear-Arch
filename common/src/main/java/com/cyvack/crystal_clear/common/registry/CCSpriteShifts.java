package com.cyvack.crystal_clear.common.registry;

import com.cyvack.crystal_clear.common.CrystalClear;
import com.cyvack.crystal_clear.common.util.CasingTypes;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.createmod.catnip.data.Iterate;

import java.util.IdentityHashMap;
import java.util.Map;


public class CCSpriteShifts {

	public static final Map<String, MultiShiftHolder> GLASS_CASING_SHIFTS = new IdentityHashMap<>();

	public static void populateMaps() {
		for (String name : CasingTypes.NORMAL_CASINGS.names) {
			GLASS_CASING_SHIFTS.put(name, MultiShiftHolder.createHolder(AllCTTypes.OMNIDIRECTIONAL, name));
		}
	}

	public static CTSpriteShiftEntry omni(String name) {
		return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
	}

	public static CTSpriteShiftEntry horizontal(String name) {
		return getCT(AllCTTypes.HORIZONTAL, name);
	}

	public static CTSpriteShiftEntry vertical(String name) {
		return getCT(AllCTTypes.VERTICAL, name);
	}

	private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
		return CTSpriteShifter.getCT(type, CrystalClear.path("block/" + blockTextureName), CrystalClear.path("block/" + connectedTextureName + "_connected"));
	}

	private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
		return getCT(type, blockTextureName, blockTextureName);
	}

	public record MultiShiftHolder(CTSpriteShiftEntry regular, CTSpriteShiftEntry clear) {
		public CTSpriteShiftEntry get(boolean clear) {
			return clear ? clear() : regular();
		}

		private static MultiShiftHolder createHolder(CTType type, String casingName) {
			CTSpriteShiftEntry regularShift = null;
			CTSpriteShiftEntry clearShift = null;

			for (boolean clear : Iterate.falseAndTrue) {
				String loc = casingName.concat(clear ? "_clear" : "").concat("_glass_casing");

				if (clear) {
					clearShift = getCT(type, loc);
				} else {
					regularShift = getCT(type, loc);
				}
			}

			return new MultiShiftHolder(regularShift, clearShift);
		}
	}
}

