package com.five35.minecraft.fractalcrates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CrateContainer extends Container {
	public final static int WIDTH = 176;
	public final static int HEIGHT = 166;

	public final EntityPlayer player;
	public final CrateTileEntity crate;

	public CrateContainer(final EntityPlayer player, final CrateTileEntity crate) {
		this.player = player;
		this.crate = crate;

		// crates don't actually care about this, but it's good form
		crate.openChest();

		final GuiHelper helper = new GuiHelper(CrateContainer.WIDTH, CrateContainer.HEIGHT);

		for (final Slot slot : helper.getInventorySlots(crate, 1, 1)) {
			this.addSlotToContainer(slot);
		}

		for (final Slot slot : helper.getPlayerSlots(player)) {
			this.addSlotToContainer(slot);
		}
	}

	@Override
	public boolean canInteractWith(@SuppressWarnings("hiding") final EntityPlayer player) {
		return this.crate.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(@SuppressWarnings("hiding") final EntityPlayer player, final int slotIndex) {
		final Slot slot = (Slot) this.inventorySlots.get(slotIndex);
		ItemStack result = null;

		if (slot != null && slot.getHasStack()) {
			final ItemStack stack = slot.getStack();

			result = stack.copy();

			if (slotIndex == 0) {
				// coming from crate

				if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), true)) {
					return null;
				}
			} else {
				// going into crate

				if (!this.mergeItemStack(stack, 0, 1, false)) {
					return null;
				}
			}

			if (stack.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (stack.stackSize == result.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, stack);
		}

		return result;
	}
}
