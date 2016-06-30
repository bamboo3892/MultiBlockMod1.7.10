package com.okina.multiblock.construct.mode;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.processor.AlterProcessor;
import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.network.PacketType;
import com.okina.register.AlterRecipeRegister;
import com.okina.register.AlterRecipeRegister.AlterRecipe;
import com.okina.utils.ColoredString;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;
import com.okina.utils.RenderingHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class AlterMode extends ContainerModeBase {

	/**server only*/
	List<ItemStack> history = Lists.newArrayList();
	/**server only*/
	private AlterProcessor alter = null;
	private AlterRecipe recipe = null;
	public int processingTicks = -1;
	private int connectDirection = -1;
	private int rotation = 0;

	public AlterMode(ContainerProcessor container) {
		super(container);
	}

	@Override
	public boolean checkTileNewConnection() {
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof AlterProcessor){
				alter = (AlterProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(alter.grade == grade && (alter.container == null || !alter.container.isValid || alter.container == container)){
					alter.container = container;
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
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof AlterProcessor){
			alter = (AlterProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(alter.grade == grade && (alter.container == null || !alter.container.isValid || alter.container == container)){
				alter.container = container;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkPartDesignatedConnection() {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof AlterProcessor){
			alter = (AlterProcessor) pc.getProcessor(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(alter.grade == grade && (alter.container == null || !alter.container.isValid || alter.container == container)){
				alter.container = container;
				return true;
			}
		}
		return false;
	}

	/**called by alter*/
	/**server only*/
	public void startWorship() {
		assert !isRemote;
		recipe = AlterRecipeRegister.instance.findRecipeFromMaterial(container.getStackInSlot(0));
		if(processingTicks == -1 && alter != null && alter.isValid && recipe != null && alter.readyToStartWorship(recipe.energy) && matchRecipeItem(recipe)){
			processingTicks = 0;
			if(isTile) markForModeUpdate();
		}
	}

	@Override
	public void progressMode() {
		//not started
		if(processingTicks == -1){
			return;
		}
		if(!isRemote){
			recipe = AlterRecipeRegister.instance.findRecipeFromMaterial(container.getStackInSlot(0));
			//stop
			if(recipe == null || alter == null || !alter.isValid || (processingTicks % 20 == 10 && !matchRecipeItem(recipe))){
				processingTicks = -1;
				if(isTile) markForModeUpdate();
				return;
			}
			//process
			int speed = getSpeedMultiply(recipe.getProduct());
			if(alter != null && alter.isValid && alter.progressWorship(recipe.getEnergyRate(grade), speed)){
				processingTicks += speed;
				if(isTile) markForModeUpdate();
			}
			if(processingTicks >= recipe.getTime(grade)){
				if(matchRecipeItem(recipe)){
					alter.doWorship(recipe.energy);
					container.setInventorySlotContents(0, recipe.getProduct());
					addItemToHistory(recipe.getProduct());
					for (Entry<Position, Object> entry : recipe.subItemMap.entrySet()){
						Position p = entry.getKey().turnY(rotation);
						if(pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z) instanceof ContainerProcessor){
							((ContainerProcessor) pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z)).getStackInSlot(0).stackSize--;
							if(((ContainerProcessor) pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z)).getStackInSlot(0).stackSize == 0){
								((ContainerProcessor) pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z)).setInventorySlotContents(0, null);
							}
						}
					}
					container.dispatchEventOnNextTick();
					container.markDirty();
					pc.sendPacket(PacketType.EFFECT, getModeIndex());
				}
				processingTicks = -1;
				if(isTile) markForModeUpdate();
			}
		}else{
			if(isTile){
				spawnWorshippingParticle();
			}
		}
	}

	private boolean matchRecipeItem(AlterRecipe recipe) {
		if(recipe != null){
			flag: for (int i = 0; i < (recipe.symmetryFlag == 2 ? 4 : recipe.symmetryFlag + 1); i++){
				for (Entry<Position, Object> entry : recipe.subItemMap.entrySet()){
					Position p = entry.getKey().turnY(i);
					if(pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z) instanceof ContainerProcessor && ((ContainerProcessor) pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z)).mode2 instanceof NormalMode){
						ItemStack subItem = ((ContainerProcessor) pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z)).getStackInSlot(0);
						if(InventoryHelper.isItemMaches(subItem, entry.getValue())){
							continue;
						}
					}
					continue flag;
				}
				rotation = i;
				return true;
			}
		}
		return false;
	}

	private void addItemToHistory(ItemStack itemStack) {
		if(itemStack == null) return;
		for (int i = history.size() - 1; i >= 0; i--){
			if(itemStack.isItemEqual(history.get(i))){
				List<ItemStack> newList = Lists.newArrayList();
				for (int j = i; j < history.size(); j++){
					newList.add(history.get(j));
				}
				history = newList;
				break;
			}
		}
		history.add(itemStack);
	}

	private int getSpeedMultiply(ItemStack itemStack) {
		if(itemStack == null) return 1;
		for (int i = history.size() - 1; i >= 0; i--){
			if(itemStack.isItemEqual(history.get(i))){
				return history.size() - i;
			}
		}
		return history.size() + 1;
	}

	private void spawnWorshippingParticle() {
		if(this.processingTicks == 0){
			TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_ALTER_DOT, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 6f, this);
		}
		if(pc.world().getTotalWorldTime() % 100 == 10){
			TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_ALTER, this, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, rand.nextDouble() * 0.7 + 1, (float) (rand.nextDouble() * 2 * Math.PI), (float) (rand.nextDouble() * 2 * Math.PI));
		}
	}

	private void spawnItemParticle(AlterRecipe recipe) {
		if(recipe != null){
			int index = rand.nextInt(recipe.subItemMap.entrySet().size());
			Iterator<Entry<Position, Object>> iterator = recipe.subItemMap.entrySet().iterator();
			for (int i = 0; i < index; i++){
				iterator.next();
			}
			Entry<Position, Object> entry = iterator.next();
			Position p = entry.getKey();
			if(pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z) instanceof ContainerProcessor && ((ContainerProcessor) pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z)).mode2 instanceof NormalMode){
				ItemStack subItem = ((ContainerProcessor) pc.getProcessor(xCoord + p.x, yCoord + p.y, zCoord + p.z)).getStackInSlot(0);
				if(subItem != null){
					IIcon icon = null;
					if(Block.getBlockFromItem(subItem.getItem()) != Blocks.air){
						icon = Block.getBlockFromItem(subItem.getItem()).getIcon((int) (Math.random() * 6), subItem.getItemDamage());
					}else{
						icon = subItem.getItem().getIcon(subItem, 0);
					}
					if(icon != null){
						ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
						TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_CRUCK, xCoord + p.x + 0.5, yCoord + p.y + 0.5, zCoord + p.z + 0.5, (double) xCoord + 0.5, (double) yCoord + 0.5, (double) zCoord + 0.5, icon);
					}
				}
			}
		}
	}

	private void spawnWorshipedParticle() {
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
		alter = null;
		processingTicks = -1;
		connectDirection = -1;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == getModeIndex()){
				spawnWorshipedParticle();
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return AlterRecipeRegister.instance.findRecipeFromMaterial(itemStack) != null;
	}

	@Override
	public int getModeIndex() {
		return 5;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		connectDirection = tag.getInteger("connectDirection");
		int past = processingTicks;
		processingTicks = tag.getInteger("processingTicks");
		if(isRemote && isTile && processingTicks != past && processingTicks != -1){
			recipe = AlterRecipeRegister.instance.findRecipeFromMaterial(container.getStackInSlot(0));
			if(recipe != null) spawnItemParticle(recipe);
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
		return new ColoredString("ALTER", 0x2f4f4f);
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
