package com.okina.item;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.ILinkConnectionUser;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemConnector extends Item {

	public ItemConnector() {
		setTextureName(TestCore.MODID + ":connector");
		setUnlocalizedName("mbm_connector");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if(tag == null){
			tag = new NBTTagCompound();
		}
		boolean prev = tag.getBoolean("connected");
		if(prev){
			int prevX = tag.getInteger("x");
			int prevY = tag.getInteger("y");
			int prevZ = tag.getInteger("z");
			int prevSide = tag.getInteger("side");
			if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
				if(world.getTileEntity(prevX, prevY, prevZ) instanceof ILinkConnectionUser){
					ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
					ILinkConnectionUser prevTile = (ILinkConnectionUser) world.getTileEntity(prevX, prevY, prevZ);
					if(prevTile.tryConnect(tile, side, prevSide)){
						if(!world.isRemote) player.addChatMessage(new ChatComponentText("Connected"));
					}else{
						if(!world.isRemote) player.addChatMessage(new ChatComponentText("Illegal Block"));
					}
				}
			}
			tag.setBoolean("connected", false);
		}else{
			if(world.getTileEntity(x, y, z) instanceof ILinkConnectionUser){
				if(((ILinkConnectionUser) world.getTileEntity(x, y, z)).canStartAt(side)){
					tag.setInteger("x", x);
					tag.setInteger("y", y);
					tag.setInteger("z", z);
					tag.setInteger("side", side);
					tag.setBoolean("connected", true);
					itemStack.setTagCompound(tag);
					if(!world.isRemote) player.addChatMessage(new ChatComponentText("Start Point"));
				}
			}
		}
		return true;
	}

}
