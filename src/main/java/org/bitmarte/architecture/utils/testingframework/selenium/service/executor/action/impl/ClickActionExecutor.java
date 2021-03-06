package org.bitmarte.architecture.utils.testingframework.selenium.service.executor.action.impl;

import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.A_BrowserAction;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.ClickAction;
import org.bitmarte.architecture.utils.testingframework.selenium.service.executor.action.A_BrowserActionExecutor;
import org.bitmarte.architecture.utils.testingframework.selenium.service.extractor.ElementExtractorFactory;
import org.openqa.selenium.WebDriver;

/**
 * @author bitmarte
 *
 */
public class ClickActionExecutor extends A_BrowserActionExecutor {

	public ClickActionExecutor(WebDriver driver, A_BrowserAction browserAction) {
		super(driver, browserAction);
		// TODO Auto-generated constructor stub
	}

	public void execute() throws Exception {
		try {
			super.waitBefore();
			ElementExtractorFactory.getInstance(((ClickAction) this.action).getElementExtractor())
					.getElements(this.driver, ((ClickAction) this.action).getElement()).get(0).click();
		} catch (Exception e) {
			throw e;
		}
	}

}
