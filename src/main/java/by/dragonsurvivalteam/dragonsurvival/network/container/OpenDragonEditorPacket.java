package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenDragonEditorPacket implements IMessage<OpenDragonEditorPacket>{

	public OpenDragonEditorPacket(){}

	@Override
	public void encode(OpenDragonEditorPacket message, FriendlyByteBuf buffer){

	}

	@Override
	public OpenDragonEditorPacket decode(FriendlyByteBuf buffer){
		return new OpenDragonEditorPacket();
	}

	@Override
	public void handle(OpenDragonEditorPacket message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		supplier.get().setPacketHandled(true);
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(OpenDragonEditorPacket message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Minecraft.getInstance().setScreen(new DragonEditorScreen(Minecraft.getInstance().screen));
			context.setPacketHandled(true);
		});
	}
}