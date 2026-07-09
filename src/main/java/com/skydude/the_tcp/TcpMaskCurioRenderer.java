package com.skydude.the_tcp;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import static com.skydude.the_tcp.The_tcp.MODID;

public class TcpMaskCurioRenderer implements ICurioRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "textures/curios/tcp_mask_geo.png"
    );

    private final ModelPart mask = createMask();

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource bufferSource,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        LivingEntity wearer = slotContext.entity();

        poseStack.pushPose();
        ICurioRenderer.translateIfSneaking(poseStack, wearer);
        humanoidModel.head.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.0F, 0.025F);
        poseStack.scale(1.0F, 1.0F, 1.0F);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        mask.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static ModelPart createMask() {
        MeshDefinition meshDefinition = new MeshDefinition();
        meshDefinition.getRoot().addOrReplaceChild(
                "mask",
                CubeListBuilder.create()
                        .texOffs(0, 43)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.75F))
                        .texOffs(0, 21)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.55F))
                        .texOffs(0, 0)
                        .addBox(-5.0F, -8.0F, -5.0F, 10.0F, 11.0F, 10.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(meshDefinition, 64, 64).bakeRoot().getChild("mask");
    }
}
