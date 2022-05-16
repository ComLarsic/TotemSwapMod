package com.comlarsic.totemswap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for TotemSwap
 */
public class TotemSwap implements ModInitializer {
	/// The logger for totemswap
	public static final Logger LOGGER = LoggerFactory.getLogger("totemswap");

	@Override
	public void onInitialize() {
		// The keybind for totem swapping
		var keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Swap Totem", 
			InputUtil.Type.KEYSYM, 
			GLFW.GLFW_KEY_EQUAL, 
			"TotemSwap"
		));

		// Register the keybind event
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Check for the keypress
			while (keybind.wasPressed()) {
				// Fetch the player
				var player = client.player;
				// Get the interaction manager
				var interactionManager = client.interactionManager;
				// Swap the totem
				swapTotem(interactionManager, player);
			}
		});
	}

	/** 
	 * Swaps the totem with the item in the off-hand 
	 */
	private static void swapTotem(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player) {
		// The slot id for the totem
		var totemslot = -1;

		// Find a totem in the inventory
		for (var i = 0; i <= 36; i++) {
			// Get the item stack 
			var stack = player.getInventory().getStack(i);
			
			// Assign the slot and break if the item is a totem
			if (stack.getItem().equals(Items.TOTEM_OF_UNDYING)) {
				totemslot = i;
				break;
			}
		}
		
		// Do nothing if no totem is found
		if (totemslot < 0) return;

		// Swap the items
		swapItems(totemslot, 45, player, interactionManager);
	}

	/**
	 * Swaps the two items in the slots
	 */
	private static void swapItems(int slotA, int slotB, ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager) {
		// Take the item to swap to
		interactionManager.clickSlot(0, slotA, 0, SlotActionType.PICKUP, player);
		// it in the off-hand slot
		interactionManager.clickSlot(0, slotB, 0, SlotActionType.PICKUP, player);
		// back what was in the armor slot (can be air)
		interactionManager.clickSlot(0, slotA, 0, SlotActionType.PICKUP, player);
	}
}
