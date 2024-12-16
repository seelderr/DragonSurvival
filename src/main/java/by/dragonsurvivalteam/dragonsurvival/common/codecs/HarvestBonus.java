package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncHarvestBonus;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.HarvestBonuses;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.StorageEntry;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
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

import java.util.Optional;
import javax.annotation.Nullable;

public record HarvestBonus(ResourceLocation id, HolderSet<Block> applicableTo, LevelBasedValue bonus, LevelBasedValue duration) {
    public static final int INFINITE_DURATION = -1;

    public static final Codec<HarvestBonus> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(HarvestBonus::id),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("applicable_to").forGetter(HarvestBonus::applicableTo),
            LevelBasedValue.CODEC.optionalFieldOf("bonus", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(HarvestBonus::bonus),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(HarvestBonus::duration)
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

    public static class Instance implements ClientEffectProvider, StorageEntry {
        public static final Codec<HarvestBonus.Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                HarvestBonus.CODEC.fieldOf("base_data").forGetter(HarvestBonus.Instance::baseData),
                ClientData.CODEC.fieldOf("client_data").forGetter(HarvestBonus.Instance::clientData),
                Codec.INT.fieldOf("applied_ability_level").forGetter(HarvestBonus.Instance::appliedAbilityLevel),
                Codec.INT.fieldOf("current_duration").forGetter(HarvestBonus.Instance::currentDuration)
        ).apply(instance, HarvestBonus.Instance::new));

        private final HarvestBonus baseData;
        private final ClientData clientData;
        private final int appliedAbilityLevel;
        private int currentDuration;

        public Instance(final HarvestBonus baseData, final ClientData clientData, int appliedAbilityLevel, int currentDuration) {
            this.baseData = baseData;
            this.clientData = clientData;
            this.appliedAbilityLevel = appliedAbilityLevel;
            this.currentDuration = currentDuration;
        }

        public Tag save() {
            return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
        }

        public static @Nullable HarvestBonus.Instance load(final CompoundTag nbt) {
            return CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
        }

        @Override
        public boolean tick() {
            if (currentDuration == INFINITE_DURATION) {
                return false;
            }

            currentDuration--;
            return currentDuration == 0;
        }

        public int getBonus(final BlockState state) {
            return getBonus(state.getBlockHolder());
        }

        public int getBonus(final Holder<Block> block) {
            return baseData().applicableTo().contains(block) ? (int) baseData().bonus().calculate(appliedAbilityLevel()) : 0;
        }

        public HarvestBonus baseData() {
            return baseData;
        }

        public int appliedAbilityLevel() {
            return appliedAbilityLevel;
        }

        public int currentDuration() {
            return currentDuration;
        }

        @Override
        public ClientData clientData() {
            return clientData;
        }

        @Override
        public int getDuration() {
            return (int) baseData().duration().calculate(appliedAbilityLevel());
        }

        @Override
        public ResourceLocation getId() {
            return baseData().id();
        }

        @Override
        public boolean isVisible() {
            return false;
        }
    }
}
