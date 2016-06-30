package com.okina.multiblock.construct.processor;

import java.util.List;

import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.multiblock.construct.block.ConstructVirtualGlower;
import com.okina.multiblock.construct.mode.VirtualGrowerMode;
import com.okina.network.PacketType;
import com.okina.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.okina.utils.ColoredString;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.Position;

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

public class VirtualGrowerProcessor extends ProcessorBase implements ISignalReceiver {

	public static final int[] maxCapasity = { 1000, 5000, 100000, 500000, 2000000 };

	public ContainerProcessor container = null;
	public ConnectionEntry<EnergyProviderProcessor> provider = null;

	public VirtualGrowerProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
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
		return "virtualGrower";
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

	public boolean readyToStartGrow(VirtualGrowerRecipe recipe) {
		if(!isValid) return false;
		if(isRemote) return true;
		if(provider != null && provider.getTile() != null && container != null){
			if(isTile){
				return provider.getTile().sendEnergy(getPosition(), recipe.energy, true) == recipe.energy;
			}else{
				return sendEnergyFromMultiCore(recipe.energy, true) == recipe.energy;
			}
		}
		return false;
	}

	public boolean progressGrow(VirtualGrowerRecipe recipe) {
		assert !isRemote;
		if(provider != null && provider.getTile() != null){
			int require = recipe.getEnergyRate(grade);
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

	public void doGrow() {
		assert !isRemote;
		dispatchEventOnNextTick();
	}

	@Override
	public void onSignalReceived() {
		assert !isRemote;
		if(container != null && container.mode2 instanceof VirtualGrowerMode){
			((VirtualGrowerMode) container.mode2).startGrow();
		}
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public List<ColoredString> getHUDStringsForRight(MovingObjectPosition mop, double renderTicks) {
		List<ColoredString> list = super.getHUDStringsForRight(mop, renderTicks);
		//		list.add(new ColoredString(energyStorage.getEnergyStored() + " / " + energyStorage.getMaxEnergyStored() + " RF", 0x800080));
		return list;
	}

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("VIRTUAL GROWER", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileRightClickedByWrench(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(world.getBlock(xCoord, yCoord, zCoord) instanceof ConstructVirtualGlower){
			if(player.isSneaking()){
				// do nothing
			}else{
				flagIO[side] = flagIO[side] == 0 ? 2 : 0;
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
				}
				TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
				//				if(isRemote) player.addChatMessage(new ChatComponentText(flagIO[side] == 0 ? "input" : flagIO[side] == 1 ? "output" : "disabled"));
			}
		}
		return true;
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
		return TestCore.constructVirtualGrower[grade];
	}

}
