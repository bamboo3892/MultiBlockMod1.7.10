package com.okina.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GhostSlot extends Slot {

	private final int slotIndex;

	public GhostSlot(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
		this.slotIndex = slotIndex;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
		putStack(null);
		onSlotChanged();
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return getStack() == null;
	}

	@Override
	public ItemStack getStack() {
		return inventory.getStackInSlot(slotIndex);
	}

	@Override
	public boolean getHasStack() {
		return getStack() != null;
	}

	@Override
	public void putStack(ItemStack itemStack) {
		inventory.setInventorySlotContents(slotIndex, itemStack);
		onSlotChanged();
	}

	@Override
	public void onSlotChanged() {
		inventory.markDirty();
	}

	@Override
	public int getSlotStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		return inventory.decrStackSize(slotIndex, amount);
	}

	@Override
	public boolean isSlotInInventory(IInventory inventory, int slotIndex) {
		return inventory == this.inventory && slotIndex == this.slotIndex;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		putStack(null);
		return false;
	}

}
