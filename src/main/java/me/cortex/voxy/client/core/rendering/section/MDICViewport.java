package me.cortex.voxy.client.core.rendering.section;

import me.cortex.voxy.client.core.gl.Capabilities;
import me.cortex.voxy.client.core.gl.GlBuffer;
import me.cortex.voxy.client.core.rendering.Viewport;
import me.cortex.voxy.client.core.rendering.hierachical.HierarchicalOcclusionTraverser;

public class MDICViewport extends Viewport<MDICViewport> {
    public final GlBuffer drawCountCallBuffer = new GlBuffer(1024).zero();
    // Otimização para llvmpipe: reduzir tamanho dos buffers em GPU software
    private static final int DRAW_CALL_COUNT = Capabilities.INSTANCE.isLowEndGPU ? 100_000 : 400_000;
    private static final int TRANSLUCENT_COUNT = Capabilities.INSTANCE.isLowEndGPU ? 25_000 : 100_000;
    private static final int TEMPORAL_COUNT = Capabilities.INSTANCE.isLowEndGPU ? 25_000 : 100_000;
    
    public final GlBuffer drawCallBuffer = new GlBuffer(5*4*(DRAW_CALL_COUNT+TRANSLUCENT_COUNT+TEMPORAL_COUNT)).zero();//Draw calls buffer
    public final GlBuffer positionScratchBuffer  = new GlBuffer(8*DRAW_CALL_COUNT).zero();//Positions scratch
    public final GlBuffer indirectLookupBuffer = new GlBuffer(HierarchicalOcclusionTraverser.MAX_QUEUE_SIZE *4+4);//In theory, this could be global/not unique to the viewport
    public final GlBuffer visibilityBuffer;

    public MDICViewport(int maxSectionCount) {
        this.visibilityBuffer = new GlBuffer(maxSectionCount*4L);
    }

    @Override
    protected void delete0() {
        super.delete0();
        this.visibilityBuffer.free();
        this.indirectLookupBuffer.free();
        this.drawCountCallBuffer.free();
        this.drawCallBuffer.free();
        this.positionScratchBuffer.free();
    }

    @Override
    public GlBuffer getRenderList() {
        return this.indirectLookupBuffer;
    }
}
