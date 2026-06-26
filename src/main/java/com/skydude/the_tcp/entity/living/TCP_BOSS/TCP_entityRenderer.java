package com.skydude.the_tcp.entity.living.TCP_BOSS;

import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.skydude.the_tcp.The_tcp.MODID;

public class TCP_entityRenderer extends AzEntityRenderer<TCP_entity> {
    private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "geo/entity/living/tcp_entity.geo.json"
    );

    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "textures/entity/living/tcp_entity.png"
    );

    public TCP_entityRenderer(EntityRendererProvider.Context context) {
        super(
                AzEntityRendererConfig.<TCP_entity>builder(GEO, TEX)
                        .setAnimatorProvider(TCP_entityAnimator::new).build(),
                context
        );
    }
}