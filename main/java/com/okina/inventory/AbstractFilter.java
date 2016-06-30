package com.okina.inventory;

import com.okina.utils.InventoryHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class AbstractFilter {

	public static final int MAX_PRIORITY = 7;

	public abstract boolean onRightClicked(World world, int x, int y, int z, int side, EntityPlayer player);

	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return null;
	}

	public abstract ItemStack getFilterItem();

	public int getPriority() {
		return MAX_PRIORITY;
	}

	public boolean tranferItem(IInventory start, IInventory goal, int startSide, int goalSide, int maxTransfer) {
		return InventoryHelper.tryPushItemEX(start, goal, ForgeDirection.getOrientation(startSide), ForgeDirection.getOrientation(goalSide), maxTransfer);
	}

	/**Returns if other blocks can insert/extract item in filtered block<br>
	 * input : true, output : false*/
	public abstract boolean canAutoTransferItem(ItemStack itemStack, boolean input);

	public void processNBTPacketFromGui(NBTTagCompound tag) {

	}

	public abstract void readFromNBT(NBTTagCompound tag);

	public abstract void writeToNBT(NBTTagCompound tag);

	public static AbstractFilter createFromNBT(IFilterUser tile, int side, NBTTagCompound tag) {
		if(tag == null || !tag.hasKey("filterId")) return null;
		int id = tag.getInteger("filterId");
		if(id == 1){
			FilterInventory filter = new FilterInventory(tile, side);
			filter.readFromNBT(tag);
			return filter;
		}else if(id == 2){
			CraftingFilterInventory filter = new CraftingFilterInventory(tile, side);
			filter.readFromNBT(tag);
			return filter;
		}else if(id == 3){
			RegulationFilter filter = new RegulationFilter(tile, side);
			filter.readFromNBT(tag);
			return filter;
		}
		return null;
	}

	public abstract ResourceLocation getFilterIcon();

}
