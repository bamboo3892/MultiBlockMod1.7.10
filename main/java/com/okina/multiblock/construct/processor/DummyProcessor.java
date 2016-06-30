package com.okina.multiblock.construct.processor;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.utils.ColoredString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;

public class DummyProcessor extends ProcessorBase {

	public DummyProcessor() {
		this(null, false, true, 0, 0, 0, 0);
	}

	public DummyProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void updateEntity() {

	}

	@Override
	public String getNameForNBT() {
		return "dummy";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("DUMMY", 0x000000);
	}

	@Override
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		return Lists.newArrayList(new ColoredString("Grade : " + GRADE_NAME[grade], 0x000000));
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public List<ColoredString> getHUDStringsForCenter(MovingObjectPosition mop, double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForCenter(mop, renderTicks);
		list.add(new ColoredString("This Block Must Be Replaced", 0x000000));
		return list;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@SideOnly(Side.CLIENT)
	public Block getRenderBlock() {
		return Blocks.stone;
	}

}
