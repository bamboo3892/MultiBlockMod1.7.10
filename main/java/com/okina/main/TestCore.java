package com.okina.main;

import java.io.File;

import com.okina.client.EffectProperties;
import com.okina.creativetab.TestCreativeTab;
import com.okina.server.command.MBMCommand;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = TestCore.MODID, name = TestCore.NAME, version = TestCore.VERSION, dependencies = "after:ThermalExpansion")
public class TestCore {

	public static final String MODID = "MultiBlockMod";
	public static final String NAME = "Multi Block Mod";
	public static final String VERSION = "0.20";

	@Mod.Instance(MODID)
	public static TestCore instance;
	@SidedProxy(clientSide = "com.okina.main.ClientProxy", serverSide = "com.okina.main.CommonProxy")
	public static CommonProxy proxy;

	//configuration
	public static File ConfigFile;
	@SideOnly(Side.CLIENT)
	public static EffectProperties effectProperties;

	//block instance
	public static Block pipe;
	public static Block[] baseFrame;
	public static Block[] constructFurnace;
	public static Block[] constructStorage;
	public static Block[] constructClockPulser;
	public static Block[] constructRepeater;
	public static Block[] constructContainer;
	public static Block[] constructCrusher;
	public static Block[] constructEnergyProvider;
	public static Block[] constructVirtualGrower;
	public static Block[] constructEventCatcher;
	public static Block[] constructDispatcher;
	public static Block[] constructAlter;
	public static Block[] constructRectificationRepeater;
	public static Block blockFrame;
	public static Block blockFrameLine;
	public static Block blockInterface;
	public static Block multiBlockCore;
	public static Block multiBlockCasing;
	public static Block disassemblyTable;

	public static Block denseCactusBlock;
	public static Block denseCactus1;
	public static Block denseCactus2;

	//item instance
	public static Item wrench;
	public static Item connector;
	public static Item filter;
	public static Item craftingFilter;
	public static Item regulationFilter;
	public static Item handyEmitter;
	public static Item itemMultiBlock;
	/**crusher, grower, energy, furnace*/
	public static Item[] filtering;
	public static Item toilet;
	public static Item doubleDustIron;
	public static Item doubleDustGold;
	public static Item greenPowder;
	public static Item denseGreenPowder;
	public static Item greenMatter;
	public static Item organicMatter;
	public static Item burntOrganicMatter;
	public static Item nbtPrinter;

	//creative tab
	public static final CreativeTabs testCreativeTab = new TestCreativeTab("testCreativeTab");

	//GUI ID
	public static final int ITEM_GUI_ID = 0;
	public static final int BLOCK_GUI_ID_0 = 1;
	public static final int BLOCK_GUI_ID_1 = 2;
	public static final int BLOCK_GUI_ID_2 = 3;
	public static final int BLOCK_GUI_ID_3 = 4;
	public static final int BLOCK_GUI_ID_4 = 5;
	public static final int BLOCK_GUI_ID_5 = 6;
	public static final int VIA_INTERFACE_GUI_ID_0 = 7;
	public static final int VIA_INTERFACE_GUI_ID_1 = 8;
	public static final int VIA_INTERFACE_GUI_ID_2 = 9;
	public static final int VIA_INTERFACE_GUI_ID_3 = 10;
	public static final int VIA_INTERFACE_GUI_ID_4 = 11;
	public static final int VIA_INTERFACE_GUI_ID_5 = 12;

	//render ID
	public static int TESTBLOCK_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int BLOCKPIPE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int CONSTRUCTBASE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int CONTAINER_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int ENERGYPROVIDER_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int BLOCKFRAME_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int BLOCKFRAMELINE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int MULTIBLOCK_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int DISASSEMBLY_TABLE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

	//packet
	public static SimpleNetworkWrapper packetDispatcher;
	public static final int SIMPLETILE_PACKET_ID = 0;
	public static final int SIMPLETILE_REPLY_PACKET_ID = 1;
	public static final int MULTIBLOCK_PACKET_ID = 2;
	public static final int WORLD_UPDATE_PACKET_ID = 3;
	public static final int COMMAND_PACKET_ID = 4;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigFile = event.getModConfigurationDirectory();
		proxy.loadConfiguration(event.getSuggestedConfigurationFile());
		proxy.registerBlock();
		proxy.registerItem();
		FMLCommonHandler.instance().bus().register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntity();
		proxy.registerRenderer();
		packetDispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		proxy.registerPacket();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerRecipe();
		IntegrationHandler.registerOre();
		IntegrationHandler.registerRecipe();
		proxy.LoadNEI();
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new MBMCommand());
	}

	//particle/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final int PARTICLE_GROWER = 1;
	public static final int PARTICLE_ENERGY = 2;
	public static final int PARTICLE_BEZIER = 3;
	public static final int PARTICLE_BEZIER_DOTS = 4;
	public static final int PARTICLE_DOT = 5;
	public static final int PARTICLE_CRUCK = 6;
	public static final int PARTICLE_BEZIER_DOT = 7;
	public static final int PARTICLE_FLAME = 8;
	public static final int PARTICLE_CUSTOM_ICON = 9;
	public static final int PARTICLE_ALTER = 10;
	public static final int PARTICLE_ALTER_DOT = 11;

	public static void spawnParticle(World world, int id, Object... objects) {
		proxy.spawnParticle(world, id, objects);
	}

}
