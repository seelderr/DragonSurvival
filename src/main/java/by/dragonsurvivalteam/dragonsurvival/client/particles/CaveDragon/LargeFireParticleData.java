package by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon;

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

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/CaveDragon/LargeFireParticleData.java
public class LargeFireParticleData implements ParticleOptions
{
	public static final Deserializer<LargeFireParticleData> DESERIALIZER = new Deserializer<LargeFireParticleData>()
	{
		public LargeFireParticleData fromCommand(ParticleType<LargeFireParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
		{
=======
public class LargeFireParticleData implements IParticleData{
	public static final IDeserializer<LargeFireParticleData> DESERIALIZER = new IDeserializer<LargeFireParticleData>(){
		public LargeFireParticleData fromCommand(ParticleType<LargeFireParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/CaveDragon/LargeFireParticleData.java
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new LargeFireParticleData(duration, swirls);
		}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/CaveDragon/LargeFireParticleData.java
		
		public LargeFireParticleData fromNetwork(ParticleType<LargeFireParticleData> particleTypeIn, FriendlyByteBuf buffer)
		{
=======

		public LargeFireParticleData fromNetwork(ParticleType<LargeFireParticleData> particleTypeIn, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/CaveDragon/LargeFireParticleData.java
			return new LargeFireParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};

	private final float duration;
	private final boolean swirls;

	public static Codec<LargeFireParticleData> CODEC(ParticleType<LargeFireParticleData> particleType){
		return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(LargeFireParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(LargeFireParticleData::getSwirls)).apply(codecBuilder, LargeFireParticleData::new));
	}

	public LargeFireParticleData(float duration, boolean spins){
		this.duration = duration;
		this.swirls = spins;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/CaveDragon/LargeFireParticleData.java
	}
	
	@Override
	public void writeToNetwork(FriendlyByteBuf buffer)
	{
=======
	}	@Override
	public void writeToNetwork(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/CaveDragon/LargeFireParticleData.java
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
	public ParticleType<LargeFireParticleData> getType(){
		return DSParticles.LARGE_FIRE.get();
	}






}