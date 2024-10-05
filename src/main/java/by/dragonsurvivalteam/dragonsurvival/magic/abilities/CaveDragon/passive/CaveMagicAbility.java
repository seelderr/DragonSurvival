package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.MagicAbility;
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
	public AbstractDragonType getDragonType(){
		return DragonTypes.CAVE;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_0.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_1.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_2.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_3.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_4.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_5.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_6.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_7.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_8.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_9.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_10.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_11.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_12.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_13.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_14.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_15.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_16.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_17.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_18.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_19.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_magic_20.png")
		                              };
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !caveMagic;
	}
}