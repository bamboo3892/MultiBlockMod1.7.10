package com.okina.multiblock.construct;

import org.lwjgl.opengl.GL11;

import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.network.PacketType;
import com.okina.register.ProcessorRegistry;
import com.okina.utils.ColoredString;
import com.okina.utils.Position;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ProcessorContainerPart implements IProcessorContainer {

	public static final String[] ColorCode = { "ffffff", "ffffff", "ffff00", "00ffff", "00ff00" };

	private final MultiBlockCoreTileEntity coreTile;
	private boolean isRemote;

	private ProcessorBase processor;
	public int xCoord;
	public int yCoord;
	public int zCoord;

	public ProcessorContainerPart(MultiBlockCoreTileEntity coreTile, boolean isRemote) {
		this.coreTile = coreTile;
		this.isRemote = isRemote;
	}

	//	public ProcessorContainerPart initProcessor(String name, int grade) {
	//		this.processor = ProcessorRegistry.getProcessorFromName(name, this, FMLCommonHandler.instance().getSide() == Side.CLIENT, false);
	//		processor.grade = grade;
	//		processor.xCoord = xCoord;
	//		processor.yCoord = yCoord;
	//		processor.zCoord = zCoord;
	//		processor.isValid = true;
	//		processor.init();
	//		return this;
	//	}

	public boolean onRightClicked(World world, EntityPlayer player, int side) {
		return processor.onPartRightClicked(world, player, side);
	}

	/**server only*/
	public void updatePart() {
		processor.updateEntity();
	}

	public int sendEnergy(int empty, boolean simulate) {
		return coreTile.extractEnergy(ForgeDirection.UNKNOWN, empty, simulate);
	}

	public int receive(int maxReceive, boolean simulation) {
		return coreTile.receiveEnergy(ForgeDirection.UNKNOWN, maxReceive, simulation);
	}

	/**client only*/
	public void onRandomDisplayTick() {
		processor.onRandomDisplayTick();
	}

	public boolean renderDetail() {
		return coreTile.shouldRenderDetail();
	}

	public boolean isOpenGuiOnClicked() {
		return processor.isOpenGuiOnClicked();
	}

	public Object getGuiElement(EntityPlayer player, boolean serverSide) {
		return processor.getGuiElement(player, -1, serverSide);
	}

	@Override
	public void sendPacket(PacketType type, Object value) {
		coreTile.sendPartPacket(new Position(xCoord, yCoord, zCoord), type, value);
	}

	public NBTTagCompound getContentUpdateTag() {
		return (NBTTagCompound) processor.getPacket(PacketType.NBT_CONTENT);
	}

	public void processCommand(PacketType type, Object value) {
		processor.processCommand(type, value);
	}

	public String getNameForNBT() {
		return processor.getNameForNBT();
	}

	@Override
	public ProcessorBase getContainProcessor() {
		return processor;
	}

	@Override
	public ProcessorBase getProcessor(int x, int y, int z) {
		return coreTile.getProcessor(x, y, z, false) != null ? coreTile.getProcessor(x, y, z, false) : null;
	}

	@Override
	public World world() {
		return coreTile.getWorldObj();
	}

	@Override
	public void markForUpdate(PacketType type) {
		if(type == null || type == PacketType.NBT_CONTENT){
			coreTile.markForContentUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void markForRenderUpdate() {}

	@Override
	public Vec3 toReadWorld(Vec3 coord) {
		double x = coreTile.toReadWorldX(coord.xCoord, coord.yCoord, coord.zCoord);
		double y = coreTile.toReadWorldY(coord.xCoord, coord.yCoord, coord.zCoord);
		double z = coreTile.toReadWorldZ(coord.xCoord, coord.yCoord, coord.zCoord);
		return Vec3.createVectorHelper(x, y, z);
	}

	@Override
	public int toRealWorldSide(int side) {
		return coreTile.toRealWorldSide(side);
	}

	@Override
	public int toInsideWorldSide(int side) {
		return coreTile.toInsideWorldSide(side);
	}

	public void readFromNBT(NBTTagCompound tag) {
		xCoord = tag.getInteger("x");
		yCoord = tag.getInteger("y");
		zCoord = tag.getInteger("z");
		processor = ProcessorRegistry.getProcessorFromName(tag.getString("name"), this, isRemote, false, xCoord, yCoord, zCoord, tag.getInteger("grade"));
		processor.init();
		processor.readFromNBT(tag);
		processor.isValid = true;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", xCoord);
		tag.setInteger("y", yCoord);
		tag.setInteger("z", zCoord);
		tag.setInteger("grade", processor.grade);
		tag.setString("name", processor.getNameForNBT());
		processor.writeToNBT(tag);
	}

	public ColoredString getNameForHUD() {
		return processor.getNameForHUD();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final float Amplitude = 0.1f;
	private float rand1 = (float) (Math.random() * Math.PI);
	private float rand2 = (float) (Math.random() * 30f) - 15f;
	private float rand3 = (float) (Math.random() * 30f) - 15f;

	@SideOnly(Side.CLIENT)
	public final void renderPart(double totalTicks) {
		if(RenderBlocks.getInstance().blockAccess == null) RenderBlocks.getInstance().blockAccess = coreTile.getWorldObj();
		float yOffset = (float) Math.sin(rand1 + (totalTicks / 40d) % (2d * Math.PI)) * Amplitude - 0.25f;
		GL11.glTranslatef(0, yOffset, 0);
		GL11.glRotatef(rand2, 0, 1, 0);
		GL11.glRotatef(rand3, 1, 0, 0);
		GL11.glScalef(2, 2, 2);
		EntityItem entityitem = null;
		entityitem = new EntityItem(coreTile.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(processor.getRenderBlock(), 1, processor.getRenderMeta()));
		entityitem.hoverStart = 0.0F;
		RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
		GL11.glScalef(0.5f, 0.5f, 0.5f);

		processor.customRenderPart(totalTicks);

		GL11.glRotatef(-rand3, 1, 0, 0);
		GL11.glRotatef(-rand2, 0, 1, 0);
		GL11.glTranslatef(0, -yOffset, 0);
	}

}
