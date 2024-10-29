package by.dragonsurvivalteam.dragonsurvival.config.types;

// Alternative to the static method with switch/case would be to create an instance after the 'Class#forName' call and use that instance to call validate / parse
public interface CustomConfig {
    String convert();

    static boolean validate(final Class<?> classType, final Object configValue) {
        if (configValue instanceof String data) {
            if (classType == BlockStateConfig.class) {
                return BlockStateConfig.validate(data);
            } else if (classType == ItemHurtConfig.class) {
                return ItemHurtConfig.validate(data);
            } else if (classType == FoodConfigPredicate.class) {
                return FoodConfigPredicate.validate(data);
            } else if (classType == FoodConfigCollector.class) {
                return FoodConfigCollector.validate(data);
            }
        }

        return false;
    }

    static CustomConfig parse(final Class<?> classType, final String data) {
        if (classType == BlockStateConfig.class) {
            return BlockStateConfig.of(data);
        } else if (classType == ItemHurtConfig.class) {
            return ItemHurtConfig.of(data);
        } else if (classType == FoodConfigPredicate.class) {
            return FoodConfigPredicate.of(data);
        } else if (classType == FoodConfigCollector.class) {
            return FoodConfigCollector.of(data);
        }

        throw new IllegalArgumentException("Invalid custom config class [" + classType + "]");
    }
}
