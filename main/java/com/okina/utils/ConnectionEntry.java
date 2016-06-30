package com.okina.utils;

import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.processor.ProcessorBase;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author okina
 * @param <T> Type must be TileEntity or ProcessorBase
 */
public class ConnectionEntry<T> {

	//	private T tile;
	private Object world;
	public final int x;
	public final int y;
	public final int z;
	/** -1 : no side information */
	public final int side;

	private ConnectionEntry(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = -1;
	}

	private ConnectionEntry(int x, int y, int z, int side) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}

	public ConnectionEntry(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = -1;
	}

	public ConnectionEntry(World world, int x, int y, int z, int side) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}

	public ConnectionEntry(IProcessorContainer world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = -1;
	}

	public ConnectionEntry(IProcessorContainer world, int x, int y, int z, int side) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}

	/** tile must be TileEntity or ProcessorBase*/
	public ConnectionEntry(T tile, int side) {
		if(tile instanceof TileEntity){
			TileEntity tile2 = (TileEntity) tile;
			//			this.tile = tile;
			world = tile2.getWorldObj();
			this.x = tile2.xCoord;
			this.y = tile2.yCoord;
			this.z = tile2.zCoord;
			this.side = side;
		}else if(tile instanceof ProcessorBase){
			ProcessorBase tile2 = (ProcessorBase) tile;
			//			this.tile = tile;
			world = tile2.pc;
			this.x = tile2.xCoord;
			this.y = tile2.yCoord;
			this.z = tile2.zCoord;
			this.side = side;
		}else{
			throw new IllegalArgumentException("Parameter tile should be TileEntity or ProcessorBase");
		}
	}

	/** tile must be TileEntity or ProcessorBase*/
	public ConnectionEntry(T tile) {
		this(tile, -1);
	}

	public Position getPosition() {
		return new Position(x, y, z);
	}

	public boolean hasTile() {
		return getTile() != null;
	}

	public T getTile() {
		if(world instanceof World){
			return (T) ((World) world).getTileEntity(x, y, z);
		}else if(world instanceof IProcessorContainer){
			return (T) ((IProcessorContainer) world).getProcessor(x, y, z);
		}
		return null;
	}

	public boolean hasProcessor() {
		return getProcessor() != null;
	}

	public ProcessorBase getProcessor() {
		if(world instanceof World){
			TileEntity tile = ((World) world).getTileEntity(x, y, z);
			if(tile instanceof IProcessorContainer){
				return ((IProcessorContainer) tile).getContainProcessor();
			}
		}else if(world instanceof IProcessorContainer){
			return ((IProcessorContainer) world).getProcessor(x, y, z);
		}
		return null;
	}

	public ConnectionEntry<T> setWorld(World world) {
		this.world = world;
		return this;
	}

	public ConnectionEntry<T> setWorld(IProcessorContainer world) {
		this.world = world;
		return this;
	}

	public Object getWorld() {
		return world;
	}

	public ConnectionEntry<T> writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		tag.setInteger("side", side);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		return equals(o, false);
	}

	public boolean equals(Object o, boolean sideMode) {
		if(this == o) return true;
		if(o instanceof ConnectionEntry){
			ConnectionEntry<?> entry = (ConnectionEntry) o;
			return (world == null ? entry.getWorld() == null : (world instanceof World ? world.equals(entry.getWorld()) : true)) && x == entry.x && y == entry.y && z == entry.z && (!sideMode || side == entry.side);
		}
		return false;
	}

	@Override
	public String toString() {
		T tile = getTile();
		if(tile != null){
			return getPosition() + " " + ForgeDirection.getOrientation(side).toString() + " " + tile.getClass().getSimpleName();
		}else{
			return getPosition() + " " + ForgeDirection.getOrientation(side).toString() + " No Tile";
		}
	}

	public static ConnectionEntry createFromNBT(NBTTagCompound tag, World world) {
		ConnectionEntry entry = createFromNBT(tag);
		return entry == null ? null : entry.setWorld(world);
	}

	public static ConnectionEntry createFromNBT(NBTTagCompound tag, IProcessorContainer world) {
		ConnectionEntry entry = createFromNBT(tag);
		return entry == null ? null : entry.setWorld(world);
	}

	private static ConnectionEntry createFromNBT(NBTTagCompound tag) {
		if(tag == null || !tag.hasKey("x") || !tag.hasKey("y") || !tag.hasKey("z") || !tag.hasKey("side")) return null;
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		int side = tag.getInteger("side");
		return new ConnectionEntry(x, y, z, side);
	}

}
