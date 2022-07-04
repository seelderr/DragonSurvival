package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.AthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.resources.ResourceLocation;

@RegisterDragonAbility
public class SeaAthleticsAbility extends AthleticsAbility{
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "passives"}, key = "seaAthletics", comment = "Whether the sea athletics ability should be enabled" )
	public static Boolean seaAthletics = true;

	@Override
	public String getName(){
		return "sea_athletics";
	}

	@Override
	public DragonType getDragonType(){
		return DragonType.SEA;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/sea_athletics_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/sea_athletics_1.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/sea_athletics_2.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/sea_athletics_3.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/sea_athletics_4.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/sea_athletics_5.png")};
	}

	@Override
	public int getSortOrder(){
		return 2;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !seaAthletics;
	}
}