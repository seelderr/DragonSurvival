package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DragonAbilityInstance {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 255;
    public static final int NO_COOLDOWN = 0;

    public static Codec<DragonAbilityInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DragonAbility.CODEC.fieldOf("ability").forGetter(DragonAbilityInstance::ability),
            Codec.INT.fieldOf("level").forGetter(DragonAbilityInstance::level),
            Codec.INT.fieldOf("slot").forGetter(DragonAbilityInstance::slot),
            Codec.BOOL.fieldOf("is_enabled").forGetter(DragonAbilityInstance::isEnabled)
    ).apply(instance, DragonAbilityInstance::new));

    private final Holder<DragonAbility> ability;
    private int level;
    private int slot;
    private boolean isEnabled;

    // TODO :: values which will not be saved
    private boolean isActive;
    private int currentTick;
    private int cooldown;

    public DragonAbilityInstance(final Holder<DragonAbility> ability, int level, int slot) {
        this(ability, level, slot, true);
    }

    public DragonAbilityInstance(final Holder<DragonAbility> ability, int level, int slot, boolean isEnabled) {
        this.ability = ability;
        this.level = level;
        this.slot = slot;
        this.isEnabled = isEnabled;
    }

    public Tag save(@NotNull final HolderLookup.Provider provider) {
        return CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static @Nullable DragonAbilityInstance load(@NotNull final HolderLookup.Provider provider, final CompoundTag nbt) {
        return CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
    }

    public void tick(final Player dragon) {
        cooldown = Math.max(NO_COOLDOWN, cooldown - 1);

        if (isActive && canBeCast()) {
            apply(dragon);
        }
    }

    public void apply(final Player dragon) {
        int castTime = getCastTime();
        currentTick++;

        if (currentTick < castTime) {
            return;
        }

        if (currentTick == castTime) {
            ManaHandler.consumeMana(dragon, getInitialManaCost());
        }

        if (dragon instanceof ServerPlayer serverPlayer) {
            ability.value().effects().forEach(effect -> effect.tick(serverPlayer, this, currentTick));
        }
    }

    private int getInitialManaCost() {
        return ability.value().activation().map(
                activation -> activation.initialManaCost().map(cost -> cost.calculate(level())).orElse(0f)
        ).orElse(0f).intValue(); // TODO :: use floatValue once mana has decimals
    }

    public boolean checkInitialManaCost(final Player dragon) {
        int manaCost = getInitialManaCost();

        if (!ManaHandler.hasEnoughMana(dragon, manaCost)) {
            releaseWithoutCooldown();

            if (dragon.level().isClientSide()) {
                MagicData magicData = MagicData.getData(dragon);
                magicData.setErrorMessageSent(true);
                MagicHUD.castingError(Component.translatable(MagicHUD.NO_MANA).withStyle(ChatFormatting.RED));
            }

            return false;
        }

        return true;
    }

    public ResourceLocation getIcon() {
        return ability.value().icon().get(level);
    }

    public boolean isApplyingEffects() {
        return isActive && canBeCast() && currentTick >= getCastTime();
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean canBeCast() {
        return isEnabled && cooldown == NO_COOLDOWN;
    }

    public boolean canBeUsed(final Player dragon) {
        return canBeCast() && checkInitialManaCost(dragon);
    }

    // Used for when a client was denied from casting an ability by the server
    public void releaseWithoutCooldown() {
        currentTick = 0;
    }

    public void release(final Player dragon) {
        currentTick = 0;

        if (dragon.isCreative()) {
            cooldown = NO_COOLDOWN;
        } else {
            cooldown = ability.value().getCooldown(level);
        }
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    // TODO: These need to be synced in some way for MagicHUD?
    public int getCurrentCastTime() {
        return currentTick;
    }

    // TODO: These need to be synced in some way for MagicHUD?
    public int getCastTime() {
        return ability.value().activation().map(
                activation -> activation.castTime().map(time -> time.calculate(level())).orElse(0f)
        ).orElse(0f).intValue();
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    public DragonAbility value() {
        return ability.value();
    }

    public ResourceKey<DragonAbility> key() {
        return ability.getKey();
    }

    public ResourceLocation location() {
        return key().location();
    }

    public String id() {
        return location().toString();
    }

    public Holder<DragonAbility> ability() {
        return ability;
    }

    public int level() {
        return level;
    }

    public int slot() {
        return slot;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
