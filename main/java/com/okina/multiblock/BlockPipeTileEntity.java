package com.okina.multiblock;

import java.util.ArrayList;
import java.util.List;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.IPipeConnectionUser;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.Position;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPipeTileEntity extends TileEntity implements ISimpleTilePacketUser {

	public boolean[] connection = new boolean[6];
	private boolean needUpdate = true;

	public BlockPipeTileEntity() {

	}

	@Override
	public void updateEntity() {
		if(needUpdate){
			checkConnection();
			needUpdate = false;
		}
	}

	/**effective only on client*/
	public void checkConnection() {
		boolean flag = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			boolean b = false;
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(tile instanceof ProcessorContainerTileEntity){
				if(((ProcessorContainerTileEntity) tile).getContainProcessor() != null){
					b = ((ProcessorContainerTileEntity) tile).getContainProcessor().flagIO[dir.getOpposite().ordinal()] != 2;
				}
			}else if(tile instanceof BlockPipeTileEntity && getBlockMetadata() == tile.getBlockMetadata()){
				b = true;
			}
			if(connection[dir.ordinal()] != b) flag = true;
			connection[dir.ordinal()] = b;
		}
		if(flag){
			if(worldObj.isRemote){
				//				worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
			}else{
				TestCore.proxy.markForTileUpdate(getPosition(), PacketType.RENDER);
			}
		}
	}

	/**objective tile must extend ConstructBaseTileEntity*/
	@SuppressWarnings("rawtypes")
	public void findConnection(List<BlockPipeTileEntity> checkedPipes, List<ConnectionEntry> tiles, Class tileClass, boolean sideMode) {
		checkedPipes.add(this);
		for (ForgeDirection dir : ForgeDirection.values()){
			if(dir.ordinal() >= 6) break;
			int newX = xCoord + dir.offsetX;
			int newY = yCoord + dir.offsetY;
			int newZ = zCoord + dir.offsetZ;
			TileEntity t = worldObj.getTileEntity(newX, newY, newZ);
			if(t == null) continue;
			if(t instanceof BlockPipeTileEntity && getBlockMetadata() == t.getBlockMetadata()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) t;
				if(!checkedPipes.contains(pipe)){
					pipe.findConnection(checkedPipes, tiles, tileClass, sideMode);
				}
			}else if(t instanceof ProcessorContainerTileEntity){
				ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) t;
				if(tile.getContainProcessor() != null){
					if(tileClass.isAssignableFrom(tile.getContainProcessor().getClass())){
						if(tile.getContainProcessor().flagIO[dir.getOpposite().ordinal()] == 0){
							if(sideMode){
								ConnectionEntry<ProcessorBase> entry = new ConnectionEntry<ProcessorBase>(tile.getContainProcessor(), dir.getOpposite().ordinal());
								if(!tiles.contains(entry)){
									tiles.add(entry);
								}
							}else{
								ConnectionEntry<ProcessorBase> entry = new ConnectionEntry<ProcessorBase>(tile.getContainProcessor());
								if(!tiles.contains(entry)){
									tiles.add(entry);
								}
							}
						}
					}
				}
			}
		}
	}

	public void onTileRemoved() {
		invalidate();
		if(worldObj.isRemote){
			//do nothing
		}else{
			ArrayList<BlockPipeTileEntity> checkedPipes = new ArrayList<BlockPipeTileEntity>();
			@SuppressWarnings("rawtypes")
			ArrayList<ConnectionEntry> tiles = new ArrayList<ConnectionEntry>();
			Class<?> tileClass = IPipeConnectionUser.class;
			checkedPipes.add(this);
			for (ForgeDirection dir : ForgeDirection.values()){
				if(dir.ordinal() >= 6) break;
				int newX = xCoord + dir.offsetX;
				int newY = yCoord + dir.offsetY;
				int newZ = zCoord + dir.offsetZ;
				TileEntity t = worldObj.getTileEntity(newX, newY, newZ);
				if(t == null || t.isInvalid()){
					continue;
				}else if(t instanceof BlockPipeTileEntity && getBlockMetadata() == t.getBlockMetadata()){
					BlockPipeTileEntity pipe = (BlockPipeTileEntity) t;
					if(!checkedPipes.contains(pipe)){
						pipe.findAllConnection(checkedPipes, tiles, tileClass);
					}
				}else if(t instanceof ProcessorContainerTileEntity){
					ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) t;
					if(tile.getContainProcessor() != null){
						if(tileClass.isAssignableFrom(tile.getContainProcessor().getClass())){
							ConnectionEntry<ProcessorBase> entry = new ConnectionEntry<ProcessorBase>(tile.getContainProcessor());
							if(!tiles.contains(entry)){
								tiles.add(entry);
							}
						}
					}
				}
			}
			for (ConnectionEntry<?> tile : tiles){
				((IPipeConnectionUser) tile.getTile()).needUpdateEntry();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void findAllConnection(List<BlockPipeTileEntity> checkedPipes, List<ConnectionEntry> tiles, Class tileClass) {
		checkedPipes.add(this);
		for (ForgeDirection dir : ForgeDirection.values()){
			if(dir.ordinal() >= 6) break;
			int newX = xCoord + dir.offsetX;
			int newY = yCoord + dir.offsetY;
			int newZ = zCoord + dir.offsetZ;
			TileEntity t = worldObj.getTileEntity(newX, newY, newZ);
			if(t == null) continue;
			if(t instanceof BlockPipeTileEntity && getBlockMetadata() == t.getBlockMetadata()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) t;
				if(!checkedPipes.contains(pipe)){
					pipe.findConnection(checkedPipes, tiles, tileClass, false);
				}
			}else if(t instanceof ProcessorContainerTileEntity){
				ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) t;
				if(tileClass.isAssignableFrom(tile.getContainProcessor().getClass())){
					ConnectionEntry<ProcessorBase> entry = new ConnectionEntry<ProcessorBase>(tile.getContainProcessor());
					if(!tiles.contains(entry)){
						tiles.add(entry);
					}
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.RENDER){
			return new SimpleTilePacket(this, PacketType.RENDER, 0);
		}
		return null;
	}
	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.RENDER){
			checkConnection();
		}
	}
	@Override
	public Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

}
