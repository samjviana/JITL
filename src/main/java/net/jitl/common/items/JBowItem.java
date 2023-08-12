package net.jitl.common.items;

import net.jitl.common.capability.essence.PlayerEssenceProvider;
import net.jitl.common.entity.projectile.EssenceArrowEntity;
import net.jitl.core.init.internal.JItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class JBowItem extends BowItem {

    public static final int DEFAULT_DURATION = 72000;
    protected int maxUseDuration;
    protected float damage;
    protected Item arrow_item;
    protected int uses;
    protected int essence_use;
    protected EnumSet<EssenceArrowEntity.BowEffects> effect;

    public JBowItem(float damage, int uses, EnumSet<EssenceArrowEntity.BowEffects> effects, int pullbackSpeed) {
        super(JItems.itemProps().stacksTo(1).durability(uses));
        this.effect = effects;
        this.arrow_item = JItems.ESSENCE_ARROW.get();
        this.damage = damage;
        this.uses = uses;
        this.maxUseDuration = pullbackSpeed;
    }

    public float getScaledArrowVelocity(int charge) {
        float timeRatio = ((float) DEFAULT_DURATION / (float) this.maxUseDuration);
        float f = ((float) charge / 20.0F) * timeRatio;
        f = (f * f + f * 2.0F) / 2.0F;

        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player player = (Player) entityLiving;
            boolean flag = player.isCreative() ||
                    EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0 ||
                    effect.contains(EssenceArrowEntity.BowEffects.CONSUMES_ESSENCE);

            ItemStack itemstack = this.findAmmo(player);

            int i = this.maxUseDuration - timeLeft;
            i = ForgeEventFactory.onArrowLoose(stack, worldIn, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(arrow_item);
                }

                float f = getScaledArrowVelocity(i);
                if ((double) f >= 0.1D) {
                    if(!worldIn.isClientSide) {
                        EssenceArrowEntity entityarrow = null;
                        EssenceArrowEntity entityarrow2 = null;
                        try {
                            entityarrow = new EssenceArrowEntity(worldIn, player, this.effect, this.damage);
                            entityarrow2 = new EssenceArrowEntity(worldIn, player, this.effect, this.damage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /*
                         * shoot 2 arrows if bow is Wasteful Bow
                         */
                        if (effect.contains(EssenceArrowEntity.BowEffects.DOUBLE_ARROW)) {
                            assert entityarrow != null;
                            entityarrow.shootFromRotation(player, player.getXRot(), player.getYRot() + 3.25F, 0.0F, f * 3.0F, 1.0F);
                            assert entityarrow2 != null;
                            entityarrow2.shootFromRotation(player, player.getXRot(), player.getYRot() - 3.25F, 0.0F, f * 3.0F, 1.0F);

                            if (f == 1.0F) {
                                entityarrow.setCritArrow(true);
                                entityarrow2.setCritArrow(true);
                            }

                            int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);

                            int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);

                            if (k > 0) {
                                entityarrow.setKnockback(k);
                                entityarrow2.setKnockback(k);
                            }

                            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                                entityarrow.setSecondsOnFire(100);
                                entityarrow2.setSecondsOnFire(100);
                            }

                            entityarrow.setBaseDamage(this.damage);
                            entityarrow2.setBaseDamage(this.damage);

                            stack.hurtAndBreak(1, player, (onBroken) -> onBroken.broadcastBreakEvent(player.getUsedItemHand()));

                            if (flag || player.isCreative()
                                    && (itemstack.getItem() == Items.SPECTRAL_ARROW
                                    || itemstack.getItem() == Items.TIPPED_ARROW)) {
                                entityarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                entityarrow2.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }
                            worldIn.addFreshEntity(entityarrow);
                            worldIn.addFreshEntity(entityarrow2);
                        }
                        /*
                         * shoot 1 arrow if bow isn't Wasteful Bow
                         */
                        else {
                            assert entityarrow != null;
                            entityarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);

                            int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);

                            if(j == 1.0F)
                                entityarrow.setCritArrow(true);

                            int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);

                            if (k > 0) {
                                entityarrow.setKnockback(k);
                            }

                            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                                entityarrow.setSecondsOnFire(100);
                            }

                            entityarrow.setBaseDamage(this.damage);

                            stack.hurtAndBreak(1, player, (onBroken) -> {
                                onBroken.broadcastBreakEvent(player.getUsedItemHand());
                            });

                            if (flag || player.isCreative()
                                    && (itemstack.getItem() == Items.SPECTRAL_ARROW
                                    || itemstack.getItem() == Items.TIPPED_ARROW)) {
                                entityarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            if(effect.contains(EssenceArrowEntity.BowEffects.CONSUMES_ESSENCE)) {
                                EssenceArrowEntity essenceArrow = entityarrow;
                                player.getCapability(PlayerEssenceProvider.PLAYER_ESSENCE).ifPresent(essence -> {
                                    if(essence.consumeEssence(player, essence_use)) {
                                        worldIn.addFreshEntity(essenceArrow);
                                    }
                                });
                            }

                            if(!effect.contains(EssenceArrowEntity.BowEffects.CONSUMES_ESSENCE)) {
                                worldIn.addFreshEntity(entityarrow);
                            }
                        }
                    }

                    worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                    if(!flag && !player.isCreative()) {
                        if(effect.contains(EssenceArrowEntity.BowEffects.DOUBLE_ARROW)) {
                            itemstack.shrink(2);
                        } else {
                            itemstack.shrink(1);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public ItemStack findAmmo(Player player) {
        if (this.isArrow(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return player.getItemInHand(InteractionHand.OFF_HAND);

        } else if (this.isArrow(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);

        } else if (effect.contains(EssenceArrowEntity.BowEffects.CONSUMES_ESSENCE)) {
            return ItemStack.EMPTY;

        } else {
            for (int i = 0; i < player.inventoryMenu.getItems().size(); ++i) {
                ItemStack itemstack = player.inventoryMenu.getItems().get(i);

                if (this.isArrow(itemstack)) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return maxUseDuration;
    }

    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() == JItems.ESSENCE_ARROW.get();
    }
}
