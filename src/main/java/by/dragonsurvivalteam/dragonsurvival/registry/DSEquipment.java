package by.dragonsurvivalteam.dragonsurvival.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DSEquipment {
    public static final DeferredRegister<ArmorMaterial> DS_ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, MODID);

    public static final Holder<ArmorMaterial> GOOD_DRAGON_ARMOR_MATERIAL =
            DS_ARMOR_MATERIALS.register("good_dragon", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }),
                    30,
                    Holder.direct(SoundEvents.BELL_BLOCK),
                    () -> Ingredient.of(Tags.Items.BARRELS),
                    List.of(
                            new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath(MODID, "good_dragon")
                            )
                    ),
                    3.0F,
                    0.1F
            ));

    public static final Holder<ArmorMaterial> EVIL_DRAGON_ARMOR_MATERIAL =
            DS_ARMOR_MATERIALS.register("evil_dragon", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }),
                    30,
                    Holder.direct(SoundEvents.FOX_SCREECH),
                    () -> Ingredient.of(Tags.Items.BARRELS),
                    List.of(
                            new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath(MODID, "evil_dragon")
                            )
                    ),
                    3.0F,
                    0.1F
            ));

    public static final Tier DRAGON_HUNTER = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2031,
            9.0f,
            5.0f,
            15,
            () -> Ingredient.of(Items.NETHERITE_INGOT)
    );
}
