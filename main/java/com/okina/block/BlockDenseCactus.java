package com.okina.block;

import java.util.List;
import java.util.Random;

import com.okina.client.IToolTipUser;
import com.okina.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDenseCactus extends Block implements IPlantable, IToolTipUser {

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	@SideOnly(Side.CLIENT)
	private IIcon bottomIcon;

	public BlockDenseCactus() {
		super(Material.cactus);
		setBlockName("mbm_dense_cactus");
		setHardness(1.0F);
		setCreativeTab(TestCore.testCreativeTab);
		setTickRandomly(true);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(world.isAirBlock(x, y + 1, z)){
			int l;

			for (l = 1; world.getBlock(x, y - l, z) == this; ++l){
				;
			}

			if(l < 7){
				int i1 = world.getBlockMetadata(x, y, z);

				if(i1 == 15){
					world.setBlock(x, y + 1, z, this);
					world.setBlockMetadataWithNotify(x, y, z, 0, 4);
					this.onNeighborBlockChange(world, x, y + 1, z, this);
				}else{
					world.setBlockMetadataWithNotify(x, y, z, i1 + 1, 4);
				}
			}
		}
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
	}

	/**
	 * Returns the bounding box of the wired rectangular prism to render.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1, z + 1 - f);
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.topIcon : (side == 0 ? this.bottomIcon : this.blockIcon);
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType() {
		return 13;
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return !super.canPlaceBlockAt(world, x, y, z) ? false : this.canBlockStay(world, x, y, z);
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if(!this.canBlockStay(world, x, y, z)){
			world.func_147480_a(x, y, z, true);
		}
	}

	/**
	 * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
	 */
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		if(world.getBlock(x - 1, y, z).getMaterial().isSolid()){
			return false;
		}else if(world.getBlock(x + 1, y, z).getMaterial().isSolid()){
			return false;
		}else if(world.getBlock(x, y, z - 1).getMaterial().isSolid()){
			return false;
		}else if(world.getBlock(x, y, z + 1).getMaterial().isSolid()){
			return false;
		}else{
			Block block = world.getBlock(x, y - 1, z);
			return block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this);
		}
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
		Block plant = plantable.getPlant(world, x, y + 1, z);
		EnumPlantType plantType = plantable.getPlantType(world, x, y + 1, z);

		if(plant == TestCore.denseCactus1 && this == TestCore.denseCactus1){
			return true;
		}
		return super.canSustainPlant(world, x, y, z, direction, plantable);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		entity.attackEntityFrom(DamageSource.cactus, 9.0F);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z, rand);
		if(rand.nextInt(5) == 0) world.spawnParticle("crit", x - 0.5 + rand.nextDouble() * 2.0, y + rand.nextDouble(), z - 0.5 + rand.nextDouble() * 2.0, 0, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(TestCore.MODID + ":cactus_side");
		this.topIcon = register.registerIcon(TestCore.MODID + ":cactus_top");
		this.bottomIcon = register.registerIcon(TestCore.MODID + ":cactus_bottom");
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Desert;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return -1;
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {}
	@Override
	public int getNeutralLines() {
		return 1;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

}
