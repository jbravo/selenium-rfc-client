package org.bitmarte.architecture.utils.testingframework.selenium.service.evaluator.impl;

import org.apache.commons.lang3.StringUtils;
import org.bitmarte.architecture.utils.testingframework.selenium.service.evaluator.A_ContentEvaluator;

/**
 * @author bitmarte
 *
 */
public class EndWithContentEvaluator extends A_ContentEvaluator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bitmarte.architecture.utils.testingframework.selenium.dom.evaluator.I_ContentEvaluator#evaluate(java
	 * .lang.String, java.lang.String)
	 */
	public boolean evaluate(String str1, String str2) {
		LOG.debug("using EndWithContentEvaluator...");
		boolean result = false;
		if (StringUtils.endsWithIgnoreCase(str1, str2)) {
			result = true;
		}
		return result;
	}

}
