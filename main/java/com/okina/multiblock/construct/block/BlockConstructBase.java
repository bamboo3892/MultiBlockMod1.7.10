package com.okina.multiblock.construct.block;

import java.util.List;

import com.okina.block.ITestModBlock;
import com.okina.client.IToolTipUser;
import com.okina.item.IFilterItem;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockConstructBase extends BlockContainer implements ITestModBlock, IToolTipUser {

	public static String[] GRADE_NAME = { "WOOD", "IRON", "GOLD", "DIAMOND", "EMERALD" };
	public static final int[] GRADE_COLOR = { 0xFFFFFF, 0xFFFFFF, 0xFFFF00, 0x00FFFF, 0x00FF00 };
	public static IIcon iconPane;
	public static IIcon[] FRAME_ICON_FAST = new IIcon[5];

	public final int grade;

	public BlockConstructBase(int grade) {
		super(Material.rock);
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
		setBlockBounds(2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
		this.grade = grade;
	}

	//	@Override
	//	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
	//		ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) createNewTileEntity(world, metadata);
	//		tile.setWorldObj(world);
	//		tile.xCoord = x;
	//		tile.yCoord = y;
	//		tile.zCoord = z;
	//		tile.setProcessor(getProseccorName(), grade);
	//		tile.updateIsNeighberBaseBlock();
	//		//		if((world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity)){
	//		//			ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
	//		//			tile.setProcessor(getProseccorName(), grade);
	//		//			tile.updateIsNeighberBaseBlock();
	//		//		}else{
	//		//			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
	//		//		}
	//		world.setTileEntity(x, y, z, tile);
	//		TileEntity tile2 = world.getTileEntity(x, y, z);
	//		Block block = world.getBlock(x, y, z);//null???
	//		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
	//		return super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, metadata);
	//	}

	/**Called when placed or player-log in on server and client side.<br>
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack itemStack) {
		if(!(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity)){
			return;
		}
		ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
		tile.setProcessor(getProseccorName(), grade);
		tile.updateIsNeighberBaseBlock();
		super.onBlockPlacedBy(world, x, y, z, livingBase, itemStack);
	}

	//		/**
	//		 * called only on server
	//		 */
	//		@Override
	//		public void onBlockAdded(World world, int x, int y, int z) {
	//			super.onBlockAdded(world, x, y, z);
	//			if(!(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity)){
	//				return;
	//			}
	//			ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
	//			tile.setProcessor(getProseccorName(), grade);
	//			tile.updateIsNeighberBaseBlock();
	//		}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
		if(!(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity)) return;
		ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
		tile.updateIsNeighberBaseBlock();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() == null){
			if(!player.isSneaking()){
				return onRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
			}else{
				return onShiftRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
			}
		}else if(player.getHeldItem().getItem() instanceof IToolWrench){
			return onRightClickedByWrench(world, x, y, z, player, side, hitX, hitY, hitZ);
		}else{
			if(player.getHeldItem().getItem() instanceof IFilterItem){
				return false;
			}else{
				return onRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
			}
		}
	}

	protected boolean onRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
			return ((ProcessorContainerTileEntity) world.getTileEntity(x, y, z)).onRightClicked(player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	protected boolean onShiftRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
			return ((ProcessorContainerTileEntity) world.getTileEntity(x, y, z)).onShiftRightClicked(player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	protected boolean onRightClickedByWrench(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
			return ((ProcessorContainerTileEntity) world.getTileEntity(x, y, z)).onRightClickedByWrench(player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		MovingObjectPosition mop = UtilMethods.getMovingObjectPositionFromPlayer(world, player, true);
		if(mop != null){
			if(mop.blockX == x && mop.blockY == y && mop.blockZ == z && world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
				((ProcessorContainerTileEntity) world.getTileEntity(x, y, z)).onLeftClicked(player, mop.sideHit, mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
			}
		}
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from
	 * getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride() {
		return false;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
	 * strength when this block inputs to a comparator.
	 */
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta) {
		return 0;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
			ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
			tile.onTileRemoved();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public int getRenderType() {
		return TestCore.CONSTRUCTBASE_RENDER_ID;
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return grade < 2 ? EnumRarity.common : (grade == 2 ? EnumRarity.uncommon : grade == 3 ? EnumRarity.rare : EnumRarity.epic);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {}
	@Override
	public int getNeutralLines() {
		return 0;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public abstract void registerBlockIcons(IIconRegister register);

	@Override
	/**return TileEntity extending ProcessorContainerTileEntity*/
	public final TileEntity createNewTileEntity(World world, int meta) {
		return new ProcessorContainerTileEntity();
	}

	public abstract String getProseccorName();
}
