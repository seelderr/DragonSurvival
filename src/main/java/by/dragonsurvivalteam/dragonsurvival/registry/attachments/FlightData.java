package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class FlightData implements INBTSerializable<CompoundTag> {
    public static final String COOLDOWN = "cooldown";
    public static final String DURATION = "duration";

    public Holder<FluidType> swimSpinFluid;
    public boolean hasFlight;
    public boolean hasSpin;
    public boolean areWingsSpread;

    // Data that actually needs to be saved
    public int cooldown;
    public int duration;

    public static FlightData getData(final Player player) {
        return player.getData(DSDataAttachments.SPIN);
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(COOLDOWN, cooldown);
        tag.putInt(DURATION, duration);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        cooldown = tag.getInt(COOLDOWN);
        duration = tag.getInt(DURATION);
    }

    public boolean hasFlight() {
        return hasFlight;
    }

    public boolean isWingsSpread() {
        return hasFlight && areWingsSpread;
    }
}
