package com.okina.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ParticleGrower extends ParticleBase {

	private float portalParticleScale;
	private double portalPosX;
	private double portalPosY;
	private double portalPosZ;

	public ParticleGrower(World world, double endX, double endY, double endZ, double startX, double startY, double startZ) {
		super(world, endX, endY, endZ);
		motionX = startX;
		motionY = startY;
		motionZ = startZ;
		portalPosX = posX = endX;
		portalPosY = posY = endY;
		portalPosZ = posZ = endZ;
		float f = rand.nextFloat() * 0.6F + 0.4F;
		portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleGreen *= 0.9F;
		particleRed *= 0.3F;
		particleMaxAge = (int) (Math.random() * 10.0D) + 40;
		noClip = true;
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	public ParticleGrower(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5]);
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialTick, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		float f6 = (particleAge + partialTick) / particleMaxAge;
		f6 = 1.0F - f6;
		f6 *= f6;
		f6 = 1.0F - f6;
		particleScale = portalParticleScale * f6;
		super.renderParticle(tessellator, partialTick, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
	}

	@Override
	protected void updateScale(float ageScaled) {
		float f6 = ageScaled;
		f6 = 1.0F - f6;
		f6 *= f6;
		f6 = 1.0F - f6;
		particleScale = portalParticleScale * f6;
	}

	@Override
	protected void updatePosition(float ageScaled) {
		float f = ageScaled;
		f = -f + f * f * 2.0F;
		f = 1.0F - f;
		posX = portalPosX + motionX * f;
		posY = portalPosY + motionY * f;
		posZ = portalPosZ + motionZ * f;
	}

}
