package me.cyrzu.git.supersign;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum ServerVersion {
    v1_16_R3("1.16.4/1.16.5"),
    v1_17_R1("1.17/1.17.1"),
    v1_18_R1("1.18/1.18.1"),
    v1_18_R2("1.18.2"),
    v1_19_R1("1.19/1.19.1/1.19.2"),
    v1_19_R2("1.19.3"),
    v1_19_R3("1.19.4"),
    v1_20_R1("1.20/1.20.1"),
    v1_20_R2("1.20.2"),
    v1_20_R3("1.20.3/1.20.4"),
    UNKNOWN("Unknown");

    @Getter
    @NotNull
    private final String version;

    @Getter
    @NotNull
    private static final ServerVersion current = ServerVersion.getCurrentVersion();

    ServerVersion(@NotNull String string) {
        this.version = string;
    }

    public static boolean isAtLeast(@NotNull ServerVersion serverVersion) {
        return serverVersion.isCurrent() || getCurrentVersion().isHigher(serverVersion);
    }

    public static boolean isAbove(@NotNull ServerVersion serverVersion) {
        return getCurrentVersion().isHigher(serverVersion);
    }

    public boolean isHigher(@NotNull ServerVersion serverVersion) {
        return this.ordinal() > serverVersion.ordinal();
    }

    public boolean isLower(@NotNull ServerVersion serverVersion) {
        return this.ordinal() < serverVersion.ordinal();
    }

    public boolean isCurrent() {
        return this.equals(current);
    }

    @NotNull
    private static ServerVersion getCurrentVersion() {
        String[] bukkitPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        String protocol = bukkitPackage[bukkitPackage.length - 1];

        return Arrays.stream(ServerVersion.values())
                .filter(v -> v.name().equals(protocol))
                .findAny()
                .orElse(UNKNOWN);
    }
}
