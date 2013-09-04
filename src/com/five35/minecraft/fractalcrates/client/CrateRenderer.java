package com.five35.minecraft.fractalcrates.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public class CrateRenderer implements ISimpleBlockRenderingHandler {
	private final int renderId;

	public CrateRenderer(final int renderId) {
		this.renderId = renderId;
	}

	@Override
	public int getRenderId() {
		return this.renderId;
	}

	@Override
	public void renderInventoryBlock(final Block block, final int metadata, final int modelId, final RenderBlocks renderer) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
		boolean rendered = false;

		final BlockRenderHelper helper = new WorldBlockRenderHelper(block, world, x, y, z);

		// full faces
		rendered |= helper.renderFace(ForgeDirection.DOWN);
		rendered |= helper.renderFace(ForgeDirection.NORTH);
		rendered |= helper.renderFace(ForgeDirection.SOUTH);
		rendered |= helper.renderFace(ForgeDirection.WEST);
		rendered |= helper.renderFace(ForgeDirection.EAST);

		// rim
		rendered |= helper.renderQuad(ForgeDirection.UP, 0, 0, 0, 15, 1);
		rendered |= helper.renderQuad(ForgeDirection.UP, 0, 0, 1, 1, 16);
		rendered |= helper.renderQuad(ForgeDirection.UP, 0, 1, 15, 16, 16);
		rendered |= helper.renderQuad(ForgeDirection.UP, 0, 15, 0, 16, 15);

		// "bowl"
		rendered |= helper.renderQuad(ForgeDirection.UP, 15, 1, 1, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.NORTH, 15, 1, 0, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.SOUTH, 15, 1, 0, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.WEST, 15, 1, 0, 15, 15);
		rendered |= helper.renderQuad(ForgeDirection.EAST, 15, 1, 0, 15, 15);

		return rendered;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}
}
