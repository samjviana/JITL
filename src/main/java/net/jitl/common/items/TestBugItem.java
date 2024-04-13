package net.jitl.common.items;

import net.jitl.client.knowledge.EnumKnowledge;
import net.jitl.common.capability.essence.PlayerEssence;
import net.jitl.common.capability.stats.PlayerStats;
import net.jitl.common.items.base.JItem;
import net.jitl.core.helper.IEssenceItem;
import net.jitl.core.init.internal.JDataAttachments;
import net.jitl.core.init.internal.JItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TestBugItem extends JItem implements IEssenceItem {

    public TestBugItem() {
        super(JItems.itemProps().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if(!level.isClientSide()) {
            PlayerEssence essence = player.getData(JDataAttachments.ESSENCE);


            PlayerStats stats = player.getData(JDataAttachments.PLAYER_STATS);
                stats.setLevel(EnumKnowledge.OVERWORLD, 100);

        } else {
            displayPlayerStats(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    public static void displayPlayerStats(Player player) {
        Minecraft.getInstance().setScreen(new net.jitl.client.gui.overlay.PlayerStats(player));
    }
}