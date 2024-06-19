package by.dragonsurvivalteam.dragonsurvival.common.capability;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber( modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class Capabilities{

	public static final EntityCapability<EntityStateHandler, Void> ENTITY_CAPABILITY = EntityCapability.createVoid(
			ResourceLocation.fromNamespaceAndPath(MODID, "entity_capability"),
			EntityStateHandler.class);

	public static final EntityCapability<DragonStateHandler, Void> DRAGON_CAPABILITY = EntityCapability.createVoid(
			ResourceLocation.fromNamespaceAndPath(MODID, "dragon_capability"),
			DragonStateHandler.class);

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.registerEntity(DRAGON_CAPABILITY, EntityType.PLAYER, new DragonStateProvider());
		event.registerEntity(DRAGON_CAPABILITY, DSEntities.DRAGON.get(), new DragonStateProvider());
		for(EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			if (entityType.getCategory() != MobCategory.MISC)
				event.registerEntity(ENTITY_CAPABILITY, entityType, new EntityStateProvider());
		}
	}
}