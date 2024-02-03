package me.cyrzu.git.supersign;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    private static SuperSign superSign;

    @Override
    public void onEnable() {
        superSign = new SuperSign(this);
    }

    public void log(@NotNull Object object, Object... args) {
        getLogger().info(object.toString().formatted(args));
    }

}
