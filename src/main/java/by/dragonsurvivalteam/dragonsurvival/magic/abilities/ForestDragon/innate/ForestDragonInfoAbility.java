package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.resources.ResourceLocation;

@RegisterDragonAbility
public class ForestDragonInfoAbility extends InnateDragonAbility{
	@Override
	public String getName(){
		return "forest_dragon";
	}

	@Override
	public DragonType getDragonType(){
		return DragonType.FOREST;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/forest/forest_dragon_1.png")};
	}

	@Override
	public int getSortOrder(){
		return 3;
	}
}