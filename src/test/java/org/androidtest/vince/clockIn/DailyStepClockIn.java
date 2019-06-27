package org.androidtest.vince.clockIn;

import static org.androidtest.vince.data.Constant.CURRENT_WEEK_SAVE_PATH;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.androidtest.vince.Config;
import org.androidtest.vince.data.Constant;
import org.androidtest.vince.data.Group;
import org.androidtest.vince.publicutil.LogUtil;
import org.androidtest.vince.publicutil.StringUtil;
import org.androidtest.vince.publicutil.WeekHelper;

import cn.zhouyafeng.itchat4j.api.WechatTools;

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
	public List<String> getVaildKeywords() {
		return Config.TEXT_MSG_WEEKLY_STEP_VAILD_KEYWORD_LIST;
	}

	@Override
	public String handleClockIn(String senderNickName) {
		LogUtil.MSG.debug("handleClockIn: " + this.getClass().getSimpleName()
				+ ", args: " + senderNickName);
		String result = null;
		String fileUserName = StringUtil.filter(senderNickName);
		if (StringUtil.ifNullOrEmpty(fileUserName)) {
			result = "@" + senderNickName + " 你的名字有无法识别的字符，无法处理！请改昵称。";
			LOG.warn("handleClockIn: " + result);
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
				LOG.error("handleClockIn: " + "error:" + dir.getAbsolutePath()
						+ "非文件夹路径！");
			}
		} catch (IOException e) {
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
			LOG.error("reportProcess: " + "error:" + dir.getAbsolutePath()
					+ "非文件夹路径！");
		}
		return result;
	}
}
