package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.Bolas;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.ToughSkinAbility;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DragonEffects{

	public static MobEffect STRESS;
	public static MobEffect TRAPPED;
	public static MobEffect ROYAL_CHASE;
	public static MobEffect PEACE, MAGIC, FIRE;
	public static MobEffect ANIMAL_PEACE;
	public static MobEffect PREDATOR_ANTI_SPAWN;

	public static MobEffect SOURCE_OF_MAGIC;

	public static MobEffect ROYAL_DEPARTURE;

	//Magic system effects
	public static MobEffect WATER_VISION, LAVA_VISION;
	public static MobEffect HUNTER;
	public static MobEffect REVEALING_THE_SOUL;
	public static MobEffect BURN, CHARGED, DRAIN;
	public static MobEffect STRONG_LEATHER;

	public static MobEffect cave_wings, sea_wings, forest_wings;

	@SuppressWarnings( "unused" )
	@SubscribeEvent
	public static void registerEffects(RegistryEvent.Register<MobEffect> effectRegister){
		IForgeRegistry<MobEffect> forgeRegistry = effectRegister.getRegistry();
		STRESS = new Stress(0xf4a2e8).setRegistryName(DragonSurvivalMod.MODID, "stress");
		forgeRegistry.register(STRESS);
		TRAPPED = new Trapped(MobEffectCategory.HARMFUL, 0xdddddd, true).setRegistryName(DragonSurvivalMod.MODID, "trapped");
		forgeRegistry.register(TRAPPED);
		ROYAL_CHASE = new RoyalChase(MobEffectCategory.NEUTRAL).setRegistryName(DragonSurvivalMod.MODID, "royal_chase");
		forgeRegistry.register(ROYAL_CHASE);
		PEACE = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "peace");
		forgeRegistry.register(PEACE);
		MAGIC = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "magic");
		forgeRegistry.register(MAGIC);
		FIRE = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "fire");
		forgeRegistry.register(FIRE);
		ANIMAL_PEACE = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "animal_peace");
		forgeRegistry.register(ANIMAL_PEACE);
		PREDATOR_ANTI_SPAWN = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "predator_anti_spawn");
		forgeRegistry.register(PREDATOR_ANTI_SPAWN);

		SOURCE_OF_MAGIC = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "source_of_magic");
		forgeRegistry.register(SOURCE_OF_MAGIC);

		ROYAL_DEPARTURE = new TradeEffect(MobEffectCategory.HARMFUL, -3407617, true).setRegistryName(DragonSurvivalMod.MODID, "royal_departure");
		forgeRegistry.register(ROYAL_DEPARTURE);

		//Magic system effects
		WATER_VISION = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "water_vision");
		forgeRegistry.register(WATER_VISION);

		LAVA_VISION = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "lava_vision");
		forgeRegistry.register(LAVA_VISION);

		HUNTER = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "hunter");
		forgeRegistry.register(HUNTER);

		REVEALING_THE_SOUL = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "revealing_the_soul");
		forgeRegistry.register(REVEALING_THE_SOUL);

		STRONG_LEATHER = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "strong_leather");
		STRONG_LEATHER.addAttributeModifier(Attributes.ARMOR, "1640719a-4c40-11ec-81d3-0242ac130003", ToughSkinAbility.toughSkinArmorValue, Operation.ADDITION);
		forgeRegistry.register(STRONG_LEATHER);

		BURN = new Effect2(MobEffectCategory.HARMFUL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "burn");
		forgeRegistry.register(BURN);

		CHARGED = new Effect2(MobEffectCategory.HARMFUL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "charged");
		forgeRegistry.register(CHARGED);

		DRAIN = new Effect2(MobEffectCategory.HARMFUL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "drain");
		forgeRegistry.register(DRAIN);

		forest_wings = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, true).setRegistryName(DragonSurvivalMod.MODID, "wings_forest");
		forgeRegistry.register(forest_wings);

		sea_wings = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, true).setRegistryName(DragonSurvivalMod.MODID, "wings_sea");
		forgeRegistry.register(sea_wings);

		cave_wings = new Effect2(MobEffectCategory.BENEFICIAL, 0x0, true).setRegistryName(DragonSurvivalMod.MODID, "wings_cave");
		forgeRegistry.register(cave_wings);
	}

	private static class Effect2 extends MobEffect{
		private final boolean uncurable;

		protected Effect2(MobEffectCategory type, int color, boolean uncurable){
			super(type, color);
			this.uncurable = uncurable;
		}

		@Override
		public List<ItemStack> getCurativeItems(){
			return uncurable ? Collections.emptyList() : super.getCurativeItems();
		}
	}

	private static class Stress extends MobEffect{

		protected Stress(int color){
			super(MobEffectCategory.HARMFUL, color);
		}

		@Override
		public void applyEffectTick(LivingEntity living, int p_76394_2_){
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
			}
		}

		@Override
		public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_){
			int i = 20 >> p_76397_2_;
			if(i > 0){
				return p_76397_1_ % i == 0;
			}else{
				return true;
			}
		}
	}

	private static class Trapped extends MobEffect{
		protected Trapped(MobEffectCategory effectType, int color, boolean uncurable){
			super(effectType, color);
		}

		@Override
		public List<ItemStack> getCurativeItems(){
			return Collections.emptyList();
		}

		@Override
		public void applyEffectTick(LivingEntity living, int strength){
			living.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(Bolas.DISABLE_MOVEMENT);
			living.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).removeModifier(Bolas.DISABLE_JUMP);
		}

		@Override
		public boolean isDurationEffectTick(int timeLeft, int p_76397_2_){
			return timeLeft == 1;
		}
	}

	private static class RoyalChase extends MobEffect{

		protected RoyalChase(MobEffectCategory p_i50391_1_){
			super(p_i50391_1_, 0x0);
		}

		@Override
		public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier){
			super.applyEffectTick(pLivingEntity, pAmplifier);

			if(!DragonUtils.isDragon(pLivingEntity)){
				pLivingEntity.removeEffect(DragonEffects.ROYAL_CHASE);
			}
		}

		@Override
		public List<ItemStack> getCurativeItems(){
			return Collections.emptyList();
		}
	}
	private static class TradeEffect extends MobEffect{
		private final boolean uncurable;

		protected TradeEffect(MobEffectCategory type, int color, boolean uncurable){
			super(type, color);
			this.uncurable = uncurable;
		}

		public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
			super.removeAttributeModifiers(entity, attributeMap, amplifier);
			if (entity.getType().is(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("dragonsurvival:royal_departure_affected")))) {
				if (!entity.level.isClientSide())
					entity.discard();
			}
		}

		@Override
		public boolean isDurationEffectTick(int duration, int amplifier) {
			return true;
		}
	}
}