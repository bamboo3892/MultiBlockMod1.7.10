package com.okina.main;

import static com.okina.main.TestCore.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.okina.block.BlockDenseCactus;
import com.okina.block.BlockDenseCactus2;
import com.okina.block.BlockDenseCactusBlock;
import com.okina.item.ItemConnector;
import com.okina.item.ItemCraftingFilter;
import com.okina.item.ItemFilter;
import com.okina.item.ItemFilteringGlyph;
import com.okina.item.ItemHandySignalEmitter;
import com.okina.item.ItemNBTPrinter;
import com.okina.item.ItemRegulationFilter;
import com.okina.item.ItemWrench;
import com.okina.item.itemBlock.ItemBlockTestMod;
import com.okina.item.itemBlock.ItemBlockWithMeta;
import com.okina.item.itemBlock.ItemMultiBlock;
import com.okina.multiblock.BlockBaseFrame;
import com.okina.multiblock.BlockFrame;
import com.okina.multiblock.BlockFrameLine;
import com.okina.multiblock.BlockFrameTileEntity;
import com.okina.multiblock.BlockInterface;
import com.okina.multiblock.BlockInterfaceTileEntity;
import com.okina.multiblock.BlockPipe;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.DisassemblyTable;
import com.okina.multiblock.DisassemblyTableTileEntity;
import com.okina.multiblock.MultiBlockCasing;
import com.okina.multiblock.MultiBlockCasingTileEntity;
import com.okina.multiblock.MultiBlockCore;
import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.multiblock.construct.block.ConstructAlter;
import com.okina.multiblock.construct.block.ConstructClockPulser;
import com.okina.multiblock.construct.block.ConstructContainer;
import com.okina.multiblock.construct.block.ConstructCrusher;
import com.okina.multiblock.construct.block.ConstructDispatcher;
import com.okina.multiblock.construct.block.ConstructEnergyProvider;
import com.okina.multiblock.construct.block.ConstructEventCatcher;
import com.okina.multiblock.construct.block.ConstructFurnace;
import com.okina.multiblock.construct.block.ConstructRectificationRepeater;
import com.okina.multiblock.construct.block.ConstructRepeater;
import com.okina.multiblock.construct.block.ConstructStorage;
import com.okina.multiblock.construct.block.ConstructVirtualGlower;
import com.okina.multiblock.construct.mode.AlterMode;
import com.okina.multiblock.construct.mode.CrusherMode;
import com.okina.multiblock.construct.mode.EnergyProviderMode;
import com.okina.multiblock.construct.mode.FurnaceMode;
import com.okina.multiblock.construct.mode.NormalMode;
import com.okina.multiblock.construct.mode.VirtualGrowerMode;
import com.okina.multiblock.construct.processor.AlterProcessor;
import com.okina.multiblock.construct.processor.ClockPulserProcessor;
import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.multiblock.construct.processor.CrusherProcessor;
import com.okina.multiblock.construct.processor.DispatcherProcessor;
import com.okina.multiblock.construct.processor.DummyProcessor;
import com.okina.multiblock.construct.processor.EnergyProviderProcessor;
import com.okina.multiblock.construct.processor.EventCatcherProcessor;
import com.okina.multiblock.construct.processor.FurnaceProcessor;
import com.okina.multiblock.construct.processor.RectificationRepeaterProcessor;
import com.okina.multiblock.construct.processor.RepeaterProcessor;
import com.okina.multiblock.construct.processor.StorageProcessor;
import com.okina.multiblock.construct.processor.VirtualGrowerProcessor;
import com.okina.network.CommandPacket;
import com.okina.network.CommandPacket.CommandPacketHandler;
import com.okina.network.MultiBlockPacket;
import com.okina.network.MultiBlockPacket.MultiBlockPacketHandler;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.SimpleTilePacketHandler;
import com.okina.network.SimpleTilePacket.SimpleTileReplyPacketHandler;
import com.okina.network.WorldUpdatePacket;
import com.okina.network.WorldUpdatePacket.WorldUpdatePacketHandler;
import com.okina.register.AlterRecipeRegister;
import com.okina.register.ContainerModeRegister;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.EnergyProdeceRecipeRegister;
import com.okina.register.ProcessorRegistry;
import com.okina.register.StackedOre;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.Position;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CommonProxy {

	public void loadConfiguration(File pfile) {
		Configuration config = new Configuration(pfile);
		try{
			config.load();
			config.getInt("Particle Level", "EFFECT", 3, 0, 3, "Now this configulation replaced to proterty file.");
		}catch (Exception e){
			FMLLog.severe("config load errer");
		}finally{
			config.save();
		}
	}

	public void registerBlock() {
		pipe = new BlockPipe();
		GameRegistry.registerBlock(pipe, "pipe");

		baseFrame = new BlockBaseFrame[5];
		constructStorage = new ConstructStorage[5];
		constructClockPulser = new ConstructClockPulser[5];
		constructRepeater = new ConstructRepeater[5];
		constructRectificationRepeater = new ConstructRectificationRepeater[5];
		constructEventCatcher = new ConstructEventCatcher[5];
		constructContainer = new ConstructContainer[5];
		constructFurnace = new ConstructFurnace[5];
		constructVirtualGrower = new ConstructVirtualGlower[5];
		constructEnergyProvider = new ConstructEnergyProvider[5];
		constructCrusher = new ConstructCrusher[5];
		constructDispatcher = new ConstructDispatcher[5];
		constructAlter = new ConstructAlter[5];
		for (int i = 0; i < 5; i++){
			baseFrame[i] = new BlockBaseFrame(i);
			GameRegistry.registerBlock(baseFrame[i], "baseFrame." + i);
			constructStorage[i] = new ConstructStorage(i);
			GameRegistry.registerBlock(constructStorage[i], ItemBlockTestMod.class, "storage." + i);
			constructClockPulser[i] = new ConstructClockPulser(i);
			GameRegistry.registerBlock(constructClockPulser[i], ItemBlockTestMod.class, "clockPulser." + i);
			constructRepeater[i] = new ConstructRepeater(i);
			GameRegistry.registerBlock(constructRepeater[i], ItemBlockTestMod.class, "repeater." + i);
			constructRectificationRepeater[i] = new ConstructRectificationRepeater(i);
			GameRegistry.registerBlock(constructRectificationRepeater[i], ItemBlockTestMod.class, "rectificationRepeater." + i);
			constructEventCatcher[i] = new ConstructEventCatcher(i);
			GameRegistry.registerBlock(constructEventCatcher[i], ItemBlockTestMod.class, "eventCatcher." + i);
			constructContainer[i] = new ConstructContainer(i);
			GameRegistry.registerBlock(constructContainer[i], ItemBlockTestMod.class, "container." + i);
			constructFurnace[i] = new ConstructFurnace(i);
			GameRegistry.registerBlock(constructFurnace[i], ItemBlockTestMod.class, "furnace." + i);
			constructVirtualGrower[i] = new ConstructVirtualGlower(i);
			GameRegistry.registerBlock(constructVirtualGrower[i], ItemBlockTestMod.class, "virtualGlower." + i);
			constructEnergyProvider[i] = new ConstructEnergyProvider(i);
			GameRegistry.registerBlock(constructEnergyProvider[i], ItemBlockTestMod.class, "energyProvider." + i);
			constructCrusher[i] = new ConstructCrusher(i);
			GameRegistry.registerBlock(constructCrusher[i], ItemBlockTestMod.class, "crusher." + i);
			constructDispatcher[i] = new ConstructDispatcher(i);
			GameRegistry.registerBlock(constructDispatcher[i], ItemBlockTestMod.class, "dispatcher." + i);
			constructAlter[i] = new ConstructAlter(i);
			GameRegistry.registerBlock(constructAlter[i], ItemBlockTestMod.class, "alter." + i);
		}
		blockFrame = new BlockFrame();
		GameRegistry.registerBlock(blockFrame, "blockFrame");
		blockFrameLine = new BlockFrameLine();
		GameRegistry.registerBlock(blockFrameLine, "blockFrameLine");
		blockInterface = new BlockInterface();
		GameRegistry.registerBlock(blockInterface, ItemBlockTestMod.class, "interface");
		multiBlockCore = new MultiBlockCore();
		GameRegistry.registerBlock(multiBlockCore, ItemMultiBlock.class, "multiBlockCore");
		multiBlockCasing = new MultiBlockCasing();
		GameRegistry.registerBlock(multiBlockCasing, "multiBlockCasing");
		disassemblyTable = new DisassemblyTable();
		GameRegistry.registerBlock(disassemblyTable, "disassemblyTable");

		denseCactus1 = new BlockDenseCactus();
		GameRegistry.registerBlock(denseCactus1, "denseCactus");
		denseCactus2 = new BlockDenseCactus2();
		GameRegistry.registerBlock(denseCactus2, ItemBlockWithMeta.class, "denseCactus2");
		denseCactusBlock = new BlockDenseCactusBlock();
		GameRegistry.registerBlock(denseCactusBlock, "denseCactusBlock");
	}

	public void registerItem() {
		wrench = new ItemWrench();
		GameRegistry.registerItem(wrench, "wrench");
		connector = new ItemConnector();
		GameRegistry.registerItem(connector, "connector");
		handyEmitter = new ItemHandySignalEmitter();
		GameRegistry.registerItem(handyEmitter, "handyEmitter");
		filter = new ItemFilter();
		GameRegistry.registerItem(filter, "filter");
		filtering = new Item[4];
		filtering[0] = new ItemFilteringGlyph(0.25f).setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_filtering_c").setTextureName(MODID + ":filtering_c");
		GameRegistry.registerItem(filtering[0], "filtering_c");
		filtering[1] = new ItemFilteringGlyph(0.58f).setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_filtering_g").setTextureName(MODID + ":filtering_g");
		GameRegistry.registerItem(filtering[1], "filtering_g");
		filtering[2] = new ItemFilteringGlyph(0.78f).setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_filtering_e").setTextureName(MODID + ":filtering_e");
		GameRegistry.registerItem(filtering[2], "filtering_e");
		filtering[3] = new ItemFilteringGlyph(1f).setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_filtering_f").setTextureName(MODID + ":filtering_f");
		GameRegistry.registerItem(filtering[3], "filtering_f");
		craftingFilter = new ItemCraftingFilter();
		GameRegistry.registerItem(craftingFilter, "craftingFilter");
		regulationFilter = new ItemRegulationFilter();
		GameRegistry.registerItem(regulationFilter, "regulationFilter");
		itemMultiBlock = new ItemMultiBlock(multiBlockCore);
		toilet = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_toilet").setTextureName(MODID + ":toilet");
		GameRegistry.registerItem(toilet, "toilet");
		doubleDustIron = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_dustDoubleIron").setTextureName(MODID + ":double_iron_dust");
		GameRegistry.registerItem(doubleDustIron, "dustDoubleIron");
		doubleDustGold = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_dustDoubleGold").setTextureName(MODID + ":double_gold_dust");
		GameRegistry.registerItem(doubleDustGold, "dustDoubleGold");
		greenPowder = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_greenPowder").setTextureName(MODID + ":green_powder");
		GameRegistry.registerItem(greenPowder, "greenPowder");
		denseGreenPowder = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_denseGreenPowder").setTextureName(MODID + ":dense_green_powder");
		GameRegistry.registerItem(denseGreenPowder, "denseGreenPowder");
		greenMatter = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_greenMatter").setTextureName(MODID + ":green_matter");
		GameRegistry.registerItem(greenMatter, "greenMatter");
		organicMatter = new ItemFood(4, true) {
			@Override
			public void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
				super.onFoodEaten(itemStack, world, entityPlayer);
				ForgeHooks.onPlayerTossEvent(entityPlayer, new ItemStack(toilet, 1), true);
			}
		}.setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_organicMatter").setTextureName(MODID + ":organic_matter");
		GameRegistry.registerItem(organicMatter, "organicMatter");
		burntOrganicMatter = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("mbm_burntOrganicMatter").setTextureName(MODID + ":burnt_organic_matter");
		GameRegistry.registerItem(burntOrganicMatter, "burntOrganicMatter");
		nbtPrinter = new ItemNBTPrinter();
		GameRegistry.registerItem(nbtPrinter, "nbtPrinter");
	}

	public void registerTileEntity() {
		ContainerModeRegister.registerProcessor(NormalMode.class);
		ContainerModeRegister.registerProcessor(CrusherMode.class);
		ContainerModeRegister.registerProcessor(VirtualGrowerMode.class);
		ContainerModeRegister.registerProcessor(EnergyProviderMode.class);
		ContainerModeRegister.registerProcessor(FurnaceMode.class);
		ContainerModeRegister.registerProcessor(AlterMode.class);

		GameRegistry.registerTileEntity(BlockPipeTileEntity.class, "blockPipeTileEntity");
		GameRegistry.registerTileEntity(BlockFrameTileEntity.class, "blockFrameTileEntity");
		GameRegistry.registerTileEntity(BlockInterfaceTileEntity.class, "constructInterfaceTileEntity");
		GameRegistry.registerTileEntity(MultiBlockCoreTileEntity.class, "multiBlockCoreTileEntity");
		GameRegistry.registerTileEntity(MultiBlockCasingTileEntity.class, "multiBlockCasingTileEntity");
		GameRegistry.registerTileEntity(DisassemblyTableTileEntity.class, "disassemblyTableTileEntity");

		GameRegistry.registerTileEntity(ProcessorContainerTileEntity.class, "processorContainerTileEntity");
		ProcessorRegistry.registerProcessor(null, new DummyProcessor());
		ProcessorRegistry.registerProcessor(constructFurnace, new FurnaceProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructStorage, new StorageProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructClockPulser, new ClockPulserProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructRepeater, new RepeaterProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructRectificationRepeater, new RectificationRepeaterProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructContainer, new ContainerProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructCrusher, new CrusherProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructEnergyProvider, new EnergyProviderProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructVirtualGrower, new VirtualGrowerProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructEventCatcher, new EventCatcherProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructDispatcher, new DispatcherProcessor(null, false, false, 0, 0, 0, 0));
		ProcessorRegistry.registerProcessor(constructDispatcher, new AlterProcessor(null, false, false, 0, 0, 0, 0));
	}

	public void registerRecipe() {
		//crafting
		GameRegistry.addRecipe(new ItemStack(pipe, 8, 0), new Object[] { "XRX", 'X', Blocks.fence, 'R', Blocks.redstone_block });
		GameRegistry.addRecipe(new ItemStack(blockFrame, 1, 0), new Object[] { " X ", "XRX", 'X', Items.coal, 'R', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(blockFrameLine, 8, 0), new Object[] { " X ", "XRX", 'X', Items.coal, 'R', Blocks.redstone_block });
		GameRegistry.addRecipe(new ItemStack(blockInterface, 1), new Object[] { "SBS", "CFC", 'B', Blocks.stone, 'F', Blocks.obsidian, 'S', Blocks.wool, 'C', Items.string });
		GameRegistry.addRecipe(new ItemStack(multiBlockCasing, 8, 0), new Object[] { " X ", "XRX", 'X', Blocks.glass, 'R', Blocks.glass_pane });

		GameRegistry.addRecipe(new ItemStack(baseFrame[0], 1), new Object[] { "XXX", "X X", "XXX", 'X', Blocks.fence });
		for (int i = 0; i < 5; i++){
			GameRegistry.addRecipe(new ItemStack(constructFurnace[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.furnace, 'S', Blocks.stone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructStorage[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.chest, 'S', Blocks.fence, 'C', Blocks.planks });
			GameRegistry.addRecipe(new ItemStack(constructClockPulser[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.redstone_block, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructRepeater[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', new ItemStack(Blocks.quartz_block, 1, 0), 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructRectificationRepeater[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', new ItemStack(Blocks.quartz_block, 1, 1), 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructContainer[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.glass, 'S', Blocks.glass_pane, 'C', Blocks.glass_pane });
			GameRegistry.addRecipe(new ItemStack(constructCrusher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.cactus, 'S', Items.stone_pickaxe, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructEnergyProvider[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.ender_pearl, 'S', Items.enchanted_book, 'C', Blocks.nether_brick });
			GameRegistry.addRecipe(new ItemStack(constructVirtualGrower[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.grass, 'S', Blocks.leaves, 'C', Items.flower_pot });
			GameRegistry.addRecipe(new ItemStack(constructEventCatcher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.comparator, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructDispatcher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.lapis_block, 'S', Items.redstone, 'C', Blocks.fence });
			GameRegistry.addRecipe(new ItemStack(constructAlter[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.ender_pearl, 'S', Blocks.stone, 'C', Blocks.obsidian });
		}
		Item[] material = { Items.iron_ingot, Items.gold_ingot, Items.diamond, Items.emerald };
		for (int i = 0; i < 4; i++){
			GameRegistry.addShapelessRecipe(new ItemStack(baseFrame[i + 1], 1), new Object[] { baseFrame[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructFurnace[i + 1], 1), new Object[] { constructFurnace[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructStorage[i + 1], 1), new Object[] { constructStorage[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructClockPulser[i + 1], 1), new Object[] { constructClockPulser[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructRepeater[i + 1], 1), new Object[] { constructRepeater[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructRectificationRepeater[i + 1], 1), new Object[] { constructRectificationRepeater[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructContainer[i + 1], 1), new Object[] { constructContainer[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructCrusher[i + 1], 1), new Object[] { constructCrusher[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructEnergyProvider[i + 1], 1), new Object[] { constructEnergyProvider[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructVirtualGrower[i + 1], 1), new Object[] { constructVirtualGrower[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructEventCatcher[i + 1], 1), new Object[] { constructEventCatcher[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructDispatcher[i + 1], 1), new Object[] { constructDispatcher[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructAlter[i + 1], 1), new Object[] { constructAlter[i], material[i] });
		}
		GameRegistry.addRecipe(new ItemStack(wrench, 1), new Object[] { "SBS", " B ", " B ", 'B', Items.reeds, 'S', Items.iron_ingot });
		GameRegistry.addRecipe(new ItemStack(connector, 1), new Object[] { " S ", "D  ", 'S', Blocks.sapling, 'D', Items.stick });
		GameRegistry.addRecipe(new ItemStack(handyEmitter, 1), new Object[] { " S ", "D  ", 'S', Items.redstone, 'D', Blocks.sapling });
		GameRegistry.addRecipe(new ItemStack(craftingFilter, 2, 0), new Object[] { "XRX", 'X', Items.stick, 'R', Blocks.crafting_table });
		GameRegistry.addShapelessRecipe(new ItemStack(filter, 1), new Object[] { Blocks.trapdoor });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[0], 1), new Object[] { filter, Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[0], 1), new Object[] { filtering[3], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[1], 1), new Object[] { filtering[0], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[2], 1), new Object[] { filtering[1], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[3], 1), new Object[] { filtering[2], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(regulationFilter, 1), new Object[] { Blocks.trapdoor, Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.cactus, 8), new Object[] { denseCactus1 });
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(greenMatter, 1), new Object[] { Blocks.cactus, greenPowder, "dyeGreen" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(organicMatter, 1), new Object[] { Blocks.cactus, greenPowder, "dyeGreen", Items.apple, Items.melon, Items.reeds, Items.carrot, Items.potato, Items.wheat }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(organicMatter, 1), new Object[] { greenMatter, Items.apple, Items.melon, Items.reeds, Items.carrot, Items.potato, Items.wheat }));
		GameRegistry.addRecipe(new ItemStack(denseCactusBlock, 1), new Object[] { "CC ", "CC ", 'C', denseCactus1 });

		List<ItemStack> irondusts = OreDictionary.getOres("dustIron");
		if(irondusts != null && irondusts.size() > 0){
			ItemStack dust = irondusts.get(0).copy();
			if(dust != null){
				dust.stackSize = 2;
				GameRegistry.addShapelessRecipe(dust, new ItemStack(doubleDustIron, 1));
			}
		}
		List<ItemStack> golddusts = OreDictionary.getOres("dustIron");
		if(golddusts != null && golddusts.size() > 0){
			ItemStack dust = golddusts.get(0).copy();
			if(dust != null){
				dust.stackSize = 2;
				GameRegistry.addShapelessRecipe(dust, new ItemStack(doubleDustGold, 1));
			}
		}

		//smelting
		GameRegistry.addSmelting(doubleDustIron, new ItemStack(Items.iron_ingot, 2), 0.3F);
		GameRegistry.addSmelting(doubleDustGold, new ItemStack(Items.gold_ingot, 2), 0.3F);
		GameRegistry.addSmelting(organicMatter, new ItemStack(burntOrganicMatter, 1), 0.3F);

		//crusher
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cobblestone, 1), new StackedOre(new ItemStack(Blocks.sand, 1)));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.stone, 1), new StackedOre(new ItemStack(Blocks.gravel, 1)));
		CrusherRecipeRegister.instance.registerRecipe("gravel", new StackedOre(new ItemStack(Items.flint, 1)));
		CrusherRecipeRegister.instance.registerRecipe("sandstone", new StackedOre(new ItemStack(Blocks.sand, 4)));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.bone, 1), new StackedOre(new ItemStack(Items.dye, 6, 15)));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.blaze_rod, 1), new StackedOre(new ItemStack(Items.blaze_powder, 4)));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), new StackedOre(new ItemStack(greenPowder, 1)));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus1, 1), new StackedOre(new ItemStack(denseGreenPowder, 1)));
		//		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus1, 1), new StackedOre(new ItemStack(Blocks.cactus, 8)));
		CrusherRecipeRegister.instance.registerRecipe("blockGlass", new StackedOre(new ItemStack(Blocks.sand, 1)));
		CrusherRecipeRegister.instance.registerRecipe("glowstone", new StackedOre(new ItemStack(Items.glowstone_dust, 4)));
		CrusherRecipeRegister.instance.registerRecipe("blockQuartz", new StackedOre(new ItemStack(Items.quartz, 4)));
		CrusherRecipeRegister.instance.registerRecipe("blockQuartzPiller", new StackedOre(new ItemStack(Items.quartz, 4)));
		CrusherRecipeRegister.instance.registerRecipe("blockChiseledQuartz", new StackedOre(new ItemStack(Items.quartz, 4)));
		CrusherRecipeRegister.instance.registerRecipe("blockWool", new StackedOre(new ItemStack(Items.string, 4)));
		CrusherRecipeRegister.instance.registerRecipe("logWood", new StackedOre("dustWood", 2));
		CrusherRecipeRegister.instance.registerRecipe("coal", new StackedOre("dustCoal", 1));
		CrusherRecipeRegister.instance.registerRecipe("charcoal", new StackedOre("dustCoal", 1));
		CrusherRecipeRegister.instance.registerRecipe("wheet", new StackedOre("dustWheat", 3));
		CrusherRecipeRegister.instance.registerRecipe("gemDiamond", new StackedOre("dustDiamond", 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.obsidian, 1), new StackedOre("dustObsidian", 4));
		CrusherRecipeRegister.instance.registerRecipe("oreCoal", new StackedOre(new ItemStack(Items.coal, 4)));
		CrusherRecipeRegister.instance.registerRecipe("oreLapis", new StackedOre(new ItemStack(Items.dye, 12, 4)));
		CrusherRecipeRegister.instance.registerRecipe("oreRedstone", new StackedOre(new ItemStack(Items.redstone, 6)));
		CrusherRecipeRegister.instance.registerRecipe("oreDiamond", new StackedOre(new ItemStack(Items.diamond, 2)));
		CrusherRecipeRegister.instance.registerRecipe("oreEmerald", new StackedOre(new ItemStack(Items.emerald, 2)));
		CrusherRecipeRegister.instance.registerRecipe("oreQuartz", new StackedOre(new ItemStack(Items.quartz, 3)));

		//virtual grower
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), 1000, 100, 0);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.apple, 1), 1000, 100, 0);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.melon, 1), 1000, 100, 0);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.reeds, 1), 1000, 100, 0);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.carrot, 1), 1000, 100, 0);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.potato, 1), 1000, 100, 0);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.wheat, 1), 1000, 100, 0);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.hay_block, 1), 6000, 1000, 1);
		VirtualGrowerRecipeRegister.instance.registerRecipe("logWood", 1000, 100, 1);
		VirtualGrowerRecipeRegister.instance.registerRecipe("treeSapling", 400, 100, 1);
		VirtualGrowerRecipeRegister.instance.registerRecipe("treeLeaves", 1000, 100, 1);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.deadbush, 1), 1000, 400, 2);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.vine, 1), 1000, 100, 2);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.tallgrass, 1), 1000, 100, 2);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.speckled_melon, 1), 500000, 5000, 3);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.golden_apple, 1), 500000, 5000, 3);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.nether_star, 1), 2000000, 10000, 4);

		//energy provider
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), 2000, 100);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus1, 1), 80000, 800);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus2, 1, 0), 1600000, 2800);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus2, 1, 1), 16000000, 10000);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus2, 1, 2), 48000000, 20000);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(greenMatter, 1), 30000, 300);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(organicMatter, 1), 1000000, 500);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(toilet, 1), 1500000, 500);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(burntOrganicMatter, 1), 2000000, 1000);

		//alter
		{
			Map<Position, Object> map = new HashMap();
			map.put(new Position(0, 1, 0), new ItemStack(Items.apple, 1));
			AlterRecipeRegister.instance.registerRecipe("gemDiamond", map, 40000, 4800, 2, 0, new StackedOre("mapleDiamond", 1));
		}
		//regular octahedron
		{
			Map<Position, Object> map = new HashMap();
			map.put(new Position(0, 2, 0), new ItemStack(Items.skull, 1, 1));
			map.put(new Position(0, -2, 0), new ItemStack(Items.skull, 1, 1));
			map.put(new Position(2, 0, 0), new ItemStack(Blocks.soul_sand, 1));
			map.put(new Position(0, 0, 2), new ItemStack(Blocks.soul_sand, 1));
			map.put(new Position(-2, 0, 0), new ItemStack(Blocks.soul_sand, 1));
			map.put(new Position(0, 0, -2), new ItemStack(Blocks.soul_sand, 1));
			AlterRecipeRegister.instance.registerRecipe(new ItemStack(Items.skull, 1, 1), map, 40000, 2400, 2, 0, new StackedOre(new ItemStack(Items.nether_star, 1)));
		}
		//octahedron??
		{
			Map<Position, Object> map = new HashMap();
			map.put(new Position(0, 1, 0), "treeLeaves");
			map.put(new Position(0, -1, 0), "treeLeaves");
			map.put(new Position(1, 0, 1), new ItemStack(Items.wheat_seeds, 1));
			map.put(new Position(1, 0, -1), new ItemStack(Items.wheat_seeds, 1));
			map.put(new Position(-1, 0, 1), new ItemStack(Items.wheat_seeds, 1));
			map.put(new Position(-1, 0, -1), new ItemStack(Items.wheat_seeds, 1));
			AlterRecipeRegister.instance.registerRecipe("treeLeaves", map, 2000, 1200, 0, 0, new StackedOre(new ItemStack(Blocks.cactus, 1)));
		}
		{
			Map<Position, Object> map = new HashMap();

			map.put(new Position(0, 2, 0), new ItemStack(denseGreenPowder, 1));
			map.put(new Position(0, -2, 0), new ItemStack(denseGreenPowder, 1));
			map.put(new Position(2, 0, 0), new ItemStack(greenPowder, 1));
			map.put(new Position(0, 0, 2), new ItemStack(greenPowder, 1));
			map.put(new Position(-2, 0, 0), new ItemStack(greenPowder, 1));
			map.put(new Position(0, 0, -2), new ItemStack(greenPowder, 1));
			AlterRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus1, 1), map, 2000, 1200, 0, 0, new StackedOre(new ItemStack(denseCactus2, 1, 0)));
		}
		//cube
		{
			Map<Position, Object> map = new HashMap();
			map.put(new Position(1, -1, 1), new ItemStack(Items.redstone, 1));
			map.put(new Position(1, -1, -1), new ItemStack(Items.redstone, 1));
			map.put(new Position(-1, -1, 1), new ItemStack(Items.redstone, 1));
			map.put(new Position(-1, -1, -1), new ItemStack(Items.redstone, 1));
			map.put(new Position(1, 1, 1), new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE));
			map.put(new Position(1, 1, -1), new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE));
			map.put(new Position(-1, 1, 1), new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE));
			map.put(new Position(-1, 1, -1), new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE));
			AlterRecipeRegister.instance.registerRecipe(new ItemStack(Items.fermented_spider_eye, 1), map, 10000, 6000, 2, 0, new StackedOre(new ItemStack(Items.ender_pearl, 2)));
		}
		{
			Map<Position, Object> map = new HashMap();
			map.put(new Position(1, -1, 1), new ItemStack(Blocks.cactus, 1));
			map.put(new Position(1, -1, -1), new ItemStack(Blocks.cactus, 1));
			map.put(new Position(-1, -1, 1), new ItemStack(Blocks.cactus, 1));
			map.put(new Position(-1, -1, -1), new ItemStack(Blocks.cactus, 1));
			map.put(new Position(1, 1, 1), new ItemStack(Blocks.cactus, 1));
			map.put(new Position(1, 1, -1), new ItemStack(Blocks.cactus, 1));
			map.put(new Position(-1, 1, 1), new ItemStack(Blocks.cactus, 1));
			map.put(new Position(-1, 1, -1), new ItemStack(Blocks.cactus, 1));
			AlterRecipeRegister.instance.registerRecipe(new ItemStack(greenPowder, 1), map, 10000, 6000, 2, 0, new StackedOre(new ItemStack(denseCactus1, 1)));
		}
		//top
		{
			Map<Position, Object> map = new HashMap();
			map.put(new Position(0, 2, 0), new ItemStack(denseGreenPowder, 1));
			map.put(new Position(0, -2, 0), new ItemStack(denseGreenPowder, 1));
			map.put(new Position(1, -1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(1, -1, -1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, -1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, -1, -1), new ItemStack(denseCactus1, 1));
			map.put(new Position(1, 1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(1, 1, -1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, 1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, 1, -1), new ItemStack(denseCactus1, 1));
			AlterRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus2, 1, 0), map, 10000, 6000, 2, 0, new StackedOre(new ItemStack(denseCactus2, 1, 1)));
		}
		{
			Map<Position, Object> map = new HashMap();
			map.put(new Position(0, 2, 0), new ItemStack(denseGreenPowder, 1));
			map.put(new Position(0, -2, 0), new ItemStack(denseGreenPowder, 1));
			map.put(new Position(2, 0, 0), new ItemStack(greenPowder, 1));
			map.put(new Position(0, 0, 2), new ItemStack(greenPowder, 1));
			map.put(new Position(-2, 0, 0), new ItemStack(greenPowder, 1));
			map.put(new Position(0, 0, -2), new ItemStack(greenPowder, 1));
			map.put(new Position(1, -1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(1, -1, -1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, -1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, -1, -1), new ItemStack(denseCactus1, 1));
			map.put(new Position(1, 1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(1, 1, -1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, 1, 1), new ItemStack(denseCactus1, 1));
			map.put(new Position(-1, 1, -1), new ItemStack(denseCactus1, 1));
			AlterRecipeRegister.instance.registerRecipe(new ItemStack(denseCactus2, 1, 1), map, 10000, 6000, 2, 0, new StackedOre(new ItemStack(denseCactus2, 1, 2)));
		}
	}

	public void registerRenderer() {}

	public void registerPacket() {
		packetDispatcher.registerMessage(SimpleTilePacketHandler.class, SimpleTilePacket.class, SIMPLETILE_PACKET_ID, Side.SERVER);
		packetDispatcher.registerMessage(SimpleTileReplyPacketHandler.class, SimpleTilePacket.class, SIMPLETILE_REPLY_PACKET_ID, Side.CLIENT);
		packetDispatcher.registerMessage(MultiBlockPacketHandler.class, MultiBlockPacket.class, MULTIBLOCK_PACKET_ID, Side.CLIENT);
		packetDispatcher.registerMessage(WorldUpdatePacketHandler.class, WorldUpdatePacket.class, WORLD_UPDATE_PACKET_ID, Side.CLIENT);
		packetDispatcher.registerMessage(CommandPacketHandler.class, CommandPacket.class, COMMAND_PACKET_ID, Side.CLIENT);
	}

	public void LoadNEI() {}

	//file io//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void updatePropertyFile() {}

	//packet//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Map<PacketType, List<Position>> positionListMap = new HashMap<PacketType, List<Position>>();

	/**return true if newly marked*/
	public boolean markForTileUpdate(Position position, PacketType type) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(positionListMap.get(type) != null){
				List<Position> positionList = positionListMap.get(type);
				for (Position tmp : positionList){
					if(tmp != null && tmp.equals(position)){
						//System.out.println("already marked update");
						return false;
					}
				}
				positionList.add(position);
			}else{
				List<Position> positionList = new ArrayList<Position>();
				positionList.add(position);
				positionListMap.put(type, positionList);
			}
			return true;
		}else{
			return false;
		}
	}

	void sendAllUpdatePacket() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			List<SimpleTilePacket> packets = Lists.newArrayList();
			for (PacketType type : PacketType.values()){
				List<Position> positionList = positionListMap.get(type);
				if(positionList != null){
					for (Position position : positionList){
						TileEntity tile = MinecraftServer.getServer().getEntityWorld().getTileEntity(position.x, position.y, position.z);
						if(tile instanceof ISimpleTilePacketUser){
							SimpleTilePacket packet = ((ISimpleTilePacketUser) tile).getPacket(type);
							if(packet != null){
								//								packetDispatcher.sendToAll(packet);
								packets.add(packet);
							}
						}
					}
					positionList.clear();
				}
			}
			if(!packets.isEmpty()){
				WorldUpdatePacket packet = new WorldUpdatePacket(packets);
				packetDispatcher.sendToAll(packet);
			}
		}
	}

	public void sendPacket(SimpleTilePacket packet) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			packetDispatcher.sendToAll(packet);
		}else{
			packetDispatcher.sendToServer(packet);
		}
	}

	public void sendMultiBlockPacket(MultiBlockPacket packet) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			packetDispatcher.sendToAll(packet);
		}
	}

	public void sendCommandPacket(CommandPacket packet, EntityPlayerMP player) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			packetDispatcher.sendTo(packet, player);
		}
	}

	protected void spawnParticle(World world, int id, Object... objects) {}

}
