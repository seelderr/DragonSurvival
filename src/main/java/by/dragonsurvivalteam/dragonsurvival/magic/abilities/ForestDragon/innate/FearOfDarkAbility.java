package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.innate;


import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@RegisterDragonAbility
public class FearOfDarkAbility extends InnateDragonAbility{
	@Override
	public String getName(){
		return "fear_of_dark";
	}

	@Override
	public DragonType getDragonType(){
		return DragonType.FOREST;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/fear_of_dark_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/fear_of_dark_1.png")};
	}

	@Override
	public int getLevel(){
		return ServerConfig.penalties && ServerConfig.forestStressTicks != 0.0 ? 1 : 0;
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled(){
		return super.isDisabled() || !ServerConfig.penalties || ServerConfig.forestStressTicks == 0.0;
	}

	@Override
	public int getSortOrder(){
		return 4;
	}
}