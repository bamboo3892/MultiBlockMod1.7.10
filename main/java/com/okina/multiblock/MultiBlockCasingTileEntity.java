package com.okina.multiblock;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.okina.client.IHUDUser;
import com.okina.inventory.AbstractFilter;
import com.okina.inventory.IFilterUser;
import com.okina.item.IFilterItem;
import com.okina.main.GuiHandler.IGuiTile;
import com.okina.main.TestCore;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ColoredString;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;
import com.okina.utils.UtilMethods;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MultiBlockCasingTileEntity extends TileEntity implements ISimpleTilePacketUser, ISidedInventory, IEnergyHandler, IFilterUser, IHUDUser, IGuiTile {

	/**0 : input 1 : output 2 : disabled*/
	public int[] flagIO = new int[6];
	public ConnectionEntry<MultiBlockCoreTileEntity> coreTile = null;
	private AbstractFilter[] filter = new AbstractFilter[6];

	private boolean needCheckCoreTile = true;

	public MultiBlockCasingTileEntity() {
		for (int i = 0; i < 6; i++){
			flagIO[i] = 2;
		}
	}

	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && (player.getHeldItem().getItem() instanceof IFilterItem)) return false;
		if(filter[side] != null){
			return filter[side].onRightClicked(worldObj, xCoord, yCoord, zCoord, side, player);
		}else if(isConnected()){
			return coreTile.getTile().onRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	public boolean onShiftRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isConnected()){
			return coreTile.getTile().onShiftRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	public boolean onRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isConnected()){
			if(!player.isSneaking()){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(!(worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof MultiBlockCasing)){
					flagIO[side] = flagIO[side] == 0 ? 1 : flagIO[side] == 1 ? 2 : 0;
					TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FLAG_IO);
					return true;
				}
			}
			return coreTile.getTile().onRightClickedByWrench(world, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	public void onLeftClicked(EntityPlayer player, int side, double hitX, double hitY, double hitZ) {
		if(player.isSneaking() && filter[side] != null){
			ItemStack filter = removeFilter(side);
			if(filter != null && !worldObj.isRemote){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
			}
		}
	}

	@Override
	public void updateEntity() {
		if(needCheckCoreTile){
			//			if(coreTile != null){
			//				if(worldObj.getTileEntity(coreTile.x, coreTile.y, coreTile.z) instanceof MultiBlockCoreTileEntity){
			//					MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) worldObj.getTileEntity(coreTile.x, coreTile.y, coreTile.z);
			//					coreTile.setTile(tile);
			//				}
			//			}
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
			needCheckCoreTile = false;
		}
	}

	public void transferItemAndEnergyForSide(int side) {
		if(side <= -1 || side >= 6 || !isConnected()) return;
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
		if(tile instanceof MultiBlockCasingTileEntity && ((MultiBlockCasingTileEntity) tile).getCoreTile() == getCoreTile()) return;
		if(tile == getCoreTile()) return;
		//item
		if(tile instanceof IInventory){
			IInventory inv = (IInventory) tile;
			if((flagIO[side] == 1 || flagIO[side] == 0)){
				if(filter[side] == null){
					if(flagIO[side] == 1){
						InventoryHelper.tryPushItemEX(this, inv, dir, dir.getOpposite(), coreTile.getTile().getMaxTransfer(side));
					}else if(flagIO[side] == 0){
						InventoryHelper.tryPushItemEX(inv, this, dir.getOpposite(), dir, coreTile.getTile().getMaxTransfer(side));
					}
				}else{
					if(flagIO[side] == 1){
						filter[side].tranferItem(this, inv, dir.ordinal(), dir.getOpposite().ordinal(), coreTile.getTile().getMaxTransfer(side));
					}else if(flagIO[side] == 0){
						filter[side].tranferItem(inv, this, dir.getOpposite().ordinal(), dir.ordinal(), coreTile.getTile().getMaxTransfer(side));
					}
				}
			}
		}
		//enery
		if(tile instanceof IEnergyReceiver){
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			if(flagIO[side] == 1){
				int receive = receiver.receiveEnergy(dir.getOpposite(), coreTile.getTile().getMaxEnergyTransfer(side), true);
				int extract = coreTile.getTile().extractEnergy(dir, receive, false);
				receiver.receiveEnergy(dir.getOpposite(), extract, false);
			}
		}
	}

	public boolean isConnected() {
		return !(coreTile == null || coreTile.getTile() == null);
	}
	public MultiBlockCoreTileEntity getCoreTile() {
		if(isConnected()){
			return coreTile.getTile();
		}
		return null;
	}
	public boolean connect(MultiBlockCoreTileEntity tile) {
		assert !worldObj.isRemote;
		if(!isConnected()){
			coreTile = new ConnectionEntry<MultiBlockCoreTileEntity>(tile);
			TestCore.proxy.markForTileUpdate(getPosition(), PacketType.NBT_CONNECTION);
			return true;
		}
		return false;
	}
	public void disconnect() {
		assert !worldObj.isRemote;
		coreTile = null;
		Random rand = worldObj.rand;
		for (int i = 0; i < 6; i++){
			flagIO[i] = 2;
			if(filter[i] != null){
				ItemStack itemStack = filter[i].getFilterItem();
				double x = xCoord + rand.nextFloat() * 0.8F + 0.1F;
				double y = yCoord + rand.nextFloat() * 0.8F + 0.1F;
				double z = zCoord + rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(worldObj, x, y, z, itemStack);
				entityitem.motionX = (float) rand.nextGaussian() * 0.05F;
				entityitem.motionY = (float) rand.nextGaussian() * 0.05F + 0.2F;
				entityitem.motionZ = (float) rand.nextGaussian() * 0.05F;
				if(worldObj.spawnEntityInWorld(entityitem)){
					removeFilter(i);
				}
			}
		}
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FLAG_IO);
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.NBT_CONNECTION);
	}

	@Override
	public IInventory getInventory() {
		return this;
	}
	@Override
	public void updateFilter() {
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.ALL_FILTER_UPDATE);
	}
	@Override
	public boolean setFilter(int side, AbstractFilter filter) {
		if(side >= 0 && side < 6 && this.filter[side] == null){
			if(isConnected()){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(worldObj.isAirBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)){
					this.filter[side] = filter;
					updateFilter();
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public AbstractFilter getFilter(int side) {
		if(side >= 0 && side < 6){
			return filter[side];
		}
		return null;
	}
	@Override
	public ItemStack removeFilter(int side) {
		if(filter[side] == null){
			return null;
		}else{
			ItemStack itemStack = filter[side].getFilterItem();
			filter[side] = null;
			updateFilter();
			return itemStack;
		}
	}
	@Override
	public World getWorldObject() {
		return worldObj;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**contents items update*/
	@Override
	public void markDirty() {
		super.markDirty();
		if(coreTile != null && coreTile.getTile() != null){
			TestCore.proxy.markForTileUpdate(coreTile.getTile().getPosition(), PacketType.NBT_CONTENT);
		}
	}

	@Override
	public void setWorldObj(World world) {
		if(coreTile != null) coreTile.setWorld(world);
		super.setWorldObj(world);
	}

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.FLAG_IO){
			String str = "";
			for (int i = 0; i < 6; i++){
				str += flagIO[i];
			}
			return new SimpleTilePacket(this, PacketType.FLAG_IO, str);
		}else if(type == PacketType.NBT_CONNECTION){
			NBTTagCompound tag = new NBTTagCompound();
			if(coreTile != null){
				coreTile.writeToNBT(tag);
			}
			return new SimpleTilePacket(this, PacketType.NBT_CONNECTION, tag);
		}else if(type == PacketType.ALL_FILTER_UPDATE){
			NBTTagCompound tag = new NBTTagCompound();
			for (int side = 0; side < 6; side++){
				if(filter[side] == null) continue;
				NBTTagCompound filterTag = new NBTTagCompound();
				filter[side].writeToNBT(filterTag);
				tag.setTag("filter" + side, filterTag);
			}
			return new SimpleTilePacket(this, PacketType.ALL_FILTER_UPDATE, tag);
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
		}else if(type == PacketType.NBT_CONNECTION && value instanceof NBTTagCompound){//should client
			NBTTagCompound tag = (NBTTagCompound) value;
			ConnectionEntry past = coreTile;
			coreTile = ConnectionEntry.createFromNBT(tag, getWorldObj());
			if(coreTile != null && !coreTile.equals(past, false)){
				needCheckCoreTile = true;
			}
		}else if(type == PacketType.FILTER_NBT_FROM_GUI && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			if(tag.hasKey("side")){
				int side = tag.getInteger("side");
				if(getFilter(side) != null){
					filter[side].processNBTPacketFromGui(tag);
				}
			}
		}else if(type == PacketType.ALL_FILTER_UPDATE && value instanceof NBTTagCompound){
			for (int side = 0; side < 6; side++){
				NBTTagCompound sideTag = ((NBTTagCompound) value).getCompoundTag("filter" + side);
				filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
			}
		}
	}
	@Override
	public final Position getPosition() {
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

	@Override
	public int getSizeInventory() {
		if(isConnected()){
			return coreTile.getTile().getSizeInventory();
		}
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		if(isConnected()){
			return coreTile.getTile().getStackInSlot(slot);
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(isConnected()){
			return coreTile.getTile().decrStackSize(slot, amount);
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if(isConnected()){
			return coreTile.getTile().getStackInSlotOnClosing(slot);
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(isConnected()){
			coreTile.getTile().setInventorySlotContents(slot, itemStack);
		}
	}
	@Override
	public String getInventoryName() {
		if(isConnected()){
			return coreTile.getTile().getInventoryName();
		}
		return null;
	}
	@Override
	public boolean hasCustomInventoryName() {
		if(isConnected()){
			return coreTile.getTile().hasCustomInventoryName();
		}
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		if(isConnected()){
			return coreTile.getTile().getInventoryStackLimit();
		}
		return 0;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(isConnected()){
			return coreTile.getTile().isUseableByPlayer(player);
		}
		return false;
	}
	@Override
	public void openInventory() {
		if(isConnected()){
			coreTile.getTile().openInventory();
		}
	}
	@Override
	public void closeInventory() {
		if(isConnected()){
			coreTile.getTile().closeInventory();
		}
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(isConnected()){
			return coreTile.getTile().isItemValidForSlot(slot, itemStack);
		}
		return false;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(isConnected()){
			return coreTile.getTile().getAccessibleSlotsFromSide(side);
		}
		return new int[0];
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
		if(isConnected() && flagIO[side] == 0){
			if(filter[side] != null){
				return filter[side].canAutoTransferItem(itemStack, true) && coreTile.getTile().canInsertItem(slot, itemStack, side);
			}
			return coreTile.getTile().canInsertItem(slot, itemStack, side);
		}
		return false;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
		if(isConnected() && flagIO[side] == 1){
			if(filter[side] != null){
				return filter[side].canAutoTransferItem(itemStack, false) && coreTile.getTile().canInsertItem(slot, itemStack, side);
			}
			return coreTile.getTile().canExtractItem(slot, itemStack, side);
		}
		return false;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		if(isConnected()){
			return coreTile.getTile().canConnectEnergy(from);
		}
		return false;
	}
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if(isConnected()){
			return coreTile.getTile().receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if(isConnected()){
			return coreTile.getTile().extractEnergy(from, maxExtract, simulate);
		}
		return 0;
	}
	@Override
	public int getEnergyStored(ForgeDirection from) {
		if(isConnected()){
			return coreTile.getTile().getEnergyStored(from);
		}
		return 0;
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if(isConnected()){
			return coreTile.getTile().getMaxEnergyStored(from);
		}
		return 0;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return filter[side] == null ? null : filter[side].getGuiElement(player, side, serverSide);
	}

	@Override
	public final void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		if(isConnected()){
			List<ColoredString> listCenter = Lists.newArrayList(coreTile.getTile().getInterfaceString(mop.sideHit));
			listCenter.add(flagIO[mop.sideHit] == 0 ? new ColoredString("Input", 0x00BFFF) : flagIO[mop.sideHit] == 1 ? new ColoredString("Output", 0xFF8C00) : null);
			while (listCenter.remove(null)){
			}
			UtilMethods.renderHUDCenter(mc, listCenter);

			List<ColoredString> listRight = Lists.newArrayList();
			listRight.add(new ColoredString(coreTile.getTile().getEnergyStored(null) + " / " + coreTile.getTile().getMaxEnergyStored(null) + " RF", 0x800080));
			if(coreTile.getTile().hasInterfaceConnection(mop.sideHit)){
				List list = coreTile.getTile().getInterfaceInfo(mop.sideHit, mop, renderTicks);
				if(list != null && !list.isEmpty()) listRight.addAll(list);
			}
			while (listRight.remove(null)){
			}
			UtilMethods.renderHUDRight(mc, listRight);
		}
	}
	@Override
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current) {
		return object != null && object.getClass() == this.getClass() && past != null && current != null && past.sideHit == current.sideHit;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
		}
		for (int side = 0; side < 6; side++){
			NBTTagCompound sideTag = tag.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
		}
		coreTile = ConnectionEntry.createFromNBT(tag.getCompoundTag("core"), getWorldObj());
		needCheckCoreTile = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound[] sideTag = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++){
			sideTag[i] = new NBTTagCompound();
			sideTag[i].setByte("io", (byte) flagIO[i]);
			tag.setTag("side" + i, sideTag[i]);
		}
		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound filterTag = new NBTTagCompound();
			filter[side].writeToNBT(filterTag);
			tag.setTag("filter" + side, filterTag);
		}
		if(isConnected()){
			NBTTagCompound coreTag = new NBTTagCompound();
			coreTile.writeToNBT(coreTag);
			tag.setTag("core", coreTag);
		}
	}

}
