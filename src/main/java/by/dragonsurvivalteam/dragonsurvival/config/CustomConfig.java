package by.dragonsurvivalteam.dragonsurvival.config;

public interface CustomConfig {
    String convert();

    static boolean validate(final Class<?> classType, final Object configValue) {
        if (configValue instanceof String data) {
            if (classType == BlockStateConfig.class) {
                return BlockStateConfig.validate(data);
            }
        }

        return false;
    }

    static CustomConfig parse(final Class<?> classType, final String data) {
        if (classType == BlockStateConfig.class) {
            return BlockStateConfig.of(data);
        }

        throw new IllegalArgumentException("Invalid custom config class [" + classType + "]");
    }
}
