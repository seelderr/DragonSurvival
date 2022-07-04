package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.MagicAbility;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.resources.ResourceLocation;

@RegisterDragonAbility
public class ForestMagicAbility extends MagicAbility{
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "passives"}, key = "forestMagic", comment = "Whether the forest magic ability should be enabled" )
	public static Boolean forestMagic = true;

	@Override
	public String getName(){
		return "forest_magic";
	}

	@Override
	public int getSortOrder(){
		return 1;
	}

	@Override
	public DragonType getDragonType(){
		return DragonType.FOREST;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_1.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_2.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_3.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_4.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_5.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_6.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_7.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_8.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_9.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_10.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_11.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_12.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_13.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_14.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_15.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_16.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_17.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_18.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_19.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_magic_20.png")
		};
	}

	@Override
	public int getMaxLevel(){
		return 10;
	}

	@Override
	public int getMinLevel(){
		return 0;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !forestMagic;
	}
}