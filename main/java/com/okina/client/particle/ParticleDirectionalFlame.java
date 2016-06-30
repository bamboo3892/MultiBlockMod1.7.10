package com.okina.client.particle;

import net.minecraft.world.World;

public class ParticleDirectionalFlame extends ParticleBase {

	private float baseScale;
	private double startX;
	private double startY;
	private double startZ;
	private double endX;
	private double endY;
	private double endZ;

	/**end coordinates parameter should be relative one*/
	protected ParticleDirectionalFlame(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		super(world, startX, startY, startZ);
		this.startX = posX = startX + (Math.random() * 0.4 - 0.2);
		this.startY = posY = startY + (Math.random() * 0.4 - 0.2);
		this.startZ = posZ = startZ + (Math.random() * 0.4 - 0.2);
		this.endX = endX + (Math.random() * 0.4 - 0.2);
		this.endY = endY + (Math.random() * 0.4 - 0.2);
		this.endZ = endZ + (Math.random() * 0.4 - 0.2);
		baseScale = particleScale = rand.nextFloat() * 0.2F + 1.5F;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		particleMaxAge = (int) (Math.random() * 2.0D) + 8;
		setParticleTextureIndex(48);
	}

	public ParticleDirectionalFlame(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5]);
	}

	@Override
	protected void updateScale(float ageScaled) {
		//		ageScaled = (float) Math.sin(ageScaled * Math.PI / 2);
		//		particleScale = baseScale * ageScaled;
	}

	@Override
	protected void updatePosition(float ageScaled) {
		//		ageScaled = (float) ((-1) * (4F / 9F) * (ageScaled - 1.5) * (ageScaled - 1.5) + 1);
		posX = startX + endX * ageScaled;
		posY = startY + endY * ageScaled;
		posZ = startZ + endZ * ageScaled;
	}

}
