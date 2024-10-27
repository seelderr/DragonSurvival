package by.dragonsurvivalteam.dragonsurvival.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DSAttributes {
    public static final DeferredRegister<Attribute> DS_ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MODID);
    public static final Holder<Attribute> FLIGHT_STAMINA_COST = DS_ATTRIBUTES.register("flight_stamina_cost", () -> new RangedAttribute("ds.attribute.flight_stamina", 1, 0, 5).setSyncable(true));
    public static final Holder<Attribute> LAVA_SWIM_SPEED = DS_ATTRIBUTES.register("lava_swim_speed", () -> new RangedAttribute("ds.attribute.lava_swim_speed", 1, 0, 1024).setSyncable(true));

    @SubscribeEvent
    public static void attachAttributes(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, FLIGHT_STAMINA_COST);
        event.getTypes().forEach(type -> event.add(type, LAVA_SWIM_SPEED));
    }
}
