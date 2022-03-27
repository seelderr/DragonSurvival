package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;

public class SkinCap extends SubCap{
	public static final String defaultSkinValue = "None";
	public boolean renderNewborn;
	public boolean renderYoung;
	public boolean renderAdult;
	public SkinPreset skinPreset = new SkinPreset();
	public HashSet<EnumSkinLayer> updateLayers = new HashSet<>();
	public boolean blankSkin = false;

	public SkinCap(DragonStateHandler handler){
		super(handler);
		for(DragonType value : DragonType.values()){
			skinPreset.initDefaults(value);
		}
	}

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();

		tag.putBoolean("renderNewborn", renderNewborn);
		tag.putBoolean("renderYoung", renderYoung);
		tag.putBoolean("renderAdult", renderAdult);

		tag.put("skinPreset", skinPreset.writeNBT());

		return tag;
	}

	@Override
	public void readNBT(CompoundTag tag){
		renderNewborn = tag.getBoolean("renderNewborn");
		renderYoung = tag.getBoolean("renderYoung");
		renderAdult = tag.getBoolean("renderAdult");

		CompoundTag skinNbt = tag.getCompound("skinPreset");
		skinPreset = new SkinPreset();
		skinPreset.readNBT(skinNbt);
	}
}