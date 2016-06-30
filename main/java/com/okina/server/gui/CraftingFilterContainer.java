package com.okina.server.gui;

import com.okina.inventory.CraftingFilterInventory;
import com.okina.inventory.GhostSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class CraftingFilterContainer extends Container {

	/** The crafting matrix inventory (3x3). */
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public Slot recipeSlot;
	private World worldObj;
	private CraftingFilterInventory filter;

	public CraftingFilterContainer(InventoryPlayer inventoryPlayer, CraftingFilterInventory filter, World world) {
		worldObj = world;
		this.filter = filter;

		recipeSlot = new Slot(filter, 9, 124, 35);
		addSlotToContainer(recipeSlot);

		int row;
		int column;
		for (row = 0; row < 3; ++row){
			for (column = 0; column < 3; ++column){
				GhostSlot slot = new GhostSlot(craftMatrix, column + row * 3, 30 + column * 18, 17 + row * 18);
				addSlotToContainer(slot);
				slot.putStack(filter.craftGrid[column + row * 3]);
			}
		}
		for (row = 0; row < 3; ++row){
			for (column = 0; column < 9; ++column){
				addSlotToContainer(new Slot(inventoryPlayer, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row){
			addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 142));
		}

		onCraftMatrixChanged(craftMatrix);
	}

	@Override
	public void onCraftMatrixChanged(IInventory craftMatrix) {
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, worldObj);
		filter.setRecipe(this.craftMatrix, result);
		recipeSlot.putStack(result);
		filter.markDirty();
		//		if(result != null){
		//			ItemStack[] materials = new ItemStack[9];
		//			ItemStack material;
		//			int types = 0;
		//			for (int i = 0; i < 9; i++){
		//				material = this.craftMatrix.getStackInSlot(i);
		//				if(material != null){
		//					material = material.copy();
		//					material.stackSize = 1;
		//					for (int j = 0; j < 9; j++){
		//						if(materials[j] != null){
		//							if(materials[j].isItemEqual(material) && ItemStack.areItemStackTagsEqual(materials[j], material)){
		//								materials[j].stackSize++;
		//								break;
		//							}else{
		//								continue;
		//							}
		//						}else{
		//							materials[j] = material;
		//							types++;
		//							break;
		//						}
		//					}
		//				}
		//			}
		//			NBTTagCompound tag = new NBTTagCompound();
		//
		//			NBTTagCompound productTag = new NBTTagCompound();
		//			result.writeToNBT(productTag);
		//			tag.setTag("product", productTag);
		//
		//			NBTTagList itemsTagList = new NBTTagList();
		//			for (int i = 0; i < 9; i++){
		//				if(materials[i] != null){
		//					NBTTagCompound itemTag = new NBTTagCompound();
		//					materials[i].writeToNBT(itemTag);
		//					itemsTagList.appendTag(itemTag);
		//				}
		//			}
		//			tag.setTag("material", itemsTagList);
		//			filter.setTagCompound(tag);
		//			System.out.println(tag);
		//		}else{
		//			filter.setTagCompound(null);
		//		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if(!worldObj.isRemote){
			ItemStack result = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj);
			filter.setRecipe(craftMatrix, result);
			for (int i = 0; i < 9; i++){
				filter.craftGrid[i] = craftMatrix.getStackInSlot(i);
			}
			recipeSlot.putStack(result);
			filter.markDirty();
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotIndex, int mouseButton, int command, EntityPlayer player) {
		ItemStack returnStack = null;
		InventoryPlayer inventoryplayer = player.inventory;
		int i1;
		ItemStack clickedStack;

		if(true){
			Slot clickedSlot;
			int givingStackSize;
			ItemStack itemstack5;

			//clicked
			if((command == 0 || command == 1) && (mouseButton == 0 || mouseButton == 1)){

				//outside
				if(slotIndex == -999){
					if(inventoryplayer.getItemStack() != null && slotIndex == -999){
						if(mouseButton == 0){
							player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), true);
							inventoryplayer.setItemStack((ItemStack) null);
						}

						if(mouseButton == 1){
							player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack().splitStack(1), true);

							if(inventoryplayer.getItemStack().stackSize == 0){
								inventoryplayer.setItemStack((ItemStack) null);
							}
						}
					}

					//shift clicked
				}else if(command == 1){
					if(slotIndex < 0){
						return null;
					}

					clickedSlot = (Slot) inventorySlots.get(slotIndex);

					if(clickedSlot != null && clickedSlot.canTakeStack(player)){
						clickedStack = transferStackInSlot(player, slotIndex);

						if(clickedStack != null){
							Item item = clickedStack.getItem();
							returnStack = clickedStack.copy();

							if(clickedSlot.getStack() != null && clickedSlot.getStack().getItem() == item){
								retrySlotClick(slotIndex, mouseButton, true, player);//necessarily?????
							}
						}
					}

					//normally clicked
				}else{
					if(slotIndex < 0){
						return null;
					}

					clickedSlot = (Slot) inventorySlots.get(slotIndex);

					if(clickedSlot != null){
						clickedStack = clickedSlot.getStack();
						ItemStack playerInvStack = inventoryplayer.getItemStack();

						if(clickedStack != null){
							returnStack = clickedStack.copy();
						}

						if(clickedStack == null){
							if(playerInvStack != null && clickedSlot.isItemValid(playerInvStack) && canPlaceStack(clickedSlot.getSlotIndex())){

								if(clickedSlot instanceof GhostSlot){
									ItemStack tmpStack = playerInvStack.copy();
									tmpStack.stackSize = 1;
									clickedSlot.putStack(tmpStack);
								}else{
									givingStackSize = mouseButton == 0 ? playerInvStack.stackSize : 1;
									if(givingStackSize > clickedSlot.getSlotStackLimit()){
										givingStackSize = clickedSlot.getSlotStackLimit();
									}
									if(playerInvStack.stackSize >= givingStackSize){
										ItemStack tmpStack = playerInvStack.copy();
										tmpStack.stackSize = 1;
										clickedSlot.putStack(tmpStack);
										clickedSlot.putStack(playerInvStack.splitStack(givingStackSize));
									}
									if(playerInvStack.stackSize == 0){
										inventoryplayer.setItemStack((ItemStack) null);
									}
								}
							}
						}else if(clickedSlot.canTakeStack(player)){
							if(playerInvStack == null){
								givingStackSize = mouseButton == 0 ? clickedStack.stackSize : (clickedStack.stackSize + 1) / 2;
								itemstack5 = clickedSlot.decrStackSize(givingStackSize);
								inventoryplayer.setItemStack(itemstack5);

								if(clickedStack.stackSize == 0){
									clickedSlot.putStack((ItemStack) null);
								}

								clickedSlot.onPickupFromSlot(player, inventoryplayer.getItemStack());
							}else if(clickedSlot.isItemValid(playerInvStack)){
								if(clickedStack.getItem() == playerInvStack.getItem() && clickedStack.getItemDamage() == playerInvStack.getItemDamage() && ItemStack.areItemStackTagsEqual(clickedStack, playerInvStack)){
									givingStackSize = mouseButton == 0 ? playerInvStack.stackSize : 1;

									if(givingStackSize > clickedSlot.getSlotStackLimit() - clickedStack.stackSize){
										givingStackSize = clickedSlot.getSlotStackLimit() - clickedStack.stackSize;
									}

									if(givingStackSize > playerInvStack.getMaxStackSize() - clickedStack.stackSize){
										givingStackSize = playerInvStack.getMaxStackSize() - clickedStack.stackSize;
									}

									playerInvStack.splitStack(givingStackSize);

									if(playerInvStack.stackSize == 0){
										inventoryplayer.setItemStack((ItemStack) null);
									}

									clickedStack.stackSize += givingStackSize;
								}else if(playerInvStack.stackSize <= clickedSlot.getSlotStackLimit()){
									clickedSlot.putStack(playerInvStack);
									inventoryplayer.setItemStack(clickedStack);
								}
							}else if(clickedStack.getItem() == playerInvStack.getItem() && playerInvStack.getMaxStackSize() > 1 && (!clickedStack.getHasSubtypes() || clickedStack.getItemDamage() == playerInvStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(clickedStack, playerInvStack)){
								givingStackSize = clickedStack.stackSize;

								if(givingStackSize > 0 && givingStackSize + playerInvStack.stackSize <= playerInvStack.getMaxStackSize()){
									playerInvStack.stackSize += givingStackSize;
									clickedStack = clickedSlot.decrStackSize(givingStackSize);

									if(clickedStack.stackSize == 0){
										clickedSlot.putStack((ItemStack) null);
									}

									clickedSlot.onPickupFromSlot(player, inventoryplayer.getItemStack());
								}
							}
						}

						clickedSlot.onSlotChanged();
					}
				}
			}else if(command == 2 && mouseButton >= 0 && mouseButton < 9){//2 : ???
				clickedSlot = (Slot) inventorySlots.get(slotIndex);

				if(clickedSlot.canTakeStack(player)){
					clickedStack = inventoryplayer.getStackInSlot(mouseButton);
					boolean flag = clickedStack == null || clickedSlot.inventory == inventoryplayer && clickedSlot.isItemValid(clickedStack);
					givingStackSize = -1;

					if(!flag){
						givingStackSize = inventoryplayer.getFirstEmptyStack();
						flag |= givingStackSize > -1;
					}

					if(clickedSlot.getHasStack() && flag){
						itemstack5 = clickedSlot.getStack();
						inventoryplayer.setInventorySlotContents(mouseButton, itemstack5.copy());

						if((clickedSlot.inventory != inventoryplayer || !clickedSlot.isItemValid(clickedStack)) && clickedStack != null){
							if(givingStackSize > -1){
								inventoryplayer.addItemStackToInventory(clickedStack);
								clickedSlot.decrStackSize(itemstack5.stackSize);
								clickedSlot.putStack((ItemStack) null);
								clickedSlot.onPickupFromSlot(player, itemstack5);
							}
						}else{
							clickedSlot.decrStackSize(itemstack5.stackSize);
							clickedSlot.putStack(clickedStack);
							clickedSlot.onPickupFromSlot(player, itemstack5);
						}
					}else if(!clickedSlot.getHasStack() && clickedStack != null && clickedSlot.isItemValid(clickedStack)){
						inventoryplayer.setInventorySlotContents(mouseButton, (ItemStack) null);
						clickedSlot.putStack(clickedStack);
					}
				}
			}else if(command == 3 && player.capabilities.isCreativeMode && inventoryplayer.getItemStack() == null && slotIndex >= 0){//wheel
				clickedSlot = (Slot) inventorySlots.get(slotIndex);

				if(clickedSlot != null && clickedSlot.getHasStack()){
					clickedStack = clickedSlot.getStack().copy();
					clickedStack.stackSize = clickedStack.getMaxStackSize();
					inventoryplayer.setItemStack(clickedStack);
				}
			}else if(command == 4 && inventoryplayer.getItemStack() == null && slotIndex >= 0){//outside
				clickedSlot = (Slot) inventorySlots.get(slotIndex);

				if(clickedSlot != null && clickedSlot.getHasStack() && clickedSlot.canTakeStack(player)){
					clickedStack = clickedSlot.decrStackSize(mouseButton == 0 ? 1 : clickedSlot.getStack().stackSize);
					clickedSlot.onPickupFromSlot(player, clickedStack);
					player.dropPlayerItemWithRandomChoice(clickedStack, true);
				}
			}else if(command == 6 && slotIndex >= 0){//double
				clickedSlot = (Slot) inventorySlots.get(slotIndex);
				clickedStack = inventoryplayer.getItemStack();

				if(clickedStack != null && (clickedSlot == null || !clickedSlot.getHasStack() || !clickedSlot.canTakeStack(player))){
					i1 = mouseButton == 0 ? 0 : inventorySlots.size() - 1;
					givingStackSize = mouseButton == 0 ? 1 : -1;

					for (int i2 = 0; i2 < 2; ++i2){
						for (int j2 = i1; j2 >= 0 && j2 < inventorySlots.size() && clickedStack.stackSize < clickedStack.getMaxStackSize(); j2 += givingStackSize){
							Slot slot3 = (Slot) inventorySlots.get(j2);

							if(slot3.getHasStack() && func_94527_a(slot3, clickedStack, true) && slot3.canTakeStack(player) && func_94530_a(clickedStack, slot3) && (i2 != 0 || slot3.getStack().stackSize != slot3.getStack().getMaxStackSize())){
								int k1 = Math.min(clickedStack.getMaxStackSize() - clickedStack.stackSize, slot3.getStack().stackSize);
								ItemStack itemstack2 = slot3.decrStackSize(k1);
								clickedStack.stackSize += k1;

								if(itemstack2.stackSize <= 0){
									slot3.putStack((ItemStack) null);
								}

								slot3.onPickupFromSlot(player, itemstack2);
							}
						}
					}
				}

				detectAndSendChanges();
			}
		}
		return returnStack;
	}

	private boolean canPlaceStack(int slotIndex) {
		return slotIndex != 9;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(slotIndex >= 37 && slotIndex < 46){
				if(!mergeItemStack(itemstack1, 10, 37, false)){
					return null;
				}
			}

			if(itemstack1.stackSize == 0){
				slot.putStack((ItemStack) null);
			}else{
				slot.onSlotChanged();
			}

			if(itemstack1.stackSize == itemstack.stackSize){
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return false;
	}

	/**can place item?*/
	@Override
	public boolean func_94530_a(ItemStack itemStack, Slot slot) {
		if(slot.getSlotIndex() == 0) return false;
		return slot.inventory.isItemValidForSlot(slot.slotNumber, itemStack) && super.func_94530_a(itemStack, slot);
	}

}
