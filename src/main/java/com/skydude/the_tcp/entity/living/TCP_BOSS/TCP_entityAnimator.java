package com.skydude.the_tcp.entity.living.TCP_BOSS;

import com.skydude.the_tcp.entity.living.ai.TCP_entity_melee_goal;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.controller.keyframe.AzKeyframeCallbacks;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.core.particles.ParticleTypes;
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
                AzAnimationController.builder(this, "attack_controller")                        .setKeyframeCallbacks(
                                AzKeyframeCallbacks.<TCP_entity>builder()
                                        .setParticleKeyframeHandler(
                                                event -> {
                                                    if (event.getKeyframeData().getEffect().equals("crit")) {
                                                        TCP_entity entity = event.getAnimatable();

                                                        for (int i = 0; i < 20; i++) {
                                                            double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
                                                            double y = entity.getY() + 0.8 + entity.getRandom().nextDouble() * entity.getBbHeight() * 0.5;
                                                            double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
                                                            double xSpeed = (entity.getRandom().nextDouble() - 0.5) * 0.2;
                                                            double ySpeed = entity.getRandom().nextDouble() * 0.2;
                                                            double zSpeed = (entity.getRandom().nextDouble() - 0.5) * 0.2;

                                                            entity.level().addParticle(
                                                                    ParticleTypes.CRIT,
                                                                    true,
                                                                    x,
                                                                    y,
                                                                    z,
                                                                    xSpeed,
                                                                    ySpeed,
                                                                    zSpeed
                                                            );
                                                        }
                                                    }
                                                }

                                        ).setCustomInstructionKeyframeHandler(
                                                event -> {
                                                    if (event.getKeyframeData().getInstructions().equals("damage")) {
                                                        // Do your custom instructions here



                                                    }
                                                }
                                        )
                                        .build()
                        )
                        .build(),
                AzAnimationController.builder(this, "procedure").build()
        );

    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(TCP_entity animatable) {
        return ANIMATIONS;
    }
}
