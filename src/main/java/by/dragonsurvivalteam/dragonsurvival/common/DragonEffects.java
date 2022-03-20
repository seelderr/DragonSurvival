package by.dragonsurvivalteam.dragonsurvival.common;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BolasEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DragonEffects{


	public static Effect STRESS;
	public static Effect TRAPPED;
	public static Effect EVIL_DRAGON;
	public static Effect PEACE, MAGIC, FIRE;
	public static Effect ANIMAL_PEACE;
	public static Effect PREDATOR_ANTI_SPAWN;

	public static Effect SOURCE_OF_MAGIC;

	//Magic system effects
	public static Effect WATER_VISION, LAVA_VISION;
	public static Effect HUNTER;
	public static Effect REVEALING_THE_SOUL;
	public static Effect BURN, CHARGED, DRAIN;
	public static Effect STRONG_LEATHER;

	public static Effect cave_wings, sea_wings, forest_wings;

	@SuppressWarnings( "unused" )
	@SubscribeEvent
	public static void registerEffects(RegistryEvent.Register<Effect> effectRegister){
		IForgeRegistry<Effect> forgeRegistry = effectRegister.getRegistry();
		STRESS = new Stress(0xf4a2e8).setRegistryName(DragonSurvivalMod.MODID, "stress");
		forgeRegistry.register(STRESS);
		TRAPPED = new Trapped(EffectType.NEUTRAL, 0xdddddd).setRegistryName(DragonSurvivalMod.MODID, "trapped");
		forgeRegistry.register(TRAPPED);
		EVIL_DRAGON = new EvilDragon(EffectType.NEUTRAL).setRegistryName(DragonSurvivalMod.MODID, "evil_dragon");
		forgeRegistry.register(EVIL_DRAGON);
		PEACE = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "peace");
		forgeRegistry.register(PEACE);
		MAGIC = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "magic");
		forgeRegistry.register(MAGIC);
		FIRE = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "fire");
		forgeRegistry.register(FIRE);
		ANIMAL_PEACE = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "animal_peace");
		forgeRegistry.register(ANIMAL_PEACE);
		PREDATOR_ANTI_SPAWN = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "predator_anti_spawn");
		forgeRegistry.register(PREDATOR_ANTI_SPAWN);

		SOURCE_OF_MAGIC = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "source_of_magic");
		forgeRegistry.register(SOURCE_OF_MAGIC);

		//Magic system effects
		WATER_VISION = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "water_vision");
		forgeRegistry.register(WATER_VISION);

		LAVA_VISION = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "lava_vision");
		forgeRegistry.register(LAVA_VISION);

		HUNTER = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "hunter");
		forgeRegistry.register(HUNTER);

		REVEALING_THE_SOUL = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "revealing_the_soul");
		forgeRegistry.register(REVEALING_THE_SOUL);

		STRONG_LEATHER = new Effect2(EffectType.BENEFICIAL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "strong_leather");
		STRONG_LEATHER.addAttributeModifier(Attributes.ARMOR, "1640719a-4c40-11ec-81d3-0242ac130003", ConfigHandler.SERVER.toughSkinArmorValue.get(), Operation.ADDITION);
		forgeRegistry.register(STRONG_LEATHER);

		BURN = new Effect2(EffectType.HARMFUL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "burn");
		forgeRegistry.register(BURN);

		CHARGED = new Effect2(EffectType.HARMFUL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "charged");
		forgeRegistry.register(CHARGED);

		DRAIN = new Effect2(EffectType.HARMFUL, 0x0, false).setRegistryName(DragonSurvivalMod.MODID, "drain");
		forgeRegistry.register(DRAIN);

		forest_wings = new Effect2(EffectType.BENEFICIAL, 0x0, true).setRegistryName(DragonSurvivalMod.MODID, "wings_forest");
		forgeRegistry.register(forest_wings);

		sea_wings = new Effect2(EffectType.BENEFICIAL, 0x0, true).setRegistryName(DragonSurvivalMod.MODID, "wings_sea");
		forgeRegistry.register(sea_wings);

		cave_wings = new Effect2(EffectType.BENEFICIAL, 0x0, true).setRegistryName(DragonSurvivalMod.MODID, "wings_cave");
		forgeRegistry.register(cave_wings);
	}

	private static class Effect2 extends Effect{
		private final boolean uncurable;

		protected Effect2(EffectType type, int color, boolean uncurable){
			super(type, color);
			this.uncurable = uncurable;
		}

		@Override
		public List<ItemStack> getCurativeItems(){
			return uncurable ? Collections.emptyList() : super.getCurativeItems();
		}
	}

	private static class Stress extends Effect{

		protected Stress(int color){
			super(EffectType.HARMFUL, color);
		}

		@Override
		public void applyEffectTick(LivingEntity livingEntity, int p_76394_2_){
			if(livingEntity instanceof PlayerEntity){
				PlayerEntity playerEntity = (PlayerEntity)livingEntity;
				FoodStats food = playerEntity.getFoodData();
				if(food.getSaturationLevel() > 0){
					int oldFood = food.getFoodLevel();
					food.eat(1, -0.5F * food.getSaturationLevel());
					if(oldFood != 20){
						food.setFoodLevel(food.getFoodLevel() - 1);
					}
				}
				playerEntity.causeFoodExhaustion(1.0f);
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

	private static class Trapped extends Effect{
		protected Trapped(EffectType effectType, int color){
			super(effectType, color);
		}

		public List<ItemStack> getCurativeItems(){
			return Collections.emptyList();
		}

		public void applyEffectTick(LivingEntity livingEntity, int strength){
			livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(BolasEntity.DISABLE_MOVEMENT);
			livingEntity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).removeModifier(BolasEntity.DISABLE_JUMP);
		}

		public boolean isDurationEffectTick(int timeLeft, int p_76397_2_){
			return (timeLeft == 1);
		}
	}

	private static class EvilDragon extends Effect{

		protected EvilDragon(EffectType p_i50391_1_){
			super(p_i50391_1_, 0x0);
		}

		@Override
		public List<ItemStack> getCurativeItems(){
			return Collections.emptyList();
		}
	}
}