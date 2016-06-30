package com.okina.multiblock.construct.processor;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.inventory.InternalInventory;
import com.okina.multiblock.construct.IConstructInventory;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.network.PacketType;
import com.okina.utils.InventoryHelper;
import com.okina.utils.UtilMethods;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class InventoryProcessorBase extends SidedOutputerProcessor<IConstructInventory> implements IConstructInventory {

	protected final InternalInventory internalInv;

	public InventoryProcessorBase(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade, int stackSize, int stackLimit, String invName) {
		super(pc, isRemote, isTile, x, y, z, grade);
		internalInv = new InternalInventory(this, stackSize, stackLimit, invName);
	}

	@Override
	public List<ItemStack> onRemoved() {
		List<ItemStack> itemList = Lists.newArrayList();
		for (int i = 0; i < this.getSizeInventory(); ++i){
			ItemStack itemstack = this.getStackInSlot(i);
			if(itemstack != null && itemstack.stackSize > 0){
				itemList.add(itemstack);
			}
		}
		return itemList;
	}

	@Override
	public Object getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTENT){
			NBTTagCompound invTag = new NBTTagCompound();
			internalInv.writeToNBT(invTag);
			return invTag;
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagCompound invTag = ((NBTTagCompound) value);
			internalInv.readFromNBT(invTag);
		}
		super.processCommand(type, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound invTag = tag.getCompoundTag("inv");
		if(invTag != null){
			internalInv.readFromNBT(invTag);
		}else{
			internalInv.reset();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound invTag = new NBTTagCompound();
		internalInv.writeToNBT(invTag);
		tag.setTag("inv", invTag);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected boolean itemTransfer(int maxTransfer) {
		assert !isRemote;
		int[] order = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
		for (int i : order){
			if(connection[i] != null && flagIO[i] == 1){
				if(InventoryHelper.tryPushItemEX(this, connection[i].getTile(), ForgeDirection.getOrientation(i), ForgeDirection.getOrientation(connection[i].side), maxTransfer)){
					if(!isTile) sendConnectionParticlePacket(i, 0x00ff7f);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected Class<IConstructInventory> getTargetClass() {
		return IConstructInventory.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return true;
	}

	@Override
	public final int getSizeInventory() {
		return internalInv.getSizeInventory();
	}
	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		return internalInv.getStackInSlot(slotIndex);
	}
	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return internalInv.getStackInSlotOnClosing(slotIndex);
	}
	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		internalInv.setInventorySlotContents(slotIndex, itemStack);
	}
	@Override
	public final ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		return internalInv.decrStackSize(slotIndex, splitStackSize);
	}
	@Override
	public final String getInventoryName() {
		return internalInv.getInventoryName();
	}
	@Override
	public final boolean hasCustomInventoryName() {
		return true;
	}
	@Override
	public final int getInventoryStackLimit() {
		return internalInv.getInventoryStackLimit();
	}
	@Override
	public final boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}
	@Override
	public final void openInventory() {}
	@Override
	public final void closeInventory() {}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 0;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 1;
	}
	@Override
	public final void markDirty() {
		pc.markForUpdate(PacketType.NBT_CONTENT);
	}
	@Override
	public final InternalInventory getInternalInventory() {
		return internalInv;
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
