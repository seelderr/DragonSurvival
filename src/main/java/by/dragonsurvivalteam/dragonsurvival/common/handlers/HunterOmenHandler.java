package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.DragonHunter;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class HunterOmenHandler {
	// FIXME: This will fail if a player dies, then closes the game/server. The player will respawn without the effect.
	private static final Map<UUID, MobEffectInstance> playersToReapplyHunterOmen = new HashMap<>();

	private static boolean doesEntityApplyHunterOmen(Entity entity){
		String typeName = ResourceHelper.getKey(entity).toString();
		return ServerConfig.hunterOmenStatusGivers.contains(typeName) || entity instanceof DragonHunter || entity instanceof AbstractVillager;
	}

	// This used to apply hunter omen for the villagers when killed. Drops are now handled by VillagerLootModifier.
	@SubscribeEvent
	public static void applyHunterOmenOnMurderedEntities(LivingDeathEvent deathEvent){
		LivingEntity livingEntity = deathEvent.getEntity();
		Entity killer = deathEvent.getSource().getEntity();
		if(killer instanceof Player playerEntity){
			if(doesEntityApplyHunterOmen(livingEntity)){
				applyHunterOmenFromKilling(playerEntity);
			}
		}
	}

	public static boolean isNearbyPlayerWithHunterOmen(double detectionRadius, Level level, Entity entity) {
		List<Player> players = level.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(detectionRadius));

		for (Player player : players) {
			if (player.hasEffect(DSEffects.HUNTER_OMEN)) {
				return true;
			}
		}
		return false;
	}

	public static ObjectArrayList<ItemStack> getVillagerLoot(AbstractVillager villager, Level level, @Nullable Player player){
		List<ItemStack> nonEmeraldTrades = new ArrayList<>();
		villager.getOffers().stream().filter(merchantOffer -> merchantOffer.getResult().getItem() != Items.EMERALD).forEach(merchantOffer -> {
			// Be careful to copy the stack, since otherwise if we use this function when the villager is alive (when we steal from them)
			// we will modify the villager's trades and crash the game due to empty stacks.
			nonEmeraldTrades.add(merchantOffer.getResult().copy());
		});

		ObjectArrayList<ItemStack> loot = new ObjectArrayList<>();
		if(!nonEmeraldTrades.isEmpty()) {
			int lootingLevel = 0;
			if(player != null) {
				lootingLevel = EnchantmentUtils.getLevel(player.level(), Enchantments.LOOTING, player);
			}
			int numRolls = Math.min(lootingLevel + 1, nonEmeraldTrades.size());
			for(int i = 0; i < numRolls; i++) {
				int roll = level.getRandom().nextInt(nonEmeraldTrades.size());
				loot.add(nonEmeraldTrades.get(roll));
				nonEmeraldTrades.remove(roll);
			}
		}

		return loot;
	}

	// I want to use a loot modifier here, but it isn't possible since the villagers don't have an initial loot table to begin with.
	@SubscribeEvent
	public static void modifyDropsForVillagers(LivingDeathEvent deathEvent){
		Entity entity = deathEvent.getEntity();
		Entity killer = deathEvent.getSource().getEntity();
		if(killer instanceof Player player) {
			if(entity instanceof AbstractVillager abstractVillager) {
				ObjectArrayList<ItemStack> loot = getVillagerLoot(abstractVillager, player.level(), player);
				for(ItemStack stack : loot){
					entity.spawnAtLocation(stack);
				}

				int experience;
				if(entity instanceof Villager villager) {
					experience = (int) Math.pow(2.0, villager.getVillagerData().getLevel());
				} else {
					// This happens with the wandering trader in vanilla.
					experience = 4;
				}
				player.level().addFreshEntity(new ExperienceOrb(player.level(), entity.getX(), entity.getY() + 0.5, entity.getZ(), experience));
			}
		}
	}

	@SubscribeEvent
	public static void preserveHunterOmenOnRespawn(LivingDeathEvent deathEvent){
		LivingEntity livingEntity = deathEvent.getEntity();
		if(livingEntity instanceof Player playerEntity) {
			if(playerEntity.hasEffect(DSEffects.HUNTER_OMEN)){
				playersToReapplyHunterOmen.put(playerEntity.getUUID(), playerEntity.getEffect(DSEffects.HUNTER_OMEN));
			}
		}
	}

	@SubscribeEvent
	public static void reapplyHunterOmenOnRespawn(PlayerEvent.PlayerRespawnEvent respawnEvent){
		Player playerEntity = respawnEvent.getEntity();
		UUID playerUUID = playerEntity.getUUID();
		MobEffectInstance effectInstance = playersToReapplyHunterOmen.remove(playerUUID);
		if(effectInstance != null){
			playerEntity.addEffect(effectInstance);
		}
	}

	private static void applyHunterOmenFromKilling(Player playerEntity){
		int duration = 0;

		MobEffectInstance effectInstance = playerEntity.getEffect(DSEffects.HUNTER_OMEN);
		if(effectInstance != null){
			duration = effectInstance.getDuration();
		}

		// Double the duration unless it would add more than 30 minutes to the timer, but add a minimum of 1 minute
		playerEntity.addEffect(new MobEffectInstance(DSEffects.HUNTER_OMEN, Math.max(Functions.minutesToTicks(1), Math.min(duration * 2, Functions.minutesToTicks(30))), 0, false, false));
	}

	@SubscribeEvent
	public static void voidsHunterOmen(MobEffectEvent.Added potionAddedEvent){
		MobEffectInstance effectInstance = potionAddedEvent.getEffectInstance();
		LivingEntity livingEntity = potionAddedEvent.getEntity();
		if(effectInstance.getEffect() == MobEffects.HERO_OF_THE_VILLAGE){
			livingEntity.removeEffect(DSEffects.HUNTER_OMEN);
		}
	}

	@SubscribeEvent
	public static void ironGolemTargetsMarkedPlayers(EntityJoinLevelEvent joinWorldEvent){
		Entity entity = joinWorldEvent.getEntity();
		if(entity instanceof IronGolem golemEntity){
			golemEntity.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(golemEntity, Player.class, 0, true, false, livingEntity -> livingEntity.hasEffect(DSEffects.HUNTER_OMEN)));
		}
	}

	@SubscribeEvent
	public static void applyHunterOmenOnHurtEntities(LivingIncomingDamageEvent attackEntityEvent){
		Entity attacked = attackEntityEvent.getEntity();
		Player attacker = attackEntityEvent.getSource().getEntity() instanceof Player ? (Player)attackEntityEvent.getSource().getEntity() : null;

		if(attacker == null){
			return;
		}

		if(doesEntityApplyHunterOmen(attacked)) {
			int duration = 0;
			if(attacker.hasEffect(DSEffects.HUNTER_OMEN)) {
				duration = attacker.getEffect(DSEffects.HUNTER_OMEN).getDuration();
			}

			attacker.addEffect(new MobEffectInstance(DSEffects.HUNTER_OMEN, duration + Functions.secondsToTicks(5), 0, false, false));
		}
	}
}