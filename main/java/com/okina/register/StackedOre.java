package com.okina.register;

import java.util.Objects;

import com.okina.utils.InventoryHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

public class StackedOre {

	private final Object obj;
	public final int size;

	public StackedOre(ItemStack itemstack) {
		this.obj = Objects.requireNonNull(itemstack);
		this.size = itemstack.stackSize;
	}

	public StackedOre(Object obj, int size) {
		this.obj = Objects.requireNonNull(obj);
		this.size = size;
	}

	public boolean isValid() {
		return InventoryHelper.hasOreItem(obj);
	}

	public ItemStack getItemStackForServer() {
		ItemStack item = InventoryHelper.getOreItemForServer(obj);
		if(item != null){
			item = item.copy();
			item.stackSize = size;
			return item;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getItemStackForClient(int index) {
		ItemStack item = InventoryHelper.getOreItemForClient(obj, index);
		if(item != null){
			item = item.copy();
			item.stackSize = size;
			return item;
		}
		return null;
	}

}
