package com.okina.multiblock;

import java.util.ArrayList;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.multiblock.construct.processor.AlterProcessor;
import com.okina.multiblock.construct.processor.EnergyProviderProcessor;
import com.okina.utils.Position;
import com.okina.utils.RectangularSolid;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFrameTileEntity extends TileEntity {

	public int[] length = new int[6];

	public BlockFrameTileEntity() {

	}

	@Override
	public void updateEntity() {
		checkConnection();
	}

	public void checkConnection() {
		length = new int[6];
		for (int side = 0; side < 6; side++){
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(length[dir.ordinal()] == -1) continue;
			for (int n = 1; n < 16; n++){
				int newX = xCoord + dir.offsetX * n;
				int newY = yCoord + dir.offsetY * n;
				int newZ = zCoord + dir.offsetZ * n;
				if(worldObj.getTileEntity(newX, newY, newZ) instanceof BlockFrameTileEntity){
					length[dir.ordinal()] = n;
					length[dir.getOpposite().ordinal()] = -1;
					break;
				}
			}
		}
	}

	public boolean tryConstruct(EntityPlayer player) {
		checkConnection();
		if(worldObj.isRemote) return true;
		//check
		ArrayList<Position> interfaceList = new ArrayList<Position>();
		RectangularSolid solid = new RectangularSolid();
		if(!checkCubicFrame(interfaceList, solid, player)) return false;
		player.addChatMessage(new ChatComponentText("Construct Success!"));
		ItemStack itemStack = new ItemStack(TestCore.multiBlockCore, 1);
		solid = new RectangularSolid(solid.getMinPoint().sum(1, 1, 1), solid.getMaxPoint().sum(-1, -1, -1));
		writeNBTToItemStack(itemStack, solid, interfaceList, player.capabilities.isCreativeMode);
		if(worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord, yCoord, zCoord, itemStack))){
			if(!player.capabilities.isCreativeMode){
				for (int x = -1; x < solid.getXSize() + 1; x++){
					for (int y = -1; y < solid.getYSize() + 1; y++){
						for (int z = -1; z < solid.getZSize() + 1; z++){

							worldObj.setBlockToAir(x + solid.minX, y + solid.minY, z + solid.minZ);
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param interfaceList
	 * @param solid absolute coordinate
	 * @param player
	 * @return
	 */
	private boolean checkCubicFrame(ArrayList<Position> interfaceList, RectangularSolid solid, EntityPlayer player) {
		ArrayList<Position> list = new ArrayList<Position>();//absolute
		list.add(new Position(0, 0, 0));
		for (int n = 0; n < 6; n++){
			if(length[n] > 0){
				ForgeDirection dir = ForgeDirection.getOrientation(n);
				list.add(new Position(dir.offsetX * length[n], dir.offsetY * length[n], dir.offsetZ * length[n]));
			}
		}
		if(list.size() != 4) return false;
		list.add(Position.sum(list.get(1), list.get(2)));
		list.add(Position.sum(list.get(2), list.get(3)));
		list.add(Position.sum(list.get(3), list.get(1)));
		list.add(Position.sum(list.get(3), list.get(4)));

		//frame core
		Position tilePos = new Position(xCoord, yCoord, zCoord);
		for (int i = 0; i < list.size(); i++){
			list.set(i, Position.sum(list.get(i), tilePos));
			Position p = list.get(i);
			if(!(worldObj.getTileEntity(p.x, p.y, p.z) instanceof BlockFrameTileEntity)){
				player.addChatMessage(new ChatComponentText("Place Frame Core in Cube Vertexes"));
				return false;
			}
		}
		solid.set(list);

		//		for (Position p : list){
		//			p = Position.sum(p, new Position(xCoord, yCoord, zCoord));
		//			if(!(worldObj.getTileEntity(p.x, p.y, p.z) instanceof BlockFrameTileEntity)){
		//				return false;
		//			}
		//		}

		//frame line
		int k[] = new int[3];//length
		for (int kkk = 0; kkk < 3; kkk++){
			k[kkk] = -1;
		}
		ForgeDirection dir[] = new ForgeDirection[3];//direction
		for (int l = 0; l < 6; l++){
			if(length[l] > 0){
				if(k[0] == -1){
					k[0] = length[l];
					dir[0] = ForgeDirection.getOrientation(l);
				}else if(k[1] == -1){
					k[1] = length[l];
					dir[1] = ForgeDirection.getOrientation(l);
				}else if(k[2] == -1){
					k[2] = length[l];
					dir[2] = ForgeDirection.getOrientation(l);
				}else{
					System.err.println("something is wrong");
				}
			}
		}
		for (int i1 = 0; i1 < 3; i1++){
			int i2 = i1 == 2 ? 0 : i1 + 1;
			int i3 = i2 == 2 ? 0 : i2 + 1;
			for (int n1 = 0; n1 < 2; n1++){
				for (int n2 = 0; n2 < 2; n2++){
					for (int m = 1; m < k[i1]; m++){
						int x = m * dir[i1].offsetX + k[i2] * dir[i2].offsetX * n1 + k[i3] * dir[i3].offsetX * n2 + xCoord;
						int y = m * dir[i1].offsetY + k[i2] * dir[i2].offsetY * n1 + k[i3] * dir[i3].offsetY * n2 + yCoord;
						int z = m * dir[i1].offsetZ + k[i2] * dir[i2].offsetZ * n1 + k[i3] * dir[i3].offsetZ * n2 + zCoord;
						if(!(worldObj.getBlock(x, y, z) == TestCore.blockFrameLine)){
							player.addChatMessage(new ChatComponentText("Place Frame in Cube Edge"));
							return false;
						}
					}
				}
			}
		}

		//interface
		Position p1 = null;
		Position p2 = null;
		boolean count;
		for (int side = 0; side < 6; side++){
			switch (side) {
			case 0:
				p1 = list.get(0);
				p2 = list.get(4);
				break;
			case 1:
				p1 = list.get(0);
				p2 = list.get(5);
				break;
			case 2:
				p1 = list.get(0);
				p2 = list.get(6);
				break;
			case 3:
				p1 = list.get(4);
				p2 = list.get(5);
				break;
			case 4:
				p1 = list.get(4);
				p2 = list.get(6);
				break;
			case 5:
				p1 = list.get(5);
				p2 = list.get(6);
				break;
			}
			count = false;
			for (int i1 = (p1.x < p2.x ? p1.x : p2.x); i1 <= (p1.x < p2.x ? p2.x : p1.x); i1++){
				for (int i2 = (p1.y < p2.y ? p1.y : p2.y); i2 <= (p1.y < p2.y ? p2.y : p1.y); i2++){
					for (int i3 = (p1.z < p2.z ? p1.z : p2.z); i3 <= (p1.z < p2.z ? p2.z : p1.z); i3++){
						if(worldObj.getTileEntity(i1, i2, i3) instanceof BlockInterfaceTileEntity){
							if(count){
								player.addChatMessage(new ChatComponentText("Too Many Interface"));
								return false;
							}else{
								interfaceList.add(new Position(i1, i2, i3));
								count = true;
							}
						}else if(worldObj.getTileEntity(i1, i2, i3) instanceof BlockFrameTileEntity){
							if(!((i1 == p1.x || i1 == p2.x) && (i2 == p1.y || i2 == p2.y) && (i3 == p1.z || i3 == p2.z))){
								player.addChatMessage(new ChatComponentText("Illegal frame"));
								return false;
							}
						}else if(!worldObj.isAirBlock(i1, i2, i3) && worldObj.getBlock(i1, i2, i3) != TestCore.blockFrameLine){
							player.addChatMessage(new ChatComponentText("Frame pane can include only interface"));
							return false;
						}
					}
				}
			}
		}
		p1 = list.get(0);
		p2 = list.get(7);
		boolean alter = false;
		for (int i1 = (p1.x < p2.x ? p1.x : p2.x) + 1; i1 < (p1.x < p2.x ? p2.x : p1.x); i1++){
			for (int i2 = (p1.y < p2.y ? p1.y : p2.y) + 1; i2 < (p1.y < p2.y ? p2.y : p1.y); i2++){
				for (int i3 = (p1.z < p2.z ? p1.z : p2.z) + 1; i3 < (p1.z < p2.z ? p2.z : p1.z); i3++){
					if(worldObj.getTileEntity(i1, i2, i3) instanceof BlockInterfaceTileEntity){
						player.addChatMessage(new ChatComponentText("Interface cannot be inside the frame"));
						return false;
					}else if(worldObj.getTileEntity(i1, i2, i3) instanceof BlockPipeTileEntity){

					}else if(worldObj.getTileEntity(i1, i2, i3) instanceof ProcessorContainerTileEntity){
						if(((ProcessorContainerTileEntity) worldObj.getTileEntity(i1, i2, i3)).getContainProcessor() instanceof AlterProcessor){
							if(alter){
								player.addChatMessage(new ChatComponentText("You can put only one Alter Construct in frame."));
								return false;
							}else{
								alter = true;
							}
						}
					}else if(!worldObj.isAirBlock(i1, i2, i3)){
						player.addChatMessage(new ChatComponentText("Illegal Block contains"));
						return false;
					}
				}
			}
		}
		return true;
	}

	private void writeNBTToItemStack(ItemStack itemStack, RectangularSolid solid, ArrayList<Position> interfaceList, boolean isCreative) {
		Position min = solid.getMinPoint();
		Position max = solid.getMaxPoint();

		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("minX", min.x);
		tags.setInteger("minY", min.y);
		tags.setInteger("minZ", min.z);
		tags.setInteger("xSize", solid.getXSize());
		tags.setInteger("ySize", solid.getYSize());
		tags.setInteger("zSize", solid.getZSize());
		tags.setBoolean("pause", true);

		NBTTagList blocksTagList = new NBTTagList();
		int energy = 0;
		for (int index = 0; index < solid.getIndexSize(); index++){
			Position p = solid.toCoord(index);
			if(worldObj.getTileEntity(p.x, p.y, p.z) instanceof ProcessorContainerTileEntity){
				ProcessorContainerTileEntity base = (ProcessorContainerTileEntity) worldObj.getTileEntity(p.x, p.y, p.z);
				NBTTagCompound baseTag = new NBTTagCompound();
				//				baseTag.setInteger("index", index);
				//				baseTag.setString("name", base.getNameForNBT());
				//				base.writeDetailToNBTForItemStack(baseTag, solid, isCreative);
				//				base.writeDetailToNBTForItemStack(baseTag, isCreative);
				base.writeToNBT(baseTag);
				blocksTagList.appendTag(baseTag);
				if(base.getContainProcessor() instanceof EnergyProviderProcessor){
					energy += ((EnergyProviderProcessor) base.getContainProcessor()).getEnergyStored(ForgeDirection.UNKNOWN);
					if(!isCreative) ((EnergyProviderProcessor) base.getContainProcessor()).setEnergyStored(0);
				}
			}
		}
		tags.setTag("blockList", blocksTagList);
		EnergyStorage energyStorage = new EnergyStorage(energy);
		energyStorage.setEnergyStored(energy);
		energyStorage.writeToNBT(tags);

		NBTTagList interfaceTagList = new NBTTagList();
		for (Position p : interfaceList){
			if(worldObj.getTileEntity(p.x, p.y, p.z) instanceof BlockInterfaceTileEntity){
				BlockInterfaceTileEntity inter = (BlockInterfaceTileEntity) worldObj.getTileEntity(p.x, p.y, p.z);
				NBTTagCompound interfaceTag = new NBTTagCompound();
				if(p.y < min.y){
					interfaceTag.setInteger("side", 0);
				}else if(p.y > max.y){
					interfaceTag.setInteger("side", 1);
				}else if(p.z < min.z){
					interfaceTag.setInteger("side", 2);
				}else if(p.z > max.z){
					interfaceTag.setInteger("side", 3);
				}else if(p.x < min.x){
					interfaceTag.setInteger("side", 4);
				}else if(p.x > max.x){
					interfaceTag.setInteger("side", 5);
				}
				if(inter.connection != null && inter.connection.getTile() != null){
					//					interfaceTag.setInteger("x", inter.connection.x - min.x);
					//					interfaceTag.setInteger("y", inter.connection.y - min.y);
					//					interfaceTag.setInteger("z", inter.connection.z - min.z);
					interfaceTag.setInteger("x", inter.connection.x);
					interfaceTag.setInteger("y", inter.connection.y);
					interfaceTag.setInteger("z", inter.connection.z);
				}else{
					interfaceTag.setInteger("x", Integer.MAX_VALUE);
					interfaceTag.setInteger("y", Integer.MAX_VALUE);
					interfaceTag.setInteger("z", Integer.MAX_VALUE);
				}
				interfaceTagList.appendTag(interfaceTag);
			}
		}
		tags.setTag("interface", interfaceTagList);

		itemStack.setTagCompound(tags);

		if(!isCreative){
			for (int index = 0; index < solid.getIndexSize(); index++){
				Position p = solid.toCoord(index);
				if(worldObj.getTileEntity(p.x, p.y, p.z) instanceof ProcessorContainerTileEntity){
					ProcessorContainerTileEntity base = (ProcessorContainerTileEntity) worldObj.getTileEntity(p.x, p.y, p.z);
					base.invalidate();
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = INFINITE_EXTENT_AABB;
		return bb;
	}

}
