package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
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
import java.util.function.Function;
import javax.annotation.Nullable;

public class ModifierWithDuration implements AttributeModifierSupplier, ClientEffectProvider {
    public static final int INFINITE_DURATION = -1;
    public static int NO_LEVEL = -1;

    public static final Codec<ModifierWithDuration> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ModifierWithDuration::id),
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierWithDuration::modifiers),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(ModifierWithDuration::duration)
    ).apply(instance, ModifierWithDuration::new));

    public static final Codec<ModifierWithDuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DIRECT_CODEC.fieldOf("base_data").forGetter(Function.identity()),
            Codec.compoundList(BuiltInRegistries.ATTRIBUTE.holderByNameCodec(), ResourceLocation.CODEC.listOf()).xmap(pairs -> {
                Map<Holder<Attribute>, List<ResourceLocation>> ids = new HashMap<>();
                pairs.forEach(pair -> pair.getSecond().forEach(id -> ids.computeIfAbsent(pair.getFirst(), key -> new ArrayList<>()).add(id)));
                return ids;
            }, ids -> {
                List<Pair<Holder<Attribute>, List<ResourceLocation>>> pairs = new ArrayList<>();
                ids.forEach((attribute, value) -> pairs.add(new Pair<>(attribute, value)));
                return pairs;
            }).fieldOf("ids").forGetter(ModifierWithDuration::ids),
            Codec.INT.fieldOf("current_duration").forGetter(ModifierWithDuration::currentDuration),
            Codec.INT.fieldOf("applied_ability_level").forGetter(ModifierWithDuration::appliedAbilityLevel),
            ClientData.CODEC.fieldOf("client_data").forGetter(ModifierWithDuration::clientData)
    ).apply(instance, ModifierWithDuration::new));

    private final ResourceLocation id;
    private final List<Modifier> modifiers;
    private final LevelBasedValue duration;

    private final Map<Holder<Attribute>, List<ResourceLocation>> ids;
    private int currentDuration;
    private int appliedAbilityLevel = NO_LEVEL;
    private ClientData clientData = ClientEffectProvider.NONE;

    public ModifierWithDuration(final ResourceLocation id, final List<Modifier> modifiers, final LevelBasedValue duration) {
        this.id = id;
        this.modifiers = modifiers;
        this.duration = duration;

        this.ids = new HashMap<>();
    }

    public ModifierWithDuration(final ModifierWithDuration baseData, final Map<Holder<Attribute>, List<ResourceLocation>> ids, int currentDuration, int appliedAbilityLevel, final ClientData clientData) {
        this.id = baseData.id();
        this.modifiers = baseData.modifiers();
        this.duration = baseData.duration();
        this.clientData = baseData.clientData();

        this.ids = ids;
        this.currentDuration = currentDuration;
        this.appliedAbilityLevel = appliedAbilityLevel;
    }

    public Tag save() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public static @Nullable ModifierWithDuration load(final CompoundTag nbt) {
        return CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
    }

    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final LivingEntity target) {
        String dragonType = DragonStateProvider.getOptional(target).map(DragonStateHandler::getTypeNameLowerCase).orElse(null);
        int abilityLevel = ability.getLevel();

        ModifiersWithDuration data = target.getData(DSDataAttachments.MODIFIERS_WITH_DURATION);
        int newDuration = (int) duration().calculate(abilityLevel);

        if (currentDuration == newDuration && appliedAbilityLevel == abilityLevel && data.contains(this)) {
            return;
        }

        data.remove(target, this);
        currentDuration = newDuration;
        appliedAbilityLevel = abilityLevel;
        clientData = new ClientData(ability.getAbility().icon().get(abilityLevel), /* TODO */ Component.empty(), Optional.of(dragon.getUUID()));
        data.add(this);
        // TODO :: send packet to client

        applyModifiers(target, dragonType, abilityLevel);
    }

    public void tick(final LivingEntity entity) {
        if (currentDuration == INFINITE_DURATION) {
            return;
        }

        currentDuration--;

        if (currentDuration == 0) {
            entity.getData(DSDataAttachments.MODIFIERS_WITH_DURATION).remove(entity, this);
        }
    }

    public Map<Holder<Attribute>, List<ResourceLocation>> ids() {
        return ids;
    }

    public ResourceLocation id() {
        return id;
    }

    public List<Modifier> modifiers() {
        return modifiers;
    }

    public LevelBasedValue duration() {
        return duration;
    }

    public int currentDuration() {
        return currentDuration;
    }

    public int appliedAbilityLevel() {
        return appliedAbilityLevel;
    }

    @Override
    public ClientData clientData() {
        return clientData;
    }

    @Override
    public int getDuration() {
        return (int) duration().calculate(appliedAbilityLevel());
    }

    @Override
    public void storeId(final Holder<Attribute> attribute, final ResourceLocation id) {
        ids.computeIfAbsent(attribute, key -> new ArrayList<>()).add(id);
    }

    @Override
    public Map<Holder<Attribute>, List<ResourceLocation>> getStoredIds() {
        return ids();
    }

    @Override
    public ModifierType getModifierType() {
        return ModifierType.CUSTOM;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof ModifierWithDuration otherModifier && id().equals(otherModifier.id())) {
            return true;
        }

        return super.equals(other);
    }
}
