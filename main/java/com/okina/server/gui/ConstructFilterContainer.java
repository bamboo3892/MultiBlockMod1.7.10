package com.okina.server.gui;

import java.util.HashSet;
import java.util.Set;

import com.okina.inventory.FilterInventory;
import com.okina.inventory.GhostSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ConstructFilterContainer extends Container {

	private IInventory player;
	private FilterInventory filter;
	private int field_94535_f = -1;
	private int dragStartButton;
	private final Set<Slot> dragedSlots = new HashSet<Slot>();

	public ConstructFilterContainer(IInventory player, FilterInventory filter) {
		this.player = player;
		this.filter = filter;

		for (int col = 0; col < 3; ++col){
			for (int row = 0; row < 3; ++row){
				addSlotToContainer(new GhostSlot(filter, row + col * 3, 62 + row * 18, 17 + col * 18));
			}
		}
		for (int col = 0; col < 3; ++col){
			for (int row = 0; row < 9; ++row){
				addSlotToContainer(new Slot(player, row + col * 9 + 9, 8 + row * 18, 84 + col * 18));
			}
		}
		for (int row = 0; row < 9; ++row){
			addSlotToContainer(new Slot(player, row, 8 + row * 18, 142));
		}
	}

	/**
	 * <blockquote><pre>
	 * slotIndex
	 * -999 : no destination
	 * 	(-999, *, 4) : outside
	 * 	(-999, 0, 5) : drag start (left or wheel)
	 * 	(-999, 2, 5) : drag release (left or wheel)
	 * 	(-999, 4, 5) : drag start (right)
	 * 	(-999, 6, 5) : drag release (right)
	 *
	 * mouse button
	 * 0 : left
	 * 1 : right
	 * 2 : wheel or drag release
	 *
	 * command
	 * 0 : normal
	 * 1 : shift
	 * 2 : ??? related to player inventory (armor?)
	 * 3 : wheel (include outside)
	 * 4 : outside
	 * 5 : drag
	 * 6 : double ckick (left normal click)
	 *
	 * when player double-click the same item,
	 * shift left click apply all the item in the inventory (not player inventory)
	 *</blockquote></pre>
	 */
	@Override
	public ItemStack slotClick(int slotIndex, int mouseButton, int command, EntityPlayer player) {
		ItemStack returnStack = null;
		InventoryPlayer inventoryplayer = player.inventory;
		int i1;
		ItemStack clickedStack;
		//System.out.println(slotIndex + ", " + mouseButton + ", " + command);

		/*
		if(command == 5){
			System.out.println("");
			int pastButton = this.dragStartButton;
			this.dragStartButton = func_94532_c(mouseButton);
		
			if((pastButton != 1 || this.dragStartButton != 2) && pastButton != this.dragStartButton){
				this.func_94533_d();
			}else if(inventoryplayer.getItemStack() == null){
				this.func_94533_d();
			}else if(this.dragStartButton == 0){
				this.field_94535_f = func_94529_b(mouseButton);
		
				if(func_94528_d(this.field_94535_f)){//left or right
					this.dragStartButton = 1;
					this.dragedSlots.clear();
				}else{
					this.func_94533_d();
				}
			}else if(this.dragStartButton == 1){
				Slot slot = (Slot) this.inventorySlots.get(slotIndex);
		
				if(slot != null && func_94527_a(slot, inventoryplayer.getItemStack(), true) && slot.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize > this.dragedSlots.size() && this.canDragIntoSlot(slot)){
					this.dragedSlots.add(slot);
				}
			}else if(this.dragStartButton == 2){
				if(!this.dragedSlots.isEmpty()){
					clickedStack = inventoryplayer.getItemStack().copy();
					i1 = inventoryplayer.getItemStack().stackSize;
					Iterator iterator = this.dragedSlots.iterator();
		
					while (iterator.hasNext()){
						Slot slot1 = (Slot) iterator.next();
		
						if(slot1 != null && func_94527_a(slot1, inventoryplayer.getItemStack(), true) && slot1.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize >= this.dragedSlots.size() && this.canDragIntoSlot(slot1)){
							ItemStack itemstack1 = clickedStack.copy();
							int j1 = slot1.getHasStack() ? slot1.getStack().stackSize : 0;
							func_94525_a(this.dragedSlots, this.field_94535_f, itemstack1, j1);
		
							if(itemstack1.stackSize > itemstack1.getMaxStackSize()){
								itemstack1.stackSize = itemstack1.getMaxStackSize();
							}
		
							if(itemstack1.stackSize > slot1.getSlotStackLimit()){
								itemstack1.stackSize = slot1.getSlotStackLimit();
							}
		
							i1 -= itemstack1.stackSize - j1;
							slot1.putStack(itemstack1);
						}
					}
		
					clickedStack.stackSize = i1;
		
					if(clickedStack.stackSize <= 0){
						clickedStack = null;
					}
		
					inventoryplayer.setItemStack(clickedStack);
				}
		
				this.func_94533_d();
			}else{
				this.func_94533_d();
			}
		}else if(this.dragStartButton != 0){
			System.out.println("");
			this.func_94533_d();
		}else
		*/

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
							if(playerInvStack != null && clickedSlot.isItemValid(playerInvStack)){

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

	/**reset drag setting*/
	@Override
	protected void func_94533_d() {
		dragStartButton = 0;
		dragedSlots.clear();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if(slot != null && slot.getHasStack()){
			itemstack = slot.getStack();

			if(slotIndex < 9){
				slot.putStack(null);
				slot.onSlotChanged();
				return null;
			}
			registerToFilter(itemstack);
		}

		return itemstack;
	}

	@Override
	protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer p_75133_4_) {
		return;
		//this.slotClick(p_75133_1_, p_75133_2_, 1, p_75133_4_);
	}

	private void registerToFilter(ItemStack itemStack) {
		if(itemStack == null) return;
		for (int index = 0; index < 9; index++){
			Slot slot = (Slot) inventorySlots.get(index);
			if(slot == null){
				continue;
			}else if(slot.getHasStack()){
				if(slot.getStack().getItem() == itemStack.getItem()){
					break;
				}
			}else{
				slot.putStack(itemStack.copy());
				slot.onSlotChanged();
				break;
			}
		}
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return false;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return filter.isUseableByPlayer(player);
	}

}
