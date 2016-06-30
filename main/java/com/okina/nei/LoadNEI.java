package com.okina.nei;

import codechicken.nei.api.API;

public class LoadNEI {

	public static CrusherRecipeHandler CrusherRecipeCatch;
	private static EnergyProduceRecipeHandler EnergyProduceRecipeCatch;
	private static VirtualGrowerRecipeHandler VirtualGrowerRecipeCatch;
	private static AlterRecipeHandler AlterRecipeCatch;

	public static void loadNEI() {
		try{
			CrusherRecipeCatch = new CrusherRecipeHandler();
			API.registerRecipeHandler(CrusherRecipeCatch);
			API.registerUsageHandler(CrusherRecipeCatch);
			API.registerGuiOverlay(GuiDummy.class, CrusherRecipeCatch.getOverlayIdentifier(), 0, 0);
			EnergyProduceRecipeCatch = new EnergyProduceRecipeHandler();
			API.registerRecipeHandler(EnergyProduceRecipeCatch);
			API.registerUsageHandler(EnergyProduceRecipeCatch);
			API.registerGuiOverlay(GuiDummy.class, EnergyProduceRecipeCatch.getOverlayIdentifier(), 0, 0);
			VirtualGrowerRecipeCatch = new VirtualGrowerRecipeHandler();
			API.registerRecipeHandler(VirtualGrowerRecipeCatch);
			API.registerUsageHandler(VirtualGrowerRecipeCatch);
			API.registerGuiOverlay(GuiDummy.class, VirtualGrowerRecipeCatch.getOverlayIdentifier(), 0, 0);
			AlterRecipeCatch = new AlterRecipeHandler();
			API.registerRecipeHandler(AlterRecipeCatch);
			API.registerUsageHandler(AlterRecipeCatch);
			API.registerGuiOverlay(GuiDummy.class, AlterRecipeCatch.getOverlayIdentifier(), 0, 0);
		}catch (Exception e){
			e.printStackTrace(System.err);
		}
	}

}
