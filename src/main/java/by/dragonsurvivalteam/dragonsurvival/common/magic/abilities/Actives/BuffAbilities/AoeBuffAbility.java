<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
package by.jackraidenph.dragonsurvival.common.magic.abilities.Actives.BuffAbilities;

import by.jackraidenph.dragonsurvival.client.handlers.KeyInputHandler;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
public class AoeBuffAbility extends ActiveDragonAbility
{
	protected MobEffectInstance effect;
	protected int range;
	protected ParticleOptions particle;
	
	public AoeBuffAbility(DragonType type, MobEffectInstance effect, int range, ParticleOptions particle, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
=======
public class AoeBuffAbility extends ActiveDragonAbility{
	protected EffectInstance effect;
	protected int range;
	protected IParticleData particle;

	public AoeBuffAbility(DragonType type, EffectInstance effect, int range, IParticleData particle, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.effect = effect;
		this.range = range;
		this.particle = particle;
	}

	@Override
	public AoeBuffAbility createInstance(){
		return new AoeBuffAbility(type, effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
	public ArrayList<Component> getInfo()
	{
		ArrayList<Component> components = super.getInfo();
		components.add(new TranslatableComponent("ds.skill.duration.seconds", getDuration()));
		components.add(new TranslatableComponent("ds.skill.aoe", getRange() + "x" + getRange()));
		
=======
	public ArrayList<ITextComponent> getInfo(){
		ArrayList<ITextComponent> components = super.getInfo();
		components.add(new TranslationTextComponent("ds.skill.duration.seconds", getDuration()));
		components.add(new TranslationTextComponent("ds.skill.aoe", getRange() + "x" + getRange()));

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
		if(effect.getEffect() == DragonEffects.REVEALING_THE_SOUL){
			components.add(new TranslatableComponent("ds.skill.bonus_exp.multiplier", ConfigHandler.SERVER.revealingTheSoulMultiplier.get() + "x"));
			components.add(new TranslatableComponent("ds.skill.bonus_exp.max_gain", Integer.toString(ConfigHandler.SERVER.revealingTheSoulMaxEXP.get())));
		}

		if(!KeyInputHandler.ABILITY3.isUnbound()){
			String key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getString();
			}
			components.add(new TranslatableComponent("ds.skill.keybind", key));
		}

		return components;
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
	
	@Override
	public boolean isDisabled()
	{
		if(effect.getEffect() == DragonEffects.REVEALING_THE_SOUL && !ConfigHandler.SERVER.revealingTheSoul.get()) return true;
		if(effect.getEffect() == DragonEffects.STRONG_LEATHER && !ConfigHandler.SERVER.toughSkin.get()) return true;
		if(effect.getEffect() == MobEffects.DIG_SPEED && !ConfigHandler.SERVER.inspiration.get()) return true;
		return super.isDisabled();
	}
	
	@Override
	public AoeBuffAbility createInstance()
	{
		return new AoeBuffAbility(type, effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	public MobEffectInstance getEffect(){
		return new MobEffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()) * 4, effect.getAmplifier());
	}
	
	@Override
	public void onActivation(Player player)
	{
=======

	@Override
	public void onActivation(PlayerEntity player){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
		super.onActivation(player);
		float f5 = (float)Math.PI * getRange() * getRange();

		for(int i = 0; i < 20; i++){
			for(int k1 = 0; (float)k1 < f5; ++k1){
				float f6 = player.level.random.nextFloat() * ((float)Math.PI * 2F);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
				float f7 = Mth.sqrt(player.level.random.nextFloat()) * getRange();
				float f8 = Mth.cos(f6) * f7;
				float f9 = Mth.sin(f6) * f7;
				player.level.addAlwaysVisibleParticle(particle, player.getX() + (double)f8, player.getY(), player.getZ() + (double)f9, (0.5D - player.level.random.nextDouble()) * 0.15D, (double)0.01F, (0.5D - player.level.random.nextDouble()) * 0.15D);
=======
				float f7 = MathHelper.sqrt(player.level.random.nextFloat()) * getRange();
				float f8 = MathHelper.cos(f6) * f7;
				float f9 = MathHelper.sin(f6) * f7;
				player.level.addAlwaysVisibleParticle(particle, player.getX() + (double)f8, player.getY(), player.getZ() + (double)f9, (0.5D - player.level.random.nextDouble()) * 0.15D, 0.01F, (0.5D - player.level.random.nextDouble()) * 0.15D);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
			}
		}

		List<LivingEntity> list1 = player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(getRange()));
		if(!list1.isEmpty()){
			for(LivingEntity livingentity : list1){
				if(livingentity.isAffectedByPotions()){
					double d0 = livingentity.getX() - player.getX();
					double d1 = livingentity.getZ() - player.getZ();
					double d2 = d0 * d0 + d1 * d1;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
					if (d2 <= (double)(getRange() * getRange())) {
						livingentity.addEffect(new MobEffectInstance(new MobEffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()), effect.getAmplifier())));
=======
					if(d2 <= (double)(getRange() * getRange())){
						livingentity.addEffect(new EffectInstance(new EffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()), effect.getAmplifier())));
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
					}
				}
			}
		}
		player.level.playLocalSound(player.position().x, player.position().y + 0.5, player.position().z, SoundEvents.UI_TOAST_OUT, SoundSource.PLAYERS, 5F, 0.1F, false);
	}

	public boolean canMoveWhileCasting(){return false;}

	@Override
	public AbilityAnimation getLoopingAnimation(){
		return new AbilityAnimation("cast_mass_buff", true, true);
	}

	@Override
	public AbilityAnimation getStoppingAnimation(){
		return new AbilityAnimation("mass_buff", 0.56 * 20, true, true);
	}

	public int getDuration(){
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
		if(effect.getEffect() == MobEffects.DIG_SPEED){
			return ConfigHandler.SERVER.inspirationDuration.get();
=======
		if(effect.getEffect() == Effects.DIG_SPEED){
			return getLevel() * ConfigHandler.SERVER.inspirationDuration.get();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
		}else if(effect.getEffect() == DragonEffects.REVEALING_THE_SOUL){
			return getLevel() * ConfigHandler.SERVER.revealingTheSoulDuration.get();
		}else if(effect.getEffect() == DragonEffects.STRONG_LEATHER){
			return getLevel() * ConfigHandler.SERVER.toughSkinDuration.get();
		}
		return getLevel() * (effect.getDuration() > 0 ? effect.getDuration() : 30);
	}

	public int getRange(){
		return range;
	}

	public EffectInstance getEffect(){
		return new EffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()) * 4, effect.getAmplifier());
	}

	@Override
	public IFormattableTextComponent getDescription(){
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.duration.seconds", "+" + (effect.getDuration() > 0 ? effect.getDuration() : 30)));
		return list;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
	public Component getDescription()
	{
		return new TranslatableComponent("ds.skill.description." + getId(), getDuration());
=======
	public boolean isDisabled(){
		if(effect.getEffect() == DragonEffects.REVEALING_THE_SOUL && !ConfigHandler.SERVER.revealingTheSoul.get()){
			return true;
		}
		if(effect.getEffect() == DragonEffects.STRONG_LEATHER && !ConfigHandler.SERVER.toughSkin.get()){
			return true;
		}
		if(effect.getEffect() == Effects.DIG_SPEED && !ConfigHandler.SERVER.inspiration.get()){
			return true;
		}
		return super.isDisabled();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BuffAbilities/AoeBuffAbility.java
	}
}