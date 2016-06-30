package com.okina.item.itemBlock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockWithMeta extends ItemBlockWithMetadata {

	public ItemBlockWithMeta(Block b) {
		super(b, b);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack == null){
			return Block.getBlockFromItem(this).getUnlocalizedName();
		}
		return Block.getBlockFromItem(this).getUnlocalizedName() + "_" + itemstack.getItemDamage();
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

}
