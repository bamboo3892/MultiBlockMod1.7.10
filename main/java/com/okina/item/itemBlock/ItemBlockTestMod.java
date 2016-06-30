package com.okina.item.itemBlock;

import com.okina.block.ITestModBlock;
import com.okina.main.TestCore;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTestMod extends ItemBlock {

	public ItemBlockTestMod(Block block) {
		super(block);
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(64);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return ((ITestModBlock) field_150939_a).getRarity(itemStack);
	}

}
