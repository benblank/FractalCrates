package com.five35.minecraft.fractalcrates.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public class RenderHelper {
	private static class Offset {
		private static final Map<Integer, Offset> cache = new HashMap<Integer, Offset>();

		public final int x;
		public final int y;
		public final int z;

		public static Offset create(final ForgeDirection dir) {
			return Offset.get(dir.offsetX, dir.offsetY, dir.offsetZ);
		}

		public static Offset get(final int x, final int y, final int z) {
			final Integer hash = Integer.valueOf(Offset.hashCode(x, y, z));

			if (!Offset.cache.containsKey(hash)) {
				Offset.cache.put(hash, new Offset(x, y, z));
			}

			return Offset.cache.get(hash);
		}

		private static int hashCode(final int x, final int y, final int z) {
			return 31 * (31 * (31 + x) + y) + z;
		}

		private Offset(final int x, final int y, final int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Offset add(final ForgeDirection dir) {
			return Offset.get(this.x + dir.offsetX, this.y + dir.offsetY, this.z + dir.offsetZ);
		}

		public Offset add(final Offset other) {
			return Offset.get(this.x + other.x, this.y + other.y, this.z + other.z);
		}

		@Override
		public boolean equals(final Object obj) {
			return this == obj;
		}

		@Override
		public int hashCode() {
			return Offset.hashCode(this.x, this.y, this.z);
		}
	}

	private static class Vertex {
		public final double x;
		public final double y;
		public final double z;

		private static double dim(final double pos, final int offset) {
			return (offset > 0 ? 1 : 0) + pos * offset * -1;
		}

		public Vertex(final ForgeDirection dir, final double depth, final double x, final double y) {
			final ForgeDirection topDir = RenderHelper.dirTop.get(dir);
			final ForgeDirection leftDir = topDir.getRotation(dir.getOpposite());

			this.x = Vertex.dim(depth, dir.offsetX) + Vertex.dim(x, leftDir.offsetX) + Vertex.dim(y, topDir.offsetX);
			this.y = Vertex.dim(depth, dir.offsetY) + Vertex.dim(x, leftDir.offsetY) + Vertex.dim(y, topDir.offsetY);
			this.z = Vertex.dim(depth, dir.offsetZ) + Vertex.dim(x, leftDir.offsetZ) + Vertex.dim(y, topDir.offsetZ);
		}
	}

	final static Map<ForgeDirection, ForgeDirection> dirTop = new HashMap<ForgeDirection, ForgeDirection>();

	static {
		RenderHelper.dirTop.put(ForgeDirection.DOWN, ForgeDirection.NORTH);
		RenderHelper.dirTop.put(ForgeDirection.UP, ForgeDirection.NORTH);
		RenderHelper.dirTop.put(ForgeDirection.NORTH, ForgeDirection.UP);
		RenderHelper.dirTop.put(ForgeDirection.SOUTH, ForgeDirection.UP);
		RenderHelper.dirTop.put(ForgeDirection.WEST, ForgeDirection.UP);
		RenderHelper.dirTop.put(ForgeDirection.EAST, ForgeDirection.UP);
	}

	private final Map<Offset, Boolean> blocksLight = new HashMap<Offset, Boolean>();
	private final Map<Offset, Integer> light = new HashMap<Offset, Integer>();
	private final Map<Offset, Float> occlusion = new HashMap<Offset, Float>();
	private final Map<Offset, Boolean> opaqueCube = new HashMap<Offset, Boolean>();

	private final Block block;
	private final int metadata;
	private final IBlockAccess world;
	private final int x;
	private final int y;
	private final int z;
	private final boolean useOcclusion;

	// special renderers (e.g. grass) may want to set these explicitly or per-quad
	public float red = 1;
	public float green = 1;
	public float blue = 1;

	public RenderHelper(final Block block) {
		this(block, 0);
	}

	public RenderHelper(final Block block, final IBlockAccess world, final int x, final int y, final int z) {
		this(block, world.getBlockMetadata(x, y, z), world, x, y, z);
	}

	public RenderHelper(final Block block, final int metadata) {
		this(block, metadata, null, 0, 0, 0);
	}

	private RenderHelper(final Block block, final int metadata, final IBlockAccess world, final int x, final int y, final int z) {
		this.block = block;
		this.metadata = metadata;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.useOcclusion = Minecraft.isAmbientOcclusionEnabled() && Block.lightValue[block.blockID] == 0;

		if (world != null) {
			final int color = block.colorMultiplier(world, x, y, z);

			this.red = (color >> 16 & 255) / 255f;
			this.green = (color >> 8 & 255) / 255f;
			this.blue = (color & 255) / 255f;
		}
	}

	private boolean doesBlockLight(final Offset offset) {
		if (!this.blocksLight.containsKey(offset)) {
			this.blocksLight.put(offset, Boolean.valueOf(!Block.canBlockGrass[this.world.getBlockId(this.x + offset.x, this.y + offset.y, this.z + offset.z)]));
		}

		return this.blocksLight.get(offset).booleanValue();
	}

	private int getLight(final Offset offset) {
		if (!this.light.containsKey(offset)) {
			this.light.put(offset, Integer.valueOf(this.block.getMixedBrightnessForBlock(this.world, this.x + offset.x, this.y + offset.y, this.z + offset.z)));
		}

		return this.light.get(offset).intValue();
	}

	private float getOcclusion(final Offset offset) {
		if (!this.occlusion.containsKey(offset)) {
			this.occlusion.put(offset, Float.valueOf(this.block.getAmbientOcclusionLightValue(this.world, this.x + offset.x, this.y + offset.y, this.z + offset.z)));
		}

		return this.occlusion.get(offset).floatValue();
	}

	private boolean isOpaqueCube(final Offset offset) {
		if (!this.opaqueCube.containsKey(offset)) {
			this.opaqueCube.put(offset, Boolean.valueOf(this.world.isBlockOpaqueCube(this.x + offset.x, this.y + offset.y, this.z + offset.z)));
		}

		return this.opaqueCube.get(offset).booleanValue();
	}

	public boolean renderFace(final ForgeDirection dir) {
		return this.renderFace(dir, this.block.getIcon(dir.ordinal(), this.metadata));
	}

	public boolean renderFace(final ForgeDirection dir, final Icon icon) {
		// defaults are no depth (at face) and full width/height of face/texture
		return this.renderQuad(dir, icon, 0, 0, 1, 0, 1, 0, 16, 0, 16);
	}

	public boolean renderQuad(final ForgeDirection dir, final Icon icon, final double depth, final double xMin, final double xMax, final double yMin, final double yMax, final double uMin, final double uMax, final double vMin, final double vMax) {
		if (!this.shouldRenderQuad(dir, depth)) {
			return false;
		}

		int trLight = 0xF000F, tlLight = 0xF000F, blLight = 0xF000F, brLight = 0xF000F;
		float trOcclusion = 1, tlOcclusion = 1, blOcclusion = 1, brOcclusion = 1;

		final Offset base = depth <= 0 ? Offset.create(dir) : Offset.get(0, 0, 0);
		final int baseLight = this.getLight(base);

		final ForgeDirection topDir = RenderHelper.dirTop.get(dir);
		final ForgeDirection leftDir = topDir.getRotation(dir.getOpposite());

		if (this.useOcclusion) {
			final float baseOcclusion = this.getOcclusion(Offset.get(0, 0, 0)); // MC doesn't offset occlusion based on depth

			final Offset top = base.add(topDir);
			final Offset left = base.add(leftDir);
			final Offset bottom = base.add(topDir.getOpposite());
			final Offset right = base.add(leftDir.getOpposite());

			final boolean topBlocksLight = this.doesBlockLight(top.add(base));
			final boolean leftBlocksLight = this.doesBlockLight(left.add(base));
			final boolean bottomBlocksLight = this.doesBlockLight(bottom.add(base));
			final boolean rightBlocksLight = this.doesBlockLight(right.add(base));

			trLight = topBlocksLight && rightBlocksLight ? this.getLight(right) : this.getLight(top.add(right));
			tlLight = topBlocksLight && leftBlocksLight ? this.getLight(left) : this.getLight(top.add(left));
			blLight = bottomBlocksLight && leftBlocksLight ? this.getLight(left) : this.getLight(bottom.add(left));
			brLight = bottomBlocksLight && rightBlocksLight ? this.getLight(right) : this.getLight(bottom.add(right));

			int topLight = this.getLight(top);
			int leftLight = this.getLight(left);
			int bottomLight = this.getLight(bottom);
			int rightLight = this.getLight(right);

			topLight = topLight > 0 ? topLight : baseLight;
			leftLight = leftLight > 0 ? leftLight : baseLight;
			bottomLight = bottomLight > 0 ? bottomLight : baseLight;
			rightLight = rightLight > 0 ? rightLight : baseLight;

			trLight = (trLight > 0 ? trLight : baseLight) + topLight + rightLight + baseLight >> 2;
			tlLight = (tlLight > 0 ? tlLight : baseLight) + topLight + leftLight + baseLight >> 2;
			blLight = (blLight > 0 ? blLight : baseLight) + bottomLight + leftLight + baseLight >> 2;
			brLight = (brLight > 0 ? brLight : baseLight) + bottomLight + rightLight + baseLight >> 2;

			trOcclusion = topBlocksLight && rightBlocksLight ? this.getOcclusion(right) : this.getOcclusion(top.add(right));
			tlOcclusion = topBlocksLight && leftBlocksLight ? this.getOcclusion(left) : this.getOcclusion(top.add(left));
			blOcclusion = bottomBlocksLight && leftBlocksLight ? this.getOcclusion(left) : this.getOcclusion(bottom.add(left));
			brOcclusion = bottomBlocksLight && rightBlocksLight ? this.getOcclusion(right) : this.getOcclusion(bottom.add(right));

			trOcclusion = (trOcclusion + this.getOcclusion(top) + this.getOcclusion(right) + baseOcclusion) / 4;
			tlOcclusion = (tlOcclusion + this.getOcclusion(top) + this.getOcclusion(left) + baseOcclusion) / 4;
			blOcclusion = (blOcclusion + this.getOcclusion(bottom) + this.getOcclusion(left) + baseOcclusion) / 4;
			brOcclusion = (brOcclusion + this.getOcclusion(bottom) + this.getOcclusion(right) + baseOcclusion) / 4;
		}

		final Tessellator t = Tessellator.instance;

		if (this.useOcclusion) {
			t.setBrightness(trLight);
			t.setColorOpaque_F(this.red * trOcclusion, this.green * trOcclusion, this.blue * trOcclusion);
		} else {
			t.setBrightness(baseLight);
			t.setColorOpaque_F(this.red, this.green, this.blue);
		}

		final double topV = icon.getInterpolatedV(vMin);
		final double rightU = icon.getInterpolatedU(uMax);
		final double bottomV = icon.getInterpolatedV(vMax);
		final double leftU = icon.getInterpolatedU(uMin);

		Vertex vertex = new Vertex(dir, depth, xMax, yMin);

		t.addVertexWithUV(this.x + vertex.x, this.y + vertex.y, this.z + vertex.z, rightU, topV);

		if (this.useOcclusion) {
			t.setBrightness(tlLight);
			t.setColorOpaque_F(this.red * tlOcclusion, this.green * tlOcclusion, this.blue * tlOcclusion);
		}

		vertex = new Vertex(dir, depth, xMin, yMin);

		t.addVertexWithUV(this.x + vertex.x, this.y + vertex.y, this.z + vertex.z, leftU, topV);

		if (this.useOcclusion) {
			t.setBrightness(blLight);
			t.setColorOpaque_F(this.red * blOcclusion, this.green * blOcclusion, this.blue * blOcclusion);
		}

		vertex = new Vertex(dir, depth, xMin, yMax);

		t.addVertexWithUV(this.x + vertex.x, this.y + vertex.y, this.z + vertex.z, leftU, bottomV);

		if (this.useOcclusion) {
			t.setBrightness(brLight);
			t.setColorOpaque_F(this.red * brOcclusion, this.green * brOcclusion, this.blue * brOcclusion);
		}

		vertex = new Vertex(dir, depth, xMax, yMax);

		t.addVertexWithUV(this.x + vertex.x, this.y + vertex.y, this.z + vertex.z, rightU, bottomV);

		return true;
	}

	private boolean shouldRenderQuad(final ForgeDirection dir, final double depth) {
		if (depth > 0 || this.world == null) {
			return true;
		}

		return !this.isOpaqueCube(Offset.create(dir));
	}
}
