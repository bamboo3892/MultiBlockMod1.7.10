package com.okina.multiblock.construct;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.client.IHUDUser;
import com.okina.inventory.AbstractFilter;
import com.okina.inventory.IFilterUser;
import com.okina.main.GuiHandler.IGuiTile;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.BlockConstructBase;
import com.okina.multiblock.construct.processor.DummyProcessor;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.register.ProcessorRegistry;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ColoredString;
import com.okina.utils.Position;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ProcessorContainerTileEntity extends TileEntity implements IProcessorContainer, ILinkConnectionUser, ISimpleTilePacketUser, IFilterUser, IGuiTile, IHUDUser, IEnergyHandler {

	public ProcessorBase processor = new DummyProcessor();

	/**use for only rendering*/
	public boolean[] isNeighberBaseBlock = new boolean[6];
	public int restRenderTicks = 0;
	public int renderSide = -1;

	public ProcessorContainerTileEntity() {

	}

	/**
	 * called when player put block.
	 * when logging in world, readFromNBT play this roll.
	 */
	public ProcessorContainerTileEntity setProcessor(String name, int grade) {
		processor = ProcessorRegistry.getProcessorFromName(name, this, FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT, true, xCoord, yCoord, zCoord, grade);
		processor.init();
		processor.isValid = true;
		return this;
	}

	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return processor.onTileRightClicked(worldObj, player, side, hitX, hitY, hitZ);
	}

	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return processor.onTileShiftRightClicked(worldObj, player, side, hitX, hitY, hitZ);
	}

	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return processor.onTileRightClickedByWrench(worldObj, player, side, hitX, hitY, hitZ);
	}

	public void onLeftClicked(EntityPlayer player, int side, double hitX, double hitY, double hitZ) {
		restRenderTicks = 50;
		renderSide = -1;
		processor.onTileLeftClicked(worldObj, player, side, hitX, hitY, hitZ);
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return processor.getGuiElement(player, side, serverSide);
	}

	@Override
	public void updateEntity() {
		if(getContainProcessor() != null){
			getContainProcessor().updateEntity();
			if(worldObj.isRemote){
				restRenderTicks = restRenderTicks != 0 ? restRenderTicks - 1 : 0;
			}else{
				//no something
			}
		}
	}

	/**effective only on client*/
	public void updateIsNeighberBaseBlock() {
		boolean flag = false;
		boolean b = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			b = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockConstructBase;
			if(isNeighberBaseBlock[dir.ordinal()] != b) flag = true;
			isNeighberBaseBlock[dir.ordinal()] = b;
		}
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		if(flag){
			if(worldObj.isRemote){
			}else{
				TestCore.proxy.markForTileUpdate(getPosition(), PacketType.RENDER);
			}
			processor.onNeighberProcessorChanged();
		}
	}

	/**drop containeditem or custom NBT item*/
	public void onTileRemoved() {
		if(getContainProcessor() != null){
			List<ItemStack> itemList = getContainProcessor().onRemoved();
			if(itemList != null && !itemList.isEmpty()){
				Random rand = worldObj.rand;
				for (ItemStack itemstack : itemList){
					if(itemstack != null){
						float f = rand.nextFloat() * 0.8F + 0.1F;
						float f1 = rand.nextFloat() * 0.8F + 0.1F;
						EntityItem entityitem;

						for (float f2 = rand.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; worldObj.spawnEntityInWorld(entityitem)){
							int j1 = rand.nextInt(21) + 10;

							if(j1 > itemstack.stackSize){
								j1 = itemstack.stackSize;
							}

							itemstack.stackSize -= j1;
							entityitem = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
							float f3 = 0.05F;
							entityitem.motionX = (float) rand.nextGaussian() * f3;
							entityitem.motionY = (float) rand.nextGaussian() * f3 + 0.2F;
							entityitem.motionZ = (float) rand.nextGaussian() * f3;

							if(itemstack.hasTagCompound()){
								entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
							}
						}
					}
				}
			}
		}
		processor.isValid = false;
	}

	@Override
	public void invalidate() {
		processor.isValid = false;
		super.invalidate();
	}

	/**return valid processor*/
	@Override
	public ProcessorBase getContainProcessor() {
		return processor == null ? null : processor.isValid ? processor : null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean canStartAt(int side) {
		if(getContainProcessor() != null){
			return processor.canStartAt(side);
		}else{
			return false;
		}
	}

	@Override
	public boolean tryConnect(ProcessorContainerTileEntity tile, int clickedSide, int linkUserSide) {
		return processor.tryConnect(tile.getContainProcessor(), clickedSide, linkUserSide);
	}

	@Override
	public void sendPacket(PacketType type, Object value) {
		TestCore.proxy.sendPacket(new SimpleTilePacket(this, type, value));
	}
	//ISimplePacketUser
	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(getContainProcessor() != null){
			if(type == PacketType.FLAG_IO){
				return new SimpleTilePacket(this, PacketType.FLAG_IO, processor.getPacket(PacketType.FLAG_IO));
			}else if(type == PacketType.RENDER){
				return new SimpleTilePacket(this, PacketType.RENDER, 0);
			}
			return new SimpleTilePacket(this, type, processor.getPacket(type));
		}else{
			return null;
		}
	}
	@Override
	public void processCommand(PacketType type, Object value) {
		if(getContainProcessor() != null){
			if(type == PacketType.FLAG_IO && value instanceof String){//should client
				processor.processCommand(PacketType.FLAG_IO, value);
				//				worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
			}else if(type == PacketType.RENDER){
				updateIsNeighberBaseBlock();
			}
		}
		processor.processCommand(type, value);
	}
	@Override
	public final Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

	//IProcessorContainer
	@Override
	public ProcessorBase getProcessor(int x, int y, int z) {
		TileEntity tile = worldObj.getTileEntity(x, y, z);
		if(tile instanceof ProcessorContainerTileEntity){
			return ((ProcessorContainerTileEntity) tile).getContainProcessor();
		}
		return null;
	}
	@Override
	public World world() {
		return this.worldObj;
	}
	@Override
	public void markForUpdate(PacketType type) {
		TestCore.proxy.markForTileUpdate(getPosition(), type);
	}
	@Override
	public void markForRenderUpdate() {
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	@Override
	public Vec3 toReadWorld(Vec3 coord) {
		return coord;
	}
	@Override
	public int toRealWorldSide(int side) {
		return side;
	}
	@Override
	public int toInsideWorldSide(int side) {
		return side;
	}

	@Override
	public IInventory getInventory() {
		if(processor instanceof IFilterUser){
			return ((IFilterUser) processor).getInventory();
		}
		return null;
	}
	@Override
	public boolean setFilter(int side, AbstractFilter filter) {
		if(processor instanceof IFilterUser){
			return ((IFilterUser) processor).setFilter(side, filter);
		}
		return false;
	}
	@Override
	public AbstractFilter getFilter(int side) {
		if(processor instanceof IFilterUser){
			return ((IFilterUser) processor).getFilter(side);
		}
		return null;
	}
	@Override
	public ItemStack removeFilter(int side) {
		if(processor instanceof IFilterUser){
			return ((IFilterUser) processor).removeFilter(side);
		}
		return null;
	}
	@Override
	public World getWorldObject() {
		if(processor instanceof IFilterUser){
			return worldObj;
		}
		return null;
	}
	@Override
	public void updateFilter() {
		if(processor instanceof IFilterUser){
			((IFilterUser) processor).updateFilter();
		}
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		if(processor instanceof IEnergyConnection){
			return ((IEnergyConnection) processor).canConnectEnergy(from);
		}
		return false;
	}
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if(processor instanceof IEnergyHandler){
			return ((IEnergyHandler) processor).receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if(processor instanceof IEnergyHandler){
			return ((IEnergyHandler) processor).extractEnergy(from, maxExtract, simulate);
		}
		return 0;
	}
	@Override
	public int getEnergyStored(ForgeDirection from) {
		if(processor instanceof IEnergyHandler){
			return ((IEnergyHandler) processor).getEnergyStored(from);
		}
		return 0;
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if(processor instanceof IEnergyHandler){
			return ((IEnergyHandler) processor).getMaxEnergyStored(from);
		}
		return 0;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**contents items update*/
	@Override
	public void markDirty() {
		super.markDirty();
		TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONTENT);
	}

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbtTagCompound);
	}

	@Override
	public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbtTagCompound = pkt.func_148857_g();
		readFromNBT(nbtTagCompound);
	}

	public String getNameForNBT() {
		return processor.getNameForNBT();
	}

	//	public void writeDetailToNBTForItemStack(NBTTagCompound tag, boolean isCreative) {
	//		writeToNBT(tag);
	//		if(!isCreative) processor.isValid = false;
	//	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		processor = ProcessorRegistry.getProcessorFromName(tag.getString("name"), this, FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT, true, xCoord, yCoord, zCoord, tag.getInteger("grade"));
		processor.init();
		processor.readFromNBT(tag);
		processor.isValid = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("grade", processor.grade);
		tag.setString("name", processor.getNameForNBT());
		processor.writeToNBT(tag);
	}

	@Override
	public final void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);
		Point right = new Point(sr.getScaledWidth(), sr.getScaledHeight() / 2);
		if(processor != null){
			List<ColoredString> listCenter = processor.getHUDStringsForCenter(mop, renderTicks);
			if(listCenter != null && !listCenter.isEmpty()){
				int size = listCenter.size();
				for (int i = 0; i < listCenter.size(); i++){
					ColoredString str = listCenter.get(i);
					if(str != null && !str.isEmpty()){
						int length = mc.fontRenderer.getStringWidth(str.str);
						GL11.glPushMatrix();
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glTranslatef(center.getX(), center.getY(), 0);
						GL11.glTranslatef(-length / 2, 20 + i * 10, 0);
						mc.fontRenderer.drawString(str.str, 0, 0, str.color, true);
						GL11.glPopMatrix();
					}
				}
			}
			List<ColoredString> listRight = processor.getHUDStringsForRight(mop, renderTicks);
			if(listRight != null && !listRight.isEmpty()){
				int size = listRight.size();
				for (int i = 0; i < listRight.size(); i++){
					ColoredString str = listRight.get(i);
					if(str != null && !str.isEmpty()){
						int length = mc.fontRenderer.getStringWidth(str.str);
						GL11.glPushMatrix();
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glTranslatef(right.getX(), right.getY(), 0);
						GL11.glTranslatef(-length - 5, -size * 10 / 2 + i * 10, 0);
						mc.fontRenderer.drawString(str.str, 0, 0, str.color, true);
						GL11.glPopMatrix();
					}
				}
			}
		}
	}
	@Override
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current) {
		return object == this;
	}

}
