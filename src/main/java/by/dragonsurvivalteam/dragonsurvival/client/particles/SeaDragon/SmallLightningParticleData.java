package by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon;

import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/SmallLightningParticleData.java
public class SmallLightningParticleData implements ParticleOptions
{
	public static final Deserializer<SmallLightningParticleData> DESERIALIZER = new Deserializer<SmallLightningParticleData>()
	{
		public SmallLightningParticleData fromCommand(ParticleType<SmallLightningParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
		{
=======
public class SmallLightningParticleData implements IParticleData{
	public static final IDeserializer<SmallLightningParticleData> DESERIALIZER = new IDeserializer<SmallLightningParticleData>(){
		public SmallLightningParticleData fromCommand(ParticleType<SmallLightningParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/SmallLightningParticleData.java
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new SmallLightningParticleData(duration, swirls);
		}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/SmallLightningParticleData.java
		
		public SmallLightningParticleData fromNetwork(ParticleType<SmallLightningParticleData> particleTypeIn, FriendlyByteBuf buffer)
		{
=======

		public SmallLightningParticleData fromNetwork(ParticleType<SmallLightningParticleData> particleTypeIn, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/SmallLightningParticleData.java
			return new SmallLightningParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};

	private final float duration;
	private final boolean swirls;

	public static Codec<SmallLightningParticleData> CODEC(ParticleType<SmallLightningParticleData> particleType){
		return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(SmallLightningParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(SmallLightningParticleData::getSwirls)).apply(codecBuilder, SmallLightningParticleData::new));
	}

	public SmallLightningParticleData(float duration, boolean spins){
		this.duration = duration;
		this.swirls = spins;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/SmallLightningParticleData.java
	}
	
	@Override
	public void writeToNetwork(FriendlyByteBuf buffer)
	{
=======
	}	@Override
	public void writeToNetwork(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/SmallLightningParticleData.java
		buffer.writeFloat(this.duration);
		buffer.writeBoolean(this.swirls);
	}

	@OnlyIn( Dist.CLIENT )
	public float getDuration(){
		return this.duration;
	}	@SuppressWarnings( "deprecation" )
	@Override
	public String writeToString(){
		return String.format(Locale.ROOT, "%s %.2f %b", Registry.PARTICLE_TYPE.getKey(this.getType()), this.duration, this.swirls);
	}

	@OnlyIn( Dist.CLIENT )
	public boolean getSwirls(){
		return this.swirls;
	}	@Override
	public ParticleType<SmallLightningParticleData> getType(){
		return DSParticles.LIGHTNING.get();
	}






}