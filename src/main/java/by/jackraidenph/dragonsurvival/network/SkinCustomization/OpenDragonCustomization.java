package by.jackraidenph.dragonsurvival.network.SkinCustomization;

import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenDragonCustomization implements IMessage<OpenDragonCustomization>
{
	
	public OpenDragonCustomization() {}

	@Override
	public void encode(OpenDragonCustomization message, PacketBuffer buffer) {

	}
	
	@Override
	public OpenDragonCustomization decode(PacketBuffer buffer) {
		return new OpenDragonCustomization();
	}
	
	@Override
	public void handle(OpenDragonCustomization message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void runClient(OpenDragonCustomization message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Minecraft.getInstance().setScreen(new DragonCustomizationScreen(Minecraft.getInstance().screen));
			context.setPacketHandled(true);
		});
	}
}