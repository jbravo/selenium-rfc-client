package org.bitmarte.architecture.utils.testingframework.selenium.service.driver;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.bitmarte.architecture.utils.testingframework.selenium.constants.E_BrowserMode;
import org.bitmarte.architecture.utils.testingframework.selenium.constants.E_BrowserName;
import org.bitmarte.architecture.utils.testingframework.selenium.service.configuration.SeleniumConfigProvider;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;

/**
 * @author bitmarte
 *
 */
public class WebDriverFactory {

	private static final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

	public static WebDriver getInstance(String browserMode, String browserName, BrowserMobProxy proxy)
			throws Exception {
		E_BrowserMode e_BrowserMode = E_BrowserMode.LOCAL;
		E_BrowserName e_BrowserName = E_BrowserName.FIREFOX;
		try {
			e_BrowserMode = E_BrowserMode.valueOf(browserMode);
			e_BrowserName = E_BrowserName.valueOf(browserName);
		} catch (Exception e) {
			throw new WebDriverException("Unsupported testing mode for browserMode '" + browserMode
					+ "' and browserName '" + browserName + "'", e);
		}

		DesiredCapabilities capabilities = new DesiredCapabilities();

		// BrowserMobProxyServer
		if (proxy != null) {
			browserMobConfigure(proxy);
			Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
			capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
		}

		switch (e_BrowserMode) {
		case REMOTE:
			switch (e_BrowserName) {
			case CHROME:
				LOG.info("using remote chrome browser on server '"
						+ SeleniumConfigProvider.getConfig().getSeleniumRcURL() + "'");
				capabilities.setBrowserName("chrome");
				return new RemoteWebDriver(new URL(SeleniumConfigProvider.getConfig().getSeleniumRcURL()),
						capabilities);
			case IEXPLORER:
				LOG.info("using remote iexplorer browser on server '"
						+ SeleniumConfigProvider.getConfig().getSeleniumRcURL() + "'");
				capabilities.setBrowserName("internet explorer");
				return new RemoteWebDriver(new URL(SeleniumConfigProvider.getConfig().getSeleniumRcURL()),
						capabilities);
			case FIREFOX:
				LOG.info("using remote firefox browser on server '"
						+ SeleniumConfigProvider.getConfig().getSeleniumRcURL() + "'");
				capabilities.setBrowserName("firefox");
				return new RemoteWebDriver(new URL(SeleniumConfigProvider.getConfig().getSeleniumRcURL()),
						capabilities);

			default:
				throw new WebDriverException("Unknown case on E_BrowserName enum!");
			}
		case LOCAL:
			// Using local mode as default browserMode
			switch (e_BrowserName) {
			case CHROME:
				LOG.info("using local chrome browser");
				System.setProperty("webdriver.chrome.driver",
						SeleniumConfigProvider.getConfig().getLocalWebDriverPath());
				return new ChromeDriver(capabilities);
			case IEXPLORER:
				throw new WebDriverException("IExplorer browser in remote mode is not supported!");
			default:
				// Using Firefox as default local browser
				LOG.info("using local firefox browser");
				return new FirefoxDriver(capabilities);
			}

		default:
			throw new WebDriverException("Unknown case on E_BrowserMode enum!");
		}
	}

	/**
	 * Configure BrowserMobProxy
	 * 
	 * @param proxy
	 */
	private static void browserMobConfigure(BrowserMobProxy proxy) {

		// Trust all certificates
		proxy.setMitmDisabled(true);

		// ChainedProxy
		if (SeleniumConfigProvider.getConfig().getMobProxy().getChainedProxy() != null) {
			String host = null;
			int port = -1;
			StringTokenizer stringTokenizer = new StringTokenizer(
					SeleniumConfigProvider.getConfig().getMobProxy().getChainedProxy(), ":");
			while (stringTokenizer.hasMoreTokens()) {
				host = stringTokenizer.nextToken();
				port = Integer.parseInt(stringTokenizer.nextToken());
			}
			proxy.setChainedProxy(new InetSocketAddress(host, port));
		}

		// port
		proxy.start(SeleniumConfigProvider.getConfig().getMobProxy().getPort());

		// DownloadBytePerSec
		if (SeleniumConfigProvider.getConfig().getMobProxy().getDownloadBytePerSec() > 0) {
			LOG.info("setting DownloadBytePerSec: "
					+ SeleniumConfigProvider.getConfig().getMobProxy().getDownloadBytePerSec() + " bps");
			proxy.setReadBandwidthLimit(SeleniumConfigProvider.getConfig().getMobProxy().getDownloadBytePerSec());
		}

		// UploadBytePerSec
		if (SeleniumConfigProvider.getConfig().getMobProxy().getUploadBytePerSec() > 0) {
			LOG.info("setting UploadBytePerSec: "
					+ SeleniumConfigProvider.getConfig().getMobProxy().getUploadBytePerSec() + " bps");
			proxy.setWriteBandwidthLimit(SeleniumConfigProvider.getConfig().getMobProxy().getUploadBytePerSec());
		}

		// Latency
		if (SeleniumConfigProvider.getConfig().getMobProxy().getLatencyInMillisec() > 0) {
			LOG.info("setting LatencyInMillisec: "
					+ SeleniumConfigProvider.getConfig().getMobProxy().getLatencyInMillisec() + " msec");
			proxy.setLatency(SeleniumConfigProvider.getConfig().getMobProxy().getLatencyInMillisec(),
					TimeUnit.MILLISECONDS);
		}
	}
}
