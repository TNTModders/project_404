package com.tntmodders.asm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TNT404ASMNameMap {

    public static final Map<String, String> FIELD_MAP = new HashMap<>();
    public static final Map<String, String> METHOD_MAP = new HashMap<>();

    static {
        FIELD_MAP.put("explosionRadius", "field_82226_g");
        FIELD_MAP.put("POWERED", "field_184714_b");
        FIELD_MAP.put("size", "field_77280_f");
        FIELD_MAP.put("blockHardness", "field_149782_v");
        FIELD_MAP.put("advancementToProgress", "field_192803_d");
        FIELD_MAP.put("fuseTime", "field_82225_f");
        FIELD_MAP.put("lastPortalPos", "field_181016_an");
        FIELD_MAP.put("lastPortalVec", "field_181017_ao");
        FIELD_MAP.put("teleportDirection", "field_181018_ap");
        FIELD_MAP.put("inPortal", "field_71087_bX");
        FIELD_MAP.put("closestPlayer", "field_80001_f");
        FIELD_MAP.put("timeSinceIgnited", "field_70833_d");

        METHOD_MAP.put("onUpdate", "func_70071_h_");
        METHOD_MAP.put("explode", "func_146077_cc");
        METHOD_MAP.put("renderByItem", "func_179022_a");
        METHOD_MAP.put("renderByItem2", "func_192838_a");
    }

    public static boolean matchName(String name1, String name2) {
        return name1.equals(name2) || FIELD_MAP.containsKey(name1) && FIELD_MAP.get(name1).equals(name2) ||
                METHOD_MAP.containsKey(name1) && METHOD_MAP.get(name1).equals(name2);
    }

    public static Field getField(Class clazz, String sourceName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(sourceName);
        } catch (NoSuchFieldException e) {
            return clazz.getDeclaredField(FIELD_MAP.get(sourceName));
        }
    }
}
