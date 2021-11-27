package by.jackraidenph.dragonsurvival.registration;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.items.base.ItemBase;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DragonSurvivalMod.MODID);

    public static final RegistryObject<Item> DRAGON_HEART_SHARD = ITEMS.register("dragon_heart_shard", ItemBase::new);
    public static final RegistryObject<Item> WEAK_DRAGON_HEART = ITEMS.register("weak_dragon_heart", ItemBase::new);
    public static final RegistryObject<Item> ANCIENT_DRAGON_HEART = ITEMS.register("ancient_dragon_heart", ItemBase::new);

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}