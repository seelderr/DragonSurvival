package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ManaCost;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncStopCast;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
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
        if (dragon.isCreative()) {
            cooldown = NO_COOLDOWN;
        } else {
            cooldown = Math.max(NO_COOLDOWN, cooldown - 1);
        }

        if (isActive && canBeCast()) {
            tickActions(dragon);
        }
    }

    public void tickActions(final Player dragon) {
        if (currentTick == 0) {
            // TODO :: does sound need to be played on both sides? would it overlap?
            //  currently at least this one would play on both
            value().activation().playStartSound(dragon);
        }

        currentTick++;

        if (!(dragon instanceof ServerPlayer serverPlayer)) {
            // TODO :: should mana also be consumed client-side?
            //  maybe we can skip the sync that way?
            return;
        }

        int castTime = getCastTime();

        if (currentTick < castTime) {
            value().activation().playChargingSound(dragon);
            return;
        }

        if (currentTick == castTime) {
            ManaHandler.consumeMana(serverPlayer, getInitialManaCost());
        }

        if (currentTick > castTime) {
            float manaCost = getContinuousManaCost(ManaCost.Type.TICKING);

            if (ManaHandler.hasEnoughMana(serverPlayer, manaCost)) {
                // TODO :: make this return a boolean and remove 'hasEnoughMana'?
                ManaHandler.consumeMana(serverPlayer, manaCost);
            } else {
                stopCasting(serverPlayer);
                return;
            }
        }

        value().activation().playLoopingSound(dragon);
        ability.value().actions().forEach(action -> action.tick(serverPlayer, this, currentTick));

        if (value().activation().type() == Activation.Type.ACTIVE_SIMPLE) {
            stopCasting(serverPlayer);
        }
    }

    private void stopCasting(final ServerPlayer dragon) {
        value().activation().playEndSound(dragon);
        release(dragon);

        MagicData magic = MagicData.getData(dragon);
        magic.stopCasting(dragon);
        // TODO: We can send back the reason we failed here to the client
        PacketDistributor.sendToPlayer(dragon, new SyncStopCast(dragon.getId(), false));
    }

    private float getInitialManaCost() {
        return value().activation().initialManaCost().map(cost -> cost.calculate(level)).orElse(0f);
    }

    public float getContinuousManaCost(final ManaCost.Type type) {
        Optional<ManaCost> optional = value().activation().continuousManaCost();

        if (optional.isEmpty()) {
            return 0;
        }

        ManaCost manaCost = optional.get();

        if (manaCost.type() != type) {
            return 0;
        }

        return manaCost.manaCost().calculate(level);
    }

    public boolean checkInitialManaCost(final Player dragon) {
        float manaCost = getInitialManaCost();

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

    public boolean isApplyingEffects() {
        return isActive && canBeCast() && currentTick >= getCastTime();
    }

    public boolean canBeCast() {
        return isEnabled && cooldown == NO_COOLDOWN;
    }

    public ResourceLocation getIcon() {
        return ability.value().icon().get(level);
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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
    //  technically no since it just adds 1 per tick while the ability is active
    //  this can be done on both sides
    public int getCurrentCastTime() {
        return currentTick;
    }

    // TODO: These need to be synced in some way for MagicHUD?
    //  should not be needed, since the client has info about the level and would reach the same value as the server here
    public int getCastTime() {
        return value().activation().castTime().map(time -> time.calculate(level)).orElse(0f).intValue();
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
