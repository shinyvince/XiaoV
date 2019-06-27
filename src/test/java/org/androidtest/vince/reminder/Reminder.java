package org.androidtest.vince.reminder;

import org.androidtest.vince.data.Group;
import org.apache.log4j.Logger;

/**
 * 提醒组件（抽象类）
 * 
 * @author caipeipei
 *
 *         TODO 代码未使用
 */
public abstract class Reminder {
	protected Logger LOG = Logger.getLogger(Reminder.class);

	public abstract boolean reminderRegularly(Group currentGroup);

}