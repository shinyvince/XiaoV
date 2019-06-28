package org.androidtest.xiaoV.factory;

import java.io.File;
import java.util.Map;

import org.androidtest.xiaoV.Config;
import org.androidtest.xiaoV.customized.DailySelfReflectionCustomized;
import org.androidtest.xiaoV.customized.LifeRoutineClockInCustomized;
import org.androidtest.xiaoV.customized.WeeklyReportClockInCustomized;
import org.androidtest.xiaoV.publicutil.DateUtil;

public class CustomizedFactory {
	public static LifeRoutineClockInCustomized createLifeRoutineClockInCustomized(
			boolean life_routine_monrning_call, int morning_call_time,
			boolean life_routine_sleep_remind, int sleep_remind_time) {
		LifeRoutineClockInCustomized action = null;
		if (Config.DEBUG) {
			action = new LifeRoutineClockInCustomized(
					life_routine_monrning_call, DateUtil.getCurrentTime() + 1,
					life_routine_sleep_remind, DateUtil.getCurrentTime() + 3);
		} else {
			action = new LifeRoutineClockInCustomized(
					life_routine_monrning_call, morning_call_time,
					life_routine_sleep_remind, sleep_remind_time);
		}
		return action;
	}

	public static WeeklyReportClockInCustomized createWeeklyReportClockInCustomized(
			int dailyStep_weeklyLimitTimes, int weeklySport_weeklyLimitTimes) {
		WeeklyReportClockInCustomized action = new WeeklyReportClockInCustomized(
				dailyStep_weeklyLimitTimes, weeklySport_weeklyLimitTimes);
		return action;
	}

	public static DailySelfReflectionCustomized createDailySelfReflectionCustomized(
			Map<String, File> whiteList, boolean noonRemind) {
		DailySelfReflectionCustomized action = null;
		if (Config.DEBUG) {
			action = new DailySelfReflectionCustomized(whiteList,
					DateUtil.getCurrentTime() + 5,
					DateUtil.getCurrentTime() + 7);
		} else {
			action = new DailySelfReflectionCustomized(whiteList, noonRemind);
		}

		return action;
	}
}
