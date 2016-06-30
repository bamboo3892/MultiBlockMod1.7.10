package com.okina.item;

import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemWrench extends Item implements IToolWrench {

	public ItemWrench() {
		setTextureName(TestCore.MODID + ":hummer");
		setUnlocalizedName("mbm_wrench");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		Block block = world.getBlock(x, y, z);
		if(block != null && !player.isSneaking() && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))){
			player.swingItem();
			world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.8F);
			return !world.isRemote;
		}
		return false;
	}

	//	@Override
	//	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
	//		if(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity){
	//			ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(x, y, z);
	//			tile.onRightClickedByWrench(player, side, hitX, hitY, hitZ);
	//			//spawnParticle(world, x, y, z, side);
	//		}else if(world.getTileEntity(x, y, z) instanceof BlockFrameTileEntity){
	//			BlockFrameTileEntity frame = (BlockFrameTileEntity) world.getTileEntity(x, y, z);
	//			frame.tryConstruct();
	//		}
	//		return true;
	//	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

	}

	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {
		return true;
	}

}
