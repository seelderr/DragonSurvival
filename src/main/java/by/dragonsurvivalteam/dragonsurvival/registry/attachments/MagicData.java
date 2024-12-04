package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncCooldownState;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MagicData implements INBTSerializable<CompoundTag> {

    private final List<DragonAbilityInstance> abilities = new ArrayList<>();
    private boolean renderAbilities = true;
    private int selectedAbilitySlot = 0;
    private int currentMana = 0;

    private boolean errorMessageSent = false;
    private boolean isCasting = false;
    private boolean castWasDenied = false;
    private int castTimer;

    public static MagicData getData(Player player) {
        return player.getData(DSDataAttachments.MAGIC);
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
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

    public void tickAbilities(Player player) {
        for (DragonAbilityInstance instance : abilities) {
            instance.apply(player);
        }

        if (player.level().isClientSide()) {
            if (isCasting) {
                castTimer = Math.max(0, castTimer - 1);
            }
        }
    }

    public DragonAbilityInstance getAbilityFromSlot(int slot) {
        if(slot < 0 || slot >= abilities.size()) {
            return null;
        }

        for(DragonAbilityInstance ability : abilities) {
            if(ability.getSlot() == slot) {
                return ability;
            }
        }

        return null;
    }

    public @Nullable DragonAbilityInstance getCurrentlyCasting() {
        return isCasting ? getAbilityFromSlot(getSelectedAbilitySlot()) : null;
    }

    public boolean setAbilitySlotAndBeginCast(int slot, Player player) {
        if(slot < 0 || slot >= abilities.size()) {
            return false;
        }

        DragonAbilityInstance ability = getAbilityFromSlot(slot);
        if(ability == null || !canBeginCast(slot, player)) {
            if(player.level().isClientSide()) {
                if(ability != null && getAbilityFromSlot(slot).isInCooldown(player) && !errorMessageSent) {
                    errorMessageSent = true;
                    MagicHUD.castingError(Component.translatable(MagicHUD.COOLDOWN, NumberFormat.getInstance().format(ability.getCooldown(player) / 20F) + "s").withStyle(ChatFormatting.RED));
                }
            }
            return false;
        }

        DragonAbilityInstance currentlyCasting = getCurrentlyCasting();
        if(currentlyCasting != null) {
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

    public void stopCasting(Player player) {
        DragonAbilityInstance currentlyCasting = getCurrentlyCasting();
        if (currentlyCasting != null) {
            if(currentlyCasting.isActive()) {
                currentlyCasting.release(player);
            } else {
                currentlyCasting.releaseWithoutCooldown();
            }

            if(player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncCooldownState(player.getId(), getSelectedAbilitySlot(), currentlyCasting.getCooldown(player)));
            }
        }
        isCasting = false;
    }

    public void setCooldown(int slot, int cooldown) {
        DragonAbilityInstance ability = getAbilityFromSlot(slot);
        if(ability != null) {
            ability.setCooldown(cooldown);
        }
    }

    public void setCastWasDenied(boolean castWasDenied) {
        this.castWasDenied = castWasDenied;
    }

    public void setErrorMessageSent(boolean errorMessageSent) {
        this.errorMessageSent = errorMessageSent;
    }

    private void beginCasting() {
        DragonAbilityInstance abilityToBeCast = getAbilityFromSlot(getSelectedAbilitySlot());
        if(abilityToBeCast == null) {
            return;
        }

        isCasting = true;
        castTimer = abilityToBeCast.getAbility().getChargeTime(abilityToBeCast.getLevel());
        abilityToBeCast.setEnabled(true);
    }

    public boolean isCasting() {
        return isCasting && getCurrentlyCasting() != null;
    }

    private boolean canBeginCast(int slot, Player player) {
        DragonAbilityInstance ability = getAbilityFromSlot(slot);
        if(ability == null) {
            return false;
        }

        boolean isNotInCooldown = !ability.isInCooldown(player);
        boolean hasEnoughMana = getAbilityFromSlot(slot).checkInitialManaCost(player);
        boolean commonConditions = isNotInCooldown && hasEnoughMana;
        if(commonConditions && player.level().isClientSide()) {
            commonConditions = !castWasDenied;
        } else if(commonConditions) {
            boolean entityPredicate = !getAbilityFromSlot(slot).getAbility().usageBlocked().map(blocked -> !blocked.matches((ServerLevel) player.level(), player.position(), player)).orElse(false);
            commonConditions = entityPredicate;
        }
        return commonConditions;
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
            if (ability.value().type() != DragonAbility.Type.PASSIVE) {
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
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        CompoundTag abilityKeys = new CompoundTag();
        abilities.forEach((ability) -> abilityKeys.put(ability.getAbilityKey().toString(), ability.serializeNBT(provider)));
        tag.put(ABILITIES, abilityKeys);
        tag.putInt(CURRENT_MANA, currentMana);
        tag.putInt(SELECTED_ABILITY_SLOT, selectedAbilitySlot);
        tag.putBoolean(RENDER_ABILITIES, renderAbilities);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        CompoundTag abilities = nbt.getCompound(ABILITIES);
        for (String key : abilities.getAllKeys()) {
            DragonAbilityInstance instance = new DragonAbilityInstance();
            instance.deserializeNBT(provider, abilities.getCompound(key));
            this.abilities.add(instance);
        }
        currentMana = nbt.getInt(CURRENT_MANA);
        selectedAbilitySlot = nbt.getInt(SELECTED_ABILITY_SLOT);
        renderAbilities = nbt.getBoolean(RENDER_ABILITIES);
    }

    private final String ABILITIES = "abilities";
    private final String CURRENT_MANA = "currentMana";
    private final String SELECTED_ABILITY_SLOT = "selectedAbilitySlot";
    private final String RENDER_ABILITIES = "renderAbilities";
}
