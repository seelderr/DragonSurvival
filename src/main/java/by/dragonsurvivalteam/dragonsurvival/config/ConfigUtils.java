package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class ConfigUtils {
    private static final int NAMESPACE = 0;
    private static final int PATH = 1;

    public static Predicate<ItemStack> createItemPredicate(final String[] splitData) {
        boolean isTag = splitData[NAMESPACE].startsWith("#");

        ResourceLocation location = getLocation(splitData, isTag);
        if (isTag) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, location);
            // Can't check if tag exists or not at this point in time
            return stack -> stack.is(tag);
        } else {
            Item item = BuiltInRegistries.ITEM.get(location);
            return stack -> stack.is(item);
        }
    }

    public static Predicate<BlockState> createBlockPredicate(final String[] splitData) {
        boolean isTag = splitData[NAMESPACE].startsWith("#");
        ResourceLocation location = getLocation(splitData, isTag);

        if (isTag) {
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, location);
            // Can't check if tag exists or not at this point in time
            return stack -> stack.is(tag);
        } else {
            Block block = BuiltInRegistries.BLOCK.get(location);
            return stack -> stack.is(block);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // ignore
    public static boolean validateResourceLocation(final String[] splitData) {
        if (splitData.length < 2) {
            return false;
        }

        String namespace = splitData[NAMESPACE].startsWith("#") ? splitData[NAMESPACE].substring(1) : splitData[NAMESPACE];
        String path = splitData[PATH];

        return ResourceLocation.tryParse(namespace + ":" + path) != null;
    }

    public static boolean validateInteger(final String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validateFloat(final String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validateDouble(final String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static ResourceLocation getLocation(final String[] splitData, boolean isTag) {
        if (isTag) {
            return DragonSurvivalMod.location(splitData[NAMESPACE].substring(1), splitData[PATH]);
        }

        return DragonSurvivalMod.location(splitData[NAMESPACE], splitData[PATH]);
    }
}
