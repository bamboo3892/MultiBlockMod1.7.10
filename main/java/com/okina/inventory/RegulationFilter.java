package com.okina.inventory;

import com.okina.client.gui.RegulationFilterGui;
import com.okina.main.TestCore;
import com.okina.server.gui.DummyContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class RegulationFilter extends AbstractFilter {

	public static ResourceLocation ICON = new ResourceLocation(TestCore.MODID + ":textures/filter/regulation_filter.png");

	public static final int FilterID = 3;

	/**true : black list false : white list*/
	public boolean useNBT;
	public boolean useDamage;
	public boolean useOreDictionary;
	/**1 - 6*/
	public int priority = 1;
	public int itemSize = 1;
	private int side;
	private IFilterUser tile;

	public RegulationFilter(IFilterUser tile, int side) {
		this.tile = tile;
		this.side = side;
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, int side, EntityPlayer player) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, x, y, z);
		return true;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new DummyContainer() : new RegulationFilterGui(player.inventory, tile, this, side);
	}

	@Override
	public boolean canAutoTransferItem(ItemStack itemStack, boolean input) {
		if(itemStack == null) return false;
		int size = 0;
		if(input) size += itemStack.stackSize;
		if(tile.getInventory() instanceof ISidedInventory){
			ISidedInventory inv = (ISidedInventory) tile.getInventory();
			for (int i = 0; i < inv.getAccessibleSlotsFromSide(side).length; i++){
				ItemStack slotStack = inv.getStackInSlot(inv.getAccessibleSlotsFromSide(side)[i]);
				if(isItemMatches(slotStack, itemStack)) size += slotStack.stackSize;
				if(size > itemSize) return !input;
			}
			return input;
		}else if(tile.getInventory() instanceof IInventory){
			for (int i = 0; i < tile.getInventory().getSizeInventory(); i++){
				ItemStack slotStack = tile.getInventory().getStackInSlot(i);
				if(isItemMatches(slotStack, itemStack)) size += slotStack.stackSize;
				if(size > itemSize) return !input;
			}
			return input;
		}
		return false;
	}

	private boolean isItemMatches(ItemStack item1, ItemStack item2) {
		if(item1 == null || item2 == null) return false;
		Item item = item1.getItem();
		if(useOreDictionary){
			if(isOreMatches(item2, item1)) return true;
		}
		boolean flag = item == item2.getItem();
		if(useNBT && flag){
			flag = ItemStack.areItemStackTagsEqual(item2, item1);
		}
		if(useDamage && flag){
			flag = item2.getItemDamage() == item1.getItemDamage();
		}
		return flag;
	}

	private static boolean isOreMatches(ItemStack item1, ItemStack item2) {
		if(item1 == null || item2 == null) return false;
		int[] id1 = OreDictionary.getOreIDs(item1);
		int[] id2 = OreDictionary.getOreIDs(item2);
		if(id1 == null || id2 == null) return false;
		for (int i1 : id1){
			for (int i2 : id2){
				if(i1 == i2) return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getFilterItem() {
		ItemStack filter = new ItemStack(TestCore.regulationFilter, 1);
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		filter.setTagCompound(tag);
		return filter;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public ResourceLocation getFilterIcon() {
		return ICON;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void processNBTPacketFromGui(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if(tag.getInteger("filterId") != FilterID) return;
		NBTTagCompound flags = tag.getCompoundTag("flags");
		useNBT = flags.getBoolean("nbt");
		useDamage = flags.getBoolean("damage");
		useOreDictionary = flags.getBoolean("ore");
		priority = flags.getInteger("priority");
		itemSize = flags.getInteger("itemSize");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("filterId", FilterID);
		NBTTagCompound flags = new NBTTagCompound();
		flags.setBoolean("nbt", useNBT);
		flags.setBoolean("damage", useDamage);
		flags.setBoolean("ore", useOreDictionary);
		flags.setInteger("priority", priority);
		flags.setInteger("itemSize", itemSize);
		tag.setTag("flags", flags);
	}

}
