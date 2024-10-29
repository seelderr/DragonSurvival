package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.LightInDarknessAbility;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.neoforged.neoforge.network.PacketDistributor;

public class ForestDragonType extends AbstractDragonType{
	public static ResourceLocation FOREST_FOOD = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/forest_food_icons.png");
	public static ResourceLocation FOREST_MANA = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/forest_magic_icons.png");

	public int timeInDarkness;

    public ForestDragonType() {
        slotForBonus = 2;
    }

    @Override
    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("timeInDarkness", timeInDarkness);
        return tag;
    }

    @Override
    public void readNBT(CompoundTag base) {
        timeInDarkness = base.getInt("timeInDarkness");
    }

    @Override
    public void onPlayerUpdate(Player player, DragonStateHandler dragonStateHandler) {
        Level world = player.level();

        int maxStressTicks = ServerConfig.forestStressTicks;
        LightInDarknessAbility lightInDarkness = DragonAbilities.getSelfAbility(player, LightInDarknessAbility.class);

        if (lightInDarkness != null) {
            maxStressTicks += Functions.secondsToTicks(lightInDarkness.getDuration());
        }

        double oldDarknessTime = timeInDarkness;

        if (ServerConfig.penaltiesEnabled && !player.hasEffect(DSEffects.MAGIC)
                && ServerConfig.forestStressTicks > 0
                && !player.isCreative() &&
                !player.isSpectator()) {
            if (!world.isClientSide()) {
                LevelLightEngine lightManager = world.getChunkSource().getLightEngine();
                if (lightManager.getLayerListener(LightLayer.BLOCK).getLightValue(player.blockPosition()) < 3 && lightManager.getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition()) < 3 && lightManager.getLayerListener(LightLayer.SKY).getLightValue(
                        player.blockPosition().above()) < 3) {
                    if (timeInDarkness < maxStressTicks) {
                        timeInDarkness++;
                    }

                } else {
                    timeInDarkness = Math.max(timeInDarkness - (int) Math.ceil(maxStressTicks * 0.02F), 0);
                }

                timeInDarkness = Math.min(timeInDarkness, maxStressTicks);

                if (timeInDarkness >= maxStressTicks && player.tickCount % 21 == 0) {
                    player.addEffect(new MobEffectInstance(DSEffects.STRESS, Functions.secondsToTicks(ServerConfig.forestStressEffectDuration)));
                }


                if (timeInDarkness != oldDarknessTime) {
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonType.Data(player.getId(), dragonStateHandler.getType().writeNBT()));
                }
            }

            if (world.isClientSide() && !player.isCreative() && !player.isSpectator()) {
                if (!player.hasEffect(DSEffects.MAGIC) && timeInDarkness == ServerConfig.forestStressTicks) {
                    world.addParticle(ParticleTypes.SMOKE, player.getX() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + world.random.nextDouble() * (world.random.nextBoolean() ? 1 : -1), 0, 0, 0);
                }
            }
        }
    }

    @Override
    public boolean isInManaCondition(Player player, DragonStateHandler cap) {
        if (player.level().canSeeSky(player.blockPosition())) {
            int light = player.level().getBrightness(LightLayer.SKY, player.blockPosition()) - player.level().getSkyDarken();
            float f = player.level().getSunAngle(1.0F);

            float f1 = f < (float) Math.PI ? 0.0F : (float) Math.PI * 2F;
            f = f + (f1 - f) * 0.2F;
            light = Math.round((float) light * Mth.cos(f));
            light = Mth.clamp(light, 0, 15);

            if (light >= 10) {
                return true;
            }
        }

        return player.hasEffect(DSEffects.DRAIN) || player.hasEffect(DSEffects.MAGIC);
    }

    @Override
    public void onPlayerDeath() {
        timeInDarkness = 0;
    }

    @Override
    public List<Pair<ItemStack, FoodData>> validFoods(Player player, DragonStateHandler handler) {
        return null;
    }

	@Override
	public ResourceLocation getFoodIcons() {
		return FOREST_FOOD;
	}

	@Override
	public ResourceLocation getManaIcons() {
		return FOREST_MANA;
	}

	@Override
	public String getTypeName(){
		return "forest";
	}

    @Override
    public List<TagKey<Block>> mineableBlocks() {
        return List.of(BlockTags.MINEABLE_WITH_AXE);
    }
}