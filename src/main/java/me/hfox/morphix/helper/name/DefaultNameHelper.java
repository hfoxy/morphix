package me.hfox.morphix.helper.name;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.util.StringUtils;

public class DefaultNameHelper implements NameHelper {

    private Morphix morphix;

    @Override
    public String generate(Class<?> clazz) {
        String name = clazz.getSimpleName();
        if (morphix.getOptions().isCollectionNameSnakeCase()) {
            name = StringUtils.toSnakeCase(name);
        }

        if (morphix.getOptions().isCollectionNameLowercase()) {
            name = name.toLowerCase();
        }

        return name;
    }

}
