package com.github.wolfiewaffle.bon.capability.temperature;

import net.minecraft.nbt.CompoundTag;

public class BodyTempImplementation implements IBodyTemp {
    private static final String NBT_KEY_BODY_TEMP = "body_temp";
    private static final String NBT_KEY_TARGET_TEMP = "target_temp";

    private float bodyTemp = 100f;
    private float targetTemp = 100f;

    @Override
    public float getTemp() {
        return this.bodyTemp;
    }

    @Override
    public float getTargetTemp() {
        return this.targetTemp;
    }

    @Override
    public void setTemp(float temp) {
        this.bodyTemp = temp;
    }

    @Override
    public void setTargetTemp(float targetTemp) {
        this.targetTemp = targetTemp;
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putFloat(NBT_KEY_BODY_TEMP, this.bodyTemp);
        tag.putFloat(NBT_KEY_TARGET_TEMP, this.targetTemp);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.bodyTemp = nbt.getFloat(NBT_KEY_BODY_TEMP);
        this.targetTemp = nbt.getFloat(NBT_KEY_TARGET_TEMP);
    }
}