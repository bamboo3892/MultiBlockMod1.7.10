package com.okina.multiblock.construct.processor;

import java.util.List;

import com.okina.inventory.AbstractFilter;
import com.okina.inventory.IFilterUser;
import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.IConstructInventory;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.network.PacketType;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class FilterUserProcessor extends InventoryProcessorBase implements IFilterUser {

	public static final int[] filterCapability = { 0, 2, 4, 5, 6 };

	private AbstractFilter[] filter = new AbstractFilter[6];

	public FilterUserProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade, int stackSize, int stackLimit, String invName) {
		super(pc, isRemote, isTile, x, y, z, grade, stackSize, stackLimit, invName);
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		if(isTile){
			return filter[side] != null ? filter[side].getGuiElement(player, side, serverSide) : null;
		}else{
			return null;
		}
	}

	@Override
	public List<ItemStack> onRemoved() {
		List<ItemStack> itemList = super.onRemoved();
		for (int i = 0; i < 6; i++){
			if(filter[i] != null){
				itemList.add(filter[i].getFilterItem());
			}
		}
		return itemList;
	}

	@Override
	public Object getPacket(PacketType type) {
		if(type == PacketType.ALL_FILTER_UPDATE){
			NBTTagCompound tag = new NBTTagCompound();
			for (int side = 0; side < 6; side++){
				if(filter[side] == null) continue;
				NBTTagCompound filterTag = new NBTTagCompound();
				filter[side].writeToNBT(filterTag);
				tag.setTag("filter" + side, filterTag);
			}
			return tag;
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.FILTER_NBT_FROM_GUI && value instanceof NBTTagCompound){//from client gui
			NBTTagCompound tag = (NBTTagCompound) value;
			if(tag.hasKey("side")){
				int side = tag.getInteger("side");
				if(getFilter(side) != null){
					filter[side].processNBTPacketFromGui(tag);
				}
			}
		}else if(type == PacketType.ALL_FILTER_UPDATE && value instanceof NBTTagCompound){
			for (int side = 0; side < 6; side++){
				NBTTagCompound sideTag = ((NBTTagCompound) value).getCompoundTag("filter" + side);
				filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
			}
			//			pc.markForRenderUpdate();
		}
		super.processCommand(type, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound sideTag;
		for (int side = 0; side < 6; side++){
			sideTag = tag.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			tag.setTag("filter" + side, sideTag);
		}
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean onRightClickedNotFilterSide(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	protected boolean itemTransfer(int maxTransfer) {
		assert !isRemote;
		int[] randomOrder = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
		int[] priority = new int[6];
		for (int side = 0; side < 6; side++){
			if(connection[side] != null && flagIO[side] == 1){
				if(filter[side] != null){
					priority[side] = filter[side].getPriority();
				}else{
					priority[side] = 0;
				}
			}else{
				priority[side] = 0;
			}
		}
		int[] order = new int[6];
		for (int i = 0; i < 6; i++){
			order[i] = -1;
		}
		for (int i = AbstractFilter.MAX_PRIORITY; i >= 0; i--){
			for (int side : randomOrder){
				if(priority[side] == i){
					int index = 0;
					while (order[index] != -1)
						index++;
					order[index] = side;
				}
			}
		}
		for (int side : order){
			if(connection[side] != null && connection[side].getTile() != null && flagIO[side] == 1){
				if(filter[side] == null){
					if(InventoryHelper.tryPushItemEX(this, connection[side].getTile(), ForgeDirection.getOrientation(side), ForgeDirection.getOrientation(connection[side].side), maxTransfer)){
						if(!isTile) sendConnectionParticlePacket(side, 0x00ff7f);
						return true;
					}
				}else{
					if(filter[side].tranferItem(this, connection[side].getTile(), side, connection[side].side, maxTransfer)){
						if(!isTile) sendConnectionParticlePacket(side, 0x00ff7f);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		if(flagIO[side] != 0){
			return false;
		}else{
			if(filter[side] != null){
				if(filter[side].canAutoTransferItem(itemStack, true)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
	}
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if(flagIO[side] != 1){
			return false;
		}else{
			if(filter[side] != null){
				if(filter[side].canAutoTransferItem(itemStack, false)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
	}

	@Override
	public IInventory getInventory() {
		return this;
	}
	@Override
	public void updateFilter() {
		if(isTile) TestCore.proxy.markForTileUpdate(getPosition(), PacketType.ALL_FILTER_UPDATE);
	}
	@Override
	public boolean setFilter(int side, AbstractFilter filter) {
		if(this.filter[side] != null) return false;
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(this.filter[i] != null) n++;
		}
		if(n >= filterCapability[grade]) return false;
		this.filter[side] = filter;
		updateFilter();
		return true;
	}
	@Override
	public AbstractFilter getFilter(int side) {
		if(side >= 0 && side < 6){
			return filter[side];
		}
		return null;
	}
	@Override
	public ItemStack removeFilter(int side) {
		if(filter[side] == null){
			return null;
		}else{
			ItemStack itemStack = filter[side].getFilterItem();
			filter[side] = null;
			updateFilter();
			return itemStack;
		}
	}
	@Override
	public World getWorldObject() {
		return pc.world();
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(filter[side] != null){
			return filter[side].onRightClicked(world, xCoord, yCoord, zCoord, side, player);
		}else{
			return onRightClickedNotFilterSide(world, player, side, hitX, hitY, hitZ);
		}
	}

	@Override
	public boolean onTileRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(!player.isSneaking()){
			int n = changeIO(side);
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
			}
			TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
			//			if(isRemote) player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
		}else{
			if(isRemote){
				//do nothing
			}else{
				if(flagIO[side] == 1){
					if(connectNextBlock(side)){
						ConnectionEntry<IConstructInventory> entry = connection[side];
						if(!(entry == null)){
							player.addChatMessage(new ChatComponentText(connection[side].toString()));
						}else{
							player.addChatMessage(new ChatComponentText("No Connection Found"));
						}
					}else{
						player.addChatMessage(new ChatComponentText("No Connection Found"));
					}
					spawnParticle = true;
					pSide = side;
					TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
				}
			}
		}
		return true;
	}

	@Override
	public void onTileLeftClicked(World world, EntityPlayer player, int side, double hitX, double hitY, double hitZ) {
		if(player.isSneaking() && filter[side] != null){
			ItemStack filter = removeFilter(side);
			if(filter != null && !isRemote){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				world.spawnEntityInWorld(new EntityItem(world, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
			}
		}
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
