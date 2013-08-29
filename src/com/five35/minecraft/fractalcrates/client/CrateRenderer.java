package com.five35.minecraft.fractalcrates.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class CrateRenderer implements ISimpleBlockRenderingHandler {
	private final int renderId;

	private static boolean renderSide(final IBlockAccess world, final int x, final int y, final int z, final int side, final Block block, final RenderBlocks renderer) {
		final ForgeDirection dir = ForgeDirection.getOrientation(side);
		final int sideX = x + dir.offsetX;
		final int sideY = y + dir.offsetY;
		final int sideZ = z + dir.offsetZ;

		if (world == null || block.shouldSideBeRendered(world, sideX, sideY, sideZ, side)) {
			final Tessellator tessellator = Tessellator.instance;

			if (world == null) {
				tessellator.startDrawingQuads();
			} else {
				final float color = side == 0 ? 0.5f : side == 1 ? 1.0f : side < 4 ? 0.8f : 0.6f;

				tessellator.setBrightness(block.getMixedBrightnessForBlock(world, sideX, sideY, sideZ));
				tessellator.setColorOpaque_F(color, color, color);
			}

			switch (side) {
				case 0:
					if (world == null) {
						tessellator.setNormal(0, -1, 0);
					}

					renderer.renderFaceYNeg(block, x, y, z, block.getIcon(side, 0));

					break;

				case 1:
					if (world == null) {
						tessellator.setNormal(0, 1, 0);
					}

					renderer.renderFaceYPos(block, x, y, z, block.getIcon(side, 0));

					break;

				case 2:
					if (world == null) {
						tessellator.setNormal(0, 0, -1);
					}

					renderer.renderFaceZNeg(block, x, y, z, block.getIcon(side, 0));

					break;

				case 3:
					if (world == null) {
						tessellator.setNormal(0, 0, 1);
					}

					renderer.renderFaceZPos(block, x, y, z, block.getIcon(side, 0));

					break;

				case 4:
					if (world == null) {
						tessellator.setNormal(-1, 0, 0);
					}

					renderer.renderFaceXNeg(block, x, y, z, block.getIcon(side, 0));

					break;

				default: // 5
					if (world == null) {
						tessellator.setNormal(1, 0, 0);
					}

					renderer.renderFaceXPos(block, x, y, z, block.getIcon(side, 0));

					break;
			}

			if (world == null) {
				tessellator.draw();
			}

			return true;
		}

		return false;
	}

	public CrateRenderer(final int renderId) {
		this.renderId = renderId;
	}

	@Override
	public int getRenderId() {
		return this.renderId;
	}

	@Override
	public void renderInventoryBlock(final Block block, final int metadata, final int modelId, final RenderBlocks renderer) {
		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, -0.5, -0.5);
		this.renderWorldBlock(null, 0, 0, 0, block, modelId, renderer);
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
		boolean rendered = false;

		// all faces other than YPos render normally
		rendered |= CrateRenderer.renderSide(world, x, y, z, 0, block, renderer);
		rendered |= CrateRenderer.renderSide(world, x, y, z, 2, block, renderer);
		rendered |= CrateRenderer.renderSide(world, x, y, z, 3, block, renderer);
		rendered |= CrateRenderer.renderSide(world, x, y, z, 4, block, renderer);
		rendered |= CrateRenderer.renderSide(world, x, y, z, 5, block, renderer);

		if (world == null || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
			final Icon icon = block.getIcon(1, 0);
			final Tessellator tessellator = Tessellator.instance;

			final double outerWest = x;
			final double innerWest = x + 1 / 16.0;
			final double outerEast = x + 1;
			final double innerEast = x + 15 / 16.0;
			final double outerNorth = z;
			final double innerNorth = z + 1 / 16.0;
			final double outerSouth = z + 1;
			final double innerSouth = z + 15 / 16.0;

			final double bottom = y + 1 / 16.0;

			final double u0 = icon.getMinU();
			final double u1 = icon.getInterpolatedU(1);
			final double u15 = icon.getInterpolatedU(15);
			final double u16 = icon.getMaxU();
			final double v0 = icon.getMinV();
			final double v1 = icon.getInterpolatedV(1);
			final double v15 = icon.getInterpolatedV(15);
			final double v16 = icon.getMaxV();

			if (world == null) {
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 1, 0);
			} else {
				tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y + 1, z));
				tessellator.setColorOpaque_F(1, 1, 1);
			}

			// west rim
			tessellator.addVertexWithUV(outerWest, y + 1, innerSouth, u0, v15);
			tessellator.addVertexWithUV(innerWest, y + 1, innerSouth, u1, v15);
			tessellator.addVertexWithUV(innerWest, y + 1, outerNorth, u1, v0);
			tessellator.addVertexWithUV(outerWest, y + 1, outerNorth, u0, v0);

			// east rim
			tessellator.addVertexWithUV(innerEast, y + 1, outerSouth, u15, v16);
			tessellator.addVertexWithUV(outerEast, y + 1, outerSouth, u16, v16);
			tessellator.addVertexWithUV(outerEast, y + 1, innerNorth, u16, v1);
			tessellator.addVertexWithUV(innerEast, y + 1, innerNorth, u15, v1);

			// north rim
			tessellator.addVertexWithUV(innerWest, y + 1, innerNorth, u1, v1);
			tessellator.addVertexWithUV(outerEast, y + 1, innerNorth, u16, v1);
			tessellator.addVertexWithUV(outerEast, y + 1, outerNorth, u16, v0);
			tessellator.addVertexWithUV(innerWest, y + 1, outerNorth, u1, v0);

			// south rim
			tessellator.addVertexWithUV(outerWest, y + 1, outerSouth, u0, v16);
			tessellator.addVertexWithUV(innerEast, y + 1, outerSouth, u15, v16);
			tessellator.addVertexWithUV(innerEast, y + 1, innerSouth, u15, v15);
			tessellator.addVertexWithUV(outerWest, y + 1, innerSouth, u0, v15);

			if (world != null) {
				tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y + 1, z) - 1);
			}

			// bottom
			tessellator.addVertexWithUV(innerWest, bottom, innerSouth, u1, v15);
			tessellator.addVertexWithUV(innerEast, bottom, innerSouth, u15, v15);
			tessellator.addVertexWithUV(innerEast, bottom, innerNorth, u15, v1);
			tessellator.addVertexWithUV(innerWest, bottom, innerNorth, u1, v1);

			if (world == null) {
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(1, 0, 0);
			} else {
				tessellator.setColorOpaque_F(0.6f, 0.6f, 0.6f);
			}

			// west wall
			tessellator.addVertexWithUV(innerWest, bottom, innerSouth, u1, v15);
			tessellator.addVertexWithUV(innerWest, bottom, innerNorth, u15, v15);
			tessellator.addVertexWithUV(innerWest, y + 1, innerNorth, u15, v0);
			tessellator.addVertexWithUV(innerWest, y + 1, innerSouth, u1, v0);

			if (world == null) {
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(-1, 0, 0);
			}

			// east wall
			tessellator.addVertexWithUV(innerEast, bottom, innerNorth, u1, v15);
			tessellator.addVertexWithUV(innerEast, bottom, innerSouth, u15, v15);
			tessellator.addVertexWithUV(innerEast, y + 1, innerSouth, u15, v0);
			tessellator.addVertexWithUV(innerEast, y + 1, innerNorth, u1, v0);

			if (world == null) {
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, 1);
			} else {
				tessellator.setColorOpaque_F(0.8f, 0.8f, 0.8f);
			}

			// north wall
			tessellator.addVertexWithUV(innerWest, bottom, innerNorth, u1, v15);
			tessellator.addVertexWithUV(innerEast, bottom, innerNorth, u15, v15);
			tessellator.addVertexWithUV(innerEast, y + 1, innerNorth, u15, v0);
			tessellator.addVertexWithUV(innerWest, y + 1, innerNorth, u1, v0);

			if (world == null) {
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, -1);
			}

			// south wall
			tessellator.addVertexWithUV(innerEast, bottom, innerSouth, u1, v15);
			tessellator.addVertexWithUV(innerWest, bottom, innerSouth, u15, v15);
			tessellator.addVertexWithUV(innerWest, y + 1, innerSouth, u15, v0);
			tessellator.addVertexWithUV(innerEast, y + 1, innerSouth, u1, v0);

			if (world == null) {
				tessellator.draw();
			}
		}

		return rendered;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}
}
