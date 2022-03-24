package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Passives/SpectralImpactAbility.java
import by.jackraidenph.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
=======
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Passives/SpectralImpactAbility.java
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class SpectralImpactAbility extends PassiveDragonAbility{
	public SpectralImpactAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public SpectralImpactAbility createInstance(){
		return new SpectralImpactAbility(type, id, icon, minLevel, maxLevel);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Passives/SpectralImpactAbility.java
	public Component getDescription()
	{
		return new TranslatableComponent("ds.skill.description." + getId(), getChance());
=======
	public IFormattableTextComponent getDescription(){
		return new TranslationTextComponent("ds.skill.description." + getId(), getChance());
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Passives/SpectralImpactAbility.java
	}

	public int getChance(){
		return ConfigHandler.SERVER.spectralImpactProcChance.get() * getLevel();
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.chance", "+" + ConfigHandler.SERVER.spectralImpactProcChance.get()));
		return list;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !ConfigHandler.SERVER.spectralImpact.get();
	}
}