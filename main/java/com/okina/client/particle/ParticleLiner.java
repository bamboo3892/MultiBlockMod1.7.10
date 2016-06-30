package com.okina.client.particle;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * This class is templete ParticleBase class
 * I made this only for instruction.
 * @author masaki
 */
public class ParticleLiner extends ParticleBase {

	private float baseScale;
	private double startX;
	private double startY;
	private double startZ;
	private double endX;
	private double endY;
	private double endZ;

	/**all coordinate parameter should be relative one*/
	protected ParticleLiner(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		super(world, startX, startY, startZ);
		this.startX = posX = startX;
		this.startY = posY = startY;
		this.startZ = posZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
		float f = rand.nextFloat() * 0.6F + 0.4F;
		baseScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleGreen *= 0.5F;
		particleBlue *= 0.1F;
		particleMaxAge = (int) (Math.random() * 2.0D) + 8;
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	@Override
	protected void updateScale(float ageScaled) {
		ageScaled = MathHelper.sin((float) (ageScaled * Math.PI));
		particleScale = baseScale * ageScaled;
	}

	@Override
	protected void updatePosition(float ageScaled) {
		ageScaled = (float) ((-1) * (4F / 9F) * (ageScaled - 1.5) * (ageScaled - 1.5) + 1);
		posX = startX + endX * ageScaled;
		posY = startY + endY * ageScaled;
		posZ = startZ + endZ * ageScaled;
	}

}
