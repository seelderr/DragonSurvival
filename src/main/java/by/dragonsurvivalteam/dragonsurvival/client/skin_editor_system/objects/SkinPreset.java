package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorRegistry;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.NBTInterface;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

public class SkinPreset implements NBTInterface{
	public HashMap<DragonLevel, SkinAgeGroup> skinAges = new HashMap<>();
	public double sizeMul = 1.0;

	public SkinPreset(){
		for(DragonLevel level : DragonLevel.values()){
			skinAges.computeIfAbsent(level, SkinAgeGroup::new);
		}
	}

	public void initDefaults(DragonStateHandler handler){
		initDefaults(handler.getType());
	}

	public void initDefaults(AbstractDragonType type){
		for(DragonLevel level : DragonLevel.values()){
			skinAges.put(level, new SkinAgeGroup(level, type));
		}
	}

	@Override
	public CompoundTag writeNBT(){
		CompoundTag nbt = new CompoundTag();
		nbt.putDouble("sizeMul", sizeMul);

		for(DragonLevel level : DragonLevel.values()){
			nbt.put(level.name, skinAges.getOrDefault(level, new SkinAgeGroup(level)).writeNBT());
		}

		return nbt;
	}

	@Override
	public void readNBT(CompoundTag base){
		sizeMul = base.getDouble("sizeMul");

		for(DragonLevel level : DragonLevel.values()){
			SkinAgeGroup ageGroup = new SkinAgeGroup(level);
			CompoundTag nbt = base.getCompound(level.name);
			ageGroup.readNBT(nbt);
			skinAges.put(level, ageGroup);
		}
	}

	public static class SkinAgeGroup implements NBTInterface{
		public DragonLevel level;
		public HashMap<EnumSkinLayer, LayerSettings> layerSettings = new HashMap<>();

		public boolean wings = true;
		public boolean defaultSkin = false;

		public SkinAgeGroup(DragonLevel level, AbstractDragonType type){
			this(level);
			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				layerSettings.put(layer, new LayerSettings(DragonEditorRegistry.getDefaultPart(type, level, layer)));
			}
		}

		public SkinAgeGroup(DragonLevel level){
			this.level = level;

			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				layerSettings.computeIfAbsent(layer, (s) -> new LayerSettings());
			}
		}

		@Override
		public CompoundTag writeNBT(){
			CompoundTag nbt = new CompoundTag();

			nbt.putBoolean("wings", wings);
			nbt.putBoolean("defaultSkin", defaultSkin);

			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				nbt.put(layer.name(), layerSettings.getOrDefault(layer, new LayerSettings()).writeNBT());
			}

			return nbt;
		}

		@Override
		public void readNBT(CompoundTag base){
			wings = base.getBoolean("wings");
			defaultSkin = base.getBoolean("defaultSkin");

			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				LayerSettings ageGroup = new LayerSettings();
				CompoundTag nbt = base.getCompound(layer.name());
				ageGroup.readNBT(nbt);
				layerSettings.put(layer, ageGroup);
			}
		}
	}
}