<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
package by.jackraidenph.dragonsurvival.common.magic.abilities.Actives.BuffAbilities;

import by.jackraidenph.dragonsurvival.client.handlers.KeyInputHandler;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
=======
package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BuffAbilities;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Locale;

public class HunterAbility extends ActiveDragonAbility{
	public HunterAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public HunterAbility createInstance(){
		return new HunterAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
	public Component getDescription()
	{
		return new TranslatableComponent("ds.skill.description." + getId(), (1.5 * getLevel() + "x"), getDuration());
=======
	public ArrayList<ITextComponent> getInfo(){
		ArrayList<ITextComponent> components = super.getInfo();

		if(!KeyInputHandler.ABILITY4.isUnbound()){
			String key = KeyInputHandler.ABILITY4.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY4.getKey().getDisplayName().getString();
			}
			components.add(new TranslationTextComponent("ds.skill.keybind", key));
		}

		return components;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
	public void onActivation(Player player)
	{
=======
	public void onActivation(PlayerEntity player){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
		super.onActivation(player);
		player.addEffect(new MobEffectInstance(DragonEffects.HUNTER, Functions.secondsToTicks(getDuration()), getLevel() - 1));
		player.level.playLocalSound(player.position().x, player.position().y + 0.5, player.position().z, SoundEvents.UI_TOAST_IN, SoundSource.PLAYERS, 5F, 0.1F, false);
	}

	public boolean canMoveWhileCasting(){return false;}

	@Override
	public AbilityAnimation getLoopingAnimation(){
		return new AbilityAnimation("cast_self_buff", true, false);
	}

	@Override
	public AbilityAnimation getStoppingAnimation(){
		return new AbilityAnimation("self_buff", 0.52 * 20, true, false);
	}

	public double getDamage(){
		return ConfigHandler.SERVER.hunterDamageBonus.get() * getLevel();
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
	public ArrayList<Component> getInfo()
	{
		ArrayList<Component> components = super.getInfo();
		
		if(!KeyInputHandler.ABILITY4.isUnbound()) {
			String key = KeyInputHandler.ABILITY4.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);
			
			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY4.getKey().getDisplayName().getString();
			}
			components.add(new TranslatableComponent("ds.skill.keybind", key));
		}
		
		return components;
=======
	public IFormattableTextComponent getDescription(){
		return new TranslationTextComponent("ds.skill.description." + getId(), (1.5 * getLevel() + "x"), getDuration());
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/HunterAbility.java
	}

	public int getDuration(){
		return ConfigHandler.SERVER.hunterDuration.get() * getLevel();
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.duration.seconds", "+" + ConfigHandler.SERVER.hunterDuration.get()));
		list.add(new TranslatableComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.hunterDamageBonus.get() + "X"));
		return list;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !ConfigHandler.SERVER.hunter.get();
	}
}