package com.five35.minecraft.fractalcrates;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class GuiHelper {
	private static final int SLOT_SIZE = 18;

	private final int width;
	private final int height;
	private final int border;
	private final int padding;
	private final int textHeight;

	public static String getInventoryName(final IInventory inventory) {
		return inventory.isInvNameLocalized() ? inventory.getInvName() : I18n.func_135053_a(inventory.getInvName());
	}

	public static List<Slot> getInventorySlots(final IInventory inventory, final int offset, final int cols, final int rows, final int left, final int top) {
		final List<Slot> slots = new ArrayList<Slot>();

		for (int row = 0; row < rows; row++) {
			final int rowTop = top + row * GuiHelper.SLOT_SIZE + 1;

			for (int col = 0; col < cols; col++) {
				slots.add(new Slot(inventory, row * cols + col + offset, left + col * GuiHelper.SLOT_SIZE + 1, rowTop));
			}
		}

		return slots;
	}

	public GuiHelper(final int width, final int height) {
		this(width, height, 3, 4, 13);
	}

	public GuiHelper(final int width, final int height, final int border, final int padding, final int textHeight) {
		this.width = width;
		this.height = height;
		this.border = border;
		this.padding = padding;
		this.textHeight = textHeight;
	}

	public List<Slot> getInventorySlots(final IInventory inventory) {
		return new ArrayList<Slot>();
	}

	public List<Slot> getPlayerSlots(final EntityPlayer player) {
		final List<Slot> slots = new ArrayList<Slot>();
		final int left = this.border + this.padding;
		final int top = this.height - this.border - this.padding - GuiHelper.SLOT_SIZE;

		slots.addAll(GuiHelper.getInventorySlots(player.inventory, 0, 9, 1, left, top));
		slots.addAll(GuiHelper.getInventorySlots(player.inventory, 9, 9, 3, left, top - this.padding - GuiHelper.SLOT_SIZE * 3));

		return slots;
	}
}
