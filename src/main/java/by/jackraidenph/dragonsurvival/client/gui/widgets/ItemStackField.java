package by.jackraidenph.dragonsurvival.client.gui.widgets;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings.DSItemStackFieldOption;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemStackField extends TextFieldWidget implements IBidiTooltip
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	private DSItemStackFieldOption option;
	private List<ItemStack> stack = new ArrayList<>();
	private int index = 0;
	private int tick = 0;
	
	public ItemStackField(int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage)
	{
		this(null, pX, pY, pWidth, pHeight, pMessage);
	}
	
	public ItemStackField(DSItemStackFieldOption option, int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage)
	{
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
		this.option = option;
		update();
	}
	
	@Override
	public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		tick++;
		Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y + 1, 0, isHovered ? 32 : 0, width, height, 32, 32, 10, 0);
		
		if(!stack.isEmpty()) {
			if(stack.size() > 1){
				if(tick % 20 == 0){
					index++;
					
					if(index > stack.size()){
						index = 0;
					}
				}
			}else{
				index = 0;
			}
			ItemStack sl = stack.get(index);
			
			if(sl != null && !sl.isEmpty()) {
				ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
				itemRenderer.renderGuiItem(sl, x + 3, y + 3);
			}
		}
		
		
		this.x += 32;
		this.y += 6;
		super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		
		if(getValue().isEmpty()){
			setTextColor(7368816);
			setValue(this.getMessage().getString());
			super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			setValue("");
			setTextColor(14737632);
		}
		
		this.x -= 32;
		this.y -= 6;
	}
	
	public void update(){
		stack.clear();
		String value = option.getter.apply(Minecraft.getInstance().options);
		if(value.isEmpty() || !value.contains(":")) return;
		
		String type = value.substring(0, value.indexOf(":"));
		String resource = value.substring(value.indexOf(":")+1);
		
		while(StringUtils.countMatches(resource, ":") > 1){
			resource = resource.substring(0, resource.lastIndexOf(":"));
		}
		
		if(type.equalsIgnoreCase("tag")){
			ResourceLocation location = new ResourceLocation(resource);
			final ITag<Block> blockITag = BlockTags.getAllTags().getTag(location);
			
			if (blockITag != null && blockITag.getValues().size() != 0) {
				stack.addAll(blockITag.getValues().stream().map(ItemStack::new).collect(Collectors.toList()));
			}
			
			final ITag<Item> itemITag = ItemTags.getAllTags().getTag(location);
			
			if (itemITag != null && itemITag.getValues().size() != 0) {
				stack.addAll(itemITag.getValues().stream().map(ItemStack::new).collect(Collectors.toList()));
			}
		}
		
		if(type.equalsIgnoreCase("item")) {
			try {
				ItemParser parser = new ItemParser(new StringReader(resource), false).parse();
				Item item = parser.getItem();
				
				if (item != null) {
					stack.add(new ItemStack(item));
				}
				
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
		}
		
		if(type.equalsIgnoreCase("block")) {
			try {
				BlockStateParser parser = new BlockStateParser(new StringReader(resource), false).parse(false);
				BlockState state = parser.getState();
				
				if (state != null) {
					stack.add(new ItemStack(state.getBlock()));
				}
				
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean charTyped(char pCodePoint, int pModifiers)
	{
		update();
		return super.charTyped(pCodePoint, pModifiers);
	}
	
	@Override
	public void deleteChars(int pNum)
	{
		update();
		super.deleteChars(pNum);
	}
	
	@Override
	public Optional<List<IReorderingProcessor>> getTooltip()
	{
		return option != null ? option.getTooltip() : Optional.empty();
	}
}