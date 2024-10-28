package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.*;
import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the annotated classes to handle the config values <br>
 * Normally it's a one way setting from the {@link ModConfigSpec.ConfigValue} fields to the class fields <br>
 * (The exception being {@link ConfigHandler#updateConfigValue(String, Object)})
 */
@EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigHandler {
    public static ClientConfig CLIENT;
    public static ModConfigSpec clientSpec;
    public static ServerConfig SERVER;
    public static ModConfigSpec serverSpec;

    /** Contains the default values (specified in-code) <br> The key is {@link ConfigOption#key()} */
    public static final HashMap<String, Object> DEFAULT_CONFIG_VALUES = new HashMap<>();
    /** Contains the {@link ConfigType} variants <br> The key is {@link ConfigOption#key()} */
    public static final HashMap<String, ConfigType> CONFIG_TYPES = new HashMap<>();
    /** Contains all {@link ConfigOption} entries <br> The key is {@link ConfigOption#key()} */
    public static final HashMap<String, ConfigOption> CONFIG_OBJECTS = new HashMap<>();
    /** Contains all fields which have an {@link ConfigOption} annotation <br> The key is {@link ConfigOption#key()} */
    public static final HashMap<String, Field> CONFIG_FIELDS = new HashMap<>();
    /** Contains all config keys per side (i.e. client or server) */
    public static final HashMap<ConfigSide, Set<String>> CONFIG_KEYS = new HashMap<>();
    /** Contains all config values */
    public static final HashMap<String, ModConfigSpec.ConfigValue<?>> CONFIG_VALUES = new HashMap<>();

    private static final HashMap<Class<?>, Registry<?>> REGISTRY_MAP = new HashMap<>();

    @ConfigOption(side = ConfigSide.SERVER, key = "test_config", comment = "some comment")
    public static List<BlockStateConfig> testConfig = List.of(BlockStateConfig.of("#minecraft:campfires:lit=true"), BlockStateConfig.of("#minecraft:others:lit=false,my_state=true,c=1"));

    public static void initTypes() {
        REGISTRY_MAP.put(Item.class, BuiltInRegistries.ITEM);
        REGISTRY_MAP.put(Block.class, BuiltInRegistries.BLOCK);
        REGISTRY_MAP.put(EntityType.class, BuiltInRegistries.ENTITY_TYPE);
        REGISTRY_MAP.put(BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);
        REGISTRY_MAP.put(Biome.class, BuiltInRegistries.BIOME_SOURCE);
        REGISTRY_MAP.put(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
        REGISTRY_MAP.put(Potion.class, BuiltInRegistries.POTION);
    }

    private static List<Field> getFields() {
        List<Field> instances = new ArrayList<>();

        Type annotationType = Type.getType(ConfigOption.class);
        ModList.get().getAllScanData().forEach(s -> {
            List<ModFileScanData.AnnotationData> ebsTargets = s.getAnnotations().stream().filter(s1 -> s1.targetType() == ElementType.FIELD).filter(annotationData -> annotationType.equals(annotationData.annotationType())).toList();

            ebsTargets.forEach(ad -> {
                ModAnnotation.EnumHolder sidesValue = (ModAnnotation.EnumHolder) ad.annotationData().get("side");
                Dist side = Objects.equals(sidesValue.value(), "CLIENT") ? Dist.CLIENT : Dist.DEDICATED_SERVER;

                if (side == FMLEnvironment.dist || side == Dist.DEDICATED_SERVER) {
                    try {
                        Class<?> c = Class.forName(ad.clazz().getClassName());
                        Field fe = c.getDeclaredField(ad.memberName());
                        instances.add(fe);
                    } catch (Exception e) {
                        DragonSurvivalMod.LOGGER.error(e);
                    }
                }
            });
        });
        return instances;
    }

    public static void initConfig() {
        initTypes();

        List<String> duplicateKeys = new ArrayList<>();

        getFields().forEach(field -> {
            // There are no per-instance configs
            if (!Modifier.isStatic(field.getModifiers())) {
                return;
            }

            ConfigOption configOption = field.getAnnotation(ConfigOption.class);

            try {
                // null because it's a static access (i.e. no instance)
                DEFAULT_CONFIG_VALUES.put(configOption.key(), field.get(null));
            } catch (IllegalAccessException e) {
                DragonSurvivalMod.LOGGER.error("There was a problem while trying to get the default config value of [{}]", ConfigHandler.createConfigPath(configOption), e);
            }

            CONFIG_FIELDS.put(configOption.key(), field);
            CONFIG_OBJECTS.put(configOption.key(), configOption);

            ConfigType configType = field.getAnnotation(ConfigType.class);

            if (configType != null) {
                CONFIG_TYPES.put(configOption.key(), configType);
            }

            boolean keyAdded = CONFIG_KEYS.computeIfAbsent(configOption.side(), key -> new HashSet<>()).add(configOption.key());

            if (!keyAdded) {
                duplicateKeys.add(configOption.key());
            }
        });

        if (!duplicateKeys.isEmpty()) {
            throw new IllegalStateException("Tried to add duplicate config keys: " + duplicateKeys);
        }

        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();

        if (FMLLoader.getDist().isClient()) {
            Pair<ClientConfig, ModConfigSpec> clientConfig = new ModConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT = clientConfig.getLeft();
            clientSpec = clientConfig.getRight();

            modContainer.registerConfig(ModConfig.Type.CLIENT, clientSpec);
        }

        Pair<ServerConfig, ModConfigSpec> serverConfig = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER = serverConfig.getLeft();
        serverSpec = serverConfig.getRight();

        modContainer.registerConfig(ModConfig.Type.SERVER, serverSpec);
    }

    public static void createConfigEntries(final ModConfigSpec.Builder builder, final ConfigSide side) {
        for (String key : CONFIG_KEYS.getOrDefault(side, Set.of())) {
            ConfigOption configOption = CONFIG_OBJECTS.get(key);
            Field field = CONFIG_FIELDS.get(key);
            Object defaultValues = DEFAULT_CONFIG_VALUES.get(configOption.key());

            // Get the category - if none is present put it in the 'general' category
            String[] categories = configOption.category() != null && configOption.category().length > 0 ? configOption.category() : new String[]{"general"};
            String[] comment = configOption.comment() != null ? configOption.comment() : new String[0];

            for (String category : categories) {
                builder.push(category);
            }

            builder.comment(comment);

            if (!configOption.localization().isBlank()) {
                builder.translation(configOption.localization());
            }

            try {
                ConfigRange range = field.isAnnotationPresent(ConfigRange.class) ? field.getAnnotation(ConfigRange.class) : null;
                boolean hasRange = range != null;

                // Fill the configuration options (define the key, default value and predicate to check if the option is valid)
                if (defaultValues instanceof Integer intVal) {
                    ModConfigSpec.IntValue value = builder.defineInRange(configOption.key(), intVal, hasRange ? (int) range.min() : Integer.MIN_VALUE, hasRange ? (int) range.max() : Integer.MAX_VALUE);
                    CONFIG_VALUES.put(key, value);
                } else if (defaultValues instanceof Float floatVal) {
                    ModConfigSpec.DoubleValue value = builder.defineInRange(configOption.key(), floatVal, hasRange ? range.min() : Float.MIN_VALUE, hasRange ? range.max() : Float.MAX_VALUE);
                    CONFIG_VALUES.put(key, value);
                } else if (defaultValues instanceof Long longVal) {
                    ModConfigSpec.LongValue value = builder.defineInRange(configOption.key(), longVal, hasRange ? (long) range.min() : Long.MIN_VALUE, hasRange ? (long) range.max() : Long.MAX_VALUE);
                    CONFIG_VALUES.put(key, value);
                } else if (defaultValues instanceof Double doubleVal) {
                    ModConfigSpec.DoubleValue value = builder.defineInRange(configOption.key(), doubleVal, hasRange ? range.min() : Double.MIN_VALUE, hasRange ? range.max() : Double.MAX_VALUE);
                    CONFIG_VALUES.put(key, value);
                } else if (defaultValues instanceof Boolean boolValue) {
                    ModConfigSpec.BooleanValue value = builder.define(configOption.key(), (boolean) boolValue);
                    CONFIG_VALUES.put(key, value);
                } else if (field.getType().isEnum()) {
                    //noinspection unchecked,rawtypes -> ignored
                    ModConfigSpec.EnumValue<?> value = builder.defineEnum(configOption.key(), (Enum) defaultValues, ((Enum<?>) defaultValues).getClass().getEnumConstants());
                    CONFIG_VALUES.put(key, value);
                } else if (defaultValues instanceof List<?> list) {
                    // By default, lists are not allowed to be empty, so we define the range manually here.
                    ModConfigSpec.Range<Integer> sizeRange = ModConfigSpec.Range.of(0, Integer.MAX_VALUE);
                    ModConfigSpec.ConfigValue<List<?>> configList = null;

                    boolean handledList = false;

                    // Convert custom config list to a string-based list for the 'ModConfig$ConfigValue' field
                    if (field.getGenericType() instanceof ParameterizedType listParameter) { // Get the type parameter from List<CustomConfig>
                        String className = listParameter.getActualTypeArguments()[0].getTypeName();

                        try {
                            Class<?> customConfigType = Class.forName(className);

                            if (CustomConfig.class.isAssignableFrom(customConfigType)) {
                                //noinspection unchecked -> ignore, type is safe
                                List<CustomConfig> customList = (List<CustomConfig>) list;

                                configList = builder.defineList(
                                        List.of(configOption.key()),
                                        () -> customList.stream().map(CustomConfig::convert).toList(),
                                        () -> getDefaultListValueForConfig(configOption.key()),
                                        configValue -> field.isAnnotationPresent(IgnoreConfigCheck.class) || CustomConfig.validate(customConfigType, configValue),
                                        sizeRange
                                );

                                handledList = true;
                            }
                        } catch (ClassNotFoundException exception) {
                            DragonSurvivalMod.LOGGER.error("A problem occurred while trying to handle the config [{}]", configOption.key(), exception);
                        }
                    }

                    if (!handledList) {
                        configList = builder.defineList(
                                List.of(configOption.key()),
                                () -> list,
                                () -> getDefaultListValueForConfig(configOption.key()),
                                configValue -> field.isAnnotationPresent(IgnoreConfigCheck.class) || checkConfig(configOption, configValue),
                                sizeRange
                        );
                    }

                    CONFIG_VALUES.put(key, configList);
                } else if (defaultValues instanceof CustomConfig customConfig) {
                    ModConfigSpec.ConfigValue<String> value = builder.define(configOption.key(), customConfig.convert());
                    CONFIG_VALUES.put(key, value);
                } else {
                    // This will likely run into a 'com.electronwill.nightconfig.core.io.WritingException: Unsupported value type' exception
                    ModConfigSpec.ConfigValue<Object> value = builder.define(configOption.key(), defaultValues);
                    CONFIG_VALUES.put(key, value);
                    DragonSurvivalMod.LOGGER.warn("Potential issue found for configuration: [{}]", configOption.key());
                }
            } catch (Exception e) {
                DragonSurvivalMod.LOGGER.error("Invalid configuration found: [{}]", configOption.key(), e);
            }

            for (int i = 0; i < categories.length; i++) {
                builder.pop();
            }
        }
    }

    public static boolean checkConfig(final ConfigOption configOption, final Object configValue) {
        return checkConfig(configOption.key(), configValue);
    }

    public static boolean checkConfig(final String key, final Object configValue) {
        return checkSpecific(key, configValue);
    }

    public static String getDefaultListValueForConfig(final String key) {
        return switch (key) {
            // Food options
            case "caveDragonFoods", "forestDragonFoods", "seaDragonFoods" -> "minecraft:empty:0:0";
            // Hurtful items
            case "hurtfulToCaveDragon", "hurtfulToForestDragon", "hurtfulToSeaDragon" -> "minecraft:empty:0";


            // Blacklisted Slots
            case "blacklistedSlots" -> "0";

            // Dirt transformations (forest dragon breath)
            case "dirtTransformationBlocks" -> "minecraft:empty:0";
            default -> "minecraft:empty";
        };
    }

    /**
     * More specific checks depending on the config type
     */
    public static boolean checkSpecific(final String configKey, final Object configValue) {
        // Food options
        switch (configKey) {
            case "caveDragonFoods", "forestDragonFoods", "seaDragonFoods" -> {
                if (configValue instanceof String string) {
                    // namespace:item_id:hunger:saturation
                    String[] split = string.split(":");

                    if (split.length == 2) {
                        return ResourceLocation.tryParse(string) != null;
                    } else if (split.length == 4) {
                        return ResourceLocation.tryParse(split[0] + ":" + split[1]) != null;
                    }
                }

                return false;
            }
            case "hurtfulToCaveDragon", "hurtfulToForestDragon", "hurtfulToSeaDragon" -> {
                if (configValue instanceof String string) {
                    // namespace:item_id:damage
                    String[] split = string.split(":");

                    if (split.length == 3) {
                        return ResourceLocation.tryParse(split[0] + ":" + split[1]) != null;
                    }
                }

                return false;
            }

            // Blacklisted Slots
            case "blacklistedSlots" -> {
                try {
                    Integer.parseInt(String.valueOf(configValue));
                } catch (NumberFormatException e) {
                    return false;
                }

                return true;
            }

            // Dirt transformations (forest dragon breath)
            case "dirtTransformationBlocks" -> {
                if (configValue instanceof String string) {
                    String[] data = string.split(":");

                    if (data.length == 3) {
                        return isInteger(data[2]) && ResourceLocation.tryParse(data[0] + ":" + data[1]) != null;
                    }

                    return false;
                }
            }
        }

        String string = String.valueOf(configValue);

        if (string.split(":").length == 2) {
            return ResourceLocation.tryParse(string) != null;
        }

        return false;
    }

    private static boolean isInteger(final String string) {
        try {
            Integer.parseInt(String.valueOf(string));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * If {@link ConfigType} is used then said config entries will go through here <br>
     * This also means it cannot be used if the config entries contain additional information (e.g. like the food configs)
     * @param registry Registry to check data for
     * @param location Value to parse
     * @param <T>      Types which can be used in a registry (e.g. Item or Block)
     * @return Either a list of the resolved tag or the resource element
     */
    public static <T> List<T> parseResourceLocation(@NotNull final Registry<T> registry, final String location) {
        // There are configuration which have additional information after the resource location (e.g. food configuration)
        ResourceLocation resourceLocation = ResourceLocation.tryParse(location);

        if (resourceLocation == null) {
            // Only split the namespace from the (potential) regex path
            String[] splitLocation = location.split(":", 1);

            if (splitLocation.length < 2) {
                return List.of();
            }

            // Try parsing regex if it's not a valid resource location
            List<T> list = new ArrayList<>();

            registry.registryKeySet().forEach((key) -> {
                ResourceLocation keyLocation = key.location();

                if (keyLocation.getNamespace().equals(splitLocation[0])) {
                    Pattern pattern = Pattern.compile(splitLocation[1]);

                    Matcher matcher = pattern.matcher(keyLocation.getPath());

                    if (matcher.matches()) {
                        list.add(registry.get(key));
                    }
                }
            });

            return list;
        }

        if (registry.containsKey(resourceLocation)) {
            Optional<Holder.Reference<T>> optional = registry.getHolder(resourceLocation);

            if (optional.isPresent() && optional.get().isBound()) {
                return List.of(optional.get().value());
            }
        } else {
            Optional<TagKey<T>> tag = registry.getTagNames().filter(registryTag -> registryTag.location().equals(resourceLocation)).findAny();

            if (tag.isPresent()) {
                List<T> list = new ArrayList<>();

                registry.holders().forEach(holder -> holder.tags().forEach(
                                holderTag -> {
                                    if (tag.get().equals(holderTag)) {
                                        list.add(holder.value());
                                    }
                                }
                        )
                );

                return list;
            }
        }

        return List.of();
    }

    /**
     * @param field The class field which dictates the type to set
     * @param value The value (from {@link net.neoforged.neoforge.common.ModConfigSpec.ConfigValue}) which will be converted to the class field type
     * @param registryType (Optional) The type of registry object (e.g. {@link Block})
     * @return The converted value for the field
     */
    @SuppressWarnings({"unchecked", "rawtypes"}) // should be fine
    private static @Nullable Object convertToFieldValue(final Field field, final Object value, @Nullable final Class<?> registryType) {
        if (field.getGenericType() instanceof ParameterizedType listParameter) {
            try {
                Class<?> classType = Class.forName(listParameter.getActualTypeArguments()[0].getTypeName());

                // Check for string since the list itself goes through here as well
                if (CustomConfig.class.isAssignableFrom(classType) && value instanceof String string) {
                    return CustomConfig.parse(classType, string);
                }
            } catch (ClassNotFoundException exception) {
                DragonSurvivalMod.LOGGER.error("A problem occurred while trying to parse a custom config entry: {}", value);
            }
        }

        if (value instanceof String string) {
            if (field.getType().isEnum()) {
                Class<? extends Enum> cs = (Class<? extends Enum<?>>) field.getType();
                return EnumGetMethod.ORDINAL_OR_NAME.get(value, cs);
            }

            Registry<?> registry = REGISTRY_MAP.get(registryType);

            if (registry != null) {
                List<?> list = parseResourceLocation(registry, string);

                if (list != null) {
                    if (field.getGenericType() instanceof List<?>) {
                        return list.isEmpty() ? List.of(string) : list;
                    } else {
                        return list.isEmpty() ? null : string;
                    }
                }
            }
        }

        if (value instanceof Number number) {
            if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                return number.doubleValue();
            }

            if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                return number.intValue();
            }

            if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                return number.longValue();
            }

            if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                return number.floatValue();
            }

            return number;
        }

        return value;
    }

    @SubscribeEvent
    public static void handleConfigLoading(final ModConfigEvent.Loading event) {
        handleConfigChange(event.getConfig().getType());
    }

    @SubscribeEvent
    public static void handleConfigReloading(final ModConfigEvent.Reloading event) {
        handleConfigChange(event.getConfig().getType());
    }

    /**
     * Sets the values of the config fields
     */
    public static void handleConfigChange(final ModConfig.Type type) {
        ConfigSide side = type == ModConfig.Type.SERVER ? ConfigSide.SERVER : ConfigSide.CLIENT;
        Set<String> configKeys = CONFIG_KEYS.get(side);

        for (String configKey : configKeys) {
            try {
                if (CONFIG_VALUES.containsKey(configKey) && CONFIG_FIELDS.containsKey(configKey)) {
                    Field field = ConfigHandler.CONFIG_FIELDS.get(configKey);

                    if (field != null) {
                        Object value = convertToFieldValue(field, configKey);

                        if (value != null) {
                            field.set(null, value);
                        }
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException exception) {
                DragonSurvivalMod.LOGGER.error("An error occurred while setting the config [{}]", configKey, exception);
            }
        }

        if (type == ModConfig.Type.SERVER) {
            DragonFoodHandler.rebuildFoodMap();

            // Technically only relevant if the config spec belongs to us
            DragonConfigHandler.rebuildSeaDragonConfigs();
            DragonConfigHandler.rebuildBreathBlocks();
            DragonConfigHandler.rebuildManaBlocks();
            DragonConfigHandler.rebuildForestDragonConfigs();
            DragonConfigHandler.rebuildBlacklistedItems();
        }
    }

    /**
     * Update the {@link ModConfigSpec.ConfigValue} and class field with the new value <br>
     * (Currently only used for the ui when enabling / disabling claws e.g.)
     * @param configKey The config key of the {@link ConfigOption}
     * @param newValue Thew value that will be set
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void updateConfigValue(final String configKey, final Object newValue) {
        ModConfigSpec.ConfigValue valueHolder = CONFIG_VALUES.get(configKey);

        if (valueHolder == null) {
            DragonSurvivalMod.LOGGER.error("Could not set the config value for [{}]", configKey);
            return;
        }

        // Set the value for the (hidden) config value field
        valueHolder.set(convertToConfigValue(newValue));
        valueHolder.save();

        try {
            // Set the value for the class field
            Field field = ConfigHandler.CONFIG_FIELDS.get(configKey);

            if (newValue != null) {
                field.set(null, newValue);
            } else {
                DragonSurvivalMod.LOGGER.error("Tried to update [{}] with a 'null' value", configKey);
            }
        } catch (IllegalAccessException | IllegalArgumentException | NullPointerException exception) {
            DragonSurvivalMod.LOGGER.error("An error occurred while trying to update the config [{}] with the value [{}]", configKey, newValue, exception);
        }
    }

    /**
     * Get the relevant data that is supposed to be stored in the {@link ModConfigSpec.ConfigValue} field
     * @return The result of {@link ConfigHandler#getRelevantConfigValue(Object)} (lists will convert their entries using that method)
     */
    private static Object convertToConfigValue(final Object object) {
        Object result;

        if (object instanceof Collection<?> collection) {
            Collection<Object> list = new ArrayList<>();

            for (Object listElement : collection) {
                list.add(getRelevantConfigValue(listElement));
            }

            result = list;
        } else {
            result = getRelevantConfigValue(object);
        }

        return result;
    }

    /**
     * Get the relevant data that is supposed to be stored in the {@link ModConfigSpec.ConfigValue} field <br>
     * @return Most likely a string or number value
     */
    @SuppressWarnings("deprecation") // ignore
    private static Object getRelevantConfigValue(final Object object) {
        if (object instanceof Registry<?> registry) {
            return registry.key().location();
        }

        if (object instanceof CustomConfig customConfig) {
            return customConfig.convert();
        }

        if (object instanceof Enum<?> enumValue) {
            return enumValue.name();
        }

        if (object instanceof Item item) {
            return item.builtInRegistryHolder().key().location();
        }

        if (object instanceof Block block) {
            return block.builtInRegistryHolder().key().location();
        }

        return object;
    }

    /**
     * Retrieves the current config value (from {@link ModConfigSpec.ConfigValue}) <br>
     * Said value will then be converted to match the class field <br>
     * See {@link ConfigHandler#convertToFieldValue(Field, Object, Class)} for more information
     */
    private static @Nullable Object convertToFieldValue(final Field field, final String configKey) throws IllegalAccessException {
        Object configValue = CONFIG_VALUES.get(configKey).get();
        ConfigType configType = CONFIG_TYPES.get(configKey);
        Class<?> registryType = configType != null ? configType.value() : null;

        Object result;

        if (Collection.class.isAssignableFrom(field.getType())) {
            Collection<?> collection = (Collection<?>) configValue;
            ArrayList<Object> resultList = new ArrayList<>();

            for (Object listValue : collection) {
                Object value = convertToFieldValue(field, listValue, registryType);

                // Could be null if the registry entry is not present (e.g. certain mod is not loaded)
                if (value != null) {
                    resultList.add(value);
                }
            }

            result = resultList;
        } else if (CustomConfig.class.isAssignableFrom(field.getType())) {
            result = CustomConfig.parse(field.getType(), (String) configValue);
        } else {
            result = configValue;
        }

        return convertToFieldValue(field, result, registryType);
    }

    /**
     * @param type   Class of the resource type
     * @param values Resource locations
     * @param <T>    Types which can be used in a registry (e.g. Item or Block)
     * @return HashSet of the resource element and the resolved tag
     */
    @SuppressWarnings("unchecked") // should be fine
    public static <T> HashSet<T> getResourceElements(final Class<T> type, final List<String> values) {
        Registry<T> registry = (Registry<T>) REGISTRY_MAP.getOrDefault(type, null);
        HashSet<T> hashSet = new HashSet<>();

        for (String rawResourceLocation : values) {
            if (rawResourceLocation == null) {
                continue;
            }

            hashSet.addAll(parseResourceLocation(registry, rawResourceLocation));
        }

        return hashSet;
    }

    private static String createConfigPath(final ConfigOption configOption) {
        return createConfigPath(configOption.category(), configOption.key());
    }

    private static String createConfigPath(final String[] category, final String key) {
        StringBuilder path = new StringBuilder();

        for (String pathElement : category) {
            path.append(pathElement).append(".");
        }

        path.append(key);

        return path.toString();
    }
}