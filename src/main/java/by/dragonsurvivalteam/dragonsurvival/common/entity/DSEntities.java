package by.dragonsurvivalteam.dragonsurvival.common.entity;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.*;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredator;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.*;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.VillagerRelationsHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "rawtypes,unchecked" )
@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSEntities{
	private static final List<EntityType<?>> entities = Lists.newArrayList();
	public static EntityType<DragonEntity> DRAGON;
	public static EntityType<DragonEntity> DRAGON_ARMOR;
	public static EntityType<MagicalPredator> MAGICAL_BEAST;
	public static EntityType<HunterHoundEntity> HUNTER_HOUND;
	public static EntityType<Shooter> SHOOTER_HUNTER;
	public static EntityType<SquireEntity> SQUIRE_HUNTER;
	public static EntityType<Princess> PRINCESS;
	public static EntityType<KnightEntity> KNIGHT;
	public static EntityType<PrincesHorseEntity> PRINCESS_ON_HORSE;
	public static EntityType<PrinceHorseEntity> PRINCE_ON_HORSE;
	public static EntityType<Bolas> BOLAS_ENTITY;
	public static EntityType<DragonHitBox> DRAGON_HITBOX;
	//Magic abilities
	public static EntityType<DragonSpikeEntity> DRAGON_SPIKE;
	public static EntityType<BallLightningEntity> BALL_LIGHTNING;
	public static EntityType<FireBallEntity> FIREBALL;
	public static EntityType<StormBreathEntity> STORM_BREATH_EFFECT;
	public static VillagerProfession PRINCESS_PROFESSION, PRINCE_PROFESSION;

	@SubscribeEvent
	public static void attributeCreationEvent(EntityAttributeCreationEvent event){
//		event.put(MAGICAL_BEAST, MagicalPredator.createMonsterAttributes().build());
		event.put(DRAGON, DragonEntity.createLivingAttributes().build());
		event.put(DRAGON_ARMOR, DragonEntity.createLivingAttributes().build());
		event.put(DRAGON_HITBOX, DragonHitBox.createLivingAttributes().build());
		event.put(HUNTER_HOUND, Wolf.createAttributes().add(Attributes.MOVEMENT_SPEED, ServerConfig.houndSpeed).add(Attributes.ATTACK_DAMAGE, ServerConfig.houndDamage).add(Attributes.MAX_HEALTH, ServerConfig.houndHealth).build());
		event.put(SHOOTER_HUNTER, Pillager.createAttributes().add(Attributes.MOVEMENT_SPEED, ServerConfig.hunterSpeed).add(Attributes.MAX_HEALTH, ServerConfig.houndHealth).add(Attributes.ARMOR, ServerConfig.hunterArmor).add(Attributes.ATTACK_DAMAGE, ServerConfig.hunterDamage).build());
		event.put(SQUIRE_HUNTER, Vindicator.createAttributes().add(Attributes.MOVEMENT_SPEED, ServerConfig.squireSpeed).add(Attributes.ATTACK_DAMAGE, ServerConfig.squireDamage).add(Attributes.ARMOR, ServerConfig.squireArmor).add(Attributes.MAX_HEALTH, ServerConfig.squireHealth).build());
		event.put(PRINCESS, Villager.createAttributes().build());
		event.put(PRINCESS_ON_HORSE, Villager.createAttributes().build());
		event.put(KNIGHT, KnightEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, ServerConfig.knightSpeed).add(Attributes.ATTACK_DAMAGE, ServerConfig.knightDamage).add(Attributes.ARMOR, ServerConfig.knightArmor).add(Attributes.MAX_HEALTH, ServerConfig.knightHealth).build());
		event.put(PRINCE_ON_HORSE, Villager.createAttributes().add(Attributes.ATTACK_DAMAGE, ServerConfig.princeDamage).add(Attributes.MAX_HEALTH, ServerConfig.princeHealth).add(Attributes.ARMOR, ServerConfig.princeArmor).add(Attributes.MOVEMENT_SPEED, ServerConfig.princeSpeed).build());
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
		IForgeRegistry<EntityType<?>> registry = event.getRegistry();
		DRAGON = register(registry, "dummy_dragon", new EntityType<>(DragonEntity::new, MobCategory.MISC, true, false, false, false, ImmutableSet.of(), EntityDimensions.fixed(0.9f, 1.9f), 0, 0));
		DRAGON_ARMOR = register(registry, "dragon_armor", new EntityType<>(DragonEntity::new, MobCategory.MISC, true, false, false, false, ImmutableSet.of(), EntityDimensions.fixed(0.9f, 1.9f), 0, 0));
		DRAGON_HITBOX = register(registry, "dragon_hitbox", new EntityType<>(DragonHitBox::new, MobCategory.MISC, true, false, false, false, ImmutableSet.of(), EntityDimensions.fixed(0.5f, 0.5f), 1, 1));

		BOLAS_ENTITY = register(registry, "bolas", cast(EntityType.Builder.of((p_create_1_, p_create_2_) -> new Bolas(p_create_2_), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("bolas")));

		DRAGON_SPIKE = register(registry, "dragon_spike", EntityType.Builder.<DragonSpikeEntity>of(DragonSpikeEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).build("dragon_spike"));
		BALL_LIGHTNING = register(registry, "ball_lightning", EntityType.Builder.<BallLightningEntity>of(BallLightningEntity::new, MobCategory.MISC).sized(1F, 1F).clientTrackingRange(4).updateInterval(1).build("ball_lightning"));
		FIREBALL = register(registry, "fireball", EntityType.Builder.<FireBallEntity>of(FireBallEntity::new, MobCategory.MISC).sized(1F, 1F).clientTrackingRange(4).updateInterval(1).build("fireball"));
		STORM_BREATH_EFFECT = register(registry, "storm_breath_effect", EntityType.Builder.of(StormBreathEntity::new, MobCategory.MISC).sized(1F, 1F).clientTrackingRange(4).updateInterval(1).build("storm_breath_effect"));


		for(EntityType entity : entities){
			Preconditions.checkNotNull(entity.getRegistryName(), "registryName");
			registry.register(entity);
		}

		VillagerRelationsHandler.dragonHunters = new ArrayList<>(4);

		if(ServerConfig.spawnHound){
			VillagerRelationsHandler.dragonHunters.add(cast(HUNTER_HOUND));
		}

		if(ServerConfig.spawnSquire){
			VillagerRelationsHandler.dragonHunters.add(cast(SQUIRE_HUNTER));
		}

		if(ServerConfig.spawnHunter){
			VillagerRelationsHandler.dragonHunters.add(cast(SHOOTER_HUNTER));
		}

		if(ServerConfig.spawnKnight){
			VillagerRelationsHandler.dragonHunters.add(cast(KNIGHT));
		}


		SpawnPlacements.SpawnPredicate predicate = (pEntityType, serverWorld, mobSpawnType, pPos, random) -> serverWorld.getBlockState(pPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && serverWorld.canSeeSky(pPos);
		SpawnPlacements.SpawnPredicate princeSpawn = (pEntityType, serverWorld, mobSpawnType, pPos, random) -> predicate.test(pEntityType,serverWorld, mobSpawnType, pPos, random) && serverWorld.getLevel().isVillage(pPos);

		SpawnPlacements.register(SHOOTER_HUNTER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate);
		SpawnPlacements.register(SQUIRE_HUNTER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate);
		SpawnPlacements.register(PRINCESS, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, princeSpawn);
		SpawnPlacements.register(KNIGHT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate);
		SpawnPlacements.register(PRINCE_ON_HORSE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, princeSpawn);
		SpawnPlacements.register(PRINCESS_ON_HORSE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, princeSpawn);
	}

	private static EntityType register(IForgeRegistry<EntityType<?>> registry, String id, EntityType type){
		ResourceLocation location = new ResourceLocation(DragonSurvivalMod.MODID, id);
		type.setRegistryName(location);
		if(registry != null){
			registry.register(type);
		}else{
			entities.add(type);
		}
		return type;
	}

	private static <T extends EntityType<?>> T cast(EntityType<?> entityType){
		return (T)entityType;
	}

	@SubscribeEvent
	public static void registerVillageTypes(RegistryEvent.Register<VillagerProfession> event){
		PRINCESS_PROFESSION = new VillagerProfession("princess", PoiType.UNEMPLOYED, ImmutableSet.of(), ImmutableSet.of(), null);
		PRINCESS_PROFESSION.setRegistryName(new ResourceLocation(DragonSurvivalMod.MODID, "princess"));
		event.getRegistry().register(PRINCESS_PROFESSION);

		PRINCE_PROFESSION = new VillagerProfession("prince", PoiType.UNEMPLOYED, ImmutableSet.of(), ImmutableSet.of(), null);
		PRINCE_PROFESSION.setRegistryName(new ResourceLocation(DragonSurvivalMod.MODID, "prince"));
		event.getRegistry().register(PRINCE_PROFESSION);
	}

	@SubscribeEvent( priority = EventPriority.LOWEST )
	public static void registerSpawnEggs(RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> registry = event.getRegistry();

		HUNTER_HOUND = register(null, "hunter_hound", EntityType.Builder.of(HunterHoundEntity::new, MobCategory.MONSTER).sized(0.6F, 0.85F).clientTrackingRange(64).updateInterval(1).build("hunter_hound"));
		SHOOTER_HUNTER = register(null, "shooter", EntityType.Builder.of(Shooter::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(64).updateInterval(1).build("shooter"));
		SQUIRE_HUNTER = register(null, "squire", EntityType.Builder.of(SquireEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(64).updateInterval(1).build("squire"));
		PRINCESS = register(null, "princess_entity", EntityType.Builder.<Princess>of(Princess::new, MobCategory.MONSTER).sized(0.6F, 1.9F).clientTrackingRange(64).updateInterval(1).build("princess_entity"));
		KNIGHT = register(null, "knight", EntityType.Builder.of(KnightEntity::new, MobCategory.MONSTER).sized(0.8f, 2.5f).clientTrackingRange(64).updateInterval(1).build("knight"));
		PRINCE_ON_HORSE = register(null, "prince", EntityType.Builder.<PrinceHorseEntity>of(PrinceHorseEntity::new, MobCategory.MONSTER).sized(0.8f, 2.5f).clientTrackingRange(64).updateInterval(1).build("prince"));
		PRINCESS_ON_HORSE = register(null, "princess", EntityType.Builder.<PrincesHorseEntity>of(PrincesHorseEntity::new, MobCategory.MONSTER).sized(0.8f, 2.5f).clientTrackingRange(64).updateInterval(1).build("princess"));

		registerSpawnEgg(registry, HUNTER_HOUND, 10510648, 8934192);
		registerSpawnEgg(registry, SHOOTER_HUNTER, 12486764, 2690565);
		registerSpawnEgg(registry, SQUIRE_HUNTER, 12486764, 5318420);
		registerSpawnEgg(registry, PRINCESS, 16766495, 174864);
		registerSpawnEgg(registry, KNIGHT, 0, 0x510707);
		registerSpawnEgg(registry, PRINCE_ON_HORSE, 0xffdd1f, 0x2ab10);
		registerSpawnEgg(registry, PRINCESS_ON_HORSE, 0xffd61f, 0x2ab10);
	}

	private static void registerSpawnEgg(IForgeRegistry<Item> registry, EntityType entity, int eggPrimary, int eggSecondary){
		Item spawnEgg = new SpawnEggItem(entity, eggPrimary, eggSecondary, (new Item.Properties()).tab(DragonSurvivalMod.items));
		spawnEgg.setRegistryName(new ResourceLocation(DragonSurvivalMod.MODID, entity.getRegistryName().getPath() + "_spawn_egg"));
		registry.register(spawnEgg);
	}
}