package com.five35.minecraft.fractalcrates.client;

import com.five35.minecraft.fractalcrates.CrateContainer;
import com.five35.minecraft.fractalcrates.GuiHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CrateGui extends GuiContainer {
	public CrateGui(final CrateContainer container) {
		super(container);

		this.xSize = CrateContainer.WIDTH;
		this.ySize = CrateContainer.HEIGHT;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int i, final int j) {
		GL11.glColor4f(1, 1, 1, 1);

		this.mc.renderEngine.func_110577_a(new ResourceLocation("fractalcrates:textures/gui/crate.png"));
		this.drawTexturedModalRect((this.width - this.xSize) / 2, (this.height - this.ySize) / 2, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
		final CrateContainer container = (CrateContainer) this.inventorySlots;

		this.fontRenderer.drawString(GuiHelper.getInventoryName(container.crate), 8, 6, 0x404040);
		this.fontRenderer.drawString(GuiHelper.getInventoryName(container.player.inventory), 8, this.ySize - 96 + 2, 0x404040);
	}
}
