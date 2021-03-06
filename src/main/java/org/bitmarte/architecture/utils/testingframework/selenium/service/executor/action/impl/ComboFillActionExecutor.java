package org.bitmarte.architecture.utils.testingframework.selenium.service.executor.action.impl;

import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.A_BrowserAction;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.ComboFillAction;
import org.bitmarte.architecture.utils.testingframework.selenium.service.executor.action.A_BrowserActionExecutor;
import org.bitmarte.architecture.utils.testingframework.selenium.service.extractor.ElementExtractorFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * @author bitmarte
 *
 */
public class ComboFillActionExecutor extends A_BrowserActionExecutor {

	public ComboFillActionExecutor(WebDriver driver, A_BrowserAction browserAction) {
		super(driver, browserAction);
		// TODO Auto-generated constructor stub
	}

	public void execute() throws Exception {
		try {
			super.waitBefore();
			Select select = new Select(
					ElementExtractorFactory.getInstance(((ComboFillAction) this.action).getElementExtractor())
							.getElements(this.driver, ((ComboFillAction) this.action).getElement()).get(0));
			select.selectByValue(((ComboFillAction) this.action).getValue());
		} catch (Exception e) {
			throw e;
		}
	}

}
