package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.innate;


import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class AmphibianAbility extends InnateDragonAbility{
	@Override
	public Component getDescription(){
		return Component.translatable("ds.skill.description." + getName(), ServerConfig.seaDehydrationDamage, 2);
	}

	@Override
	public int getSortOrder(){
		return 4;
	}

	@Override
	public String getName(){
		return "amphibian";
	}

	@Override
	public AbstractDragonType getDragonType(){
		return DragonTypes.SEA;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/amphibian_0.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/amphibian_1.png")};
	}
	@Override
	public int getLevel(){
		return ServerConfig.penalties && ServerConfig.seaTicksWithoutWater != 0.0 ? 1 : 0;
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public boolean isDisabled(){
		return super.isDisabled() || !ServerConfig.penalties || ServerConfig.seaTicksWithoutWater == 0.0;
	}
}