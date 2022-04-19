package com.github.wolfiewaffle.bon.compat;

import com.github.wolfiewaffle.bon.config.Config;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class CompatSereneSeasons {

    public static float getSeasonMod(Level world) {
        double springMod = Config.seasonSpringMod.get();
        double summerMod = Config.seasonSummerMod.get();
        double autumnMod = Config.seasonAutumnMod.get();
        double winterMod = Config.seasonWinterMod.get();

        ISeasonState state = SeasonHelper.getSeasonState(world);
        Season.SubSeason subSeason = state.getSubSeason();

        double seasonMod = 0;

        switch (subSeason) {
            case EARLY_SPRING:
                seasonMod = (springMod * 0.7) + (winterMod * 0.3);
                break;
            case MID_SPRING:
                seasonMod = springMod;
                break;
            case LATE_SPRING:
                seasonMod = (springMod * 0.7) + (summerMod * 0.3);
                break;
            case EARLY_SUMMER:
                seasonMod = (summerMod * 0.7) + (springMod * 0.3);
                break;
            case MID_SUMMER:
                seasonMod = summerMod;
                break;
            case LATE_SUMMER:
                seasonMod = (summerMod * 0.7) + (autumnMod * 0.3);
                break;
            case EARLY_AUTUMN:
                seasonMod = (autumnMod * 0.7) + (summerMod * 0.3);
                break;
            case MID_AUTUMN:
                seasonMod = autumnMod;
                break;
            case LATE_AUTUMN:
                seasonMod = (autumnMod * 0.7) + (winterMod * 0.3);
                break;
            case EARLY_WINTER:
                seasonMod = (winterMod * 0.7) + (autumnMod * 0.3);
                break;
            case MID_WINTER:
                seasonMod = winterMod;
                break;
            case LATE_WINTER:
                seasonMod = (winterMod * 0.7) + (springMod * 0.3);
                break;
        }

        return (float) seasonMod;
    }
}
