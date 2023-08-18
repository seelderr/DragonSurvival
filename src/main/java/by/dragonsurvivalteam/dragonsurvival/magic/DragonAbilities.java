package by.dragonsurvivalteam.dragonsurvival.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncMagicCap;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.network.PacketDistributor;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public class DragonAbilities{
	public static HashMap<String, ArrayList<DragonAbility>> ABILITIES = new HashMap<>();
	public static HashMap<String, ArrayList<ActiveDragonAbility>> ACTIVE_ABILITIES = new HashMap<>();
	public static HashMap<String, ArrayList<PassiveDragonAbility>> PASSIVE_ABILITIES = new HashMap<>();
	public static HashMap<String, ArrayList<InnateDragonAbility>> INNATE_ABILITIES = new HashMap<>();
	public static HashMap<String, DragonAbility> ABILITY_LOOKUP = new HashMap<>();

	public static void initAbilities(){
		List<DragonAbility> abs = getInstances(RegisterDragonAbility.class, DragonAbility.class);

		for(DragonAbility ability : abs){
			if(ability == null) continue;
			ABILITY_LOOKUP.put(ability.getName(), ability);

			if(ability.getDragonType() == null){
				for(String type : DragonTypes.getTypes()){
					ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
					ABILITIES.get(type).add(ability);

					if(ability instanceof InnateDragonAbility ab){
						INNATE_ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
						INNATE_ABILITIES.get(type).add(ab);

					}else if(ability instanceof PassiveDragonAbility ab){
						PASSIVE_ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
						PASSIVE_ABILITIES.get(type).add(ab);

					}else if(ability instanceof ActiveDragonAbility ab){
						ACTIVE_ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
						ACTIVE_ABILITIES.get(type).add(ab);
					}
				}
			}else{
				ABILITIES.computeIfAbsent(ability.getDragonType().getTypeName(), s -> new ArrayList<>());
				ABILITIES.get(ability.getDragonType().getTypeName()).add(ability);

				if(ability instanceof InnateDragonAbility ab){
					INNATE_ABILITIES.computeIfAbsent(ability.getDragonType().getTypeName(), s -> new ArrayList<>());
					INNATE_ABILITIES.get(ability.getDragonType().getTypeName()).add(ab);
				}else if(ability instanceof PassiveDragonAbility ab){
					PASSIVE_ABILITIES.computeIfAbsent(ability.getDragonType().getTypeName(), s -> new ArrayList<>());
					PASSIVE_ABILITIES.get(ability.getDragonType().getTypeName()).add(ab);
				}else if(ability instanceof ActiveDragonAbility ab){
					ACTIVE_ABILITIES.computeIfAbsent(ability.getDragonType().getTypeName(), s -> new ArrayList<>());
					ACTIVE_ABILITIES.get(ability.getDragonType().getTypeName()).add(ab);
				}
			}
		}
		ABILITIES.forEach((key, value) -> value.sort(Comparator.comparingInt(DragonAbility::getSortOrder)));
		INNATE_ABILITIES.forEach((key, value) -> value.sort(Comparator.comparingInt(DragonAbility::getSortOrder)));
		PASSIVE_ABILITIES.forEach((key, value) -> value.sort(Comparator.comparingInt(DragonAbility::getSortOrder)));
		ACTIVE_ABILITIES.forEach((key, value) -> value.sort(Comparator.comparingInt(DragonAbility::getSortOrder)));
	}

	@SuppressWarnings("SameParameterValue")
	private static <T> List<T> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
		Type annotationType = Type.getType(annotationClass);
		List<ModFileScanData> allScanData = ModList.get().getAllScanData();
		Set<String> pluginClassNames = new LinkedHashSet<>();
		for (ModFileScanData scanData : allScanData) {
			Iterable<ModFileScanData.AnnotationData> annotations = scanData.getAnnotations();
			for (ModFileScanData.AnnotationData a : annotations) {
				if (Objects.equals(a.annotationType(), annotationType)) {
					String memberName = a.memberName();
					pluginClassNames.add(memberName);
				}
			}
		}
		List<T> instances = new ArrayList<>();
		for (String className : pluginClassNames) {
			try {
				Class<?> asmClass = Class.forName(className);
				Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
				Constructor<? extends T> constructor = asmInstanceClass.getDeclaredConstructor();
				T instance = constructor.newInstance();
				instances.add(instance);
			} catch (ReflectiveOperationException | LinkageError e) {
				e.printStackTrace();
			}
		}
		return instances;
	}

	public static void addAbility(LivingEntity player, Class<? extends DragonAbility> c){
		DragonStateHandler handler = DragonUtils.getHandler(player);

		try{
			DragonAbility ability = c.newInstance();

			if(player instanceof Player p){
				ability.player = p;
			}

			handler.getMagicData().abilities.put(ability.getName(), ability);

			if(player.level.isClientSide){
				NetworkHandler.CHANNEL.sendToServer(new SyncMagicCap(player.getId(), handler.getMagicData()));
			}else{
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicCap(player.getId(), handler.getMagicData()));
			}
		}catch(InstantiationException | IllegalAccessException e){
			throw new RuntimeException(e);
		}
	}

	public static void setAbilityLevel(LivingEntity player, Class<? extends DragonAbility> c, int level){
		if(!hasAbility(player, c)){
			addAbility(player, c);
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);
		handler.getMagicData().abilities.values().stream().filter(s-> s.getClass() == c).forEach(s -> {
			s.level = Mth.clamp(level, s.getMinLevel(), s.getMaxLevel());
		});

		if(player.level.isClientSide){
			NetworkHandler.CHANNEL.sendToServer(new SyncMagicCap(player.getId(), handler.getMagicData()));
		}else{
			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new SyncMagicCap(player.getId(), handler.getMagicData()));
		}
	}

	public static boolean hasAbility(LivingEntity player, Class<? extends DragonAbility> c, @Nullable String dragonType) {
		DragonStateHandler handler = DragonUtils.getHandler(player);
		return handler.getMagicData().abilities.values().stream().anyMatch(s-> {
			if (s.getClass() != c && !s.getClass().isAssignableFrom(c) && !c.isAssignableFrom(s.getClass()))
				return false;
			if (dragonType != null && !dragonType.equals(s.getDragonType().getTypeName()))
				return false;
			return true;
		});
	}
	public static boolean hasAbility(LivingEntity player, Class<? extends DragonAbility> c) {
		return hasAbility(player, c, null);
	}
	public static boolean hasSelfAbility(LivingEntity player, Class<? extends DragonAbility> c) {
		AbstractDragonType dragonType = DragonUtils.getHandler(player).getType();
		if (dragonType == null)
			return hasAbility(player, c, null);
		else
			return hasAbility(player, c, dragonType.getTypeName());
	}
	public static <T extends DragonAbility> T getAbility(LivingEntity player, Class<T> c, @Nullable String dragonType){
		DragonStateHandler handler = DragonUtils.getHandler(player);
		Optional<T> optionalT = (Optional<T>)handler.getMagicData().abilities.values().stream().filter(s-> {
			if (s.getClass() != c && !s.getClass().isAssignableFrom(c) && !c.isAssignableFrom(s.getClass()))
				return false;
			if (dragonType != null && !dragonType.equals(s.getDragonType().getTypeName()))
				return false;
			return true;
		}).findAny();
		return optionalT.orElseGet(() -> {
			if(Modifier.isAbstract(c.getModifiers())) return null;
			try{
				return c.newInstance();
			}catch(InstantiationException | IllegalAccessException e){
				throw new RuntimeException(e);
			}
		});
	}
	public static <T extends DragonAbility> T getAbility(LivingEntity player, Class<T> c) {
		return getAbility(player, c, null);
	}
	public static <T extends DragonAbility> T getSelfAbility(LivingEntity player, Class<T> c) {
		AbstractDragonType dragonType = DragonUtils.getHandler(player).getType();
		if (dragonType == null)
			return getAbility(player, c, null);
		else
			return getAbility(player, c, dragonType.getTypeName());
	}

	public static AABB calculateHitRange(final Player player, double range) {
		Vec3 viewVector = player.getLookAngle().scale(range);
		// TODO :: Change depending on dragon level
		double defaultRadius = 1;

		double xOffset = getOffset(viewVector.x(), defaultRadius);
		// TODO :: Fix accuracy when player starts to look down (e.g. don't immediately add the eye height, or at all?)
		double yOffset = player.getEyeHeight() + Math.abs(viewVector.y());
		double zOffset = getOffset(viewVector.z(), defaultRadius);

		/*
		north: positive z
		east: positive x
		west: negative x
		south: positive z
		*/
		Direction direction = player.getDirection();

		// Check for look angle to avoid extending the range in the direction the player is not facing / looking
		double xMin = (direction == Direction.EAST ? 0 : player.getLookAngle().x() < 0 ? xOffset : defaultRadius);
		double zMin = (direction == Direction.SOUTH ? 0 : player.getLookAngle().z() < 0 ? zOffset : defaultRadius);

		double xMax = (direction == Direction.WEST ? 0 : player.getLookAngle().x() > 0 ? xOffset : defaultRadius);
		double zMax = (direction == Direction.NORTH ? 0 : player.getLookAngle().z() > 0 ? zOffset : defaultRadius);

		return new AABB(
				player.getX() - Math.abs(xMin),
				player.getY() - Math.abs(player.getLookAngle().y < 0 ? yOffset : 0), // Only increase when player is looking down
				player.getZ() - Math.abs(zMin),
				player.getX() + Math.abs(xMax),
				player.getY() + Math.abs(player.getLookAngle().y > 0 ? yOffset : player.getEyeHeight()), // Only increase when player is looking up
				player.getZ() + Math.abs(zMax)
		);
	}

	private static double getOffset(double value, double defaultValue) {
		if (value < 0) {
			return Math.min(value, -defaultValue);
		}

		return Math.max(value, defaultValue);
	}
}