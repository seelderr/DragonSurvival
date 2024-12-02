package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class SpinData implements INBTSerializable<CompoundTag> {
    public static final String HAS_SPIN = "has_spin";
    public static final String COOLDOWN = "cooldown";
    public static final String DURATION = "duration";

    public boolean hasSpin;
    public int cooldown;
    public int duration;

    public static SpinData getData(final Player player) {
        return player.getData(DSDataAttachments.SPIN);
    }

    // TODO: ServerConfig.saveAllAbilities made this data not get saved if you weren't a dragon. How to handle this here?
    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(HAS_SPIN, hasSpin);
        tag.putInt(COOLDOWN, cooldown);
        tag.putInt(DURATION, duration);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        hasSpin = tag.getBoolean(HAS_SPIN);
        cooldown = tag.getInt(COOLDOWN);
        duration = tag.getInt(DURATION);
    }
}
