package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class TreasureRestData implements INBTSerializable<CompoundTag> {
    public boolean treasureResting;
    public int treasureRestTimer;
    public int treasureSleepTimer;

    public static TreasureRestData getData(Player player) {
        return player.getData(DSDataAttachments.TREASURE_REST);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("resting", treasureResting);
        tag.putInt("restingTimer", treasureRestTimer);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        treasureResting = nbt.getBoolean("resting");
        treasureRestTimer = nbt.getInt("restingTimer");
    }
}
