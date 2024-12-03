package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MagicData implements INBTSerializable<CompoundTag> {

    private final List<DragonAbilityInstance> abilities = new ArrayList<>();
    private boolean renderAbilities = true;
    private int selectedAbilitySlot = 0;
    private int currentMana = 0;

    public boolean isCasting = false;

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

    public void tickAbilities(ServerPlayer player) {
        for (DragonAbilityInstance instance : abilities) {
            instance.apply(player);
        }
    }

    public DragonAbilityInstance getAbilityFromSlot(int slot) {
        if(slot < 0 || slot >= abilities.size()) {
            return null;
        }
        return abilities.get(slot);
    }

    public @Nullable DragonAbilityInstance getCurrentlyCasting() {
        return isCasting ? getAbilityFromSlot(getSelectedAbilitySlot()) : null;
    }

    public void setCurrentlyCasting() {
        isCasting = true;
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
        return abilityKeys;
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
