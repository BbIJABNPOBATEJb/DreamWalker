package me.bbijabnpobatejb.dreamwalker.mixin;

import com.google.common.collect.Lists;
import me.bbijabnpobatejb.dreamwalker.util.ChatFormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin {

    @Final @Shadow private Minecraft mc;

    @Shadow private int scrollPos;
    @Shadow private boolean isScrolled;

    @Final @Shadow private List<ChatLine> chatLines;
    @Final @Shadow private List<ChatLine> field_146253_i;

    @Shadow public abstract void scroll(int amount);
    @Shadow public abstract int getChatWidth();
    @Shadow public abstract float getChatScale();
    @Shadow public abstract boolean getChatOpen();
    @Shadow public abstract void deleteChatLine(int id);
    @Shadow protected abstract String formatColors(String str);

    /**
     * Переопределяем метод setChatLine, чтобы починить перенос строк с сохранением цветового форматирования.
     */
    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    public void onSetChatLine(IChatComponent component, int id, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        // Прерываем оригинальный метод
        ci.cancel();

        // Удаляем строку с таким же ID, если есть
        if (id != 0) {
            this.deleteChatLine(id);
        }

        // Получаем ширину чата в пикселях
        int maxLineWidth = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
        int currentLineWidth = 0;

        // Строим текущую строку и финальный список строк
        ChatComponentText currentLine = new ChatComponentText("");
        List<IChatComponent> finalLines = Lists.newArrayList();
        List<IChatComponent> processingQueue = Lists.newArrayList(component);

        FontRenderer font = mc.fontRendererObj;

        // Проходим по всем частям текста
        for (int index = 0; index < processingQueue.size(); ++index) {
            IChatComponent part = processingQueue.get(index);

            // Получаем отформатированный текст
            String rawText = part.getUnformattedTextForChat();
            String formattedText = formatColors(part.getChatStyle().getFormattingCode() + rawText);
            int partWidth = font.getStringWidth(formattedText);

            // Клонируем компонент
            ChatComponentText partComponent = new ChatComponentText(formattedText);
            partComponent.setChatStyle(part.getChatStyle().createShallowCopy());

            boolean requiresNewLine = false;

            // Если текущая часть не помещается
            if (currentLineWidth + partWidth > maxLineWidth) {
                // Подрезаем часть
                String trimmedText = font.trimStringToWidth(formattedText, maxLineWidth - currentLineWidth, false);
                String remainder = trimmedText.length() < formattedText.length() ? formattedText.substring(trimmedText.length()) : null;

                if (remainder != null) {
                    int splitIndex = trimmedText.lastIndexOf(" ");

                    if (splitIndex >= 0 && font.getStringWidth(trimmedText.substring(0, splitIndex)) > 0) {
                        trimmedText = trimmedText.substring(0, splitIndex);
                        remainder = formattedText.substring(splitIndex);
                    }

                    String inheritedFormat = ChatFormatUtil.getLastColors(trimmedText);
                    remainder = inheritedFormat + remainder;

                    ChatComponentText remainderComponent = new ChatComponentText(remainder);
                    remainderComponent.setChatStyle(part.getChatStyle().createShallowCopy());
                    processingQueue.add(index + 1, remainderComponent);
                }

                // Пересчитываем ширину и создаём обрезанную часть
                partWidth = font.getStringWidth(trimmedText);
                partComponent = new ChatComponentText(trimmedText);
                partComponent.setChatStyle(part.getChatStyle().createShallowCopy());

                requiresNewLine = true;
            }

            // копим часть в текущей строке
            if (currentLineWidth + partWidth <= maxLineWidth) {
                currentLineWidth += partWidth;
                currentLine.appendSibling(partComponent);
            } else {
                requiresNewLine = true;
            }

            // Если нужно закончить текущую строку
            if (requiresNewLine) {
                finalLines.add(currentLine);
                currentLineWidth = 0;
                currentLine = new ChatComponentText("");
            }
        }

        // Добавляем последнюю строку
        finalLines.add(currentLine);

        boolean chatOpen = this.getChatOpen();

        // Записываем строки в буфер отображения
        for (IChatComponent line : finalLines) {
            if (chatOpen && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.field_146253_i.add(0, new ChatLine(updateCounter, line, id));
        }

        // Обрезаем буфер отображения до лимита
        while (this.field_146253_i.size() > 100) {
            this.field_146253_i.remove(this.field_146253_i.size() - 1);
        }

        // Если строка не временная — добавляем в чат-историю
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, component, id));

            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }
}