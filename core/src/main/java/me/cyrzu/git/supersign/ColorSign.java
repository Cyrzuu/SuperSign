package me.cyrzu.git.supersign;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public enum ColorSign {

    OAK(Material.OAK_SIGN),
    SPRUCE(Material.SPRUCE_SIGN),
    BIRCH(Material.BIRCH_SIGN),
    JUNGLE(Material.JUNGLE_SIGN),
    ACACIA(Material.ACACIA_SIGN),
    DARK_OAK(Material.DARK_OAK_SIGN),
    CRIMSON(Material.CRIMSON_SIGN),
    WARPED(Material.WARPED_SIGN),
    MANGROVE("MANGROVE_SIGN"),
    BAMBOO("BAMBOO_SIGN"),
    CHERRY("CHERRY_SIGN");

    @NotNull
    private final Material material;

    ColorSign(@NotNull Material material) {
        this.material = material;
    }

    ColorSign(@NotNull String material) {
        Material m = Material.matchMaterial(material);
        this.material = m != null ? m : Material.OAK_SIGN;
    }

    @NotNull
    public BlockData getBlockData() {
        if((this == CHERRY || this == BAMBOO) && !ServerVersion.isAtLeast(ServerVersion.v1_20_R1)) {
            return Material.OAK_SIGN.createBlockData();
        }

        if(this == MANGROVE && !ServerVersion.isAtLeast(ServerVersion.v1_19_R1)) {
            return Material.OAK_SIGN.createBlockData();
        }

        return material.createBlockData();
    }

}
