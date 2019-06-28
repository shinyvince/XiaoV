package org.androidtest.xiaoV.action;

import static org.androidtest.xiaoV.data.Constant.CURRENT_WEEK_SAVE_PATH;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidtest.xiaoV.Config;
import org.androidtest.xiaoV.action.ClockIn.ClockIn;
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
public class WeeklyReportAction extends Action {

	private int dailyStep_weeklyLimitTimes = -1;
	private int weeklySport_weeklyLimitTimes = -1;

	private final int autoReportTime = 2359;

	public WeeklyReportAction(int dailyStep_weeklyLimitTimes,
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
	@SuppressWarnings("rawtypes")
	public List<String> getVaildKeywords(MsgTypeEnum type) {// TODO
															// 待重构，可以在基类里使用减少代码量
		List<String> list = new ArrayList<String>();
		Iterator iter = Config.WEEKLY_REPORT_VAILD_KEYWORD_LIST.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String vaildKeyword = (String) entry.getKey();
			MsgTypeEnum vaildType = (MsgTypeEnum) entry.getValue();
			if (vaildType == type) {
				list.add(vaildKeyword);
			}
		}
		return list;
	}

	@Override
	public String action(Group g, BaseMsg msg) {
		LogUtil.MSG.debug("action: " + this.getClass().getSimpleName() + ", "
				+ g.getGroupNickName());
		String result = null;
		String currentGroupNickName = g.getGroupNickName();
		File dir = new File(CURRENT_WEEK_SAVE_PATH.getAbsolutePath());
		List<String> list = WechatTools
				.getMemberListByGroupNickName2(currentGroupNickName);
		LogUtil.MSG.debug("action: " + currentGroupNickName + "群成员:"
				+ list.toString());
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
			if (isMapContainsKey(Config.WEEKLY_SPORT_VAILD_KEYWORD_LIST,
					group.getAllVaildKeyword(MsgTypeEnum.TEXT))) {
				supportSport = true;
			}
			if (isMapContainsKey(Config.DAILY_STEP_VAILD_KEYWORD_LIST,
					group.getAllVaildKeyword(MsgTypeEnum.TEXT))) {
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

					List<Action> actions = group.getActionList();
					for (Action a : actions) {
						if (a.getClass().getGenericSuperclass() == ClockIn.class) {
							String content = a.report(group);
							if (StringUtil.ifNotNullOrEmpty(content)) {
								content = "\n" + content;
								result = result + content;
							}
						}
					}
				} else {
					LogUtil.MSG.error("action: " + "error:"
							+ dir.getAbsolutePath() + "非文件夹路径！");
				}
			}
		}

		return result;
	}

	@Override
	public String report(Group group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean notify(Group currentGroup) {
		Date currentDate = new Date();
		int currentTime = Integer.parseInt(new SimpleDateFormat("HHmm")
				.format(currentDate));
		String currentTimeString = new SimpleDateFormat("HH:mm")
				.format(currentDate);
		LogUtil.MSG.debug("notify: " + "currentTime: " + currentTime
				+ ",autoReportTime: " + autoReportTime);
		if (currentTime == autoReportTime) {
			String currentGroupNickName = currentGroup.getGroupNickName();
			MessageTools.sendGroupMsgByNickName(currentTimeString
					+ "，开始进行本周播报。不达标的人需要发红包。", currentGroupNickName);
			String result = action(currentGroup, null);
			MessageTools.sendGroupMsgByNickName(result, currentGroupNickName);
			LogUtil.MSG.info("notify: " + currentGroup.getGroupNickName()
					+ ": report " + true);
			return true;
		}
		return false;
	}

	private boolean isMapContainsKey(Map<String, MsgTypeEnum> map,
			List<String> list) {
		for (String string : list) {
			if (map.containsKey(string)) {
				return true;
			}
		}
		return false;
	}
}