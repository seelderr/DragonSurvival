package by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon;

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

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/SmallPoisonParticleData.java
public class SmallPoisonParticleData implements ParticleOptions
{
	public static final Deserializer<SmallPoisonParticleData> DESERIALIZER = new Deserializer<SmallPoisonParticleData>()
	{
		public SmallPoisonParticleData fromCommand(ParticleType<SmallPoisonParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
		{
=======
public class SmallPoisonParticleData implements IParticleData{
	public static final IDeserializer<SmallPoisonParticleData> DESERIALIZER = new IDeserializer<SmallPoisonParticleData>(){
		public SmallPoisonParticleData fromCommand(ParticleType<SmallPoisonParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/SmallPoisonParticleData.java
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new SmallPoisonParticleData(duration, swirls);
		}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/SmallPoisonParticleData.java
		
		public SmallPoisonParticleData fromNetwork(ParticleType<SmallPoisonParticleData> particleTypeIn, FriendlyByteBuf buffer)
		{
=======

		public SmallPoisonParticleData fromNetwork(ParticleType<SmallPoisonParticleData> particleTypeIn, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/SmallPoisonParticleData.java
			return new SmallPoisonParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};

	private final float duration;
	private final boolean swirls;

	public static Codec<SmallPoisonParticleData> CODEC(ParticleType<SmallPoisonParticleData> particleType){
		return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(SmallPoisonParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(SmallPoisonParticleData::getSwirls)).apply(codecBuilder, SmallPoisonParticleData::new));
	}

	public SmallPoisonParticleData(float duration, boolean spins){
		this.duration = duration;
		this.swirls = spins;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/SmallPoisonParticleData.java
	}
	
	@Override
	public void writeToNetwork(FriendlyByteBuf buffer)
	{
=======
	}	@Override
	public void writeToNetwork(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/SmallPoisonParticleData.java
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
	public ParticleType<SmallPoisonParticleData> getType(){
		return DSParticles.POISON.get();
	}






}