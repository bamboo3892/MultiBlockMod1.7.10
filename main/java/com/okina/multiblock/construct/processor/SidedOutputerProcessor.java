package com.okina.multiblock.construct.processor;

import java.util.ArrayList;
import java.util.List;

import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.IPipeConnectionUser;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.network.PacketType;
import com.okina.utils.Bezier;
import com.okina.utils.ColoredString;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.Position;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class SidedOutputerProcessor<Target> extends ProcessorBase implements IPipeConnectionUser {

	/**server and tile only*/
	@SuppressWarnings("rawtypes")
	private ArrayList<ConnectionEntry>[] connections = new ArrayList[6];
	/**server and tile only*/
	private int[] index = new int[6];

	protected ConnectionEntry<Target>[] connection = new ConnectionEntry[6];
	protected boolean needUpdateEntry = true;

	/**use to send connection packet with spawn particle*/
	protected boolean spawnParticle = false;
	protected int pSide = -1;

	@SuppressWarnings("rawtypes")
	public SidedOutputerProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
		for (int i = 0; i < 6; i++){
			connection[i] = null;
			if(isTile){
				connections[i] = new ArrayList<ConnectionEntry>();
				index[i] = 0;
			}
		}
		if(!isTile){
			connections = null;
			index = null;
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(isRemote){
			if(needUpdateEntry){
				//				updateEntry();
				needUpdateEntry = false;
			}
		}else{
			if(needUpdateEntry){
				//				updateEntry();
				if(isTile) checkConnectionToEntry();
				needUpdateEntry = false;
			}
		}
	}

	@Override
	public Object getPacket(PacketType type) {
		if(type == PacketType.NBT_CONNECTION){
			NBTTagCompound tag = new NBTTagCompound();
			for (int i = 0; i < 6; i++){
				if(connection[i] != null){
					NBTTagCompound sideTag = new NBTTagCompound();
					connection[i].writeToNBT(sideTag);
					tag.setTag("side" + i, sideTag);
				}
			}
			if(spawnParticle){
				tag.setBoolean("connectP", true);
				tag.setInteger("pSide", pSide);
				spawnParticle = false;
				pSide = -1;
			}
			return tag;
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.NBT_CONNECTION && value instanceof NBTTagCompound){//should client
			if(isTile){
				NBTTagCompound tag = (NBTTagCompound) value;
				for (int i = 0; i < 6; i++){
					NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
					connection[i] = ConnectionEntry.createFromNBT(sideTag, pc);
				}
				needUpdateEntry = true;
				if(tag.getBoolean("connectP")) spawnCennectionParticle(tag.getInteger("pSide"), "cloud");
			}else{
				NBTTagCompound tag = (NBTTagCompound) value;
				int side = tag.getByte("side");
				int color = tag.getInteger("color");
				if(connection[side] != null){
					Vec3 vec1 = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));
					Vec3 vec2 = pc.toReadWorld(Vec3.createVectorHelper(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5));
					int side1 = pc.toRealWorldSide(side);
					int side2 = pc.toRealWorldSide(connection[side].side == -1 ? 6 : connection[side].side);
					ForgeDirection startDir = ForgeDirection.getOrientation(side1);
					ForgeDirection endDir = ForgeDirection.getOrientation(side2);
					Bezier bezier = new Bezier(vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord, startDir.offsetX * 2, startDir.offsetY * 2, startDir.offsetZ * 2, -endDir.offsetX * 2, -endDir.offsetY * 2, -endDir.offsetZ * 2);
					TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_BEZIER, bezier, color);
				}
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
			if(isTile) index[i] = sideTag.getInteger("index");
			connection[i] = ConnectionEntry.createFromNBT(sideTag, pc);
		}
		needUpdateEntry = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		for (int i = 0; i < 6; i++){
			NBTTagCompound side = new NBTTagCompound();
			side.setByte("io", (byte) flagIO[i]);
			if(isTile) side.setInteger("index", index[i]);
			if(connection[i] != null){
				connection[i].writeToNBT(side);
			}
			tag.setTag("side" + i, side);
		}
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**Does not delete connection entry*/
	//	protected void updateEntry() {
	//		for (ConnectionEntry<Target> entry : connection){
	//			if(entry != null){
	//				ProcessorBase pb = pc.getProcessor(entry.x, entry.y, entry.z);
	//				if(pb != null && getTargetClass().isAssignableFrom(pb.getClass())){
	//					entry.setTile((Target) getTargetClass().cast(pb));
	//				}else if(isTile && !isRemote){
	//					entry = null;
	//				}
	//			}
	//		}
	//		if(isTile) TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
	//	}

	@SuppressWarnings("rawtypes")
	protected abstract Class getTargetClass();

	protected abstract boolean shouldDistinguishSide();

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**tile only*/
	protected int changeIO(int side) {
		assert isTile;
		if(side < 0 || side >= 6) return 2;
		flagIO[side] = flagIO[side] == 2 ? 0 : flagIO[side] + 1;
		return flagIO[side];
	}

	/**server and tile only*/
	protected boolean connectNextBlock(int side) {
		assert !isRemote && isTile;
		checkConnectionToEntryForSide(side);
		if(connections[side].size() <= 0){
			index[side] = 0;
			connection[side] = null;
			return false;
		}else{
			index[side] = (index[side] >= connections[side].size() - 1 ? 0 : index[side] + 1);
			connection[side] = connections[side].get(index[side]);
			return true;
		}
	}

	/**cilent and tile only*/
	protected void spawnCennectionParticle(int side, String name) {
		assert isRemote && isTile;
		if(side < 0 || side > 5) return;
		if(isTile){
			if(connection[side] != null){
				ConnectionEntry<Target> entry = connection[side];

				if(pc.world().getTileEntity(entry.x, entry.y, entry.z) instanceof ProcessorContainerTileEntity){
					ProcessorContainerTileEntity baseTile = (ProcessorContainerTileEntity) pc.world().getTileEntity(entry.x, entry.y, entry.z);
					baseTile.restRenderTicks = 100;
					baseTile.renderSide = entry.side;
				}

				if(shouldDistinguishSide()){
					ForgeDirection dir = ForgeDirection.getOrientation(entry.side);
					for (int i = 0; i < 5; i++)
						pc.world().spawnParticle(name, entry.x + dir.offsetX * 0.6 + 0.5, entry.y + dir.offsetY * 0.6 + 0.5, entry.z + dir.offsetZ * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
				}else{
					for (int i = 0; i < 8; i++){
						double offsetX = (i & 4) == 4 ? 0.4 : -0.4;
						double offsetY = (i & 2) == 2 ? 0.4 : -0.4;
						double offsetZ = (i & 1) == 1 ? 0.4 : -0.4;
						pc.world().spawnParticle(name, entry.x + offsetX + 0.5, entry.y + offsetY + 0.5, entry.z + offsetZ + 0.5, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}

	/**tile only*/
	@Override
	public final void needUpdateEntry() {
		assert isTile;
		needUpdateEntry = true;
	}

	/**server and tile only*/
	private void checkConnectionToEntry() {
		assert !isRemote && isTile;
		for (int side = 0; side < 6; side++){
			checkConnectionToEntryForSide(side);
		}
	}

	/**server and tile only*/
	@SuppressWarnings("rawtypes")
	public void checkConnectionToEntryForSide(int side) {
		assert !isRemote && isTile;
		if(side < 0 || side > 5) return;
		connections[side].clear();
		if(shouldDistinguishSide()){
			connections[side].add(new ConnectionEntry(this, ForgeDirection.getOrientation(side).ordinal()));
		}else{
			connections[side].add(new ConnectionEntry(this));
		}
		ArrayList<BlockPipeTileEntity> ppppppppppp = new ArrayList<BlockPipeTileEntity>();
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int newX = xCoord + dir.offsetX;
		int newY = yCoord + dir.offsetY;
		int newZ = zCoord + dir.offsetZ;
		TileEntity tile = pc.world().getTileEntity(newX, newY, newZ);
		if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
			BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
			pipe.findConnection(ppppppppppp, connections[side], getTargetClass(), shouldDistinguishSide());
		}else if(tile instanceof ProcessorContainerTileEntity){
			ProcessorBase processor = ((ProcessorContainerTileEntity) tile).getContainProcessor();
			if(processor != null && getTargetClass().isAssignableFrom(processor.getClass())){
				if(processor.flagIO[dir.getOpposite().ordinal()] == 0){
					if(shouldDistinguishSide()){
						connections[side].add(new ConnectionEntry<Target>((Target) getTargetClass().cast(processor), ForgeDirection.getOrientation(side).getOpposite().ordinal()));
					}else{
						connections[side].add(new ConnectionEntry<Target>((Target) getTargetClass().cast(processor)));
					}
				}
			}
		}
		connections[side].remove(0);

		//find past connection
		if(connection[side] != null){
			for (int i = 0; i < connections[side].size(); i++){
				ConnectionEntry<Target> entry = connections[side].get(i);
				if(entry == null){
					continue;
				}else if(connection[side].equals(entry)){
					index[side] = i;
					return;
				}
			}
		}
		connection[side] = null;
		TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
	}

	@Override
	public boolean onTileShiftRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isRemote){
			spawnCennectionParticle(side, "cloud");
		}else{
			//do nothing
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
				if(pc.world().getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) pc.world().getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
				}
				TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
				//				player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
			}
		}else{
			if(isRemote){
				//do nothing
			}else{
				if(flagIO[side] == 1){
					if(connectNextBlock(side)){
						ConnectionEntry<Target> entry = connection[side];
						if(!(entry == null)){
							player.addChatMessage(new ChatComponentText(connection[side].toString()));
						}else{
							player.addChatMessage(new ChatComponentText("No Connection Found"));
						}
					}else{
						player.addChatMessage(new ChatComponentText("No Connection Found"));
					}
					this.spawnParticle = true;
					this.pSide = side;
					TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
				}
			}
		}
		return true;
	}

	@Override
	public final boolean canStartAt(int side) {
		return flagIO[side] == 1;
	}
	@Override
	public final boolean tryConnect(ProcessorBase tile, int clickedSide, int linkUserSide) {
		if(!isRemote){
			if(tile != null && getTargetClass().isAssignableFrom(tile.getClass())){
				if(flagIO[linkUserSide] == 1 && tile.flagIO[clickedSide] == 0){
					ConnectionEntry<Target> pastEntry = connection[linkUserSide];
					if(shouldDistinguishSide()){
						connection[linkUserSide] = new ConnectionEntry<Target>((Target) getTargetClass().cast(tile), clickedSide);
					}else{
						connection[linkUserSide] = new ConnectionEntry<Target>((Target) getTargetClass().cast(tile));
					}
					checkConnectionToEntryForSide(linkUserSide);
					if(connection[linkUserSide] != null){
						spawnParticle = true;
						pSide = linkUserSide;
						TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
						return true;
					}else{
						connection[linkUserSide] = pastEntry;
						return false;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<ColoredString> getHUDStringsForCenter(MovingObjectPosition mop, double renderTicks) {
		ColoredString str = null;
		if(flagIO[mop.sideHit] == 0){
			str = new ColoredString("Input", 0x00BFFF);
		}else if(flagIO[mop.sideHit] == 1 && connection[mop.sideHit] != null && pc.getProcessor(connection[mop.sideHit].x, connection[mop.sideHit].y, connection[mop.sideHit].z) != null){
			str = new ColoredString("Output >> " + pc.getProcessor(connection[mop.sideHit].x, connection[mop.sideHit].y, connection[mop.sideHit].z).getNameForHUD().str, 0xFF8C00);
		}else if(flagIO[mop.sideHit] == 1){
			str = new ColoredString("Output", 0xFF8C00);
		}
		List<ColoredString> list = new ArrayList<ColoredString>();
		if(str != null){
			list.add(str);
		}
		return list;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**server and part only*/
	protected void sendConnectionParticlePacket(int side, int color) {
		assert !isTile && !isRemote;
		if(renderDetail() && connection[side] != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("side", (byte) side);
			tag.setInteger("color", color);
			pc.sendPacket(PacketType.NBT_CONNECTION, tag);
		}
	}

	/**client only*/
	@Override
	public void onRandomDisplayTick() {
		for (int side = 0; side < 6; side++){
			if(connection[side] != null){
				Vec3 vec1 = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));
				Vec3 vec2 = pc.toReadWorld(Vec3.createVectorHelper(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5));
				int side1 = pc.toRealWorldSide(side);
				int side2 = pc.toRealWorldSide(connection[side].side == -1 ? 6 : connection[side].side);
				TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_BEZIER_DOTS, vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord, side1, side2, 0x00FFFF);
			}
		}
	}

}
