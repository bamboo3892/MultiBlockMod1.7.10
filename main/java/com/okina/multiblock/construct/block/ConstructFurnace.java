package com.okina.multiblock.construct.block;

import com.okina.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ConstructFurnace extends BlockConstructBase {

	protected IIcon top;
	protected IIcon front;

	public ConstructFurnace(int grade) {
		super(grade);
		setBlockName("mbm_furnace");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack itemStack) {
		int l = MathHelper.floor_double(livingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if(l == 0){
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}else if(l == 1){
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}else if(l == 2){
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}else if(l == 3){
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}
		super.onBlockPlacedBy(world, x, y, z, livingBase, itemStack);
	}

	@Override
	public String getProseccorName() {
		return "furnace";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon("furnace_side");
		front = register.registerIcon(TestCore.MODID + ":furnace_front_off");
		top = register.registerIcon("furnace_top");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? top : (side == 0 ? top : (side != meta ? blockIcon : front));
	}

	@Override
	public int getShiftLines() {
		return 3;
	}

}
