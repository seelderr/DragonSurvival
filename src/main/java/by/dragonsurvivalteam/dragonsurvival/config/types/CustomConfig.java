package by.dragonsurvivalteam.dragonsurvival.config.types;

public interface CustomConfig {
    String convert();

    static boolean validate(final Class<?> classType, final Object configValue) {
        if (configValue instanceof String data) {
            if (classType == BlockStateConfig.class) {
                return BlockStateConfig.validate(data);
            } else if (classType == ItemHurtConfig.class) {
                return ItemHurtConfig.validate(data);
            } else if (classType == FoodConfig.class) {
                return FoodConfig.validate(data);
            }
        }

        return false;
    }

    static CustomConfig parse(final Class<?> classType, final String data) {
        if (classType == BlockStateConfig.class) {
            return BlockStateConfig.of(data);
        } else if (classType == ItemHurtConfig.class) {
            return ItemHurtConfig.of(data);
        } else if (classType == FoodConfig.class) {
            return FoodConfig.of(data);
        }

        throw new IllegalArgumentException("Invalid custom config class [" + classType + "]");
    }
}
