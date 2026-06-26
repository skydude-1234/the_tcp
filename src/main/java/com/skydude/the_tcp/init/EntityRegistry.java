package com.skydude.the_tcp.init;



import com.skydude.the_tcp.The_tcp;
import com.skydude.the_tcp.entity.living.TCP_BOSS.TCP_entity;
import com.skydude.the_tcp.entity.living.TCP_BOSS.TCP_entityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


import static com.skydude.the_tcp.The_tcp.MODID;
import static net.minecraft.core.registries.Registries.ENTITY_TYPE;

@EventBusSubscriber(modid = MODID)
public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ENTITY_TYPE, The_tcp.MODID);
    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final DeferredHolder<EntityType<?>, EntityType<TCP_entity>> TCP_ENTITY =
            ENTITIES.register("tcp", () -> EntityType.Builder.of(TCP_entity::new, MobCategory.MONSTER)
                    .sized(.6f, 2.15f)
                    .clientTrackingRange(10)
                    .build(ResourceLocation.fromNamespaceAndPath(MODID, "tcp").toString()));

    //register attri
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.TCP_ENTITY.get(), TCP_entity.createAttributes().build());
      }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TCP_ENTITY.get(), TCP_entityRenderer::new);
    }
}
