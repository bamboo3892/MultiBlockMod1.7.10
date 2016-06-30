package com.okina.utils;

import java.util.ArrayList;

public class RectangularSolid {

	public int minX = Integer.MAX_VALUE;
	public int minY = Integer.MAX_VALUE;
	public int minZ = Integer.MAX_VALUE;
	public int maxX = Integer.MIN_VALUE;
	public int maxY = Integer.MIN_VALUE;
	public int maxZ = Integer.MIN_VALUE;

	public RectangularSolid() {}

	public RectangularSolid(int xSize, int ySize, int zSize) {
		if(xSize < 1 || ySize < 1 || zSize < 1) return;
		minX = 0;
		minY = 0;
		minZ = 0;
		maxX = xSize - 1;
		maxY = ySize - 1;
		maxZ = zSize - 1;
	}

	public RectangularSolid(Position start, int xSize, int ySize, int zSize) {
		minX = start.x;
		minY = start.y;
		minZ = start.z;
		maxX = minX + xSize;
		maxY = minY + ySize;
		maxZ = minZ + zSize;
	}

	public RectangularSolid(Position minPoint, Position maxPoint) {
		if(minPoint == null || maxPoint == null) throw new IllegalArgumentException();
		minX = minPoint.x;
		minY = minPoint.y;
		minZ = minPoint.z;
		maxX = maxPoint.x;
		maxY = maxPoint.y;
		maxZ = maxPoint.z;
	}

	public RectangularSolid(ArrayList<Position> list) {
		if(list.size() != 8){
			System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
			return;
		}
		for (Position pos : list){
			minX = minX > pos.x ? pos.x : minX;
			minY = minY > pos.y ? pos.y : minY;
			minZ = minZ > pos.z ? pos.z : minZ;
			maxX = maxX < pos.x ? pos.x : maxX;
			maxY = maxY < pos.y ? pos.y : maxY;
			maxZ = maxZ < pos.z ? pos.z : maxZ;
		}
	}

	public Position getMinPoint() {
		return new Position(minX, minY, minZ);
	}

	public Position getMaxPoint() {
		return new Position(maxX, maxY, maxZ);
	}

	public Position getCenterPoint() {
		return new Position((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
	}

	public int getXSize() {
		return maxX - minX + 1;
	}

	public int getYSize() {
		return maxY - minY + 1;
	}

	public int getZSize() {
		return maxZ - minZ + 1;
	}

	public boolean isInclude(Position p) {
		if(p != null){
			return p.x >= minX && p.y >= minY && p.z >= minZ && p.x <= maxX && p.y <= maxY && p.z <= maxZ;
		}
		return false;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param relative
	 * @return index
	 */
	public int toIndex(int x, int y, int z, boolean relative) {
		if(!relative){
			x -= minX;
			y -= minY;
			z -= minZ;
		}
		int index = x * getYSize() * getZSize() + y * getZSize() + z;
		return index;
	}

	public int toIndex(Position p, boolean relative) {
		return toIndex(p.x, p.y, p.z, relative);
	}

	public int getIndexSize() {
		return getXSize() * getYSize() * getZSize();
	}

	public Position toCoord(int index) {
		int z = index % getZSize();
		index -= z;
		index /= getZSize();
		int y = index % getYSize();
		index -= y;
		index /= getYSize();
		int x = index;
		return Position.sum(new Position(x, y, z), getMinPoint());
	}

	@Override
	public String toString() {
		String x = " minX = " + minX + "\t maxX = " + maxX;
		String y = " minY = " + minY + "\tmaxY = " + maxY;
		String z = " minZ = " + minZ + "\tmaxZ = " + maxZ;
		return x + "\n" + y + "\n" + z;
	}

	public void set(ArrayList<Position> list) {
		if(list.size() != 8){
			System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
			return;
		}
		for (Position pos : list){
			minX = minX > pos.x ? pos.x : minX;
			minY = minY > pos.y ? pos.y : minY;
			minZ = minZ > pos.z ? pos.z : minZ;
			maxX = maxX < pos.x ? pos.x : maxX;
			maxY = maxY < pos.y ? pos.y : maxY;
			maxZ = maxZ < pos.z ? pos.z : maxZ;
		}
	}

}
