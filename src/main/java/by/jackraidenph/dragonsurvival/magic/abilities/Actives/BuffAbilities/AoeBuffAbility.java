package by.jackraidenph.dragonsurvival.magic.abilities.Actives.BuffAbilities;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.handlers.Client.KeyInputHandler;
import by.jackraidenph.dragonsurvival.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AoeBuffAbility extends ActiveDragonAbility
{
	protected EffectInstance effect;
	protected int range;
	protected ParticleType particle;
	
	public AoeBuffAbility(EffectInstance effect, int range, ParticleType particle, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.effect = effect;
		this.range = range;
		this.particle = particle;
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> components = super.getInfo();
		components.add(new TranslationTextComponent("ds.skill.duration.seconds", getDuration()));
		components.add(new TranslationTextComponent("ds.skill.aoe", getRange() + "x" + getRange()));
		
		if(effect.getEffect() == DragonEffects.REVEALING_THE_SOUL){
			components.add(new TranslationTextComponent("ds.skill.bonus_exp.multiplier", "2x"));
			components.add(new TranslationTextComponent("ds.skill.bonus_exp.max_gain", "20"));
		}
		
		if(!KeyInputHandler.ABILITY3.isUnbound()) {
			String key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);
			
			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getString();
			}
			components.add(new TranslationTextComponent("ds.skill.keybind", key));
		}
		
		return components;
	}
	
	@Override
	public AoeBuffAbility createInstance()
	{
		return new AoeBuffAbility(effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	public EffectInstance getEffect(){
		return new EffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()) * 4, effect.getAmplifier());
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		AreaEffectCloudEntity entity = new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, player.level);
		entity.setWaitTime(10);
		entity.setPos(player.position().x, player.position().y + 0.5, player.position().z);
		entity.setPotion(new Potion(getEffect())); //Effect duration is divided by 4 normaly
		entity.setDuration(20);
		entity.setRadius(getRange());
		
		try {
			entity.setParticle(particle.getDeserializer().fromCommand(particle, new StringReader("")));
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		
		List<LivingEntity> list1 = player.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox());
		if (!list1.isEmpty()) {
			for(LivingEntity livingentity : list1) {
				if (livingentity.isAffectedByPotions()) {
					double d0 = livingentity.getX() - entity.getX();
					double d1 = livingentity.getZ() - entity.getZ();
					double d2 = d0 * d0 + d1 * d1;
					if (d2 <= (double)(getRange() * getRange())) {
						livingentity.addEffect(new EffectInstance(new EffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()), effect.getAmplifier())));
					}
				}
			}
		}
		
		
		player.level.addFreshEntity(entity);
		player.level.playLocalSound(player.position().x, player.position().y + 0.5, player.position().z, SoundEvents.UI_TOAST_OUT, SoundCategory.PLAYERS, 5F, 0.1F, false);
	}
	
	public int getCastingSlowness() { return 10; }
	
	public int getDuration(){
		return getLevel() * (effect.getDuration() > 0 ? effect.getDuration() : 30);
	}
	
	public int getRange()
	{
		return range;
	}
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.duration.seconds", "+" + (effect.getDuration() > 0 ? effect.getDuration() : 30)));
		return list;
	}
	
	@Override
	public AbilityAnimation getLoopingAnimation()
	{
		return new AbilityAnimation("cast_mass_buff", true);
	}
	
	@Override
	public AbilityAnimation getStoppingAnimation()
	{
		return new AbilityAnimation("mass_buff", 160, true);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
	}
}
