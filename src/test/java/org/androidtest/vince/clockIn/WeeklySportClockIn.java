package org.androidtest.vince.clockIn;

import static org.androidtest.vince.data.Constant.CURRENT_WEEK_SAVE_PATH;
import static org.androidtest.vince.data.Constant.SIMPLE_DAY_FORMAT_FILE;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.androidtest.vince.Config;
import org.androidtest.vince.data.Group;
import org.androidtest.vince.publicutil.LogUtil;
import org.androidtest.vince.publicutil.StringUtil;
import org.androidtest.vince.publicutil.WeekHelper;

import cn.zhouyafeng.itchat4j.api.WechatTools;

/**
 * 每周运动打卡组件
 * 
 * @author caipeipei
 *
 */
public class WeeklySportClockIn extends ClockIn {

	public WeeklySportClockIn(int weeklyLimitTimes) {
		super(weeklyLimitTimes);
	}

	@Override
	public List<String> getVaildKeywords() {
		return Config.TEXT_MSG_WEEKLY_SPORT_VAILD_KEYWORD_LIST;
	}

	@Override
	public String handleClockIn(String senderNickName) {
		LogUtil.MSG.debug("handleClockIn: " + this.getClass().getSimpleName()
				+ ", args: " + senderNickName);
		String result = null;
		String fileUserName = StringUtil.filter(senderNickName);
		int week_sport_limit_times = getWeeklyLimitTimes();
		if (StringUtil.ifNullOrEmpty(fileUserName)) {
			return "@" + senderNickName + " 你的名字有无法识别的字符，无法处理！请改昵称。";
		}
		String sportfilename = SIMPLE_DAY_FORMAT_FILE.format(new Date()) + "-"
				+ senderNickName + ".sport";
		File sportfile = new File(CURRENT_WEEK_SAVE_PATH.getAbsolutePath()
				+ File.separator + sportfilename);
		try {
			boolean isExist = false;
			if (sportfile.exists()) {
				isExist = true;
			} else {
				sportfile.createNewFile();
			}
			File dir = new File(CURRENT_WEEK_SAVE_PATH.getAbsolutePath());
			if (dir.isDirectory()) {
				File[] array = dir.listFiles();
				int count = 0;
				for (int i = 0; i < array.length; i++) {
					if (array[i].isFile()
							&& array[i].getName().endsWith(".sport")
							&& array[i].getName().contains(fileUserName)) {
						count++;
					}
				}
				if (isExist) {
					if (week_sport_limit_times > count) {
						result = "@" + senderNickName + " 你今天已经运动打卡过，无需重复打卡。"
								+ WeekHelper.getCurrentWeek() + "打卡运动第" + count
								+ "次，本周还差" + (week_sport_limit_times - count)
								+ "次";
					} else {
						result = "@" + senderNickName + " 你今天已经运动打卡过，无需重复打卡。"
								+ WeekHelper.getCurrentWeek() + "打卡运动第" + count
								+ "次，本周已经达标。已经运动了" + count + "次";
					}
				} else {
					if (week_sport_limit_times > count) {
						result = "@" + senderNickName + " 于"
								+ WeekHelper.getCurrentWeek() + "打卡运动第" + count
								+ "次，本周还差" + (week_sport_limit_times - count)
								+ "次";
					} else {
						result = "@" + senderNickName + " 于"
								+ WeekHelper.getCurrentWeek() + "打卡运动第" + count
								+ "次，本周已经达标。已经运动了" + count + "次";
					}
				}

			} else {
				LOG.error("handleClockIn: " + "error:" + dir.getAbsolutePath()
						+ "非文件夹路径！");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String reportProcess(Group group) {
		LogUtil.MSG.debug("reportProcess: " + this.getClass().getSimpleName()
				+ ", " + group.getGroupNickName());
		String currentGroupNickName = group.getGroupNickName();
		String result = null;
		File dir = CURRENT_WEEK_SAVE_PATH;
		List<String> list = WechatTools
				.getMemberListByGroupNickName2(currentGroupNickName);
		LogUtil.MSG.debug("reportProcess: " + currentGroupNickName + "群成员:"
				+ list.toString());
		String errorSport = null;
		String error404Name = "";
		if (dir.isDirectory()) {
			File[] array = dir.listFiles();
			for (int i = 0; i < list.size(); i++) {
				int countsport = 0;
				String name = StringUtil.filter(list.get(i));
				if (StringUtil.ifNullOrEmpty(name)) {
					error404Name = error404Name + "@" + list.get(i) + " ";
					continue;
				}
				for (int j = 0; j < array.length; j++) {
					if ((array[j].isFile()
							&& array[j].getName().endsWith(".sport") && array[j]
							.getName().contains(list.get(i)))
							|| (array[j].isFile()
									&& array[j].getName().endsWith(".sport") && array[j]
									.getName().contains(name))) {
						countsport++;
					}
				}
				if (countsport < getWeeklyLimitTimes()) {
					if (errorSport == null) {
						errorSport = "@" + list.get(i) + " ";
					} else {
						errorSport = errorSport + "@" + list.get(i) + " ";
					}
				}
			}
			if (errorSport == null) {
				errorSport = "无";
			}
			result = "------本周运动未达标：-------\n" + errorSport;
			if (!StringUtil.ifNullOrEmpty(error404Name)) {
				result = result + "\n如下（微信名含非法字符无法统计: " + error404Name + "）";
			}
		} else {
			LOG.error("reportProcess: " + "error:" + dir.getAbsolutePath()
					+ "非文件夹路径！");
		}
		return result;
	}

}