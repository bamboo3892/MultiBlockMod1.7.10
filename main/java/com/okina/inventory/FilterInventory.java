package com.okina.inventory;

import com.okina.client.gui.ConstructFilterGui;
import com.okina.main.TestCore;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.EnergyProdeceRecipeRegister;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.server.gui.ConstructFilterContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class FilterInventory extends AbstractFilter implements IInventory {

	public static ResourceLocation ICON = new ResourceLocation(TestCore.MODID + ":textures/filter/filter.png");

	private ItemStack[] items = new ItemStack[9];
	/**true : black list false : white list*/
	public boolean filterBan;
	public boolean useNBT;
	public boolean useDamage = true;
	public boolean useOreDictionary;
	/**1 - 6*/
	public int priority = 1;
	private int side;
	private IFilterUser tile;

	public FilterInventory(IFilterUser tile, int side) {
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
		return serverSide ? new ConstructFilterContainer(player.inventory, this) : new ConstructFilterGui(player.inventory, tile, side);
	}

	@Override
	public boolean canAutoTransferItem(ItemStack itemStack, boolean input) {
		if(itemStack == null) return false;
		for (int i = 0; i < 9; i++){
			if(items[i] == null) continue;
			Item item = items[i].getItem();
			if(item == TestCore.filtering[0]){
				if(CrusherRecipeRegister.instance.findRecipeFromMaterial(itemStack) != null) return !filterBan;
			}else if(item == TestCore.filtering[1]){
				if(VirtualGrowerRecipeRegister.instance.findRecipe(itemStack) != null) return !filterBan;
			}else if(item == TestCore.filtering[2]){
				if(EnergyProdeceRecipeRegister.instance.findRecipe(itemStack) != null) return !filterBan;
			}else if(item == TestCore.filtering[3]){
				if(FurnaceRecipes.smelting().getSmeltingResult(itemStack) != null) return !filterBan;
			}else{
				if(useOreDictionary){
					if(isMatches(itemStack, items[i])) return !filterBan;
				}
				boolean flag = itemStack.getItem() == items[i].getItem();
				if(useNBT && flag){
					flag = ItemStack.areItemStackTagsEqual(itemStack, items[i]);
				}
				if(useDamage && flag){
					flag = itemStack.getItemDamage() == items[i].getItemDamage();
				}
				if(flag) return !filterBan;
			}
		}
		return filterBan;
	}

	private boolean isMatches(ItemStack item1, ItemStack item2) {
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
		ItemStack filter = new ItemStack(TestCore.filter, 1);
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
	public int getSizeInventory() {
		return 9;
	}
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex < 0 || slotIndex >= 9) return null;
		return items[slotIndex];
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		items[slotIndex] = null;
		markDirty();
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		return null;
	}
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack item) {
		items[slotIndex] = item;
		if(item != null && item.stackSize > getInventoryStackLimit()){
			item.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}
	@Override
	public String getInventoryName() {
		return "Filter " + ForgeDirection.getOrientation(side);
	}
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public void markDirty() {
		tile.updateFilter();
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return slot >= 0 && slot <= 8;
	}

	@Override
	public void processNBTPacketFromGui(NBTTagCompound tag) {
		if(tag.getInteger("filterId") != 1) return;

		NBTTagCompound flags = tag.getCompoundTag("flags");
		filterBan = flags.getBoolean("ban");
		useNBT = flags.getBoolean("nbt");
		useDamage = flags.getBoolean("damage");
		useOreDictionary = flags.getBoolean("ore");
		priority = flags.getInteger("priority");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if(tag.getInteger("filterId") != 1) return;

		NBTTagList itemsTagList = tag.getTagList("items", Constants.NBT.TAG_COMPOUND);
		items = new ItemStack[getSizeInventory()];
		for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
			NBTTagCompound itemTagCompound = itemsTagList.getCompoundTagAt(tagCounter);
			byte slotIndex = itemTagCompound.getByte("slot");
			if(slotIndex >= 0 && slotIndex < items.length){
				items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
			}
		}

		NBTTagCompound flags = tag.getCompoundTag("flags");
		filterBan = flags.getBoolean("ban");
		useNBT = flags.getBoolean("nbt");
		useDamage = flags.getBoolean("damage");
		useOreDictionary = flags.getBoolean("ore");
		priority = flags.getInteger("priority");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("filterId", 1);

		NBTTagList itemsTagList = new NBTTagList();
		for (int slotIndex = 0; slotIndex < items.length; ++slotIndex){
			if(items[slotIndex] != null){
				NBTTagCompound itemTagCompound = new NBTTagCompound();
				itemTagCompound.setByte("slot", (byte) slotIndex);
				items[slotIndex].writeToNBT(itemTagCompound);
				itemsTagList.appendTag(itemTagCompound);
			}
		}
		tag.setTag("items", itemsTagList);

		NBTTagCompound flags = new NBTTagCompound();
		flags.setBoolean("ban", filterBan);
		flags.setBoolean("nbt", useNBT);
		flags.setBoolean("damage", useDamage);
		flags.setBoolean("ore", useOreDictionary);
		flags.setInteger("priority", priority);
		tag.setTag("flags", flags);
	}

}
