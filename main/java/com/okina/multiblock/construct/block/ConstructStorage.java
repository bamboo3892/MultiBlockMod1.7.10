package com.okina.multiblock.construct.block;

import com.okina.main.TestCore;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class ConstructStorage extends BlockConstructBase {

	public static IIcon filteredPane;
	public static IIcon craftingFilteredPane;

	public ConstructStorage(int grade) {
		super(grade);
		setBlockName("mbm_storage");
	}

	@Override
	public String getProseccorName() {
		return "storage";
	}
	//
	//	@Override
	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
	//		if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
	//			ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
	//			if(tile.getFilter(side) instanceof FilterInventory){
	//				return filteredPane;
	//			}else if(tile.getFilter(side) instanceof CraftingFilterInventory){
	//				return craftingFilteredPane;
	//			}
	//		}
	//		return this.getIcon(side, world.getBlockMetadata(x, y, z));
	//	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.planks.getIcon(0, 0);
		filteredPane = register.registerIcon(TestCore.MODID + ":filtered_storage");
		craftingFilteredPane = register.registerIcon(TestCore.MODID + ":crafting_filtered_storage");
	}

	@Override
	public int getShiftLines() {
		return 3;
	}

}
