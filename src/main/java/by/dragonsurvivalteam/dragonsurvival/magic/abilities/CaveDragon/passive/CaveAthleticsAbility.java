package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.AthleticsAbility;
import net.minecraft.resources.ResourceLocation;

@RegisterDragonAbility
public class CaveAthleticsAbility extends AthleticsAbility{
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "passives"}, key = "caveAthletics", comment = "Whether the cave athletics ability should be enabled" )
	public static Boolean caveAthletics = true;

	@Override
	public int getSortOrder(){
		return 2;
	}

	@Override
	public String getName(){
		return "cave_athletics";
	}

	@Override
	public AbstractDragonType getDragonType(){
		return DragonTypes.CAVE;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_athletics_0.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_athletics_1.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_athletics_2.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_athletics_3.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_athletics_4.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_athletics_5.png")};
	}

	@Override
	@SuppressWarnings("RedundantMethodOverride")
	public int getMaxLevel(){
		return 5;
	}

	@Override
	@SuppressWarnings("RedundantMethodOverride")
	public int getMinLevel(){
		return 0;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !caveAthletics;
	}
}