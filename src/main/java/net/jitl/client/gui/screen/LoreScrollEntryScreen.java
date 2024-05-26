package net.jitl.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.jitl.client.util.EnumHexColor;
import net.jitl.common.scroll.IDescComponent;
import net.jitl.common.scroll.ScrollEntry;
import net.jitl.core.helper.internal.DrawHelper;
import net.jitl.core.init.JITL;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class LoreScrollEntryScreen extends Screen {

    private static final ResourceLocation BG = JITL.rl("textures/gui/scroll_base.png");
    private static final int SLIDER_LIGHT_COLOR = 0xFFe5bd85;
    private static final int SLIDER_PATH_COLOR = 0x333c2c14;
    private static final int SLIDER_DARK_COLOR = 0xFFc18a3c;
    private final int headerHeight = 30;
    private final ScrollEntry scrollEntry;
    private int guiWidth;
    private int guiHeight;
    private int guix0;
    private int guiy0;
    private int mouseX;
    private int mouseY;
    private int entryWidth;
    private int top;
    private int bottom;
    private int left;
    private float initialMouseClickY = -2.0F;
    private float scrollDistance;
    private float scrollFactor;

    public LoreScrollEntryScreen(ScrollEntry ScrollEntry) {
        super(Component.translatable("jitl.lore_scroll"));
        this.scrollEntry = ScrollEntry;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }


    private void drawScreen(int mouseX, int mouseY) { }

    private int getContentPartCount() {
        return scrollEntry.getDesc().size();
    }

    private int getContentHeight() {
        int conHeight = this.headerHeight;
        for (IDescComponent part : scrollEntry.getDesc()) {
            conHeight += part.getContentPartHeight();
        }
        return conHeight;
    }

    private int getContentPartHeight(int index) {
        return scrollEntry.getDesc().get(index).getContentPartHeight();
    }

    private void determineAllContentPartHeight(int width) {
        for (IDescComponent part : scrollEntry.getDesc()) {
            part.determineContentPartHeight(width);
        }
    }

    private void drawHeader(GuiGraphics poseStack, int maxX, int y0, Tesselator tess) {
        float zLevel = this.getContentHeight();
        if (scrollEntry.hasComment()) {
            drawCenteredStringWithCustomScale(poseStack, font, Component.translatable(scrollEntry.getTitleKey()), left + (maxX - left) / 2 + 1, y0, (int) zLevel, EnumHexColor.BLACK, 1.5F, headerHeight - 5);
            if (scrollEntry.getCommentKey() != null)
                drawCenteredStringWithCustomScale(poseStack, font, Component.translatable(scrollEntry.getCommentKey()), left + (maxX - left) / 2 + 1, y0 + (int) ((float) font.lineHeight * 0.7), (int) zLevel, EnumHexColor.DARK_BROWN, 1F, headerHeight + 5);
        } else {
            drawCenteredStringWithCustomScale(poseStack, font, Component.translatable(scrollEntry.getTitleKey()), left + (maxX - left) / 2 + 1, y0, (int) zLevel, EnumHexColor.BLACK, 1.2F, headerHeight);
        }
    }

    public void drawCenteredStringWithCustomScale(GuiGraphics gui, Font f, FormattedText comp, int x, int y, int z, EnumHexColor colour, float size, int avaliableHeight) {
        gui.pose().pushPose();
        gui.pose().translate(x - (double)f.width(comp) / 2 * size, y + ((double)avaliableHeight / 2) + (f.lineHeight * size > 1 ? -1 * f.lineHeight * size : f.lineHeight * size) * 0.5, z);
        gui.pose().scale(size, size, size);
        gui.drawString(f, comp.getString(), 0, 0, colour.getInt(), false);
        gui.pose().popPose();
    }

    private void drawContentPart(GuiGraphics poseStack, int partIdx, int contentRight, int partTop, int partBuffer, Tesselator tess) {
        scrollEntry.getDesc().get(partIdx).drawContentPart(poseStack, this.left + 2, partTop, contentRight);
    }

    private void applyScrollLimits() {
        int listHeight = this.getContentHeight() - (this.bottom - this.top - 4);

        if(listHeight < 0)
            listHeight /= 2;

        if(this.scrollDistance < 0.0F)
            this.scrollDistance = 0.0F;

        if(this.scrollDistance > (float) listHeight)
            this.scrollDistance = (float) listHeight;
    }

    @Override
    public void render(@NotNull GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack, mouseX, mouseY, partialTicks);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BG);

        int heightRectCount = (height - (height <= 480 ? 12 : 48)) / 32;
        int widthRectCount = height <= 480 ? 6 : 10;

        this.guiWidth = widthRectCount * 32;
        this.guiHeight = heightRectCount * 32;

        this.guix0 = width / 2 - guiWidth / 2;
        this.guiy0 = height / 2 - guiHeight / 2;

        for (int x = 0; x < widthRectCount; x++) {
            for (int y = 0; y < heightRectCount; y++) {
                int textureX = x == 0 ? 0 : (x == widthRectCount - 1 ? 64 : 32);
                int textureY = y == 0 ? 0 : (y == heightRectCount - 1 ? 64 : 32);
                poseStack.blit(BG, guix0 + x * 32, guiy0 + y * 32, textureX, textureY, 32, 32);
            }
        }
        drawScrollingContent(poseStack, mouseX, mouseY);
    }

    private void drawScrollingContent(GuiGraphics poseStack, int mouseX, int mouseY) {
        int indent = 17;
        this.left = guix0 + indent + 4;
        this.top = guiy0 + indent;
        this.entryWidth = guiWidth - indent * 2;
        int entryHeight = guiHeight - indent * 2;
        this.bottom = top + entryHeight;

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        boolean isHovering = mouseX >= this.left && mouseX <= this.left + this.entryWidth && mouseY >= this.top && mouseY <= this.bottom;
        int entryLength = this.getContentPartCount();
        int scrollButtonWidth = 3;
        int scrollButtonRightTop = this.left + this.entryWidth;
        int scrollButtonLeftTop = scrollButtonRightTop - scrollButtonWidth;
        int contentRightTop = scrollButtonLeftTop - 5;

        determineAllContentPartHeight(contentRightTop - this.left - 4);

        int viewHeight = this.bottom - this.top;
        int border = 4;

        assert minecraft != null;
        MouseHandler mouseHandler = new MouseHandler(minecraft);
        if (mouseHandler.isLeftPressed()) {
            if (this.initialMouseClickY == -1.0F) {
                if (isHovering) {
                    if (mouseX >= scrollButtonLeftTop && mouseX <= scrollButtonRightTop) {
                        this.scrollFactor = -1.0F;
                        int scrollHeight = this.getContentHeight() - viewHeight - border;
                        if (scrollHeight < 1) scrollHeight = 1;

                        int var13 = (int) ((float) (viewHeight * viewHeight) / (float) this.getContentHeight());

                        if (var13 < 32) var13 = 32;
                        if (var13 > viewHeight - border * 2)
                            var13 = viewHeight - border * 2;

                        this.scrollFactor /= (float) (viewHeight - var13) / (float) scrollHeight;

                    } else {
                        this.scrollFactor = 1.0F;
                    }

                    this.initialMouseClickY = mouseY;
                } else {
                    this.initialMouseClickY = -2.0F;
                }
            } else if (this.initialMouseClickY >= 0.0F) {
                this.scrollDistance -= ((float) mouseY - this.initialMouseClickY) * this.scrollFactor;
                this.initialMouseClickY = (float) mouseY;
            }
        } else {
            this.initialMouseClickY = -1.0F;
        }

        this.applyScrollLimits();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder worldr = tess.getBuilder();

        double scaleW = minecraft.getWindow().getScreenWidth() / (double) minecraft.getWindow().getGuiScaledWidth();
        double scaleH = minecraft.getWindow().getScreenHeight() / (double) minecraft.getWindow().getGuiScaledHeight();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (left * scaleW), (int) (minecraft.getWindow().getScreenHeight() - (bottom * scaleH)), (int) (entryWidth * scaleW), (int) (viewHeight * scaleH));

        int baseY = this.top + border - (int) this.scrollDistance;
        int indentY = 0;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int partIdx = 0; partIdx < entryLength; ++partIdx) {
            int partTop = baseY + this.headerHeight + indentY;
            int partBuffer = getContentPartHeight(partIdx) - border;

            if (baseY + headerHeight >= top) {
                drawHeader(poseStack, contentRightTop, baseY, tess);
            }

            if (partTop <= this.bottom && partTop + partBuffer >= this.top) {
                int min = this.left;
                int max = contentRightTop;
                this.drawContentPart(poseStack, partIdx, max - min - 4, partTop, partBuffer, tess);
            }
            indentY += scrollEntry.getDesc().get(partIdx).getContentPartHeight();
        }

        int extraHeight = (this.getContentHeight() + border) - viewHeight;
        if (extraHeight > 0) {
            int height = (viewHeight * viewHeight) / this.getContentHeight();
            if (height < 32) {
                height = 32;
            }
            if (height > viewHeight - border * 2) {
                height = viewHeight - border * 2;
            }
            int barTop = (int) this.scrollDistance * (viewHeight - height) / extraHeight + this.top;
            if (barTop < this.top) {
                barTop = this.top;
            }

            int alpha, red, green, blue;


            RenderSystem.disableBlend();
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            alpha = DrawHelper.getAlpha(SLIDER_PATH_COLOR);
            red = DrawHelper.getRed(SLIDER_PATH_COLOR);
            green = DrawHelper.getGreen(SLIDER_PATH_COLOR);
            blue = DrawHelper.getBlue(SLIDER_PATH_COLOR);
            worldr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            worldr.vertex(scrollButtonLeftTop, this.bottom, 0.0D).uv(0.0F, 1.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonRightTop, this.bottom, 0.0D).uv(1.0F, 1.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonRightTop, this.top, 0.0D).uv(1.0F, 0.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonLeftTop, this.top, 0.0D).uv(0.0F, 0.0F).color(red, green, blue, alpha).endVertex();

            // Dark slider part
            alpha = DrawHelper.getAlpha(SLIDER_DARK_COLOR);
            red = DrawHelper.getRed(SLIDER_DARK_COLOR);
            green = DrawHelper.getGreen(SLIDER_DARK_COLOR);
            blue = DrawHelper.getBlue(SLIDER_DARK_COLOR);
            worldr.vertex(scrollButtonLeftTop, barTop + height, 0.0D).uv(0.0F, 1.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonRightTop, barTop + height, 0.0D).uv(1.0F, 1.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonRightTop, barTop, 0.0D).uv(1.0F, 0.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonLeftTop, barTop, 0.0D).uv(0.0F, 0.0F).color(red, green, blue, alpha).endVertex();

            // Light slider part
            alpha = DrawHelper.getAlpha(SLIDER_LIGHT_COLOR);
            red = DrawHelper.getRed(SLIDER_LIGHT_COLOR);
            green = DrawHelper.getGreen(SLIDER_LIGHT_COLOR);
            blue = DrawHelper.getBlue(SLIDER_LIGHT_COLOR);
            worldr.vertex(scrollButtonLeftTop, barTop + height - 1, 0.0D).uv(0.0F, 1.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonRightTop - 1, barTop + height - 1, 0.0D).uv(1.0F, 1.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonRightTop - 1, barTop, 0.0D).uv(1.0F, 0.0F).color(red, green, blue, alpha).endVertex();
            worldr.vertex(scrollButtonLeftTop, barTop, 0.0D).uv(0.0F, 0.0F).color(red, green, blue, alpha).endVertex();
            tess.end();

            RenderSystem.disableBlend();
        }

        this.drawScreen(mouseX, mouseY);
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        boolean isHovering = mouseX >= this.left && mouseX <= this.left + this.entryWidth &&
                mouseY >= this.top && mouseY <= this.bottom;
        if (!isHovering)
            return false;

        this.scrollDistance += (float)((-1 * pScrollY) * 2);
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers_) {
        if(keyCode == 256) {
            assert minecraft != null;
            minecraft.setScreen(null);
        }
        return true;
    }
}