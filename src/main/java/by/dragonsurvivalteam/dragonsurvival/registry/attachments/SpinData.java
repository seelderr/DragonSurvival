package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class SpinData implements INBTSerializable<CompoundTag> {
    public boolean spinLearned;
    public int spinCooldown;
    public int spinAttack;

    public static SpinData getData(Entity entity) {
        return entity.getData(DSDataAttachments.SPIN);
    }

    // TODO: ServerConfig.saveAllAbilities made this data not get saved if you weren't a dragon. How to handle this here?
    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag data = new CompoundTag();
        data.putBoolean("spinLearned", spinLearned);
        data.putInt("spinCooldown", spinCooldown);
        data.putInt("spinAttack", spinAttack);
        return data;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        spinLearned = nbt.getBoolean("spinLearned");
        spinCooldown = nbt.getInt("spinCooldown");
        spinAttack = nbt.getInt("spinAttack");
    }
}
