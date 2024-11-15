package net.jitl.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderProjectile<T extends Entity> extends EntityRenderer<T, ThrownItemRenderState> {

    private final RenderType renderType;
    private float scale = 1.0F;
    private final boolean fullBright = true;

    public RenderProjectile(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context);
        this.renderType = RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void render(@NotNull ThrownItemRenderState entityIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.partialTick >= 2) {
            renderProjectile(matrixStackIn, bufferIn, packedLightIn);
        }
        super.render(entityIn, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    @Override
    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
    }


    private void renderProjectile(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        float scale = 0.5F;
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
        matrixStackIn.translate(0, 0.5D, 0);
        matrixStackIn.scale(this.scale, this.scale, this.scale);

        PoseStack.Pose lastMatrix = matrixStackIn.last();
        Matrix4f pose = lastMatrix.pose();
        Matrix3f normal = lastMatrix.normal();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);
        vertex(ivertexbuilder, pose, normal, packedLightIn, 0, 0, 0, 1);
        vertex(ivertexbuilder, pose, normal, packedLightIn, 1, 0, 1, 1);
        vertex(ivertexbuilder, pose, normal, packedLightIn, 1, 1, 1, 0);
        vertex(ivertexbuilder, pose, normal, packedLightIn, 0, 1, 0, 0);

        matrixStackIn.popPose();
    }

    private static void vertex(VertexConsumer builder, Matrix4f pose, Matrix3f normal, int lightmapUV, float x, float y, int u, int v) {
        builder.addVertex(pose, x - 0.5F, y - 0.5F, 0.0F).setColor(255, 255, 255, 255).setUv((float) u, (float) v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightmapUV).setNormal(0.0F, 1.0F, 0.0F);
    }
}
