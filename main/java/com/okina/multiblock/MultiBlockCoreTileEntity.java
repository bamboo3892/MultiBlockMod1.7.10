package com.okina.multiblock;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.okina.client.IHUDUser;
import com.okina.inventory.AbstractFilter;
import com.okina.main.GuiHandler.IGuiTile;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.IConstructInventory;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ProcessorContainerPart;
import com.okina.multiblock.construct.processor.EnergyProviderProcessor;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.network.MultiBlockPacket;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ColoredString;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;
import com.okina.utils.UtilMethods;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class MultiBlockCoreTileEntity extends TileEntity implements ISimpleTilePacketUser, IEnergyHandler, ISidedInventory, IGuiTile, IHUDUser {

	private int minX;
	private int minY;
	private int minZ;
	public int xSize;//the number of blocks in x direction
	public int ySize;
	public int zSize;
	private ProcessorContainerPart[][][] parts;
	private ConnectionEntry<ProcessorBase>[] interfaceConnection = new ConnectionEntry[6];
	private boolean[] sidePowered = new boolean[6];
	/**0 : input 1 : output 2 : disabled*/
	public int[] flagIO = { 2, 2, 2, 2, 2, 2 };
	private EnergyStorage energyStorage;

	public boolean connected;
	private boolean isPausing = true;
	private boolean[][][] contentUpdateMarked;

	private int lastEnergy = 0;
	public boolean renderDetail = true;

	public MultiBlockCoreTileEntity() {

	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) checkCasingConnection();
		if(!worldObj.isRemote && !isPausing){
			//casing transfer
			if(connected){
				for (int kkk : UtilMethods.getRandomArray(0, 5)){
					ForgeDirection dir = ForgeDirection.getOrientation(kkk);
					List<Integer> priority = new LinkedList<Integer>();
					List<MultiBlockCasingTileEntity> inv = new LinkedList<MultiBlockCasingTileEntity>();
					int xxx = dir.offsetX != 0 ? 0 : 1;
					int yyy = dir.offsetY != 0 ? 0 : 1;
					int zzz = dir.offsetZ != 0 ? 0 : 1;
					for (int xxx2 = -1; xxx2 < (dir.offsetX == 0 ? 2 : 0); xxx2++){
						for (int yyy2 = -1; yyy2 < (dir.offsetY == 0 ? 2 : 0); yyy2++){
							for (int zzz2 = -1; zzz2 < (dir.offsetZ == 0 ? 2 : 0); zzz2++){
								if(worldObj.getTileEntity(xCoord + xxx * xxx2 + dir.offsetX, yCoord + yyy * yyy2 + dir.offsetY, zCoord + zzz * zzz2 + dir.offsetZ) instanceof MultiBlockCasingTileEntity){
									MultiBlockCasingTileEntity ttt = (MultiBlockCasingTileEntity) worldObj.getTileEntity(xCoord + xxx * xxx2 + dir.offsetX, yCoord + yyy * yyy2 + dir.offsetY, zCoord + zzz * zzz2 + dir.offsetZ);
									inv.add(ttt);
									if(ttt.flagIO[dir.ordinal()] == 1 || ttt.flagIO[dir.ordinal()] == 0){
										if(ttt.getFilter(dir.ordinal()) != null){
											priority.add(ttt.getFilter(dir.ordinal()).getPriority());
										}else{
											priority.add(0);
										}
									}else{
										priority.add(0);
									}
								}
							}
						}
					}
					if(priority.size() != 9 || inv.size() != 9){
						continue;
					}
					int[] order = new int[9];
					for (int i = 0; i < 9; i++){
						order[i] = -1;
					}
					int[] randomOrder = UtilMethods.getRandomArray(0, 8);
					for (int i = AbstractFilter.MAX_PRIORITY; i >= 0; i--){
						for (int j : randomOrder){
							if(priority.get(j) == i){
								int index = 0;
								while (order[index] != -1)
									index++;
								order[index] = j;
							}
						}
					}
					for (int index : order){
						inv.get(index).transferItemAndEnergyForSide(dir.ordinal());
					}
				}
			}else{
				int[] order = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
				for (int side : order){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
					if(tile instanceof IInventory && getInterfaceConnection(side) instanceof IConstructInventory){
						if(tile instanceof MultiBlockCasingTileEntity && ((MultiBlockCasingTileEntity) tile).getCoreTile() == this) continue;
						if(flagIO[side] == 1){
							InventoryHelper.tryPushItemEX(this, (IInventory) tile, dir, dir.getOpposite(), getMaxTransfer(side));
						}else if(flagIO[side] == 0){
							InventoryHelper.tryPushItemEX((IInventory) tile, this, dir.getOpposite(), dir, getMaxTransfer(side));
						}
					}
					//enery
					if(tile instanceof IEnergyReceiver){
						IEnergyReceiver receiver = (IEnergyReceiver) tile;
						if(flagIO[side] == 1){
							if(receiver instanceof MultiBlockCasingTileEntity && ((MultiBlockCasingTileEntity) receiver).getCoreTile() == this) return;
							int receive = receiver.receiveEnergy(dir.getOpposite(), getMaxEnergyTransfer(side), true);
							int extract = extractEnergy(dir, receive, false);
							receiver.receiveEnergy(dir.getOpposite(), extract, false);
						}
					}
				}
			}

			//	//update all parts
			for (int i = 0; i < xSize; i++){
				for (int j = 0; j < ySize; j++){
					for (int k = 0; k < zSize; k++){
						if(getProcessor(i, j, k, true) != null){
							getProcessor(i, j, k, true).updateEntity();
						}
					}
				}
			}
		}

		//energy update
		if(!worldObj.isRemote){
			if(lastEnergy != energyStorage.getEnergyStored()){
				TestCore.proxy.markForTileUpdate(getPosition(), PacketType.ENERGY);
			}
			lastEnergy = energyStorage.getEnergyStored();
		}

		//particle
		if(worldObj.isRemote && connected){
			if((int) (Math.random() * xSize * ySize * zSize) % 5 == 0){
				int x = (int) (Math.random() * xSize);
				int y = (int) (Math.random() * ySize);
				int z = (int) (Math.random() * zSize);
				if(getProcessor(x, y, z, true) != null){
					getProcessor(x, y, z, true).onRandomDisplayTick();
				}
			}
			if((int) (Math.random() * 10) % 10 == 0){
				int side = (int) (Math.random() * 6);
				int side2 = toInsideWorldSide(side);
				if(interfaceConnection[side2] != null && interfaceConnection[side2].getTile() != null){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					double x2 = toReadWorldX(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
					double y2 = toReadWorldY(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
					double z2 = toReadWorldZ(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
					int side3 = ForgeDirection.OPPOSITES[side];
					TestCore.spawnParticle(getWorldObj(), TestCore.PARTICLE_BEZIER_DOTS, xCoord + 0.5 + dir.offsetX * 1.5, yCoord + 0.5 + dir.offsetY * 1.5, zCoord + 0.5 + dir.offsetZ * 1.5, x2, y2, z2, side3, side, 0x00FF00);
				}
			}
		}
	}

	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null && interfaceConnection[side].getTile() != null){
			if(interfaceConnection[side].getTile().isOpenGuiOnClicked()){
				if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + toRealWorldSide(side), world, xCoord, yCoord, zCoord);
				return true;
			}
			return interfaceConnection[side].getTile().onPartRightClicked(world, player, side);
		}
		return false;
	}

	public boolean onShiftRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		int side2 = toInsideWorldSide(side);
		if(connected){
			if(interfaceConnection[side2] != null && interfaceConnection[side2].getTile() != null){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				double x2 = toReadWorldX(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
				double y2 = toReadWorldY(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
				double z2 = toReadWorldZ(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
				int side3 = ForgeDirection.OPPOSITES[side];
				TestCore.spawnParticle(getWorldObj(), TestCore.PARTICLE_BEZIER_DOTS, x + 0.5 + dir.offsetX * 0.5, y + 0.5 + dir.offsetY * 0.5, z + 0.5 + dir.offsetZ * 0.5, x2, y2, z2, side3, side, 0x00FF00);
				return true;
			}
		}
		renderDetail = !renderDetail;
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.RENDER);
		if(connected && !renderDetail){
			for (int i = 0; i < xSize; i++){
				for (int j = 0; j < ySize; j++){
					for (int k = 0; k < zSize; k++){
						ProcessorBase part = getProcessor(i, j, k, true);
						if(part != null){
							double x2 = toReadWorldX(i + 0.5, j + 0.5, k + 0.5);
							double y2 = toReadWorldY(i + 0.5, j + 0.5, k + 0.5);
							double z2 = toReadWorldZ(i + 0.5, j + 0.5, k + 0.5);
							int grade = part.grade;
							int color = grade == 0 ? 0x8B4513 : grade == 1 ? 0xE0FFFF : grade == 2 ? 0xFFFF00 : grade == 3 ? 0x00FFFF : 0x00FF00;
							float size = 1.5f / Math.max(xSize, Math.max(ySize, zSize));
							TestCore.spawnParticle(getWorldObj(), TestCore.PARTICLE_DOT, x2, y2, z2, color, size);
						}
					}
				}
			}
		}
		if(player.capabilities.isCreativeMode){
			if(!worldObj.isRemote){
				energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
			}
		}
		return true;
	}

	public boolean onRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.isSneaking()){
			isPausing = !isPausing;
			TestCore.proxy.markForTileUpdate(getPosition(), PacketType.OTHER2);
			if(world.isRemote){
				player.addChatMessage(new ChatComponentText(isPausing ? "Stopped!" : "Started!"));
			}
			return true;
		}else{
			int side2 = toInsideWorldSide(side);
			if(interfaceConnection[side2] != null){
				if(!connected){
					flagIO[side] = flagIO[side] == 0 ? 1 : flagIO[side] == 1 ? 2 : 0;
					TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FLAG_IO);
					return true;
				}
			}
		}
		return false;
	}

	/**Server only
	 * @return whether this tile is connecting
	 */
	private boolean checkCasingConnection() {
		assert !worldObj.isRemote;
		if(!connected){
			//check new connection
			for (int i = -1; i < 2; i++){
				for (int j = -1; j < 2; j++){
					for (int k = -1; k < 2; k++){
						if(i == 0 && j == 0 && k == 0) continue;
						if(worldObj.getTileEntity(xCoord + i, yCoord + j, zCoord + k) instanceof MultiBlockCasingTileEntity){
							MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) worldObj.getTileEntity(xCoord + i, yCoord + j, zCoord + k);
							if(tile.coreTile != null && !tile.coreTile.getPosition().equals(getPosition())) return false;
						}else{
							return false;
						}
					}
				}
			}
			//can connect
			for (int i = -1; i < 2; i++){
				for (int j = -1; j < 2; j++){
					for (int k = -1; k < 2; k++){
						if(i == 0 && j == 0 && k == 0) continue;
						MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) worldObj.getTileEntity(xCoord + i, yCoord + j, zCoord + k);
						tile.connect(this);
					}
				}
			}
			connected = true;
			TestCore.proxy.markForTileUpdate(getPosition(), PacketType.OTHER);
			for (int i = 0; i < 6; i++){
				flagIO[i] = 2;
			}
			TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FLAG_IO);
			return true;
		}else{
			//chect existing connection
			flag: for (int i = -1; i < 2; i++){
				for (int j = -1; j < 2; j++){
					for (int k = -1; k < 2; k++){
						if(i == 0 && j == 0 && k == 0) continue;
						if(worldObj.getTileEntity(xCoord + i, yCoord + j, zCoord + k) instanceof MultiBlockCasingTileEntity){
							MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) worldObj.getTileEntity(xCoord + i, yCoord + j, zCoord + k);
							if(tile.coreTile == null || !tile.coreTile.getPosition().equals(getPosition())){
								TestCore.proxy.markForTileUpdate(getPosition(), PacketType.OTHER);
								connected = false;
								break flag;
							}
						}else{
							TestCore.proxy.markForTileUpdate(getPosition(), PacketType.OTHER);
							connected = false;
							break flag;
						}
					}
				}
			}
			if(connected){
				return true;
			}else{
				for (int i = -1; i < 2; i++){
					for (int j = -1; j < 2; j++){
						for (int k = -1; k < 2; k++){
							if(i == 0 && j == 0 && k == 0) continue;
							if(worldObj.getTileEntity(xCoord + i, yCoord + j, zCoord + k) instanceof MultiBlockCasingTileEntity){
								MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) worldObj.getTileEntity(xCoord + i, yCoord + j, zCoord + k);
								tile.disconnect();
							}
						}
					}
				}
				return false;
			}
		}
	}

	public int getMaxTransfer(int side) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null && interfaceConnection[side].hasTile()){
			return IConstructInventory.maxTransfer[interfaceConnection[side].getTile().grade];
		}
		return 0;
	}
	public int getMaxEnergyTransfer(int side) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null){
			return energyStorage.getMaxExtract();
		}
		return 0;
	}
	public boolean hasInterfaceConnection(int side) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null){
			return true;
		}
		return false;
	}
	public ProcessorBase getInterfaceConnection(int side) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null){
			return interfaceConnection[side].getTile();
		}
		return null;
	}
	public ColoredString getInterfaceString(int side) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null){
			if(interfaceConnection[side].getTile() != null){
				return interfaceConnection[side].getTile().getNameForHUD();
			}else{
				return new ColoredString("No Inventory Connection", 0x000000);
			}
		}
		return null;
	}
	public List<ColoredString> getInterfaceInfo(int side, MovingObjectPosition mop, double renderTicks) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null && interfaceConnection[side].getTile() != null){
			return interfaceConnection[side].getTile().getHUDStringsForRight(mop, renderTicks);
		}
		return Lists.newArrayList();
	}

	public void changeSidePowered(int side, boolean powered) {
		if(worldObj.isRemote) return;
		if(powered){
			sidePowered[side] = true;
		}else{
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int xxx = dir.offsetX != 0 ? 0 : 1;
			int yyy = dir.offsetY != 0 ? 0 : 1;
			int zzz = dir.offsetZ != 0 ? 0 : 1;
			sidePowered[side] = false;
			flag: for (int xxx2 = -1; xxx2 < (dir.offsetX == 0 ? 2 : 0); xxx2++){
				for (int yyy2 = -1; yyy2 < (dir.offsetY == 0 ? 2 : 0); yyy2++){
					for (int zzz2 = -1; zzz2 < (dir.offsetZ == 0 ? 2 : 0); zzz2++){
						int power = worldObj.getIndirectPowerLevelTo(xCoord + xxx * xxx2 + dir.offsetX * 2, yCoord + yyy * yyy2 + dir.offsetY * 2, zCoord + zzz * zzz2 + dir.offsetZ * 2, dir.getOpposite().ordinal());
						if(power != 0){
							sidePowered[side] = true;
							break flag;
						}
					}
				}
			}
		}
		//		System.out.println(side + " : " + sidePowered[side]);
		//		if(this.interfaceConnection[side] != null && this.interfaceConnection[side].getTile() != null){
		//			this.interfaceConnection[side].getTile().isStopped = sidePowered[side];
		//		}
	}

	/**
	 * @param x : spawn x (inside coord)
	 * @param y : spawn y (inside coord)
	 * @param z : spawn z (inside coord)
	 * @param particleId
	 * @param objects
	 */
	public void spawnDotParticle(int x, int y, int z, int color) {
		double x1 = x + Math.random() * 0.5 - 0.25;
		double y1 = y + Math.random() * 0.5 - 0.25;
		double z1 = z + Math.random() * 0.5 - 0.25;
		double x2 = toReadWorldX(x1 + 0.5, y1 + 0.5, z1 + 0.5);
		double y2 = toReadWorldY(x1 + 0.5, y1 + 0.5, z1 + 0.5);
		double z2 = toReadWorldZ(x1 + 0.5, y1 + 0.5, z1 + 0.5);
		float size = 1.5f / Math.max(xSize, Math.max(ySize, zSize));
		TestCore.spawnParticle(getWorldObj(), TestCore.PARTICLE_DOT, x2, y2, z2, color, size);
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side] != null && interfaceConnection[side].getTile() != null){
			return interfaceConnection[side].getTile().getGuiElement(player, side, serverSide);
		}
		return null;
	}

	public boolean shouldRenderDetail() {
		return renderDetail && connected;
	}

	//helper
	public ProcessorBase getProcessor(int x, int y, int z, boolean relative) {
		if(!relative){
			x -= minX;
			y -= minY;
			z -= minZ;
		}
		if(x < 0 || y < 0 || z < 0 || x >= xSize || y >= ySize || z >= zSize) return null;
		return parts[x][y][z] == null ? null : parts[x][y][z].getContainProcessor();
	}
	public ProcessorContainerPart getPart(int x, int y, int z, boolean relative) {
		if(!relative){
			x -= minX;
			y -= minY;
			z -= minZ;
		}
		if(x < 0 || y < 0 || z < 0 || x >= xSize || y >= ySize || z >= zSize) return null;
		return parts[x][y][z];
	}
	public double toReadWorldX(double x, double y, double z) {
		x -= minX;
		y -= minY;
		z -= minZ;
		float coreSize = connected ? 2f : (2f / 3f);
		int maxLength = Math.max(xSize, Math.max(ySize, zSize));
		double xxx = -(xSize / 2f - x) * coreSize / maxLength;
		double zzz = -(zSize / 2f - z) * coreSize / maxLength;
		double angle = Math.PI / 2 * getBlockMetadata();
		return xxx * Math.cos(angle) - zzz * Math.sin(angle) + xCoord + 0.5d;
	}
	public double toReadWorldY(double x, double y, double z) {
		x -= minX;
		y -= minY;
		z -= minZ;
		float coreSize = connected ? 2f : (2f / 3f);
		int maxLength = Math.max(xSize, Math.max(ySize, zSize));
		double yyy = yCoord + 0.5d - (ySize / 2f - y) * coreSize / maxLength;
		return yyy;
	}
	public double toReadWorldZ(double x, double y, double z) {
		x -= minX;
		y -= minY;
		z -= minZ;
		float coreSize = connected ? 2f : (2f / 3f);
		int maxLength = Math.max(xSize, Math.max(ySize, zSize));
		double xxx = -(xSize / 2f - x) * coreSize / maxLength;
		double zzz = -(zSize / 2f - z) * coreSize / maxLength;
		double angle = Math.PI / 2 * getBlockMetadata();
		return xxx * Math.sin(angle) + zzz * Math.cos(angle) + zCoord + 0.5d;
	}
	public int toRealWorldSide(int side) {
		for (int i = 0; i < getBlockMetadata(); i++){
			side = ForgeDirection.ROTATION_MATRIX[1][side];
		}
		return side;
	}
	public int toInsideWorldSide(int side) {
		for (int i = 0; i < getBlockMetadata(); i++){
			side = ForgeDirection.ROTATION_MATRIX[0][side];
		}
		return side;
	}

	//use SimpleTilePacket
	public void markForContentUpdate(int x, int y, int z) {
		if(getProcessor(x, y, z, false) != null){
			contentUpdateMarked[x - minX][y - minY][z - minZ] = true;
			TestCore.proxy.markForTileUpdate(getPosition(), PacketType.NBT_CONTENT);
		}
	}

	//use MultiBlockPacket
	/**server only*/
	public void sendPartPacket(Position partPos, PacketType type, Object value) {
		if(worldObj.isRemote){
			System.err.println("Illegal side access");
			return;
		}
		TestCore.proxy.sendMultiBlockPacket(new MultiBlockPacket(getPosition(), partPos, type, value));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void markDirty() {
		super.markDirty();
		for (int side = 0; side < 6; side++){
			if(interfaceConnection[side] != null && interfaceConnection[side].getTile() != null){
				markForContentUpdate(interfaceConnection[side].x, interfaceConnection[side].y, interfaceConnection[side].z);
			}
		}
	}

	//	@Override
	//	public void setWorldObj(World world) {
	//		if(interfaceConnection != null){
	//			for (int i = 0; i < 6; i++){
	//				if(interfaceConnection[i] != null) interfaceConnection[i].setWorld(world);
	//			}
	//		}
	//		super.setWorldObj(world);
	//	}

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTENT){
			NBTTagCompound baseTag = new NBTTagCompound();
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < xSize; i++){
				for (int j = 0; j < ySize; j++){
					for (int k = 0; k < zSize; k++){
						if(contentUpdateMarked[i][j][k] && getProcessor(i, j, k, true) != null){
							NBTTagCompound tag = (NBTTagCompound) getProcessor(i, j, k, true).getPacket(PacketType.NBT_CONTENT);
							if(tag != null){
								tag.setInteger("x", i + minX);
								tag.setInteger("y", j + minY);
								tag.setInteger("z", k + minZ);
								list.appendTag(tag);
							}
							contentUpdateMarked[i][j][k] = false;
						}
					}
				}
			}
			baseTag.setTag("list", list);
			return new SimpleTilePacket(this, PacketType.NBT_CONTENT, baseTag);
		}else if(type == PacketType.FLAG_IO){
			String str = "";
			for (int i = 0; i < 6; i++){
				str += flagIO[i];
			}
			return new SimpleTilePacket(this, PacketType.FLAG_IO, str);
		}else if(type == PacketType.ENERGY){
			return new SimpleTilePacket(this, PacketType.ENERGY, energyStorage.getEnergyStored());
		}else if(type == PacketType.RENDER){
			return new SimpleTilePacket(this, PacketType.RENDER, renderDetail ? 1 : 0);
		}else if(type == PacketType.OTHER){
			return new SimpleTilePacket(this, PacketType.OTHER, connected ? "1" : "0");
		}else if(type == PacketType.OTHER2){
			return new SimpleTilePacket(this, PacketType.OTHER2, isPausing ? 1 : 0);
		}
		return null;
	}
	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.FLAG_IO && value instanceof String){//should client
			String str = (String) value;
			if(str.length() == 6){
				for (int i = 0; i < 6; i++){
					flagIO[i] = Character.getNumericValue(str.charAt(i));
				}
			}
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}else if(type == PacketType.OTHER){
			connected = value.equals("1");
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}else if(type == PacketType.NBT_MULTIBLOCK && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			int id = tag.getInteger("type");
			int x = tag.getInteger("x");
			int y = tag.getInteger("y");
			int z = tag.getInteger("z");
			NBTTagCompound valueTag = tag.getCompoundTag("value");
			if(getProcessor(x, y, z, false) != null){
				getProcessor(x, y, z, false).processCommand(PacketType.getFromId(id), valueTag);
			}
		}else if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagList list = ((NBTTagCompound) value).getTagList("list", Constants.NBT.TAG_COMPOUND);
			if(list != null){
				for (int tagCounter = 0; tagCounter < list.tagCount(); ++tagCounter){
					NBTTagCompound tag = list.getCompoundTagAt(tagCounter);
					int x = tag.getInteger("x");
					int y = tag.getInteger("y");
					int z = tag.getInteger("z");
					ProcessorBase part = getProcessor(x, y, z, false);
					if(part != null){
						part.processCommand(PacketType.NBT_CONTENT, tag);
					}
				}
			}
		}else if(type == PacketType.EFFECT2 && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			int id = tag.getInteger("id");
			if(id == 1){
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				int send = tag.getInteger("send");
				Random rand = worldObj.rand;
				for (int i = 0; i < (int) (send / 100F); i++){
					double startX = xCoord + 0.4 + rand.nextDouble() * 0.2;
					double startY = yCoord + 0.4 + rand.nextDouble() * 0.2;
					double startZ = zCoord + 0.4 + rand.nextDouble() * 0.2;
					double endX = toReadWorldX(x + 0.5, y + 0.5, z + 0.5) - startX;
					double endY = toReadWorldY(x + 0.5, y + 0.5, z + 0.5) - startY;
					double endZ = toReadWorldZ(x + 0.5, y + 0.5, z + 0.5) - startZ;
					TestCore.spawnParticle(worldObj, TestCore.PARTICLE_ENERGY, startX, startY, startZ, endX, endY, endZ);
				}
			}
		}else if(type == PacketType.ENERGY && value instanceof Integer){
			energyStorage.setEnergyStored((Integer) value);
		}else if(type == PacketType.RENDER && value instanceof Integer){
			renderDetail = (Integer) value == 1;
		}else if(type == PacketType.OTHER2 && value instanceof Integer){
			isPausing = (Integer) value == 1;
		}
	}
	public void processPacket(Position partPos, PacketType type, Object value) {
		ProcessorBase part = this.getProcessor(partPos.x, partPos.y, partPos.z, false);
		if(part != null){
			part.processCommand(type, value);
		}
	}
	@Override
	public Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbtTagCompound);
	}
	@Override
	public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbtTagCompound = pkt.func_148857_g();
		readFromNBT(nbtTagCompound);
	}

	private int attemptSide = 0;

	@Override
	public int getSizeInventory() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).getSizeInventory();
		}
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).getStackInSlot(slot);
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).decrStackSize(slot, amount);
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).getStackInSlotOnClosing(slot);
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			((IConstructInventory) interfaceConnection[attemptSide].getTile()).setInventorySlotContents(slot, itemStack);
		}
	}
	@Override
	public String getInventoryName() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).getInventoryName();
		}
		return null;
	}
	@Override
	public boolean hasCustomInventoryName() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).hasCustomInventoryName();
		}
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).getInventoryStackLimit();
		}
		return 0;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).isUseableByPlayer(player);
		}
		return false;
	}
	@Override
	public void openInventory() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			((IConstructInventory) interfaceConnection[attemptSide].getTile()).openInventory();
		}
	}
	@Override
	public void closeInventory() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			((IConstructInventory) interfaceConnection[attemptSide].getTile()).closeInventory();
		}
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).isItemValidForSlot(slot, itemStack);
		}
		return false;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		side = toInsideWorldSide(side);
		attemptSide = side;
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return ((IConstructInventory) interfaceConnection[attemptSide].getTile()).getAccessibleSlotsFromSide(side);
		}
		return new int[0];
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
		side = toInsideWorldSide(side);
		attemptSide = side;
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return true;
		}
		return false;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
		side = toInsideWorldSide(side);
		attemptSide = side;
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() instanceof IConstructInventory){
			return true;
		}
		return false;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return energyStorage.extractEnergy(maxExtract, simulate);
	}
	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energyStorage.getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 2, yCoord + 2, zCoord + 2);
	}

	@Override
	public final void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		List<ColoredString> listCenter = Lists.newArrayList(getInterfaceString(mop.sideHit));
		listCenter.add(new ColoredString("Not Connected", 0xFF8C00));
		UtilMethods.renderHUDCenter(mc, listCenter);

		List<ColoredString> listRight = Lists.newArrayList();
		listRight.add(new ColoredString(getEnergyStored(null) + " / " + getMaxEnergyStored(null) + " RF", 0x800080));
		UtilMethods.renderHUDRight(mc, listRight);
	}
	@Override
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current) {
		return object == this;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void writeToNBTForItemDrop(ItemStack itemStack) {
		NBTTagCompound itemTag = itemStack.getTagCompound() == null ? new NBTTagCompound() : itemStack.getTagCompound();
		writeToNBT(itemTag);
		itemStack.setTagCompound(itemTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		connected = tag.getBoolean("connected");
		isPausing = tag.getBoolean("pause");
		if(tag.hasKey("renderDetail")){
			renderDetail = tag.getBoolean("renderDetail");
		}else{
			renderDetail = true;
		}

		//content
		minX = tag.getInteger("minX");
		minY = tag.getInteger("minY");
		minZ = tag.getInteger("minZ");
		xSize = tag.getInteger("xSize");
		ySize = tag.getInteger("ySize");
		zSize = tag.getInteger("zSize");
		parts = new ProcessorContainerPart[xSize][ySize][zSize];

		NBTTagList blockTagList = tag.getTagList("blockList", Constants.NBT.TAG_COMPOUND);
		//		int eStored = 0;
		int eStorage = 0;
		int eTransfer = 0;
		for (int tagCounter = 0; tagCounter < blockTagList.tagCount(); ++tagCounter){
			NBTTagCompound blockTagCompound = blockTagList.getCompoundTagAt(tagCounter);
			int x = blockTagCompound.getInteger("x") - minX;
			int y = blockTagCompound.getInteger("y") - minY;
			int z = blockTagCompound.getInteger("z") - minZ;
			if(parts[x][y][z] == null){
				parts[x][y][z] = new ProcessorContainerPart(this, FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT);
			}
			parts[x][y][z].readFromNBT(blockTagCompound);
			if(parts[x][y][z].getContainProcessor() instanceof EnergyProviderProcessor){
				//				eStored += ((EnergyProviderProcessor) parts[x][y][z].getContainProcessor()).extractEnergy(ForgeDirection.UNKNOWN, EnergyProviderProcessor.storage[4], false);
				eStorage += EnergyProviderProcessor.storage[parts[x][y][z].getContainProcessor().grade];
				eTransfer = Math.max(eTransfer, EnergyProviderProcessor.transfer[parts[x][y][z].getContainProcessor().grade]);
			}
		}

		//		if(eStorage != 0 && eTransfer != 0){
		energyStorage = new EnergyStorage(eStorage, eTransfer);
		energyStorage.readFromNBT(tag);
		//		energyStorage.setEnergyStored(eStored);
		//		}else{
		//			energyStorage = new EnergyStorage(0, 0);
		//		}

		NBTTagList interfaceTagList = tag.getTagList("interface", Constants.NBT.TAG_COMPOUND);
		for (int tagCounter = 0; tagCounter < interfaceTagList.tagCount(); ++tagCounter){
			NBTTagCompound interfaceTag = interfaceTagList.getCompoundTagAt(tagCounter);
			int side = interfaceTag.getInteger("side");
			int x = interfaceTag.getInteger("x");
			int y = interfaceTag.getInteger("y");
			int z = interfaceTag.getInteger("z");
			if(getProcessor(x, y, z, false) != null){
				interfaceConnection[side] = new ConnectionEntry<ProcessorBase>(getProcessor(x, y, z, false), side);
			}else{
				interfaceConnection[side] = new ConnectionEntry<ProcessorBase>((IProcessorContainer) null, x, y, z, side);
			}
		}

		contentUpdateMarked = new boolean[xSize][ySize][zSize];
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("connected", connected);
		tag.setBoolean("pause", isPausing);
		tag.setBoolean("renderDetail", renderDetail);

		//content
		tag.setInteger("minX", minX);
		tag.setInteger("minY", minY);
		tag.setInteger("minZ", minZ);
		tag.setInteger("xSize", xSize);
		tag.setInteger("ySize", ySize);
		tag.setInteger("zSize", zSize);

		NBTTagList blocksTagList = new NBTTagList();
		for (int i = 0; i < xSize; i++){
			for (int j = 0; j < ySize; j++){
				for (int k = 0; k < zSize; k++){
					if(getPart(i, j, k, true) != null){
						NBTTagCompound partTag = new NBTTagCompound();
						getPart(i, j, k, true).writeToNBT(partTag);
						blocksTagList.appendTag(partTag);
					}
				}
			}
		}
		tag.setTag("blockList", blocksTagList);

		if(energyStorage != null){
			energyStorage.writeToNBT(tag);
		}

		NBTTagList interfaceTagList = new NBTTagList();
		for (int side = 0; side < 6; side++){
			if(interfaceConnection[side] != null){
				NBTTagCompound interfaceTag = new NBTTagCompound();
				interfaceTag.setInteger("side", side);
				interfaceTag.setInteger("x", interfaceConnection[side].x);
				interfaceTag.setInteger("y", interfaceConnection[side].y);
				interfaceTag.setInteger("z", interfaceConnection[side].z);
				interfaceTagList.appendTag(interfaceTag);
			}
		}
		tag.setTag("interface", interfaceTagList);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static MultiBlockCoreTileEntity createTileFromNBT(NBTTagCompound tag) {
		MultiBlockCoreTileEntity tile = new MultiBlockCoreTileEntity();
		tile.readFromNBT(tag);
		return tile;
	}

}
