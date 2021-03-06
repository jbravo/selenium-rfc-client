package org.bitmarte.architecture.utils.testingframework.selenium.service.executor.action.impl;

import java.util.List;

import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.A_BrowserAction;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.RadioFillAction;
import org.bitmarte.architecture.utils.testingframework.selenium.service.executor.action.A_BrowserActionExecutor;
import org.bitmarte.architecture.utils.testingframework.selenium.service.extractor.ElementExtractorFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author bitmarte
 *
 */
public class RadioFillActionExecutor extends A_BrowserActionExecutor {

	public RadioFillActionExecutor(WebDriver driver, A_BrowserAction browserAction) {
		super(driver, browserAction);
		// TODO Auto-generated constructor stub
	}

	public void execute() throws Exception {
		try {
			super.waitBefore();
			List<WebElement> radios = ElementExtractorFactory
					.getInstance(((RadioFillAction) this.action).getElementExtractor())
					.getElements(this.driver, ((RadioFillAction) this.action).getElement());
			for (WebElement radio : radios) {
				if (radio.getText().equals(((RadioFillAction) this.action).getValue())) {
					radio.click();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

}
