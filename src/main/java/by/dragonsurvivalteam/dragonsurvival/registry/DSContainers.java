package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import by.dragonsurvivalteam.dragonsurvival.server.containers.SourceOfMagicContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSContainers{

	public static MenuType<SourceOfMagicContainer> nestContainer;
	public static MenuType<DragonContainer> dragonContainer;

	//MenuType
	@SubscribeEvent
	public static void registerContainers(RegistryEvent.Register<MenuType<?>> register){
		IForgeRegistry<MenuType<?>> forgeRegistry = register.getRegistry();

		nestContainer = IForgeMenuType.create(SourceOfMagicContainer::new);
		forgeRegistry.register(nestContainer.setRegistryName(DragonSurvivalMod.MODID, "dragon_nest"));

		dragonContainer = IForgeMenuType.create((windowId, inv, data) -> new DragonContainer(windowId, inv, false));
		forgeRegistry.register(dragonContainer.setRegistryName(DragonSurvivalMod.MODID, "dragon_container"));
	}
}