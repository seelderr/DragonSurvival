package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.MagicAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.util.DragonType;
import net.minecraft.resources.ResourceLocation;

@RegisterDragonAbility
public class CaveMagicAbility extends MagicAbility{
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "passives"}, key = "caveMagic", comment = "Whether the cave magic ability should be enabled" )
	public static Boolean caveMagic = true;

	@Override
	public int getSortOrder(){
		return 1;
	}

	@Override
	public String getName(){
		return "cave_magic";
	}

	@Override
	public DragonType getDragonType(){
		return DragonType.CAVE;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_1.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_2.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_3.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_4.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_5.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_6.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_7.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_8.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_9.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_10.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_11.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_12.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_13.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_14.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_15.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_16.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_17.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_18.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_19.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_magic_20.png")
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
		return super.isDisabled() || !caveMagic;
	}
}