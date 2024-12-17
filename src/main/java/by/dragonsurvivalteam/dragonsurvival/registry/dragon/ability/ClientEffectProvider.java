package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DurationInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

public interface ClientEffectProvider {
    record ClientData(ResourceLocation texture, Component tooltip, Optional<UUID> effectSource) {
        public static final Codec<ClientData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ClientData::texture),
                // TODO :: since the arguments are handled by string.format we can just supply them
                //  and define the order in some wiki
                //  people can then specify which argument they want to use by using '%2$' etc. (example for using the second parameter)
                ComponentSerialization.CODEC.optionalFieldOf("tooltip", Component.empty()).forGetter(ClientData::tooltip),
                UUIDUtil.CODEC.optionalFieldOf("effect_source").forGetter(ClientData::effectSource)
        ).apply(instance, ClientData::new));
    }

    /** See {@link net.minecraft.client.renderer.texture.MissingTextureAtlasSprite#MISSING_TEXTURE_LOCATION} */
    ResourceLocation MISSING_TEXTURE = ResourceLocation.withDefaultNamespace("missingno");
    ClientData NONE = new ClientData(MISSING_TEXTURE, Component.empty(), Optional.empty());

    default boolean isInfiniteDuration() {
        return getDuration() == DurationInstance.INFINITE_DURATION;
    }

    default boolean isInvisible() {
        return false;
    }

    ClientData clientData();
    ResourceLocation id();

    int getDuration();
    int currentDuration();
}
