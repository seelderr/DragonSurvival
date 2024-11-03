package by.dragonsurvivalteam.dragonsurvival.server;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.AmbusherEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.ModifyCustomSpawnersEvent;
import org.jetbrains.annotations.NotNull;

// This is mostly copied from PatrolSpawner.java
@EventBusSubscriber
public class AmbusherSpawner implements CustomSpawner {
    private int nextTick;

    @Override
    public int tick(@NotNull ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (!spawnEnemies || !level.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return 0;
        }

        nextTick--;

        if (nextTick > 0) {
            return 0;
        }

        // The random is used to vary the spawn rate a bit
        nextTick = nextTick + ServerConfig.ambusherSpawnAttemptFrequency + level.getRandom().nextInt(ServerConfig.ambusherSpawnAttemptFrequency / 10);

        if (!level.isDay() || level.getRandom().nextDouble() < ServerConfig.ambusherSpawnChance) {
            return 0;
        }

        if (level.players().isEmpty()) {
            return 0;
        }

        Player player = level.players().get(level.getRandom().nextInt(level.players().size()));

        if (player.isCreative() || player.isSpectator()) {
            return 0;
        }

        int x = (24 + level.getRandom().nextInt(24)) * (level.getRandom().nextBoolean() ? -1 : 1);
        int z = (24 + level.getRandom().nextInt(24)) * (level.getRandom().nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos spawnPosition = player.blockPosition().mutable().move(x, 0, z);

        if (!level.hasChunksAt(spawnPosition.getX() - 10, spawnPosition.getZ() - 10, spawnPosition.getX() + 10, spawnPosition.getZ() + 10)) {
            return 0;
        }

        if (level.getBiome(spawnPosition).is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
            return 0;
        }

        int membersSpawned = 0; // Doesn't seem to be used from the caller
        int difficulty = (int) Math.ceil(level.getCurrentDifficultyAt(spawnPosition).getEffectiveDifficulty()) + 1;

        // Spawns members depending on the calculated difficulty
        for (int i = 0; i < difficulty; i++) {
            spawnPosition.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPosition).getY());
            membersSpawned++;

            if (i == 0) { // Spawn the leader at the end
                if (!this.spawnPatrolMember(level, spawnPosition, level.getRandom(), true)) {
                    break;
                }
            } else {
                this.spawnPatrolMember(level, spawnPosition, level.getRandom(), false);
            }

            spawnPosition.setX(spawnPosition.getX() + level.getRandom().nextInt(5) - level.getRandom().nextInt(5));
            spawnPosition.setZ(spawnPosition.getZ() + level.getRandom().nextInt(5) - level.getRandom().nextInt(5));
        }

        return membersSpawned;
    }

    private boolean spawnPatrolMember(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, boolean isLeader) {
        if (!isLeader) {
            return false;
        }

        BlockState blockstate = pLevel.getBlockState(pPos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(pLevel, pPos, blockstate, blockstate.getFluidState(), DSEntities.HUNTER_AMBUSHER.get())) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, pLevel, MobSpawnType.PATROL, pPos, pRandom)) {
            return false;
        } else {
            AmbusherEntity ambusherEntity = DSEntities.HUNTER_AMBUSHER.get().create(pLevel);
            if (ambusherEntity == null) {
                return false;
            }

            ambusherEntity.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
            EventHooks.finalizeMobSpawn(ambusherEntity, pLevel, pLevel.getCurrentDifficultyAt(pPos), MobSpawnType.PATROL, null);
            pLevel.addFreshEntityWithPassengers(ambusherEntity);

            return true;
        }
    }

    @SubscribeEvent
    public static void addCustomSpawners(final ModifyCustomSpawnersEvent event) {
        event.addCustomSpawner(new AmbusherSpawner());
    }
}
