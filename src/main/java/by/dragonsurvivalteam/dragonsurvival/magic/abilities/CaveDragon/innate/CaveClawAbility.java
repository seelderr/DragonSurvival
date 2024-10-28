package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.innate;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.DragonClawsAbility;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class CaveClawAbility extends DragonClawsAbility {
	@Override
	public String getName() {
		return "cave_claws_and_teeth";
	}

	@Override
	public AbstractDragonType getDragonType() {
		return DragonTypes.CAVE;
	}

	@Override
	public ResourceLocation[] getSkillTextures() {
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_0.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_1.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_2.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_3.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_4.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_5.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_6.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/cave/cave_claws_and_teeth_7.png")};
	}
}