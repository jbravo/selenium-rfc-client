package org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author bitmarte
 *
 */
public abstract class A_BrowserAction {

	@XStreamAlias("waitBeforeActionInMillis")
	@XStreamAsAttribute
	private long waitBeforeActionInMillis;

	public long getWaitBeforeActionInMillis() {
		return waitBeforeActionInMillis;
	}

	public void setWaitBeforeActionInMillis(long waitBeforeActionInMillis) {
		this.waitBeforeActionInMillis = waitBeforeActionInMillis;
	}

}
