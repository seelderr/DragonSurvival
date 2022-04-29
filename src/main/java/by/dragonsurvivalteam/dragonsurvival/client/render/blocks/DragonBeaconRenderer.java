package by.dragonsurvivalteam.dragonsurvival.client.render.blocks;

import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DragonBeaconTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class DragonBeaconRenderer implements BlockEntityRenderer<DragonBeaconTileEntity>{

	public DragonBeaconRenderer(BlockEntityRendererProvider.Context pContext){}

	@Override
	public void render(DragonBeaconTileEntity dragonBeaconEntity, float v, PoseStack PoseStack, MultiBufferSource iRenderTypeBuffer, int light, int overlay){
		dragonBeaconEntity.tick++;
		PoseStack.pushPose();
		DragonBeaconTileEntity.Type type = dragonBeaconEntity.type;
		Item item = DSBlocks.dragonBeacon.asItem();
		ClientLevel clientWorld = (ClientLevel)dragonBeaconEntity.getLevel();
		if(dragonBeaconEntity.getLevel().getBlockState(dragonBeaconEntity.getBlockPos().below()).is(DSBlocks.dragonMemoryBlock)){
			Minecraft minecraft = Minecraft.getInstance();
			Random random = clientWorld.random;
			double x = 0.25 + random.nextInt(5) / 10d;
			double z = 0.25 + random.nextInt(5) / 10d;
			switch(type){
				case PEACE -> {
					item = DSItems.passivePeaceBeacon;
					try{
						if(!minecraft.isPaused() && dragonBeaconEntity.tick % 5 == 0){
							clientWorld.addParticle(DSParticles.peaceBeaconParticle.getDeserializer().fromCommand(DSParticles.peaceBeaconParticle, new StringReader("")), dragonBeaconEntity.getX() + x, dragonBeaconEntity.getY() + 0.5, dragonBeaconEntity.getZ() + z, 0, 0, 0);
						}
					}catch(CommandSyntaxException e){
						e.printStackTrace();
					}
				}
				case MAGIC -> {
					item = DSItems.passiveMagicBeacon;
					try{
						if(!minecraft.isPaused() && dragonBeaconEntity.tick % 5 == 0){
							clientWorld.addParticle(DSParticles.magicBeaconParticle.getDeserializer().fromCommand(DSParticles.magicBeaconParticle, new StringReader("")), dragonBeaconEntity.getX() + x, dragonBeaconEntity.getY() + 0.5, dragonBeaconEntity.getZ() + z, 0, 0, 0);
						}
					}catch(CommandSyntaxException e){
						e.printStackTrace();
					}
				}
				case FIRE -> {
					item = DSItems.passiveFireBeacon;
					try{
						if(!minecraft.isPaused() && dragonBeaconEntity.tick % 5 == 0){
							clientWorld.addParticle(DSParticles.fireBeaconParticle.getDeserializer().fromCommand(DSParticles.fireBeaconParticle, new StringReader("")), dragonBeaconEntity.getX() + x, dragonBeaconEntity.getY() + 0.5, dragonBeaconEntity.getZ() + z, 0, 0, 0);
						}
					}catch(CommandSyntaxException e){
						e.printStackTrace();
					}
				}
			}
		}

		item = switch(type){
			case PEACE -> DSBlocks.peaceDragonBeacon.asItem();
			case MAGIC -> DSBlocks.magicDragonBeacon.asItem();
			case FIRE -> DSBlocks.fireDragonBeacon.asItem();
			default -> item;
		};
		float f1 = Mth.sin(((float)dragonBeaconEntity.tick + v) / 20.0F + dragonBeaconEntity.bobOffs) * 0.1F + 0.1F;
		PoseStack.translate(0.5, 0.25 + (f1), 0.5);
		PoseStack.mulPose(Vector3f.YP.rotationDegrees(dragonBeaconEntity.tick));
		PoseStack.scale(2, 2, 2);
		Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(item), TransformType.GROUND, light, overlay, PoseStack, iRenderTypeBuffer, 0);
		PoseStack.popPose();
	}
}