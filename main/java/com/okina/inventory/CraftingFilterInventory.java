package com.okina.inventory;

import com.okina.client.gui.CraftingFilterGui;
import com.okina.main.TestCore;
import com.okina.server.gui.CraftingFilterContainer;
import com.okina.utils.InventoryHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class CraftingFilterInventory extends AbstractFilter implements IInventory {

	public static ResourceLocation ICON = new ResourceLocation(TestCore.MODID + ":textures/filter/crafting_filter.png");

	public ItemStack[] craftGrid = new ItemStack[9];
	public ItemStack result;

	private ItemStack product;
	private ItemStack[] materials;
	private IFilterUser tile;
	private int side;

	public CraftingFilterInventory(IFilterUser tile, int side) {
		this.tile = tile;
		this.side = side;
	}

	public CraftingFilterInventory(IFilterUser tile, int side, ItemStack itemStack) {
		this(tile, side);
		Item item = itemStack.getItem();
		if(item == TestCore.craftingFilter){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag != null){
				NBTTagCompound productTag = tag.getCompoundTag("product");
				NBTTagList materialTagList = tag.getTagList("material", Constants.NBT.TAG_COMPOUND);
				if(productTag != null && materialTagList != null){
					product = ItemStack.loadItemStackFromNBT(productTag);
					materials = new ItemStack[materialTagList.tagCount()];
					for (int tagCounter = 0; tagCounter < materialTagList.tagCount(); ++tagCounter){
						NBTTagCompound materialTag = materialTagList.getCompoundTagAt(tagCounter);
						materials[tagCounter] = ItemStack.loadItemStackFromNBT(materialTag);
					}
				}
				NBTTagList gridTagList = tag.getTagList("craftGrid", Constants.NBT.TAG_COMPOUND);
				if(gridTagList != null){
					craftGrid = new ItemStack[9];
					for (int tagCounter = 0; tagCounter < gridTagList.tagCount(); ++tagCounter){
						NBTTagCompound gridTag = gridTagList.getCompoundTagAt(tagCounter);
						byte slotIndex = gridTag.getByte("slot");
						if(slotIndex >= 0 && slotIndex < 9){
							craftGrid[slotIndex] = ItemStack.loadItemStackFromNBT(gridTag);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, int side, EntityPlayer player) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, x, y, z);
		return true;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new CraftingFilterContainer(player.inventory, this, tile.getWorldObject()) : new CraftingFilterGui(player.inventory, this, tile.getWorldObject());
	}

	public void setRecipe(InventoryCrafting craftMatrix, ItemStack result) {
		if(result != null){
			ItemStack[] materials = new ItemStack[9];
			ItemStack material;
			int types = 0;
			for (int i = 0; i < 9; i++){
				material = craftMatrix.getStackInSlot(i);
				if(material != null){
					material = material.copy();
					material.stackSize = 1;
					for (int j = 0; j < 9; j++){
						if(materials[j] != null){
							if(materials[j].isItemEqual(material) && ItemStack.areItemStackTagsEqual(materials[j], material)){
								materials[j].stackSize++;
								break;
							}else{
								continue;
							}
						}else{
							materials[j] = material;
							types++;
							break;
						}
					}
				}
			}
			this.materials = new ItemStack[types];
			for (int i = 0; i < types; i++){
				this.materials[i] = materials[i];
			}
			product = result;
		}else{
			materials = null;
			product = null;
		}
	}

	@Override
	public boolean tranferItem(IInventory start, IInventory goal, int startSide, int goalSide, int maxTransfer) {
		if(materials != null && product != null){
			ItemStack[] mat = new ItemStack[materials.length];
			for (int j = 0; j < mat.length; j++){
				mat[j] = materials[j].copy();
			}
			if(start instanceof ISidedInventory){
				ISidedInventory sided = (ISidedInventory) start;
				int[] accessible = sided.getAccessibleSlotsFromSide(startSide);
				if(accessible != null){
					ItemStack[] startInv = new ItemStack[accessible.length];
					for (int i = 0; i < accessible.length; i++){
						if(sided.getStackInSlot(accessible[i]) != null){
							startInv[i] = sided.getStackInSlot(accessible[i]).copy();
							for (int j = 0; j < mat.length; j++){
								if(startInv[i].isItemEqual(mat[j]) && ItemStack.areItemStackTagsEqual(startInv[i], mat[j]) && mat[j].stackSize > 0){
									int amountToUse = Math.min(mat[j].stackSize, startInv[i].stackSize);
									mat[j].stackSize -= amountToUse;
									startInv[i].stackSize -= amountToUse;
								}
							}
						}
					}
					for (int j = 0; j < mat.length; j++){
						if(mat[j].stackSize > 0) return false;
					}

					//can craft
					if(InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), product.copy(), InventoryHelper.SIMULATE) == null){
						for (ItemStack mat2 : materials){
							if(mat2.getItem().getContainerItem() != null){
								if(InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), new ItemStack(mat2.getItem().getContainerItem(), mat2.stackSize), InventoryHelper.SIMULATE) != null) return false;
							}
						}
						//can transfer
						for (int i = 0; i < accessible.length; i++){
							if(startInv[i] != null && startInv[i].stackSize == 0) startInv[i] = null;
							sided.setInventorySlotContents(accessible[i], startInv[i]);
						}
						InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), product.copy(), InventoryHelper.WHOLE);
						for (ItemStack mat2 : materials){
							if(mat2.getItem().getContainerItem() != null){
								InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), new ItemStack(mat2.getItem().getContainerItem(), mat2.stackSize), InventoryHelper.WHOLE);
							}
						}
						sided.markDirty();
						return true;
					}
				}
			}else{
				int invSize = start.getSizeInventory();
				ItemStack[] startInv = new ItemStack[invSize];
				for (int i = 0; i < invSize; i++){
					if(start.getStackInSlot(i) != null){
						startInv[i] = start.getStackInSlot(i).copy();
						for (int j = 0; j < mat.length; j++){
							if(startInv[i].isItemEqual(mat[j]) && ItemStack.areItemStackTagsEqual(startInv[i], mat[j]) && mat[j].stackSize > 0){
								int amountToUse = Math.min(mat[j].stackSize, startInv[i].stackSize);
								mat[j].stackSize -= amountToUse;
								startInv[i].stackSize -= amountToUse;
							}
						}
					}
				}
				for (int j = 0; j < mat.length; j++){
					if(mat[j].stackSize > 0) return false;
				}

				//can craft
				if(InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), product.copy(), InventoryHelper.SIMULATE) == null){
					for (ItemStack mat2 : materials){
						if(mat2.getItem().getContainerItem() != null){
							if(InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), new ItemStack(mat2.getItem().getContainerItem(), mat2.stackSize), InventoryHelper.SIMULATE) != null) return false;
						}
					}
					//can transfer
					for (int i = 0; i < invSize; i++){
						if(startInv[i] != null && startInv[i].stackSize == 0) startInv[i] = null;
						start.setInventorySlotContents(i, startInv[i]);
					}
					InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), product.copy(), InventoryHelper.WHOLE);
					for (ItemStack mat2 : materials){
						if(mat2.getItem().getContainerItem() != null){
							InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), new ItemStack(mat2.getItem().getContainerItem(), mat2.stackSize), InventoryHelper.WHOLE);
						}
					}
					start.markDirty();
					return true;
				}
			}
		}
		return false;
	}

	//	@Override
	//	public boolean tranferItem(IConstructInventory start, IConstructInventory goal, int startSide, int goalSide, int maxTransfer) {
	//		if(materials != null && product != null){
	//			ItemStack[] mat = new ItemStack[materials.length];
	//			for (int j = 0; j < mat.length; j++){
	//				mat[j] = materials[j].copy();
	//			}
	//			int[] accessible = start.getAccessibleSlotsFromSide2(startSide);
	//			if(accessible != null){
	//				ItemStack[] startInv = new ItemStack[accessible.length];
	//				for (int i = 0; i < accessible.length; i++){
	//					if(start.getStackInSlot2(accessible[i]) != null){
	//						startInv[i] = start.getStackInSlot2(accessible[i]).copy();
	//						for (int j = 0; j < mat.length; j++){
	//							if(startInv[i].isItemEqual(mat[j]) && ItemStack.areItemStackTagsEqual(startInv[i], mat[j]) && mat[j].stackSize > 0){
	//								int amountToUse = Math.min(mat[j].stackSize, startInv[i].stackSize);
	//								mat[j].stackSize -= amountToUse;
	//								startInv[i].stackSize -= amountToUse;
	//							}
	//						}
	//					}
	//				}
	//				for (int j = 0; j < mat.length; j++){
	//					if(mat[j].stackSize > 0) return false;
	//				}
	//
	//				//can craft
	//				if(InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), product.copy(), InventoryHelper.SIMULATE) == null){
	//					for (int i = 0; i < accessible.length; i++){
	//						if(startInv[i] != null && startInv[i].stackSize == 0) startInv[i] = null;
	//						start.setInventorySlotContents2(accessible[i], startInv[i]);
	//					}
	//					InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), product.copy(), InventoryHelper.WHOLE);
	//					for (ItemStack mat2 : materials){
	//						if(mat2.getItem().getContainerItem() != null){
	//							InventoryHelper.tryPushItem(goal, ForgeDirection.getOrientation(goalSide), new ItemStack(mat2.getItem().getContainerItem(), 1), InventoryHelper.AS_MANY_AS_POSSIBLE);
	//						}
	//					}
	//					start.markDirty2();
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}

	@Override
	public ItemStack getFilterItem() {
		ItemStack itemStack = new ItemStack(TestCore.craftingFilter, 1);
		NBTTagCompound tag = new NBTTagCompound();

		if(product != null){
			NBTTagCompound productTag = new NBTTagCompound();
			product.writeToNBT(productTag);
			tag.setTag("product", productTag);
		}

		if(materials != null){
			NBTTagList itemsTagList = new NBTTagList();
			for (int i = 0; i < materials.length; i++){
				if(materials[i] != null){
					NBTTagCompound itemTag = new NBTTagCompound();
					materials[i].writeToNBT(itemTag);
					itemsTagList.appendTag(itemTag);
				}
			}
			tag.setTag("material", itemsTagList);
		}
		if(craftGrid != null){
			NBTTagList itemsTagList = new NBTTagList();
			for (int i = 0; i < 9; i++){
				if(craftGrid[i] != null){
					NBTTagCompound itemTag = new NBTTagCompound();
					itemTag.setByte("slot", (byte) i);
					craftGrid[i].writeToNBT(itemTag);
					itemsTagList.appendTag(itemTag);
				}
			}
			tag.setTag("craftGrid", itemsTagList);
		}
		itemStack.setTagCompound(tag);
		return itemStack;
	}

	@Override
	public int getPriority() {
		return 7;
	}

	@Override
	public ResourceLocation getFilterIcon() {
		return ICON;
	}

	@Override
	public boolean canAutoTransferItem(ItemStack itemStack, boolean input) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 10;
	}
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex == 9) return result;
		if(slotIndex < 0 || slotIndex >= 9) return null;
		return craftGrid[slotIndex];
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if(slotIndex == 9) return null;
		craftGrid[slotIndex] = null;
		markDirty();
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		return null;
	}
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack item) {
		if(slotIndex == 9){
			result = item;
			return;
		}
		craftGrid[slotIndex] = item;
		if(item != null && item.stackSize > getInventoryStackLimit()){
			item.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}
	@Override
	public String getInventoryName() {
		return "Crafting Filter " + ForgeDirection.getOrientation(side);
	}
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
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

	//////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if(tag.getInteger("filterId") != 2) return;
		NBTTagCompound productTag = tag.getCompoundTag("product");
		NBTTagList materialTagList = tag.getTagList("material", Constants.NBT.TAG_COMPOUND);
		if(productTag != null && materialTagList != null){
			product = ItemStack.loadItemStackFromNBT(productTag);
			materials = new ItemStack[materialTagList.tagCount()];
			for (int tagCounter = 0; tagCounter < materialTagList.tagCount(); ++tagCounter){
				NBTTagCompound materialTag = materialTagList.getCompoundTagAt(tagCounter);
				materials[tagCounter] = ItemStack.loadItemStackFromNBT(materialTag);
			}
		}
		NBTTagList gridTagList = tag.getTagList("craftGrid", Constants.NBT.TAG_COMPOUND);
		craftGrid = new ItemStack[9];
		if(gridTagList != null){
			for (int tagCounter = 0; tagCounter < gridTagList.tagCount(); ++tagCounter){
				NBTTagCompound gridTag = gridTagList.getCompoundTagAt(tagCounter);
				byte slotIndex = gridTag.getByte("slot");
				if(slotIndex >= 0 && slotIndex < 9){
					craftGrid[slotIndex] = ItemStack.loadItemStackFromNBT(gridTag);
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("filterId", 2);
		if(product != null){
			NBTTagCompound productTag = new NBTTagCompound();
			product.writeToNBT(productTag);
			tag.setTag("product", productTag);
		}
		if(materials != null){
			NBTTagList itemsTagList = new NBTTagList();
			for (int i = 0; i < materials.length; i++){
				if(materials[i] != null){
					NBTTagCompound itemTag = new NBTTagCompound();
					materials[i].writeToNBT(itemTag);
					itemsTagList.appendTag(itemTag);
				}
			}
			tag.setTag("material", itemsTagList);
		}
		if(craftGrid != null){
			NBTTagList itemsTagList = new NBTTagList();
			for (int i = 0; i < 9; i++){
				if(craftGrid[i] != null){
					NBTTagCompound itemTag = new NBTTagCompound();
					itemTag.setByte("slot", (byte) i);
					craftGrid[i].writeToNBT(itemTag);
					itemsTagList.appendTag(itemTag);
				}
			}
			tag.setTag("craftGrid", itemsTagList);
		}
	}

}
