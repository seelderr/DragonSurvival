package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.innate;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.DragonWingAbility;
import net.minecraft.resources.ResourceLocation;

@RegisterDragonAbility
public class SeaWingsAbility extends DragonWingAbility{
	@Override
	public String getName(){
		return "sea_wings";
	}

	@Override
	public AbstractDragonType getDragonType(){
		return DragonTypes.SEA;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_wings_0.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/sea_wings_1.png")};
	}

	@Override
	public int getSortOrder(){
		return 2;
	}
}