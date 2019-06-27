package org.androidtest.vince;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidtest.vince.data.Constant;
import org.androidtest.vince.data.Group;
import org.androidtest.vince.factory.ClockInFactory;
import org.androidtest.vince.factory.CustomizedFactory;

/**
 * 配置类
 *
 * @author caipeipei
 */
@SuppressWarnings("serial")
public class Config {

	public static final boolean DEBUG = false;// 全局的代码调试开关，打开后会一键进行测试模块，会在短时间里把各个组件的主动触发功能执行一遍

	/**
	 * 需要配置的群白名单和该群所需的组件信息。 可选组件的工厂见org.androidtest.vince.factory
	 */
	public static void initGroupAndAdmin() {
		Group group = new Group("彭于晏已退出群聊", Constant.DEFAULT_ADMIN);// 新建群信息
		group.addClockIn(ClockInFactory.createWeeklySportClockIn(2));// 配置该群有运动打卡功能，要求一周2次打卡
		group.addClockIn(ClockInFactory.createDailyStepClockIn());// 配置该群有每日步数功能
		group.addCustomized(CustomizedFactory
				.createLifeRoutineClockInCustomized(true, 0731, true, 2230));// 配置该群有作息打卡功能，设定每天0731晚起提醒和2230睡觉提醒
		group.addCustomized(CustomizedFactory
				.createWeeklyReportClockInCustomized(7, 2));// 配置该群有周报功能，周报功能会统计并播报运动打卡和每日步数数据
		Map<String, File> whiteList = new HashMap<String, File>();
		whiteList
				.put("Vince蔡培培",
						new File(
								"C:\\Users\\Administrator\\Desktop\\DAILY CHECK LIST @Vince蔡培培.xlsx"));
		group.addCustomized(CustomizedFactory
				.createDailySelfReflectionCustomized(whiteList, true));// 配置该群有每日反思打卡功能，需要配置反思人及对应的反思excel文件地址
		Constant.groupList.add(group);

	}

	/**
	 * DailyStepClockIn类的合法参数
	 */
	public static final List<String> TEXT_MSG_WEEKLY_SPORT_VAILD_KEYWORD_LIST = new ArrayList<String>() {
		{
			add("运动打卡");
			add("打卡运动");
		}
	};

	/**
	 * WeeklySportClockIn类的合法参数
	 */
	public static final List<String> TEXT_MSG_WEEKLY_STEP_VAILD_KEYWORD_LIST = new ArrayList<String>() {
		{
			add("步数打卡");
			add("打卡步数");
		}
	};

	/**
	 * WeeklyReportClockInCustomized类的合法参数
	 */
	public static final List<String> TEXT_MSG_WEEKLY_REPORT_VAILD_KEYWORD_LIST = new ArrayList<String>() {
		{
			add("周报");
		}
	};

	/**
	 * LifeRoutineClockInCustomized类的合法参数
	 */
	public static final List<String> SYS_MSG_LIFE_ROUTINE_VAILD_KEYWORD_LIST = new ArrayList<String>() {
		{
			add("收到红包，请在手机上查看");
			add("Red packet received. View on phone.");
		}
	};

	/**
	 * DailySelfReflectionCustomized类的合法参数
	 */
	public static final List<String> MEDIA_MSG_DAILY_SELF_REFLECTION_VAILD_KEYWORD_LIST = new ArrayList<String>() {
		{
			add(Constant.DAILY_SELF_REFLECTION_DEFAULT_FILENAME_TEMPLATES
					+ ":xlsx");
			add(Constant.DAILY_SELF_REFLECTION_DEFAULT_FILENAME_TEMPLATES
					+ ":xls");
		}
	};

}
