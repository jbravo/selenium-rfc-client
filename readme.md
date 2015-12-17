# Selenium Run-From-Configuration client
This project allow you to create and run ent-to-end tests, running on real browser.<br/>
The artifact is a simple JAR file that you need to run directly from a shell; it requires only one argument, the path where you put configuration files (setup and plans).

## Two different approaches (local vs. remote)
It depends on your goal you can run a test on two different platforms: "local" or "remote".<br/>
**Don't worry, you can run the same test on both platforms.**<br/>

### Local approach
In this case the client execute tests on the same machine that you use to run it.<br/>
**You need to be sure that the browser used for testing is installed on you machine.**

### Remote approach
In this case the client execute tests on remote machine.<br/>
**You need to be sure that Selenium Standalone Server (http://docs.seleniumhq.org/download/) and the browser used for testing are installed on remote machine**.

## Client Setup
<ol>
	<li>Clone this repo</li>
	<li>Choose a path where you want to put your configuration, and create it on your local machine (eg. /var/selenium/my-cfg)</li>
	<li>Copy to your preferred path all "src/main/examples/" content (config.xml and plans folder with its content)</li>
	<li>
		Open "config.xml" (eg. /var/selenium/my-cfg/config.xml) and apply your configuration, as the example below:
		<pre>
			<code>
&lt;config&gt;
	&lt;browserName&gt;FIREFOX_REMOTE&lt;/browserName&gt;
	&lt;seleniumRcURL&gt;http://10.217.xx.xx:4444/wd/hub&lt;/seleniumRcURL&gt;
	&lt;maxTimeOutPerPageInSec&gt;30&lt;/maxTimeOutPerPageInSec&gt;
	&lt;screenshotBaseDir&gt;/var/tmp/selenium/check-evidences/&lt;/screenshotBaseDir&gt;
&lt;/config&gt;
			</code>
		</pre>
		<ul>
			<li>&lt;browserName&gt; is a **require** node where you put the browser that you want to run tests on. **Take as look below for supported browsers**</li>
			<li>&lt;localWebDriverPath&gt; is a **optional** node where you put the webdriver path, only if you are not using Firefox</li>
			<li>&lt;seleniumRcURL&gt; is an **optional** node where you put the SeleniumRC URL connection. **Remove this node for LOCAL approach**</li>
			<li>&lt;maxTimeOutPerPageInSec&gt; is a **require** node where you put your waiting timeout in second. It used for checking your success condition</li>
			<li>&lt;screenshotBaseDir&gt; is a **require** node where you put your preferred location where Selenium save screenshots on at success condition</li>
		</ul>
	</li>
</ol>

You can see the allowed values for &lt;browserName&gt; attribute:

| Attribute value        		| Description										|
| ----------------------------- | ------------------------------------------------- |
| FIREFOX_LOCAL					| Running local firefox browser instance			|
| FIREFOX_REMOTE				| Running remote firefox browser instance (*)		|
| CHROME_LOCAL					| Running local chrome browser instance (**)		|
| CHROME_REMOTE					| Running remote chrome browser instance (***)		|
| IEXPLORER_REMOTE				| Running remote iexplorer browser instance (****)	|

(*) You need to install Selenium Standalone Server (http://docs.seleniumhq.org/download/) and passing the '-Dwebdriver.firefox.bin' jar argument point to firefox bin browser<br/>
(**) You need to download ChromeWebDriver (http://chromedriver.storage.googleapis.com/index.html?path=2.20/) and setup &lt;localWebDriverPath&gt; node into your config.xml<br/>
(***) You need to download ChromeWebDriver (http://chromedriver.storage.googleapis.com/index.html?path=2.20/) and passing the '-Dwebdriver.chrome.bin' and
'-Dwebdriver.chrome.driver' jar arguments point to chrome bin browser and chrome driver on your remote machine<br/>
(****) You need to download Internet Explorer Driver Server (http://www.seleniumhq.org/download/) and passing the '-Dwebdriver.ie.driver' jar argument point to iexplorer driver on your remote machine.
Pay attention that all IExplorer security zone must be the same and the zoom must be setup to 100%

## Make your plan
<ol>
	<li>Open "sample-plan.xml" or create new one (eg. /var/selenium/my-cfg/plans/sample-plan.xml)</li>
	<li>
		Let's create your first run! A run is an action (eg. click or form filling) with a success condition.
		You can find a run example below:
		<pre>
			<code>
&lt;plan&gt;
	&lt;run windowWidthPx="1440" windowHeightPx="900"&gt;
		&lt;runName&gt;001_gotoSeleniumHQ&lt;/runName&gt;
		&lt;url&gt;http://www.seleniumhq.org/&lt;/url&gt;
		&lt;successCondition&gt;
			&lt;element&gt;//h2&lt;/element&gt;
			&lt;elementContent&gt;What is Selenium?&lt;/elementContent&gt;
		&lt;/successCondition&gt;
	&lt;/run&gt;
	&lt;run&gt;
		&lt;runName&gt;002_gotoDownload&lt;/runName&gt;
		&lt;url&gt;http://www.seleniumhq.org/download/&lt;/url&gt;
		&lt;successCondition&gt;
			&lt;element&gt;//h2&lt;/element&gt;
			&lt;elementContent&gt;Downloads&lt;/elementContent&gt;
		&lt;/successCondition&gt;
	&lt;/run&gt;
	&lt;run&gt;
		&lt;runName&gt;003_search&lt;/runName&gt;
		&lt;inputFields&gt;
			&lt;inputField&gt;
				&lt;element&gt;//input[@name="q"]&lt;/element&gt;
				&lt;value&gt;webdriver&lt;/value&gt;
			&lt;/inputField&gt;
		&lt;/inputFields&gt;
		&lt;clickByXPATH&gt;//input[@value="Go"]&lt;/clickByXPATH&gt;
		&lt;successCondition elementExtractor="BY_ID" contentEvaluator="CONTAINS"&gt;
			&lt;element&gt;cse-footer&lt;/element&gt;
			&lt;elementContent&gt;Google&lt;/elementContent&gt;
		&lt;/successCondition&gt;
	&lt;/run&gt;
&lt;/plan&gt;
			</code>
		</pre>
		<ul>
			<li>&lt;windowWidthPx&gt; is an **optional** attribute where you put window width size in pixel</li>
			<li>&lt;windowHeightPx&gt; is an **optional** attribute where you put window height size in pixel</li>
			<li>&lt;runName&gt; is a **require** node where you put the name (unique at all plan) and it used for screenshot naming</li>
			<li>&lt;url&gt; is an **optional** node where you put the "goToURL". **Remove this if it is not necessary for your run**</li>
			<li>&lt;inputFields&gt; is an **optional** list where you put your form filler. You must to use XPath for searching element and fill it with a value</li>
			<li>&lt;clickByXPATH&gt; is an **optional** node where you put your preferred action (click action)</li>
			<li>&lt;successCondition&gt; is a **require** node where you put the element selection and the value to be checked</li>
		</ul>
	</li>
</ol>

## Execute your plan/plans and enjoy it :)
You are ready to execute your plan, or plans.<br/>
Simple open your console (shell), point to your cloned repo (at pom.xml level) and call the command below:<br/>
<pre>
	<code>
mvn -f pom.xml compile exec:java -Dexec.mainClass=org.bitmarte.architecture.utils.testingframework.selenium.RunTestSuite -Dexec.args="/var/selenium/my-cfg"
	</code>
</pre>
**Pay attention: specify your base config path as the only require java argument ("/var/selenium/my-cfg" for this tutorial)**

<br/><br/><hr/>
## Advanced success condition usage
You can use some advanced matchers at &lt;successCondition&gt; node in order to use make your test more powerful.

### contentEvaluator
You can see the allowed values for this attribute:

| Attribute value        		| Description										|
| ----------------------------- | ------------------------------------------------- |
| STARTWITH						| StringUtils.startWithIgnoreCase will be use		|
| ENDWITH						| StringUtils.endWithIgnoreCase will be use			|
| CONTAINS						| StringUtils.containsIgnoreCase will be use		|
| REGEX							| Regex pattern matcher will be use					|
| EQUALS						| StringUtils.equalsIgnoreCase						|

Here you can find an example that you can use for matching all numbers inside an "h5" html tag:

<pre>
	<code>
&lt;successCondition contentEvaluator="REGEX"&gt;
	&lt;element&gt;//h5&lt;/element&gt;
	&lt;elementContent&gt;[0-9]&lt;/elementContent&gt;
&lt;/successCondition&gt;
	</code>
</pre>

**Pay attention: when attribute is not present or an unknown value is used, the dafault content evaluator will be use: StringUtils.equalsIgnoreCase**
<br/><br/>
### elementExtractor
You can see the allowed values for this attribute:

| Attribute value        		| Description																|
| ----------------------------- | ------------------------------------------------------------------------- |
| BY_ID							| Using HTML "id" attribute for finding element								|
| BY_XPATH						| Using XPath for finding element, all matched elements will be returned	|

Here you can find an example that you can use for matching all numbers inside an html element with "myName" ID value:

<pre>
	<code>
&lt;successCondition contentEvaluator="REGEX" elementExtractor="BY_ID"&gt;
	&lt;element&gt;myName&lt;/element&gt;
	&lt;elementContent&gt;[0-9]&lt;/elementContent&gt;
&lt;/successCondition&gt;
	</code>
</pre>

**Pay attention: when attribute is not present or an unknown value is used, the dafault content evaluator will be use: BY_XPATH**
<br/><br/>
## Advanced input field usage
You can use some advanced matchers at &lt;inputField&gt; node in order to use make your test more powerful.

### elementExtractor
You can see the allowed values for this attribute:

| Attribute value        		| Description																|
| ----------------------------- | ------------------------------------------------------------------------- |
| BY_ID							| Using HTML "id" attribute for finding element								|
| BY_XPATH						| Using XPath for finding element, all matched elements will be returned	|

Here you can find an example that you can use for filling value "myValue" into an html element with "myName" ID value:

<pre>
	<code>
&lt;inputField elementExtractor="BY_ID"&gt;
	&lt;element&gt;myName&lt;/element&gt;
	&lt;value&gt;myValue&lt;/value&gt;
&lt;/inputField&gt;
	</code>
</pre>

**Pay attention: when attribute is not present or an unknown value is used, the dafault content evaluator will be use: BY_XPATH**