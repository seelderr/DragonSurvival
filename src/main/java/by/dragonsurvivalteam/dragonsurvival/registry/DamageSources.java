package by.dragonsurvivalteam.dragonsurvival.registry;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSources{
	public static final DamageSource STAR_DRAIN = new DamageSource("starDrain").bypassArmor().bypassMagic();
	public static final DamageSource WATER_BURN = new DamageSource("waterBurn").bypassArmor();
	public static final DamageSource RAIN_BURN = new DamageSource("rainBurn").bypassArmor();
	public static final DamageSource DEHYDRATION = new DamageSource("dehydration").bypassArmor();
	public static final DamageSource CRUSHED = new DamageSource("crushed");
}