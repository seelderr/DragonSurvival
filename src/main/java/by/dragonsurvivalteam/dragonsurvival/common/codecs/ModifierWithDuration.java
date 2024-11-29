package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class ModifierWithDuration implements AttributeModifierSupplier {
    public static final int INFINITE_DURATION = -1;

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
            Codec.INT.fieldOf("current_duration").forGetter(ModifierWithDuration::currentDuration)
    ).apply(instance, ModifierWithDuration::new));

    private final Map<Holder<Attribute>, List<ResourceLocation>> ids;
    private final List<Modifier> modifiers;
    private final LevelBasedValue duration;

    private int currentDuration;

    public ModifierWithDuration(final List<Modifier> modifiers, final LevelBasedValue duration) {
        this.ids = new HashMap<>();
        this.modifiers = modifiers;
        this.duration = duration;
    }

    public ModifierWithDuration(final Map<Holder<Attribute>, List<ResourceLocation>> ids, final List<Modifier> modifiers, final LevelBasedValue duration, int currentDuration) {
        this.ids = ids;
        this.modifiers = modifiers;
        this.duration = duration;
        this.currentDuration = currentDuration;
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
        currentDuration = (int) duration().calculate(abilityLevel);
        entity.getData(DSDataAttachments.MODIFIERS_WITH_DURATION).add(this);
        applyModifiers(entity, dragonType, abilityLevel);
    }

    public void tick(final LivingEntity entity) {
        currentDuration--;

        if (currentDuration == 0) {
            ids.forEach((attribute, ids) -> {
                AttributeInstance instance = entity.getAttribute(attribute);

                if (instance != null) {
                    ids.forEach(instance::removeModifier);
                }
            });
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

    @Override
    public void storeId(final Holder<Attribute> attribute, final ResourceLocation id) {
        ids.computeIfAbsent(attribute, key -> new ArrayList<>()).add(id);
    }

    @Override
    public ModifierType getModifierType() {
        return ModifierType.CUSTOM;
    }
}
