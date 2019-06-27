package org.androidtest.vince.factory;

import org.androidtest.vince.clockIn.DailyStepClockIn;
import org.androidtest.vince.clockIn.WeeklySportClockIn;

public class ClockInFactory {

	public static DailyStepClockIn createDailyStepClockIn() {
		DailyStepClockIn action = new DailyStepClockIn(7);
		return action;
	}

	public static WeeklySportClockIn createWeeklySportClockIn(
			int week_sport_limit_times) {
		WeeklySportClockIn action = new WeeklySportClockIn(
				week_sport_limit_times);
		return action;
	}

}
