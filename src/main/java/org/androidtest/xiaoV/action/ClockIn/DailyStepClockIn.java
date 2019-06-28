package org.androidtest.xiaoV.action.ClockIn;

import static org.androidtest.xiaoV.data.Constant.CURRENT_WEEK_SAVE_PATH;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidtest.xiaoV.Config;
import org.androidtest.xiaoV.data.Constant;
import org.androidtest.xiaoV.data.Group;
import org.androidtest.xiaoV.publicutil.LogUtil;
import org.androidtest.xiaoV.publicutil.StringUtil;
import org.androidtest.xiaoV.publicutil.WeekHelper;

import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;

/**
 * 每日步数打卡组件
 * 
 * @author caipeipei
 *
 */
public class DailyStepClockIn extends ClockIn {

	public DailyStepClockIn(int weeklyLimitTimes) {
		super(weeklyLimitTimes);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<String> getVaildKeywords(MsgTypeEnum type) {// TODO
															// 待重构，可以在基类里使用减少代码量
		List<String> list = new ArrayList<String>();
		Iterator iter = Config.DAILY_STEP_VAILD_KEYWORD_LIST.entrySet()
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
	public String action(Group group, BaseMsg msg) {
		LogUtil.MSG.debug("action: " + this.getClass().getSimpleName());

		String currentGroupNickName = WechatTools
				.getGroupNickNameByGroupUserName(msg.getFromUserName());
		String senderNickName = WechatTools
				.getMemberDisplayOrNickNameByGroupNickName(
						currentGroupNickName, msg.getStatusNotifyUserName());
		String result = null;
		String fileUserName = StringUtil.filter(senderNickName);
		if (StringUtil.ifNullOrEmpty(fileUserName)) {
			result = "@" + senderNickName + " 你的名字有无法识别的字符，无法处理！请改昵称。";
			LogUtil.MSG.warn("action: " + result);
			return result;
		}

		String stepfilename = Constant.SIMPLE_DAY_FORMAT_FILE
				.format(new Date()) + "-" + senderNickName + ".step";
		File stepfile = new File(
				Constant.CURRENT_WEEK_SAVE_PATH.getAbsolutePath()
						+ File.separator + stepfilename);
		try {
			boolean isExist = false;
			if (stepfile.exists()) {
				isExist = true;
			} else {
				stepfile.createNewFile();
			}
			File dir = new File(
					Constant.CURRENT_WEEK_SAVE_PATH.getAbsolutePath());
			if (dir.isDirectory()) {
				File[] array = dir.listFiles();
				int count = 0;
				for (int i = 0; i < array.length; i++) {
					if (array[i].isFile()
							&& array[i].getName().endsWith(".step")
							&& array[i].getName().contains(fileUserName)) {
						count++;
					}
				}
				if (isExist) {
					result = "@" + senderNickName + " 你今天已经步数打卡过，无需重复打卡。"
							+ WeekHelper.getCurrentWeek() + "步数打卡已完成了" + count
							+ "次，再接再励！";
				} else {
					result = "@" + senderNickName + " 今天步数打卡成功！"
							+ WeekHelper.getCurrentWeek() + "步数打卡已完成了" + count
							+ "次，继续保持。";
				}

			} else {
				LogUtil.MSG.error("action: " + "error:" + dir.getAbsolutePath()
						+ "非文件夹路径！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String report(Group group) {
		LogUtil.MSG.debug("report: " + this.getClass().getSimpleName() + ", "
				+ group.getGroupNickName());
		String currentGroupNickName = group.getGroupNickName();
		String result = null;
		File dir = CURRENT_WEEK_SAVE_PATH;
		List<String> list = WechatTools
				.getMemberListByGroupNickName2(currentGroupNickName);
		LogUtil.MSG.debug("report: " + currentGroupNickName + "群成员:"
				+ list.toString());
		String errorStep = null;
		String error404Name = "";
		String todaystepkeyword = Constant.SIMPLE_DAY_FORMAT_FILE
				.format(new Date());
		boolean isCurrentUserExistTodayStep = false;
		if (dir.isDirectory()) {
			File[] array = dir.listFiles();
			for (int i = 0; i < list.size(); i++) {
				isCurrentUserExistTodayStep = false;
				String name = StringUtil.filter(list.get(i));
				if (StringUtil.ifNullOrEmpty(name)) {
					error404Name = error404Name + "@" + list.get(i) + " ";
					continue;
				}
				for (int j = 0; j < array.length; j++) {
					if ((array[j].isFile()
							&& array[j].getName().endsWith(".step") && array[j]
							.getName().contains(list.get(i)))
							|| (array[j].isFile()
									&& array[j].getName().endsWith(".step") && array[j]
									.getName().contains(name))) {
						if (array[j].getName().contains(todaystepkeyword)) {
							isCurrentUserExistTodayStep = true;
							break;
						}
					}
				}
				if (!isCurrentUserExistTodayStep) {
					if (errorStep == null) {
						errorStep = "@" + list.get(i) + " ";
					} else {
						errorStep = errorStep + "@" + list.get(i) + " ";
					}
				}
			}
			result = "------今日步数未完成：-------\n" + errorStep;
			if (!StringUtil.ifNullOrEmpty(error404Name)) {
				result = result + "\n如下（微信名含非法字符无法统计: " + error404Name + "）";
			}
		} else {
			LogUtil.MSG.error("report: " + "reportProcess: " + "error:"
					+ dir.getAbsolutePath() + "非文件夹路径！");
		}
		return result;
	}

	@Override
	public boolean notify(Group group) {
		return false;
	}
}
