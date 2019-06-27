package org.androidtest.vince.data;

import java.util.ArrayList;
import java.util.List;

import org.androidtest.vince.clockIn.ClockIn;
import org.androidtest.vince.customized.Customized;
import org.androidtest.vince.publicutil.LogUtil;
import org.androidtest.vince.publicutil.StringUtil;
import org.androidtest.vince.reminder.Reminder;

import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;

public class Group {

	private String groupId;
	private String groupNickName;
	private String admin;
	private List<ClockIn> clockInList = new ArrayList<>();
	private List<Customized> customizedList = new ArrayList<>();
	private List<Reminder> reminderList = new ArrayList<>();

	@Override
	public String toString() {
		return "Group [groupId=" + groupId + ", groupNickName=" + groupNickName
				+ ", admin=" + admin + ", clockInList=" + clockInList
				+ ", customizedList=" + customizedList + ", reminderList="
				+ reminderList + "]";
	}

	public Group(String nickName, String admin) {
		LogUtil.MSG.debug("Group: init: nickName: " + nickName + ",admin: "
				+ admin);
		groupNickName = nickName;
		this.admin = admin;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupNickName() {
		return groupNickName;
	}

	public void setGroupNickName(String groupNickName) {
		this.groupNickName = groupNickName;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public List<ClockIn> getClockInList() {
		return clockInList;
	}

	public List<Customized> getCustomizedList() {
		return customizedList;
	}

	public List<Reminder> getReminderList() {
		return reminderList;
	}

	public void setClockInList(List<ClockIn> actionsList) {
		this.clockInList.clear();
		this.clockInList = actionsList;
	}

	public void setReminderList(List<Reminder> reminderList) {
		this.reminderList.clear();
		this.reminderList = reminderList;
	}

	public void setCustomizedist(List<Customized> customizedList) {
		this.customizedList.clear();
		this.customizedList = customizedList;
	}

	public void addClockIn(ClockIn clockIn) {
		// TODO 需要确保action唯一性，免得重复添加一样的action
		if (clockIn != null) {
			this.clockInList.add(clockIn);

		}
	}

	public void addCustomized(Customized customized) {
		// TODO 需要确保action唯一性，免得重复添加一样的action
		if (customized != null) {
			this.customizedList.add(customized);
		}
	}

	public void addReminder(Reminder reminder) {
		// TODO 需要确保action唯一性，免得重复添加一样的action
		if (reminder != null) {
			this.reminderList.add(reminder);
		}
	}

	public List<String> getMemberList() {
		List<String> list = null;
		if (groupId != null) {
			list = WechatTools.getMemberListByGroupId2(groupId);
		} else {
			list = WechatTools.getMemberListByGroupNickName2(groupNickName);
		}
		return list;
	}

	public List<String> getAllVaildTextMsgKeyword() {
		List<String> list = new ArrayList<String>();
		for (ClockIn clockIn : clockInList) {
			List<String> cList = clockIn.getVaildKeywords();
			if (StringUtil.ifNotNullOrEmpty(cList)) {
				for (String word : clockIn.getVaildKeywords()) {
					list.add(word);
				}
			}
		}
		for (Customized customized : customizedList) {
			List<String> cList = customized.getVaildKeywords(MsgTypeEnum.TEXT
					.getType());
			if (StringUtil.ifNotNullOrEmpty(cList)) {
				for (String word : cList) {
					list.add(word);
				}
			}
		}
		return list;
	}

	public List<String> getAllVaildSysMsgKeyword() {
		List<String> list = new ArrayList<String>();
		for (Customized customized : customizedList) {
			List<String> cList = customized.getVaildKeywords(MsgTypeEnum.SYS
					.getType());
			if (StringUtil.ifNotNullOrEmpty(cList)) {
				for (String word : cList) {
					list.add(word);
				}
			}
		}
		return list;
	}

	public List<String> getAllVaildMediaMsgKeyword() {
		List<String> list = new ArrayList<String>();
		for (Customized customized : customizedList) {
			List<String> cList = customized.getVaildKeywords(MsgTypeEnum.MEDIA
					.getType());
			if (StringUtil.ifNotNullOrEmpty(cList)) {
				for (String word : cList) {
					list.add(word);
				}
			}
		}
		return list;
	}

	public ClockIn getClockInFromVaildKeywords(String keyword) {
		LogUtil.MSG.debug("getClockInFromVaildKeywords: groupNickName: "
				+ groupNickName + ", keyword: " + keyword);
		if (StringUtil.ifNotNullOrEmpty(keyword)) {
			for (ClockIn clockIn : clockInList) {
				List<String> cList = clockIn.getVaildKeywords();
				if (StringUtil.ifNotNullOrEmpty(cList)) {
					for (String word : cList) {
						if (keyword.equals(word)) {
							LogUtil.MSG
									.debug("getClockInFromVaildKeywords: return "
											+ clockIn.getClass()
													.getSimpleName());
							return clockIn;
						}
					}
				}
			}
		}
		LogUtil.MSG.debug("getClockInFromVaildKeywords: return " + null);
		return null;
	}

	public Customized getCustomizedFromVaildKeywords(String keyword) {
		LogUtil.MSG.debug("getCustomizedFromVaildKeywords: groupNickName: "
				+ groupNickName + ", keyword: " + keyword);
		if (StringUtil.ifNotNullOrEmpty(keyword)) {
			for (Customized customized : customizedList) {
				for (int i = 0; i < MsgTypeEnum.values().length; i++) {
					List<String> cList = customized
							.getVaildKeywords(MsgTypeEnum.values()[i].getType());
					if (StringUtil.ifNotNullOrEmpty(cList)) {
						for (String word : cList) {
							if (keyword.equals(word)) {
								LogUtil.MSG
										.debug("getCustomizedFromVaildKeywords: return "
												+ customized.getClass()
														.getSimpleName());
								return customized;
							}
						}
					}
				}
			}
		}
		LogUtil.MSG.debug("getCustomizedFromVaildKeywords: return " + null);
		return null;
	}
}
