package me.bbijabnpobatejb.dreamwalker.util;


import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import net.minecraft.util.EnumChatFormatting;

import javax.annotation.Nullable;

@UtilityClass
public class ChatFormatUtil {
    public String getLastColors(String input) {
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strike = false;
        boolean obfuscated = false;
        boolean reset = false;
        char lastColor = 0;

        // обходим в обратном порядке
        for (int i = input.length() - 1; i >= 0 && !reset; i--) {
            if (input.charAt(i) == '§' && i < input.length() - 1) {
                char code = input.charAt(i + 1);

                switch (code) {
                    case 'k':
                        obfuscated = true;
                        break;
                    case 'l':
                        bold = true;
                        break;
                    case 'm':
                        strike = true;
                        break;
                    case 'n':
                        underline = true;
                        break;
                    case 'o':
                        italic = true;
                        break;
                    case 'r':
                        reset = true;
                        break;
                    default:
                        if (isColorCode(code) && lastColor == 0) {
                            lastColor = code;
                        }
                        break;
                }
            }
        }

        StringBuilder result = new StringBuilder();

        if (lastColor != 0)
            result.append('§').append(lastColor);
        if (obfuscated)
            result.append("§k");
        if (bold)
            result.append("§l");
        if (strike)
            result.append("§m");
        if (underline)
            result.append("§n");
        if (italic)
            result.append("§o");

        return result.toString();
    }

    public boolean isColorCode(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
    }

    public @Nullable EnumChatFormatting getValueByChat(char c) {
        for (EnumChatFormatting value : EnumChatFormatting.values()) {
            if (value.getFormattingCode() == c) return value;
        }
        return null;
    }
}