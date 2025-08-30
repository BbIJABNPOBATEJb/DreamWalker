package me.bbijabnpobatejb.dreamwalker.util;

import lombok.experimental.UtilityClass;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;

import java.util.Map;

@UtilityClass
public class StringUtil {

    public String applyPlaceholders(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    public boolean containsPrefix(SimpleConfig config, String message, String prefix) {
        String msg = message.trim();
        if (msg.startsWith(prefix)) return true;

        for (String channel : config.getChannelPrefixes()) {
            if (msg.startsWith(channel + prefix)) {
                return true;
            }
        }
        return false;
    }

    public String subStingWithoutChannel(String message, String prefix) {
        int index = message.indexOf(prefix);

        if (index == -1) return message;
        return message.substring(index + prefix.length());
    }

    public String subStingChannel(SimpleConfig config, String message, String prefix) {
        String prefixFound = "";
        for (String channel : config.getChannelPrefixes()) {
            if (message.startsWith(channel + prefix)) {
                prefixFound = channel;
                break;
            }
        }
        return prefixFound;
    }
}
