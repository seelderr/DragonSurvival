package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.DragonWingAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.resources.ResourceLocation;

@RegisterDragonAbility
public class CaveWingsAbility extends DragonWingAbility{
	@Override
	public String getName(){
		return "cave_wings";
	}

	@Override
	public DragonType getDragonType(){
		return DragonType.CAVE;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_wings_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/cave_wings_1.png")};
	}

	@Override
	public int getSortOrder(){
		return 2;
	}
}