package com.okina.multiblock.construct.processor;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.okina.client.gui.ConstructContainerGui;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.multiblock.construct.mode.ContainerModeBase;
import com.okina.multiblock.construct.mode.ContainerModeBase.TransferPolicy;
import com.okina.multiblock.construct.mode.NormalMode;
import com.okina.network.PacketType;
import com.okina.register.ContainerModeRegister;
import com.okina.server.gui.ConstructContainerContainer;
import com.okina.utils.ColoredString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerProcessor extends InventoryProcessorBase implements ISignalReceiver {

	private ContainerModeBase[] modeList;
	private ContainerModeBase NORMAL_MODE;
	public ContainerModeBase mode2;
	public ItemStack renderStack = null;
	private boolean waitForTransfer = false;

	public ContainerProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade, 1, 1, "Container");
		mode2 = NORMAL_MODE = new NormalMode(this);
	}

	@Override
	public void init() {
		super.init();
		modeList = ContainerModeRegister.getModeInstances(this);
		mode2 = NORMAL_MODE = new NormalMode(this);
	}

	@Override
	public void updateEntity() {
		if(!isRemote){
			checkNeighberBlockConnection();
			if(mode2 == NORMAL_MODE){
				renderStack = null;
				markDirty();
			}
		}
		super.updateEntity();
		if(waitForTransfer){
			if(!isRemote){
				if(mode2.getTransferPolicy() == TransferPolicy.REST_ONE){
					itemTransfer(1);
					if(getStackInSlot(0) == null || getStackInSlot(0).stackSize == 1) waitForTransfer = false;
				}else{
					itemTransfer(64);
				}
				if(getStackInSlot(0) == null) waitForTransfer = false;
			}
		}
		if(!waitForTransfer){
			mode2.progressMode();
		}
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new ConstructContainerContainer(player.inventory, internalInv) : new ConstructContainerGui(player.inventory, internalInv);
	}

	@Override
	public Object getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTAINER_MODE){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("index", mode2.getModeIndex());
			mode2.writeToNBT(tag);
			return tag;
		}else if(type == PacketType.NBT_CONTENT){
			Object obj = super.getPacket(type);
			if(obj instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) obj;
				if(renderStack != null){
					NBTTagCompound itemTag = new NBTTagCompound();
					renderStack.writeToNBT(itemTag);
					tag.setTag("renderStack", itemTag);
				}
			}
			return obj;
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.NBT_CONTAINER_MODE && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			int index = tag.getInteger("index");
			if(mode2.getModeIndex() != index){
				pc.markForRenderUpdate();
			}
			mode2 = getMode(index);
			mode2.readFromNBT(tag);
			//			checkNeighberBlockConnection();
		}else if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(mode2.getModeIndex() == id){
				mode2.processCommand(type, value);
			}
		}else if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagCompound invTag = ((NBTTagCompound) value);
			NBTTagCompound itemTAg = invTag.getCompoundTag("renderStack");
			renderStack = ItemStack.loadItemStackFromNBT(itemTAg);
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "container";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList list = tag.getTagList("modeList", Constants.NBT.TAG_COMPOUND);
		if(list != null){
			for (int tagCounter = 0; tagCounter < list.tagCount(); ++tagCounter){
				NBTTagCompound modeTag = list.getCompoundTagAt(tagCounter);
				int index = modeTag.getInteger("index");
				getMode(index).readFromNBT(modeTag);
			}
		}
		mode2 = getMode(tag.getInteger("mode"));
		waitForTransfer = tag.getBoolean("waitForTransfer");
		if(renderStack != null){
			NBTTagCompound itemTag = new NBTTagCompound();
			renderStack.writeToNBT(itemTag);
			tag.setTag("renderStack", itemTag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList list = new NBTTagList();
		for (ContainerModeBase mode : modeList){
			NBTTagCompound modeTag = new NBTTagCompound();
			modeTag.setInteger("index", mode.getModeIndex());
			mode.writeToNBT(modeTag);
			list.appendTag(modeTag);
		}
		tag.setTag("modeList", list);
		tag.setInteger("mode", mode2.getModeIndex());
		tag.setBoolean("waitForTransfer", waitForTransfer);
		NBTTagCompound itemTAg = tag.getCompoundTag("renderStack");
		renderStack = ItemStack.loadItemStackFromNBT(itemTAg);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void checkNeighberBlockConnection() {
		if(!isRemote){
			if(isTile){
				ContainerModeBase original = mode2;
				//check existing connection
				if(mode2 != NORMAL_MODE && !mode2.checkTileExistingConnection()){
					mode2.reset();
					mode2 = NORMAL_MODE;
				}

				//check new connection
				//needUpdateEntry is for gamestart attachment part initialization
				if(mode2 == NORMAL_MODE && !needUpdateEntry){
					for (ContainerModeBase mode : modeList){
						if(mode.checkTileNewConnection()){
							mode2 = mode;
							break;
						}
					}
				}
				if(original != mode2){
					markForModeUpdate();
				}
			}else{
				//check disignated connection
				mode2.checkPartDesignatedConnection();
			}
		}else{
			//do nothing
		}
	}

	private ContainerModeBase getMode(int index) {
		for (ContainerModeBase mode : modeList){
			if(mode.getModeIndex() == index) return mode;
		}
		return NORMAL_MODE;
	}

	public void markForModeUpdate() {
		pc.markForUpdate(PacketType.NBT_CONTAINER_MODE);
	}

	@Override
	public void onSignalReceived() {
		assert !isRemote;
		waitForTransfer = true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return slot == 0 && itemStack != null && mode2.isItemValid(itemStack);
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0 };
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * for detail infomation
	 * @param mop
	 * @param renderTicks
	 * @return list for rendering
	 */
	@Override
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(mop, renderTicks);
		list.add(new ColoredString("Mode : " + mode2.getModeNameForRender().str, 0x008000));
		list.addAll(mode2.getHUDStringsForRight(renderTicks));
		return list;
	}

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("Container", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isRemote){
			return !(getStackInSlot(0) == null && player.getHeldItem() == null);
		}else{
			if(getStackInSlot(0) != null){
				ItemStack content = getStackInSlot(0).copy();
				content.stackSize = 1;
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				world.spawnEntityInWorld(new EntityItem(world, xCoord + dir.offsetX * 0.7, yCoord + dir.offsetY * 0.7, zCoord + dir.offsetZ * 0.7, content));
				decrStackSize(0, 1);
				markDirty();
				return true;
			}else{
				if(isItemValidForSlot(0, player.getHeldItem())){
					ItemStack set = player.getHeldItem().copy();
					set.stackSize = 1;
					setInventorySlotContents(0, set);
					player.getHeldItem().stackSize -= 1;
					return true;
				}else{
					return false;
				}
			}
		}
	}

	@Override
	public boolean onTileShiftRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return super.onTileShiftRightClicked(world, player, side, hitX, hitY, hitZ);
	}

	@Override
	public boolean onClickedViaInterface(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, xCoord, yCoord, zCoord);
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void customRenderTile(float partialTicks) {
		ItemStack tis = renderStack == null ? getStackInSlot(0) : renderStack;
		if(tis != null){
			float ticks = pc.world().getTotalWorldTime() + partialTicks;

			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glTranslatef(0.5F, 0.3F, 0.5F);
			GL11.glScalef(2F, 2F, 2F);
			GL11.glRotatef(0.1F * ticks % 360.0F, 0.0F, 1.0F, 0.0F);

			ItemStack is = tis.copy();
			is.stackSize = 1;
			EntityItem entityitem = new EntityItem(pc.world(), 0.0D, 0.0D, 0.0D, is);
			entityitem.hoverStart = 0.0F;

			RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

			if(tis.stackSize >= 2){
				if(Block.getBlockFromItem(is.getItem()) != Blocks.air){
					RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.25D, 0.0D, 0.0F, 0.0F);
				}else{
					GL11.glTranslatef(0.05F, 0.05F, 0.02F);
					RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				}
			}
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPopMatrix();
		}
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isOpenGuiOnClicked() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block getRenderBlock() {
		return TestCore.constructContainer[grade];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void customRenderPart(double totalTicks) {
		ItemStack tis = renderStack == null ? getStackInSlot(0) : renderStack;
		if(tis != null){
			GL11.glRotatef((float) (0.1F * totalTicks % 180.0F), 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0, 0.08f, 0);

			ItemStack is = tis.copy();
			is.stackSize = 1;
			EntityItem entityitem2 = new EntityItem(pc.world(), 0.0D, 0.0D, 0.0D, is);
			entityitem2.hoverStart = 0.0F;

			RenderManager.instance.renderEntityWithPosYaw(entityitem2, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

			if(tis.stackSize >= 2 && Block.getBlockFromItem(is.getItem()) != Blocks.air){
				RenderManager.instance.renderEntityWithPosYaw(entityitem2, 0.0D, 0.25D, 0.0D, 0.0F, 0.0F);
			}
			GL11.glTranslatef(0, -0.08f, 0);
			GL11.glRotatef((float) (-0.1F * totalTicks % 180.0F), 0.0F, 1.0F, 0.0F);
		}
	}

}
