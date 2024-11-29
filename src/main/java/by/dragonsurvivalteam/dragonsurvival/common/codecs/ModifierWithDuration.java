package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierWithDuration implements AttributeModifierSupplier {
    public static final int INFINITE_DURATION = -1;

    public static final Codec<ModifierWithDuration> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierWithDuration::modifiers),
            Codec.INT.optionalFieldOf("duration", INFINITE_DURATION).forGetter(ModifierWithDuration::duration)
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
            Codec.INT.optionalFieldOf("duration", INFINITE_DURATION).forGetter(ModifierWithDuration::duration),
            Codec.INT.fieldOf("current_duration").forGetter(ModifierWithDuration::currentDuration)
    ).apply(instance, ModifierWithDuration::new));

    private final Map<Holder<Attribute>, List<ResourceLocation>> ids;
    private final List<Modifier> modifiers;
    private final int duration;

    private int currentDuration;

    public ModifierWithDuration(final List<Modifier> modifiers, int duration) {
        if (duration < 0 && duration != INFINITE_DURATION) {
            throw new IllegalArgumentException("Invalid duration - value must be either [" + INFINITE_DURATION + "] for an infinite duration or positive: [" + duration + "]");
        }

        this.ids = new HashMap<>();
        this.modifiers = modifiers;
        this.duration = duration;
        this.currentDuration = duration;
    }

    public ModifierWithDuration(final Map<Holder<Attribute>, List<ResourceLocation>> ids, final List<Modifier> modifiers, int duration, int currentDuration) {
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

    public int duration() {
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
