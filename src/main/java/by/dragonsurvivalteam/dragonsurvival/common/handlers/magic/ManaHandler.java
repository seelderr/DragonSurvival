package by.dragonsurvivalteam.dragonsurvival.common.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.CaveMagicAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.ForestMagicAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.SeaMagicAbility;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncMagicStats;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

import static by.dragonsurvivalteam.dragonsurvival.misc.DragonType.CAVE;
import static by.dragonsurvivalteam.dragonsurvival.misc.DragonType.SEA;

@EventBusSubscriber
public class ManaHandler{
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		if(event.phase == Phase.START){
			return;
		}

		Player player = event.player;

		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(cap.getMagic().getCurrentlyCasting() != null){
				return;
			}

			boolean goodConditions = ManaHandler.isPlayerInGoodConditions(player);

			int timeToRecover = goodConditions ? ServerConfig.favorableManaTicks : ServerConfig.normalManaTicks;

			if(player.hasEffect(DragonEffects.SOURCE_OF_MAGIC)){
				timeToRecover = 1;
			}

			if(player.tickCount % Functions.secondsToTicks(timeToRecover) == 0){
				if(cap.getMagic().getCurrentMana() < getMaxMana(player)){
					replenishMana(player, 1);
				}
			}
		});
	}

	public static boolean isPlayerInGoodConditions(Player player){
		if(!DragonUtils.isDragon(player)){
			return false;
		}

		BlockState blockBelow = player.level.getBlockState(player.blockPosition().below());
		BlockState feetBlock = player.getFeetBlockState();

		if(feetBlock.getBlock() instanceof TreasureBlock || blockBelow.getBlock() instanceof TreasureBlock){
			return true;
		}

		if(player.hasEffect(DragonEffects.SOURCE_OF_MAGIC)){
			return true;
		}

		return DragonStateProvider.getCap(player).map(cap -> {

			if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(cap.getType())){
				if(DragonConfigHandler.DRAGON_MANA_BLOCKS.get(cap.getType()).contains(blockBelow.getBlock()) || DragonConfigHandler.DRAGON_MANA_BLOCKS.get(cap.getType()).contains(feetBlock.getBlock())){
					if(!(blockBelow.getBlock() instanceof AbstractFurnaceBlock) && !(feetBlock.getBlock() instanceof AbstractFurnaceBlock) && !(blockBelow.getBlock() instanceof CauldronBlock) && !(feetBlock.getBlock() instanceof CauldronBlock)){
						return true;
					}
				}
			}

			switch(cap.getType()){
				case SEA:
					if(player.isInWaterRainOrBubble() || player.hasEffect(DragonEffects.CHARGED) || player.hasEffect(DragonEffects.PEACE)){
						return true;
					}
					if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(SEA)){
						if(DragonConfigHandler.DRAGON_MANA_BLOCKS.get(SEA).contains(blockBelow.getBlock())){
							if(blockBelow.getBlock() instanceof LayeredCauldronBlock){
								if(blockBelow.hasProperty(LayeredCauldronBlock.LEVEL)){
									int level = blockBelow.getValue(LayeredCauldronBlock.LEVEL);

									if(level > 0){
										return true;
									}
								}
							}

							if(feetBlock.getBlock() instanceof LayeredCauldronBlock){
								if(feetBlock.hasProperty(LayeredCauldronBlock.LEVEL)){
									int level = feetBlock.getValue(LayeredCauldronBlock.LEVEL);

									if(level > 0){
										return true;
									}
								}
							}
						}
					}

					break;

				case FOREST:
					if(player.level.canSeeSky(player.blockPosition())){
						int light = player.level.getBrightness(LightLayer.SKY, player.blockPosition()) - player.level.getSkyDarken();
						float f = player.level.getSunAngle(1.0F);

						float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
						f = f + (f1 - f) * 0.2F;
						light = Math.round((float)light * Mth.cos(f));
						light = Mth.clamp(light, 0, 15);

						if(light >= 14){
							return true;
						}
					}

					if(player.hasEffect(DragonEffects.DRAIN) || player.hasEffect(DragonEffects.MAGIC)){
						return true;
					}

					break;

				case CAVE:
					if(player.isInLava() || player.isOnFire() || player.hasEffect(DragonEffects.BURN) || player.hasEffect(DragonEffects.FIRE)){
						return true;
					}

					//If cave dragon is ontop of a burning furnace
					if(DragonConfigHandler.DRAGON_MANA_BLOCKS != null && DragonConfigHandler.DRAGON_MANA_BLOCKS.containsKey(CAVE)){
						if(DragonConfigHandler.DRAGON_MANA_BLOCKS.get(CAVE).contains(blockBelow.getBlock())){
							if(blockBelow.getBlock() instanceof AbstractFurnaceBlock){
								if(blockBelow.hasProperty(AbstractFurnaceBlock.LIT)){
									if(blockBelow.getValue(AbstractFurnaceBlock.LIT)){
										return true;
									}
								}
							}
						}
					}

					break;
			}

			return false;
		}).orElse(false);
	}

	public static int getMaxMana(Player entity){
		return DragonStateProvider.getCap(entity).map(cap -> {
			int mana = 1;

			mana += ServerConfig.noEXPRequirements ? 9 : Math.max(0, (Math.min(50, entity.experienceLevel) - 5) / 5);

			switch(cap.getType()){
				case SEA -> mana += DragonAbilities.getAbility(entity, SeaMagicAbility.class).getLevel();
				case CAVE -> mana += DragonAbilities.getAbility(entity, CaveMagicAbility.class).getLevel();
				case FOREST -> mana += DragonAbilities.getAbility(entity, ForestMagicAbility.class).getLevel();
			}

			return mana;
		}).orElse(0);
	}

	public static void replenishMana(Player entity, int mana){
		if(entity.level.isClientSide){
			return;
		}

		DragonStateProvider.getCap(entity).ifPresent(cap -> {
			cap.getMagic().setCurrentMana(Math.min(getMaxMana(entity), cap.getMagic().getCurrentMana() + mana));
			NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)entity), new SyncMagicStats(entity.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().isRenderAbilities()));
		});
	}

	public static void consumeMana(Player entity, int mana){
		if(entity == null || entity.isCreative() || entity.hasEffect(DragonEffects.SOURCE_OF_MAGIC))
			return;

		if(ServerConfig.consumeEXPAsMana){
			if(entity.level.isClientSide){
				if(getCurrentMana(entity) < mana && (getCurrentMana(entity) + entity.totalExperience / 10 >= mana || entity.experienceLevel > 0)){
					entity.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.01F, 0.01F);
				}
			}
		}

		if(entity.level.isClientSide){
			return;
		}

		DragonStateProvider.getCap(entity).ifPresent(cap -> {
			if(ServerConfig.consumeEXPAsMana){
				if(getCurrentMana(entity) < mana && (getCurrentMana(entity) + (entity.totalExperience / 10) >= mana || entity.experienceLevel > 0)){
					int missingMana = mana - getCurrentMana(entity);
					int missingExp = (missingMana * 10);
					entity.giveExperiencePoints(-missingExp);
					cap.getMagic().setCurrentMana(0);
				}else{
					cap.getMagic().setCurrentMana(Math.max(0, cap.getMagic().getCurrentMana() - mana));
				}
			}else{
				cap.getMagic().setCurrentMana(Math.max(0, cap.getMagic().getCurrentMana() - mana));
			}

			NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)entity), new SyncMagicStats(entity.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().isRenderAbilities()));
		});
	}

	public static int getCurrentMana(Player entity){
		return DragonStateProvider.getCap(entity).map(cap -> Math.min(cap.getMagic().getCurrentMana(), getMaxMana(entity))).orElse(0);
	}
}