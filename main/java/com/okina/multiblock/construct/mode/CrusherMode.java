package com.okina.multiblock.construct.mode;

import java.util.List;
import java.util.Random;

import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.multiblock.construct.processor.CrusherProcessor;
import com.okina.network.PacketType;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.CrusherRecipeRegister.CrusherRecipe;
import com.okina.utils.ColoredString;
import com.okina.utils.RenderingHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class CrusherMode extends ContainerModeBase {

	/**server only*/
	private CrusherProcessor crusher1 = null;
	private CrusherProcessor crusher2 = null;
	public int connectDirection = -1;
	public int processingTicks = -1;

	public CrusherMode(ContainerProcessor container) {
		super(container);
	}

	@Override
	public boolean checkTileNewConnection() {
		for (int i = 1; i < 6;){
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof CrusherProcessor){
				crusher2 = (CrusherProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(crusher2.getDirection() == dir.getOpposite().ordinal() && crusher2.grade == grade && (crusher2.container == null || crusher2.container == container)){
					dir = dir.getOpposite();
					if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof CrusherProcessor){
						crusher1 = (CrusherProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
						if(crusher1.getDirection() == dir.getOpposite().ordinal() && crusher1.grade == grade && (crusher1.container == null || crusher1.container == container)){
							connectDirection = dir.ordinal();
							crusher1.container = container;
							crusher2.container = container;
							return true;
						}
					}
				}
			}
			i += 2;
		}
		return false;
	}

	@Override
	public boolean checkTileExistingConnection() {
		if(!isConnectedToCrusher()){
			return false;
		}
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) == crusher1){
			if(crusher1.getDirection() == dir.getOpposite().ordinal() && crusher1.grade == grade && (crusher1.container == null || crusher1.container == container)){
				dir = dir.getOpposite();
				if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) == crusher2){
					if(crusher2.getDirection() == dir.getOpposite().ordinal() && crusher2.grade == grade && (crusher2.container == null || crusher2.container == container)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean checkPartDesignatedConnection() {
		if(!isTile){
			int n = 0;
		}
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof CrusherProcessor){
			crusher2 = (CrusherProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(crusher2.getDirection() == dir.getOpposite().ordinal() && crusher2.grade == grade && (crusher2.container == null || crusher2.container == container)){
				dir = dir.getOpposite();
				if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof CrusherProcessor){
					crusher1 = (CrusherProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
					if(crusher1.getDirection() == dir.getOpposite().ordinal() && crusher1.grade == grade && (crusher1.container == null || crusher1.container == container)){
						crusher1.container = container;
						crusher2.container = container;
						return true;
					}
				}
			}
		}
		return false;
	}

	/**called by crusher*/
	/**server only*/
	public void startCrush() {
		assert !isRemote;
		if(readyToCrush()){
			if(processingTicks == -1){
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
			if(!readyToCrush()){
				processingTicks = -1;
				if(isTile) markForModeUpdate();
				return;
			}
			processingTicks++;
			if(processingTicks >= 10){
				CrusherRecipe recipe = CrusherRecipeRegister.instance.findRecipeFromMaterial(container.getStackInSlot(0));
				if(recipe != null){
					crusher1.doCrash();
					crusher2.doCrash();
					container.setInventorySlotContents(0, recipe.getProduct());
					container.dispatchEventOnNextTick();
					container.markDirty();
					pc.sendPacket(PacketType.EFFECT, getModeIndex());
				}
				processingTicks = -1;
				if(isTile) markForModeUpdate();
			}
		}else{
			//process
			if(isRemote && isTile){
				ForgeDirection dir1 = ForgeDirection.getOrientation(connectDirection);
				ForgeDirection dir2 = dir1.getOpposite();
				if(pc.getProcessor(xCoord + dir1.offsetX, yCoord + dir1.offsetX, zCoord + dir1.offsetX) instanceof CrusherProcessor && pc.getProcessor(xCoord + dir2.offsetX, yCoord + dir2.offsetX, zCoord + dir2.offsetX) instanceof CrusherProcessor){
					((CrusherProcessor) pc.getProcessor(xCoord + dir1.offsetX, yCoord + dir1.offsetX, zCoord + dir1.offsetX)).spawnCrushingParticle();
					((CrusherProcessor) pc.getProcessor(xCoord + dir2.offsetX, yCoord + dir2.offsetX, zCoord + dir2.offsetX)).spawnCrushingParticle();
				}
			}
			processingTicks++;
		}
	}

	@Override
	public void reset() {
		crusher1 = null;
		crusher2 = null;
		connectDirection = -1;
		processingTicks = -1;
	}

	private boolean isConnectedToCrusher() {
		return connectDirection != -1 && crusher1 != null && crusher2 != null && crusher1.isValid && crusher2.isValid;
	}

	private boolean readyToCrush() {
		return isConnectedToCrusher() && CrusherRecipeRegister.instance.findRecipeFromMaterial(container.getStackInSlot(0)) != null && crusher1.readyToCrush() && crusher2.readyToCrush();
	}

	public void spawnCrushedParticle() {
		assert isRemote;
		if(isTile){
			String str = "";
			if(Block.getBlockFromItem(container.getStackInSlot(0).getItem()) != Blocks.air){
				str = "blockcrack_" + Block.getIdFromBlock(Block.getBlockFromItem(container.getStackInSlot(0).getItem())) + "_" + container.getStackInSlot(0).getItemDamage();
			}else{
				str = "iconcrack_" + Item.getIdFromItem(container.getStackInSlot(0).getItem()) + "_" + container.getStackInSlot(0).getItemDamage();
			}
			Random random = rand;
			double scale = 0.5D;
			int particleCount = (int) (150.0D * scale);
			for (int i2 = 0; i2 < particleCount; ++i2){
				float direction = MathHelper.randomFloatClamp(random, 0.0F, ((float) Math.PI * 2F));
				double speed = MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
				double vecY = 0.20000000298023224D + scale / 100.0D;
				double vecX = MathHelper.cos(direction) * 0.2F * speed * speed * (scale + 0.2D);
				double vecZ = MathHelper.sin(direction) * 0.2F * speed * speed * (scale + 0.2D);
				pc.world().spawnParticle(str, xCoord + 0.5F, yCoord + 0.2F, zCoord + 0.5F, vecX, vecY, vecZ);
			}
			pc.world().playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, Blocks.cactus.stepSound.getBreakSound(), 1.0F, 0.8F);
		}else{
			String str = "";
			if(Block.getBlockFromItem(container.getStackInSlot(0).getItem()) != Blocks.air){
				str = "blockcrack_" + Block.getIdFromBlock(Block.getBlockFromItem(container.getStackInSlot(0).getItem())) + "_" + container.getStackInSlot(0).getItemDamage();
			}else{
				str = "iconcrack_" + Item.getIdFromItem(container.getStackInSlot(0).getItem()) + "_" + container.getStackInSlot(0).getItemDamage();
			}
			double scale = 0.01D;
			int particleCount = (int) (150.0D * scale);
			for (int i2 = 0; i2 < particleCount; ++i2){
				float direction = MathHelper.randomFloatClamp(rand, 0.0F, ((float) Math.PI * 2F));
				double speed = MathHelper.randomFloatClamp(rand, 0.75F, 1.0F);
				double vecY = 0.20000000298023224D + scale / 100.0D;
				double vecX = MathHelper.cos(direction) * 0.2F * speed * speed * (scale + 0.2D);
				double vecZ = MathHelper.sin(direction) * 0.2F * speed * speed * (scale + 0.2D);
				Vec3 vec = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.2, zCoord + 0.5));
				pc.world().spawnParticle(str, vec.xCoord, vec.yCoord, vec.zCoord, vecX, vecY, vecZ);
			}
		}
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == getModeIndex()){
				spawnCrushedParticle();
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return CrusherRecipeRegister.instance.findRecipeFromMaterial(itemStack) != null;
	}

	@Override
	public int getModeIndex() {
		return 1;
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
		return new ColoredString("CRUSHER", 0x00ff00);
	}

	@Override
	public List<ColoredString> getHUDStringsForRight(double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(renderTicks);
		if(isTile) list.add(new ColoredString("Ticks : " + (processingTicks == -1 ? "--" : processingTicks), 0x0000ff));
		return list;
	}

	@Override
	public void renderConnectionBox(int x, int y, int z, Block block, RenderBlocks renderer) {
		if(connectDirection != -1){
			renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
			if(connectDirection == 0){
				RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, -15F / 16F, 1F / 16F, 14F / 16F, 46F / 16F, 14F / 16F, 1F / 16F, renderer);
			}else if(connectDirection == 2){
				RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, 1F / 16F, -15F / 16F, 14F / 16F, 14F / 16F, 46F / 16F, 1F / 16F, renderer);
			}else if(connectDirection == 4){
				RenderingHelper.renderCubeFrame(x, y, z, block, -15F / 16F, 1F / 16F, 1F / 16F, 46F / 16F, 14F / 16F, 14F / 16F, 1F / 16F, renderer);
			}
		}
	}

}



