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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class BlockDenseCactusBlock extends Block implements IToolTipUser {

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	@SideOnly(Side.CLIENT)
	private IIcon bottomIcon;

	public BlockDenseCactusBlock() {
		super(Material.cactus);
		setBlockName("mbm_dense_cactus_block");
		setHardness(1.0F);
		setCreativeTab(TestCore.testCreativeTab);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1, z + 1 - f);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(!(entity instanceof EntityLivingBase)){
			return;
		}
		if((world.isRemote) || (!(world instanceof WorldServer))){
			return;
		}
		FakePlayer fakeplayer = FakePlayerFactory.getMinecraft((WorldServer) world);
		entity.attackEntityFrom(DamageSource.causePlayerDamage(fakeplayer), 9.0F);
	}

	@Override
	public int getMobilityFlag() {
		return 2;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z, rand);
		world.spawnParticle("crit", x - 0.5 + rand.nextDouble() * 2.0, y + rand.nextDouble(), z - 0.5 + rand.nextDouble() * 2.0, 0, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.topIcon : (side == 0 ? this.bottomIcon : this.blockIcon);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(TestCore.MODID + ":cactus_block_side");
		this.topIcon = register.registerIcon(TestCore.MODID + ":cactus_block_top");
		this.bottomIcon = register.registerIcon(TestCore.MODID + ":cactus_block_bottom");
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {}
	@Override
	public int getNeutralLines() {
		return 2;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

}
