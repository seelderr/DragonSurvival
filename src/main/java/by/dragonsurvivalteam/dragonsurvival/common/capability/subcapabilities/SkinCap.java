package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class SkinCap extends SubCap {
	public static final String defaultSkinValue = "None";
	public boolean renderNewborn;
	public boolean renderYoung;
	public boolean renderAdult;
	public SkinPreset skinPreset = new SkinPreset();
	public boolean blankSkin = false;

	public Map<DragonLevel, Boolean> recompileSkin = new HashMap<>();
	public Map<DragonLevel, Boolean> isCompiled = new HashMap<>();

	public void compileSkin() {
		for (DragonLevel level : DragonLevel.values()) {
			recompileSkin.put(level, true);
		}
	}

	public SkinCap(DragonStateHandler handler) {
		super(handler);
		for (String value : DragonTypes.getTypes()) {
			skinPreset.initDefaults(DragonTypes.getStatic(value));
		}

		for (DragonLevel level : DragonLevel.values()) {
			recompileSkin.put(level, true);
			isCompiled.put(level, false);
		}
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();

		tag.putBoolean("renderNewborn", renderNewborn);
		tag.putBoolean("renderYoung", renderYoung);
		tag.putBoolean("renderAdult", renderAdult);

		tag.put("skinPreset", skinPreset.serializeNBT(provider));

		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		renderNewborn = tag.getBoolean("renderNewborn");
		renderYoung = tag.getBoolean("renderYoung");
		renderAdult = tag.getBoolean("renderAdult");

		CompoundTag skinNbt = tag.getCompound("skinPreset");
		skinPreset = new SkinPreset();
		skinPreset.deserializeNBT(provider, skinNbt);
	}
}