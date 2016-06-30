package com.okina.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class InternalInventory implements IInventory {

	private IInternalInventoryUser user;
	private int stackSize;
	private int stackLimit;
	private String invName;

	private ItemStack[] items;

	public InternalInventory(IInternalInventoryUser user, int stackSize, int stackLimit, String invName) {
		this.user = user;
		this.stackSize = stackSize;
		this.stackLimit = stackLimit;
		this.invName = invName;
		items = new ItemStack[stackSize];
	}

	@Override
	public int getSizeInventory() {
		return stackSize;
	}

	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex >= 0 && slotIndex < items.length){
			return items[slotIndex];
		}
		return null;
	}

	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		if(slotIndex >= 0 && slotIndex < items.length){
			ItemStack itemStack = items[slotIndex].copy();
			items[slotIndex] = null;
			return itemStack;
		}
		return null;
	}

	@Override
	public final ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		if(slotIndex < 0 && slotIndex >= items.length || splitStackSize <= 0) return null;
		if(items[slotIndex] != null){
			if(items[slotIndex].stackSize <= splitStackSize){
				ItemStack tmpItemStack = items[slotIndex].copy();
				items[slotIndex] = null;
				markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = items[slotIndex].splitStack(splitStackSize);
			if(items[slotIndex].stackSize == 0){
				items[slotIndex] = null;
			}
			markDirty();
			return splittedItemStack;
		}
		return null;
	}

	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if(slotIndex < 0 && slotIndex >= items.length) return;
		items[slotIndex] = itemStack;
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return invName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public void markDirty() {
		user.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return user.isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
	}

	public void reset() {
		items = new ItemStack[stackSize];
	}

	public void readFromNBT(NBTTagCompound tag) {
		stackSize = tag.getInteger("size");
		stackLimit = tag.getInteger("limit");
		NBTTagList itemsTagList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		items = new ItemStack[getSizeInventory()];
		for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
			NBTTagCompound itemTagCompound = itemsTagList.getCompoundTagAt(tagCounter);
			byte slotIndex = itemTagCompound.getByte("Slot");
			if(slotIndex >= 0 && slotIndex < items.length){
				items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("size", stackSize);
		tag.setInteger("limit", stackLimit);
		NBTTagList itemsTagList = new NBTTagList();
		for (int slotIndex = 0; slotIndex < items.length; ++slotIndex){
			if(items[slotIndex] != null){
				NBTTagCompound itemTagCompound = new NBTTagCompound();
				itemTagCompound.setByte("Slot", (byte) slotIndex);
				items[slotIndex].writeToNBT(itemTagCompound);
				itemsTagList.appendTag(itemTagCompound);
			}
		}
		tag.setTag("Items", itemsTagList);
	}

	//	/**
	//	 * @param user
	//	 * @param tag
	//	 * @param defaultize
	//	 * @param defaultimit
	//	 * @param defaultName
	//	 * @return
	//	 */
	//	public static InternalInventory createFromNBT(IInternalInventoryUser user, NBTTagCompound tag, int defaultize, int defaultimit, String defaultName) {
	//		if(tag.hasKey("size") && tag.hasKey("limit") && tag.hasKey("name")){
	//			int size = tag.getInteger("size");
	//			int limit = tag.getInteger("limit");
	//			String name = tag.getString("name");
	//			InternalInventory inv = new InternalInventory(user, size, limit, name);
	//			inv.readFromNBT(tag);
	//			return inv;
	//		}
	//		return new InternalInventory(user, defaultize, defaultimit, defaultName);
	//	}

}






