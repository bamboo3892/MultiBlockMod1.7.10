package com.okina.inventory;

import com.okina.utils.Position;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IFilterUser {

	IInventory getInventory();

	boolean setFilter(int side, AbstractFilter filter);

	AbstractFilter getFilter(int side);

	ItemStack removeFilter(int side);

	Position getPosition();

	World getWorldObject();

	void updateFilter();

}
