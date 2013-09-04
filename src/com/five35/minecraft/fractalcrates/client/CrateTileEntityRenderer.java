package com.five35.minecraft.fractalcrates.client;

import com.five35.minecraft.fractalcrates.CrateTileEntity;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class CrateTileEntityRenderer extends TileEntitySpecialRenderer {
	private final EntityItem itemEntity = new EntityItem(null);

	private final RenderItem itemRenderer = new RenderItem() {
		@Override
		public boolean shouldBob() {
			return false;
		}
	};

	public CrateTileEntityRenderer() {
		this.itemEntity.hoverStart = 0;
		this.itemRenderer.setRenderManager(RenderManager.instance);
	}

	@Override
	public void renderTileEntityAt(final TileEntity entity, final double x, final double y, final double z, final float time) {
		if (!(entity instanceof CrateTileEntity)) {
			return;
		}

		final CrateTileEntity crateEntity = (CrateTileEntity) entity;
		final ItemStack stack = crateEntity.getStackInSlot(0);

		if (stack == null || stack.stackSize == 0) {
			return;
		}

		final ItemStack copy = stack.copy();
		copy.stackSize = 1;

		this.itemEntity.setEntityItemStack(copy);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		for (int i = 0; i < stack.stackSize; i++) {
			GL11.glPushMatrix();

			final int itemX = i & 3;
			final int itemY = i >> 4;
			final int itemZ = i >> 2 & 3;

			// blocks render three "pixels" wide, with 2/5 of a pixel spacing between them
			GL11.glTranslated(2.9 / 16 + itemX * 3.4 / 16, 2.9 / 16 + itemY * 3.4 / 16, 2.9 / 16 + itemZ * 3.4 / 16);
			GL11.glScaled(0.75, 0.75, 0.75);

			this.itemRenderer.doRenderItem(this.itemEntity, 0, 0, 0, 0, 0);

			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();
	}
}
