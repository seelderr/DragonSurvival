package by.jackraidenph.dragonsurvival.config;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    public static List<IItemProvider> parseCombinedList(List<String> values, boolean ignoreType, boolean firstOnly){
        List<IItemProvider> result = new ArrayList<>();
    
        for(String value : values) {
            String type = value.substring(0, value.indexOf(":"));
            String resource = !ignoreType ? value.substring(value.indexOf(":") + 1) : value;
    
            if(!ignoreType) {
                while (StringUtils.countMatches(resource, ":") > 1) {
                    resource = resource.substring(0, resource.lastIndexOf(":"));
                }
            }
    
            if (type.equalsIgnoreCase("tag") || ignoreType) {
                ResourceLocation location = new ResourceLocation(resource);
                final ITag<Block> blockITag = BlockTags.getAllTags().getTag(location);
        
                if (blockITag != null && blockITag.getValues().size() != 0) {
                    if(firstOnly) {
                        result.add(blockITag.getValues().get(0));
                    }else{
                        result.addAll(blockITag.getValues());
                    }
                }
        
                final ITag<Item> itemITag = ItemTags.getAllTags().getTag(location);
        
                if (itemITag != null && itemITag.getValues().size() != 0) {
                    if(firstOnly) {
                        result.add(itemITag.getValues().get(0));
                    }else{
                        result.addAll(itemITag.getValues());
                    }
                }
            }
    
            if (type.equalsIgnoreCase("item") || ignoreType) {
                try {
                    ItemParser parser = new ItemParser(new StringReader(resource), false).parse();
                    Item item = parser.getItem();
            
                    if (item != null) {
                        if(!result.contains(item)) {
                            result.add(item);
                        }
                    }
            
                } catch (CommandSyntaxException ignored) {}
            }
    
            if (type.equalsIgnoreCase("block") || ignoreType) {
                try {
                    BlockStateParser parser = new BlockStateParser(new StringReader(resource), false).parse(false);
                    BlockState state = parser.getState();
            
                    if (state != null) {
                        if(!result.contains(state.getBlock())) {
                            result.add(state.getBlock());
                        }
                    }
            
                } catch (CommandSyntaxException ignored) {}
            }
        }
        
        return result;
    }
    
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
    
    public static List<Block> parseConfigBlockList(List<? extends String> values) {
        List<Block> result = new ArrayList<>();
        
        for (String entry : values.toArray(new String[0])) {
            final String[] sEntry = entry.split(":");
            final ResourceLocation rlEntry = new ResourceLocation(sEntry[1], sEntry[2]);
            if (sEntry[0].equalsIgnoreCase("tag")) {
                final ITag<Block> tag = BlockTags.getAllTags().getTag(rlEntry);
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