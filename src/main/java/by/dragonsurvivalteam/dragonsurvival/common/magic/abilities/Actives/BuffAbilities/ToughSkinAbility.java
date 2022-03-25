package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BuffAbilities;


import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Locale;


public class ToughSkinAbility extends AoeBuffAbility{
	public ToughSkinAbility(DragonType type, MobEffectInstance effect, int range, ParticleOptions particle, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
		super(type, effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}


	@Override
	public ToughSkinAbility createInstance(){
		return new ToughSkinAbility(type, effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}


	@Override
	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = super.getInfo();

		if(!KeyInputHandler.ABILITY3.isUnbound()){
			components = new ArrayList<>(components.subList(0, components.size() - 1));
		}

		components.add(new TranslatableComponent("ds.skill.duration.seconds", getDuration()));

		if(!KeyInputHandler.ABILITY3.isUnbound()){

			String key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getString();
			}
			components.add(new TranslatableComponent("ds.skill.keybind", key));
		}

		return components;
	}

	public boolean canMoveWhileCasting(){return false;}

	@Override
	public MobEffectInstance getEffect(){
		return new MobEffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()) * 4, getLevel() - 1, false, false);
	}

	@Override
	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getId(), getDuration(), getDefence(getLevel()));
	}

	public static double getDefence(int level){
		return level * ConfigHandler.SERVER.toughSkinArmorValue.get();
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.defence", "+" + ConfigHandler.SERVER.toughSkinArmorValue.get()));
		return list;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !ConfigHandler.SERVER.toughSkin.get();
	}
}