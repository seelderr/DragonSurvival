package by.dragonsurvivalteam.dragonsurvival.common;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;

public class EffectInstance2 extends MobEffectInstance{
	public EffectInstance2(MobEffect effect){
		super(effect);
	}

	public EffectInstance2(MobEffect effect, int duration){
		super(effect, duration);
	}

	public EffectInstance2(MobEffect effect, int duration, int strength){
		super(effect, duration, strength);
	}

	public EffectInstance2(MobEffect effect, int duration, int strength, boolean ambient, boolean visible){
		super(effect, duration, strength, ambient, visible);
	}

	public EffectInstance2(MobEffect effect, int duration, int strength, boolean ambient, boolean visible, boolean showIcon){
		super(effect, duration, strength, ambient, visible, showIcon);
	}

	public EffectInstance2(MobEffect effect, int duration, int strength, boolean ambient, boolean visible, boolean showIcon,
		@Nullable
			MobEffectInstance hiddenEffect){
		super(effect, duration, strength, ambient, visible, showIcon, hiddenEffect);
	}

	public EffectInstance2(MobEffectInstance other){
		super(other);
	}
}