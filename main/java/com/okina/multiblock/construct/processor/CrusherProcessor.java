package com.okina.multiblock.construct.processor;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.client.gui.ConstructContainerGui;
import com.okina.inventory.InternalInventory;
import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.IConstructInventory;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.multiblock.construct.block.ConstructCrusher;
import com.okina.multiblock.construct.mode.CrusherMode;
import com.okina.network.PacketType;
import com.okina.server.gui.ConstructContainerContainer;
import com.okina.utils.ColoredString;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CrusherProcessor extends ProcessorBase implements IConstructInventory, ISignalReceiver {

	public static int[] maxRemain = { 2, 10, 20, 40, 100 };

	protected InternalInventory internalInv;
	private int direction;
	public int remain = maxRemain[grade];
	/**server only*/
	public ContainerProcessor container = null;

	public CrusherProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
		internalInv = new InternalInventory(this, 1, 1, "Crusher");
	}

	@Override
	public void init() {
		remain = maxRemain[grade];
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new ConstructContainerContainer(player.inventory, internalInv) : new ConstructContainerGui(player.inventory, internalInv);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	/**drop containeditem or custom NBT item*/
	@Override
	public List<ItemStack> onRemoved() {
		List<ItemStack> itemList = Lists.newArrayList();
		if(isValid && this instanceof IConstructInventory){
			for (int i = 0; i < getSizeInventory(); ++i){
				ItemStack itemstack = getStackInSlot(i);
				if(itemstack != null && itemstack.stackSize > 0){
					itemList.add(itemstack);
				}
			}
		}
		return itemList;
	}

	/**callled on only server*/
	@Override
	public Object getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTENT){
			NBTTagCompound invTag = new NBTTagCompound();
			internalInv.writeToNBT(invTag);
			return invTag;
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagCompound invTag = ((NBTTagCompound) value);
			internalInv.readFromNBT(invTag);
		}else if(type == PacketType.OTHER2 && value instanceof Integer){
			remain = ((Integer) value);
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "crusher";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound invTag = tag.getCompoundTag("inv");
		if(invTag != null){
			internalInv.readFromNBT(invTag);
		}else{
			internalInv.reset();
		}
		direction = tag.getInteger("dir");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound invTag = new NBTTagCompound();
		internalInv.writeToNBT(invTag);
		tag.setTag("inv", invTag);
		tag.setInteger("dir", getDirection());
	}

	//non-base-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		assert !isRemote;
		if(readyToCrush() && container != null && container.mode2 instanceof CrusherMode){
			((CrusherMode) container.mode2).startCrush();
		}
	}

	public int getDirection() {
		return isTile ? pc.world().getBlockMetadata(xCoord, yCoord, zCoord) : direction;
	}
	public boolean readyToCrush() {
		return getStackInSlot(0) != null && Block.getBlockFromItem(getStackInSlot(0).getItem()) == Blocks.cactus && remain > 0;
	}
	public void doCrash() {
		assert !isRemote;
		if(remain <= 1){
			remain = maxRemain[grade];
			setInventorySlotContents(0, null);
			markDirty();
		}else{
			remain -= 1;
		}
		//		if(isTile) TestCore.proxy.sendPacket(new SimpleTilePacket(this.getPosition(), PacketType.OTHER2, remain));
		if(isTile) pc.sendPacket(PacketType.OTHER2, remain);
		dispatchEventOnNextTick();
	}

	public void spawnCrushingParticle() {
		assert isRemote && isTile;
		Random random = rand;
		double d3 = 0.1D;
		int l1 = (int) (150.0D * d3);
		for (int i2 = 0; i2 < l1; ++i2){
			float f3 = MathHelper.randomFloatClamp(random, 0.0F, ((float) Math.PI * 2F));
			double d5 = MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
			double d6 = 0.20000000298023224D + d3 / 100.0D;
			double d7 = MathHelper.cos(f3) * 0.2F * d5 * d5 * (d3 + 0.2D);
			double d8 = MathHelper.sin(f3) * 0.2F * d5 * d5 * (d3 + 0.2D);
			String str = "blockcrack_" + Block.getIdFromBlock(Blocks.cactus) + "_0";
			pc.world().spawnParticle(str, xCoord + 0.5F, yCoord + 0.2F, zCoord + 0.5F, d7, d6, d8);
		}
	}

	@Override
	public final int getSizeInventory() {
		return internalInv.getSizeInventory();
	}
	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		return internalInv.getStackInSlot(slotIndex);
	}
	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return internalInv.getStackInSlotOnClosing(slotIndex);
	}
	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		internalInv.setInventorySlotContents(slotIndex, itemStack);
	}
	@Override
	public final ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		return internalInv.decrStackSize(slotIndex, splitStackSize);
	}
	@Override
	public final String getInventoryName() {
		return internalInv.getInventoryName();
	}
	@Override
	public final boolean hasCustomInventoryName() {
		return false;
	}
	@Override
	public final int getInventoryStackLimit() {
		return internalInv.getInventoryStackLimit();
	}
	@Override
	public final boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return itemStack == null ? false : Block.getBlockFromItem(itemStack.getItem()) == Blocks.cactus;
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 0;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 1;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0 };
	}
	@Override
	public void markDirty() {
		if(isTile){
			pc.markForUpdate(PacketType.NBT_CONTENT);
		}
	}
	@Override
	public InternalInventory getInternalInventory() {
		return internalInv;
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
		list.add(new ColoredString("Rest Usage Count : " + this.remain, 0x00FF00));
		return list;
	}

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("CRUSHER", ProcessorBase.ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isRemote){
			return !(getStackInSlot(0) == null && !isItemValidForSlot(0, player.getHeldItem()));
		}else{
			if(getStackInSlot(0) != null){
				ItemStack content = getStackInSlot(0).copy();
				world.spawnEntityInWorld(new EntityItem(world, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, content));
				decrStackSize(0, 1);
				markDirty();
				return true;
			}else{
				if(isItemValidForSlot(0, player.getHeldItem())){
					ItemStack set = player.getHeldItem().copy();
					set.stackSize = 1;
					setInventorySlotContents(0, set);
					player.getHeldItem().stackSize -= 1;
					markDirty();
					return true;
				}else{
					return false;
				}
			}
		}
	}

	@Override
	public boolean onTileRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(world.getBlock(xCoord, yCoord, zCoord) instanceof ConstructCrusher){
			if(player.isSneaking()){
				world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, side, 3);
			}else{
				flagIO[side] = flagIO[side] == 0 ? 2 : 0;
				//				TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
				pc.markForUpdate(PacketType.FLAG_IO);
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
				}
				//				if(world.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side] == 0 ? "input" : flagIO[side] == 1 ? "output" : "disabled"));
			}
		}
		return true;
	}

	@Override
	public boolean onClickedViaInterface(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, xCoord, yCoord, zCoord);
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void customRenderTile(float partialTicks) {
		if(getStackInSlot(0) != null){
			float ticks = pc.world().getTotalWorldTime() + partialTicks;
			float rotateSpeed = 3;
			float progress = 0;
			ForgeDirection dir = ForgeDirection.getOrientation(getDirection());
			if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ContainerProcessor){
				ContainerProcessor container = (ContainerProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(container.mode2 instanceof CrusherMode){
					CrusherMode crusherMode = (CrusherMode) container.mode2;
					if(crusherMode.processingTicks != -1 && (crusherMode.connectDirection == dir.ordinal() || crusherMode.connectDirection == dir.getOpposite().ordinal())){
						rotateSpeed = 12;
						progress = crusherMode.processingTicks + partialTicks;
					}
				}
			}
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			int meta = pc.world().getBlockMetadata(xCoord, yCoord, zCoord);
			if(meta == 0){
				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 1){
				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 2){
				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(270F, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 3){
				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 4){
				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 5){
				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(270F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(2F, 3F, 2F);
			}
			GL11.glTranslatef(0, (float) (0.078 * (1 - Math.cos(progress / 10 * Math.PI * 2))) - 0.055F, 0);
			EntityItem entity = new EntityItem(pc.world(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.cactus, 1));
			entity.hoverStart = 0.0F;
			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
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
		return TestCore.constructCrusher[grade];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderMeta() {
		return direction;
	}

}
