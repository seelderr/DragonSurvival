package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class AltarData implements INBTSerializable<CompoundTag> {
    public int altarCooldown;
    public boolean hasUsedAltar;
    public boolean isInAltar;

    public static AltarData getData(Player player) {
        return player.getData(DSDataAttachments.ALTAR);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("altarCooldown", altarCooldown);
        nbt.putBoolean("hasUsedAltar", hasUsedAltar);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        altarCooldown = nbt.getInt("altarCooldown");
        hasUsedAltar = nbt.getBoolean("hasUsedAltar");
    }
}
