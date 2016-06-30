package com.okina.multiblock.construct.processor;

import com.okina.client.gui.ConstructStorageGui;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.server.gui.ConstructStorageContainer;
import com.okina.utils.ColoredString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StorageProcessor extends FilterUserProcessor {

	public static final int[] SLOTS;

	static{
		SLOTS = new int[27];
		for (int i = 0; i < 27; i++){
			SLOTS[i] = i;
		}
	}

	public StorageProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade, 27, 64, "Storage");
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!isRemote){
			itemTransfer(maxTransfer[grade]);
		}
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		Object elem = super.getGuiElement(player, side, serverSide);
		if(elem == null){
			return serverSide ? new ConstructStorageContainer(player.inventory, internalInv) : new ConstructStorageGui(player.inventory, internalInv);
		}else{
			return elem;
		}
	}

	@Override
	public String getNameForNBT() {
		return "storage";
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return SLOTS;
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("STORAGE", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onRightClickedNotFilterSide(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, xCoord, yCoord, zCoord);
		return true;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isOpenGuiOnClicked() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block getRenderBlock() {
		return TestCore.constructStorage[grade];
	}

	@Override
	public boolean onClickedViaInterface(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, xCoord, yCoord, zCoord);
		return true;
	}

}
