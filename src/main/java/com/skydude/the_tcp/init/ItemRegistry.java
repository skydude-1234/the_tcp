package com.skydude.the_tcp.init;

import com.skydude.the_tcp.The_tcp;
import com.skydude.the_tcp.item.EssenceOfPvpItem;
import com.skydude.the_tcp.item.InfamousWoolItem;
import com.skydude.the_tcp.item.TcpMaskItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(The_tcp.MODID);

    public static final DeferredItem<InfamousWoolItem> INFAMOUS_WOOL = ITEMS.registerItem(
            "infamous_wool",
            InfamousWoolItem::new,
            new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.RARE)
                    .fireResistant()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );

    public static final DeferredItem<EssenceOfPvpItem> ESSENCE_OF_PVP = ITEMS.registerItem(
            "essence_of_pvp",
            EssenceOfPvpItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );

    public static final DeferredItem<TcpMaskItem> TCP_MASK = ITEMS.registerItem(
            "tcp_mask",
            TcpMaskItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .fireResistant()
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
