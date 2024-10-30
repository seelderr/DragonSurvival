package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.CaveDragon.SmallFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.ForestDragon.SmallPoisonParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.SeaDragon.LargeLightningParticle;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChargeCastAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Arrays;
import java.util.Objects;

@EventBusSubscriber(Dist.CLIENT)
public class ClientMagicHandler {
    @ConfigOption(side = ConfigSide.CLIENT, category = "rendering", key = "particles_on_dragons", comment = "Particles (from the dragon type effects) will be rendered on dragons if this is enabled")
    public static Boolean particlesOnDragons = false;

    @SubscribeEvent
    public static void onFovEvent(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();

        DragonStateProvider.getOptional(player).ifPresent(cap -> {
            if (Arrays.stream(cap.getEmoteData().currentEmotes).anyMatch(Objects::nonNull) && DragonStateProvider.isDragon(player)) {
                event.setNewFovModifier(1f);
                return;
            }

            ActiveDragonAbility ability = cap.getMagicData().getCurrentlyCasting();

            if (ability instanceof ChargeCastAbility chargeCastAbility) {
                if (chargeCastAbility.getCastTime() > 0) {
                    double perc = Math.min(chargeCastAbility.getCastTime() / (float) chargeCastAbility.getSkillCastingTime(), 1) / 4;
                    double c4 = 2 * Math.PI / 3;

                    if (perc != 0 && perc != 1) {
                        perc = Math.pow(2, -10 * perc) * Math.sin((perc * 10 - 0.75) * c4) + 1;
                    }

                    float newFov = (float) Mth.clamp(perc, 1.0F, 1.0F);
                    event.setNewFovModifier(newFov);
                }
            }
        });
    }

    @SubscribeEvent
    public static void livingTick(final EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (!particlesOnDragons && DragonStateProvider.isDragon(livingEntity)) {
                return;
            }

            if (livingEntity.hasEffect(DSEffects.BURN)) {
                ParticleOptions data = new SmallFireParticle.Data(37F, false);
                for (int i = 0; i < 4; i++) {
                    renderEffectParticle(livingEntity, data);
                }
            }

            if (livingEntity.hasEffect(DSEffects.DRAIN)) {
                ParticleOptions data = new SmallPoisonParticle.Data(37F, false);
                for (int i = 0; i < 4; i++) {
                    renderEffectParticle(livingEntity, data);
                }
            }

            if (livingEntity.hasEffect(DSEffects.CHARGED)) {
                ParticleOptions data = new LargeLightningParticle.Data(37F, false);
                for (int i = 0; i < 4; i++) {
                    renderEffectParticle(livingEntity, data);
                }
            }
        }
    }

    public static void renderEffectParticle(final LivingEntity entity, final ParticleOptions particle) {
        Player localPlayer = DragonSurvival.PROXY.getLocalPlayer();

        if (localPlayer != null) {
            double d0 = (double) entity.getRandom().nextFloat() * entity.getBbWidth();
            double d1 = (double) entity.getRandom().nextFloat() * entity.getBbHeight();
            double d2 = (double) entity.getRandom().nextFloat() * entity.getBbWidth();
            double x = entity.getX() + d0 - entity.getBbWidth() / 2;
            double y = entity.getY() + d1;
            double z = entity.getZ() + d2 - entity.getBbWidth() / 2;

            localPlayer.level().addParticle(particle, x, y, z, 0, 0, 0);
        }
    }
}