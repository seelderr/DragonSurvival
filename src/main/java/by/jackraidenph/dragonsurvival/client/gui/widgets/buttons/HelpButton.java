package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.utils.TooltipProvider;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class HelpButton extends ImageButton implements TooltipProvider
{
	public String text;
	private List<Component> tooltip;
	
	public static final ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/help_button.png");
	
	public HelpButton(int x, int y, int sizeX, int sizeY, String text)
	{
		this(DragonUtils.getDragonType(Minecraft.getInstance().player), x, y, sizeX, sizeY, text);
	}
	
	public HelpButton(DragonType type, int x, int y, int sizeX, int sizeY, String text)
	{
		super(x, y, sizeX, sizeY, 0, 0, (int)(((type.ordinal() + 1) * 18f) *((float)sizeY / 18f)), texture, (int)(256f * ((float)sizeX / 18f)), (int)(256f * ((float)sizeY / 18f)), (btn) -> {});
		this.text = text;
		if(text != null) {
			this.tooltip = List.of(new TranslatableComponent(text));
		}
	}
	
	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_)
	{
		return false;
	}
	
	@Override
	public List<Component> getTooltip()
	{
		return text != null && tooltip != null && tooltip.size() > 0 ? tooltip : List.of();
	}
}
