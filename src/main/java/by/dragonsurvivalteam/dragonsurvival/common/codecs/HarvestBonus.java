package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncHarvestBonus;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.HarvestBonuses;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import javax.annotation.Nullable;

public record HarvestBonus(ResourceLocation id, HolderSet<Block> applicableTo, LevelBasedValue bonus, LevelBasedValue duration) {
    public static final Codec<HarvestBonus> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(HarvestBonus::id),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("applicable_to").forGetter(HarvestBonus::applicableTo),
            LevelBasedValue.CODEC.fieldOf("bonus").forGetter(HarvestBonus::bonus),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(DurationInstance.INFINITE_DURATION)).forGetter(HarvestBonus::duration)
    ).apply(instance, HarvestBonus::new));

    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final LivingEntity target) {
        int abilityLevel = ability.level();
        int newDuration = (int) duration().calculate(abilityLevel);

        HarvestBonuses data = target.getData(DSDataAttachments.HARVEST_BONUSES);
        Instance instance = data.get(id);

        if (instance != null && instance.currentDuration() == newDuration && instance.appliedAbilityLevel() == abilityLevel) {
            return;
        }

        if (instance != null) {
            data.remove(target, instance);
        }

        ClientEffectProvider.ClientData clientData = new ClientEffectProvider.ClientData(ability.getIcon(), /* TODO */ Component.empty(), Optional.of(dragon.getUUID()));
        instance = new Instance(this, clientData, abilityLevel, newDuration);
        data.add(target, instance);

        if (target instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncHarvestBonus(player.getId(), instance, false));
        }
    }

    public void remove(final LivingEntity target) {
        HarvestBonuses data = target.getData(DSDataAttachments.HARVEST_BONUSES);
        Instance instance = data.get(id);

        if (instance != null && target instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncHarvestBonus(player.getId(), instance, true));
        }

        data.remove(target, instance);
    }

    public static class Instance extends DurationInstance<HarvestBonus> {
        public static final Codec<HarvestBonus.Instance> CODEC = RecordCodecBuilder.create(instance -> DurationInstance.codecStart(instance, () -> HarvestBonus.CODEC).apply(instance, Instance::new));

        public Instance(final HarvestBonus baseData, final ClientData clientData, int appliedAbilityLevel, int currentDuration) {
            super(baseData, clientData, appliedAbilityLevel, currentDuration);
        }

        public Tag save(@NotNull final HolderLookup.Provider provider) {
            return CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
        }

        public static @Nullable HarvestBonus.Instance load(@NotNull final HolderLookup.Provider provider, final CompoundTag nbt) {
            return CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
        }

        public int getBonus(final BlockState state) {
            return getBonus(state.getBlockHolder());
        }

        public int getBonus(final Holder<Block> block) {
            return baseData().applicableTo().contains(block) ? (int) baseData().bonus().calculate(appliedAbilityLevel()) : 0;
        }

        @Override
        public ResourceLocation id() {
            return baseData().id();
        }

        @Override
        public int getDuration() {
            return (int) baseData().duration().calculate(appliedAbilityLevel());
        }

        @Override
        public boolean isVisible() {
            return false;
        }
    }
}
