package net.jitl.core.init.internal;

import net.jitl.core.init.JITL;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class JTabs {

    public static final CreativeModeTab BLOCKS = new CreativeModeTab(JITL.MODID + ".blocks") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(JBlocks.SAPPHIRE_ORE.get());
        }
    };

    public static final CreativeModeTab MATERIALS = new CreativeModeTab(JITL.MODID + ".materials") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(JItems.SAPPHIRE.get());
        }
    };

}
