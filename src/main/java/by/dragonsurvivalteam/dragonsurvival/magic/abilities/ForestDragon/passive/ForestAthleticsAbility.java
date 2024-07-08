package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.AthleticsAbility;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class ForestAthleticsAbility extends AthleticsAbility{
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon", "passives"}, key = "forestAthletics", comment = "Whether the forest athletics ability should be enabled" )
	public static Boolean forestAthletics = true;

	@Override
	public String getName(){
		return "forest_athletics";
	}

	@Override
	public int getSortOrder(){
		return 2;
	}

	@Override
	public AbstractDragonType getDragonType(){
		return DragonTypes.FOREST;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_0.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_1.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_2.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_3.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_4.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_athletics_5.png")};
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
		return super.isDisabled() || !ForestAthleticsAbility.forestAthletics;
	}
}