package by.dragonsurvivalteam.dragonsurvival.server;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.AmbusherEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.ModifyCustomSpawnersEvent;

// This is mostly copied from PatrolSpawner.java
@EventBusSubscriber
public class AmbusherSpawner implements CustomSpawner {

    private int nextTick;

    @Override
    public int tick(ServerLevel pLevel, boolean pSpawnEnemies, boolean pSpawnFriendlies) {
        if (!pSpawnEnemies) {
            return 0;
        } else if (!pLevel.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return 0;
        } else {
            RandomSource randomsource = pLevel.random;
            this.nextTick--;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick = this.nextTick + ServerConfig.ambusherSpawnAttemptFrequency + randomsource.nextInt(ServerConfig.ambusherSpawnAttemptFrequency / 10);
                if (!pLevel.isDay()) {
                    return 0;
                } else if (randomsource.nextDouble() < ServerConfig.ambusherSpawnChance) {
                    return 0;
                } else {
                    int j = pLevel.players().size();
                    if (j < 1) {
                        return 0;
                    } else {
                        Player player = pLevel.players().get(randomsource.nextInt(j));
                        if (player.isSpectator()) {
                            return 0;
                        } else {
                            int k = (24 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                            int l = (24 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutableblockpos = player.blockPosition().mutable().move(k, 0, l);
                            int i1 = 10;
                            if (!pLevel.hasChunksAt(
                                    blockpos$mutableblockpos.getX() - 10,
                                    blockpos$mutableblockpos.getZ() - 10,
                                    blockpos$mutableblockpos.getX() + 10,
                                    blockpos$mutableblockpos.getZ() + 10
                            )) {
                                return 0;
                            } else {
                                Holder<Biome> holder = pLevel.getBiome(blockpos$mutableblockpos);
                                if (holder.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                    return 0;
                                } else {
                                    int j1 = 0;
                                    int k1 = (int)Math.ceil((double)pLevel.getCurrentDifficultyAt(blockpos$mutableblockpos).getEffectiveDifficulty()) + 1;

                                    for (int l1 = 0; l1 < k1; l1++) {
                                        j1++;
                                        blockpos$mutableblockpos.setY(
                                                pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY()
                                        );
                                        if (l1 == 0) {
                                            if (!this.spawnPatrolMember(pLevel, blockpos$mutableblockpos, randomsource, true)) {
                                                break;
                                            }
                                        } else {
                                            this.spawnPatrolMember(pLevel, blockpos$mutableblockpos, randomsource, false);
                                        }

                                        blockpos$mutableblockpos.setX(blockpos$mutableblockpos.getX() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                        blockpos$mutableblockpos.setZ(blockpos$mutableblockpos.getZ() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                    }

                                    return j1;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean spawnPatrolMember(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, boolean pLeader) {
        if(!pLeader) {
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
