package com.okina.inventory;

import net.minecraft.inventory.ISidedInventory;

public interface IInternalInventoryUser extends ISidedInventory {

	public InternalInventory getInternalInventory();

}
