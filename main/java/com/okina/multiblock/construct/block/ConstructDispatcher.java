package com.okina.multiblock.construct.block;

import com.okina.inventory.FilterInventory;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class ConstructDispatcher extends BlockConstructBase {

	public static IIcon filteredPane;

	public ConstructDispatcher(int grade) {
		super(grade);
		setBlockName("mbm_dispatcher");
	}

	@Override
	public String getProseccorName() {
		return "dispatcher";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
			ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
			if(tile.getFilter(side) instanceof FilterInventory){
				return filteredPane;
			}
		}
		return this.getIcon(side, world.getBlockMetadata(x, y, z));
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.lapis_block.getIcon(0, 0);
		filteredPane = register.registerIcon(TestCore.MODID + ":filtered_dispatcher");
	}

	@Override
	public int getShiftLines() {
		return 3;
	}


}
