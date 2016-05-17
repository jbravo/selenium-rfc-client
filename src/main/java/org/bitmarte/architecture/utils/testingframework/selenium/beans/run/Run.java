package org.bitmarte.architecture.utils.testingframework.selenium.beans.run;

import java.util.List;

import org.bitmarte.architecture.utils.testingframework.selenium.beans.reports.RunReport;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.A_BrowserAction;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;

/**
 * @author bitmarte
 *
 */
@XStreamAlias("run")
public class Run {

	@XStreamOmitField
	private RunReport runReport;

	@XStreamAlias("cookiesRemoveAll")
	@XStreamConverter(value = BooleanConverter.class, booleans = { false }, strings = { "true", "false" })
	@XStreamAsAttribute
	private boolean cookiesRemoveAll;

	@XStreamAlias("cookiesRemove")
	@XStreamAsAttribute
	private String cookiesRemove;

	@XStreamAlias("runName")
	private String runName;

	@XStreamAlias("browserActions")
	private List<BrowserAction> browserActions;

	@XStreamAlias("authentication")
	private Authentication authentication;

	@XStreamAlias("url")
	private String url;

	@XStreamAlias("inputFields")
	private List<InputField> inputFields;

	@XStreamAlias("successCondition")
	private SuccessCondition successCondition;

	@XStreamAsAttribute
	@XStreamAlias("windowWidthPx")
	private int windowWidthPx;

	@XStreamAsAttribute
	@XStreamAlias("windowHeightPx")
	private int windowHeightPx;

	@XStreamAlias("fullscreen")
	@XStreamConverter(value = BooleanConverter.class, booleans = { false }, strings = { "true", "false" })
	@XStreamAsAttribute
	private boolean fullscreen;

	@XStreamAlias("myActions")
	private List<A_BrowserAction> myActions;

	public String getRunName() {
		return runName;
	}

	public void setRunName(String runName) {
		this.runName = runName;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<InputField> getInputFields() {
		return inputFields;
	}

	public void setInputFields(List<InputField> inputFields) {
		this.inputFields = inputFields;
	}

	public SuccessCondition getSuccessCondition() {
		return successCondition;
	}

	public void setSuccessCondition(SuccessCondition successCondition) {
		this.successCondition = successCondition;
	}

	public int getWindowWidthPx() {
		return windowWidthPx;
	}

	public void setWindowWidthPx(int windowWidthPx) {
		this.windowWidthPx = windowWidthPx;
	}

	public int getWindowHeightPx() {
		return windowHeightPx;
	}

	public void setWindowHeightPx(int windowHeightPx) {
		this.windowHeightPx = windowHeightPx;
	}

	public String getCookiesRemove() {
		return cookiesRemove;
	}

	public void setCookiesRemove(String cookiesRemove) {
		this.cookiesRemove = cookiesRemove;
	}

	public boolean isCookiesRemoveAll() {
		return cookiesRemoveAll;
	}

	public void setCookiesRemoveAll(boolean cookiesRemoveAll) {
		this.cookiesRemoveAll = cookiesRemoveAll;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public List<BrowserAction> getBrowserActions() {
		return browserActions;
	}

	public void setBrowserActions(List<BrowserAction> browserActions) {
		this.browserActions = browserActions;
	}

	public List<A_BrowserAction> getMyActions() {
		return myActions;
	}

	public void setMyActions(List<A_BrowserAction> myActions) {
		this.myActions = myActions;
	}

	public RunReport getRunReport() {
		if (runReport == null) {
			this.runReport = new RunReport();
		}
		return runReport;
	}

}