package com.okina.utils;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class InventoryHelper {

	public static final int SIMULATE = 0;
	public static final int AS_MANY_AS_POSSIBLE = 1;
	public static final int WHOLE = 2;

	/**Returns true if any item moved*/
	public static boolean tryPushItemEX(IInventory start, IInventory goal, ForgeDirection startSide, ForgeDirection goalSide, int amount) {
		if(start == null && amount <= 0) return false;
		ItemStack item;
		int restPushSize = amount;
		if(start instanceof ISidedInventory){
			ISidedInventory backSidedInv = (ISidedInventory) start;
			int[] accessible = backSidedInv.getAccessibleSlotsFromSide(startSide.ordinal());
			for (int slot : accessible){
				if(backSidedInv.getStackInSlot(slot) != null){
					for (int i = 0; i < restPushSize; i++){
						item = backSidedInv.getStackInSlot(slot);
						if(item != null){
							ItemStack item1 = item.copy();
							item1.stackSize = 1;
							if(backSidedInv.canExtractItem(slot, item1, startSide.ordinal())){
								if(tryPushOneItem(goal, goalSide, item1.copy())){
									restPushSize--;
									item.stackSize--;
									if(item.stackSize <= 0){
										item = null;
									}
									backSidedInv.setInventorySlotContents(slot, item);
									if(restPushSize <= 0){
										backSidedInv.markDirty();
										return true;
									}
									continue;
								}
							}
						}
						break;
					}
					//					int size = Math.min(amount, item.stackSize);
					//					item.stackSize = size;
					//					if(backSidedInv.canExtractItem(slot, item, startSide.ordinal())){
					//						int originalStackSize = item.stackSize;
					//						ItemStack restItem = InventoryHelper.tryPushItem(goal, goalSide, item, AS_MANY_AS_POSSIBLE);
					//						backSidedInv.decrStackSize(slot, restItem == null ? size : originalStackSize - restItem.stackSize);
					//						amount -= restItem == null ? size : originalStackSize - restItem.stackSize;
					//						if(amount <= 0){
					//							backSidedInv.markDirty();
					//							return true;
					//						}
					//					}
				}
			}
		}else{
			for (int slot = 0; slot < start.getSizeInventory(); slot++){
				if(start.getStackInSlot(slot) != null){
					for (int i = 0; i < restPushSize; i++){
						item = start.getStackInSlot(slot);
						if(item != null){
							ItemStack item1 = item.copy();
							item1.stackSize = 1;
							if(tryPushOneItem(goal, goalSide, item1.copy())){
								restPushSize--;
								item.stackSize--;
								if(item.stackSize <= 0){
									item = null;
								}
								start.setInventorySlotContents(slot, item);
								if(restPushSize <= 0){
									start.markDirty();
									return true;
								}
								continue;
							}
						}
						break;
					}
					//					item = start.getStackInSlot(slot).copy();
					//					int size = Math.min(restPushSize, item.stackSize);
					//					item.stackSize = size;
					//					int originalStackSize = item.stackSize;
					//					ItemStack restItem = InventoryHelper.tryPushItem(goal, goalSide, item, AS_MANY_AS_POSSIBLE);
					//					int splitSize = restItem == null ? size : originalStackSize - restItem.stackSize;
					//					if(splitSize != 0){
					//						start.decrStackSize(slot, splitSize);
					//						restPushSize -= splitSize;
					//						if(restPushSize <= 0){
					//							start.markDirty();
					//							return true;
					//						}
					//					}
				}
			}
		}
		if(amount != restPushSize){
			start.markDirty();
			return true;
		}else{
			return false;
		}
	}

	/**command 
	 * 0 : simulate
	 * 1 : try push item as many as possible
	 * 2 : try push item if can transfer whole itemstack
	 * Returns the rest of param itemstack
	 */
	public static ItemStack tryPushItem(IInventory goal, ForgeDirection side, ItemStack item, int command) {
		if(item != null){
			ItemStack itemstack = item.copy();
			if(command == WHOLE || command == SIMULATE){
				return tryPushWholeItem(goal, side, itemstack, command);
			}else if(command == AS_MANY_AS_POSSIBLE){
				int size = 0;
				int originalSize = itemstack.stackSize;
				for (int i = 0; i < originalSize; i++){
					if(tryPushOneItem(goal, side, itemstack.copy())){
						size++;
					}else{
						break;
					}
				}
				itemstack.stackSize -= size;
				if(itemstack.stackSize <= 0){
					itemstack = null;
				}
			}
			return itemstack;
		}
		return item;
	}

	public static ItemStack tryPushWholeItem(IInventory goal, ForgeDirection side, ItemStack item, int command) {
		if(goal == null || item == null){
			return item;
		}
		ItemStack original = item.copy();
		int initSize = item.stackSize;
		if(goal instanceof ISidedInventory){
			ISidedInventory sided = (ISidedInventory) goal;
			int[] accessible = sided.getAccessibleSlotsFromSide(side.ordinal());
			if(accessible != null){
				ItemStack[] goalInv = new ItemStack[accessible.length];
				for (int i = 0; i < accessible.length; i++){
					if(!sided.isItemValidForSlot(accessible[i], item)) continue;
					if(!sided.canInsertItem(accessible[i], item, side.ordinal())) continue;
					goalInv[i] = sided.getStackInSlot(accessible[i]) == null ? null : sided.getStackInSlot(accessible[i]).copy();
					int stackable = getStackableSize(item, goalInv[i], sided.getInventoryStackLimit());
					if(stackable > 0){
						item.stackSize -= stackable;
						if(goalInv[i] != null){
							goalInv[i].stackSize += stackable;
						}else{
							goalInv[i] = item.copy();
							goalInv[i].stackSize = stackable;
						}
						if(item.stackSize <= 0){//transfer complete
							if(command != SIMULATE){
								for (int j = 0; j <= i; j++){
									sided.setInventorySlotContents(accessible[j], goalInv[j]);
								}
								sided.markDirty();
								return null;
							}else{
								return null;
							}
						}
					}
				}
				if(item.stackSize <= 0){
					if(command != SIMULATE){
						for (int i = 0; i <= accessible.length; i++){
							sided.setInventorySlotContents(accessible[i], goalInv[i]);
						}
						sided.markDirty();
						return null;
					}else{
						return null;
					}
				}
				//transfer imcomplete
				if(command == AS_MANY_AS_POSSIBLE && item.stackSize < initSize){
					for (int i = 0; i < accessible.length; i++){
						sided.setInventorySlotContents(accessible[i], goalInv[i]);
					}
					sided.markDirty();
					return item;
				}
			}
			return original;
		}else{
			IInventory inv = goal;
			ItemStack[] goalInv = new ItemStack[inv.getSizeInventory()];
			for (int i = 0; i < inv.getSizeInventory(); i++){
				if(!inv.isItemValidForSlot(i, item)) continue;
				goalInv[i] = inv.getStackInSlot(i) == null ? null : inv.getStackInSlot(i).copy();
				int stackable = getStackableSize(item, goalInv[i], inv.getInventoryStackLimit());
				if(stackable > 0){
					item.stackSize -= stackable;
					if(goalInv[i] != null){
						goalInv[i].stackSize += stackable;
					}else{
						goalInv[i] = item.copy();
						goalInv[i].stackSize = stackable;
					}
					if(item.stackSize <= 0){//transfer complete
						if(command != SIMULATE){
							for (int j = 0; j <= i; j++){
								inv.setInventorySlotContents(j, goalInv[j]);
							}
							inv.markDirty();
							return null;
						}else{
							return null;
						}
					}
				}
			}
			//transfer imcomplete
			if(command == AS_MANY_AS_POSSIBLE && item.stackSize < initSize){
				for (int i = 0; i < inv.getSizeInventory(); i++){
					inv.setInventorySlotContents(i, goalInv[i]);
				}
				inv.markDirty();
				return item;
			}
			return original;
		}
	}

	public static boolean tryPushOneItem(IInventory tile, ForgeDirection side, ItemStack item) {
		boolean flag = false;
		if(tile instanceof ISidedInventory){
			ISidedInventory sided = (ISidedInventory) tile;
			int[] accessible = sided.getAccessibleSlotsFromSide(side.ordinal());
			ItemStack ret = null;
			int slot = 0;
			for (int i : accessible){
				ItemStack destitem = sided.getStackInSlot(i);
				if(!sided.isItemValidForSlot(i, item) || !sided.canInsertItem(i, item, side.ordinal())) continue;
				ret = item.copy();
				ret.stackSize = 1;
				if(destitem == null || isItemStackable(ret, destitem, sided.getInventoryStackLimit())){
					slot = i;
					flag = true;
					break;
				}
			}
			if(ret != null && flag){
				if(sided.getStackInSlot(slot) == null){
					sided.setInventorySlotContents(slot, ret);
				}else{
					sided.getStackInSlot(slot).stackSize++;
				}
				sided.markDirty();
				return true;
			}
		}else if(tile instanceof IInventory){
			IInventory inv = tile;

			ItemStack ret = null;
			int slot = 0;

			for (int i = 0; i < inv.getSizeInventory(); i++){
				ItemStack destitem = inv.getStackInSlot(i);
				if(!inv.isItemValidForSlot(i, item)) continue;

				ret = item.copy();
				ret.stackSize = 1;

				if(destitem == null || isItemStackable(ret, destitem, inv.getInventoryStackLimit())){
					slot = i;
					flag = true;
					break;
				}
			}
			if(ret != null && flag){
				if(inv.getStackInSlot(slot) == null){
					inv.setInventorySlotContents(slot, ret);
				}else{
					inv.getStackInSlot(slot).stackSize++;
				}

				inv.markDirty();
				return true;
			}
		}
		return false;
	}

	public static int getStackableSize(ItemStack current, ItemStack target, int invStackLimit) {
		if(current != null){
			if(target != null){
				if(current.isItemEqual(target) && ItemStack.areItemStackTagsEqual(current, target)){
					return Math.min(current.stackSize, Math.min(invStackLimit, current.getMaxStackSize()) - target.stackSize);
				}
			}else{
				return Math.min(current.stackSize, Math.min(invStackLimit, current.getMaxStackSize()));
			}
		}
		return 0;
	}

	public static boolean isItemStackable(ItemStack target, ItemStack current, int invMaxStackSize) {
		if(target == null || current == null) return false;
		if(target.getItem() == current.getItem() && target.getItemDamage() == current.getItemDamage()){
			return (current.stackSize + target.stackSize) <= current.getMaxStackSize() && (current.stackSize + target.stackSize) <= invMaxStackSize;
		}
		return false;
	}

	//ore dictionery/////////////////////////////////////////////////////////////////////////////////////

	public static boolean hasOreItem(Object obj) {
		if(obj instanceof String){
			return !OreDictionary.getOres((String) obj).isEmpty();
		}else if(obj instanceof ItemStack){
			return true;
		}else if(obj instanceof Integer){
			return !OreDictionary.getOres((Integer) obj).isEmpty();
		}
		return false;
	}

	/**
	 * Returns ItemStack of param ore.<br>
	 * Index is always 0;
	 * @param oreName
	 * @return
	 */
	public static ItemStack getOreItemForServer(Object obj) {
		if(obj instanceof String){
			List<ItemStack> list = OreDictionary.getOres((String) obj);
			if(list != null && !list.isEmpty()){
				return list.get(0);
			}
		}else if(obj instanceof ItemStack){
			return (ItemStack) obj;
		}else if(obj instanceof Integer){
			List<ItemStack> list = OreDictionary.getOres((Integer) obj);
			if(list != null && !list.isEmpty()){
				return list.get(0);
			}
		}
		return null;
	}

	/**
	 * 
	 * To check if ore id is valid, use {@link InventoryHelper#hasOreItem} method.
	 * @param oreName
	 * @param index
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public static ItemStack getOreItemForClient(Object oreName, int index) {
		List<ItemStack> list = getOreItems(oreName);
		if(list != null && !list.isEmpty()){
			int size = 0;
			for (ItemStack item : list){
				if(item != null){
					if(item.getItemDamage() != OreDictionary.WILDCARD_VALUE){
						size++;
					}else{
						if(item.getHasSubtypes()){
							List<ItemStack> tmp = Lists.newArrayList();
							item.getItem().getSubItems(item.getItem(), CreativeTabs.tabAllSearch, tmp);
							size += tmp.size();
						}
					}
				}
			}
			int index1 = index % size;
			int index2 = 0;
			for (ItemStack item : list){
				if(item != null){
					if(item.getItemDamage() != OreDictionary.WILDCARD_VALUE){
						if(index1 == index2){
							return item.copy();
						}else{
							index2++;
						}
					}else{
						if(item.getHasSubtypes()){
							List<ItemStack> tmp = Lists.newArrayList();
							item.getItem().getSubItems(item.getItem(), CreativeTabs.tabAllSearch, tmp);
							if(!tmp.isEmpty()){
								if(index1 - index2 >= tmp.size()){
									index2 += tmp.size();
								}else{
									return tmp.get(index1 - index2);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**Returns the ore items.
	 * If obj is ItemStack and its damage is WILDCARD, return its sub items. 
	 * */
	public static List<ItemStack> getOreItems(Object obj) {
		List<ItemStack> list = Lists.newArrayList();
		if(obj instanceof String){
			list.addAll(OreDictionary.getOres((String) obj));
		}else if(obj instanceof ItemStack){
			ItemStack item = (ItemStack) obj;
			if(item.getItemDamage() == OreDictionary.WILDCARD_VALUE && item.getHasSubtypes()){
				item.getItem().getSubItems(item.getItem(), CreativeTabs.tabAllSearch, list);
			}else{
				list.add(item);
			}
			//			int[] ids = OreDictionary.getOreIDs((ItemStack) obj);
			//			if(ids.length == 0){
			//				list.add((ItemStack) obj);
			//			}else{
			//				for (int id : ids){
			//					list.addAll(OreDictionary.getOres(id));
			//				}
			//			}
		}else if(obj instanceof Integer){
			list.addAll(OreDictionary.getOres((Integer) obj));
		}
		return list;
	}

	public static boolean isItemMaches(Object obj1, Object obj2) {
		if(obj1 instanceof ItemStack && obj2 instanceof ItemStack){
			return OreDictionary.itemMatches((ItemStack) obj1, (ItemStack) obj2, false) || OreDictionary.itemMatches((ItemStack) obj2, (ItemStack) obj1, false);
		}else{
			return isOreIDMaches(obj1, obj2);
		}
	}

	public static boolean isOreIDMaches(Object obj1, Object obj2) {
		if(obj1 instanceof String){
			if(obj2 instanceof String){
				return obj1.equals(obj2);
			}else if(obj2 instanceof ItemStack){
				int id1 = OreDictionary.getOreID((String) obj1);
				int[] ids2 = OreDictionary.getOreIDs((ItemStack) obj2);
				for (int id2 : ids2){
					if(id1 == id2) return true;
				}
				return false;
			}else if(obj2 instanceof Integer){
				return OreDictionary.getOreID((String) obj1) == (Integer) obj2;
			}
		}else if(obj1 instanceof ItemStack){
			if(obj2 instanceof String){
				return isOreIDMaches(obj2, obj1);
			}else if(obj2 instanceof ItemStack){
				int[] ids1 = OreDictionary.getOreIDs((ItemStack) obj1);
				int[] ids2 = OreDictionary.getOreIDs((ItemStack) obj2);
				if(ids1.length == 0 || ids2.length == 0){
					return OreDictionary.itemMatches((ItemStack) obj1, (ItemStack) obj2, false);
				}
				for (int id1 : ids1){
					for (int id2 : ids2){
						if(id1 == id2) return true;
					}
				}
			}else if(obj2 instanceof Integer){
				int[] ids1 = OreDictionary.getOreIDs((ItemStack) obj1);
				int id2 = (Integer) obj2;
				for (int id1 : ids1){
					if(id1 == id2) return true;
				}
			}
		}else if(obj1 instanceof Integer){
			if(obj2 instanceof String){
				return isOreIDMaches(obj2, obj1);
			}else if(obj2 instanceof ItemStack){
				return isOreIDMaches(obj2, obj1);
			}else if(obj2 instanceof Integer){
				return (Integer) obj1 == (Integer) obj2;
			}
		}
		return false;
	}

}







