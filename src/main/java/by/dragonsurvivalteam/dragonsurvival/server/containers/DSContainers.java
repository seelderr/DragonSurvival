package by.dragonsurvivalteam.dragonsurvival.server.containers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSContainers{

	public static ContainerType<SourceOfMagicContainer> nestContainer;
	public static ContainerType<CraftingContainer> craftingContainer;
	public static ContainerType<DragonContainer> dragonContainer;

	@SubscribeEvent
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> register){
		nestContainer = IForgeContainerType.create(SourceOfMagicContainer::new);
		IForgeRegistry<ContainerType<?>> forgeRegistry = register.getRegistry();
		forgeRegistry.register(nestContainer.setRegistryName(DragonSurvivalMod.MODID, "dragon_nest"));

		craftingContainer = new ContainerType<>(CraftingContainer::new);
		forgeRegistry.register(craftingContainer.setRegistryName(DragonSurvivalMod.MODID, "extra_crafting"));

		dragonContainer = IForgeContainerType.create((windowId, inv, data) -> new DragonContainer(windowId, inv, false));
		forgeRegistry.register(dragonContainer.setRegistryName(DragonSurvivalMod.MODID, "dragon_container"));
	}
}