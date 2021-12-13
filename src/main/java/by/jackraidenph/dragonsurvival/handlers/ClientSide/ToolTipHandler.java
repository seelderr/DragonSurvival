package by.jackraidenph.dragonsurvival.handlers.ClientSide;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.blocks.DragonAltarBlock;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gui.magic.AbilityScreen;
import by.jackraidenph.dragonsurvival.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.registration.BlockInit;
import by.jackraidenph.dragonsurvival.registration.ItemRegistry;
import by.jackraidenph.dragonsurvival.registration.ItemsInit;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.UUID;
@Mod.EventBusSubscriber( Dist.CLIENT)
public class ToolTipHandler
{
	private static final ResourceLocation tooltip_1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/magic_tips_0.png");
	private static final ResourceLocation tooltip_2 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/magic_tips_1.png");
	private static boolean blink = false;
	private static int tick = 0;
	
	
	@SubscribeEvent
	public static void checkIfDragonFood(ItemTooltipEvent tooltipEvent) {
		if (tooltipEvent.getPlayer() != null) {
			Item item = tooltipEvent.getItemStack().getItem();
			List<ITextComponent> toolTip = tooltipEvent.getToolTip();
			if (DragonFoodHandler.getSafeEdibleFoods(DragonType.CAVE).contains(item)) {
				toolTip.add(new TranslationTextComponent("ds.cave.dragon.food"));
			}
			if (DragonFoodHandler.getSafeEdibleFoods(DragonType.FOREST).contains(item)) {
				toolTip.add(new TranslationTextComponent("ds.forest.dragon.food"));
			}
			if (DragonFoodHandler.getSafeEdibleFoods(DragonType.SEA).contains(item)) {
				toolTip.add(new TranslationTextComponent("ds.sea.dragon.food"));
			}
		}
	}
	
	
	@SubscribeEvent
	public static void itemDescriptions(ItemTooltipEvent event){
		if(event.getPlayer() != null){
			Item item = event.getItemStack().getItem();
			List<ITextComponent> toolTip = event.getToolTip();
			
			if (item == ItemsInit.starBone){
				toolTip.add(new TranslationTextComponent("ds.description.starBone"));
			}
			if (item == ItemsInit.chargedSoup){
				toolTip.add(new TranslationTextComponent("ds.description.chargedSoup"));
			}
			if (item == ItemsInit.forestDragonTreat){
				toolTip.add(new TranslationTextComponent("ds.description.forestDragonTreat"));
			}
			if (item == ItemsInit.seaDragonTreat){
				toolTip.add(new TranslationTextComponent("ds.description.seaDragonTreat"));
			}
			if (item == ItemsInit.caveDragonTreat){
				toolTip.add(new TranslationTextComponent("ds.description.caveDragonTreat"));
			}
			if (item == ItemsInit.elderDragonBone){
				toolTip.add(new TranslationTextComponent("ds.description.elderDragonBone"));
			}
			if (item == ItemsInit.elderDragonDust){
				toolTip.add(new TranslationTextComponent("ds.description.elderDragonDust"));
			}
			if (item == BlockInit.fireDragonBeacon.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.passiveFireBeacon"));
			}
			if (item == BlockInit.magicDragonBeacon.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.passiveMagicBeacon"));
			}
			if (item == BlockInit.peaceDragonBeacon.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.passivePeaceBeacon"));
			}
			if (item == BlockInit.caveDoor.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.caveDoor"));
			}
			if (item == BlockInit.forestDoor.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.forestDoor"));
			}
			if (item == BlockInit.seaDoor.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.seaDoor"));
			}
			if (item == BlockInit.legacyDoor.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.legacyDoor"));
			}
			if (item == BlockInit.helmet1.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.grayHelmet"));
			}
			if (item == BlockInit.helmet2.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.goldHelmet"));
			}
			if (item == BlockInit.helmet3.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.blackHelmet"));
			}
			if (item == BlockInit.dragonBeacon.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.dragonBeacon"));
			}
			if (item == BlockInit.dragonMemoryBlock.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.dragonMemoryBlock"));
			}
			if (Block.byItem(item) instanceof DragonAltarBlock){
				toolTip.add(new TranslationTextComponent("ds.description.dragonAltar"));
			}
			if (item == BlockInit.PREDATOR_STAR_BLOCK.asItem()){
				toolTip.add(new TranslationTextComponent("ds.description.predatorStar"));
			}
			if (item == ItemRegistry.WEAK_DRAGON_HEART.orElse(null)){
				toolTip.add(new TranslationTextComponent("ds.description.weakDragonHeart"));
			}
			if (item == ItemRegistry.ELDER_DRAGON_HEART.orElse(null)){
				toolTip.add(new TranslationTextComponent("ds.description.elderDragonHeart"));
			}
		}
	}
	
	
	private static boolean userCheck(){
		if(Minecraft.getInstance().level == null) return false;
		
		UUID playerId = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getGameProfile() != null && Minecraft.getInstance().player.getGameProfile().getId() != null ? Minecraft.getInstance().player.getGameProfile().getId() : null;
		UUID player1 = UUID.fromString("6848748e-f3c1-4c30-91e4-4c7cc3fbeec5");
		UUID player2 = UUID.fromString("05a6e38f-9cd9-3f4a-849c-68841b773e39");
		boolean renderAll = playerId != null && (player1 != null && playerId.equals(player1) || player2 != null && playerId.equals(player2));
		return renderAll;
	}
	
	private static boolean isHelpText(List<ITextProperties> lines){
		if(Minecraft.getInstance().level == null) return false;
		boolean renderAll = userCheck();
		boolean text = false;
		
		String translatedText1 = I18n.get("ds.skill.help");
		String translatedText2 = I18n.get("ds.skill.help.claws");
		String translatedText3 = I18n.get("ds.gui.skins.tooltip.help");
		
		String mergedString = "";
		
		for(ITextProperties comp : lines) {
			if (comp instanceof TranslationTextComponent) {
				TranslationTextComponent textComponent = (TranslationTextComponent)comp;
				if(textComponent.getKey().contains("ds.skill.help") || textComponent.getKey().contains("ds.gui.skins.tooltip.help")){
					text = true;
					break;
				}
			}
			
			mergedString += comp.getString();
		}
		
		if(!text){
			if(mergedString.replace("\n", "").replace(" ", "").contains(translatedText1.replace("\n", "").replace(" ", ""))
			   || mergedString.replace("\n", "").replace(" ", "").contains(translatedText2.replace("\n", "").replace(" ", ""))
			   || mergedString.replace("\n", "").replace(" ", "").contains(translatedText3.replace("\n", "").replace(" ", ""))){
				text = true;
			}
		}
		
		return text || renderAll;
	}
	
	@SubscribeEvent
	public static void onPostTooltipEvent(RenderTooltipEvent.PostText event) {
		boolean render = isHelpText((List<ITextProperties>)event.getLines());
		boolean renderAll = userCheck();
		
		if(!render){
			return;
		}
		
		if(!blink){
			if(tick >= Functions.secondsToTicks(30)){
				blink = true;
				tick = 0;
			}
		}else{
			if(tick >= Functions.secondsToTicks(5)){
				blink = false;
				tick = 0;
			}
		}
		
		tick++;
		
		int x = event.getX();
		int y = event.getY();
		int width = event.getWidth();
		int height = event.getHeight();
		MatrixStack matrix = event.getMatrixStack();
		
		Minecraft.getInstance().getTextureManager().bind(blink ? tooltip_2 : tooltip_1);
		
		int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		
		if (texHeight == 0 || texWidth == 0)
			return;
		
		matrix.pushPose();
		
		RenderSystem.enableBlend();
		
		matrix.translate(0, 0, 410.0);
		
		if(!renderAll) {
			AbstractGui.blit(matrix, x - 8 - 6, y - 8 - 6, 1, 1 % texHeight, 16, 16, texWidth, texHeight);
			AbstractGui.blit(matrix, x + width - 8 + 6, y - 8 - 6, texWidth - 16 - 1, 1 % texHeight, 16, 16, texWidth, texHeight);
			
			AbstractGui.blit(matrix, x - 8 - 6, y + height - 8 + 6, 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
			AbstractGui.blit(matrix, x + width - 8 + 6, y + height - 8 + 6, texWidth - 16 - 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
		}
		
		AbstractGui.blit(matrix, x + (width / 2) - 47, y - 16, 16 + 2 * texWidth + 1, 1 % texHeight, 94, 16, texWidth, texHeight);
		AbstractGui.blit(matrix, x + (width / 2) - 47, y + height, 16 + 2 * texWidth + 1, 1 % texHeight + 16, 94, 16, texWidth, texHeight);
	
		RenderSystem.disableBlend();
		
		matrix.popPose();
	}
	
	@SubscribeEvent
	public static void onTooltipColorEvent(RenderTooltipEvent.Color event) {
		boolean render = isHelpText((List<ITextProperties>)event.getLines());
		boolean screen = Minecraft.getInstance().screen instanceof AbilityScreen;
		
		ItemStack stack = event.getStack();
		
		boolean isSeaFood = !stack.isEmpty() && DragonFoodHandler.getSafeEdibleFoods(DragonType.SEA).contains(stack.getItem());
		boolean isForestFood = !stack.isEmpty()  && DragonFoodHandler.getSafeEdibleFoods(DragonType.FOREST).contains(stack.getItem());
		boolean isCaveFood = !stack.isEmpty()  && DragonFoodHandler.getSafeEdibleFoods(DragonType.CAVE).contains(stack.getItem());
		int foodCount = (isSeaFood ? 1 : 0) + (isForestFood ? 1 : 0) + (isCaveFood ? 1 : 0);
		
		boolean isFood = foodCount == 1;
		
		if(render) {
			int top = new Color(154, 132, 154).getRGB();
			int bottom = new Color(89, 68, 89).getRGB();
			
			event.setBorderStart(top);
			event.setBorderEnd(bottom);
		}else if(screen || isFood){
			DragonType type = DragonStateProvider.getCap(Minecraft.getInstance().player).map((cap) -> cap.getType()).get();
			Color topColor = null;
			Color bottomColor = null;
			
			if(type == DragonType.SEA && screen || isSeaFood){
				topColor = new Color(93, 201, 255);
				bottomColor = new Color(49, 109, 144);
				
			}else if(type == DragonType.FOREST && screen || isForestFood){
				topColor = new Color(0, 255, 148);
				bottomColor = new Color(4, 130, 82);
				
			}else if(type == DragonType.CAVE && screen || isCaveFood){
				topColor = new Color(255, 118, 133);
				bottomColor = new Color(139, 66, 74);
			}
			
			if(topColor != null) {
				event.setBorderStart(topColor.getRGB());
			}
			
			if(bottomColor != null) {
				event.setBorderEnd(bottomColor.getRGB());
			}
		}
	}
}
