package com.okina.multiblock.construct.mode;

import java.util.List;

import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.multiblock.construct.processor.FurnaceProcessor;
import com.okina.network.PacketType;
import com.okina.utils.ColoredString;
import com.okina.utils.RenderingHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class FurnaceMode extends ContainerModeBase {

	public static final int[] furnaceSpeed = { 5, 10, 20, 40, 200 };

	/**server only*/
	private FurnaceProcessor furnace = null;
	private int processingTicks = -1;
	private int connectDirection = -1;

	/**client only*/

	public FurnaceMode(ContainerProcessor container) {
		super(container);
	}

	@Override
	public boolean checkTileNewConnection() {
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof FurnaceProcessor){
				furnace = (FurnaceProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(furnace.grade == grade && (furnace.container == null || !furnace.container.isValid || furnace.container == container)){
					furnace.container = container;
					connectDirection = dir.ordinal();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean checkTileExistingConnection() {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof FurnaceProcessor){
			furnace = (FurnaceProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(furnace.grade == grade && (furnace.container == null || !furnace.container.isValid || furnace.container == container)){
				furnace.container = container;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkPartDesignatedConnection() {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof FurnaceProcessor){
			furnace = (FurnaceProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(furnace.grade == grade && (furnace.container == null || !furnace.container.isValid || furnace.container == container)){
				furnace.container = container;
				return true;
			}
		}
		return false;
	}

	/**called by furnace*/
	/**server only*/
	public void startFurnace() {
		assert !isRemote;
		if(processingTicks == -1){
			if(container.getStackInSlot(0) != null && container.getStackInSlot(0).stackSize == 1){
				if(FurnaceRecipes.smelting().getSmeltingResult(container.getStackInSlot(0)) == null || furnace == null || !furnace.isValid || !furnace.readyToStartSmelt()) return;
				processingTicks = 0;
				if(isTile) markForModeUpdate();
			}
		}
	}

	@Override
	public void progressMode() {
		//not started
		if(processingTicks == -1){
			return;
		}
		if(!isRemote){
			//stop
			if(furnace == null || !furnace.isValid || container.getStackInSlot(0) == null || container.getStackInSlot(0).stackSize != 1 || FurnaceRecipes.smelting().getSmeltingResult(container.getStackInSlot(0)) == null){
				processingTicks = -1;
				if(isTile) markForModeUpdate();
				return;
			}
			//process
			if(processingTicks < FurnaceProcessor.smeltTicks){
				if(furnace.progressSmelt(furnaceSpeed[grade])){
					processingTicks += furnaceSpeed[grade];
					if(isTile) markForModeUpdate();
				}
			}else{
				if(container.getStackInSlot(0) != null){
					container.setInventorySlotContents(0, FurnaceRecipes.smelting().getSmeltingResult(container.getStackInSlot(0)).copy());
					furnace.doSmelt();
					container.dispatchEventOnNextTick();
					container.markDirty();
					pc.sendPacket(PacketType.EFFECT, getModeIndex());
				}
				processingTicks = -1;
				if(isTile) markForModeUpdate();
			}
		}
		//		else{
		//			if(isTile && spawnParticle){
		//				spawnFurnacingParticle();
		//				spawnParticle = false;
		//			}
		//		}
	}

	private void spawnFurnacingParticle() {
		assert isRemote && isTile;
		ForgeDirection dir1 = ForgeDirection.getOrientation(connectDirection);
		if(pc.getProcessor(xCoord + dir1.offsetX, yCoord + dir1.offsetY, zCoord + dir1.offsetZ) instanceof FurnaceProcessor){
			((FurnaceProcessor) pc.getProcessor(xCoord + dir1.offsetX, yCoord + dir1.offsetY, zCoord + dir1.offsetZ)).spawnFurnacingParticle(dir1.getOpposite());
		}
	}

	private void spawnFurnacedParticle() {
		assert isRemote;
		if(isTile){
			ForgeDirection dir = ForgeDirection.getOrientation((int) (Math.random() * 6));
			pc.world().spawnParticle("flame", xCoord + 0.5 + dir.offsetX * 0.2, yCoord + 0.5 + dir.offsetY * 0.2, zCoord + 0.5 + dir.offsetZ * 0.2, 0, 0, 0);
		}else{
			ForgeDirection dir = ForgeDirection.getOrientation((int) (Math.random() * 6));
			Vec3 vec = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5 + dir.offsetX * 0.2, yCoord + 0.5 + dir.offsetY * 0.2, zCoord + 0.5 + dir.offsetZ * 0.2));
			pc.world().spawnParticle("flame", vec.xCoord, vec.yCoord, vec.zCoord, 0, 0, 0);
		}
	}

	@Override
	public void reset() {
		furnace = null;
		processingTicks = -1;
		connectDirection = -1;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == getModeIndex()){
				spawnFurnacedParticle();
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		if(itemStack != null){
			return FurnaceRecipes.smelting().getSmeltingResult(itemStack) != null;
		}else{
			return false;
		}
	}

	@Override
	public int getModeIndex() {
		return 4;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		connectDirection = tag.getInteger("connectDirection");
		int past = processingTicks;
		processingTicks = tag.getInteger("processingTicks");
		if(isRemote && isTile && processingTicks != past && processingTicks != -1){
			spawnFurnacingParticle();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("connectDirection", connectDirection);
		tag.setInteger("processingTicks", processingTicks);
	}

	//render/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getModeNameForRender() {
		return new ColoredString("FURNACE", 0xa9a9a9);
	}

	@Override
	public List<ColoredString> getHUDStringsForRight(double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(renderTicks);
		if(isTile) list.add(new ColoredString("Ticks : " + (processingTicks == -1 ? "--" : processingTicks), 0x0000ff));
		return list;
	}

	@Override
	public void renderConnectionBox(int x, int y, int z, Block block, RenderBlocks renderer) {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(dir != ForgeDirection.UNKNOWN){
			renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
			float startX = dir.offsetX == -1 ? -15F / 16F : 1F / 16F;
			float startY = dir.offsetY == -1 ? -15F / 16F : 1F / 16F;
			float startZ = dir.offsetZ == -1 ? -15F / 16F : 1F / 16F;
			float sizeX = dir.offsetX != 0 ? 30F / 16F : 14F / 16F;
			float sizeY = dir.offsetY != 0 ? 30F / 16F : 14F / 16F;
			float sizeZ = dir.offsetZ != 0 ? 30F / 16F : 14F / 16F;
			RenderingHelper.renderCubeFrame(x, y, z, block, startX, startY, startZ, sizeX, sizeY, sizeZ, 1F / 16F, renderer);
		}
	}

}
