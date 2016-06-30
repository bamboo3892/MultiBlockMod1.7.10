package com.okina.multiblock.construct.processor;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.network.PacketType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class SignalEmitterProcessor extends SidedOutputerProcessor<ISignalReceiver> {

	public SignalEmitterProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.EFFECT){//should client
			spawnSignalEffect();
		}
		super.processCommand(type, value);
	}

	@Override
	protected Class<ISignalReceiver> getTargetClass() {
		return ISignalReceiver.class;
	}

	@Override
	protected boolean shouldDistinguishSide() {
		return false;
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**server only*/
	public boolean emitSignal(int dir) {
		assert !isRemote;
		if(dir >= 0 && dir < 6){
			if(flagIO[dir] == 1 && connection[dir] != null && connection[dir].hasTile()){
				connection[dir].getTile().onSignalReceived();
				if(!isTile) sendConnectionParticlePacket(dir, 0xb22222);
				return true;
			}
		}
		return false;
	}

	/**server only*/
	public boolean emitSignal() {
		assert !isRemote;
		boolean flag = false;
		for (int i = 0; i < 6; i++){
			if(emitSignal(i)) flag = true;
		}
		pc.sendPacket(PacketType.EFFECT, 0);
		return flag;
	}

	/**client only*/
	public void spawnSignalEffect() {
		assert isRemote;
		if(isTile){
			pc.world().playSound(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, TestCore.MODID + ":clockpulser", 0.05F, 1F, false);
			for (int i = 0; i < 5; i++){
				pc.world().spawnParticle("reddust", xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
			}
		}else{
			if(renderDetail()){
				ForgeDirection dir = ForgeDirection.getOrientation((int) (Math.random() * 6));
				Vec3 vec = pc.toReadWorld(Vec3.createVectorHelper(xCoord + Math.random() * 0.4 + 0.3, yCoord + Math.random() * 0.4 + 0.3, zCoord + Math.random() * 0.4 + 0.3));
				pc.world().spawnParticle("reddust", vec.xCoord, vec.yCoord, vec.zCoord, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onTileShiftRightClicked(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(isRemote){
			spawnCennectionParticle(side, "reddust");
		}else{
			//do nothing
			int n = 0;
		}
		return true;
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
