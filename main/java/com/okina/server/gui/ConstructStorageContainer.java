package com.okina.server.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ConstructStorageContainer extends Container {

	private IInventory storageInv;
	private int numRows;

	public ConstructStorageContainer(IInventory invPlayer, IInventory invChest) {
		storageInv = invChest;
		numRows = invChest.getSizeInventory() / 9;
		invChest.openInventory();
		int i = (numRows - 4) * 18;
		int j;
		int k;

		for (j = 0; j < numRows; ++j){
			for (k = 0; k < 9; ++k){
				addSlotToContainer(new Slot(invChest, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (j = 0; j < 3; ++j){
			for (k = 0; k < 9; ++k){
				addSlotToContainer(new Slot(invPlayer, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}

		for (j = 0; j < 9; ++j){
			addSlotToContainer(new Slot(invPlayer, j, 8 + j * 18, 161 + i));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return storageInv.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(slotIndex < numRows * 9){
				if(!mergeItemStack(itemstack1, numRows * 9, inventorySlots.size(), true)){
					return null;
				}
			}else if(!mergeItemStack(itemstack1, 0, numRows * 9, false)){
				return null;
			}

			if(itemstack1.stackSize == 0){
				slot.putStack((ItemStack) null);
			}else{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		storageInv.closeInventory();
	}

}
