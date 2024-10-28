package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.config.obj.*;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.primitives.Primitives;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;
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
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

@EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigHandler {

	public static ClientConfig CLIENT;
	public static ModConfigSpec clientSpec;
	public static ServerConfig SERVER;
	public static ModConfigSpec serverSpec;

	public static HashMap<String, Object> defaultConfigValues = new HashMap<>(); // Contains the default values
	public static HashMap<String, ConfigType> configTypes = new HashMap<>(); // Contains the config types
	public static HashMap<String, ConfigOption> configObjects = new HashMap<>(); // Contains config options
	public static HashMap<String, Field> configFields = new HashMap<>(); // Contains all annotated config fields
	public static HashMap<ConfigSide, List<String>> configs = new HashMap<>(); // Contains all config keys per side
	public static HashMap<String, ModConfigSpec.ConfigValue<?>> configValues = new HashMap<>(); // contains all config values

	private static final HashMap<Class<?>, Registry<?>> REGISTRY_MAP = new HashMap<>();

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

		List<Field> fields = getFields();

		fields.forEach(field -> {
			if (!Modifier.isStatic(field.getModifiers())) {
				return;
			}

			ConfigOption configOption = field.getAnnotation(ConfigOption.class);

			try {
				defaultConfigValues.put(configOption.key(), field.get(null));
			} catch (IllegalAccessException e) {
				DragonSurvivalMod.LOGGER.error("There was a problem while trying to get the default config value of [{}]", ConfigHandler.createConfigPath(configOption), e);
			}

			configFields.put(configOption.key(), field);
			configObjects.put(configOption.key(), configOption);

			ConfigType configType = field.getAnnotation(ConfigType.class);
			if (configType != null) {
				configTypes.put(configOption.key(), configType);
			}

			configs.computeIfAbsent(configOption.side(), key -> new ArrayList<>()).add(configOption.key());
		});

		ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
		if (FMLEnvironment.dist.isClient()) {
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

	public static void addConfigs(final ModConfigSpec.Builder builder, final ConfigSide side) {
		for (String key : configs.getOrDefault(side, Collections.emptyList())) {
			ConfigOption configOption = configObjects.get(key);
			Field field = configFields.get(key);
			Object defaultValues = defaultConfigValues.get(configOption.key());

			// Get the category - if none is present put it in the `general` category
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
				Object tt = Primitives.isWrapperType(defaultValues.getClass()) ? Primitives.wrap(defaultValues.getClass()).cast(defaultValues) : defaultValues;

				ConfigRange range = field.isAnnotationPresent(ConfigRange.class) ? field.getAnnotation(ConfigRange.class) : null;
				boolean rang = range != null;

				// Fill the configuration options (define the key, default value and predicate to check if the option is valid)
				if (tt instanceof Integer intVal) {
					ModConfigSpec.IntValue value = builder.defineInRange(configOption.key(), intVal, rang ? (int) range.min() : Integer.MIN_VALUE, rang ? (int) range.max() : Integer.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Float floatVal) {
					ModConfigSpec.DoubleValue value = builder.defineInRange(configOption.key(), floatVal, rang ? range.min() : Float.MIN_VALUE, rang ? range.max() : Float.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Long longVal) {
					ModConfigSpec.LongValue value = builder.defineInRange(configOption.key(), longVal, rang ? (long) range.min() : Long.MIN_VALUE, rang ? (long) range.max() : Long.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Double doubleVal) {
					ModConfigSpec.DoubleValue value = builder.defineInRange(configOption.key(), doubleVal, rang ? range.min() : Double.MIN_VALUE, rang ? range.max() : Double.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Boolean boolValue) {
					ModConfigSpec.BooleanValue value = builder.define(configOption.key(), (boolean) boolValue);
					configValues.put(key, value);
				} else if (field.getType().isEnum()) {
					ModConfigSpec.EnumValue<?> value = builder.defineEnum(configOption.key(), (Enum) defaultValues, ((Enum<?>) defaultValues).getClass().getEnumConstants());
					configValues.put(key, value);
				} else if (tt instanceof List<?> list) { // TODO :: Numeric lists?
					// By default, lists are not allowed to be empty, so we define the range manually here.
					ModConfigSpec.Range<Integer> sizeRange = ModConfigSpec.Range.of(0, Integer.MAX_VALUE);
					ModConfigSpec.ConfigValue<List<?>> value = builder.defineList(List.of(configOption.key()), () -> list, () -> getDefaultListValueForConfig(configOption.key()), configValue -> field.isAnnotationPresent(IgnoreConfigCheck.class) || checkConfig(configOption, configValue), sizeRange);
					configValues.put(key, value);
				} else {
					ModConfigSpec.ConfigValue<Object> value = builder.define(configOption.key(), defaultValues);
					configValues.put(key, value);
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
	public static boolean checkSpecific(final String key, final Object configValue) {
		// TODO :: Maybe specifiy the full path?
		// Food options
		switch (key) {
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
			// Simply checking for this at the start is unsafe since `awtaj` is a valid resource location but can cause problems if it's just some gibberish
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
	 * Get the relevant string value from an object for specific types
	 */
	private static Object getRelevantString(final Object object) {
		// TODO :: Might not be used / relevant at the moment?
		if (object instanceof Registry<?> registry) {
			return registry.toString();
		}

		if (object instanceof Enum<?> enumValue) {
			return enumValue.name();
		}

		return object;
	}

	/**
	 * @param registry Registry to check data for
	 * @param location Value to parse
	 * @param <T>      Types which can be used in a registry (e.g. Item or Block)
	 * @return Either a list of the resolved tag or the resource element
	 */
	public static <T> List<T> parseResourceLocation(final Registry<T> registry, final String location) {
		ResourceLocation resourceLocation = ResourceLocation.tryParse(location);

		if (resourceLocation == null) {
			// Try parsing regex if it's not a valid resource location
			String[] split = location.split(":");

			// Just to be sure
			if (split.length != 2) {
				DragonSurvivalMod.LOGGER.warn("Regex definition for the blacklist has the wrong format: {}", location);
				return Collections.emptyList();
			}

			List<T> list = new ArrayList<>();
			registry.registryKeySet().forEach((key) -> {
				ResourceLocation keyLocation = key.location();
				if (keyLocation.getNamespace().equals(split[0])) {
					Pattern pattern = Pattern.compile(split[1]);

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
				registry.holders().forEach(
						holder -> holder.tags().forEach(
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

		return Collections.emptyList();
	}

	/**
	 * If the value is a {@link String} it can return the following:
	 * <ul>
	 *     <li>Enum value if the field is an enum</li>
	 *     <li>Resource entries from {@link ConfigHandler#parseResourceLocation(Registry, String)}</li>
	 *     <li>Otherwise just the original string value</li>
	 * </ul>
	 * <p>
	 * Otherwise, it will check if the value is a {@link Number} and return the correct value for that<br>
	 * If it's also not a number the original value will be returned
	 */
	private static Object getRelevantValue(final Field field, final Object object, final Class<?> clazz) {
		if (object instanceof String stringValue) {
			if (field.getType().isEnum()) {
				Class<? extends Enum> cs = (Class<? extends Enum>) field.getType();
				return EnumGetMethod.ORDINAL_OR_NAME.get(object, cs);
			}

			List<?> list = parseResourceLocation(REGISTRY_MAP.get(clazz), stringValue);

			if (list != null) {
				if (field.getGenericType() instanceof List<?>) {
					return list.isEmpty() ? List.of(stringValue) : list;
				} else {
					return list.isEmpty() ? null : stringValue;
				}
			}
		}

		if (object instanceof Number number) {
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

		return object;
	}

	@SubscribeEvent
	public static void onModConfig(final ModConfigEvent.Loading event) {
		onModConfig(event.getConfig().getType());
	}

	@SubscribeEvent
	public static void onModConfig(final ModConfigEvent.Reloading event) {
		onModConfig(event.getConfig().getType());
	}

	/**
	 * Sets the values of the config fields
	 */
	public static void onModConfig(final ModConfig.Type type) {
		ConfigSide side = type == ModConfig.Type.SERVER ? ConfigSide.SERVER : ConfigSide.CLIENT;
		List<String> configList = configs.get(side);

		for (String config : configList) {
			try {
				if (configValues.containsKey(config) && configFields.containsKey(config)) {
					Field field = ConfigHandler.configFields.get(config);

					if (field != null) {
						Object object = convertFromGeneric(field, config);

						if (object != null) {
							field.set(null, object);
						}
					}
				}
			} catch (IllegalAccessException e) {
				DragonSurvivalMod.LOGGER.error("An error occurred while setting the config [{}]", config, e);
			}
		}
	}

	public static void updateConfigValue(final String configKey, final Object configValue) {
		if (configValues.containsKey(configKey)) {
			updateConfigValue(configValues.get(configKey), configKey);
		}
	}

	/**
	 * Update and save the config
	 */
	public static void updateConfigValue(final ModConfigSpec.ConfigValue config, final String configKey) {
		Object configValue = convertToString(configKey);
		config.set(convertToString(configValue));
		config.save();

		ConfigHandler.configValues.entrySet().stream().filter(configList -> configList.getValue() == config).findFirst().ifPresent(configList -> {
			try {
				Field field = ConfigHandler.configFields.get(configList.getKey());

				if (field != null) {
					Object object = convertFromGeneric(field, configKey);

					if (object != null) {
						field.set(null, object);
					}
				}
			} catch (IllegalAccessException e) {
				DragonSurvivalMod.LOGGER.error("An error occurred while trying to update the config [{}] with the value [{}]", config.getPath(), configValue, e);
			}
		});
	}

    @Nullable
    private static Object convertToString(final Object object) {
		Object result;

		if (object instanceof Collection<?> collection) {
			Collection<Object> list = new ArrayList<>();

			for (Object listElement : collection) {
				list.add(getRelevantString(listElement));
			}

			result = list;
		} else {
			result = getRelevantString(object);
		}

		return result;
	}

	/**
	 * See {@link ConfigHandler#getRelevantValue(Field, Object, Class)} for more info
	 */
	private static Object convertFromGeneric(final Field field, String config) throws IllegalAccessException {
		Object result;
		Object object = configValues.get(config).get();
		ConfigType type = configTypes.get(config);
		Class clazz = null;
		if (type != null) {
			clazz = type.value();
		}

		if (field.getType().isAssignableFrom(Collection.class)) {
			ArrayList<Object> list = new ArrayList<>();

			for (Object listValue : (Collection<?>) object) {
				list.add(getRelevantValue(field, listValue, clazz));
			}

			result = list;
		} else {
			result = object;
		}

		return getRelevantValue(field, result, clazz);
	}

	/**
	 * @param type   Class of the resource type
	 * @param values Resource locations
	 * @param <T>    Types which can be used in a registry (e.g. Item or Block)
	 * @return HashSet of the resource element and the resolved tag
	 */
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

	public static String createConfigPath(final String[] category, final String key) {
		StringBuilder path = new StringBuilder();

		for (String pathElement : category) {
			path.append(pathElement).append(".");
		}

		path.append(key);

		return path.toString();
	}

	public static String createConfigPath(final ConfigOption configOption) {
		return createConfigPath(configOption.category(), configOption.key());
	}

	public static boolean isResource(final ConfigOption configOption) {
		Field field = ConfigHandler.configFields.get(configOption.key());
		Class<?> checkType = Primitives.unwrap(field.getType());

		if (field.isAnnotationPresent(ConfigType.class)) {
			ConfigType type = field.getAnnotation(ConfigType.class);
			checkType = Primitives.unwrap(type.value());
		}

		return ItemLike.class.isAssignableFrom(checkType) || checkType == Block.class || checkType == EntityType.class || checkType == MobEffect.class || checkType == Biome.class;
	}
}