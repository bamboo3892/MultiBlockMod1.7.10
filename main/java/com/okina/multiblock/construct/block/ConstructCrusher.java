package com.okina.multiblock.construct.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ConstructCrusher extends BlockConstructBase {

	private IIcon front;

	public ConstructCrusher(int grade) {
		super(grade);
		setBlockName("mbm_crusher");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
		int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
		super.onBlockPlacedBy(world, x, y, z, entity, itemstack);
	}

	@Override
	public String getProseccorName() {
		return "crusher";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side != meta ? blockIcon : front;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.cactus.getIcon(2, 0);
		front = Blocks.cactus.getIcon(0, 0);
	}

	@Override
	public int getShiftLines() {
		return 3;
	}

}
