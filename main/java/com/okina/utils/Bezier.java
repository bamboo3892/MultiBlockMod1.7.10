package com.okina.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class Bezier {

	public double[] start = new double[3];
	public double[] end = new double[3];
	public double[] startVec = new double[3];
	public double[] endVec = new double[3];

	private Bezier() {

	}

	public Bezier(double startX, double startY, double startZ, double endX, double endY, double endZ, double startVecX, double startvecY, double startVecZ, double endVecX, double endVecY, double endVecZ) {
		this(new double[] { startX, startY, startZ }, new double[] { endX, endY, endZ }, new double[] { startVecX, startvecY, startVecZ }, new double[] { endVecX, endVecY, endVecZ });
	}

	public Bezier(double[] start, double[] end, double[] startVec, double[] endVec) {
		for (int i = 0; i < 3; i++){
			this.start[i] = start[i];
			this.end[i] = end[i];
			this.startVec[i] = startVec[i];
			this.endVec[i] = endVec[i];
		}
	}

	public Bezier(Vec3 start, Vec3 end, Vec3 startVec, Vec3 endVec) {
		this.start[0] = start.xCoord;
		this.start[1] = start.yCoord;
		this.start[2] = start.zCoord;
		this.end[0] = end.xCoord;
		this.end[1] = end.yCoord;
		this.end[2] = end.zCoord;
		this.startVec[0] = startVec.xCoord;
		this.startVec[1] = startVec.yCoord;
		this.startVec[2] = startVec.zCoord;
		this.endVec[0] = endVec.xCoord;
		this.endVec[1] = endVec.yCoord;
		this.endVec[2] = endVec.zCoord;
	}

	public double[] getPosition(double t) {
		if(t > 1){
			t = 1;
		}else if(t < 0){
			t = 0;
		}
		double[] pos = new double[3];
		double t2 = 1 - t;
		for (int i = 0; i < 3; i++){
			pos[i] = end[i] * t * t * t + 3 * (end[i] - endVec[i] / 3) * t2 * t * t + 3 * (start[i] + startVec[i] / 3) * t2 * t2 * t + start[i] * t2 * t2 * t2;
		}
		return pos;
	}

	public double[] getVelocity(double t) {
		if(t > 1){
			t = 1;
		}else if(t < 0){
			t = 0;
		}
		double[] vec = new double[3];
		double t2 = 1 - t;
		for (int i = 0; i < 3; i++){
			vec[i] = 3 * (t * t * endVec[i] / 3 + 2 * t * t2 * (end[i] - start[i] - endVec[i] / 3 - startVec[i] / 3) - t2 * t2 * startVec[i] / 3);
		}
		return vec;
	}

	public double getLength() {
		double length = 0;
		double t = 0;
		double[] vec;
		for (int i = 0; i < 32; i++){
			t = i / 32d;
			vec = getVelocity(t);
			length += Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]) / 32d;
		}
		return length;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setDouble("startX", start[0]);
		tag.setDouble("startY", start[1]);
		tag.setDouble("startZ", start[2]);
		tag.setDouble("endX", end[0]);
		tag.setDouble("endY", end[1]);
		tag.setDouble("endZ", end[2]);
		tag.setDouble("startVecX", startVec[0]);
		tag.setDouble("startVecY", startVec[1]);
		tag.setDouble("startVecZ", startVec[2]);
		tag.setDouble("endVecX", endVec[0]);
		tag.setDouble("endVecY", endVec[1]);
		tag.setDouble("endVecZ", endVec[2]);
	}

	public void readFromNBT(NBTTagCompound tag) {
		start[0] = tag.getDouble("startX");
		start[1] = tag.getDouble("startY");
		start[2] = tag.getDouble("startZ");
		end[0] = tag.getDouble("endX");
		end[1] = tag.getDouble("endY");
		end[2] = tag.getDouble("endZ");
		startVec[0] = tag.getDouble("startVecX");
		startVec[1] = tag.getDouble("startVecY");
		startVec[2] = tag.getDouble("startVecZ");
		endVec[0] = tag.getDouble("endVecX");
		endVec[1] = tag.getDouble("endVecY");
		endVec[2] = tag.getDouble("endVecZ");
	}

	public static Bezier createFromNBT(NBTTagCompound tag) {
		Bezier b = new Bezier();
		b.readFromNBT(tag);
		return b;
	}

}
