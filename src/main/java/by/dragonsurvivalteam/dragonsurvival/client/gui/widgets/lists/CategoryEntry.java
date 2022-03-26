package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstanceInstance;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@OnlyIn( Dist.CLIENT )
public class CategoryEntry extends by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionListEntry{
	public final TextComponent name;
	private final OptionsList optionsList;
	private final ResourceLocation BUTTON_UP = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/button_up.png");
	private final ResourceLocation BUTTON_DOWN = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/button_down.png");
	private final int width;
	public String origName;
	public boolean enabled = false;
	public int indent = 0;
	public CategoryEntry parent;
	public int catNum;

	public CategoryEntry(OptionsList optionsList, TextComponent p_i232280_2_, CategoryEntry entry, int catNum){
		super(ImmutableMap.of());
		this.optionsList = optionsList;
		this.name = p_i232280_2_;
		this.width = Minecraft.getInstance().font.width(this.name);
		this.parent = entry;
		this.catNum = catNum;
		if(entry != null){
			this.indent = entry.indent + 10;
		}

		if(OptionsList.activeCats.contains(catNum)){
			enabled = true;
		}
	}

	public void render(PoseStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_){
		if(parent != null && !parent.enabled){
			return;
		}

		int color = new Color(0.05F, 0.05F, 0.05F, 0.85F).getRGB();
		Gui.fill(p_230432_1_, 32 + indent, (p_230432_3_ + p_230432_6_ - 16), ((OptionsList)list).getScrollbarPosition(), (p_230432_3_ + p_230432_6_), color);

		Minecraft.getInstance().font.draw(p_230432_1_, name, (float)(Minecraft.getInstance().screen.width / 2 - this.width / 2) + indent, (float)(p_230432_3_ + p_230432_6_ - 12), 16777215);

		if(!enabled){
			RenderSystem.setShaderTexture(0, BUTTON_UP);
			Gui.blit(p_230432_1_, ((OptionsList)list).getScrollbarPosition() - 30, (p_230432_3_ + p_230432_6_ - 16), 0, 0, 16, 16, 16, 16);
		}else{
			RenderSystem.setShaderTexture(0, BUTTON_DOWN);
			Gui.blit(p_230432_1_, ((OptionsList)list).getScrollbarPosition() - 30, (p_230432_3_ + p_230432_6_ - 16), 0, 0, 16, 16, 16, 16);
		}
	}

	public List<? extends GuiEventListener> children(){
		return Collections.emptyList();
	}

	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_){
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstanceInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		this.enabled = !this.enabled;

		if(enabled){
			OptionsList.activeCats.add(catNum);
		}else{
			OptionsList.activeCats.removeIf((s) -> s == catNum);
		}

		return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
	}

	public boolean changeFocus(boolean p_231049_1_){
		return false;
	}

	@Override
	public int getHeight(){
		return parent == null || parent.enabled ? 20 : 0;
	}

	public boolean isMouseOver(double p_231047_1_, double p_231047_3_){
		return Objects.equals(((OptionsList)this.list).getEntryAtPos(p_231047_1_, p_231047_3_), this);
	}

	@Override
	public List<? extends NarratableEntry> narratables(){
		return null;
	}
}