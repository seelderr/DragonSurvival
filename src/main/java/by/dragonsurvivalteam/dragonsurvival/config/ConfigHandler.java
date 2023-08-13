package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.config.obj.*;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.primitives.Primitives;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation.EnumHolder;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

@EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Bus.MOD )
public class ConfigHandler{

	public static ClientConfig CLIENT;
	public static ForgeConfigSpec clientSpec;
	public static ServerConfig SERVER;
	public static ForgeConfigSpec serverSpec;

	public static HashMap<String, Object> defaultConfigValues = new HashMap<>(); // Contains the default values
	public static HashMap<String, ConfigOption> configObjects = new HashMap<>(); // Contains config options
	public static HashMap<String, Field> configFields = new HashMap<>(); // Contains all annotated config fields
	public static HashMap<ConfigSide, List<String>> configs = new HashMap<>(); // Contains all config keys per side
	public static HashMap<String, ForgeConfigSpec.ConfigValue<?>> configValues = new HashMap<>(); // contains all config values

	private static final HashMap<Class<?>, Tuple<Supplier<IForgeRegistry<?>>, Supplier<ResourceKey<? extends Registry<?>>>>> REGISTRY_HASH_MAP = new HashMap<>();

	public static void initTypes() {
		REGISTRY_HASH_MAP.put(Item.class, new Tuple<>(() -> ForgeRegistries.ITEMS, () -> Registry.ITEM_REGISTRY));
		REGISTRY_HASH_MAP.put(Block.class,  new Tuple<>(() -> ForgeRegistries.BLOCKS, () -> Registry.BLOCK_REGISTRY));
		REGISTRY_HASH_MAP.put(EntityType.class,  new Tuple<>(() -> ForgeRegistries.ENTITY_TYPES, () -> Registry.ENTITY_TYPE_REGISTRY));
		REGISTRY_HASH_MAP.put(BlockEntityType.class, new Tuple<>(() -> ForgeRegistries.BLOCK_ENTITY_TYPES, () -> Registry.BLOCK_ENTITY_TYPE_REGISTRY));
		REGISTRY_HASH_MAP.put(Biome.class,  new Tuple<>(() -> ForgeRegistries.BIOMES, () -> Registry.BIOME_REGISTRY));
		REGISTRY_HASH_MAP.put(MobEffect.class,  new Tuple<>(() -> ForgeRegistries.MOB_EFFECTS, () -> Registry.MOB_EFFECT_REGISTRY));
		REGISTRY_HASH_MAP.put(Potion.class,  new Tuple<>(() -> ForgeRegistries.POTIONS, () -> Registry.POTION_REGISTRY));
	}

	private static List<Field> getFields() {
		List<Field> instances = new ArrayList<>();

		Type annotationType = Type.getType(ConfigOption.class);
		ModList.get().getAllScanData().forEach(s -> {
			List<ModFileScanData.AnnotationData> ebsTargets = s.getAnnotations().stream().filter(s1 -> s1.targetType() == ElementType.FIELD).filter(annotationData -> annotationType.equals(annotationData.annotationType())).toList();

			ebsTargets.forEach(ad ->  {
				EnumHolder sidesValue = (EnumHolder)ad.annotationData().get("side");
				Dist side = Objects.equals(sidesValue.getValue(), "CLIENT") ? Dist.CLIENT : Dist.DEDICATED_SERVER;

				if(side == FMLEnvironment.dist || side == Dist.DEDICATED_SERVER){
					try{
						Class<?> c = Class.forName(ad.clazz().getClassName());
						Field fe = c.getDeclaredField(ad.memberName());
						instances.add(fe);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		});
		return instances;
	}

	public static void initConfig(){
		initTypes();
		List<Field> set = getFields();

		set.forEach(s -> {
			if(!Modifier.isStatic(s.getModifiers()))
				return;

			ConfigOption option = s.getAnnotation(ConfigOption.class);

			try{
				defaultConfigValues.put(option.key(), s.get(null));
			}catch(IllegalAccessException e){
				e.printStackTrace();
			}
			configFields.put(option.key(), s);
			configObjects.put(option.key(), option);

			configs.computeIfAbsent(option.side(), c -> new ArrayList<>());
			configs.get(option.side()).add(option.key());
		});

		if(FMLEnvironment.dist.isClient()){
			Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
			CLIENT = client.getLeft();
			clientSpec = client.getRight();
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
		}

		Pair<ServerConfig, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		SERVER = server.getLeft();
		serverSpec = server.getRight();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
	}

	public static void addConfigs(final ForgeConfigSpec.Builder builder, final ConfigSide side) {
		for (String key : configs.getOrDefault(side, Collections.emptyList())) {
			ConfigOption option = configObjects.get(key);
			Field field = configFields.get(key);
			Object defaultValues = defaultConfigValues.get(option.key());

			// Get the category - if none is present put it in the `general` category
			String[] categories = option.category() != null && option.category().length > 0 ? option.category() : new String[]{"general"};
			String[] comment = option.comment() != null ? option.comment() : new String[0];

			for (String category : categories) {
				builder.push(category);
			}

			builder.comment(comment);

			if (!option.localization().isBlank()) {
				builder.translation(option.localization());
			}

			try {
				Object tt = Primitives.isWrapperType(defaultValues.getClass()) ? Primitives.wrap(defaultValues.getClass()).cast(defaultValues) : defaultValues;

				ConfigRange range = field.isAnnotationPresent(ConfigRange.class) ? field.getAnnotation(ConfigRange.class) : null;
				boolean rang = range != null;

				// Fill the configuration options (define the key, default value and predicate to check if the option is valid)
				if (tt instanceof Integer intVal) {
					IntValue value = builder.defineInRange(option.key(), intVal, rang ? (int) range.min() : Integer.MIN_VALUE, rang ? (int) range.max() : Integer.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Float floatVal) {
					DoubleValue value = builder.defineInRange(option.key(), floatVal, rang ? range.min() : Float.MIN_VALUE, rang ? range.max() : Float.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Long longVal) {
					LongValue value = builder.defineInRange(option.key(), longVal, rang ? (long) range.min() : Long.MIN_VALUE, rang ? (long) range.max() : Long.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Double doubleVal) {
					DoubleValue value = builder.defineInRange(option.key(), doubleVal, rang ? range.min() : Double.MIN_VALUE, rang ? range.max() : Double.MAX_VALUE);
					configValues.put(key, value);
				} else if (tt instanceof Boolean boolValue) {
					BooleanValue value = builder.define(option.key(), (boolean) boolValue);
					configValues.put(key, value);
				} else if (field.getType().isEnum()) {
					EnumValue value = builder.defineEnum(option.key(), (Enum)defaultValues, ((Enum<?>)defaultValues).getClass().getEnumConstants());
					configValues.put(key, value);
				} else if(tt instanceof List<?> list) { // TODO :: Numeric lists?
					ConfigValue<List<?>> value = builder.defineList(option.key(), list, configValue -> field.isAnnotationPresent(IgnoreConfigCheck.class) || checkConfig(option, configValue));
					configValues.put(key, value);
				} else {
					ConfigValue<Object> value = builder.define(option.key(), defaultValues);
					configValues.put(key, value);
					DragonSurvivalMod.LOGGER.warn("Potential issue found for configuration: [" + option.key() + "]");
				}
			} catch (Exception e) {
				DragonSurvivalMod.LOGGER.error("Invalid configuration found: [" + option.key() + "]", e);
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

	/** More specific checks depending on the config type */
	public static boolean checkSpecific(final String key, final Object configValue) {
		// Food options
		if (key.equals("caveDragon") || key.equals("forestDragon") || key.equals("seaDragon")) {
			if (configValue instanceof String string) {
				// namespace:item_id:hunter:saturation
				String[] split = string.split(":");

				if (split.length == 2) {
					return ResourceLocation.isValidResourceLocation(string);
				} else if (split.length == 4) {
					return ResourceLocation.isValidResourceLocation(split[0] + ":" + split[1]);
				}
			}

			return false;
		}

		if (key.equals("hurtfulToCaveDragon") || key.equals("hurtfulToForestDragon") || key.equals("hurtfulToSeaDragon")) {
			if (configValue instanceof String string) {
				// namespace:item_id:damage
				String[] split = string.split(":");

				if (split.length == 3) {
					return ResourceLocation.isValidResourceLocation(split[0] + ":" + split[1]);
				}
			}

			return false;
		}

		// Blacklist Item Regex
		if (key.equals("blacklistedItemsRegex")) {
			if (configValue instanceof String string) {
				// namespace:regex
				// TODO :: Handle `:` or `"` in some way for regex purposes?
				int length = string.split(":").length;
				return length == 2;
			}

			return false;
		}

		// Blacklisted Slots
		if (key.equals("blacklistedSlots")) {
			try {
				Integer.parseInt(String.valueOf(configValue));
			} catch (NumberFormatException e) {
				return false;
			}

			return true;
		}

		String string = String.valueOf(configValue);

		if (string.split(":").length == 2) {
			// Simply checking for this at the start is unsafe since `awtaj` is a valid resource location but can cause problems if it's just some gibberish
			return ResourceLocation.isValidResourceLocation(string);
		}

		return false;
	}

	private static Object convertObject(Object ob){
		if(ob instanceof IForgeRegistry<?> forge){
			return forge.getRegistryName().toString();
		}

		if(ob instanceof Enum<?>){
			return ((Enum<?>)ob).name();
		}

		return ob;
	}

	public static <T> List<T> parseResourceLocation(final Class<T> type, final ResourceLocation location) {
		Tuple<Supplier<IForgeRegistry<?>>, Supplier<ResourceKey<? extends Registry<?>>>> registry = REGISTRY_HASH_MAP.getOrDefault(type, null);

		if (registry != null) {
			if (registry.getA().get().containsKey(location)) {
				Optional<? extends Holder<?>> optional = registry.getA().get().getHolder(location);

				if (optional.isPresent() && optional.get().isBound()) {
					return List.of((T) optional.get().value());
				}
			} else {
				TagKey<T> tagKey = TagKey.create((ResourceKey<? extends Registry<T>>) registry.getB().get(), location);

				if (tagKey.isFor(registry.getA().get().getRegistryKey())) {
					ITagManager<T> manager = (ITagManager<T>) registry.getA().get().tags();
					return manager.getTag(tagKey).stream().toList();
				}
			}
		}

		return Collections.emptyList();
	}

	private static Object getRelevantValue(final Field field, final Object object) {
		if (object instanceof String stringValue) {
			if (field.getType().isEnum()) {
				Class<? extends Enum> cs = (Class<? extends Enum>) field.getType();
				return EnumGetMethod.ORDINAL_OR_NAME.get(object, cs);
			}

			ResourceLocation location = ResourceLocation.tryParse(stringValue);
			List<?> list = parseResourceLocation((Class<? extends IForgeRegistry>) field.getType(), location);

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
	public static void onModConfig(final ModConfigEvent event) {
		onModConfig(event.getConfig().getType());
	}

	/** Sets the values of the config fields */
	public static void onModConfig(final ModConfig.Type type) {
		ConfigSide side = type == ModConfig.Type.SERVER ? ConfigSide.SERVER : ConfigSide.CLIENT;
		List<String> configList = configs.get(side);

		for (String config : configList) {
			try {
				if (configValues.containsKey(config) && configFields.containsKey(config)) {
					Field field = ConfigHandler.configFields.get(config);

					if (field != null) {
						Object object = convertFromString(field, configValues.get(config).get());

						if (object != null) {
							field.set(null, object);
						}
					}
				}
			} catch (IllegalAccessException e) {
				DragonSurvivalMod.LOGGER.error("An error occured while setting the config [" + config + "]", e);
			}
		}
	}

	public static void updateConfigValue(final String configKey, final Object configValue) {
		if (configValues.containsKey(configKey)) {
			updateConfigValue(configValues.get(configKey), configValue);
		}
	}

	/** Update and save the config */
	public static void updateConfigValue(final ForgeConfigSpec.ConfigValue config, final Object configValue) {
		config.set(convertToString(configValue));
		config.save();

		ConfigHandler.configValues.entrySet().stream().filter(configList -> configList.getValue() == config).findFirst().ifPresent(configList -> {
			try {
				Field field = ConfigHandler.configFields.get(configList.getKey());

				if (field != null) {
					Object object = convertFromString(field, configValue);

					if (object != null) {
						field.set(null, object);
					}
				}
			} catch (IllegalAccessException e) {
				DragonSurvivalMod.LOGGER.error("An error occured while trying to update the config [" + config.getPath() + "] with the value [" + configValue + "]", e);
			}
		});
	}

	@Nullable
	private static Object convertToString(Object object) {
		if (object instanceof Collection<?>) {
			Collection<Object> list = new ArrayList<>();

			for (Object o : (Collection<?>) object) {
				list.add(convertObject(o));
			}

			object = list;
		} else {
			object = convertObject(object);
		}

		return object;
	}

	private static Object convertFromString(final Field field, Object object) throws IllegalAccessException {
		if (field.getType().isAssignableFrom(Collection.class)) {
			ArrayList<Object> list = new ArrayList<>();

			for (Object listValue : (Collection<?>) object) {
				list.add(getRelevantValue(field, listValue));
			}

			object = list;
		}

		return getRelevantValue(field, object);
	}

	/**
	 * @param type Class of the resource type
	 * @param values Resource locations
	 * @return List of the resource element and the resolved tag
	 * @param <T> Types which can be used in a registry (e.g. Item or Block)
	 */
	public static <T> List<T> getResourceElements(final Class<T> type, final List<?> values) {
		Tuple<Supplier<IForgeRegistry<?>>, Supplier<ResourceKey<? extends Registry<?>>>> registry = REGISTRY_HASH_MAP.getOrDefault(type, null);
		ArrayList<T> list = new ArrayList<>();

		for (Object object : values) {
			if (object == null) {
				continue;
			}

			if (object instanceof String stringValue) {
				ResourceLocation location = ResourceLocation.tryParse(stringValue);
				Optional<? extends Holder<?>> optional = registry.getA().get().getHolder(location);
				optional.ifPresent(holder -> list.add((T) holder.value()));

				TagKey<T> tagKey = TagKey.create((ResourceKey<? extends Registry<T>>) registry.getB().get(), location);

				if (tagKey.isFor(registry.getA().get().getRegistryKey())) {
					ITagManager<T> manager = (ITagManager<T>) registry.getA().get().tags();
					list.addAll(manager.getTag(tagKey).stream().toList());
				}
			}
		}

		return list;
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

		if (ItemLike.class.isAssignableFrom(checkType) || checkType == Block.class || checkType == EntityType.class || checkType == MobEffect.class || checkType == Biome.class) {
			return true;
		}

		return false;
	}
}