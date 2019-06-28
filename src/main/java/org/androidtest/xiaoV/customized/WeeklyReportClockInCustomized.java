package org.androidtest.xiaoV.customized;

import static org.androidtest.xiaoV.data.Constant.CURRENT_WEEK_SAVE_PATH;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.androidtest.xiaoV.Config;
import org.androidtest.xiaoV.clockIn.ClockIn;
import org.androidtest.xiaoV.data.Constant;
import org.androidtest.xiaoV.data.Group;
import org.androidtest.xiaoV.publicutil.LogUtil;
import org.androidtest.xiaoV.publicutil.StringUtil;
import org.androidtest.xiaoV.publicutil.WeekHelper;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;

/**
 * 周报组件
 * 
 * @author caipeipei
 *
 */
public class WeeklyReportClockInCustomized extends Customized {

	private int dailyStep_weeklyLimitTimes = -1;
	private int weeklySport_weeklyLimitTimes = -1;

	private final int autoReportTime = 2359;

	public WeeklyReportClockInCustomized(int dailyStep_weeklyLimitTimes,
			int weeklySport_weeklyLimitTimes) {
		setDailyStep_weeklyLimitTimes(dailyStep_weeklyLimitTimes);
		setWeeklySport_weeklyLimitTimes(weeklySport_weeklyLimitTimes);
	}

	private int getDailyStep_weeklyLimitTimes() {
		return dailyStep_weeklyLimitTimes;
	}

	private void setDailyStep_weeklyLimitTimes(int dailyStep_weeklyLimitTimes) {
		LogUtil.MSG.debug("setDailyStep_weeklyLimitTimes: "
				+ dailyStep_weeklyLimitTimes);
		if (dailyStep_weeklyLimitTimes >= 0) {
			this.dailyStep_weeklyLimitTimes = dailyStep_weeklyLimitTimes;
		} else {
			throw new RuntimeException(
					"setDailyStep_weeklyLimitTimes: out of range: "
							+ dailyStep_weeklyLimitTimes);
		}

	}

	private int getWeeklySport_weeklyLimitTimes() {
		return weeklySport_weeklyLimitTimes;
	}

	private void setWeeklySport_weeklyLimitTimes(
			int weeklySport_weeklyLimitTimes) {
		LogUtil.MSG.debug("setWeeklySport_weeklyLimitTimes: "
				+ weeklySport_weeklyLimitTimes);
		if (weeklySport_weeklyLimitTimes >= 0) {
			this.weeklySport_weeklyLimitTimes = weeklySport_weeklyLimitTimes;
		} else {
			throw new RuntimeException(
					"setWeeklySport_weeklyLimitTimes: out of range: "
							+ weeklySport_weeklyLimitTimes);
		}

	}

	@Override
	public List<String> getVaildKeywords(String msgType) {
		if (msgType.equals(MsgTypeEnum.TEXT.getType())) {
			return Config.TEXT_MSG_WEEKLY_REPORT_VAILD_KEYWORD_LIST;
		} else {
			return new ArrayList<String>();
		}

	}

	@Override
	public String handleClockInCustomized(Group g, BaseMsg msg) {
		LogUtil.MSG
				.debug("handleClockInCustomized: "
						+ this.getClass().getSimpleName() + ", "
						+ g.getGroupNickName());
		String result = null;
		String currentGroupNickName = g.getGroupNickName();
		File dir = new File(CURRENT_WEEK_SAVE_PATH.getAbsolutePath());
		List<String> list = WechatTools
				.getMemberListByGroupNickName2(currentGroupNickName);
		LogUtil.MSG.debug("handleClockInCustomized: " + currentGroupNickName
				+ "群成员:" + list.toString());
		String error404Name = "";
		Group group = null;
		for (Group gr : Constant.groupList) {
			if (gr.getGroupNickName().equals(currentGroupNickName)) {
				group = gr;
				break;
			}
		}
		if (group != null) {
			boolean supportSport = false;
			boolean supportStep = false;
			if (group.getAllVaildTextMsgKeyword().contains(
					Config.TEXT_MSG_WEEKLY_REPORT_VAILD_KEYWORD_LIST.get(0))) {
				supportSport = true;
			}
			if (group.getAllVaildTextMsgKeyword().contains(
					Config.TEXT_MSG_WEEKLY_REPORT_VAILD_KEYWORD_LIST.get(0))) {
				supportStep = true;
			}
			if (supportSport == true || supportStep == true) {
				if (dir.isDirectory()) {
					File[] array = dir.listFiles();
					for (int i = 0; i < list.size(); i++) {
						int countstep = 0;
						int countsport = 0;
						String name = StringUtil.filter(list.get(i));
						if (StringUtil.ifNullOrEmpty(name)) {
							error404Name = error404Name + "@" + list.get(i)
									+ " ";
							continue;
						}
						for (int j = 0; j < array.length; j++) {

							if ((array[j].isFile()
									&& array[j].getName().endsWith(".step") && array[j]
									.getName().contains(list.get(i)))
									|| (array[j].isFile()
											&& array[j].getName().endsWith(
													".step") && array[j]
											.getName().contains(name))) {
								countstep++;
							}
							if ((array[j].isFile()
									&& array[j].getName().endsWith(".sport") && array[j]
									.getName().contains(list.get(i)))
									|| (array[j].isFile()
											&& array[j].getName().endsWith(
													".sport") && array[j]
											.getName().contains(name))) {
								countsport++;
							}
						}
						if (result == null) {
							result = WeekHelper.getCurrentWeek() + "\n";
							result = result + list.get(i) + ":";
							if (supportSport) {
								result = result + "	运动" + countsport + "/"
										+ getWeeklySport_weeklyLimitTimes();
							}
							if (supportStep) {
								result = result + "	步数" + countstep + "/"
										+ getDailyStep_weeklyLimitTimes();
							}
							result = result + "；\n";
						} else {
							result = result + list.get(i) + ":";
							if (supportSport) {
								result = result + "	运动" + countsport + "/"
										+ getWeeklySport_weeklyLimitTimes();
							}
							if (supportStep) {
								result = result + "	步数" + countstep + "/"
										+ getDailyStep_weeklyLimitTimes();
							}
							result = result + "；\n";
						}
					}
					if (!StringUtil.ifNullOrEmpty(error404Name)) {
						result = result + "\n如下（微信名含非法字符无法统计: " + error404Name
								+ "）";
					}
					List<ClockIn> clockIns = group.getClockInList();
					for (ClockIn clockIn : clockIns) {
						String content = "\n" + clockIn.reportProcess(group);
						if (content != null) {
							result = result + content;
						}
					}
				} else {
					LOG.error("handleClockInCustomized: " + "error:"
							+ dir.getAbsolutePath() + "非文件夹路径！");
				}
			}
		}

		return result;
	}

	private String handleClockInCustomized(Group g) {
		return handleClockInCustomized(g, null);
	}

	@Override
	public boolean reportProcessRegularly(Group currentGroup) {
		Date currentDate = new Date();
		int currentTime = Integer.parseInt(new SimpleDateFormat("HHmm")
				.format(currentDate));
		String currentTimeString = new SimpleDateFormat("HH:mm")
				.format(currentDate);
		LogUtil.MSG.debug("reportProcessRegularly: " + "currentTime: "
				+ currentTime + ",autoReportTime: " + autoReportTime);
		if (currentTime == autoReportTime) {
			String currentGroupNickName = currentGroup.getGroupNickName();
			MessageTools.sendGroupMsgByNickName(currentTimeString
					+ "，开始进行本周播报。不达标的人需要发红包。", currentGroupNickName);
			String result = handleClockInCustomized(currentGroup);
			MessageTools.sendGroupMsgByNickName(result, currentGroupNickName);
			LOG.info("reportProcessRegularly: "
					+ currentGroup.getGroupNickName() + ": report " + true);
			return true;
		}
		return false;

	}

}