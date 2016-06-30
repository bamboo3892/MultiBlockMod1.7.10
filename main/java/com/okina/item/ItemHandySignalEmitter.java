package com.okina.item;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemHandySignalEmitter extends Item {

	public ItemHandySignalEmitter() {
		setTextureName(TestCore.MODID + ":handy_signal_emitter");
		setUnlocalizedName("mbm_signalEmitter");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity && ((ProcessorContainerTileEntity) world.getTileEntity(x, y, z)).getContainProcessor() instanceof ISignalReceiver){
				((ISignalReceiver) ((ProcessorContainerTileEntity) world.getTileEntity(x, y, z)).getContainProcessor()).onSignalReceived();
			}
		}
		return !world.isRemote;
	}

}
