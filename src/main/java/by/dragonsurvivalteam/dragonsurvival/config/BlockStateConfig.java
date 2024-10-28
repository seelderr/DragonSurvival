package by.dragonsurvivalteam.dragonsurvival.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class BlockStateConfig implements CustomConfig {
    private static final int NAMESPACE = 0;
    private static final int PATH = 1;
    private static final int BLOCK_STATES = 2;

    private final Predicate<BlockState> predicate;
    private final Map<String, String> properties;

    private final String originalData;

    private BlockStateConfig(final Predicate<BlockState> predicate, final Map<String, String> properties, @Nullable final String originalData) {
        this.predicate = predicate;
        this.properties = properties;
        this.originalData = originalData;
    }

    @Override
    public String convert() {
        return originalData;
    }

    public boolean test(final BlockState state) {
        if (predicate.test(state)) {
            // Get known properties of the block
            StateDefinition<Block, BlockState> stateDefinition = state.getBlock().getStateDefinition();

            for (String key : properties.keySet()) {
                Property<?> property = stateDefinition.getProperty(key);

                if (property == null) {
                    return false;
                }

                // Compare the property value with the current of the state
                if (!property.getValue(properties.get(key)).map(value -> value.equals(state.getValue(property))).orElse(false)) {
                    return false;
                }

            }
        }

        return false;
    }

    public static Optional<BlockStateConfig> of(final String data) {
        String[] splitData = data.split(":");
        boolean isTag = splitData[NAMESPACE].startsWith("#");

        Predicate<BlockState> blockPredicate;

        if (isTag) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(splitData[NAMESPACE].substring(1), splitData[PATH]);
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, location);
            // Can't check if tag exists or not at this point in time
            blockPredicate = state -> state.is(tag);
        } else {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(splitData[NAMESPACE], splitData[PATH]);
            Block block = BuiltInRegistries.BLOCK.get(location);

            // Default return value of the registry
            if (block == Blocks.AIR) {
                return Optional.empty();
            }

            blockPredicate = state -> state.is(block);
        }

        Map<String, String> properties = new HashMap<>();
        String[] states = splitData[BLOCK_STATES].split(",");

        for (String state : states) {
            String[] split = state.split("=");
            properties.put(split[0], split[1]);
        }

        return Optional.of(new BlockStateConfig(blockPredicate, properties, data));
    }

    public static boolean validate(final String data) {
        String[] splitData = data.split(":");

        if (splitData.length != 3) {
            return false;
        }

        if (splitData[NAMESPACE].startsWith("#")) {
            splitData[NAMESPACE] = splitData[NAMESPACE].substring(1);
        }

        if (ResourceLocation.tryParse(splitData[NAMESPACE] + ":" + splitData[PATH]) == null) {
            return false;
        }

        String[] blockStateData = splitData[BLOCK_STATES].split(",");

        for (String stateData : blockStateData) {
            if (stateData.split("=").length != 2) {
                return false;
            }
        }

        return true;
    }
}
