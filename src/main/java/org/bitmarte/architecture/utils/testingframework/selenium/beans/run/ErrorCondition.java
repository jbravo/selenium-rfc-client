package org.bitmarte.architecture.utils.testingframework.selenium.beans.run;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author bitmarte
 *
 */
@XStreamAlias("errorCondition")
public class ErrorCondition {

	@XStreamAlias("element")
	private String element;

	@XStreamAlias("elementContent")
	private String elementContent;

	@XStreamAlias("contentEvaluator")
	@XStreamAsAttribute
	private String contentEvaluator;

	@XStreamAlias("elementExtractor")
	@XStreamAsAttribute
	private String elementExtractor;

	@XStreamAlias("maxTimeOutPerErrorConditionInSec")
	private int maxTimeOutPerErrorConditionInSec;

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getElementContent() {
		return elementContent;
	}

	public void setElementContent(String elementContent) {
		this.elementContent = elementContent;
	}

	public String getContentEvaluator() {
		return contentEvaluator;
	}

	public void setContentEvaluator(String contentEvaluator) {
		this.contentEvaluator = contentEvaluator;
	}

	public String getElementExtractor() {
		return elementExtractor;
	}

	public void setElementExtractor(String elementExtractor) {
		this.elementExtractor = elementExtractor;
	}

	public int getMaxTimeOutPerErrorConditionInSec() {
		return maxTimeOutPerErrorConditionInSec;
	}

	public void setMaxTimeOutPerErrorConditionInSec(int maxTimeOutPerErrorConditionInSec) {
		this.maxTimeOutPerErrorConditionInSec = maxTimeOutPerErrorConditionInSec;
	}

}
