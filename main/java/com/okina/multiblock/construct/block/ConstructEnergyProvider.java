package com.okina.multiblock.construct.block;

import java.util.Random;

import com.okina.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ConstructEnergyProvider extends BlockConstructBase {

	public static IIcon[] energyIcon = new IIcon[16];

	public ConstructEnergyProvider(int grade) {
		super(grade);
		setBlockName("mbm_energyprovider");
	}

	@Override
	public String getProseccorName() {
		return "energyprovider";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if((int) (rand.nextDouble() * 3) == 0){
			double spawnX = x + 0.4 + rand.nextDouble() * 0.2;
			double spawnY = y + 0.8 + rand.nextDouble() * 0.2;
			double spawnZ = z + 0.4 + rand.nextDouble() * 0.2;
			world.spawnParticle("portal", spawnX, spawnY, spawnZ, 0, 0, 0);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.portal.getIcon(0, 0);
		iconPane = register.registerIcon(TestCore.MODID + ":construct_pane");
		for (int i = 0; i < 5; i++){
			FRAME_ICON_FAST[i] = register.registerIcon(TestCore.MODID + ":part_frame_" + i);
		}
		for (int i = 0; i < energyIcon.length; i++){
			energyIcon[i] = register.registerIcon(TestCore.MODID + ":energy/energy" + i);
		}
	}

	@Override
	public int getShiftLines() {
		return 4;
	}

}
