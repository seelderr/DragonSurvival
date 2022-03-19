package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class DragonEditorDropdownEntry extends DropdownEntry
{
	public int num;
	public String value;
	public Consumer<String> setter;
	
	public ExtendedButton button;
	public DropDownButton source;
	private DragonEditorScreen screen;
	private EnumSkinLayer layer;
	
	private DragonStateHandler handler = new DragonStateHandler();
	
	private StringTextComponent message;
	
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	
	public DragonEditorDropdownEntry(DragonEditorScreen screen, DropDownButton source, int num, String value, Consumer<String> setter, EnumSkinLayer layer)
	{
		this.num = num;
		this.value = value;
		this.setter = setter;
		this.source = source;
		this.screen = screen;
		message = new StringTextComponent(value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT));
		this.layer = layer;
		
		handler.getSkin().blankSkin = true;
		handler.setSize(DragonLevel.ADULT.size);
		handler.setType(DragonType.SEA);
		
		handler.getSkin().skinPreset.skinAges.get(DragonLevel.ADULT).layerSettings.get(layer).selectedSkin = value;
	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of(button);
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks)
	{
		if(button == null) {
			if(list != null) {
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight+1, null, null)
				{
					@Override
					public ITextComponent getMessage()
					{
						return message;
					}
					
					@Override
					public void onPress()
					{
						source.current = value;
						source.onPress();
						setter.accept(value);
					}
					
					@Override
					public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
					{
						mStack.pushPose();
						mStack.translate(0, 0, 200);
						super.renderButton(mStack, mouseX, mouseY, partial);
						mStack.popPose();
					}
				};
			}
		}else {
			button.y = pTop;
			button.visible = source.visible;
			button.active = (!Objects.equals(source.current, value));
			button.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		}
	}
	
	@Override
	public void lateRender(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		if(button != null && button.isHovered() && !Objects.equals(value, SkinCap.defaultSkinValue)){
			Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
			GuiUtils.drawContinuousTexturedBox(pMatrixStack, Objects.equals(layer.name, "Extra") ? source.list.getLeft() - 75 : source.list.getRight(), source.list.getTop() - 3, 0, 0, 75, 75, 32, 32, 10, 10);
			
			handler.getSkin().blankSkin = true;
			handler.setSize(DragonLevel.ADULT.size);
			handler.setType(screen.type);
			handler.setHasWings(true);
			
			FakeClientPlayerUtils.getFakePlayer(1, handler).animationSupplier = () -> "sit";
			
			float zoom = 0;
			float xRot = -5;
			float yRot = -3;
			float xOffset = 0;
			float yOffset = 0;
			
			if(layer == EnumSkinLayer.EYES){
				handler.getSkin().skinPreset.skinAges.get(DragonLevel.ADULT).wings = false;
				zoom = 40;
				yOffset = -1f;
				xOffset = 1f;
				xRot = -10;
				yRot = 0;
			}else if(layer == EnumSkinLayer.HORNS){
				handler.getSkin().skinPreset.skinAges.get(DragonLevel.ADULT).wings = false;
				zoom = 20;
				yOffset = -1f;
				xOffset = 0.75f;
				xRot = -10;
				yRot = -5;
			}else if(layer == EnumSkinLayer.BOTTOM){
				handler.getSkin().skinPreset.skinAges.get(DragonLevel.ADULT).wings = true;
				yOffset = -0.5f;
				xRot = 2;
				yRot = 6;
			}else if(layer == EnumSkinLayer.SPIKES){
				handler.getSkin().skinPreset.skinAges.get(DragonLevel.ADULT).wings = false;
				xRot = 6;
				yRot = -5;
				yOffset = 0.5f;
			}else{
				handler.getSkin().skinPreset.skinAges.get(DragonLevel.ADULT).wings = true;
				xRot = 6;
			}
			
			RenderSystem.pushMatrix();
			pMatrixStack.pushPose();

			GL11.glScissor((int)(((Objects.equals(layer.name, "Extra") ? source.list.getLeft() - 75 : source.list.getRight()) + 2) * Minecraft.getInstance().getWindow().getGuiScale()),
			               (int)((int)(Minecraft.getInstance().getWindow().getScreenHeight() - ((source.list.getTop() - 5) * Minecraft.getInstance().getWindow().getGuiScale())) - (75 * Minecraft.getInstance().getWindow().getGuiScale())),
			               (int)(71 * Minecraft.getInstance().getWindow().getGuiScale()),
			               (int)(71 * Minecraft.getInstance().getWindow().getGuiScale()));
			GL11.glEnable(GL11.GL_SCISSOR_TEST);

			ClientDragonRender.dragonModel.setCurrentTexture(null);
			ClientDragonRender.renderEntityInInventory(FakeClientPlayerUtils.getFakeDragon(1, handler), Objects.equals(layer.name, "Extra") ?  source.list.getLeft()  - 75 + 37 :  source.list.getRight() + 37, source.list.getTop() - 3 + 37, 20 + zoom, xRot, yRot , xOffset, yOffset);
			
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			
			pMatrixStack.popPose();
			RenderSystem.popMatrix();
		}
	}
}
