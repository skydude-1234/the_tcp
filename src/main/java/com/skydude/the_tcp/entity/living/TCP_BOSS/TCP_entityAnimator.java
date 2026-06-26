package com.skydude.the_tcp.entity.living.TCP_BOSS;

import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.skydude.the_tcp.The_tcp.MODID;

public class TCP_entityAnimator extends AzEntityAnimator<TCP_entity> {
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "animations/entity/living/tcp_entity.animation.json"
    );

    @Override
    public void registerControllers(AzAnimationControllerContainer<TCP_entity> animationControllerContainer) {
        animationControllerContainer.add(
                AzAnimationController.builder(this, "base_controller").build(),
                AzAnimationController.builder(this, "attack_controller").build(),
                AzAnimationController.builder(this, "procedure").build()
        );

    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(TCP_entity animatable) {
        return ANIMATIONS;
    }
}
