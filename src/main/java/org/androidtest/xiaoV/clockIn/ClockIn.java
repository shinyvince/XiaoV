package org.androidtest.xiaoV.clockIn;

import java.util.List;

import org.androidtest.xiaoV.data.Group;
import org.androidtest.xiaoV.publicutil.LogUtil;
import org.apache.log4j.Logger;

/**
 * 打卡组件（抽象类）
 * 
 * @author caipeipei
 *
 */
public abstract class ClockIn {
	protected Logger LOG = Logger.getLogger(ClockIn.class);

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

	public abstract List<String> getVaildKeywords();

	public abstract String handleClockIn(String nickName);

	public abstract String reportProcess(Group group);

	protected int getWeeklyLimitTimes() {
		return weeklyLimitTimes;
	}

	protected void setWeeklyLimitTimes(int weeklyLimitTimes) {
		LogUtil.MSG.debug("setWeeklyLimitTimes: " + weeklyLimitTimes);
		this.weeklyLimitTimes = weeklyLimitTimes;
	}
}