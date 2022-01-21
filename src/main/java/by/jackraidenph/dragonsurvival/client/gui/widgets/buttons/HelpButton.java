package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.utils.TooltipProvider;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class HelpButton extends ImageButton implements TooltipProvider
{
	public String text;
	private List<FormattedCharSequence> tooltip;
	
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
			TranslatableComponent component = new TranslatableComponent(text);
			if(component != null) {
				this.tooltip = Minecraft.getInstance().font.split(component, 200);
			}
		}
	}
	
	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_)
	{
		return false;
	}
	
	@Override
	public List<FormattedCharSequence> getTooltip()
	{
		return text != null && tooltip != null && tooltip.size() > 0 ? tooltip : List.of();
	}
}
