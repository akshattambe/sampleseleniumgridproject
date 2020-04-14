package org.example.grid.tool;

import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.server.SeleniumServer;

import java.io.File;

public class Entrypoint {


    public static void main(String[] args) {
        GridHubConfiguration hubConfiguration = new GridHubConfiguration();
        SeleniumServer hubServer = new SeleniumServer(hubConfiguration);
        hubServer.boot();

        // bootAndVerifyFirefoxNode();
        bootAndVerifyChromeNode();
    }

    private static void bootAndVerifyFirefoxNode() {
        bootFirefoxNode();
        FirefoxOptions firefoxOpt = new FirefoxOptions();
        firefoxOpt.addArguments("--headless");
        firefoxOpt.addArguments("--no-sandbox");
        firefoxOpt.addArguments("--start-maximized");
        firefoxOpt.addArguments("--window-size=1920,1080");
        firefoxOpt.setCapability("browserName", "firefox".toLowerCase().trim());
        RemoteWebDriver.builder().url("http://localhost:4444/wd/hub/").oneOf(firefoxOpt).build().quit();
    }

    private static void bootAndVerifyChromeNode() {
        bootChromeNode();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--window-size=1920,1080");
        RemoteWebDriver.builder()
                .url("http://localhost:4444/wd/hub/")
                .oneOf(chromeOptions)
                .build()
                .quit();
    }

    private static void bootFirefoxNode() {
        // even if you explicitly set the binary it will check the sys prop :/
        System.setProperty("webdriver.gecko.driver", getPathToExecutable("geckodriver.exe"));
        GridNodeConfiguration gridNodeConfiguration = new GridNodeConfiguration();
        gridNodeConfiguration.port = 4464;

        SeleniumServer nodeServer = new SeleniumServer(gridNodeConfiguration);
        nodeServer.boot();
    }

    private static void bootChromeNode() {
        // even if you explicitly set the binary it will check the sys prop :/
        System.setProperty("webdriver.chrome.driver", getPathToExecutable("chromedriver.exe"));
        GridNodeConfiguration gridNodeConfiguration = new GridNodeConfiguration();

        gridNodeConfiguration.port = 4454;
        SeleniumServer nodeServer = new SeleniumServer(gridNodeConfiguration);
        nodeServer.boot();
    }

    private static String getPathToExecutable(String executableFileName) {
        String fileSep = System.getProperty("file.separator");
        String userDir = System.getProperty("user.dir");
        String uri =
                new StringBuilder(userDir)
                        .append(fileSep)
                        .append("grid-tool")
                        .append(fileSep)
                        .append("src")
                        .append(fileSep)
                        .append("main")
                        .append(fileSep)
                        .append("resources")
                        .append(fileSep)
                        .append(executableFileName)
                        .toString();
        if (!new File(uri).canExecute()) {
            throw new IllegalStateException(uri);
        }
        return uri;
    }
}