package net.jitl.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jitl.client.essence.ClientEssence;
import net.jitl.client.essence.PlayerEssenceProvider;
import net.jitl.core.helper.IEssenceItem;
import net.jitl.core.init.JITL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class EssenceBar {

    private static final ResourceLocation OVER_EXP_TEXTURE = new ResourceLocation(JITL.MODID, "textures/gui/essence_over_exp.png");

    public static void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, OVER_EXP_TEXTURE);
        if(player != null && !player.isCreative() && !player.isSpectator()) {
            player.getCapability(PlayerEssenceProvider.PLAYER_ESSENCE).ifPresent(essence -> {
                int currentEssence = ClientEssence.getClientEssence();
                int yPos = 20;
                int xPos = 100;
                if (!minecraft.options.hideGui) {
                    if(instanceOfEssenceItem(player.getMainHandItem().getItem())) {
                        int y = screenHeight - yPos;
                        int x = screenWidth - xPos;
                        GuiComponent.blit(poseStack, x, y, 0, 5, 92, 11, 92, 16);
                        for(int i = 0; i < currentEssence; i++) {
                            if(!(i >= essence.getMaxEssence())) {
                                x += 11;
                                GuiComponent.blit(poseStack,  x - 8, y + 3, 0, 0, 10, 5, 92, 16);
                            }
                        }
                    }
                }
            });
        }
    }

    private static boolean instanceOfEssenceItem(Item isEssence) {
        return isEssence instanceof IEssenceItem;
    }
}
