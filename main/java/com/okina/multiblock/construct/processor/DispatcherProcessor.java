package com.okina.multiblock.construct.processor;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.okina.client.gui.ConstructContainerGui;
import com.okina.inventory.AbstractFilter;
import com.okina.inventory.CraftingFilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.inventory.InternalInventory;
import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.IConstructInventory;
import com.okina.multiblock.construct.IPipeConnectionUser;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.network.PacketType;
import com.okina.server.gui.ConstructContainerContainer;
import com.okina.utils.Bezier;
import com.okina.utils.ColoredString;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class DispatcherProcessor extends ProcessorBase implements IConstructInventory, IFilterUser, ISignalReceiver, IPipeConnectionUser {

	public static final int[] filterCapability = { 0, 2, 4, 5, 6 };

	protected InternalInventory internalInv;

	private ArrayList<ConnectionEntry> invConnect = new ArrayList<ConnectionEntry>();
	private ArrayList<ConnectionEntry> signalReceivers = new ArrayList<ConnectionEntry>();
	private AbstractFilter[] filter = new AbstractFilter[6];
	private int indexPointer;

	private boolean needUpdateEntry = true;

	public DispatcherProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void init() {
		internalInv = new InternalInventory(this, 1, maxTransfer[grade], "Dispatcher");
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(isRemote){
			//do nothing
		}else{
			if(needUpdateEntry){
				if(isTile){
					checkAllConnection();
				}else{
					//					updateAllConnection();
				}
				needUpdateEntry = false;
			}
			for (int i = 0; i < maxTransfer[grade]; i++){
				if(!itemTransfer()) break;
			}
		}
	}

	@Override
	public List<ItemStack> onRemoved() {
		List<ItemStack> itemList = Lists.newArrayList();
		for (int i = 0; i < 6; i++){
			if(filter[i] != null){
				itemList.add(filter[i].getFilterItem());
			}
		}
		for (int i1 = 0; i1 < getSizeInventory(); ++i1){
			if(getStackInSlot(i1) != null){
				itemList.add(getStackInSlot(i1));
			}
		}
		return itemList;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return filter[side] != null ? filter[side].getGuiElement(player, side, serverSide) : (serverSide ? new ConstructContainerContainer(player.inventory, internalInv) : new ConstructContainerGui(player.inventory, internalInv));
	}

	@Override
	public Object getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTENT){
			NBTTagCompound invTag = new NBTTagCompound();
			internalInv.writeToNBT(invTag);
			return invTag;
		}else if(type == PacketType.ALL_FILTER_UPDATE){
			NBTTagCompound tag = new NBTTagCompound();
			for (int side = 0; side < 6; side++){
				if(filter[side] == null) continue;
				NBTTagCompound filterTag = new NBTTagCompound();
				filter[side].writeToNBT(filterTag);
				tag.setTag("filter" + side, filterTag);
			}
			return tag;
		}else if(type == PacketType.NBT_CONNECTION){
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList invTagList = new NBTTagList();
			for (int i = 0; i < invConnect.size(); i++){
				ConnectionEntry<IConstructInventory> entry = invConnect.get(i);
				if(entry != null){
					NBTTagCompound invTag = new NBTTagCompound();
					entry.writeToNBT(invTag);
					invTagList.appendTag(invTag);
				}
			}
			tag.setTag("invs", invTagList);
			NBTTagList receiverTagList = new NBTTagList();
			for (int i = 0; i < signalReceivers.size(); i++){
				ConnectionEntry<ISignalReceiver> entry = signalReceivers.get(i);
				if(entry != null){
					NBTTagCompound receiverTag = new NBTTagCompound();
					entry.writeToNBT(receiverTag);
					receiverTagList.appendTag(receiverTag);
				}
			}
			tag.setTag("receivers", receiverTagList);
			return tag;
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT){
			spawnCennectionParticle();
		}else if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagCompound invTag = ((NBTTagCompound) value);
			internalInv.readFromNBT(invTag);
		}else if(type == PacketType.FILTER_NBT_FROM_GUI && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			if(tag.hasKey("side")){
				int side = tag.getInteger("side");
				if(filter[side] != null){
					filter[side].readFromNBT(tag);
				}
			}
		}else if(type == PacketType.ALL_FILTER_UPDATE && value instanceof NBTTagCompound){
			for (int side = 0; side < 6; side++){
				NBTTagCompound sideTag = ((NBTTagCompound) value).getCompoundTag("filter" + side);
				filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
			}
			//			pc.markForRenderUpdate();
		}else if(!isTile && type == PacketType.EFFECT2 && value instanceof NBTTagCompound){
			Bezier b = Bezier.createFromNBT((NBTTagCompound) value);
			int color = ((NBTTagCompound) value).getInteger("color");
			TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_BEZIER, b, color);
		}else if(type == PacketType.NBT_CONNECTION && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			invConnect.clear();
			NBTTagList invTagList = tag.getTagList("invs", Constants.NBT.TAG_COMPOUND);
			if(invTagList != null){
				for (int tagCounter = 0; tagCounter < invTagList.tagCount(); ++tagCounter){
					ConnectionEntry<IConstructInventory> entry = ConnectionEntry.createFromNBT(invTagList.getCompoundTagAt(tagCounter), pc);
					if(entry.side != -1){
						invConnect.add(entry);
					}else{
						FMLLog.severe("NBT Error", new Object[] {});
					}
				}
			}
			signalReceivers.clear();
			NBTTagList receiverTagList = tag.getTagList("receivers", Constants.NBT.TAG_COMPOUND);
			if(receiverTagList != null){
				for (int tagCounter = 0; tagCounter < receiverTagList.tagCount(); ++tagCounter){
					ConnectionEntry<ISignalReceiver> entry = ConnectionEntry.createFromNBT(receiverTagList.getCompoundTagAt(tagCounter), pc);
					signalReceivers.add(entry);
				}
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "dispatcher";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		indexPointer = tag.getInteger("indexPointer");

		NBTTagCompound invTag = tag.getCompoundTag("inv");
		if(invTag != null){
			internalInv.readFromNBT(invTag);
		}else{
			internalInv.reset();
		}

		NBTTagCompound sideTag;
		for (int side = 0; side < 6; side++){
			sideTag = tag.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
		}

		invConnect.clear();
		NBTTagList invTagList = tag.getTagList("invs", Constants.NBT.TAG_COMPOUND);
		if(invTagList != null){
			for (int tagCounter = 0; tagCounter < invTagList.tagCount(); ++tagCounter){
				ConnectionEntry<IConstructInventory> entry = ConnectionEntry.createFromNBT(invTagList.getCompoundTagAt(tagCounter), pc);
				if(entry.side != -1){
					invConnect.add(entry);
				}else{
					FMLLog.severe("NBT Error", new Object[] {});
				}
			}
		}

		signalReceivers.clear();
		NBTTagList receiverTagList = tag.getTagList("receivers", Constants.NBT.TAG_COMPOUND);
		if(receiverTagList != null){
			for (int tagCounter = 0; tagCounter < receiverTagList.tagCount(); ++tagCounter){
				ConnectionEntry<ISignalReceiver> entry = ConnectionEntry.createFromNBT(receiverTagList.getCompoundTagAt(tagCounter), pc);
				signalReceivers.add(entry);
			}
		}

		if(!isTile) needUpdateEntry = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setInteger("indexPointer", indexPointer);

		NBTTagCompound invTag1 = new NBTTagCompound();
		internalInv.writeToNBT(invTag1);
		tag.setTag("inv", invTag1);

		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			tag.setTag("filter" + side, sideTag);
		}

		NBTTagList invTagList = new NBTTagList();
		for (int i = 0; i < invConnect.size(); i++){
			ConnectionEntry<IConstructInventory> entry = invConnect.get(i);
			if(entry != null){
				NBTTagCompound invTag = new NBTTagCompound();
				entry.writeToNBT(invTag);
				invTagList.appendTag(invTag);
			}
		}
		tag.setTag("invs", invTagList);

		NBTTagList receiverTagList = new NBTTagList();
		for (int i = 0; i < signalReceivers.size(); i++){
			ConnectionEntry<ISignalReceiver> entry = signalReceivers.get(i);
			if(entry != null){
				NBTTagCompound receiverTag = new NBTTagCompound();
				entry.writeToNBT(receiverTag);
				receiverTagList.appendTag(receiverTag);
			}
		}
		tag.setTag("receivers", receiverTagList);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public int changeIO(int side) {
		if(side < 0 || side >= 6) return 3;
		flagIO[side] = flagIO[side] == 2 ? 0 : flagIO[side] + 1;
		if(!isRemote){
			if(flagIO[side] != 0 && filter[side] != null){
				ItemStack filter = removeFilter(side);
				if(filter != null){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					pc.world().spawnEntityInWorld(new EntityItem(pc.world(), xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
				}
			}
			checkAllConnection();
		}
		return flagIO[side];
	}

	private void checkAllConnection() {
		assert isTile;
		ArrayList<BlockPipeTileEntity> checkedPipes = new ArrayList<BlockPipeTileEntity>();
		invConnect.clear();
		for (int side = 0; side < 6; side++){
			if(flagIO[side] == 1){
				ArrayList<ConnectionEntry> invs = new ArrayList<ConnectionEntry>();
				invs.add(new ConnectionEntry<IConstructInventory>(this, ForgeDirection.getOrientation(side).ordinal()));
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				int newX = xCoord + dir.offsetX;
				int newY = yCoord + dir.offsetY;
				int newZ = zCoord + dir.offsetZ;
				TileEntity tile = pc.world().getTileEntity(newX, newY, newZ);
				if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
					BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
					pipe.findConnection(checkedPipes, invs, IConstructInventory.class, true);
				}else if(tile instanceof ProcessorContainerTileEntity){
					ProcessorBase processor = ((ProcessorContainerTileEntity) tile).getContainProcessor();
					if(processor instanceof IConstructInventory){
						if(processor.flagIO[dir.getOpposite().ordinal()] == 0){
							invs.add(new ConnectionEntry<IConstructInventory>((IConstructInventory) processor, ForgeDirection.getOrientation(side).getOpposite().ordinal()));
						}
					}
				}
				invs.remove(0);
				invConnect.addAll(invs);
			}
		}

		signalReceivers.clear();
		signalReceivers.add(new ConnectionEntry<ISignalReceiver>(this));
		checkedPipes.clear();
		for (int side = 0; side < 6; side++){
			if(flagIO[side] == 1){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				int newX = xCoord + dir.offsetX;
				int newY = yCoord + dir.offsetY;
				int newZ = zCoord + dir.offsetZ;
				TileEntity tile = pc.world().getTileEntity(newX, newY, newZ);
				if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
					BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
					pipe.findConnection(checkedPipes, signalReceivers, ISignalReceiver.class, false);
				}else if(tile instanceof ProcessorContainerTileEntity){
					ProcessorBase processor = ((ProcessorContainerTileEntity) tile).getContainProcessor();
					if(processor instanceof ISignalReceiver){
						if(processor.flagIO[dir.getOpposite().ordinal()] == 0){
							signalReceivers.add(new ConnectionEntry<ISignalReceiver>((ISignalReceiver) processor, ForgeDirection.getOrientation(side).getOpposite().ordinal()));
						}
					}
				}
			}
		}
		signalReceivers.remove(0);
		pc.markForUpdate(PacketType.NBT_CONNECTION);
	}

	//	private void updateAllConnection() {
	//		assert !isTile;
	//		for (int i = 0; i < invConnect.size(); i++){
	//			ConnectionEntry<IConstructInventory> entry = invConnect.get(i);
	//			if(entry != null){
	//				ProcessorBase part = pc.getProcessor(entry.x, entry.y, entry.z);
	//				if(part != null && part instanceof IConstructInventory){
	//					entry.setTile((IConstructInventory) part);
	//					continue;
	//				}
	//			}
	//			System.err.println("Multi block content has been illegaly changed");
	//			//			invConnect.remove(i);
	//		}
	//		for (int i = 0; i < signalReceivers.size(); i++){
	//			ConnectionEntry<ISignalReceiver> entry = signalReceivers.get(i);
	//			if(entry != null){
	//				ProcessorBase part = pc.getProcessor(entry.x, entry.y, entry.z);
	//				if(part != null && part instanceof ISignalReceiver){
	//					entry.setTile((ISignalReceiver) part);
	//					continue;
	//				}
	//			}
	//			System.err.println("Multi block content has been illegaly changed");
	//			//			signalReceivers.remove(i);
	//		}
	//	}

	private boolean itemTransfer() {
		assert !isRemote;
		if(invConnect.size() > 0 && getStackInSlot(0) != null){
			ItemStack targetStack = getStackInSlot(0).copy();
			targetStack.stackSize = 1;

			if(invConnect.size() - 1 < indexPointer){
				indexPointer = 0;
			}
			int startIndex = indexPointer;
			do{
				ConnectionEntry<IConstructInventory> entry = invConnect.get(indexPointer);
				if(entry != null && entry.hasTile()){
					ItemStack rest = InventoryHelper.tryPushItem(entry.getTile(), ForgeDirection.getOrientation(entry.side), targetStack, InventoryHelper.WHOLE);
					if(rest == null || rest.stackSize <= 0){
						getStackInSlot(0).stackSize--;
						if(getStackInSlot(0).stackSize == 0){
							setInventorySlotContents(0, null);
						}
						markDirty();
						indexPointer = invConnect.size() - 1 <= indexPointer ? 0 : indexPointer + 1;
						if(!isTile){
							Vec3 vec1 = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));
							Vec3 vec2 = pc.toReadWorld(Vec3.createVectorHelper(entry.x + 0.5, entry.y + 0.5, entry.z + 0.5));
							//							double x1 = coreTile.toReadWorldX(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
							//							double y1 = coreTile.toReadWorldY(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
							//							double z1 = coreTile.toReadWorldZ(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
							//							double x2 = coreTile.toReadWorldX(entry.x + 0.5, entry.y + 0.5, entry.z + 0.5);
							//							double y2 = coreTile.toReadWorldY(entry.x + 0.5, entry.y + 0.5, entry.z + 0.5);
							//							double z2 = coreTile.toReadWorldZ(entry.x + 0.5, entry.y + 0.5, entry.z + 0.5);
							int side2 = pc.toRealWorldSide(entry.side == -1 ? 6 : entry.side);
							Bezier bezier = new Bezier(vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord, 0, 0, 0, 0, 0, 0);
							sendConnectionParticlePacket(0x00ff7f, bezier);
						}
						return true;
					}
				}
				indexPointer = invConnect.size() - 1 <= indexPointer ? 0 : indexPointer + 1;
			}while (indexPointer != startIndex);
			return false;
		}
		return false;
	}

	/**client and tile only*/
	private void spawnCennectionParticle() {
		assert isRemote && isTile;
		for (ConnectionEntry<ISignalReceiver> entry : signalReceivers){
			if(pc.world().getTileEntity(entry.x, entry.y, entry.z) instanceof ProcessorContainerTileEntity){
				ProcessorContainerTileEntity baseTile = (ProcessorContainerTileEntity) pc.world().getTileEntity(entry.x, entry.y, entry.z);
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = entry.side;
			}
		}
		for (ConnectionEntry<IConstructInventory> entry : invConnect){
			if(pc.world().getTileEntity(entry.x, entry.y, entry.z) instanceof ProcessorContainerTileEntity){
				ProcessorContainerTileEntity baseTile = (ProcessorContainerTileEntity) pc.world().getTileEntity(entry.x, entry.y, entry.z);
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = entry.side;
			}
		}
	}

	protected void sendConnectionParticlePacket(int color, Bezier b) {
		assert !isRemote && !isTile;
		if(renderDetail()){
			NBTTagCompound tag = new NBTTagCompound();
			b.writeToNBT(tag);
			tag.setInteger("color", color);
			pc.sendPacket(PacketType.EFFECT2, tag);
		}
	}

	@Override
	public void onSignalReceived() {
		assert !isRemote;
		for (ConnectionEntry<ISignalReceiver> receiver : signalReceivers){
			if(receiver != null && receiver.hasTile()){
				receiver.getTile().onSignalReceived();
				if(!isTile){
					Vec3 vec1 = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));
					Vec3 vec2 = pc.toReadWorld(Vec3.createVectorHelper(receiver.x + 0.5, receiver.y + 0.5, receiver.z + 0.5));
					int side2 = pc.toRealWorldSide(receiver.side == -1 ? 6 : receiver.side);
					Bezier bezier = new Bezier(vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord, 0, 0, 0, 0, 0, 0);
					sendConnectionParticlePacket(0xb22222, bezier);
				}
			}
		}
	}

	@Override
	public IInventory getInventory() {
		return this;
	}
	@Override
	public void updateFilter() {
		if(isTile) pc.markForUpdate(PacketType.ALL_FILTER_UPDATE);
	}
	@Override
	public boolean setFilter(int side, AbstractFilter filter) {
		if(this.filter[side] != null || flagIO[side] == 1 || filter instanceof CraftingFilterInventory) return false;
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

	@Override
	public int[] getAccessibleSlotsFromSide(int slotIndex) {
		return new int[] { 0 };
	}
	@Override
	public final int getSizeInventory() {
		return internalInv.getSizeInventory();
	}
	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		return internalInv.getStackInSlot(slotIndex);
	}
	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return internalInv.getStackInSlotOnClosing(slotIndex);
	}
	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		internalInv.setInventorySlotContents(slotIndex, itemStack);
	}
	@Override
	public final ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		return internalInv.decrStackSize(slotIndex, splitStackSize);
	}
	@Override
	public final String getInventoryName() {
		return internalInv.getInventoryName();
	}
	@Override
	public final boolean hasCustomInventoryName() {
		return false;
	}
	@Override
	public final void openInventory() {}
	@Override
	public final void closeInventory() {}
	@Override
	public final int getInventoryStackLimit() {
		return internalInv.getInventoryStackLimit();
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return true;
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
		return flagIO[side] == 1;
	}
	@Override
	public void markDirty() {
		pc.markForUpdate(PacketType.NBT_CONTENT);
	}
	@Override
	public InternalInventory getInternalInventory() {
		return internalInv;
	}

	@Override
	public void needUpdateEntry() {
		needUpdateEntry = true;
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * for detail infomation
	 * @param mop
	 * @param renderTicks
	 * @return list for rendering
	 */
	@Override
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(mop, renderTicks);
		list.add(new ColoredString("Output(Invontory): " + invConnect.size(), 0xffdab9));
		list.add(new ColoredString("Output(Signal): " + this.signalReceivers.size(), 0xffdab9));
		return list;
	}

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("DISPATCHER", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(filter[side] != null){
			filter[side].onRightClicked(world, xCoord, yCoord, zCoord, side, player);
		}else{
			if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, xCoord, yCoord, zCoord);
		}
		return true;
	}

	@Override
	public boolean onTileShiftRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isRemote){
			spawnCennectionParticle();
		}
		return true;
	}

	@Override
	public boolean onTileRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		//checkConnection();
		if(!player.isSneaking()){
			if(!isRemote){
				int n = changeIO(side);
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
				}
				if(n == 1 && filter[side] != null){
					ItemStack filter = removeFilter(side);
					if(filter != null){
						world.spawnEntityInWorld(new EntityItem(world, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
					}
				}
				TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
				//				player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
			}
		}else{
			if(isRemote){
				//do nothing
			}else{
				//update all connection
				checkAllConnection();
				pc.sendPacket(PacketType.EFFECT, 0);
				player.addChatMessage(new ChatComponentText("Update All the Connections"));
			}
		}
		return true;
	}

	@Override
	public void onTileLeftClicked(World world, EntityPlayer player, int side, double hitX, double hitY, double hitZ) {
		if(!isRemote){
			if(player.isSneaking() && filter[side] != null){
				ItemStack filter = removeFilter(side);
				if(filter != null){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					world.spawnEntityInWorld(new EntityItem(world, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
				}
			}
		}
	}

	@Override
	public boolean onClickedViaInterface(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, world, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public List<ColoredString> getHUDStringsForCenter(MovingObjectPosition mop, double renderTicks) {
		return Lists.newArrayList(flagIO[mop.sideHit] == 0 ? new ColoredString("Input", 0x00BFFF) : flagIO[mop.sideHit] == 1 ? new ColoredString("Dispatch Output", 0xFF8C00) : null);

	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isOpenGuiOnClicked() {
		return true;
	}

	@Override
	public void onRandomDisplayTick() {
		super.onRandomDisplayTick();
		for (ConnectionEntry inv : invConnect){
			if(inv != null){
				Vec3 vec1 = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));
				Vec3 vec2 = pc.toReadWorld(Vec3.createVectorHelper(inv.x + 0.5, inv.y + 0.5, inv.z + 0.5));
				int side2 = pc.toRealWorldSide(inv.side == -1 ? 6 : inv.side);
				TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_BEZIER_DOTS, vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord, -1, side2, 0x00FFFF);
			}
		}
		for (ConnectionEntry receiver : signalReceivers){
			if(receiver != null){
				Vec3 vec1 = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));
				Vec3 vec2 = pc.toReadWorld(Vec3.createVectorHelper(receiver.x + 0.5, receiver.y + 0.5, receiver.z + 0.5));
				int side2 = pc.toRealWorldSide(receiver.side == -1 ? 6 : receiver.side);
				TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_BEZIER_DOTS, vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord, -1, side2, 0x00FFFF);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block getRenderBlock() {
		return TestCore.constructDispatcher[grade];
	}

}
