package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class TreasureRestData implements INBTSerializable<CompoundTag> {
    public static final String IS_RESTING = "is_resting";
    public static final String RESTING_TICKS = "resting_ticks";

    public static final int TICKS_TO_SLEEP = 100;

    public boolean isResting;
    public int restingTicks;
    public int sleepingTicks;

    public boolean canSleep() {
        return isResting && restingTicks >= TICKS_TO_SLEEP;
    }

    public static TreasureRestData getData(final Player player) {
        return player.getData(DSDataAttachments.TREASURE_REST);
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(IS_RESTING, isResting);
        tag.putInt(RESTING_TICKS, restingTicks);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        isResting = tag.getBoolean("resting");
        restingTicks = tag.getInt("restingTimer");
    }
}
