package com.okina.multiblock.construct.processor;

import java.util.List;

import com.okina.client.gui.GuiSliderInput;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.network.PacketType;
import com.okina.server.gui.SliderInputContainer;
import com.okina.tileentity.IGuiSliderUser;
import com.okina.utils.ColoredString;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ClockPulserProcessor extends SignalEmitterProcessor implements IGuiSliderUser {

	public static final int[] minInterval = { 40, 20, 10, 5, 1 };
	public static final int[] maxInterval = { 60, 80, 100, 500, 1000 };

	//only use on server
	public int interval = 60;
	public int partialTick = 0;

	public ClockPulserProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!isRemote){
			partialTick++;
			if(partialTick == interval){
				emitSignal();
				partialTick = 0;
			}
		}else{
			//do nothimg
		}
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.SLIDER_INPUT && value instanceof Integer){//both side
			interval = (Integer) value;
			partialTick = 0;
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "clockPulser";
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new SliderInputContainer(this) : new GuiSliderInput(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		interval = tag.getInteger("interval");
		partialTick = tag.getInteger("partialTick");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("interval", interval);
		tag.setInteger("partialTick", partialTick);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int changeIO(int side) {
		if(side < 0 || side >= 6) return 3;
		flagIO[side] = flagIO[side] == 1 ? 2 : 1;
		return flagIO[side];
	}

	@Override
	public int getValue() {
		return interval;
	}
	@Override
	public String getContainerName() {
		return "Clock Pulser";
	}
	@Override
	public int getMinValue() {
		return minInterval[grade];
	}
	@Override
	public int getMaxValue() {
		return maxInterval[grade];
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("CLOCK PULSER", ColorCode[grade]);
	}

	@Override
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(mop, renderTicks);
		list.add(new ColoredString("Interval : " + interval + " Ticks", 0xb22222));
		return list;
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0, pc.world(), xCoord, yCoord, zCoord);
		return true;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Block getRenderBlock() {
		return TestCore.constructClockPulser[grade];
	}

}
