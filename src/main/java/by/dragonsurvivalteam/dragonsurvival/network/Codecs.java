package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;

public class Codecs {
	public static StreamCodec<ByteBuf, AbstractDragonType> ABSTRACT_DRAGON_TYPE = new StreamCodec<>() {
		@Override
		public AbstractDragonType decode(ByteBuf pBuffer) {
			String typeS = Utf8String.read(pBuffer, 32);
			return typeS.equals("none") ? null : DragonTypes.getStaticSubtype(typeS);
		}

		@Override
		public void encode(ByteBuf pBuffer, AbstractDragonType pValue) {
			Utf8String.write(pBuffer, pValue.getSubtypeName(), 32);
		}
	};

	public static StreamCodec<ByteBuf, AbstractDragonBody> ABSTRACT_DRAGON_BODY = new StreamCodec<>() {
		@Override
		public AbstractDragonBody decode(ByteBuf pBuffer) {
			String typeS = Utf8String.read(pBuffer, 32);
			return typeS.equals("none") ? null : DragonBodies.getStatic(typeS);
		}

		@Override
		public void encode(ByteBuf pBuffer, AbstractDragonBody pValue) {
			Utf8String.write(pBuffer, pValue.getBodyName(), 32);
		}
	};
}
