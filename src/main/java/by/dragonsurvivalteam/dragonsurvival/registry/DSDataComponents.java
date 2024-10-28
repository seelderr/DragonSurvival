package by.dragonsurvivalteam.dragonsurvival.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.function.UnaryOperator;

public class DSDataComponents {
	//public static DataComponentType<ExtraCodecs.TagOrElementLocation> VALID_VAULTS = register("valid_vaults", e -> e.persistent(ExtraCodecs.TAG_OR_ELEMENT_ID));
	public static DataComponentType<Vector3f> TARGET_POSITION = register("target_position", e -> e.persistent(ExtraCodecs.VECTOR3F).networkSynchronized(ByteBufCodecs.VECTOR3F));

	private static <T> DataComponentType<T> register(String pName, UnaryOperator<DataComponentType.Builder<T>> pBuilder) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, pName, pBuilder.apply(DataComponentType.builder()).build());
	}
}
