package com.okina.multiblock.construct.processor;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.multiblock.construct.block.ConstructAlter;
import com.okina.multiblock.construct.mode.AlterMode;
import com.okina.network.PacketType;
import com.okina.utils.ColoredString;
import com.okina.utils.ConnectionEntry;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class AlterProcessor extends ProcessorBase implements ISignalReceiver {

	/**server only*/
	public ContainerProcessor container = null;
	public ConnectionEntry<EnergyProviderProcessor> provider = null;

	public AlterProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void init() {

	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(provider != null){
			if(pc.getProcessor(provider.x, provider.y, provider.z) instanceof EnergyProviderProcessor){
				EnergyProviderProcessor pp = (EnergyProviderProcessor) pc.getProcessor(provider.x, provider.y, provider.z);
				provider = new ConnectionEntry<EnergyProviderProcessor>(pp);
			}else{
				provider = null;
			}
		}
	}

	@Override
	public Object getPacket(PacketType type) {
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT && value instanceof Integer){
			int require = (Integer) value;
			if(provider != null){
				if(pc.getProcessor(provider.x, provider.y, provider.z) instanceof EnergyProviderProcessor){
					((EnergyProviderProcessor) pc.getProcessor(provider.x, provider.y, provider.z)).spawnEnergySendParticle(Vec3.createVectorHelper(xCoord, yCoord, zCoord), require);
				}
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "alter";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound providerTag = tag.getCompoundTag("provider");
		provider = ConnectionEntry.createFromNBT(providerTag, pc);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(provider != null){
			NBTTagCompound providerTag = new NBTTagCompound();
			provider.writeToNBT(providerTag);
			tag.setTag("provider", providerTag);
		}
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean readyToStartWorship(int requireEnergy) {
		if(!isValid) return false;
		if(isRemote) return true;
		if(container != null && container.mode2 instanceof AlterMode && provider != null && provider.getTile() != null){
			if(isTile){
				return provider.getTile().sendEnergy(getPosition(), requireEnergy, true) == requireEnergy;
			}else{
				return sendEnergyFromMultiCore(requireEnergy, true) == requireEnergy;
			}
		}
		return false;
	}

	public boolean progressWorship(int energy, int speed) {
		if(provider != null && provider.getTile() != null){
			int require = energy * speed;
			if(isTile){
				if(provider.getTile().sendEnergy(getPosition(), require, true) == require){
					provider.getTile().sendEnergy(getPosition(), require, false);
					pc.sendPacket(PacketType.EFFECT, require);
					return true;
				}else{
					return false;
				}
			}else{
				if(sendEnergyFromMultiCore(require, true) == require){
					sendEnergyFromMultiCore(require, false);
					pc.sendPacket(PacketType.EFFECT, require);
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

	public void doWorship(int energy) {
		assert !isRemote;
		dispatchEventOnNextTick();
	}

	@Override
	public void onSignalReceived() {
		assert !isRemote;
		if(!isValid) return;
		if(container != null && container.mode2 instanceof AlterMode){
			((AlterMode) container.mode2).startWorship();
		}
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		return Lists.newArrayList(new ColoredString("Grade : " + GRADE_NAME[grade], ColorCode[grade]));
	}

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("ALTER", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean onTileShiftRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean onTileRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(world.getBlock(xCoord, yCoord, zCoord) instanceof ConstructAlter){
			if(player.isSneaking()){
				// do nothing
			}else{
				flagIO[side] = flagIO[side] == 0 ? 2 : 0;
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
				}
				pc.markForUpdate(PacketType.FLAG_IO);
				//				if(isRemote) player.addChatMessage(new ChatComponentText(flagIO[side] == 0 ? "input" : flagIO[side] == 1 ? "output" : "disabled"));
			}
		}
		return true;
	}

	@Override
	public void onTileLeftClicked(World world, EntityPlayer player, int side, double hitX, double hitY, double hitZ) {

	}

	@Override
	public boolean canStartAt(int side) {
		return true;
	}
	@Override
	public boolean tryConnect(ProcessorBase tile, int clickedSide, int linkUserSide) {
		if(tile instanceof EnergyProviderProcessor){
			provider = new ConnectionEntry<EnergyProviderProcessor>((EnergyProviderProcessor) tile);
			return true;
		}
		return false;
	}

	@Override
	public List<ColoredString> getHUDStringsForCenter(MovingObjectPosition mop, double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForCenter(mop, renderTicks);
		ColoredString str;
		if(provider != null){
			str = new ColoredString("Link to Energy Provider Construct", 0x00ff00);
		}else{
			str = new ColoredString("No Link Established", 0x00ff00);
		}
		list.add(str);
		return list;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@SideOnly(Side.CLIENT)
	public Block getRenderBlock() {
		return TestCore.constructAlter[grade];
	}

}
