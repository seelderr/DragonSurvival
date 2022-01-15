package by.jackraidenph.dragonsurvival.client.gui.widgets;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.settings.ListConfigSettingsScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings.DSItemStackFieldOption;
import by.jackraidenph.dragonsurvival.config.ConfigUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.registries.ForgeRegistries;
import org.codehaus.plexus.util.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ItemStackField extends TextFieldWidget implements IBidiTooltip
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	private DSItemStackFieldOption option;
	
	private List<ItemStack> stack = new ArrayList<>();
	private HashMap<String, IItemProvider> suggestedItems = new HashMap<>();
	
	private int index = 0;
	private float tick = 0;
	private static final int maxItems = 5;
	
	public double scroll;
	
	private List<String> suggestions;
	
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
		tick += 1;
		Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y + 1, 0, isHovered ? 32 : 0, width, height, 32, 32, 10, 0);
		
		if(!stack.isEmpty()) {
			if(stack.size() > 1){
				if(tick % 120 == 0){
					index++;
					
					if(index >= stack.size()){
						index = 0;
					}
				}
			}else{
				index = 0;
			}
			ItemStack sl = stack.get(index);
			
			if(sl != null && !sl.isEmpty()) {
				ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
				itemRenderer.renderAndDecorateItem(sl, x + 3, y + 3);
				itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, sl,x + 3, y + 3);
			}
		}
		
		
		if(!isFocused()){
			suggestions = null;
		}
		
		this.x += 25;
		this.y += 6;
		super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		
		if(getValue().isEmpty()){
			setTextColor(7368816);
			setValue(this.getMessage().getString());
			super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			setValue("");
			setTextColor(14737632);
		}
		
		this.x -= 25;
		this.y -= 6;
		
		if(Minecraft.getInstance().screen instanceof ListConfigSettingsScreen){
			ListConfigSettingsScreen settingsScreen = (ListConfigSettingsScreen)Minecraft.getInstance().screen;
			
			if(isFocused() && suggestions != null && !suggestions.isEmpty()){
				if(settingsScreen.selectedField != this){
					settingsScreen.selectedField = this;
				}
			}else{
				if(settingsScreen.selectedField == this){
					settingsScreen.selectedField = null;
				}
			}
		}
	}
	
	public void renderPost(MatrixStack mStack, int mouseX, int mouseY, float partial){
		if(isFocused() && suggestions != null && !suggestions.isEmpty()) {
			int amount = Math.min(maxItems, suggestions.size());
			
			Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
			GuiUtils.drawContinuousTexturedBox(mStack, x, y + height, 0, 0, width, amount * height, 32, 32, 10, 0);
			
			int maxElement = (int)(scroll / 20);
			int renderNum = 0;
			
			for (int i1 = Math.max(0, maxElement-maxItems); i1 < Math.min(Math.max(maxElement, maxItems), suggestions.size()); i1++) {
				String suggestion = suggestions.get(i1);
				
				int startY = y + (height);
				int level = renderNum * (height);
				
				int color = new Color(0.1F, 0.1F, 0.1F, 1F).getRGB();
				
				if (renderNum % 2 == 0) {
					color = new Color(0.2F, 0.2F, 0.2F, 1F).getRGB();
				}
				
				AbstractGui.fill(mStack, x + 2, startY + level + 2 - (renderNum > 0 ? 4 : 0), x + width - 2, startY + height + level - 2, color);
				
				if (suggestedItems.containsKey(suggestion)) {
					IItemProvider item = suggestedItems.get(suggestion);
					if (item != null) {
						ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
						ItemStack sk = new ItemStack(item);
						
						itemRenderer.renderAndDecorateItem(sk, x + 3, startY + level + 1);
					}
				}
				
				String value = getValue().isEmpty() ? option.getter.apply(Minecraft.getInstance().options) : getValue();
				String type = value.substring(0, value.indexOf(":"));
				String text = type + ":" + suggestion;
				Minecraft.getInstance().font.drawShadow(mStack, new StringTextComponent(Minecraft.getInstance().font.substrByWidth(new StringTextComponent(text), width - 20).getString()), x + 25, y + 5 + height + (renderNum * height), DyeColor.WHITE.getTextColor());
				
				renderNum++;
			}
			{
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuilder();
				
				float maxPosition = (suggestions.size()) * height;
				float maxScroll = Math.max(0, maxPosition);
				
				scroll = MathHelper.clamp(scroll, 0, maxScroll);
				
				int y0 = y + height + 2;
				int y1 = (y + height + (amount * height)) - 2;
				int x0 = x;
				int x1 = x + width;
				
				int i = x1 - 8;
				int j = i + 6;
				int k1 = (int)maxScroll;
				if (k1 > maxItems * height) {
					RenderSystem.disableTexture();
					int l1 = (int)((float)((y1 - y0) * (y1 - y0)) / maxPosition);
					l1 = MathHelper.clamp(l1, 32, y1 - y0 - 8);
					int i2 = (int)scroll * (y1 - y0 - l1) / k1 + y0;
					if (i2 < y0) {
						i2 = y0;
					}
					
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
					bufferbuilder.vertex((double)i, (double)y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)(i2 + l1), 0.0D).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)(i2 + l1), 0.0D).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)i2, 0.0D).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)(i2 + l1 - 1), 0.0D).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
					bufferbuilder.vertex((double)(j - 1), (double)(i2 + l1 - 1), 0.0D).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
					bufferbuilder.vertex((double)(j - 1), (double)i2, 0.0D).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
					tessellator.end();
					
					RenderSystem.enableTexture();
				}
			}
		}
	}
	
	public void update(){
		scroll = 0;
		stack.clear();
		String value = getValue().isEmpty() ? option.getter.apply(Minecraft.getInstance().options) : getValue();
		
		if(value.contains(":")) {
			stack = ConfigUtils.parseCombinedList(Arrays.asList(value), false,false).stream().map(ItemStack::new).collect(Collectors.toList());
		}
		
		if(!isFocused()) return;
		
		String resource = "";
		String type = "";
		
		
		if(value.contains(":")){
			type = value.substring(0, value.indexOf(":"));
			resource = value.substring(value.indexOf(":") + 1);
			
			while (StringUtils.countMatches(resource, ":") > 1) {
				resource = resource.substring(0, resource.lastIndexOf(":"));
			}
		}

		if(resource.isEmpty() || type.isEmpty()){
			suggestions = null;
			return;
		}
		
		SuggestionsBuilder builder = new SuggestionsBuilder(resource, 0);
		
		suggestions = null;
		suggestedItems.clear();
		
		if(type.isEmpty() || type.equalsIgnoreCase("item")) {
			ISuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getKeys(), builder);
		}
		
		if(type.isEmpty() || type.equalsIgnoreCase("block")) {
			ISuggestionProvider.suggestResource(ForgeRegistries.BLOCKS.getKeys(), builder);
		}
		
		if(type.isEmpty() || type.equalsIgnoreCase("tag")){
			ISuggestionProvider.suggestResource(BlockTags.getAllTags().getAvailableTags(), builder);
			ISuggestionProvider.suggestResource(ItemTags.getAllTags().getAvailableTags(), builder);
		}
		Suggestions sgs = builder.build();
		suggestions = sgs.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
		suggestions.removeIf((s) -> s == null || s.isEmpty());
		
		for(String bl : suggestions){
			ConfigUtils.parseCombinedList(Arrays.asList(bl), true, true).stream().findFirst().ifPresent((item) -> {
				suggestedItems.put(bl, item);
			});
		}
	}
	
	@Override
	public boolean charTyped(char pCodePoint, int pModifiers)
	{
		boolean val = super.charTyped(pCodePoint, pModifiers);
		update();
		return val;
	}
	
	@Override
	public void deleteChars(int pNum)
	{
		super.deleteChars(pNum);
		update();
	}
	
	@Override
	public Optional<List<IReorderingProcessor>> getTooltip()
	{
		return option != null ? option.getTooltip() : Optional.empty();
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		int maxHeight = y + height;
		
		if(isFocused() && suggestions != null && !suggestions.isEmpty()){
			maxHeight += Math.min(maxItems, suggestions.size()) * height;
		}
		
		return this.active && this.visible && pMouseX >= (double)this.x && pMouseY >= (double)this.y && pMouseX < (double)(this.x + this.width) && pMouseY < maxHeight;
	}
	
	protected boolean clicked(double pMouseX, double pMouseY) {
		return isMouseOver(pMouseX, pMouseY);
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		if(isFocused() && suggestions != null && !suggestions.isEmpty()){
			int maxElement = (int)(scroll / 20);
			int renderNum = 0;
			
			for (int i1 = Math.max(0, maxElement-maxItems); i1 < Math.min(Math.max(maxElement, maxItems), suggestions.size()); i1++) {
				String suggestion = suggestions.get(i1);
				
				int startY = y + (height);
				int level = renderNum * (height);
				String value = getValue().isEmpty() ? option.getter.apply(Minecraft.getInstance().options) : getValue();
				String type = value.substring(0, value.indexOf(":"));
				String text = type + ":" + suggestion;
				
				if(pMouseX >= x + 2 && pMouseY >= startY + level + 2 - (renderNum > 0 ? 4 : 0) && pMouseX < x + width - 2 && pMouseY < startY + height + level - 2){
					setValue(text);
					setFocus(false);
					update();
					return true;
				}
				
				renderNum++;
			}
		}
		
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
	{
		scroll -= pDelta * 10;
		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}
}