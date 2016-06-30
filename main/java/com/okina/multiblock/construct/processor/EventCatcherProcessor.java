package com.okina.multiblock.construct.processor;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.utils.ColoredString;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EventCatcherProcessor extends SignalEmitterProcessor {

	public EventCatcherProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public String getNameForNBT() {
		return "eventCatcher";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**server only*/
	public void onEventReceived(int side) {
		assert !isRemote;
		emitSignal();
	}

	@Override
	public int changeIO(int side) {
		if(side < 0 || side >= 6) return 3;
		flagIO[side] = flagIO[side] == 1 ? 2 : 1;
		return flagIO[side];
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("EVENT CATCHER", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isRemote){
			//do nothing
		}else{
			onEventReceived(side);
		}
		return true;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Block getRenderBlock() {
		return TestCore.constructEventCatcher[grade];
	}

}
