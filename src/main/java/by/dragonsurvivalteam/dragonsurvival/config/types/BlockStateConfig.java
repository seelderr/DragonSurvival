package by.dragonsurvivalteam.dragonsurvival.config.types;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class BlockStateConfig implements CustomConfig {
    private static final int BLOCK_STATES = 2;

    private final Predicate<BlockState> predicate;
    private final Map<String, String> properties;
    private final String originalData;

    private BlockStateConfig(final Predicate<BlockState> predicate, final Map<String, String> properties, final String originalData) {
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

            // It's a valid block and the required properties match
            return true;
        }

        return false;
    }

    public static BlockStateConfig of(final String data) {
        String[] splitData = data.split(":");
        Predicate<BlockState> predicate = ConfigUtils.blockStatePredicate(splitData);

        Map<String, String> properties = new HashMap<>();
        String[] states = splitData[BLOCK_STATES].split(",");

        for (String state : states) {
            String[] split = state.split("=");
            properties.put(split[0], split[1]);
        }

        return new BlockStateConfig(predicate, properties, data);
    }

    public static boolean validate(final String data) {
        String[] splitData = data.split(":");

        if (!ConfigUtils.validateResourceLocation(splitData)) {
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
