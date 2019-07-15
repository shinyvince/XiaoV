package org.androidtest.xiaoV.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.androidtest.xiaoV.action.Action;
import org.androidtest.xiaoV.chat.TulingRobot;
import org.androidtest.xiaoV.data.Constant;
import org.androidtest.xiaoV.data.Group;
import org.androidtest.xiaoV.publicutil.LogUtil;
import org.androidtest.xiaoV.publicutil.StringUtil;
import org.apache.log4j.Logger;

import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;

public class MessageHandler {
	private static Logger LOG = Logger.getLogger(MessageHandler.class);

	private static String robotCall = "@" + Core.getInstance().getNickName();

	/**
	 * 处理群消息
	 * 
	 * @param msg
	 * @return
	 */
	public static String groupMsgHandle(Group group, BaseMsg msg) {
		LogUtil.MSG.debug("groupMsgHandle: " + group + ", " + msg);

		String result = null;
		String currentGroupNickName = WechatTools
				.getGroupNickNameByGroupUserName(msg.getFromUserName());
		if (isCurrentMsgFromVaildGroup(group, currentGroupNickName)) {
			final String content = msg.getText();
			String robotDisplayName = WechatTools
					.getMemberDisplayNameByGroupNickName(currentGroupNickName,
							msg.getToUserName());
			if (StringUtil.ifNullOrEmpty(robotDisplayName)) {
				robotDisplayName = robotCall;
			}
			String keyword = returnSpecificKeywordFromTextMsg(group, msg);
			Action action = group.getActionFromVaildKeywords(keyword,
					MsgTypeEnum.TEXT);

			if (keyword != null) {
				if (action != null) {
					result = action.action(group, msg);
				}
			} else {
				LOG.info("groupMsgHandle: " + "非法参数: " + content
						+ "，消息过滤不处理.result=" + result);
			}
			if ((result == null && content.contains(robotCall))
					|| (result == null && content.contains(robotDisplayName))) {
				LogUtil.MSG.debug("groupMsgHandle: result: " + result
						+ ",content:" + content + ",robotCall: " + robotCall
						+ ",robotDisplayName: " + robotDisplayName);
				String nickName = WechatTools
						.getMemberDisplayOrNickNameByGroupNickName(
								group.getGroupNickName(),
								msg.getStatusNotifyUserName());
				result = TulingRobot.chat(nickName, content);
				if (StringUtil.ifNullOrEmpty(result)) {
					result = "我听不懂，需要\"菜单\"请回复菜单";
				}

			}
		}
		return result;
	}

	private static boolean isCurrentMsgFromVaildGroup(Group group,
			String currentGroupNickName) {
		return group.getGroupNickName().equals(currentGroupNickName);
	}

	// public static String handleFunctionGroupMessage() {
	// String result = "【运动打卡】\n";
	// result = result + "【步数打卡】\n";
	// result = result + "【统计及查看本周运动情况】\n";
	// result = result + "【睡觉打卡（21:00~04:00）】\n";
	// result = result + "【睡觉打卡提醒（22:30）】\n";
	// result = result + "【早起超时提醒（07:30）】\n";
	// result = result + "【每日播报（23:59）】\n";
	// result = result + "【菜单】\n";
	// result = result + "【管理员功能】（管理员私聊机器人生效）\n";
	// result = result + "- 后台LOG调试开/关\n";
	// result = result + "- 具体参数调教\n";
	// result = result + "- 本周后台数据呈现\n";
	// result = result + "- 删除本周指定数据\n";
	// result = result + "- 将要机器人响应的群加添加群白名单\n";
	// result = result + "- 将要取消机器人响应的群移除出群白名单\n";
	// result = result + "- 退出机器人登录";
	// return result;
	// }

	// public static void handleRuleGroupMessage(String currentGroupNickName) {
	// MessageTools.sendPicMsgByGroupNickName(currentGroupNickName,
	// Constant.DATA_SAVE_PATH + File.separator + "rule.jpg");
	// }

	// public static String handleMenuGroupMessage() {
	// String result = "【运动打卡】回复: 运动打卡\n";
	// result = result + "【步数打卡】回复: 步数打卡\n";
	// result = result + "【查看本周数据情况】回复: 周报\n";
	// result = result + "【功能说明】回复: 功能\n";
	// result = result + "【作息+运动规则说明】回复: 规则\n";
	// result = result + "【查看主菜单】回复: 菜单";
	// return result;
	// }

	// /**
	// * 是否是指定的个人消息文本内容
	// *
	// * @param content
	// * @return
	// */
	// public static int isVaildUserTextKeyword(String content) {
	// for (int i = 0; i < VAILD_USER_TEXT_KEYWORD.size(); i++) {
	// if (content.startsWith(VAILD_USER_TEXT_KEYWORD.get(i))) {
	// return i;
	// }
	// }
	// return -1;
	// }

	// /**
	// * 处理个人消息
	// *
	// * @param msg
	// * @return
	// */
	// public static String userMsgHandle(BaseMsg msg) {
	// String result = null;
	// final String content = msg.getText();
	// int index_of_vaild_text = isVaildUserTextKeyword(content);
	// if (index_of_vaild_text != -1) {
	// switch (VAILD_USER_TEXT_KEYWORD.get(index_of_vaild_text)) {
	// case "退出":
	// result = handleLogoutUserMessage();
	// break;
	// case "调试开":
	// result = handleLogDebugUserMessage(true);
	// break;
	// case "调试关":
	// result = handleLogDebugUserMessage(false);
	// break;
	// case "时间调试开":
	// result = handleTimeDebugUserMessage(true);
	// break;
	// case "时间调试关":
	// result = handleTimeDebugUserMessage(false);
	// break;
	// case "本周数据":
	// result = handleWeeklyReportUserMessage();
	// break;
	// case "删除":
	// result = handleDeleteFileUserMessage(content);
	// break;
	// case "加群白名单":
	// result = handleAddWhiteGroupUserMessage(msg);
	// break;
	// case "移除群白名单":
	// result = handleRemoveWhileGroupUserMessage(content);
	// break;
	// case "刷新":
	// // handleUpdateContactSysMessage();
	// // result = "微信群和联系人信息刷新完毕";
	// break;
	// case "睡前监控":
	// result = handleStartSleepRedPacketCountUserMessage(content);
	// break;
	// case "菜单":
	// result = handleMenuUserMessage();
	// break;
	// default:
	// break;
	// }
	// } else {
	// LOG.info("非法参数: " + content + "，消息过滤不处理.result=" + result);
	// }
	// if (result == null && content.contains(robotCall)) {
	// result = "我听不懂（怪老板咯），需要\"菜单\"请回复菜单";
	// }
	// return result;
	// }

	// public static String handleLogoutUserMessage() {
	// WechatTools.logout();
	// String result = "机器人已经下线";
	// return result;
	// }

	// public static String handleLogDebugUserMessage(boolean isOn) {
	// LOG_DEBUG = isOn;
	// return isOn ? "LOG调试已经打开" : "LOG调试已经关闭";
	// }
	//
	// public static String handleTimeDebugUserMessage(boolean isOn) {
	// TIME_DEBUG = isOn;
	// String result;
	// if (isOn) {
	// result = "时间调试已经打开";
	// } else {
	// result = "时间调试已经关闭";
	// if (startSleepRedPacketCountTime !=
	// DEFAULT_START_SLEEP_RED_PACKET_COUNT_TIME) {
	// startSleepRedPacketCountTime = DEFAULT_START_SLEEP_RED_PACKET_COUNT_TIME;
	// result = result + ",睡前红包监控时间恢复默认值:"
	// + startSleepRedPacketCountTime;
	// }
	// }
	// return result;
	// }

	// public static String handleWeeklyReportUserMessage() {
	// String result = null;
	// File dir = new
	// File(Constant.Constant.getCurrentWeekSavePath().getAbsolutePath());
	// if (dir.isDirectory()) {
	// File[] array = dir.listFiles();
	// if (array.length > 0) {
	// result = WeekHelper.getCurrentWeek() + "\n";
	// for (File file : array) {
	// result = result + file.getName() + ",\n";
	// }
	// result = result.substring(0, result.lastIndexOf(","));
	// } else {
	// result = WeekHelper.getCurrentWeek() + "\n无";
	// }
	// }
	// return result;
	// }

	// public static String handleAddWhiteGroupUserMessage(BaseMsg msg) {
	// String text = msg.getText();
	// String result = "格式有误。";
	// if (text.contains(Constant.COLON_SPLIT)) {
	// String[] str = text.split(Constant.COLON_SPLIT);
	// String currentAdmin = DEFAULT_ADMIN;
	// if (str.length > 2) {
	// currentAdmin = text.split(Constant.COLON_SPLIT)[2];
	// }
	// String currentGroup = text.split(Constant.COLON_SPLIT)[1];
	// Group group = new Group(currentGroup, currentAdmin);
	// group.setGroupId(WechatTools
	// .getGroupNameByGroupNickName(currentGroup));
	// // group.setGroupNickName(currentGroup);
	// // group.setMemberList(WechatTools
	// // .getMemberListByGroupNickName(currentGroup));
	// // group.setAdmin(currentAdmin);
	// // group.setRedPacketCount(0);
	// result = str + "已添加进群白名单里";
	// }
	// return result;
	// }

	// public static String handleStartSleepRedPacketCountUserMessage(String
	// text) {
	// String result = "格式有误。";
	// if (text.contains(Constant.COLON_SPLIT)) {
	// try {
	// String str = text.split(Constant.COLON_SPLIT)[1];
	// startSleepRedPacketCountTime = Integer.parseInt(str);
	// REFLASH_A_NEW_DAY = true;
	// result = "睡前红包监控开启时间已经更新为:" + startSleepRedPacketCountTime;
	// } catch (Exception e) {
	// result = "格式有误。";
	// }
	// }
	// return result;
	// }

	// public static String handleRemoveWhileGroupUserMessage(String text) {
	// String result = null;
	// if (text.contains(Constant.COLON_SPLIT)) {
	// String currentGroup = text.split(Constant.COLON_SPLIT)[1];
	// int index = -1;
	// for (int i = 0; i < groupList.size(); i++) {
	// if (groupList.get(i).getGroupNickName().equals(currentGroup)) {
	// index = i;
	// break;
	// }
	// }
	// if (index != -1) {
	// groupList.remove(index);
	// result = currentGroup + "已从群白名单里移除";
	// } else {
	// result = currentGroup + "本来就没在群白名单里";
	// }
	// }
	// return result;
	// }

	// public static String handleDeleteFileUserMessage(String text) {
	// String result = "格式有误。";
	// if (text.contains(Constant.COLON_SPLIT)) {
	// try {
	// String str = text.split(Constant.COLON_SPLIT)[1];
	// File file = new File(Constant.Constant.getCurrentWeekSavePath()
	// + File.separator + str);
	// if (file.exists()) {
	// file.delete();
	// result = str + "文件已经删除成功。";
	// }
	// } catch (Exception e) {
	// result = "格式有误。";
	// }
	// }
	// return result;
	// }

	// public static String handleMenuUserMessage() {
	// String result = "【退出】回复: 退出\n";
	// result = result + "【后台LOG调试开】回复: 调试开\n";
	// result = result + "【后台LOG调试关】回复: 调试关\n";
	// result = result + "【后台时间调试开】回复: 时间调试开\n";
	// result = result + "【后台时间调试关】回复: 时间调试关\n";
	// result = result + "【本周数据】回复: 本周数据\n";
	// result = result + "【删除指定本周数据】回复: 删除:文件名\n";
	// result = result + "【加群白名单】回复: 加群白名单:群名:管理员名\n";
	// result = result + "【移除群白名单】回复: 移除群白名单:群名\n";
	// result = result + "【睡前红包监控时间变更】回复: 睡前监控:小时分钟\n";
	// result = result + "【查看主菜单】回复: 菜单";
	// return result;
	// }

	/**
	 * 处理多媒体消息
	 * 
	 * @param msg
	 * @return
	 */
	public static String mediaMsgHandle(Group group, BaseMsg msg) {
		LogUtil.MSG.debug("mediaMsgHandle: " + group + ", " + msg);
		String result = null;
		String currentGroupNickName = WechatTools
				.getGroupNickNameByGroupUserName(msg.getFromUserName());
		if (isCurrentMsgFromVaildGroup(group, currentGroupNickName)) {
			String keyword = returnSpecificKeywordFromMediaMsg(group, msg);
			Action customized = group.getActionFromVaildKeywords(keyword,
					MsgTypeEnum.MEDIA);
			if (keyword != null) {
				if (customized != null) {
					result = customized.action(group, msg);
				}
			} else {
				String fileName = Constant.SIMPLE_DATE_FORMAT_FILE
						.format(new Date()) + "-" + msg.getFileName();
				String filePath = Constant.getCurrentWeekSavePath()
						+ File.separator + fileName; // 这里是需要保存收到的文件路径，文件可以是任何格式如PDF，WORD，EXCEL等。
				DownloadTools.getDownloadFn(msg, MsgTypeEnum.MEDIA.getType(),
						filePath);
			}
		}
		return result;
	}

	private static String returnSpecificKeywordFromMediaMsg(Group group,
			BaseMsg msg) {
		LogUtil.MSG.debug("returnSpecificKeywordFromMediaMsg: " + group + ", "
				+ msg);
		List<String> currentGroupVaildKeyword = new ArrayList<String>();
		currentGroupVaildKeyword.addAll(group
				.getAllVaildKeyword(MsgTypeEnum.MEDIA));
		String content = msg.getContent();

		if (StringUtil.ifNotNullOrEmpty(currentGroupVaildKeyword)) {
			for (int i = 0; i < currentGroupVaildKeyword.size(); i++) {

				String expect = currentGroupVaildKeyword.get(i);
				if (expect.contains(Constant.COLON_SPLIT)) {
					if (content.contains(expect.split(Constant.COLON_SPLIT)[0])
							&& content.contains(expect
									.split(Constant.COLON_SPLIT)[1])) {
						LogUtil.MSG
								.debug("returnSpecificKeywordFromMediaMsg: return "
										+ currentGroupVaildKeyword.get(i));
						return currentGroupVaildKeyword.get(i);
					}
				}
				if (content.contains(expect)) {
					LogUtil.MSG
							.debug("returnSpecificKeywordFromMediaMsg: return "
									+ currentGroupVaildKeyword.get(i));
					return currentGroupVaildKeyword.get(i);
				}
			}
		}
		LogUtil.MSG.debug("returnSpecificKeywordFromMediaMsg: return " + null);
		return null;
	}

	// private static void handleUpdateContactSysMessage() {
	// ILoginService loginService = new LoginServiceImpl();
	// loginService.webWxGetContact();
	// loginService.WebWxBatchGetContact();
	// }

	// private static int isVaildMediaTextKeyword(String content) {
	// for (int i = 0; i < VAILD_MEDIA_TEXT_KEYWORD.size(); i++) {
	// String expect = VAILD_MEDIA_TEXT_KEYWORD.get(i);
	// if (expect.contains(Constant.COLON_SPLIT)) {
	// if (content.contains(expect.split(Constant.COLON_SPLIT)[0])
	// && content
	// .contains(expect.split(Constant.COLON_SPLIT)[1])) {
	// return i;
	// }
	// }
	// if (content.contains(expect)) {
	// return i;
	// }
	// }
	// return -1;
	// }

	/**
	 * 是否是指定的系统消息文本内容
	 * 
	 * @param content
	 * @return
	 */
	private static String returnSpecificKeywordFromSysMsg(Group group,
			BaseMsg msg) {
		LogUtil.MSG.debug("returnSpecificKeywordFromSysMsg: " + group + ", "
				+ msg);
		List<String> currentGroupVaildKeyword = new ArrayList<String>();
		currentGroupVaildKeyword.addAll(group
				.getAllVaildKeyword(MsgTypeEnum.SYS));
		String content = msg.getContent();
		if (StringUtil.ifNotNullOrEmpty(currentGroupVaildKeyword)) {
			for (int i = 0; i < currentGroupVaildKeyword.size(); i++) {
				String expect = currentGroupVaildKeyword.get(i);
				if (expect.contains(Constant.COLON_SPLIT)) {
					if (content.contains(expect.split(Constant.COLON_SPLIT)[0])
							&& content.contains(expect
									.split(Constant.COLON_SPLIT)[1])) {
						LogUtil.MSG
								.debug("returnSpecificKeywordFromSysMsg: return "
										+ currentGroupVaildKeyword.get(i));
						return currentGroupVaildKeyword.get(i);
					}
				}
				if (content.contains(expect)) {
					LogUtil.MSG
							.debug("returnSpecificKeywordFromSysMsg: return "
									+ currentGroupVaildKeyword.get(i));
					return currentGroupVaildKeyword.get(i);
				}
			}
		}
		LogUtil.MSG.debug("returnSpecificKeywordFromSysMsg: return " + null);
		return null;
	}

	/**
	 * 是否是指定的群消息文本内容
	 * 
	 * @param content
	 * @return
	 */
	private static String returnSpecificKeywordFromTextMsg(Group group,
			BaseMsg msg) {
		LogUtil.MSG.debug("returnSpecificKeywordFromTextMsg: " + group + ", "
				+ msg);
		List<String> currentGroupVaildKeyword = new ArrayList<String>();
		currentGroupVaildKeyword.addAll(group
				.getAllVaildKeyword(MsgTypeEnum.TEXT));
		String content = msg.getText();
		String robotDisplayName = WechatTools
				.getMemberDisplayOrNickNameByGroupNickName(
						group.getGroupNickName(), msg.getToUserName());
		if (StringUtil.ifNotNullOrEmpty(currentGroupVaildKeyword)) {
			for (int i = 0; i < currentGroupVaildKeyword.size(); i++) {
				if (content.startsWith(currentGroupVaildKeyword.get(i))
						|| (content.contains(currentGroupVaildKeyword.get(i)) && content
								.contains(robotCall))
						|| (content.contains(currentGroupVaildKeyword.get(i)) && content
								.contains(robotDisplayName))) {
					LogUtil.MSG
							.debug("returnSpecificKeywordFromTextMsg: return "
									+ currentGroupVaildKeyword.get(i));
					return currentGroupVaildKeyword.get(i);
				}
			}
		}
		LogUtil.MSG.debug("returnSpecificKeywordFromTextMsg: return " + null);
		return null;
	}

	// private static String handleCheckListUpdateMediaMessage(BaseMsg msg) {
	// String result = null;
	// int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	// if (hour <= 3 || hour >= 22) {// 22:00 ~ 3:00
	// String currentGroupNickName = WechatTools
	// .getGroupNickNameByGroupUserName(msg.getFromUserName());
	// String nickName = WechatTools.getMemberNickNameByGroupNickName(
	// currentGroupNickName, msg.getStatusNotifyUserName());
	// String fileName = msg.getFileName();
	//
	// for (Map.Entry clEntry : Config.CHECK_LIST_SETTING.entrySet()) {
	// String userName = (String) clEntry.getKey();
	// File filePath = (File) clEntry.getValue();
	// if (fileName.contains(userName)
	// || fileName.contains(filePath.getName())) {
	// DownloadTools.getDownloadFn(msg,
	// MsgTypeEnum.MEDIA.getType(),
	// filePath.getAbsolutePath());
	// result = "@"
	// + nickName
	// + " 今天睡前反思打卡成功！记录已更新，存档已覆盖。苏格拉底曰：未经审视的人生不值得过。今天你反思了吗？";
	// break;
	// }
	// }
	// if (result == null) {
	// result = "你不在睡前反思名单中，无法完成该功能。请联系管理员报名。" + "@" + nickName;
	// }
	// }
	// return result;
	// }

	/**
	 * 处理系统消息
	 * 
	 * @param msg
	 * @return
	 */
	public static String sysMsgHandle(Group group, BaseMsg msg) {
		LogUtil.MSG.debug("sysMsgHandle: " + group + ", " + msg);
		String result = null;
		String currentGroupNickName = WechatTools
				.getGroupNickNameByGroupUserName(msg.getFromUserName());
		if (isCurrentMsgFromVaildGroup(group, currentGroupNickName)) {
			String keyword = returnSpecificKeywordFromSysMsg(group, msg);
			Action action = group.getActionFromVaildKeywords(keyword,
					MsgTypeEnum.SYS);
			if (action != null && keyword != null) {
				if (action.getVaildKeywords(MsgTypeEnum.SYS).contains(keyword)) {
					action.action(group, msg);
				}
			}
		}
		return result;
	}
}
