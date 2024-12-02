package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class AltarData implements INBTSerializable<CompoundTag> {
    public static final String ALTAR_COOLDOWN = "altar_cooldown";
    public static final String HAS_USED_ALTAR = "has_used_altar";

    public int altarCooldown;
    public boolean hasUsedAltar;
    public boolean isInAltar;

    public static AltarData getData(Player player) {
        return player.getData(DSDataAttachments.ALTAR);
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(ALTAR_COOLDOWN, altarCooldown);
        tag.putBoolean(HAS_USED_ALTAR, hasUsedAltar);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        altarCooldown = tag.getInt(ALTAR_COOLDOWN);
        hasUsedAltar = tag.getBoolean(HAS_USED_ALTAR);
    }
}
