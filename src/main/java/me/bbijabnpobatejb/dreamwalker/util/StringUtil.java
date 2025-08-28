package me.bbijabnpobatejb.dreamwalker.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class StringUtil {

    public String applyPlaceholders(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}
