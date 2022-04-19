package com.github.wolfiewaffle.bon.capability.temperature;

import java.util.ArrayList;

public class TempModifier {
    private String name;
    private float targetMod;
    private float insulation;

    public TempModifier(String name, float targetMod, float insulation) {
        this.name = name;
        this.targetMod = targetMod;
        this.insulation = insulation;
    }

    public TempModifier(String name, float targetMod) {
        this(name, targetMod, 0f);
    }

    public float getTargetMod() {
        return targetMod;
    }

    public float getInsulation() {
        return insulation;
    }

    public String getName() {
        return name;
    }

    public float apply(ArrayList<TempModifier> modlist) {
        modlist.add(this);
        return targetMod;
    }

    @Override
    public String toString() {
        return name + " MOD: " + targetMod + (insulation != 0 ? "I: " + insulation : "");
    }
}
