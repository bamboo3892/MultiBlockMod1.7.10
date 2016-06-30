package com.okina.main;

import org.lwjgl.input.Keyboard;

import com.okina.client.IHUDUser;
import com.okina.client.IToolTipUser;
import com.okina.utils.UtilMethods;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class EventHandler {

	//	@SideOnly(Side.CLIENT)
	//	@SubscribeEvent
	//	public void inputMouse(InputEvent.MouseInputEvent event) {
	//		//		if(Mouse.isButtonDown(2)){
	//		//			FMLClientHandler.instance().getClient().thePlayer.addChatMessage(new ChatComponentTranslation("Mouse Wheel is Pressed"));
	//		//		}
	//	}

	

	@SubscribeEvent
	public void toolTip(ItemTooltipEvent event) {
		Item item = event.itemStack.getItem();
		if(item instanceof IToolTipUser){
			((IToolTipUser) item).addToolTip(event.toolTip, event.itemStack, event.entityPlayer, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT), event.showAdvancedItemTooltips);
		}else if(item != null){
			Block block = Block.getBlockFromItem(item);
			if(block instanceof IToolTipUser){
				((IToolTipUser) block).addToolTip(event.toolTip, event.itemStack, event.entityPlayer, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT), event.showAdvancedItemTooltips);
			}
		}

		if(item != null){
			if(item instanceof IToolTipUser || (item instanceof ItemBlock && Block.getBlockFromItem(item) instanceof IToolTipUser)){
				IToolTipUser tooltip;
				if(item instanceof IToolTipUser){
					tooltip = (IToolTipUser) item;
				}else{
					tooltip = (IToolTipUser) Block.getBlockFromItem(item);
				}
				for (int i = 0; i < tooltip.getNeutralLines(); i++){
					event.toolTip.add(StatCollector.translateToLocal("tooltip." + item.getUnlocalizedName() + "." + i));
				}

				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
					for (int i = 0; i < tooltip.getShiftLines(); i++){
						event.toolTip.add(StatCollector.translateToLocal("tooltipshift." + item.getUnlocalizedName() + "." + i));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void registerOreEvent(final OreRegisterEvent event) {
		IntegrationHandler.registerOre(event.Name);
	}

	@SubscribeEvent
	public void interactTest(EntityInteractEvent event) {}

	@SubscribeEvent
	public void onCraftedHook(PlayerEvent.ItemCraftedEvent event) {}

	@SubscribeEvent
	public void onSmeltedHook(PlayerEvent.ItemSmeltedEvent event) {}

	@SubscribeEvent
	public void onPickedUpHook(PlayerEvent.ItemPickupEvent event) {}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(event.phase == Phase.END){
			TestCore.proxy.sendAllUpdatePacket();
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {}

	private IHUDUser pastRenderedObject = null;
	private MovingObjectPosition pastMOP = null;
	private double renderStartTicks = 0;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(!mc.isGamePaused() && mc.currentScreen == null && mc.thePlayer != null){
			IHUDUser renderObj = null;
			MovingObjectPosition mop = UtilMethods.getMovingObjectPositionFromPlayer(mc.theWorld, mc.thePlayer, true);
			if(mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof IHUDUser){
				renderObj = (IHUDUser) mc.thePlayer.getCurrentEquippedItem().getItem();
			}else{
				if(mop != null && mop.sideHit != -1 && mop.typeOfHit == MovingObjectType.BLOCK){
					if(mc.theWorld.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof IHUDUser){
						renderObj = (IHUDUser) mc.theWorld.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
					}else if(mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof IHUDUser){
						renderObj = (IHUDUser) mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ);
					}
				}
			}
			if(renderObj != null){
				if(renderObj.comparePastRenderObj(pastRenderedObject, pastMOP, mop)){
					double tick = mc.theWorld.getTotalWorldTime() % 72000 + event.renderTickTime;
					renderObj.renderHUD(mc, tick - renderStartTicks, mop);
					return;
				}else{
					renderObj.renderHUD(mc, 0.0D, mop);
					pastRenderedObject = renderObj;
					pastMOP = mop;
					renderStartTicks = mc.theWorld.getTotalWorldTime() % 72000 + event.renderTickTime;
					return;
				}
			}
		}
		pastRenderedObject = null;
		pastMOP = null;
	}

}
