package me.cyrzu.git.supersign;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

public class SuperSign {

    public SuperSign(@NotNull Main plugin) {
        Class<?> aClass = Reflex.getClass("me.cyrzu.git.supersign.version", "Version_" + Version.getCurrent());
        Constructor<?> constructor = Reflex.getConstructor(aClass);
        VersionHandler handler = (VersionHandler) Reflex.invokeConstructor(constructor);

        plugin.log("%s i chuj", handler.getClass().getSimpleName());
    }
}
