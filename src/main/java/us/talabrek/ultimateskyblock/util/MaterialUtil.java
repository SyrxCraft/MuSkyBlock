package us.talabrek.ultimateskyblock.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * Functions for working with Materials
 */
public enum MaterialUtil {
    ;
    private static final Pattern MATERIAL_PROBABILITY = Pattern.compile("(\\{p=(?<prob>0\\.[0-9]+)\\})?\\s*(?<id>[A-Z_0-9]+)");
    private static final Collection<Material> SANDS = Arrays.asList(Material.SAND, Material.GRAVEL);
    private static final Collection<Material> WOOD_TOOLS = Arrays.asList(Material.WOOD_AXE, Material.WOOD_HOE, Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.WOOD_SWORD);
    private static final Collection<Material> STONE_TOOLS = Arrays.asList(Material.STONE_AXE, Material.STONE_HOE, Material.STONE_PICKAXE, Material.STONE_SPADE, Material.STONE_SWORD);
    private static final Collection<Material> IRON_TOOLS = Arrays.asList(Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SPADE, Material.IRON_SWORD);
    private static final Collection<Material> GOLD_TOOLS = Arrays.asList(Material.GOLD_AXE, Material.GOLD_HOE, Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.GOLD_SWORD);
    private static final Collection<Material> DIAMOND_TOOLS = Arrays.asList(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE, Material.DIAMOND_SWORD);
    private static final Collection<Material> TOOLS = new ArrayList<>();
    static {
        TOOLS.addAll(WOOD_TOOLS);
        TOOLS.addAll(STONE_TOOLS);
        TOOLS.addAll(IRON_TOOLS);
        TOOLS.addAll(GOLD_TOOLS);
        TOOLS.addAll(DIAMOND_TOOLS);
    }

    public static boolean isTool(Material type) {
        return TOOLS.contains(type);
    }

    public static String getToolType(Material tool) {
        if (isTool(tool)) {
            String enumName = tool.name();
            return enumName.substring(0, enumName.indexOf('_'));
        }
        return null;
    }

    public static Material getMaterial(String name, Material fallback) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public static boolean isFallingMaterial(Material mat) {
        return SANDS.contains(mat);
    }

    public static List<MaterialProbability> createProbabilityList(List<String> matList) {
        List<MaterialProbability> list = new ArrayList<>();
        for (String line : matList) {
            Matcher m = MATERIAL_PROBABILITY.matcher(line);
            if (m.matches()) {
                Material mat = Material.getMaterial(m.group("id"));
                double p = m.group("prob") != null ? Double.parseDouble(m.group("prob")) : 1;
                list.add(new MaterialProbability(mat, p));
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Misconfigured list of materials: " + line);
            }
        }
        return list;
    }

    public static class MaterialProbability {
        private final Material material;
        private final double probability;

        public MaterialProbability(Material material, double probability) {
            this.material = material;
            this.probability = probability;
        }

        public Material getMaterial() {
            return material;
        }

        public double getProbability() {
            return probability;
        }
    }
}
