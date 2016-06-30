package com.okina.multiblock;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.client.IHUDUser;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.IConstructInventory;
import com.okina.multiblock.construct.ILinkConnectionUser;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockInterfaceTileEntity extends TileEntity implements ISidedInventory, ILinkConnectionUser, ISimpleTilePacketUser, IHUDUser {

	public ConnectionEntry<ProcessorContainerTileEntity> connection = null;
	private int ioMode = 2;

	public BlockInterfaceTileEntity() {
		super();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(connection != null && !connection.hasTile()){
			connection = null;
			TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
		}
		if(!worldObj.isRemote && ioMode != 2) itemTransfer();
	}

	private boolean itemTransfer() {
		assert !worldObj.isRemote;
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			int[] order = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
			for (int side : order){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(tile instanceof IInventory && connection.getTile() != tile){
					if(ioMode == 1){
						if(InventoryHelper.tryPushItemEX(this, (IInventory) tile, dir, dir.getOpposite(), IConstructInventory.maxTransfer[(connection.getProcessor()).grade])){
							return true;
						}
					}else if(ioMode == 0){
						if(InventoryHelper.tryPushItemEX((IInventory) tile, this, dir.getOpposite(), dir, IConstructInventory.maxTransfer[(connection.getProcessor()).grade])){
							return true;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(connection != null){
			if(worldObj.getTileEntity(connection.x, connection.y, connection.z) instanceof ProcessorContainerTileEntity){
				ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) worldObj.getTileEntity(connection.x, connection.y, connection.z);
				if(tile.getContainProcessor() instanceof IConstructInventory){
					((IConstructInventory) tile.getContainProcessor()).onClickedViaInterface(worldObj, player, side, hitX, hitY, hitZ);
				}
			}
			return true;
		}else{
			return false;
		}
	}
	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle("cloud");
		}else{
			//do nothing
		}
		return true;
	}
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(player.isSneaking()){
			//no dothing
		}else{
			ioMode = ioMode == 0 ? 1 : (ioMode == 1 ? 2 : 0);
			TestCore.proxy.markForTileUpdate(this.getPosition(), PacketType.FLAG_IO);
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(ioMode == 0 ? "Input Mode" : ioMode == 1 ? "Output Mode" : "IO Disabled"));
			return true;
		}
		return false;
	}

	private void spawnCennectionParticle(String name) {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(connection != null){
			for (int n = 0; n < 7; n++){
				double xOffset = (n & 1) == 1 ? -0.5 : 0.5;
				double yOffset = (n & 2) == 2 ? -0.5 : 0.5;
				double zOffset = (n & 4) == 4 ? -0.5 : 0.5;
				worldObj.spawnParticle(name, connection.x + xOffset + 0.5, connection.y + yOffset + 0.5, connection.z + zOffset + 0.5, 0.0D, 0.0D, 0.0D);

				ProcessorContainerTileEntity baseTile = (ProcessorContainerTileEntity) worldObj.getTileEntity(connection.x, connection.y, connection.z);
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = -1;
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean canStartAt(int side) {
		return true;
	}
	@Override
	public boolean tryConnect(ProcessorContainerTileEntity tile, int clickedSide, int linkUserSide) {
		if(tile.getContainProcessor() instanceof IConstructInventory){
			connection = new ConnectionEntry<ProcessorContainerTileEntity>(tile);
			if(worldObj.isRemote){
				spawnCennectionParticle("cloud");
			}
			return true;
		}
		return false;
	}

	/**use to send connection packet with spawn particle*/
	protected boolean spawnParticle = false;

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONNECTION){
			NBTTagCompound tag = new NBTTagCompound();
			if(connection != null){
				connection.writeToNBT(tag);
			}
			if(spawnParticle){
				tag.setBoolean("connectP", true);
				spawnParticle = false;
			}
			return new SimpleTilePacket(this, PacketType.NBT_CONNECTION, tag);
		}
		return null;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.NBT_CONNECTION && value instanceof NBTTagCompound){//should client
			NBTTagCompound tag = (NBTTagCompound) value;
			connection = ConnectionEntry.createFromNBT(tag, getWorldObj());
			if(tag.getBoolean("connectP")) spawnCennectionParticle("cloud");
		}
	}

	/**contents items update*/
	@Override
	public void markDirty() {
		super.markDirty();
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			((IConstructInventory) connection.getProcessor()).markDirty();
		}
	}

	@Override
	public void setWorldObj(World world) {
		if(connection != null) connection.setWorld(world);
		super.setWorldObj(world);
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

	@Override
	public int getSizeInventory() {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).getSizeInventory();
		}else{
			return 0;
		}
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).getStackInSlot(slotIndex);
		}else{
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IInventory) connection.getProcessor()).decrStackSize(slotIndex, amount);
		}else{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).getStackInSlotOnClosing(slotIndex);
		}else{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			((IConstructInventory) connection.getProcessor()).setInventorySlotContents(slotIndex, itemStack);
			return;
		}else{
			return;
		}
	}

	@Override
	public String getInventoryName() {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).getInventoryName();
		}else{
			return null;
		}
	}

	@Override
	public boolean hasCustomInventoryName() {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).hasCustomInventoryName();
		}else{
			return false;
		}
	}

	@Override
	public int getInventoryStackLimit() {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).getInventoryStackLimit();
		}else{
			return 0;
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).isUseableByPlayer(player);
		}else{
			return false;
		}
	}

	@Override
	public void openInventory() {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			((IConstructInventory) connection.getProcessor()).openInventory();
		}else{
			return;
		}
	}

	@Override
	public void closeInventory() {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			((IConstructInventory) connection.getProcessor()).closeInventory();
		}else{
			return;
		}
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).isItemValidForSlot(slotIndex, itemStack);
		}else{
			return false;
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int slotIndex) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return ((IConstructInventory) connection.getProcessor()).getAccessibleSlotsFromSide(slotIndex);
		}else{
			return new int[0];
		}
	}

	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return true;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if(connection != null && connection.getProcessor() instanceof IConstructInventory){
			return true;
		}
		return false;
	}

	@Override
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current) {
		return object == this;
	}
	@Override
	public Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}
	@Override
	public void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		String message;
		if(connection != null && !mc.theWorld.isAirBlock(connection.x, connection.y, connection.z)){
			message = "Link to " + mc.theWorld.getBlock(connection.x, connection.y, connection.z).getLocalizedName();

		}else{
			message = "No Link Established";
		}
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);
		int length = mc.fontRenderer.getStringWidth(message);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef(center.getX(), center.getY(), 0);
		GL11.glTranslatef(-length / 2, 30, 0);
		mc.fontRenderer.drawString(message, 0, 0, 0x00ff00, true);
		GL11.glTranslatef(length / 2, 0, 0);
		message = ioMode == 0 ? "Input Mode" : ioMode == 1 ? "Output Mode" : "IO Disabled";
		length = mc.fontRenderer.getStringWidth(message);
		GL11.glTranslatef(-length / 2, 10, 0);
		mc.fontRenderer.drawString(message, 0, 0, 0xfff0f5, true);
		GL11.glPopMatrix();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound entrytag = tag.getCompoundTag("entry");
		connection = ConnectionEntry.createFromNBT(entrytag, getWorldObj());
		ioMode = tag.getInteger("io");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(connection != null){
			NBTTagCompound entryTag = new NBTTagCompound();
			connection.writeToNBT(entryTag);
			tag.setTag("entry", entryTag);
		}
		tag.setInteger("io", ioMode);
	}

}
