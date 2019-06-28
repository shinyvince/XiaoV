package org.androidtest.xiaoV.action;

import java.util.List;

import org.androidtest.xiaoV.data.Group;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;

/**
 * 对行为做出反应的组件基类（抽象类）
 * 
 * @author caipeipei
 *
 */
public abstract class Action {

	/**
	 * 对行为执行反应
	 * 
	 * @param group
	 * @param msg
	 * @return
	 */
	public abstract String action(Group group, BaseMsg msg);

	/**
	 * 播报
	 * 
	 * @param group
	 * @return
	 */
	public abstract String report(Group group);

	/**
	 * 广播
	 * 
	 * @param group
	 * @return
	 */
	public abstract boolean notify(Group group);

	/**
	 * 返回该类的合法关键字列表
	 * 
	 * @param type
	 */
	public abstract List<String> getVaildKeywords(MsgTypeEnum type);
}