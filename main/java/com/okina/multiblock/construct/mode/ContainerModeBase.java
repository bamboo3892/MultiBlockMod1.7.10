package com.okina.multiblock.construct.mode;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.network.PacketType;
import com.okina.utils.ColoredString;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class serves as Container mode function broker.<br>
 * One container instance has each mode instances;
 * @author okina
 */
public abstract class ContainerModeBase {

	public final ContainerProcessor container;
	protected final IProcessorContainer pc;
	protected final boolean isRemote;
	protected final boolean isTile;
	protected final int grade;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	protected final Random rand;

	protected ContainerModeBase(ContainerProcessor container) {
		this.container = container;
		this.pc = container.pc;
		this.isRemote = container.isRemote;
		this.isTile = container.isTile;
		this.grade = container.grade;
		this.xCoord = container.xCoord;
		this.yCoord = container.yCoord;
		this.zCoord = container.zCoord;
		this.rand = container.rand;
	}
	/**Tile only.<br>
	 * Check if the container newly connects to be this mode;
	 * @return true if it can be this mode.
	 */
	public abstract boolean checkTileNewConnection();

	/**
	 * Check if the container stay this mode;
	 * @return true if it can stay this mode.
	 */
	public abstract boolean checkTileExistingConnection();

	/**Part only<br>
	 * Check container connection from fields<br>
	 * @return true if it can be this mode.
	 */
	public abstract boolean checkPartDesignatedConnection();

	public abstract void progressMode();

	/**
	 * Reset all fields.
	 */
	public abstract void reset();

	public TransferPolicy getTransferPolicy() {
		return TransferPolicy.ALL;
	}

	/**Receive only {@link PacketType#EFFECT} packet.<br>
	 * You should use {@link ContainerModeBase#markForModeUpdate()} to send other packet.*/
	public void processCommand(PacketType type, Object value) {

	}

	public void markForModeUpdate() {
		container.markForModeUpdate();
	}

	public boolean isItemValid(ItemStack itemStack) {
		return true;
	}

	public void readFromNBT(NBTTagCompound tag) {

	}

	public void writeToNBT(NBTTagCompound tag) {

	}

	/**Identify mode*/
	public abstract int getModeIndex();

	//render/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract ColoredString getModeNameForRender();

	public List<ColoredString> getHUDStringsForRight(double renderTicks) {
		return Lists.newArrayList();
	}

	public void renderConnectionBox(int x, int y, int z, Block block, RenderBlocks renderer) {

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public enum TransferPolicy {

		ALL(),

		REST_ONE();

	}

}
