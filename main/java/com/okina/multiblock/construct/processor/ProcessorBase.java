package com.okina.multiblock.construct.processor;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ProcessorContainerPart;
import com.okina.network.PacketType;
import com.okina.utils.ColoredString;
import com.okina.utils.Position;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class ProcessorBase {

	public static String[] GRADE_NAME = { "WOOD", "IRON", "GOLD", "DIAMOND", "EMERALD" };
	public static final int[] ColorCode = { 0xffffff, 0xffffff, 0xffff00, 0x00ffff, 0x00ff00 };

	public final IProcessorContainer pc;
	public final boolean isRemote;
	public final boolean isTile;
	public final Random rand = new Random();

	public boolean isValid = false;
	/**
	 * 0 : input
	 * 1 : output
	 * 2 : disabled
	 */
	public final int[] flagIO = new int[6];
	public final int grade;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	/**server only*/
	private boolean eventScheduled = false;

	/**
	 * every processor mus have this constructor (IProcessorContainer pc, boolean isRemote, boolean isTile)
	 * @param pc
	 * @param isRemote
	 * @param isTile
	 */
	public ProcessorBase(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		this.pc = pc;
		this.isRemote = isRemote;
		this.isTile = isTile;
		for (int i = 0; i < 6; i++){
			flagIO[i] = 2;
		}
		this.grade = grade;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}

	/**called by its processor container after fields is set and before readFromNBT().
	 */
	public void init() {

	}

	/**
	 * If this sub class have some ProcessorBase sub class in fields<br>
	 * and you want to use them in this method,<br>
	 * make sure to check the instance is valid.
	 */
	public void updateEntity() {
		if(!isRemote){
			if(eventScheduled){
				for (int side = 0; side < 6; side++){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof EventCatcherProcessor){
						((EventCatcherProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).onEventReceived(dir.getOpposite().ordinal());
					}
				}
				eventScheduled = false;
			}
		}
	}

	public void onNeighberProcessorChanged() {

	}

	public final void dispatchEventOnNextTick() {
		if(!isRemote) eventScheduled = true;
	}

	/**drop containeditem or custom NBT item*/
	public List<ItemStack> onRemoved() {
		List<ItemStack> itemList = Lists.newArrayList();
		//		if(isValid && this instanceof IConstructInventory){
		//			IConstructInventory inv = (IConstructInventory) this;
		//			for (int i = 0; i < inv.getSizeInventory2(); ++i){
		//				ItemStack itemstack = inv.getStackInSlot2(i);
		//				if(itemstack != null && itemstack.stackSize > 0){
		//					itemList.add(itemstack);
		//				}
		//			}
		//		}
		return itemList;
	}

	/**
	 * 
	 * @param player
	 * @param side -1 : for part
	 * @param serverSide
	 * @return
	 */
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return null;
	}

	/**callled on only server at end of the tick<br>
	 * return object for update.<br>
	 * if not isTile, param type will be only NBT_CONTENT.
	 */
	public Object getPacket(PacketType type) {
		if(type == PacketType.FLAG_IO){
			String str = "";
			for (int i = 0; i < 6; i++){
				str += flagIO[i];
			}
			return str;
		}
		return null;
	}

	/**process object for update and effect ...etc
	 * @param type
	 * @param value
	 */
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.FLAG_IO && value instanceof String){//should client
			String str = (String) value;
			if(str.length() == 6){
				for (int i = 0; i < 6; i++){
					flagIO[i] = Character.getNumericValue(str.charAt(i));
				}
			}
			//			pc.markForRenderUpdate();
		}
	}
	public final Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

	public abstract String getNameForNBT();

	public void readFromNBT(NBTTagCompound tag) {
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
		}
		eventScheduled = tag.getBoolean("event");
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound[] side = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++){
			side[i] = new NBTTagCompound();
			side[i].setByte("io", (byte) flagIO[i]);
			tag.setTag("side" + i, side[i]);
		}
		tag.setBoolean("event", eventScheduled);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * for detail infomation
	 * @param mop
	 * @param renderTicks
	 * @return list for rendering
	 */
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		return Lists.newArrayList(new ColoredString("Grade : " + GRADE_NAME[grade], ColorCode[grade]));
	}

	public abstract ColoredString getNameForHUD();

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public boolean onTileShiftRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public boolean onTileRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public void onTileLeftClicked(World world, EntityPlayer player, int side, double hitX, double hitY, double hitZ) {

	}

	public boolean canStartAt(int side) {
		return false;
	}
	public boolean tryConnect(ProcessorBase tile, int clickedSide, int linkUserSide) {
		return false;
	}

	/**
	 * for building support, so only showed on tile entity
	 * @param mop
	 * @param renderTicks
	 * @return list for rendering
	 */
	public List<ColoredString> getHUDStringsForCenter(MovingObjectPosition mop, double renderTicks) {
		return Lists.newArrayList(flagIO[mop.sideHit] == 0 ? new ColoredString("Input", 0x00BFFF) : flagIO[mop.sideHit] == 1 ? new ColoredString("Output", 0xFF8C00) : null);
	}

	@SideOnly(Side.CLIENT)
	public void customRenderTile(float partialTicks) {

	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**client only*/
	public void onRandomDisplayTick() {

	}

	public boolean isOpenGuiOnClicked() {
		return false;
	}

	/**called is not open gui*/
	public boolean onPartRightClicked(World world, EntityPlayer player, int side) {
		return false;
	}

	protected final int sendEnergyFromMultiCore(int empty, boolean simulate) {
		assert !isTile;
		return ((ProcessorContainerPart) pc).sendEnergy(empty, simulate);
	}

	public final int receiveEnergy(int maxReceive, boolean simulation) {
		assert !isTile;
		return ((ProcessorContainerPart) pc).receive(maxReceive, simulation);
	}

	protected final boolean renderDetail() {
		assert !isTile;
		return ((ProcessorContainerPart) pc).renderDetail();
	}

	@SideOnly(Side.CLIENT)
	public abstract Block getRenderBlock();

	@SideOnly(Side.CLIENT)
	public int getRenderMeta() {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public void customRenderPart(double totalTicks) {

	}

}
