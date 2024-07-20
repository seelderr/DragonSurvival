package by.dragonsurvivalteam.dragonsurvival.registry;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers.SLOW_MOVEMENT;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers.TOUGH_SKIN;

import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.ToughSkinAbility;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class DSEffects {

	public static final DeferredRegister<MobEffect> DS_MOB_EFFECTS = DeferredRegister.create(
			BuiltInRegistries.MOB_EFFECT,
			MODID
	);

	private static class Stress extends MobEffect{

		protected Stress(int color){
			super(MobEffectCategory.HARMFUL, color);
		}

		@Override
		public boolean applyEffectTick(@NotNull LivingEntity living, int p_76394_2_){
			if(living instanceof Player player){
				FoodData food = player.getFoodData();

				if(food.getSaturationLevel() > 0){
					int oldFood = food.getFoodLevel();
					food.eat(1, (float)((-0.5F * food.getSaturationLevel()) * ServerConfig.stressExhaustion));
					if(oldFood != 20){
						food.setFoodLevel((int)(food.getFoodLevel() - 1 * ServerConfig.stressExhaustion));
					}
				}

				player.causeFoodExhaustion((float)(1.0f * ServerConfig.stressExhaustion));

				return true;
			}

			return false;
		}

		@Override
		public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier){
			int i = 20 >> pAmplifier;
			if(i > 0){
				return pDuration % i == 0;
			}else{
				return true;
			}
		}
	}

	public static Holder<MobEffect> STRESS = DS_MOB_EFFECTS.register(
		"stress",
		() -> new Stress(0xf4a2e8)
	);

	private static class Trapped extends MobEffect{
		protected Trapped(MobEffectCategory effectType, int color){
			super(effectType, color);
		}

		// Make this uncurable
		@Override
		public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
			cures.clear();
		}

		@Override
		public boolean applyEffectTick(LivingEntity living, int strength){
			living.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SLOW_MOVEMENT);
			return true;
		}

		@Override
		public boolean shouldApplyEffectTickThisTick(int timeLeft, int p_76397_2_){
			return timeLeft == 1;
		}
	}

	public static Holder<MobEffect> TRAPPED = DS_MOB_EFFECTS.register(
		"trapped",
		() -> new Trapped(MobEffectCategory.HARMFUL, 0xdddddd)
	);

	private static class ModifiableMobEffect extends MobEffect{
		private final boolean uncurable;

		protected ModifiableMobEffect(MobEffectCategory type, int color, boolean uncurable){
			super(type, color);
			this.uncurable = uncurable;
		}

		@Override
		public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
			if (uncurable) {
				cures.clear();
			} else {
				super.fillEffectCures(cures, effectInstance);
			}
		}
	}

	public static Holder<MobEffect> HUNTER_OMEN = DS_MOB_EFFECTS.register(
		"hunter_omen",
		() -> new ModifiableMobEffect(MobEffectCategory.NEUTRAL, 0x0, true)
	);

	public static Holder<MobEffect> PEACE = DS_MOB_EFFECTS.register(
		"peace",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> MAGIC = DS_MOB_EFFECTS.register(
		"magic",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> FIRE = DS_MOB_EFFECTS.register(
		"fire",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> ANIMAL_PEACE = DS_MOB_EFFECTS.register(
		"animal_peace",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> SOURCE_OF_MAGIC = DS_MOB_EFFECTS.register(
		"source_of_magic",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	private static class TradeEffect extends MobEffect{

		protected TradeEffect(MobEffectCategory type, int color){
			super(type, color);
		}

		// Make this uncurable
		@Override
		public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
			cures.clear();
		}

		@Override
		public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
			return true;
		}
	}

	public static Holder<MobEffect> ROYAL_DEPARTURE = DS_MOB_EFFECTS.register(
		"royal_departure",
		() -> new TradeEffect(MobEffectCategory.HARMFUL, -3407617)
	);

	public static Holder<MobEffect> WATER_VISION = DS_MOB_EFFECTS.register(
		"water_vision",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> LAVA_VISION = DS_MOB_EFFECTS.register(
		"lava_vision",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> HUNTER = DS_MOB_EFFECTS.register(
		"hunter",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> REVEALING_THE_SOUL = DS_MOB_EFFECTS.register(
		"revealing_the_soul",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
	);

	public static Holder<MobEffect> BURN = DS_MOB_EFFECTS.register(
		"burn",
		() -> new ModifiableMobEffect(MobEffectCategory.HARMFUL, 0x0, false)
	);

	public static Holder<MobEffect> CHARGED = DS_MOB_EFFECTS.register(
		"charged",
		() -> new ModifiableMobEffect(MobEffectCategory.HARMFUL, 0x0, false)
	);

	public static Holder<MobEffect> DRAIN = DS_MOB_EFFECTS.register(
		"drain",
		() -> new ModifiableMobEffect(MobEffectCategory.HARMFUL, 0x0, false)
	);

	public static Holder<MobEffect> STRONG_LEATHER = DS_MOB_EFFECTS.register(
		"strong_leather",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, false)
			.addAttributeModifier(Attributes.ARMOR, TOUGH_SKIN, ToughSkinAbility.toughSkinArmorValue, Operation.ADD_VALUE)
	);

	public static Holder<MobEffect> cave_wings = DS_MOB_EFFECTS.register(
		"wings_cave",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, true)
	);

	public static Holder<MobEffect> sea_wings = DS_MOB_EFFECTS.register(
		"wings_sea",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, true)
	);

	public static Holder<MobEffect> forest_wings = DS_MOB_EFFECTS.register(
		"wings_forest",
		() -> new ModifiableMobEffect(MobEffectCategory.BENEFICIAL, 0x0, true)
	);
}