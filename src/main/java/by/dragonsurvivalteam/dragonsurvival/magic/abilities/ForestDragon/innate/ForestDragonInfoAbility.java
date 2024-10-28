package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class ForestDragonInfoAbility extends InnateDragonAbility {
	@Override
	public String getName() {
		return "forest_dragon";
	}

	@Override
	public AbstractDragonType getDragonType() {
		return DragonTypes.FOREST;
	}

	@Override
	public ResourceLocation[] getSkillTextures() {
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/forest/forest_dragon_1.png")};
	}

	@Override
	public int getSortOrder() {
		return 3;
	}
}