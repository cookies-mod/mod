package dev.morazzer.cookies.mod.render.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.Cull;
import net.minecraft.client.render.RenderPhase.DepthTest;
import net.minecraft.client.render.RenderPhase.Transparency;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;

/**
 * Render layers for the mod renderer.
 */
public class CookiesRenderLayers {

    private static final Transparency DEFAULT_TRANSPARENCY = new Transparency("default_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }, RenderSystem::disableBlend);

    /**
     * A render layer that works like the normal block layer.
     */
    public static final MultiPhase FILLED =
        RenderLayer.of("filled", VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP, RenderLayer.CUTOUT_BUFFER_SIZE,
            false, true, MultiPhaseParameters.builder()
                .program(RenderPhase.POSITION_COLOR_PROGRAM)
                .cull(Cull.DISABLE_CULLING)
                .layering(RenderPhase.POLYGON_OFFSET_LAYERING)
                .transparency(DEFAULT_TRANSPARENCY)
                .depthTest(DepthTest.LEQUAL_DEPTH_TEST)
                .build(false));

    /**
     * A layer that works like the normal layer but also renders through walls.
     */
    public static final MultiPhase FILLED_THROUGH_WALLS =
        RenderLayer.of("filled_through_walls", VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP,
            RenderLayer.CUTOUT_BUFFER_SIZE, false, true, MultiPhaseParameters.builder()
                .program(RenderPhase.POSITION_COLOR_PROGRAM)
                .cull(Cull.DISABLE_CULLING)
                .layering(RenderPhase.POLYGON_OFFSET_LAYERING)
                .transparency(DEFAULT_TRANSPARENCY)
                .depthTest(DepthTest.ALWAYS_DEPTH_TEST)
                .build(false));

}
