package com.okina.multiblock.construct.processor;

import java.util.List;

import com.okina.client.gui.GuiSliderInput;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.network.PacketType;
import com.okina.server.gui.SliderInputContainer;
import com.okina.tileentity.IGuiSliderUser;
import com.okina.utils.ColoredString;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class RepeaterProcessor extends SignalEmitterProcessor implements IGuiSliderUser, ISignalReceiver {

	/**use only on server*/
	public boolean activate;
	public int delay = 20;
	public int partialTick = 0;

	public RepeaterProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!isRemote){
			if(activate) partialTick++;
			if(partialTick >= delay){
				emitSignal();
				activate = false;
				partialTick = 0;
			}
		}else{
			//do nothing
		}
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.SLIDER_INPUT && value instanceof Integer){//both side
			delay = (Integer) value;
			partialTick = 0;
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "repeater";
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new SliderInputContainer(this) : new GuiSliderInput(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		delay = tag.getInteger("delay");
		partialTick = tag.getInteger("partialTick");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("delay", delay);
		tag.setInteger("partialTick", partialTick);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		assert !isRemote;
		activate = true;
		partialTick = 0;
	}

	@Override
	public int getValue() {
		return delay;
	}
	@Override
	public String getContainerName() {
		return "Repeater";
	}
	@Override
	public int getMinValue() {
		return 1;
	}
	@Override
	public int getMaxValue() {
		return 100;
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("REPEATER", ColorCode[grade]);
	}

	@Override
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(mop, renderTicks);
		list.add(new ColoredString("Delay : " + delay + " Ticks", 0xb22222));
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
		return TestCore.constructRepeater[grade];
	}

}
