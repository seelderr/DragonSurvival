package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ModifiersWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.AttributeModifierSupplier;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierWithDuration implements AttributeModifierSupplier {
    public static final int INFINITE_DURATION = -1;
    public static int NO_LEVEL = -1;

    public static final Codec<ModifierWithDuration> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierWithDuration::modifiers),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(ModifierWithDuration::duration)
    ).apply(instance, ModifierWithDuration::new));

    public static final Codec<ModifierWithDuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.compoundList(BuiltInRegistries.ATTRIBUTE.holderByNameCodec(), ResourceLocation.CODEC.listOf()).xmap(pairs -> {
                Map<Holder<Attribute>, List<ResourceLocation>> ids = new HashMap<>();
                pairs.forEach(pair -> pair.getSecond().forEach(id -> ids.computeIfAbsent(pair.getFirst(), key -> new ArrayList<>()).add(id)));
                return ids;
            }, ids -> {
                List<Pair<Holder<Attribute>, List<ResourceLocation>>> pairs = new ArrayList<>();
                ids.forEach((attribute, value) -> pairs.add(new Pair<>(attribute, value)));
                return pairs;
            }).fieldOf("ids").forGetter(ModifierWithDuration::ids),
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierWithDuration::modifiers),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(ModifierWithDuration::duration),
            Codec.INT.fieldOf("current_duration").forGetter(ModifierWithDuration::currentDuration),
            Codec.INT.fieldOf("applied_ability_level").forGetter(ModifierWithDuration::appliedAbilityLevel)
    ).apply(instance, ModifierWithDuration::new));

    private final Map<Holder<Attribute>, List<ResourceLocation>> ids;
    private final List<Modifier> modifiers;
    private final LevelBasedValue duration;

    private int currentDuration;
    private int appliedAbilityLevel = NO_LEVEL;

    public ModifierWithDuration(final List<Modifier> modifiers, final LevelBasedValue duration) {
        this.ids = new HashMap<>();
        this.modifiers = modifiers;
        this.duration = duration;
    }

    public ModifierWithDuration(final Map<Holder<Attribute>, List<ResourceLocation>> ids, final List<Modifier> modifiers, final LevelBasedValue duration, int currentDuration, int appliedAbilityLevel) {
        this.ids = ids;
        this.modifiers = modifiers;
        this.duration = duration;
        this.currentDuration = currentDuration;
        this.appliedAbilityLevel = appliedAbilityLevel;
    }

    public Tag save() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public static @Nullable ModifierWithDuration load(final CompoundTag nbt) {
        return CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
    }

    public void apply(final LivingEntity entity, int abilityLevel) {
        String dragonType = DragonStateProvider.getOptional(entity).map(DragonStateHandler::getTypeNameLowerCase).orElse(null);
        apply(entity, abilityLevel, dragonType);
    }

    public void apply(final LivingEntity entity, int abilityLevel, final String dragonType) {
        ModifiersWithDuration data = entity.getData(DSDataAttachments.MODIFIERS_WITH_DURATION);
        int newDuration = (int) duration().calculate(abilityLevel);

        if (currentDuration == newDuration && appliedAbilityLevel == abilityLevel && data.contains(this)) {
            return;
        }

        data.remove(entity, this);
        currentDuration = newDuration;
        appliedAbilityLevel = abilityLevel;
        data.add(this);

        applyModifiers(entity, dragonType, abilityLevel);
    }

    public void tick(final LivingEntity entity) {
        if (currentDuration == INFINITE_DURATION) {
            return;
        }

        currentDuration--;

        if (currentDuration == 0) {
            removeModifiers(entity);
        }
    }

    public Map<Holder<Attribute>, List<ResourceLocation>> ids() {
        return ids;
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
}
