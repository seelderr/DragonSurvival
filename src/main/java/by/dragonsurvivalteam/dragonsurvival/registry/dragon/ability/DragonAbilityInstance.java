package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class DragonAbilityInstance implements INBTSerializable<CompoundTag> {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 255;

    private Holder<DragonAbility> ability;
    private int level;
    private boolean isEnabled = true;
    private int abilitySlot;

    // TODO :: values which will not be saved
    private int currentTick;
    public int cooldown;

    public DragonAbilityInstance() {}

    public DragonAbilityInstance(final Holder<DragonAbility> ability, int level, int slot) {
        this.ability = ability;
        this.level = level;
        this.abilitySlot = slot;
        this.isEnabled = ability.value().type() == DragonAbility.Type.PASSIVE;
    }

    public void applyClient() {
        cooldown = Math.max(0, cooldown - 1);
    }

    public void apply(final ServerPlayer dragon) {
        cooldown = Math.max(0, cooldown - 1);
        if (!isActive()) {
            return;
        }

        currentTick++;
        if(currentTick >= getCastTime()) {
            ability.value().effects().forEach(effect -> effect.tick(dragon, this, currentTick));
        }
    }

    public ResourceLocation getIcon() {
        return ability.value().icon().get(level);
    }

    public boolean isInCooldown() {
        return cooldown > 0;
    }

    public boolean isActive() {
        return isEnabled && cooldown <= 0;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    // Used for when a client was denied from casting an ability by the server
    public void releaseWithoutCooldown() {
        currentTick = 0;
        isEnabled = false;
    }

    // TODO :: called when the pressed key is released (+ can also call this when the ability is disabled (relevant for passive only))
    public void release() {
        currentTick = 0;
        isEnabled = false;
        cooldown = ability.value().getCooldown(level);
    }

    public DragonAbility getAbility() {
        return ability.value();
    }

    public ResourceKey<DragonAbility> getAbilityKey() {
        return ability.getKey();
    }

    public int getLevel() {
        return level;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    public int getSlot() {
        return abilitySlot;
    }

    // TODO: These need to be synced in some way for MagicHUD?
    public int getCurrentCastTime() {
        return currentTick;
    }

    // TODO: These need to be synced in some way for MagicHUD?
    public int getCastTime() {
        if(ability.value().activation().isPresent()) {
            if(ability.value().activation().get().castTime().isPresent()) {
                return (int) ability.value().activation().get().castTime().get().calculate(level);
            }
        }

        return 0;
    }

    // TODO :: could also handle this through a CODEC defined for the instance (like MobEffectInstance)
    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(LEVEL, level);
        // Actives are never enabled by default
        tag.putBoolean(ENABLED, ability.value().type() == DragonAbility.Type.PASSIVE && isEnabled);
        tag.put(ABILITY, ResourceKey.codec(DragonAbility.REGISTRY).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), ability.getKey()).getOrThrow());
        tag.putInt(ABILITY_SLOT, abilitySlot);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        this.level = tag.getInt(LEVEL);
        this.isEnabled = tag.getBoolean(ENABLED);
        this.ability = provider.lookup(DragonAbility.REGISTRY).get().getOrThrow(ResourceKey.codec(DragonAbility.REGISTRY).parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(ABILITY)).getOrThrow());
        this.abilitySlot = tag.getInt(ABILITY_SLOT);
    }

    private final String LEVEL = "level";
    private final String ENABLED = "enabled";
    private final String ABILITY = "ability";
    private final String ABILITY_SLOT = "ability_slot";
}
