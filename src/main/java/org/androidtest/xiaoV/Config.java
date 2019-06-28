package org.androidtest.xiaoV;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.androidtest.xiaoV.data.Constant;
import org.androidtest.xiaoV.data.Group;
import org.androidtest.xiaoV.factory.ActionFactory;

import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;

/**
 * 配置类
 *
 * @author caipeipei
 */
@SuppressWarnings("serial")
public class Config {

	public static final boolean DEBUG = false;// 全局的代码调试开关，打开后会一键进行测试模块，会在短时间里把各个组件的主动触发功能执行一遍

	/**
	 * MenuAction类的合法参数及参数类型
	 */
	public static final Map<String, MsgTypeEnum> MENU_VAILD_KEYWORD_LIST = new HashMap<String, MsgTypeEnum>() {
		{
			put("菜单", MsgTypeEnum.TEXT);
		}
	};

	/**
	 * 需要配置的群白名单和该群所需的组件信息。 可选组件的工厂见org.androidtest.vince.factory
	 */
	public static void initGroupAndAdmin() {
		Group group = new Group("彭于晏已退出群聊", Constant.DEFAULT_ADMIN);// 新建群信息
		group.addAction(ActionFactory.createWeeklySportClockIn(2));// 配置该群有运动打卡功能，要求一周2次打卡
		group.addAction(ActionFactory.createDailyStepClockIn());// 配置该群有每日步数功能
		group.addAction(ActionFactory.createLifeRoutineClockInAction(true,
				0731, true, 2230));// 配置该群有作息打卡功能，设定每天0731晚起提醒和2230睡觉提醒
		group.addAction(ActionFactory.createWeeklyReportClockInAction(7, 2));// 配置该群有周报功能，周报功能会统计并播报运动打卡和每日步数数据
		Map<String, File> whiteList = new HashMap<String, File>();
		whiteList
				.put("Vince蔡培培",
						new File(
								"C:\\Users\\Administrator\\Desktop\\DAILY CHECK LIST @Vince蔡培培.xlsx"));
		group.addAction(ActionFactory.createDailySelfReflectionAction(
				whiteList, true));// 配置该群有每日反思打卡功能，需要配置反思人及对应的反思excel文件地址

		Constant.groupList.add(group);

	}

}
