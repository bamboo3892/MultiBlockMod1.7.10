package com.okina.main;

import static com.okina.main.TestCore.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.okina.client.EffectProperties;
import com.okina.client.particle.ParticleAlter;
import com.okina.client.particle.ParticleAlterDot;
import com.okina.client.particle.ParticleBezierCurve;
import com.okina.client.particle.ParticleBezierDot;
import com.okina.client.particle.ParticleBezierDots;
import com.okina.client.particle.ParticleCruck;
import com.okina.client.particle.ParticleCustomIcon;
import com.okina.client.particle.ParticleDirectionalFlame;
import com.okina.client.particle.ParticleDot;
import com.okina.client.particle.ParticleEnergyProvide;
import com.okina.client.particle.ParticleGrower;
import com.okina.client.renderer.BlockDisassemblyTableRenderer;
import com.okina.client.renderer.BlockFrameLineRenderer;
import com.okina.client.renderer.BlockFrameRenderer;
import com.okina.client.renderer.BlockPipeRenderer;
import com.okina.client.renderer.ConstructBaseRenderer;
import com.okina.client.renderer.MultiBlockRenderer;
import com.okina.client.renderer.TileCasingRenderer;
import com.okina.client.renderer.TileConstructBaseRenderer;
import com.okina.client.renderer.TileFrameLaserRenderer;
import com.okina.client.renderer.TileMultiBlockRenderer;
import com.okina.client.renderer.TilePipeRenderer;
import com.okina.multiblock.BlockFrameTileEntity;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.MultiBlockCasingTileEntity;
import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.nei.LoadNEI;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

	@Override
	public void loadConfiguration(File pfile) {
		super.loadConfiguration(pfile);

		BufferedReader reader = null;
		try{
			Gson gson = new Gson();
			File file = new File(ConfigFile.getAbsolutePath() + File.separator + MODID + ".properties");
			reader = new BufferedReader(new FileReader(file));
			effectProperties = gson.fromJson(reader, EffectProperties.class);
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(reader != null){
				try{
					reader.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		if(effectProperties == null) effectProperties = new EffectProperties();
		updatePropertyFile();
	}

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerBlockHandler(new BlockPipeRenderer());
		RenderingRegistry.registerBlockHandler(new ConstructBaseRenderer());
		RenderingRegistry.registerBlockHandler(new BlockFrameRenderer());
		RenderingRegistry.registerBlockHandler(new BlockFrameLineRenderer());
		RenderingRegistry.registerBlockHandler(new MultiBlockRenderer());
		RenderingRegistry.registerBlockHandler(new BlockDisassemblyTableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BlockFrameTileEntity.class, new TileFrameLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ProcessorContainerTileEntity.class, new TileConstructBaseRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MultiBlockCoreTileEntity.class, new TileMultiBlockRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MultiBlockCasingTileEntity.class, new TileCasingRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BlockPipeTileEntity.class, new TilePipeRenderer());
	}

	@Override
	public void LoadNEI() {
		if(Loader.isModLoaded("NotEnoughItems")){
			LoadNEI.loadNEI();
		}
	}

	//file io//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void updatePropertyFile() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PrintWriter writer = null;
				try{
					Gson gson = new Gson();
					File file = new File(ConfigFile.getAbsolutePath() + File.separator + MODID + ".properties");
					writer = new PrintWriter(new FileWriter(file));
					String json = gson.toJson(effectProperties);
					//					json.replaceAll("~", "");
					//					json.replaceAll("{", "~\n");
					//					json.replaceAll("~", "{");
					//					json.replaceAll("}", "~\n");
					//					json.replaceAll("~", "}");
					//					json.replaceAll(",", "~\n");
					//					json.replaceAll("~", ",");
					writer.print(json);
				}catch (Exception e){
					e.printStackTrace();
				}finally{
					if(writer != null) writer.close();
				}
			}
		}, "Update Property Thread").start();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void spawnParticle(World world, int id, Object... objects) {
		if(effectProperties.particleSpawnRate == 0) return;
		if(world.getTotalWorldTime() % (101 - effectProperties.particleSpawnRate) == 0){
			try{
				switch (id) {
				case TestCore.PARTICLE_GROWER:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGrower(world, objects));
					break;
				case TestCore.PARTICLE_ENERGY:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleEnergyProvide(world, objects).set1());
					break;
				case TestCore.PARTICLE_BEZIER:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierCurve(world, objects));
					break;
				case TestCore.PARTICLE_BEZIER_DOTS:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierDots(world, objects));
					break;
				case TestCore.PARTICLE_DOT:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleDot(world, objects));
					break;
				case TestCore.PARTICLE_CRUCK:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleCruck(world, objects));
					break;
				case TestCore.PARTICLE_BEZIER_DOT:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierDot(world, objects));
					break;
				case TestCore.PARTICLE_FLAME:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleDirectionalFlame(world, objects));
					break;
				case TestCore.PARTICLE_CUSTOM_ICON:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleCustomIcon(world, objects));
					break;
				case TestCore.PARTICLE_ALTER:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleAlter(world, objects));
					break;
				case TestCore.PARTICLE_ALTER_DOT:
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleAlterDot(world, objects));
					break;
				}
			}catch (Exception e){
				System.err.println("Illegal parameter");
				e.printStackTrace();
			}
		}
	}

}
