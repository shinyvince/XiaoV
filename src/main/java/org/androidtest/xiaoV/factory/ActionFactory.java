package org.androidtest.xiaoV.factory;

import java.io.File;
import java.util.Map;

import org.androidtest.xiaoV.Config;
import org.androidtest.xiaoV.action.DailySelfReflectionAction;
import org.androidtest.xiaoV.action.LifeRoutineAction;
import org.androidtest.xiaoV.action.WeeklyReportAction;
import org.androidtest.xiaoV.action.ClockIn.DailyStepClockIn;
import org.androidtest.xiaoV.action.ClockIn.WeeklySportClockIn;
import org.androidtest.xiaoV.publicutil.DateUtil;

public class ActionFactory {

	public static DailySelfReflectionAction createDailySelfReflectionAction(
			Map<String, File> whiteList, boolean noonRemind) {
		DailySelfReflectionAction action = null;
		if (Config.DEBUG) {
			action = new DailySelfReflectionAction(whiteList,
					DateUtil.getCurrentTime() + 5,
					DateUtil.getCurrentTime() + 7);
		} else {
			action = new DailySelfReflectionAction(whiteList, noonRemind);
		}

		return action;
	}

	public static DailyStepClockIn createDailyStepClockIn() {
		DailyStepClockIn action = new DailyStepClockIn(7);
		return action;
	}

	public static LifeRoutineAction createLifeRoutineClockInAction(
			boolean life_routine_monrning_call, int morning_call_time,
			boolean life_routine_sleep_remind, int sleep_remind_time) {
		LifeRoutineAction action = null;
		if (Config.DEBUG) {
			action = new LifeRoutineAction(life_routine_monrning_call,
					DateUtil.getCurrentTime() + 1, life_routine_sleep_remind,
					DateUtil.getCurrentTime() + 3);
		} else {
			action = new LifeRoutineAction(life_routine_monrning_call,
					morning_call_time, life_routine_sleep_remind,
					sleep_remind_time);
		}
		return action;
	}

	public static WeeklyReportAction createWeeklyReportClockInAction(
			int dailyStep_weeklyLimitTimes, int weeklySport_weeklyLimitTimes) {
		WeeklyReportAction action = new WeeklyReportAction(
				dailyStep_weeklyLimitTimes, weeklySport_weeklyLimitTimes);
		return action;
	}

	public static WeeklySportClockIn createWeeklySportClockIn(
			int week_sport_limit_times) {
		WeeklySportClockIn action = new WeeklySportClockIn(
				week_sport_limit_times);
		return action;
	}
}
