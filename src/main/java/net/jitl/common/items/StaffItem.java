package net.jitl.common.items;

import net.jitl.client.essence.PlayerEssenceProvider;
import net.jitl.client.knowledge.EnumKnowledge;
import net.jitl.client.knowledge.PlayerKnowledgeProvider;
import net.jitl.core.helper.IEssenceItem;
import net.jitl.core.init.internal.ItemRegistrys;
import net.jitl.core.init.internal.JItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class StaffItem extends Item implements IEssenceItem {

    protected BiFunction<Level, LivingEntity, ThrowableProjectile> projectileFactory;
    private final int essenceUsage;

    public StaffItem(int essence, BiFunction<Level, LivingEntity, ThrowableProjectile> projectileFactory) {
        super(ItemRegistrys.rangedProps().stacksTo(1));
        this.projectileFactory = projectileFactory;
        this.essenceUsage = essence;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if(!level.isClientSide()) {
            player.getCapability(PlayerEssenceProvider.PLAYER_ESSENCE).ifPresent(essence -> {
                if(essence.consumeEssence(player, this.essenceUsage)) {
                    ThrowableProjectile projectile = projectileFactory.apply(level, player);
                    projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                    level.addFreshEntity(projectile);
                }
            });
            player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE).ifPresent(knowledge -> {
                knowledge.addXP(player, EnumKnowledge.OVERWORLD, 10F);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}