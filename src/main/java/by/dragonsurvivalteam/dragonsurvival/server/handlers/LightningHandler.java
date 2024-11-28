package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

@EventBusSubscriber
public class LightningHandler implements INBTSerializable<CompoundTag> {
    public boolean ignoresItemsAndExperience;
    public boolean spawnsFire;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ignores_items_and_experience", ignoresItemsAndExperience);
        tag.putBoolean("spawns_fire", spawnsFire);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        ignoresItemsAndExperience = nbt.getBoolean("ignores_items_and_experience");
        spawnsFire = nbt.getBoolean("spawns_fire");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void handleLightningBolt(final EntityStruckByLightningEvent event) {
        LightningBolt bolt = event.getLightning();
        Entity target = event.getEntity();
        LightningHandler handler = bolt.getData(DSDataAttachments.LIGHTNING_BOLT_DATA);

        if (handler.ignoresItemsAndExperience) {
            if (target instanceof ItemEntity || target instanceof ExperienceOrb) {
                event.setCanceled(true);
            }
        }
    }
}
