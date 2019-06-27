package org.androidtest.vince.customized;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidtest.vince.Config;
import org.androidtest.vince.data.Constant;
import org.androidtest.vince.data.Group;
import org.androidtest.vince.publicutil.LogUtil;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;

/**
 * 每日反思组件
 * 
 * @author caipeipei
 *
 */
public class DailySelfReflectionCustomized extends Customized {

	private Map<String, File> whiteList = new HashMap<String, File>();
	private int noonRemindTime = 1300;
	private int nightRemindTime = 0112;
	private boolean noonRemind = false;

	public DailySelfReflectionCustomized(Map<String, File> whiteList,
			int noonRemindTime, int nightRemindTime) {
		setWhiteList(whiteList);
		setNoonRemind(true);
		setNightRemindTime(nightRemindTime);
		setNoonRemindTime(noonRemindTime);
	}

	public DailySelfReflectionCustomized(Map<String, File> whiteList,
			int nightRemindTime) {
		setWhiteList(whiteList);
		setNoonRemind(false);
		setNightRemindTime(nightRemindTime);
	}

	public DailySelfReflectionCustomized(Map<String, File> whiteList,
			boolean noonRemind) {
		setWhiteList(whiteList);
		setNoonRemind(noonRemind);
	}

	@Override
	public List<String> getVaildKeywords(String msgType) {
		if (msgType.equals(MsgTypeEnum.MEDIA.getType())) {
			return Config.MEDIA_MSG_DAILY_SELF_REFLECTION_VAILD_KEYWORD_LIST;
		} else {
			return new ArrayList<String>();
		}
	}

	private int getNoonRemindTime() {
		return noonRemindTime;
	}

	private void setNoonRemindTime(int noonRemindTime) {
		LogUtil.MSG.debug("setNoonRemindTime: " + noonRemindTime);
		if (noonRemindTime >= 0000 && noonRemindTime <= 2400) {
			this.noonRemindTime = noonRemindTime;
		} else {
			throw new RuntimeException("setNoonRemindTime: out of range: "
					+ noonRemindTime);
		}

	}

	private int getNightRemindTime() {
		return nightRemindTime;
	}

	private void setNightRemindTime(int nightRemindTime) {
		LogUtil.MSG.debug("setNightRemindTime: " + nightRemindTime);
		if (nightRemindTime >= 0000 && nightRemindTime <= 2400) {
			this.nightRemindTime = nightRemindTime;
		} else {
			throw new RuntimeException("setNightRemindTime: out of range: "
					+ nightRemindTime);
		}

	}

	private boolean isNoonRemind() {
		return noonRemind;
	}

	private void setNoonRemind(boolean noonRemind) {
		LogUtil.MSG.debug("setNoonRemind: " + noonRemind);
		this.noonRemind = noonRemind;

	}

	@SuppressWarnings("unused")
	private Map<String, File> getWhiteList() {
		return whiteList;
	}

	@SuppressWarnings("rawtypes")
	private void setWhiteList(Map<String, File> whiteList) {
		this.whiteList.clear();

		Iterator iter = whiteList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String nickName = (String) entry.getKey();
			File filePath = (File) entry.getValue();

			File newfile = new File(Constant.DATA_SAVE_PATH.getAbsolutePath()
					+ File.separator
					+ createDailySelfReflectionFilename(nickName)
					+ getFileExtension(filePath));
			LOG.info("setWhiteList:" + newfile.getAbsolutePath());
			if (filePath.exists() && filePath.isFile()) {// A路径存在
				LogUtil.MSG.debug("setWhiteList: " + nickName + ", 预设的路径存在文件"
						+ filePath.getAbsolutePath());
				if (filePath.renameTo(newfile)) {
					whiteList.remove(nickName);
					whiteList.put(nickName, newfile);
					LogUtil.MSG.debug("setWhiteList: " + nickName
							+ ", 变更文件成功，新路径: " + newfile.getAbsolutePath());
				} else {
					whiteList.remove(nickName);
					throw new RuntimeException("setWhiteList:"
							+ filePath.getName() + "改名为" + newfile.getName()
							+ "失败。将" + nickName + "移除。请检查原因。");
				}
			} else if (newfile.exists() && newfile.isFile()) {// A路径不在，新路径存在
				whiteList.remove(nickName);
				whiteList.put(nickName, newfile);
				LogUtil.MSG.debug("setWhiteList: " + nickName
						+ ", 预设的文件路径不存在，但在另一路径找到，更新文件成功: "
						+ newfile.getAbsolutePath());
			} else {
				whiteList.remove(nickName);
				throw new RuntimeException("setWhiteList:"
						+ filePath.getAbsolutePath() + "路径不存在或者非文件，将"
						+ nickName + "移除。请检查文件路径。");
			}
		}
		this.whiteList = whiteList;
	}

	private String createDailySelfReflectionFilename(String nickName) {
		String result = Constant.DAILY_SELF_REFLECTION_DEFAULT_FILENAME_TEMPLATES
				+ " @" + nickName;
		LogUtil.MSG.debug("createDailySelfReflectionFilename: " + result);
		return result;
	}

	private String getFileExtension(File filePath) {
		String result = filePath.getName().substring(
				filePath.getName().lastIndexOf("."));
		LogUtil.MSG.debug("getFileExtension: " + result);
		return result;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String handleClockInCustomized(Group group, BaseMsg msg) {
		LogUtil.MSG.debug("handleClockInCustomized: "
				+ this.getClass().getSimpleName() + ", "
				+ group.getGroupNickName());
		String result = null;

		String currentGroupNickName = group.getGroupNickName();
		String currentDisplayName = WechatTools
				.getMemberDisplayOrNickNameByGroupNickName(
						currentGroupNickName, msg.getStatusNotifyUserName());
		String currentNickName = WechatTools.getMemberNickNameByGroupNickName(
				currentGroupNickName, msg.getStatusNotifyUserName());
		String currentFileName = msg.getFileName();

		Iterator iter = whiteList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String nickName = (String) entry.getKey();
			File filePath = (File) entry.getValue();
			LogUtil.MSG.debug("handleClockInCustomized: nickName: " + nickName
					+ ",currentNickName: " + currentNickName
					+ ",currentDisplayName: " + currentDisplayName);
			LogUtil.MSG.debug("handleClockInCustomized: currentFileName: "
					+ currentFileName
					+ ",createDailySelfReflectionFilename(nickName): "
					+ createDailySelfReflectionFilename(nickName));
			if ((nickName.contains(currentNickName) || nickName
					.contains(currentDisplayName))
					&& currentFileName
							.contains(createDailySelfReflectionFilename(nickName))) {
				DownloadTools.getDownloadFn(msg, MsgTypeEnum.MEDIA.getType(),
						filePath.getAbsolutePath());
				result = "@"
						+ currentNickName
						+ " 今天每日反思打卡成功！记录已更新，存档已覆盖。生活如果不经过归纳总结和深入思考，就永远无法转化为阅历和智慧。今天你反思了吗？";
				break;
			}
		}
		if (result == null) {
			result = "@" + currentNickName + " 你不在每日反思名单中，无法完成该功能。请联系管理员" + "@"
					+ group.getAdmin() + "报名。";
		}
		return result;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean reportProcessRegularly(Group currentGroup) {
		Date currentDate = new Date();
		int currentTime = Integer.parseInt(new SimpleDateFormat("HHmm")
				.format(currentDate));
		boolean result = false;
		if (isNoonRemind()) {
			LogUtil.MSG.debug("reportProcessRegularly: currentTime: "
					+ currentTime + ", getNoonRemindTime(): "
					+ getNoonRemindTime());
			if (currentTime == getNoonRemindTime()) {
				Iterator iter = whiteList.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String nickName = (String) entry.getKey();
					File filePath = (File) entry.getValue();
					MessageTools
							.sendGroupMsgByNickName(
									"【* 每日反思打卡】\n@"
											+ nickName
											+ " 您已报名每日反思打卡（午间反思+睡前反思），请填写下面的反思自检清单表格，并将文件回传留档。",
									currentGroup.getGroupNickName());
					if (filePath.exists()) {
						MessageTools.sendGroupFileMsgByNickName(
								currentGroup.getGroupNickName(),
								filePath.getAbsolutePath());
					} else {
						MessageTools.sendGroupMsgByNickName("文件不存在: "
								+ filePath.getAbsolutePath(),
								currentGroup.getGroupNickName());
					}
				}
				result = true;// 传false表示允许轮询，不阻塞消息线程
				LOG.info("reportProcessRegularly: "
						+ currentGroup.getGroupNickName() + ": report "
						+ result);
			}
		}
		if (currentTime == getNightRemindTime()) {
			LogUtil.MSG.debug("reportProcessRegularly: currentTime: "
					+ currentTime + ", getNightRemindTime(): "
					+ getNightRemindTime());
			Iterator iter = whiteList.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String nickName = (String) entry.getKey();
				File filePath = (File) entry.getValue();
				MessageTools.sendGroupMsgByNickName("【* 每日反思打卡】\n@" + nickName
						+ " 您已报名每日反思打卡，一天即将过去，请完成下面的反思自检清单表格，并在睡前将文件回传留档。",
						currentGroup.getGroupNickName());
				if (filePath.exists()) {
					MessageTools.sendGroupFileMsgByNickName(
							currentGroup.getGroupNickName(),
							filePath.getAbsolutePath());
				} else {
					MessageTools.sendGroupMsgByNickName(
							"文件不存在: " + filePath.getAbsolutePath(),
							currentGroup.getGroupNickName());
				}

			}
			result = true;// 传false表示允许轮询，不阻塞消息线程
			LOG.info("reportProcessRegularly: "
					+ currentGroup.getGroupNickName() + ": report " + result);

		}
		return result;
	}
}