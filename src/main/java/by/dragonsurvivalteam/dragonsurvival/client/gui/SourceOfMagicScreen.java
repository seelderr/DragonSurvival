package by.dragonsurvivalteam.dragonsurvival.client.gui;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.server.containers.SourceOfMagicContainer;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.lwjgl.opengl.GL11;

public class SourceOfMagicScreen extends AbstractContainerScreen<SourceOfMagicContainer>{
	static final ResourceLocation BACKGROUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/source_of_magic_ui.png");
	static final ResourceLocation CAVE_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/cave_source_of_magic_0.png");
	static final ResourceLocation CAVE_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/cave_source_of_magic_1.png");
	static final ResourceLocation FOREST_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/forest_source_of_magic_0.png");
	static final ResourceLocation FOREST_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/forest_source_of_magic_1.png");
	static final ResourceLocation SEA_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/sea_source_of_magic_0.png");
	static final ResourceLocation SEA_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/sea_source_of_magic_1.png");
	private final SourceOfMagicTileEntity nest;

	private final Player player;

	public SourceOfMagicScreen(SourceOfMagicContainer screenContainer, Inventory inv, Component titleIn){
		super(screenContainer, inv, titleIn);
		nest = screenContainer.nestEntity;
		player = inv.player;
	}

	@Override
	protected void init(){
		super.init();
		addRenderableWidget(new HelpButton(leftPos + 12, topPos + 12, 12, 12, "ds.help.source_of_magic", 0));
	}

	public void render(PoseStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_){
		this.renderBackground(matrixStack);
		super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
	}

	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY){}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY){ // WIP
		renderBackground(matrixStack);
		RenderSystem.setShaderTexture(0, BACKGROUND);
		blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		boolean hasItem = !nest.getItem(0).isEmpty();
		Block block = nest.getBlockState().getBlock();

		if(DSBlocks.caveSourceOfMagic.equals(block)){
			RenderSystem.setShaderTexture(0, hasItem ? CAVE_NEST1 : CAVE_NEST0);
		}else if(DSBlocks.forestSourceOfMagic.equals(block)){
			RenderSystem.setShaderTexture(0, hasItem ? FOREST_NEST1 : FOREST_NEST0);
		}else if(DSBlocks.seaSourceOfMagic.equals(block)){
			RenderSystem.setShaderTexture(0, hasItem ? SEA_NEST1 : SEA_NEST0);
		}

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);
		blit(matrixStack, leftPos + 8, topPos + 8, 0, 0, 160, 49, 160, 49);
	}
}