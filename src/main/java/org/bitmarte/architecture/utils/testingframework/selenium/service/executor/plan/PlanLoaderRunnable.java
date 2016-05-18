package org.bitmarte.architecture.utils.testingframework.selenium.service.executor.plan;

import java.io.File;
import java.util.List;

import org.bitmarte.architecture.utils.testingframework.selenium.beans.plan.Plan;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.ErrorCondition;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.Run;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.run.action.A_BrowserAction;
import org.bitmarte.architecture.utils.testingframework.selenium.constants.E_TestResult;
import org.bitmarte.architecture.utils.testingframework.selenium.driver.DriverUtils;
import org.bitmarte.architecture.utils.testingframework.selenium.reports.ReportGenerator;
import org.bitmarte.architecture.utils.testingframework.selenium.reports.WebTimingUtils;
import org.bitmarte.architecture.utils.testingframework.selenium.service.authentication.E_AuthType;
import org.bitmarte.architecture.utils.testingframework.selenium.service.authentication.impl.NTLMAuthentication;
import org.bitmarte.architecture.utils.testingframework.selenium.service.evaluator.ContentEvaluatorFactory;
import org.bitmarte.architecture.utils.testingframework.selenium.service.executor.action.BrowserActionExecutorFactory;
import org.bitmarte.architecture.utils.testingframework.selenium.service.extractor.ElementExtractorFactory;
import org.bitmarte.architecture.utils.testingframework.selenium.setup.DefaultSeleniumConfig;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.proxy.CaptureType;

/**
 * @author bitmarte
 *
 */
public class PlanLoaderRunnable implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(PlanLoaderRunnable.class);

	private WebDriver driver = null;
	private Plan plan = null;
	private BrowserMobProxy proxy = null;
	private WorkingPlans workingPlans = null;

	public PlanLoaderRunnable(WebDriver driver, Plan plan, BrowserMobProxy proxy, WorkingPlans workingPlans) {
		this.driver = driver;
		this.plan = plan;
		this.proxy = proxy;
		this.workingPlans = workingPlans;
	}

	public void run() {
		LOG.info(Thread.currentThread().getName() + " running for plan " + plan.getPlanName());

		Run currentRun = null;
		DriverUtils driverUtils = null;
		WebTimingUtils timingUtils = null;

		plan.getPlanReport().setTestResult(E_TestResult.ERROR.name());

		try {
			driverUtils = new DriverUtils(driver, plan.getPlanName());
			timingUtils = new WebTimingUtils(driver);

			LOG.info(plan.getRuns().size() + " runs in plan '" + plan.getPlanName() + "'");

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

				// manage browser actions
				if (currentRun.getBrowserActions() != null) {
					for (A_BrowserAction browserAction : currentRun.getBrowserActions()) {
						BrowserActionExecutorFactory.getInstance(driver, browserAction).execute();
					}
				}

				// checking successCondition
				LOG.info("Test result checking...");
				WebDriverWait wait = null;
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
					driverUtils.takeScreenshot(currentRun, null);

					// Web Timings API
					if (DefaultSeleniumConfig.getConfig().getWebTimings() != null) {
						timingUtils.calculateTimings(currentRun);
					}

					// HAR capture
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
				LOG.info("Plan '" + plan.getPlanName() + "' completed!");
				break;
			default:
				LOG.error("Plan '" + plan.getPlanName() + "' terminated with some error!");
				break;
			}

		} catch (Exception e) {
			LOG.error("Error run()!", e);

			try {
				driverUtils.takeScreenshot(currentRun, E_TestResult.ERROR);
			} catch (Exception e1) {
				LOG.error("Error run()!", e1);
			}
		} finally {
			// generating reports...
			try {
				ReportGenerator.generatePlanReport(plan);
				this.workingPlans.regWorkedPlan(plan);

				if (DefaultSeleniumConfig.getConfig().isCloseBrowserOnFinish()) {
					try {
						this.driver.quit();
					} catch (Throwable t) {
						LOG.error("Driver does not close correctly!", t);
					}
				}

			} catch (Exception e) {
				LOG.error("Error run()!", e);
			}
		}
	}

}
