package com.okina.client.particle;

import net.minecraft.world.World;

public class ParticleEnergyProvide extends ParticleBase {

	private float portalParticleScale;
	private double portalPosX;
	private double portalPosY;
	private double portalPosZ;

	public ParticleEnergyProvide(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		super(world, startX, startY, startZ);
		motionX = endX;
		motionY = endY;
		motionZ = endZ;
		portalPosX = posX = startX;
		portalPosY = posY = startY;
		portalPosZ = posZ = startZ;
		float f = rand.nextFloat() * 0.6F + 0.4F;
		portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleGreen *= 0.5F;
		particleBlue *= 0.1F;
		particleMaxAge = (int) (Math.random() * 2.0D) + 8;
		noClip = true;
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	public ParticleEnergyProvide(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5]);
	}

	public ParticleEnergyProvide set1() {
		float f = rand.nextFloat() * 0.6F + 0.4F;
		portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleGreen *= 0.3F;
		particleRed *= 0.9F;
		particleMaxAge = (int) (Math.random() * 10.0D) + 40;
		return this;
	}

	@Override
	protected void updateScale(float ageScaled) {
		particleScale = portalParticleScale * (1 - ageScaled);
	}

	@Override
	protected void updatePosition(float ageScaled) {
		float f = (float) ((-1) * (4F / 9F) * (ageScaled - 1.5) * (ageScaled - 1.5) + 1);
		posX = portalPosX + motionX * f;
		posY = portalPosY + motionY * f;
		posZ = portalPosZ + motionZ * f;
	}

}
