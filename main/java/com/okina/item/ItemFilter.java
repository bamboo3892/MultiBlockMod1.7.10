package com.okina.item;

import com.okina.inventory.FilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.main.TestCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFilter extends Item implements IFilterItem {

	public ItemFilter() {
		setTextureName(TestCore.MODID + ":filter");
		setUnlocalizedName("mbm_filter");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote && world.getTileEntity(x, y, z) instanceof IFilterUser){
			IFilterUser tile = (IFilterUser) world.getTileEntity(x, y, z);
			if(tile.setFilter(side, new FilterInventory(tile, side))){
				--itemStack.stackSize;
				return true;
			}
		}
		return false;
	}

}
