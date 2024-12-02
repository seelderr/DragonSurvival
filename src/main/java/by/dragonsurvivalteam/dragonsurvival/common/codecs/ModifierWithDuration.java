package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ModifiersWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.AttributeModifierSupplier;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.*;
import javax.annotation.Nullable;

public record ModifierWithDuration(ResourceLocation id, List<Modifier> modifiers, LevelBasedValue duration) {
    public static final int INFINITE_DURATION = -1;

    public static final Codec<ModifierWithDuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ModifierWithDuration::id),
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierWithDuration::modifiers),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(ModifierWithDuration::duration)
    ).apply(instance, ModifierWithDuration::new));

    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final LivingEntity target) {
        int abilityLevel = ability.getLevel();
        int newDuration = (int) duration().calculate(abilityLevel);

        ModifiersWithDuration data = target.getData(DSDataAttachments.MODIFIERS_WITH_DURATION);
        Instance instance = data.get(this);

        if (instance != null && instance.currentDuration() == newDuration && instance.appliedAbilityLevel() == abilityLevel) {
            return;
        }

        if (instance != null) {
            data.remove(target, instance);
        }

        ClientEffectProvider.ClientData clientData = new ClientEffectProvider.ClientData(ability.getAbility().icon().get(abilityLevel), /* TODO */ Component.empty(), Optional.of(dragon.getUUID()));
        data.add(target, new Instance(this, new HashMap<>(), clientData, abilityLevel, newDuration));
        // TODO :: send packet to client
    }

    public static class Instance implements AttributeModifierSupplier, ClientEffectProvider {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ModifierWithDuration.CODEC.fieldOf("base_data").forGetter(Instance::baseData),
                Codec.compoundList(BuiltInRegistries.ATTRIBUTE.holderByNameCodec(), ResourceLocation.CODEC.listOf()).xmap(pairs -> {
                    Map<Holder<Attribute>, List<ResourceLocation>> ids = new HashMap<>();
                    pairs.forEach(pair -> pair.getSecond().forEach(id -> ids.computeIfAbsent(pair.getFirst(), key -> new ArrayList<>()).add(id)));
                    return ids;
                }, ids -> {
                    List<Pair<Holder<Attribute>, List<ResourceLocation>>> pairs = new ArrayList<>();
                    ids.forEach((attribute, value) -> pairs.add(new Pair<>(attribute, value)));
                    return pairs;
                }).fieldOf("ids").forGetter(Instance::getStoredIds),
                ClientData.CODEC.fieldOf("client_data").forGetter(Instance::clientData),
                Codec.INT.fieldOf("applied_ability_level").forGetter(Instance::appliedAbilityLevel),
                Codec.INT.fieldOf("current_duration").forGetter(Instance::currentDuration)
        ).apply(instance, Instance::new));

        private final ModifierWithDuration baseData;
        private final Map<Holder<Attribute>, List<ResourceLocation>> ids;
        private final ClientData clientData;
        private final int appliedAbilityLevel;
        private int currentDuration;

        public Instance(final ModifierWithDuration baseData, final Map<Holder<Attribute>, List<ResourceLocation>> ids, final ClientData clientData, int appliedAbilityLevel, int currentDuration) {
            this.baseData = baseData;
            this.ids = ids;
            this.clientData = clientData;
            this.appliedAbilityLevel = appliedAbilityLevel;
            this.currentDuration = currentDuration;
        }

        public Tag save() {
            return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
        }

        public static @Nullable Instance load(final CompoundTag nbt) {
            return CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
        }

        public boolean tick() {
            if (currentDuration == INFINITE_DURATION) {
                return false;
            }

            currentDuration--;
            return currentDuration == 0;
        }

        public ModifierWithDuration baseData() {
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
        public void storeId(final Holder<Attribute> attribute, final ResourceLocation id) {
            ids.computeIfAbsent(attribute, key -> new ArrayList<>()).add(id);
        }

        @Override
        public Map<Holder<Attribute>, List<ResourceLocation>> getStoredIds() {
            return ids;
        }

        @Override
        public ModifierType getModifierType() {
            return ModifierType.CUSTOM;
        }
    }
}
