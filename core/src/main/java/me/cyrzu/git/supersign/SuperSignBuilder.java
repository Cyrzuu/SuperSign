package me.cyrzu.git.supersign;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

public class SuperSignBuilder {

    private boolean runned = false;

    @NotNull
    private final SuperSign superSign;

    @Getter
    @NotNull
    private final Player player;

    @Getter
    @NotNull
    private final String[] lines = new String[]{"", "", "", ""};

    @Getter
    @Nullable
    private Consumer<@NotNull String[]> acceptConsumer;

    @Getter
    @NotNull
    private ColorSign colorSign;

    SuperSignBuilder(@NotNull SuperSign superSign, @NotNull Player player) {
        this.superSign = superSign;
        this.player = player;
    }

    public SuperSignBuilder onAccept(@NotNull Consumer<@NotNull String[]> function) {
        acceptConsumer = function;
        return this;
    }

    public SuperSignBuilder setLines(@Nullable String... texts) {
        int index = 0;
        for (String line : texts) {
            if(index >= 4) {
                break;
            }

            lines[index] = line == null ? "" : line;
            index++;
        }

        return this;
    }

    public SuperSignBuilder setLines(int line, @Nullable String text) {
        if(line < 0 || line >= 4) {
            return this;
        }

        lines[line] = text == null ? "" : text;
        return this;
    }

    public SuperSignBuilder setColorSign(@NotNull ColorSign colorSign) {
        this.colorSign = colorSign;
        return this;
    }

    public void run() {
        if(runned) {
            return;
        }

        runned = true;
        superSign.open(this);
    }

}
