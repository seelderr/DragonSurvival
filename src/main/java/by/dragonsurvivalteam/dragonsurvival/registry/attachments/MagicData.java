package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber
public class MagicData implements INBTSerializable<CompoundTag> {
    public static final int NO_SLOT = -1;
    public static final int MAX_ACTIVE = 4;

    private Map<ResourceKey<DragonAbility>, DragonAbilityInstance> abilities = new HashMap<>();
    private Map<Integer, ResourceKey<DragonAbility>> hotbar = new HashMap<>();
    private boolean renderAbilities = true;
    private int selectedAbilitySlot;
    private float currentMana;

    private boolean errorMessageSent;
    private boolean isCasting;
    private boolean castWasDenied;
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

        for (DragonAbilityInstance instance : magic.abilities.values()) {
            instance.tick(event.getEntity());
        }

        if (event.getEntity().level().isClientSide() && magic.isCasting()) {
            magic.castTimer = Math.max(0, magic.castTimer - 1);
        }
    }

    public @Nullable DragonAbilityInstance fromSlot(int slot) {
        ResourceKey<DragonAbility> key = hotbar.get(slot);
        return key != null ? abilities.get(key) : null;
    }

    public int slotFromAbility(final ResourceKey<DragonAbility> key) {
        for (int slot : hotbar.keySet()) {
            if (hotbar.get(slot) == key) {
                return slot;
            }
        }

        return NO_SLOT;
    }

    public @Nullable DragonAbilityInstance getCurrentlyCasting() {
        return isCasting ? fromSlot(getSelectedAbilitySlot()) : null;
    }

    // TODO :: wait for some server response before showing the casting hud?
    //  otherwise it will flicker if the usage_blocked condition on the server prevents the cast
    public boolean attemptCast(int slot, Player player) {
        if (slot < 0 || slot >= abilities.size()) {
            return false;
        }

        DragonAbilityInstance instance = fromSlot(slot);

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
        castWasDenied = true;
    }

    public void stopCasting(final Player player, boolean forceApplyingEffects) {
        DragonAbilityInstance currentlyCasting = getCurrentlyCasting();

        if (currentlyCasting != null) {
            currentlyCasting.stopSound(player);

            if (forceApplyingEffects) {
                currentlyCasting.release(player);
                currentlyCasting.value().activation().playEndSound(player);
                if(currentlyCasting.hasEndAnimation()) {
                    currentlyCasting.value().activation().playEndAnimation(player);
                } else {
                    DragonSurvival.PROXY.setCurrentAbilityAnimation(player.getId(), null);
                }
            } else {
                DragonSurvival.PROXY.setCurrentAbilityAnimation(player.getId(), null);
                currentlyCasting.releaseWithoutCooldown();
            }

            currentlyCasting.setActive(false);

            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncCooldownState(player.getId(), getSelectedAbilitySlot(), currentlyCasting.getCooldown()));
            }
        }

        isCasting = false;
    }

    public void stopCasting(final Player player) {
        DragonAbilityInstance currentlyCasting = getCurrentlyCasting();
        stopCasting(player, currentlyCasting != null && currentlyCasting.isApplyingEffects());
    }

    public void setClientCooldown(int slot, int cooldown) {
        DragonAbilityInstance ability = fromSlot(slot);

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
        DragonAbilityInstance instance = fromSlot(getSelectedAbilitySlot());

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

    public void refresh(final Holder<DragonType> type) {
        abilities.clear();
        hotbar.clear();

        int slot = 0;

        for (Holder<DragonAbility> ability : type.value().abilities()) {
            if (ability.value().activation().type() != Activation.Type.PASSIVE) {
                if (slot < MAX_ACTIVE) {
                    abilities.put(ability.getKey(), new DragonAbilityInstance(ability, 1));
                    hotbar.put(slot, ability.getKey());
                    slot++;
                } else {
                    abilities.put(ability.getKey(), new DragonAbilityInstance(ability, 1));
                }
            } else {
                abilities.put(ability.getKey(), new DragonAbilityInstance(ability, 1));
            }
        }
    }

    public List<DragonAbilityInstance> getActiveAbilities() {
        return abilities.values().stream().filter(
                instance -> instance.ability().value().activation().type() != Activation.Type.PASSIVE
        ).toList();
    }

    public List<DragonAbilityInstance> getPassiveAbilities() {
        return abilities.values().stream().filter(
                instance -> instance.ability().value().activation().type() == Activation.Type.PASSIVE
        ).toList();
    }

    public void moveAbilityToSlot(final ResourceKey<DragonAbility> key, int newSlot) {
        int currentSlot = slotFromAbility(key);
        ResourceKey<DragonAbility> previous = hotbar.put(newSlot, key);

        if (previous != null && currentSlot != NO_SLOT) {
            hotbar.put(currentSlot, previous);
        } else {
            hotbar.remove(currentSlot);
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        CompoundTag abilities = new CompoundTag();
        this.abilities.values().forEach(instance -> abilities.put(instance.id(), instance.save(provider)));

        CompoundTag hotbar = new CompoundTag();
        this.hotbar.forEach((slot, key) -> hotbar.putInt(key.location().toString(), slot));

        tag.put(ABILITIES, abilities);
        tag.put(HOTBAR, hotbar);
        tag.putFloat(CURRENT_MANA, currentMana);
        tag.putInt(SELECTED_SLOT, selectedAbilitySlot);
        tag.putBoolean(RENDER_ABILITIES, renderAbilities);

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        Map<ResourceKey<DragonAbility>, DragonAbilityInstance> abilities = new HashMap<>();
        CompoundTag storedAbilities = tag.getCompound(ABILITIES);

        for (String key : storedAbilities.getAllKeys()) {
            CompoundTag abilityTag = storedAbilities.getCompound(key);
            DragonAbilityInstance instance = DragonAbilityInstance.load(provider, abilityTag);

            if (instance != null) {
                abilities.put(instance.key(), instance);
            }
        }

        this.abilities = abilities;

        Map<Integer, ResourceKey<DragonAbility>> hotbar = new HashMap<>();
        CompoundTag storedHotbar = tag.getCompound(HOTBAR);

        for (String location : storedHotbar.getAllKeys()) {
            int slot = storedHotbar.getInt(location);
            // TODO :: what if the ability is removed through a datapack?
            ResourceKey<DragonAbility> key = ResourceKey.create(DragonAbility.REGISTRY, ResourceLocation.parse(location));
            hotbar.put(slot, key);
        }

        this.hotbar = hotbar;

        currentMana = tag.getFloat(CURRENT_MANA);
        selectedAbilitySlot = tag.getInt(SELECTED_SLOT);
        renderAbilities = tag.getBoolean(RENDER_ABILITIES);
    }

    private final String ABILITIES = "abilities";
    private final String HOTBAR = "hotbar";
    private final String CURRENT_MANA = "current_mana";
    private final String SELECTED_SLOT = "selected_slot";
    private final String RENDER_ABILITIES = "render_abilities";
}
