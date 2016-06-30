package com.okina.multiblock.construct.processor;

import java.util.List;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.network.PacketType;
import com.okina.utils.ColoredString;
import com.okina.utils.Position;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyProviderProcessor extends ProcessorBase implements IEnergyHandler {

	public static final int[] storage = { 400000, 2000000, 20000000, 80000000, 100000000 };
	public static final int[] transfer = { 100000000, 100000000, 100000000, 100000000, 100000000 };

	/**server only*/
	public ContainerProcessor container;
	private EnergyStorage energyStorage = new EnergyStorage(100, 100);
	private int lastEnergy = 0;
	/**client only*/
	private int pastRenderEnergyLevel = -1;

	public EnergyProviderProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void init() {
		energyStorage = new EnergyStorage(storage[grade], transfer[grade]);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(isTile && !isRemote){
			if(lastEnergy != energyStorage.getEnergyStored()){
				TestCore.proxy.markForTileUpdate(getPosition(), PacketType.ENERGY);
			}
			lastEnergy = energyStorage.getEnergyStored();
		}
	}

	/**callled on only server*/
	@Override
	public Object getPacket(PacketType type) {
		if(type == PacketType.ENERGY){
			return energyStorage.getEnergyStored();
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.ENERGY && value instanceof Integer){
			energyStorage.setEnergyStored((Integer) value);
			if(isTile){
				int level = getEnergyLevel();
				if(level != pastRenderEnergyLevel){
					pc.markForRenderUpdate();
					pastRenderEnergyLevel = level;
				}
			}
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "energyprovider";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyStorage = new EnergyStorage(storage[grade], transfer[grade]);
		energyStorage.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		energyStorage.writeToNBT(tag);
	}

	//non-base-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**tile only*/
	public int sendEnergy(Position receiver, int maxSend, boolean simulate) {
		assert isTile;
		if(!pc.world().isRemote){
			if(simulate){
				return energyStorage.extractEnergy(maxSend, true);
			}
			int send = energyStorage.extractEnergy(maxSend, false);
			return send;
		}
		return 0;
	}

	public void spawnEnergySendParticle(Vec3 goal, int energy) {
		if(energy < 500){
			if(rand.nextInt(500) < energy){
				Vec3 start = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.4 + rand.nextDouble() * 0.2, yCoord + 0.4 + rand.nextDouble() * 0.2, zCoord + 0.4 + rand.nextDouble() * 0.2));
				Vec3 end = pc.toReadWorld(goal.addVector(-0.1 + rand.nextDouble() * 0.2, -0.1 + rand.nextDouble() * 0.2, -0.1 + rand.nextDouble() * 0.2));
				end = start.subtract(end);
				TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_ENERGY, start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord);
			}
		}
		for (int i = 0; i < (int) (energy / 500F); i++){
			Vec3 start = pc.toReadWorld(Vec3.createVectorHelper(xCoord + 0.4 + rand.nextDouble() * 0.2, yCoord + 0.4 + rand.nextDouble() * 0.2, zCoord + 0.4 + rand.nextDouble() * 0.2));
			Vec3 end = pc.toReadWorld(goal.addVector(-0.1 + rand.nextDouble() * 0.2, -0.1 + rand.nextDouble() * 0.2, -0.1 + rand.nextDouble() * 0.2));
			end = start.subtract(end);
			TestCore.spawnParticle(pc.world(), TestCore.PARTICLE_ENERGY, start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord);
		}
	}

	public int getEnergyLevel() {
		return (int) ((float) energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored() * 15F);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return energyStorage.extractEnergy(maxExtract, simulate);
	}
	public void setEnergyStored(int energy) {
		energyStorage.setEnergyStored(energy);
	}
	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energyStorage.getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyStorage.getMaxEnergyStored();
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
		list.add(new ColoredString(energyStorage.getEnergyStored() + " / " + energyStorage.getMaxEnergyStored() + " RF", 0x800080));
		return list;
	}

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("ENERGY PROVIDER", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileShiftRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.capabilities.isCreativeMode){
			if(!isRemote){
				energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
			}
			return true;
		}
		return false;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@SideOnly(Side.CLIENT)
	public Block getRenderBlock() {
		return TestCore.constructEnergyProvider[grade];
	}

}
