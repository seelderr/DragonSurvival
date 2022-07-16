package by.dragonsurvivalteam.dragonsurvival.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncMagicCap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.network.PacketDistributor;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.*;

public class DragonAbilities{
	public static HashMap<DragonType, ArrayList<DragonAbility>> ABILITIES = new HashMap<>();
	public static HashMap<DragonType, ArrayList<ActiveDragonAbility>> ACTIVE_ABILITIES = new HashMap<>();
	public static HashMap<DragonType, ArrayList<PassiveDragonAbility>> PASSIVE_ABILITIES = new HashMap<>();
	public static HashMap<DragonType, ArrayList<InnateDragonAbility>> INNATE_ABILITIES = new HashMap<>();
	public static HashMap<String, DragonAbility> ABILITY_LOOKUP = new HashMap<>();

	public static void initAbilities(){
		List<DragonAbility> abs = getInstances(RegisterDragonAbility.class, DragonAbility.class);

		for(DragonAbility ability : abs){
			ABILITY_LOOKUP.put(ability.getName(), ability);

			if(ability.getDragonType() == null){
				for(DragonType type : DragonType.values()){
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
				ABILITIES.computeIfAbsent(ability.getDragonType(), s -> new ArrayList<>());
				ABILITIES.get(ability.getDragonType()).add(ability);

				if(ability instanceof InnateDragonAbility ab){
					INNATE_ABILITIES.computeIfAbsent(ability.getDragonType(), s -> new ArrayList<>());
					INNATE_ABILITIES.get(ability.getDragonType()).add(ab);
				}else if(ability instanceof PassiveDragonAbility ab){
					PASSIVE_ABILITIES.computeIfAbsent(ability.getDragonType(), s -> new ArrayList<>());
					PASSIVE_ABILITIES.get(ability.getDragonType()).add(ab);
				}else if(ability instanceof ActiveDragonAbility ab){
					ACTIVE_ABILITIES.computeIfAbsent(ability.getDragonType(), s -> new ArrayList<>());
					ACTIVE_ABILITIES.get(ability.getDragonType()).add(ab);
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

			handler.getMagic().abilities.put(ability.getName(), ability);

			if(player.level.isClientSide){
				NetworkHandler.CHANNEL.sendToServer(new SyncMagicCap(player.getId(), handler.getMagic()));
			}else{
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicCap(player.getId(), handler.getMagic()));
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
		handler.getMagic().abilities.values().stream().filter(s-> s.getClass() == c).forEach(s -> {
			s.level = Mth.clamp(level, s.getMinLevel(), s.getMaxLevel());
		});

		if(player.level.isClientSide){
			NetworkHandler.CHANNEL.sendToServer(new SyncMagicCap(player.getId(), handler.getMagic()));
		}else{
			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicCap(player.getId(), handler.getMagic()));
		}
	}

	public static boolean hasAbility(LivingEntity player, Class<? extends DragonAbility> c) {
		DragonStateHandler handler = DragonUtils.getHandler(player);
		return handler.getMagic().abilities.values().stream().anyMatch(s-> s.getClass() == c);
	}

	public static <T extends DragonAbility> T getAbility(LivingEntity player, Class<T> c){
		DragonStateHandler handler = DragonUtils.getHandler(player);
		Optional<T> optionalT = (Optional<T>)handler.getMagic().abilities.values().stream().filter(s-> s.getClass() == c).findAny();
		return optionalT.orElseGet(() -> {
			try{
				return c.newInstance();
			}catch(InstantiationException | IllegalAccessException e){
				throw new RuntimeException(e);
			}
		});
	}
}