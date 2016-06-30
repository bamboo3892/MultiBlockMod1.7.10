package com.okina.creativetab;

import com.okina.main.TestCore;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class TestCreativeTab extends CreativeTabs {

	public TestCreativeTab(String name) {
		super(name);
	}

	@Override
	public Item getTabIconItem() {
		return TestCore.toilet;
	}

}
