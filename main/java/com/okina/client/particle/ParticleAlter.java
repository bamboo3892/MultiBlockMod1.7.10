package com.okina.client.particle;

import java.awt.Color;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.mode.AlterMode;
import com.okina.utils.Bezier;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ParticleAlter extends ParticleBase {

	private AlterMode alter;
	private double centerX;
	private double centerY;
	private double centerZ;
	private float angle1;
	private float angle2;
	private Vec3 startVec;
	private float maxAngle = (float) (10 + Math.random() * 10);
	private int color2;

	public ParticleAlter(World world, AlterMode alter, double centerX, double centerY, double centerZ, double radius, float angle1, float angle2) {
		super(world, 0, 0, 0, 0xFFFFFF);
		particleRed = particleGreen = particleBlue = rand.nextFloat() * 0.4F;
		color2 = new Color(particleRed, particleGreen, particleBlue).getRGB();
		this.alter = alter;
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.angle1 = angle1;
		this.angle2 = angle2;
		startVec = Vec3.createVectorHelper(radius, 0, 0);
		startVec.rotateAroundY((float) (Math.random() * Math.PI * 2));
		particleScale = 0.3f;
		particleTextureIndexX = 4;
		particleTextureIndexY = 2;
		textureSizeX = 4;
		textureSizeY = 4;
		particleMaxAge = 800;
	}

	public ParticleAlter(World world, Object[] objects) {
		this(world, (AlterMode) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Float) objects[5], (Float) objects[6]);
	}

	@Override
	protected void updatePosition(float ageScaled) {
		Vec3 vec = startVec.addVector(0, 0, 0);
		vec.rotateAroundY(ageScaled * maxAngle);
		vec.rotateAroundX(angle1);
		vec.rotateAroundY(angle2);
		vec = vec.addVector(centerX, centerY, centerZ);

		posX = vec.xCoord;
		posY = vec.yCoord;
		posZ = vec.zCoord;

		if(worldObj.getTotalWorldTime() % 4 == 0){
			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_BEZIER, new Bezier(posX, posY, posZ, alter.xCoord + 0.5, alter.yCoord + 0.5, alter.zCoord + 0.5, 0, 0, 0, 0, 0, 0), color2);
		}

		if(alter.processingTicks == -1 || !alter.container.isValid || alter.container.mode2 != alter){
			setDead();
		}
	}

}
