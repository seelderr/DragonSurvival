package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncCooldownState;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class MagicData implements INBTSerializable<CompoundTag> {
    private List<DragonAbilityInstance> abilities = new ArrayList<>();
    private boolean renderAbilities = true;
    private int selectedAbilitySlot = 0;
    private float currentMana = 0;

    private boolean errorMessageSent = false;
    private boolean isCasting = false;
    private boolean castWasDenied = false;
    private int castTimer;

    public static MagicData getData(Player player) {
        return player.getData(DSDataAttachments.MAGIC);
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(float currentMana) {
        this.currentMana = Math.max(0, currentMana);
    }

    public void setSelectedAbilitySlot(int newSlot) {
        selectedAbilitySlot = newSlot;
    }

    public int getSelectedAbilitySlot() {
        return selectedAbilitySlot;
    }

    public int getClientCastTimer() {
        return castTimer;
    }

    @SubscribeEvent
    public static void tickAbilities(final PlayerTickEvent.Post event) {
        if (!DragonStateProvider.isDragon(event.getEntity())) {
            return;
        }

        Optional<MagicData> optional = event.getEntity().getExistingData(DSDataAttachments.MAGIC);

        if (optional.isEmpty()) {
            return;
        }

        MagicData magic = optional.get();

        for (DragonAbilityInstance instance : magic.abilities) {
            instance.tick(event.getEntity());
        }

        if (event.getEntity().level().isClientSide() && magic.isCasting()) {
            magic.castTimer = Math.max(0, magic.castTimer - 1);
        }
    }

    public @Nullable DragonAbilityInstance getAbilityFromSlot(int slot) {
        if (slot < 0 || slot >= abilities.size()) {
            return null;
        }

        for (DragonAbilityInstance ability : abilities) {
            if (ability.slot() == slot) {
                return ability;
            }
        }

        return null;
    }

    public @Nullable DragonAbilityInstance getCurrentlyCasting() {
        return isCasting ? getAbilityFromSlot(getSelectedAbilitySlot()) : null;
    }

    // TODO :: wait for some server response before showing the casting hud?
    //  otherwise it will flicker if the usage_blocked condition on the server prevents the cast
    public boolean attemptCast(int slot, Player player) {
        if (slot < 0 || slot >= abilities.size()) {
            return false;
        }

        DragonAbilityInstance instance = getAbilityFromSlot(slot);

        if (instance == null) {
            return false;
        }

        if (isCastBlocked(player, instance)) {
            int cooldown = instance.getCooldown();

            if (!errorMessageSent && player.level().isClientSide() && cooldown != DragonAbilityInstance.NO_COOLDOWN) {
                errorMessageSent = true;
                MagicHUD.castingError(Component.translatable(MagicHUD.COOLDOWN, NumberFormat.getInstance().format(Functions.ticksToSeconds(cooldown)) + "s").withStyle(ChatFormatting.RED));
            }

            return false;
        }

        DragonAbilityInstance currentlyCasting = getCurrentlyCasting();

        if (currentlyCasting != null) {
            currentlyCasting.release(player);
        }

        setSelectedAbilitySlot(slot);
        beginCasting();

        return true;
    }

    public void denyCast() {
        isCasting = false;
        castWasDenied = true;

        DragonAbilityInstance currentlyCasting = getCurrentlyCasting();

        if (currentlyCasting != null) {
            currentlyCasting.releaseWithoutCooldown();
        }
    }

    public void stopCasting(final Player player) {
        DragonAbilityInstance currentlyCasting = getCurrentlyCasting();

        if (currentlyCasting != null) {
            if (currentlyCasting.isApplyingEffects()) {
                currentlyCasting.release(player);
            } else {
                currentlyCasting.releaseWithoutCooldown();
            }

            currentlyCasting.setActive(false);

            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncCooldownState(player.getId(), getSelectedAbilitySlot(), currentlyCasting.getCooldown()));
            }
        }

        isCasting = false;
    }

    public void setClientCooldown(int slot, int cooldown) {
        DragonAbilityInstance ability = getAbilityFromSlot(slot);

        if (ability != null) {
            ability.setCooldown(cooldown);
            ability.setActive(false);
        }
    }

    public void setCastWasDenied(boolean castWasDenied) {
        this.castWasDenied = castWasDenied;
    }

    public void setErrorMessageSent(boolean errorMessageSent) {
        this.errorMessageSent = errorMessageSent;
    }

    private void beginCasting() {
        DragonAbilityInstance instance = getAbilityFromSlot(getSelectedAbilitySlot());

        if (instance == null) {
            return;
        }

        isCasting = true;
        castTimer = instance.value().getChargeTime(instance.level());
        instance.setActive(true);
    }

    public boolean isCasting() {
        return isCasting && getCurrentlyCasting() != null;
    }

    // TODO :: return a blocked_type here? Like 'cooldown' 'mana_cost' etc.?
    private boolean isCastBlocked(final Player dragon, final DragonAbilityInstance instance) {
        boolean canBeUsed = instance.canBeCast() && instance.checkInitialManaCost(dragon);

        if (!canBeUsed) {
            return true;
        }

        if (dragon instanceof ServerPlayer serverPlayer) {
            return instance.ability().value().usageBlocked().map(
                    condition -> condition.matches(serverPlayer.serverLevel(), dragon.position(), dragon)
            ).orElse(false);
        } else {
            return castWasDenied;
        }
    }

    public boolean shouldRenderAbilities() {
        // TODO: Bother with this later
        return true;
    }

    public void setRenderAbilities(boolean renderAbilities) {
        this.renderAbilities = renderAbilities;
    }

    public void reset() {
        abilities.clear();
        currentMana = 0;
        selectedAbilitySlot = 0;
    }

    public void refresh(Holder<DragonType> type) {
        abilities.clear();
        int slot = 0;
        for (Holder<DragonAbility> ability : type.value().abilities()) {
            if (ability.value().activation().type() != Activation.Type.PASSIVE) {
                if (slot < DragonAbility.MAX_ACTIVE_ON_HOTBAR) {
                    abilities.add(new DragonAbilityInstance(ability, 1, slot++));
                } else {
                    abilities.add(new DragonAbilityInstance(ability, 1, -1));
                }
            } else {
                abilities.add(new DragonAbilityInstance(ability, 1, -1));
            }
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        CompoundTag abilityKeys = new CompoundTag();
        abilities.forEach(instance -> abilityKeys.put(instance.id(), instance.save(provider)));

        tag.put(ABILITIES, abilityKeys);
        tag.putFloat(CURRENT_MANA, currentMana);
        tag.putInt(SELECTED_ABILITY_SLOT, selectedAbilitySlot);
        tag.putBoolean(RENDER_ABILITIES, renderAbilities);

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        List<DragonAbilityInstance> abilities = new ArrayList<>();
        CompoundTag storedAbilities = tag.getCompound(ABILITIES);

        for (String key : storedAbilities.getAllKeys()) {
            CompoundTag abilityTag = storedAbilities.getCompound(key);
            DragonAbilityInstance instance = DragonAbilityInstance.load(provider, abilityTag);

            if (instance != null) {
                abilities.add(instance);
            }
        }

        this.abilities = abilities;
        currentMana = tag.getFloat(CURRENT_MANA);
        selectedAbilitySlot = tag.getInt(SELECTED_ABILITY_SLOT);
        renderAbilities = tag.getBoolean(RENDER_ABILITIES);
    }

    private final String ABILITIES = "abilities";
    private final String CURRENT_MANA = "currentMana";
    private final String SELECTED_ABILITY_SLOT = "selectedAbilitySlot";
    private final String RENDER_ABILITIES = "renderAbilities";
}
