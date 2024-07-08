package by.dragonsurvivalteam.dragonsurvival.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.BreathAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncMagicCap;
import by.dragonsurvivalteam.dragonsurvival.util.BlockPosHelper;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

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
				for(String type : DragonTypes.getAllSubtypes()){
					ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
					ABILITIES.get(type).add(ability);

                    switch (ability) {
                        case InnateDragonAbility innate -> {
                            INNATE_ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
                            INNATE_ABILITIES.get(type).add(innate);
                        }
                        case PassiveDragonAbility passive -> {
                            PASSIVE_ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
                            PASSIVE_ABILITIES.get(type).add(passive);
                        }
                        case ActiveDragonAbility active -> {
                            ACTIVE_ABILITIES.computeIfAbsent(type, s -> new ArrayList<>());
                            ACTIVE_ABILITIES.get(type).add(active);
                        }
                        default -> { /* Nothing to do */ }
                    }
				}
			}else{
				for (AbstractDragonType type : DragonTypes.getSubtypesOfType(ability.getDragonType().getTypeName())) {
					// Add all non-active abilities to each subtype, and active abilities only to the matching subtype.
					if (!(ability instanceof ActiveDragonAbility)) {
						ABILITIES.computeIfAbsent(type.getSubtypeName(), s -> new ArrayList<>());
						ABILITIES.get(type.getSubtypeName()).add(ability);
					}

                    switch (ability) {
                        case InnateDragonAbility innate -> {
                            INNATE_ABILITIES.computeIfAbsent(type.getSubtypeName(), s -> new ArrayList<>());
                            INNATE_ABILITIES.get(type.getSubtypeName()).add(innate);
                        }
                        case PassiveDragonAbility passive -> {
                            PASSIVE_ABILITIES.computeIfAbsent(type.getSubtypeName(), s -> new ArrayList<>());
                            PASSIVE_ABILITIES.get(type.getSubtypeName()).add(passive);
                        }
                        case ActiveDragonAbility active when Objects.equals(ability.getDragonType().getSubtypeName(), type.getSubtypeName()) -> {
                            ABILITIES.computeIfAbsent(type.getSubtypeName(), s -> new ArrayList<>());
                            ABILITIES.get(type.getSubtypeName()).add(ability);
                            ACTIVE_ABILITIES.computeIfAbsent(type.getSubtypeName(), s -> new ArrayList<>());
                            ACTIVE_ABILITIES.get(type.getSubtypeName()).add(active);
                        }
                        default -> { /* Nothing to do */ }
                    }
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
				DragonSurvivalMod.LOGGER.error(e);
			}
		}
		return instances;
	}

	public static void addAbility(LivingEntity player, Class<? extends DragonAbility> c){
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

		try{
			DragonAbility ability = c.newInstance();

			if(player instanceof Player p){
				ability.player = p;
			}

			handler.getMagicData().abilities.put(ability.getName(), ability);

			if(player.level().isClientSide()){
				PacketDistributor.sendToServer(new SyncMagicCap.Data(player.getId(), handler.getMagicData().serializeNBT(player.registryAccess())));
			} else {
				PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncMagicCap.Data(player.getId(), handler.getMagicData().serializeNBT(player.registryAccess())));
			}
		}catch(InstantiationException | IllegalAccessException e){
			throw new RuntimeException(e);
		}
	}

	public static void setAbilityLevel(LivingEntity player, Class<? extends DragonAbility> abilityClass, int level){
		if(!hasAbility(player, abilityClass)){
			addAbility(player, abilityClass);
		}

		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
		handler.getMagicData().abilities.values().stream().filter(ability -> ability.getClass() == abilityClass).forEach(ability -> ability.level = Mth.clamp(level, ability.getMinLevel(), ability.getMaxLevel()));

		if(player.level().isClientSide()){
			PacketDistributor.sendToServer(new SyncMagicCap.Data(player.getId(), handler.getMagicData().serializeNBT(player.registryAccess())));
		}else{
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncMagicCap.Data(player.getId(), handler.getMagicData().serializeNBT(player.registryAccess())));
		}
	}

	public static boolean hasAbility(LivingEntity player, Class<? extends DragonAbility> c, @Nullable AbstractDragonType type) {
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
		return handler.getMagicData().abilities.values().stream().anyMatch(s-> {
			if (s.getClass() != c && !s.getClass().isAssignableFrom(c) && !c.isAssignableFrom(s.getClass()))
				return false;
			if (type == null)
				return false;
            return type.getSubtypeName() == null || type.getSubtypeName().equals(s.getDragonType().getSubtypeName());
        });
	}
	public static boolean hasAbility(LivingEntity player, Class<? extends DragonAbility> c) {
		return hasAbility(player, c, null);
	}
	public static boolean hasSelfAbility(LivingEntity player, Class<? extends DragonAbility> c) {
		AbstractDragonType dragonType = DragonStateProvider.getOrGenerateHandler(player).getType();
		if (dragonType == null)
			return hasAbility(player, c, null);
		else
			return hasAbility(player, c, dragonType);
	}
	public static <T extends DragonAbility> T getAbility(LivingEntity player, Class<T> c, @Nullable String dragonType){
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
		Optional<T> optionalT = (Optional<T>)handler.getMagicData().abilities.values().stream().filter(ability-> {
			if (ability.getClass() != c && !ability.getClass().isAssignableFrom(c) && !c.isAssignableFrom(ability.getClass()))
				return false;
            return dragonType == null || dragonType.equals(ability.getDragonType().getTypeName());
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
		AbstractDragonType dragonType = DragonStateProvider.getOrGenerateHandler(player).getType();
		if (dragonType == null)
			return getAbility(player, c, null);
		else
			return getAbility(player, c, dragonType.getTypeName());
	}

	public static AABB calculateBreathArea(@NotNull final Player player, double range) {
		return calculateBreathArea(player, DragonStateProvider.getOrGenerateHandler(player), range);
	}

	public static AABB calculateBreathArea(@NotNull final Player player, final DragonStateHandler handler, double range) {
		Vec3 viewVector = player.getLookAngle().scale(range);

		double defaultRadius = switch (handler.getLevel()) {
			case NEWBORN -> 0.3;
			case YOUNG -> 0.7;
			case ADULT -> 1;
		};

		/* TODO
		For each variable - start at center and move until it hits the max. range or a solid block (mutable block pos)?
		Would be bad for performance
		*/

		double xOffset = getOffset(viewVector.x(), defaultRadius);
		double yOffset = Math.abs(viewVector.y());
		double zOffset = getOffset(viewVector.z(), defaultRadius);

		// Check for look angle to avoid extending the range in the direction the player is not facing / looking
		double xMin = (player.getLookAngle().x() < 0 ? xOffset : defaultRadius);
		double yMin = (player.getLookAngle().y() < 0 ? yOffset : 0);
		double zMin = (player.getLookAngle().z() < 0 ? zOffset : defaultRadius);
		Vec3 min = new Vec3(Math.abs(xMin), Math.abs(yMin), Math.abs(zMin));

		double xMax = (player.getLookAngle().x() > 0 ? xOffset : defaultRadius);
		double yMax = (player.getLookAngle().y() > 0 ? yOffset + player.getEyeHeight() : player.getEyeHeight());
		double zMax = (player.getLookAngle().z() > 0 ? zOffset : defaultRadius);
		Vec3 max = new Vec3(Math.abs(xMax), Math.abs(yMax), Math.abs(zMax));

		return new AABB(player.position().subtract(min), player.position().add(max));
	}

	private static double getOffset(double value, double defaultValue) {
		if (value < 0) {
			return Math.min(value, -defaultValue);
		}

		return Math.max(value, defaultValue);
	}

	/** Start position for the logic which will affect blocks */
	public static Pair<BlockPos, Direction> breathStartPosition(final Player player, final BreathAbility breathAbility, int currentBreathRange) {
		Vec3 eyePosition = player.getEyePosition(1.0F);
		Vec3 viewVector = player.getViewVector(1.0F).scale(currentBreathRange);
		Vec3 vector3d2 = eyePosition.add(viewVector);

		BlockPos pos = null;
		BlockHitResult result = player.level().clip(new ClipContext(eyePosition, vector3d2, ClipContext.Block.OUTLINE, breathAbility.clipContext(), player));

		if (result.getType() == HitResult.Type.MISS) {
			pos = BlockPosHelper.get(vector3d2);
		} else if (result.getType() == HitResult.Type.BLOCK) {
			pos = result.getBlockPos();
		}

		return Pair.of(pos, result.getDirection());
	}
}