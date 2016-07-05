package com.okina.multiblock.construct.mode;

import java.util.List;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.multiblock.construct.processor.EnergyProviderProcessor;
import com.okina.network.PacketType;
import com.okina.register.EnergyProdeceRecipeRegister;
import com.okina.register.EnergyProdeceRecipeRegister.EnergyProduceRecipe;
import com.okina.utils.ColoredString;
import com.okina.utils.RenderingHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyProviderMode extends ContainerModeBase {

	/**server only*/
	private EnergyProviderProcessor provider;
	private EnergyProduceRecipe processingRecipe;
	private int connectDirection = -1;
	private int processingTicks = -1;

	public EnergyProviderMode(ContainerProcessor container) {
		super(container);
	}

	@Override
	public boolean checkTileNewConnection() {
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof EnergyProviderProcessor){
				provider = (EnergyProviderProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(provider.grade == grade && (provider.container == null || !provider.container.isValid || provider.container == container)){
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
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof EnergyProviderProcessor){
			provider = (EnergyProviderProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(provider.grade == grade && (provider.container == null || !provider.container.isValid || provider.container == container)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkPartDesignatedConnection() {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof EnergyProviderProcessor){
			provider = (EnergyProviderProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(provider.grade == grade && (provider.container == null || !provider.container.isValid || provider.container == container)){
				return true;
			}
		}
		return false;
	}

	/**called by provider*/
	/**server only*/
	public void startProduceEnergy() {
		assert !isRemote;
		processingRecipe = EnergyProdeceRecipeRegister.instance.findRecipe(container.getStackInSlot(0));
		if(isTile){
			if(provider == null || !provider.isValid || processingRecipe == null) return;
			if(provider.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), processingRecipe.getEnergyProduceRate(grade), true) != processingRecipe.getEnergyProduceRate(grade)) return;
		}else{
			if(provider == null || processingRecipe == null) return;
			if(container.receiveEnergy(processingRecipe.getEnergyProduceRate(grade), false) != processingRecipe.getEnergyProduceRate(grade)) return;
		}
		if(processingTicks == -1){
			processingTicks = 0;
			container.renderStack = container.getStackInSlot(0);
			container.getStackInSlot(0).stackSize--;
			if(container.getStackInSlot(0).stackSize == 0) container.setInventorySlotContents(0, null);
			if(isTile) markForModeUpdate();
			container.markDirty();
		}
	}

	@Override
	public void progressMode() {
		//not started
		if(!isRemote){
			startProduceEnergy();
		}
		if(processingTicks == -1){
			return;
		}
		if(!isRemote){
			processingRecipe = EnergyProdeceRecipeRegister.instance.findRecipe(container.renderStack);
			//stop
			if(processingRecipe == null || provider == null){
				processingTicks = -1;
				container.renderStack = null;
				container.markDirty();
				if(isTile) markForModeUpdate();
				return;
			}
			//process
			processingTicks++;
			if(isTile){
				provider.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), processingRecipe.getEnergyProduceRate(grade), false);
			}else{
				container.receiveEnergy(processingRecipe.getEnergyProduceRate(grade), false);
			}
			if(processingTicks >= processingRecipe.getTime(grade)){
				container.renderStack = null;
				container.dispatchEventOnNextTick();
				container.markDirty();
				pc.sendPacket(PacketType.EFFECT, getModeIndex());
				processingTicks = -1;
				if(isTile) markForModeUpdate();
				processingRecipe = null;
			}
		}else{
			if(isTile){
				spawnEnergyProduceingParticle();
			}
		}
	}

	//	private boolean readyToProduceEnergy() {
	//		processingRecipe = EnergyProdeceRecipeRegister.instance.findRecipe(container.getStackInSlot(0));
	//		if(isTile){
	//			return provider != null && provider.isValid && processingRecipe != null;
	//		}else{
	//			return provider != null && processingRecipe != null;
	//		}
	//	}

	private void spawnEnergyProduceingParticle() {
		assert isRemote && isTile;
		if(container.renderStack != null){
			IIcon icon = null;
			if(Block.getBlockFromItem(container.renderStack.getItem()) != Blocks.air){
				icon = Block.getBlockFromItem(container.renderStack.getItem()).getIcon((int) (Math.random() * 6), container.renderStack.getItemDamage());
			}else{
				icon = container.renderStack.getItem().getIcon(container.renderStack, 0);
			}
			ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
			TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_CRUCK, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, (double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5, icon);
		}
	}

	private void spawnEnergyProducedParticle() {
		assert isRemote;
		if(isTile){
			for (int i = 0; i < 10; i++){
				IIcon icon = null;
				if(Block.getBlockFromItem(container.renderStack.getItem()) != Blocks.air){
					icon = Block.getBlockFromItem(container.renderStack.getItem()).getIcon((int) (Math.random() * 6), container.renderStack.getItemDamage());
				}else{
					icon = container.renderStack.getItem().getIcon(container.renderStack, 0);
				}
				ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
				TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_CRUCK, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, (double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5, icon);
			}
		}else{
			for (int i = 0; i < 3; i++){
				IIcon icon = null;
				if(Block.getBlockFromItem(container.renderStack.getItem()) != Blocks.air){
					icon = Block.getBlockFromItem(container.renderStack.getItem()).getIcon((int) (Math.random() * 6), container.renderStack.getItemDamage());
				}else{
					icon = container.renderStack.getItem().getIcon(container.renderStack, 0);
				}
				ForgeDirection dir = ForgeDirection.getOrientation(pc.toRealWorldSide(connectDirection));
				Vec3 vec1 = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));
				Vec3 vec2 = pc.toReadWorld(Vec3.createVectorHelper((double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5));
				TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_CRUCK, vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord, icon);
			}
		}
	}

	@Override
	public void reset() {
		provider = null;
		processingRecipe = null;
		connectDirection = -1;
		processingTicks = -1;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == getModeIndex()){
				spawnEnergyProducedParticle();
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return processingTicks == -1 && EnergyProdeceRecipeRegister.instance.findRecipe(itemStack) != null;
	}

	@Override
	public int getModeIndex() {
		return 3;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		connectDirection = tag.getInteger("connectDirection");
		processingTicks = tag.getInteger("processingTicks");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("connectDirection", connectDirection);
		tag.setInteger("processingTicks", processingTicks);
	}

	//render/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getModeNameForRender() {
		return new ColoredString("ENERGY", 0x800080);
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
