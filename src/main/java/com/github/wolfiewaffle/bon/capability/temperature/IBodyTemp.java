package com.github.wolfiewaffle.bon.capability.temperature;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IBodyTemp extends INBTSerializable<CompoundTag> {
    void setTemp(float temp);

    float getTemp();

    void setTargetTemp(float temp);

    float getTargetTemp();
}
