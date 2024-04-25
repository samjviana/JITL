package net.jitl.core.init.internal;

import net.jitl.client.gui.screen.JFurnaceScreen;
import net.jitl.client.gui.screen.SummoningTableScreen;
import net.jitl.common.block.entity.container.JFurnaceMenu;
import net.jitl.common.block.entity.container.SummoningTableContainer;
import net.jitl.common.entity.jmerchent.SentacoinMerchantScreen;
import net.jitl.common.world.menu.SentacoinMerchantMenu;
import net.jitl.core.helper.internal.EmptyContainer;
import net.jitl.core.init.JITL;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class JContainers {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, JITL.MODID);

    public static DeferredHolder<MenuType<?>, MenuType<EmptyContainer>> EMPTY_CONTAINER = registerContainer("empty", (s, in, buf) -> new EmptyContainer().create(s, in, buf));

    public static final DeferredHolder<MenuType<?>, MenuType<JFurnaceMenu>> JFURNACE = registerContainer("jfurnace", (id, inv, data) -> new JFurnaceMenu(id, inv));

    public static final DeferredHolder<MenuType<?>, MenuType<SummoningTableContainer>> SUMMONING_TABLE = registerContainer("summoning_table", SummoningTableContainer::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SentacoinMerchantMenu>> SENTACOIN_MERCHANT = registerContainer("sentacoin_merchant", SentacoinMerchantMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerContainer(String id, IContainerFactory<T> factory) {
        return JContainers.REGISTRY.register(id, () -> IMenuTypeExtension.create(factory));
    }

    public static void register() {
        MenuScreens.register(JFURNACE.get(), JFurnaceScreen::new);
        MenuScreens.register(SUMMONING_TABLE.get(), SummoningTableScreen::new);
        MenuScreens.register(SENTACOIN_MERCHANT.get(), SentacoinMerchantScreen::new);
    }
}