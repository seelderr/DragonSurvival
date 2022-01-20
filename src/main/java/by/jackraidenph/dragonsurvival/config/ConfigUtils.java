package by.jackraidenph.dragonsurvival.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    
    public static boolean containsEntity(List<? extends String> values, Entity entity) {
        String type = entity.getType().getRegistryName().toString();
        return values.contains(type) || values.contains("entity:"+type);
    }
    
    public static List<Item> parseConfigItemList(List<? extends String> values) {
        List<Item> result = new ArrayList<>();

        for (String entry : values) {
            final String[] sEntry = entry.split(":");
            final ResourceLocation rlEntry = new ResourceLocation(sEntry[1], sEntry[2]);

            if (sEntry[0].equalsIgnoreCase("tag")) {
                final Tag<Item> tag = ItemTags.getAllTags().getTag(rlEntry);

                if (tag != null)
                    result.addAll(tag.getValues());
            } else
                result.add(ForgeRegistries.ITEMS.getValue(rlEntry));
        }

        return result;
    }
    
    public static List<Block> parseConfigBlockList(List<? extends String> values) {
        List<Block> result = new ArrayList<>();
        
        for (String entry : values) {
            final String[] sEntry = entry.split(":");
            final ResourceLocation rlEntry = new ResourceLocation(sEntry[1], sEntry[2]);
            if (sEntry[0].equalsIgnoreCase("tag")) {
                final Tag<Block> tag = BlockTags.getAllTags().getTag(rlEntry);
                if (tag != null && tag.getValues().size() != 0) {
                    result.addAll(tag.getValues());
                }
            } else {
                final Block block = ForgeRegistries.BLOCKS.getValue(rlEntry);
                if (block != Blocks.AIR) {
                    result.add(block);
                }
            }
        }
        
        return result;
    }
}