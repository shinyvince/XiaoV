package org.androidtest.xiaoV.customized;

import java.util.List;

import org.androidtest.xiaoV.data.Group;
import org.apache.log4j.Logger;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;

/**
 * 高级自定义组件（抽象类）
 * 
 * @author caipeipei
 *
 */
public abstract class Customized {
	protected Logger LOG = Logger.getLogger(Customized.class);

	public abstract List<String> getVaildKeywords(String msgType);

	public abstract String handleClockInCustomized(Group group, BaseMsg msg);

	public abstract boolean reportProcessRegularly(Group group);

}