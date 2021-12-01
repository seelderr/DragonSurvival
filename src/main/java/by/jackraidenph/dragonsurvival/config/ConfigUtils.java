package by.jackraidenph.dragonsurvival.config;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    public static List<Item> parseConfigItemList(List<? extends String> values) {
        List<Item> result = new ArrayList<>();

        for (String entry : values.toArray(new String[0])) {
            final String[] sEntry = entry.split(":");
            final ResourceLocation rlEntry = new ResourceLocation(sEntry[1], sEntry[2]);

            if (sEntry[0].equalsIgnoreCase("tag")) {
                final ITag<Item> tag = ItemTags.getAllTags().getTag(rlEntry);

                if (tag != null)
                    result.addAll(tag.getValues());
            } else
                result.add(ForgeRegistries.ITEMS.getValue(rlEntry));
        }

        return result;
    }
}