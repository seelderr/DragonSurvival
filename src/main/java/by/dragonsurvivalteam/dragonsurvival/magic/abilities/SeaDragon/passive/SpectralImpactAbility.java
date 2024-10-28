package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive;


import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class SpectralImpactAbility extends PassiveDragonAbility {
	@ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "passives"}, key = "spectralImpact", comment = "Whether the spectralImpact ability should be enabled")
	public static Boolean spectralImpact = true;

	@ConfigRange(min = 0, max = 100)
	@ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "passives"}, key = "spectralImpactProcChance", comment = "The percentage chance that spectral impact will proc. This is multiplied by the level of the skill.")
	public static Integer spectralImpactProcChance = 15;

	@Override
	public Component getDescription() {
		return Component.translatable("ds.skill.description." + getName(), getChance());
	}

	@Override
	public String getName() {
		return "spectral_impact";
	}

	@Override
	public int getSortOrder() {
		return 4;
	}

	@Override
	public AbstractDragonType getDragonType() {
		return DragonTypes.SEA;
	}

	@Override
	public ResourceLocation[] getSkillTextures() {
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/spectral_impact_0.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/spectral_impact_1.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/spectral_impact_2.png"),
				ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/spectral_impact_3.png")};
	}

	public int getChance() {
		return spectralImpactProcChance * getLevel();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ArrayList<Component> getLevelUpInfo() {
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(Component.translatable("ds.skill.chance", "+" + spectralImpactProcChance));
		return list;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public boolean isDisabled() {
		return super.isDisabled() || !spectralImpact;
	}
}