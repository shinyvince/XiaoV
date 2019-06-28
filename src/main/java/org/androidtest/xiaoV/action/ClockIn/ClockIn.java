package org.androidtest.xiaoV.action.ClockIn;

import org.androidtest.xiaoV.action.Action;
import org.androidtest.xiaoV.publicutil.LogUtil;

public abstract class ClockIn extends Action {
	public ClockIn(int weeklyLimitTimes) {
		if (weeklyLimitTimes >= 0) {
			this.weeklyLimitTimes = weeklyLimitTimes;
		} else {
			throw new RuntimeException(
					"ClockIn: weeklyLimitTimes is out of range: "
							+ weeklyLimitTimes);
		}
	}

	protected int weeklyLimitTimes = -1;

	protected int getWeeklyLimitTimes() {
		return weeklyLimitTimes;
	}

	protected void setWeeklyLimitTimes(int weeklyLimitTimes) {
		LogUtil.MSG.debug("setWeeklyLimitTimes: " + weeklyLimitTimes);
		this.weeklyLimitTimes = weeklyLimitTimes;
	}
}
