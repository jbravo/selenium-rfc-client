package org.bitmarte.architecture.utils.testingframework.selenium;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.ErrorCondition;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.InputField;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.Plan;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.Run;
import org.bitmarte.architecture.utils.testingframework.selenium.constants.E_BrowserAction;
import org.bitmarte.architecture.utils.testingframework.selenium.constants.E_InputFieldType;
import org.bitmarte.architecture.utils.testingframework.selenium.constants.E_TestResult;
import org.bitmarte.architecture.utils.testingframework.selenium.driver.DriverUtils;
import org.bitmarte.architecture.utils.testingframework.selenium.reports.ReportProducer;
import org.bitmarte.architecture.utils.testingframework.selenium.reports.WebTimingUtils;
import org.bitmarte.architecture.utils.testingframework.selenium.service.authentication.E_AuthType;
import org.bitmarte.architecture.utils.testingframework.selenium.service.authentication.impl.NTLMAuthentication;
import org.bitmarte.architecture.utils.testingframework.selenium.service.evaluator.ContentEvaluatorFactory;
import org.bitmarte.architecture.utils.testingframework.selenium.service.extractor.ElementExtractorFactory;
import org.bitmarte.architecture.utils.testingframework.selenium.service.unmarshaller.UnmarshallerFactory;
import org.bitmarte.architecture.utils.testingframework.selenium.setup.DefaultSeleniumConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.proxy.CaptureType;

/**
 * @author bitmarte
 *
 */
public class PlanLoader {

	private static final Logger LOG = LoggerFactory.getLogger(PlanLoader.class);

	@SuppressWarnings("finally")
	public static Plan load(WebDriver driver, File xmlPlan, BrowserMobProxy proxy) throws Exception {
		Plan plan = null;
		Run currentRun = null;
		DriverUtils driverUtils = null;
		WebTimingUtils timingUtils = null;

		try {
			plan = (Plan) UnmarshallerFactory.getInstance(Plan.class).unmarshall(xmlPlan);

			plan.getPlanReport().setTestResult(E_TestResult.ERROR.name());
		} catch (Exception e) {
			throw e;
		}

		try {
			driverUtils = new DriverUtils(driver, plan.getPlanName());
			timingUtils = new WebTimingUtils(driver);

			// cookies managing
			if (plan.isCookiesRemoveAll()) {
				driverUtils.removeAllCookies();
			}
			if (!StringUtils.isEmpty(plan.getCookiesRemove())) {
				driverUtils.removeCookies(plan.getCookiesRemove());
			}

			// window size managing
			if (plan.isFullscreen()) {
				driverUtils.fullScreen();
			}

			LOG.info(plan.getRuns().size() + " runs in plan '" + xmlPlan.getName() + "'");

			for (Run run : plan.getRuns()) {
				currentRun = run;
				currentRun.getRunReport().setTestResult(E_TestResult.ERROR.name());
				LOG.info("Run name: " + currentRun.getRunName());

				// enable HAR capture
				if (DefaultSeleniumConfig.getConfig().getMobProxy() != null
						&& DefaultSeleniumConfig.getConfig().getMobProxy().isEnableHarCapture()) {
					LOG.info("HAR file capture enabled");
					proxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
					proxy.newHar(currentRun.getRunName());
					proxy.newPage(currentRun.getRunName());
				}

				// browser action managing
				if (currentRun.getBrowserAction() != null) {
					driverUtils.makeBrowserAction(currentRun.getBrowserAction());
				}

				// cookies managing
				if (currentRun.isCookiesRemoveAll()) {
					driverUtils.removeAllCookies();
				}
				if (!StringUtils.isEmpty(plan.getCookiesRemove())) {
					driverUtils.removeCookies(plan.getCookiesRemove());
				}

				// window size managing
				if (currentRun.isFullscreen()) {
					driverUtils.fullScreen();
				} else {
					if (currentRun.getWindowWidthPx() > 0 && currentRun.getWindowHeightPx() > 0) {
						driverUtils.resizeWindow(currentRun.getWindowWidthPx(), currentRun.getWindowHeightPx());
					}
				}

				WebDriverWait wait = null;

				if (currentRun.getUrl() != null) {
					LOG.info("Go to URL '" + currentRun.getUrl() + "'");
					driver.get(currentRun.getUrl());
				}

				// manage authentication
				if (currentRun.getAuthentication() != null) {
					switch (E_AuthType.valueOf(currentRun.getAuthentication().getAuthType())) {
					default:
						LOG.debug("using authType '" + E_AuthType.NTLM.name() + "'...");
						(new Thread(new NTLMAuthentication(currentRun))).start();
						Thread.currentThread();
						Thread.sleep(currentRun.getAuthentication().getWaitPromptInSec() + 8000);
						break;
					}
				}

				if (currentRun.getInputFields() != null) {
					LOG.info("Form filling...");
					for (InputField field : currentRun.getInputFields()) {
						switch (E_InputFieldType.valueOf(field.getType())) {
						case SELECT:
							LOG.info("Input type SELECT");
							Select select = new Select(ElementExtractorFactory.getInstance(field.getElementExtractor())
									.getElements(driver, field.getElement()).get(0));
							select.selectByValue(field.getValue());
							break;
						case RADIO:
							LOG.info("Input type RADIO");
							List<WebElement> radios = ElementExtractorFactory.getInstance(field.getElementExtractor())
									.getElements(driver, field.getElement());
							for (WebElement radio : radios) {
								if (radio.getText().equals(field.getValue())) {
									radio.click();
								}
							}
							// default: type="TEXT"
						default:
							LOG.info("Input type TEXT");
							ElementExtractorFactory.getInstance(field.getElementExtractor())
									.getElements(driver, field.getElement()).get(0).sendKeys(field.getValue());
							break;
						}
					}
				}

				if (currentRun.getClickByXPATH() != null) {
					LOG.info("Make a click on '" + currentRun.getClickByXPATH() + "'");
					driver.findElement(By.xpath(currentRun.getClickByXPATH())).click();
				}

				LOG.info("Test result checking...");
				final Run finalRun = currentRun;
				try {
					wait = new WebDriverWait(driver,
							DefaultSeleniumConfig.getConfig().getMaxTimeOutPerSuccessConditionInSec());
					LOG.debug("Serching success condition unit "
							+ DefaultSeleniumConfig.getConfig().getMaxTimeOutPerSuccessConditionInSec() + " sec...");

					wait.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver d) {
							List<WebElement> elements = ElementExtractorFactory
									.getInstance(finalRun.getSuccessCondition().getElementExtractor())
									.getElements(d, finalRun.getSuccessCondition().getElement());
							if (!elements.isEmpty()) {
								if (finalRun.getSuccessCondition().getElementContent() != null) {
									for (WebElement webElement : elements) {
										return ContentEvaluatorFactory
												.getInstance(finalRun.getSuccessCondition().getContentEvaluator())
												.evaluate(finalRun.getSuccessCondition().getElementContent(),
														webElement.getText());
									}
								} else {
									return true;
								}
							}
							return false;
						}
					});

					plan.getPlanReport().setTestResult(E_TestResult.SUCCESS.name());
					currentRun.getRunReport().setTestResult(E_TestResult.SUCCESS.name());
					LOG.info("Success on run '" + currentRun.getRunName() + "'");
				} catch (TimeoutException te1) {
					wait = new WebDriverWait(driver,
							DefaultSeleniumConfig.getConfig().getMaxTimeOutPerErrorConditionInSec());
					LOG.debug("Searching error condition unit "
							+ DefaultSeleniumConfig.getConfig().getMaxTimeOutPerErrorConditionInSec() + " sec...");
					if (DefaultSeleniumConfig.getConfig().getErrorConditions() != null) {
						for (final ErrorCondition errorCondition : DefaultSeleniumConfig.getConfig()
								.getErrorConditions()) {
							try {
								wait.until(new ExpectedCondition<Boolean>() {
									public Boolean apply(WebDriver d) {
										List<WebElement> elements = ElementExtractorFactory
												.getInstance(errorCondition.getElementExtractor())
												.getElements(d, errorCondition.getElement());
										if (!elements.isEmpty()) {
											if (errorCondition.getElementContent() != null) {
												for (WebElement webElement : elements) {
													return ContentEvaluatorFactory
															.getInstance(errorCondition.getContentEvaluator())
															.evaluate(errorCondition.getElementContent(),
																	webElement.getText());
												}
											} else {
												return true;
											}
										}
										return false;
									}
								});
								LOG.error("Error on run '" + currentRun.getRunName() + "'");

								break;
							} catch (Exception e) {
								currentRun.getRunReport().setTestResult(E_TestResult.TIMEOUT.name());
								plan.getPlanReport().setTestResult(E_TestResult.ERROR.name());
								LOG.error("Timeout on run '" + currentRun.getRunName() + "'");
							}
						}
					} else {
						currentRun.getRunReport().setTestResult(E_TestResult.TIMEOUT.name());
						plan.getPlanReport().setTestResult(E_TestResult.ERROR.name());
						LOG.error("Timeout on run '" + currentRun.getRunName() + "'");
					}
					break;
				} finally {
					if (currentRun.getBrowserAction() != null
							&& E_BrowserAction.IFRAME_SWITCH.name().equals(currentRun.getBrowserAction().getAction())
							&& currentRun.getBrowserAction().getElementByXPath() == null) {
						LOG.debug("iframe used, switching to default content...");
						driver.switchTo().defaultContent();
					}
					driverUtils.takeScreenshot(currentRun, null);

					// FIXME insert here pdf generator
					driverUtils.generatePdfFromHtml(currentRun);

					// Web Timings API
					if (DefaultSeleniumConfig.getConfig().getWebTimings() != null) {
						timingUtils.calculateTimings(currentRun);
					}

					// enable HAR capture
					if (DefaultSeleniumConfig.getConfig().getMobProxy() != null
							&& DefaultSeleniumConfig.getConfig().getMobProxy().isEnableHarCapture()) {
						String harFilePath = DefaultSeleniumConfig.getConfig().getReportBaseDir() + plan.getPlanName()
								+ "/HarFiles/" + currentRun.getRunName() + ".har";
						LOG.info("writing HAR file '" + harFilePath + "' ...");
						File harFile = new File(harFilePath);
						harFile.getParentFile().mkdirs();
						proxy.getHar().writeTo(harFile);
						currentRun.getRunReport().setHarFilePath(harFilePath);
					}
				}
			}

			switch (E_TestResult.valueOf(plan.getPlanReport().getTestResult())) {
			case SUCCESS:
				LOG.info("Plan '" + xmlPlan.getName() + "' completed!");
				break;
			default:
				LOG.error("Plan '" + xmlPlan.getName() + "' terminated with some error!");
				break;
			}

		} catch (Exception e) {
			LOG.error("Error load()!", e);

			driverUtils.takeScreenshot(currentRun, E_TestResult.ERROR);
			throw e;
		} finally {
			// generating reports...
			ReportProducer.generatePlanReport(plan);
			return plan;
		}
	}

}
