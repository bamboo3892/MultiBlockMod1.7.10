package com.okina.multiblock.construct.mode;

import java.util.List;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.multiblock.construct.processor.VirtualGrowerProcessor;
import com.okina.network.PacketType;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.okina.utils.ColoredString;
import com.okina.utils.RenderingHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class VirtualGrowerMode extends ContainerModeBase {

	private VirtualGrowerProcessor grower = null;
	private VirtualGrowerRecipe growerRecipe = null;
	private int processingTicks = -1;

	public VirtualGrowerMode(ContainerProcessor container) {
		super(container);
	}

	@Override
	public boolean checkTileNewConnection() {
		if(pc.getProcessor(xCoord, yCoord - 1, zCoord) instanceof VirtualGrowerProcessor){
			grower = (VirtualGrowerProcessor) pc.getProcessor(xCoord, yCoord - 1, zCoord);
			if(grower.grade == grade && (grower.container == null || !grower.container.isValid || grower.container == container)){
				grower.container = container;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkTileExistingConnection() {
		return checkTileNewConnection();
	}

	@Override
	public boolean checkPartDesignatedConnection() {
		return checkTileNewConnection();
	}

	/**called by grower*/
	/**server only*/
	public void startGrow() {
		assert !isRemote;
		if(processingTicks == -1){
			growerRecipe = VirtualGrowerRecipeRegister.instance.findRecipe(container.getStackInSlot(0));
			if(growerRecipe != null && growerRecipe.canProcess(grade) && grower != null && grower.isValid && grower.readyToStartGrow(growerRecipe) && container.getStackInSlot(0).stackSize == 1){
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
			growerRecipe = VirtualGrowerRecipeRegister.instance.findRecipe(container.getStackInSlot(0));
			//stop
			if(!(growerRecipe != null && growerRecipe.canProcess(grade) && grower != null && grower.isValid && container.getStackInSlot(0).stackSize == 1)){
				processingTicks = -1;
				if(isTile) markForModeUpdate();
				return;
			}
			//process
			if(grower.progressGrow(growerRecipe)){
				processingTicks++;
				if(isTile) markForModeUpdate();
			}
			if(processingTicks >= growerRecipe.getTime(grade)){
				if(container.getStackInSlot(0) != null){
					grower.doGrow();
					container.getStackInSlot(0).stackSize++;
					container.dispatchEventOnNextTick();
					container.markDirty();
					pc.sendPacket(PacketType.EFFECT, getModeIndex());
				}
				growerRecipe = null;
				processingTicks = -1;
			}
		}
		//		else{
		//			if(isTile){
		//				spawnGrowingParticle();
		//			}
		//		}
	}

	//	private boolean readyToGrow() {
	//		growerRecipe = VirtualGrowerRecipeRegister.instance.findRecipe(container.getStackInSlot(0));
	//		return growerRecipe != null && growerRecipe.canProcess(grade) && grower != null && grower.isValid && grower.readyToStartGrow(growerRecipe) && container.getStackInSlot(0).stackSize == 1;
	//	}

	private void spawnGrowingParticle() {
		assert isRemote && isTile;
		double angle1 = rand.nextDouble() * Math.PI;
		double angle2 = rand.nextDouble() * Math.PI * 2;
		double range = rand.nextDouble() + 1;
		double x = range * Math.sin(angle1) * Math.cos(angle2);
		double y = range * Math.sin(angle1) * Math.sin(angle2);
		double z = range * Math.cos(angle1);
		TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_GROWER, xCoord + 0.5, yCoord + 0.8, zCoord + 0.5, x, y, z);
	}

	private void spawnGrowedParticle() {
		assert isRemote;
		if(isTile){
			for (int i = 0; i < 4; i++){
				double angle = rand.nextDouble() * Math.PI * 2;
				double range = rand.nextDouble() * 0.4 + 0.4;
				double x = xCoord + 0.5 + range * Math.cos(angle);
				double y = yCoord + 0.5 + rand.nextDouble() * 0.1;
				double z = zCoord + 0.5 + range * Math.sin(angle);
				pc.world().spawnParticle("happyVillager", x, y, z, 0, 0, 0);
			}
			pc.world().playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, Blocks.cactus.stepSound.getBreakSound(), 1.0F, 0.8F);
		}else{
			double angle = rand.nextDouble() * Math.PI * 2;
			double range = rand.nextDouble() * 0.2 + 0.2;
			Vec3 vec = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5 + range * Math.cos(angle), yCoord + 0.5 + rand.nextDouble() * 0.1, zCoord + 0.5 + range * Math.sin(angle)));
			pc.world().spawnParticle("happyVillager", vec.xCoord, vec.yCoord, vec.zCoord, 0, 0, 0);
		}
	}

	@Override
	public void reset() {
		grower = null;
		growerRecipe = null;
		processingTicks = -1;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == getModeIndex()){
				spawnGrowedParticle();
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return VirtualGrowerRecipeRegister.instance.findRecipe(itemStack) != null;
	}

	@Override
	public TransferPolicy getTransferPolicy() {
		return TransferPolicy.REST_ONE;
	}

	@Override
	public int getModeIndex() {
		return 2;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		int past = processingTicks;
		processingTicks = tag.getInteger("processingTicks");
		if(isRemote && isTile && processingTicks != past && processingTicks != -1){
			spawnGrowingParticle();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("processingTicks", processingTicks);
	}

	//render/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getModeNameForRender() {
		return new ColoredString("GROWER", 0x8b4513);
	}

	@Override
	public List<ColoredString> getHUDStringsForRight(double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(renderTicks);
		if(isTile) list.add(new ColoredString("Ticks : " + (processingTicks == -1 ? "--" : processingTicks), 0x0000ff));
		return list;
	}

	@Override
	public void renderConnectionBox(int x, int y, int z, Block block, RenderBlocks renderer) {
		ForgeDirection dir = ForgeDirection.DOWN;
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
