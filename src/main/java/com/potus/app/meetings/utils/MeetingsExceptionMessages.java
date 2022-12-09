package com.potus.app.meetings.utils;
import com.potus.app.airquality.utils.AirQualityUtils;

import java.util.Arrays;


public class MeetingsExceptionMessages {
    public static final String REGIONS_NOT_FOUND = "REGION DOES NOT EXISTS, THESE ARE THE POSSIBLE REGIONS: "
            + Arrays.toString(AirQualityUtils.getRegions());
}
