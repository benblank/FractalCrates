package com.five35.minecraft.fractalcrates.client;

import com.five35.minecraft.fractalcrates.CrateTileEntity;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CrateRenderer extends TileEntitySpecialRenderer implements IItemRenderer, ISimpleBlockRenderingHandler {
	private final EntityItem itemEntity = new EntityItem(null);

	private final RenderItem itemRenderer = new RenderItem() {
		@Override
		public boolean shouldBob() {
			return false;
		}
	};

	protected final int renderId;

	private static boolean renderBlock(final BlockRenderHelper helper) {
		return CrateRenderer.renderBlock(helper, null);
	}

	private static boolean renderBlock(final BlockRenderHelper helper, final Icon override) {
		boolean rendered = false;

		final Icon icon = override != null ? override : helper.getIcon(ForgeDirection.DOWN);

		// full faces
		rendered |= helper.renderFace(ForgeDirection.DOWN, icon);
		rendered |= helper.renderFace(ForgeDirection.NORTH, icon);
		rendered |= helper.renderFace(ForgeDirection.SOUTH, icon);
		rendered |= helper.renderFace(ForgeDirection.WEST, icon);
		rendered |= helper.renderFace(ForgeDirection.EAST, icon);

		// rim
		rendered |= helper.renderQuad(ForgeDirection.UP, icon, 0, 0, 0, 15, 1);
		rendered |= helper.renderQuad(ForgeDirection.UP, icon, 0, 0, 1, 1, 16);
		rendered |= helper.renderQuad(ForgeDirection.UP, icon, 0, 1, 15, 16, 16);
		rendered |= helper.renderQuad(ForgeDirection.UP, icon, 0, 15, 0, 16, 15);

		// "bowl"
		rendered |= helper.renderQuad(ForgeDirection.UP, icon, 15, 1, 1, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.NORTH, icon, 15, 1, 0, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.SOUTH, icon, 15, 1, 0, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.WEST, icon, 15, 1, 0, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.EAST, icon, 15, 1, 0, 15, 15);

		return rendered;
	}

	public CrateRenderer(final int renderId) {
		this.renderId = renderId;

		this.itemEntity.hoverStart = 0;
		this.itemRenderer.setRenderManager(RenderManager.instance);
	}

	@Override
	public int getRenderId() {
		return this.renderId;
	}

	@Override
	public boolean handleRenderType(final ItemStack item, final ItemRenderType type) {
		return true;
	}

	@Override
	public void renderInventoryBlock(final Block block, final int metadata, final int modelId, final RenderBlocks renderer) {
		CrateRenderer.renderBlock(new InventoryBlockRenderHelper(block, metadata));
	}

	@Override
	public void renderItem(final ItemRenderType type, final ItemStack stack, final Object... data) {
		if (type == ItemRenderType.ENTITY) {
			GL11.glPushMatrix();
			GL11.glTranslated(-0.5, -0.5, -0.5);
		}

		CrateRenderer.renderBlock(new InventoryBlockRenderHelper(Block.blocksList[stack.itemID], stack.getItemDamage()));

		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Contents")) {
			final ItemStack contents = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("Contents"));

			contents.stackSize = 1;

			GL11.glPushMatrix();

			final Block block = contents.itemID < Block.blocksList.length ? Block.blocksList[contents.itemID] : null;

			if (contents.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
				GL11.glTranslated(0.5, 0.5, 0.5);
				GL11.glScaled(3.3, 3.3, 3.3);
			} else {
				GL11.glTranslated(0, 1, 0);
				GL11.glRotated(-90, 1, 0, 0);
				GL11.glRotated(180, 1, 1, 0);
				GL11.glTranslated(-1, 1, 0);

				GL11.glTranslated(0.5, -0.5, 0.5);
				GL11.glScaled(0.875, 0.875, 0.875);
				GL11.glTranslated(-0.5, 0.5, -0.5);

				// reverse the transforms applied by the item renderer
				GL11.glTranslated(0.5, -0.75, 0.35 / 16);
				GL11.glScaled(2, 2, 2);
			}

			this.renderStack(contents);

			GL11.glPopMatrix();
		}

		if (type == ItemRenderType.ENTITY) {
			GL11.glPopMatrix();
		} else if (type == ItemRenderType.INVENTORY) {
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	private void renderStack(final ItemStack stack) {
		try {
			this.itemEntity.setEntityItemStack(stack);
			this.itemRenderer.doRenderItem(this.itemEntity, 0, 0, 0, 0, 0);
		} catch (final NullPointerException ex) {
			// the render manager's render engine is null when rendering held items before the world is drawn
		}
	}

	@Override
	public void renderTileEntityAt(final TileEntity entity, final double x, final double y, final double z, final float time) {
		if (!(entity instanceof CrateTileEntity)) {
			return;
		}

		final CrateTileEntity crateEntity = (CrateTileEntity) entity;
		final ItemStack contents = crateEntity.getStackInSlot(0);

		if (contents == null || contents.stackSize == 0) {
			return;
		}

		final ItemStack copy = contents.copy();
		copy.stackSize = 1;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		final Block block = contents.itemID < Block.blocksList.length ? Block.blocksList[contents.itemID] : null;

		if (contents.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
			// blocks render three "pixels" wide, with 2/5 of a pixel spacing between them
			// 2.9 = 1 pixel of crate wall + .4 pixel gap + 1.5 pixels to offset entity origin (half their width)
			double offset = 2.9 / 16;
			GL11.glTranslated(offset, offset, offset);

			// blocks normally render at 1/4 size; we need 3/16
			GL11.glScaled(0.75, 0.75, 0.75);

			// 3.4 = 3 pixels per block + .4 pixel gap
			offset = 3.4 / 12;

			for (int i = 0; i < contents.stackSize; i++) {
				GL11.glPushMatrix();

				final int itemX = i & 3;
				final int itemY = i >> 4;
				final int itemZ = i >> 2 & 3;

				GL11.glTranslated(itemX * offset, itemY * offset, itemZ * offset);

				this.renderStack(copy);

				GL11.glPopMatrix();
			}
		} else {
			// shrink to only cover the 13⅓x13⅓x13⅓ pixels in the center of the block
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(5 / 6d, 5 / 6d, 5 / 6d);
			GL11.glTranslated(-0.5, -0.5, -0.5);

			// scale to half size (four quadrants)
			GL11.glScaled(0.5, 0.5, 0.5);

			for (int i = 0; i < contents.stackSize; i++) {
				GL11.glPushMatrix();

				// add four items to each quadrant before moving on to the next
				// fill all quadrants before moving to the next layer
				final int itemX = i >> 2 & 1;
				final int itemY = (i >> 4) * 4 + (i & 3);
				final int itemZ = i >> 3 & 1;

				// 4/31 arranges the vertical stack so that the top of the
				// topmost items coincides with the top of the block and the
				// bottom of the bottommost items coincides with the bottom of
				// the block
				GL11.glTranslated(itemX, itemY * 4 / 31d, itemZ);

				// shrink down to a total of 6 pixels wide (from 6⅔) and rotate
				// so that it will lay face-up
				GL11.glTranslated(0.5, 0.5, 0.5);
				GL11.glScaled(0.9, 0.9, 0.9);
				GL11.glRotated(180, 0, 0, 1);
				GL11.glTranslated(-0.5, 0.5, -0.5);

				// lay the item flat
				GL11.glRotated(-90, 1, 0, 0);

				// reverse the transforms applied by the item renderer
				GL11.glTranslated(0.5, -0.75, 0.35 / 16);
				GL11.glScaled(2, 2, 2);

				this.renderStack(copy);

				GL11.glPopMatrix();
			}
		}

		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
		return CrateRenderer.renderBlock(new WorldBlockRenderHelper(world, x, y, z), renderer.overrideBlockTexture);
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item, final ItemRendererHelper helper) {
		return true;
	}
}
