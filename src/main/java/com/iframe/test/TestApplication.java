package com.iframe.test;

import com.codeborne.selenide.*;
import com.iframe.test.dao.TestDao;
import org.openqa.selenium.Alert;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.element;

@SpringBootApplication
public class TestApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(TestApplication.class.getSimpleName());

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	String tenderNumber =  "0338200006520000003";

	@Autowired
	TestDao testDao;

	@Override
	public void run(String... args) throws Exception {
		/*
		File driverFile = new File("C:\\IEdriver\\IEDriverServer.exe");
		System.setProperty("webdriver.ie.driver",driverFile.getAbsolutePath());
		Configuration.browserCapabilities.setCapability("ie.forceCreateProcessApi",true);
		Configuration.browserCapabilities.setCapability("nativeEvents", true);
		Configuration.browserCapabilities.setCapability("unexpectedAlertBehaviour", "accept");
		Configuration.browserCapabilities.setCapability("ignoreProtectedModeSettings", true);
		Configuration.browserCapabilities.setCapability("disable-popup-blocking", true);
		Configuration.browserCapabilities.setCapability("enablePersistentHover", true);
		Configuration.browserCapabilities.setCapability("ignoreZoomSetting", true);
		Configuration.browserCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
		Configuration.browserCapabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		Configuration.browser="Internet Explorer";
		logger.info("Запуск бота" + LocalDateTime.now());
		open("https://www.sberbank-ast.ru/purchaseList.aspx");
		 */


		File driverFile = new File("C:\\App\\chromedriver.exe");
		System.setProperty("webdriver.chrome.driver",driverFile.getAbsolutePath());
		ChromeOptions options = new ChromeOptions();
		options.setAcceptInsecureCerts(true);
		options.addExtensions(new File("C:\\App\\extension_1_2_7_0.crx"));
		DesiredCapabilities capabilities = new DesiredCapabilities();
		options.merge(capabilities);
		ChromeDriver driver = new ChromeDriver(options);
		WebDriverRunner.setWebDriver(driver);

		open("https://www.sberbank-ast.ru/purchaseList.aspx");
		WebDriverRunner.getWebDriver().manage().window().maximize();

		element(byId("ctl00_ctl00_loginctrl_anchSignOn")).click();



		element(byId("mainContent_DDL1")).selectOptionByValue("5EB4A43B643B922465BF95108F01BBA8F6C7C6E7");
		element(byId("btnEnter")).click();

		element(byId("searchInput")).setValue(tenderNumber).pressEnter();
		SelenideElement selenideElement = element(byId("resultTable"));
		selenideElement.shouldBe(Condition.visible);

		SelenideElement divonerow = selenideElement.find(byClassName("element-in-one-row"));
		ElementsCollection els = divonerow.findAll(byCssSelector("input"));
		els.get(0).click();

		try {
			//скроллим до нажатия кнопки на выбор номера счета
			//executeJavaScript("window.scrollBy(0,400)", "");
			SelenideElement button = element(byXpath("//table[@id='bxAccount']/tbody/tr/td[2]/input[2]"));
			button.click();
			switchTo().frame("spravIframe");
			element(byXpath("//*[@id=\"XMLContainer\"]/table/tbody/tr[2]/td[1]/a/span")).shouldBe(Condition.visible).click();
			switchTo().defaultContent();
			String inputSchetNumber = element(byXpath("//*[@id=\"ctl00_ctl00_phWorkZone_phDocumentZone_nbtPurchaseRequest_bxAccount_account\"]")).getValue();

			if(StringUtils.hasText(inputSchetNumber)) {
				logger.info("Удалось выбрать номер счета");
				testDao.firstJourInsert(tenderNumber,"Выбрали номер счета",true,null);
			}

			//согласие на поставку услуг
			//executeJavaScript("window.scrollBy(0,400)", "");
			element(byXpath("//*[@id=\"XMLContainer\"]/div/table[5]/tbody/tr[2]/td[2]/input[2]")).click();
			switchTo().frame("spravIframe");
			element(byXpath("//*[@id=\"ctl00_phDataZone_btnOK\"]")).click();
			switchTo().defaultContent();
			String deliveryConsention = element(byXpath("//*[@id=\"ctl00_ctl00_phWorkZone_phDocumentZone_nbtPurchaseRequest_reqAgreementAnswer\"]")).getText();

			if(StringUtils.hasText(deliveryConsention)) {
				logger.info("Согласились на предоставление услуг");
				testDao.firstJourInsert(tenderNumber,"Согласились на предоставление услуг", true, null);
			}

			//скроллим до кнопки формы согласия
			//executeJavaScript("window.scrollBy(0,400)", "");
			element(byXpath("//*[@id=\"ctl00$ctl00$phWorkZone$phDocumentZone$nbtPurchaseRequest$reqDocsPart1tblDoc\"]/tbody/tr/td[2]/input[1]")).click();
			switchTo().frame("spravIframe");
			element(byXpath("//*[@id=\"ctl00_phDataZone_FileStoreContainer\"]/div[1]/a")).click();
			switchTo().defaultContent();
			String formConsensionDoc = element(byXpath("//*[@id=\"txbFileName\"]")).getText();

			if (StringUtils.hasText(formConsensionDoc)) {
				logger.info("Приложили документ согласия");
				testDao.firstJourInsert(tenderNumber,"приложили документ на предоставление услуг", true, null);
			}

			//скроллим до кнопки подписать декларацию
			//executeJavaScript("window.scrollBy(0,1000)", "");
			element(byXpath("//*[@id=\"tblrequireddocs22\"]/tbody/tr[4]/td[2]/input[2]")).click();
			switchTo().frame("spravIframe");
			executeJavaScript("window.scrollBy(0,700)", "");
			element(byXpath("//*[@id=\"ctl00_phDataZone_btnOK\"]")).click();
			switchTo().defaultContent();
			String declarationConsent = element(byXpath("//*[@id=\"ctl00_ctl00_phWorkZone_phDocumentZone_nbtPurchaseRequest_reqDeclarationRequirementsAnswer\"]")).getText();

			if(StringUtils.hasText(declarationConsent)) {
				logger.info("подписали декларацию");
				testDao.firstJourInsert(tenderNumber,"подписали декларацию", true, null);
			}

			//скроллим чтобы приложить документы 2 часть
			//executeJavaScript("window.scrollBy(0,400)", "");
			element(byXpath("//*[@id=\"ctl00$ctl00$phWorkZone$phDocumentZone$nbtPurchaseRequest$FileAttach2tblDoc\"]/tbody/tr/td[2]/input[1]")).click();
			switchTo().frame("spravIframe");
			element(byXpath("//*[@id=\"ctl00_phDataZone_FileStoreContainer\"]/div[2]/a")).click();
			switchTo().defaultContent();
			String form2part = element(byXpath("//*[@id=\"txbFileName\"]/span")).getText();

			if(StringUtils.hasText(form2part)) {
				logger.info("приложили документы, вторую часть");
				testDao.firstJourInsert(tenderNumber,"приложили вторую часть документов", true, null);
			}

			//Подачу самой заявки пока не делаем
			//element(byXpath("//*[@id=\"ctl00_ctl00_phWorkZone_SignPanel_btnSignAllFilesAndDocument\"]")).click();
			if (
					StringUtils.hasText(inputSchetNumber)
							& StringUtils.hasText(deliveryConsention)
							& StringUtils.hasText(formConsensionDoc)
							& StringUtils.hasText(declarationConsent)
							& StringUtils.hasText(form2part)) {
				element(byXpath("//*[@id=\"ctl00_ctl00_phWorkZone_SignPanel_btnSignAllFilesAndDocument\"]")).click();
				element(byXpath("//*[@id=\"ctl00_ctl00_phWorkZone_SignPanel_btnSignAllFilesAndDocument\"]")).waitUntil(Condition.not(Condition.visible), 60000);
				SelenideElement errorMessage = element(byXpath("//*[@id=\"ctl00_ctl00_phWorkZone_errorMsg\"]"));
				logger.info(errorMessage.toString());
				if(errorMessage.text().contains("Ваш документ зарегистрирован как отвергнутый.")) {
					testDao.firstJourInsert(tenderNumber,"нажатие кнопки подписать и отправить", false,"подписываем и отправляем");
				}
				else {
					testDao.firstJourInsert(tenderNumber,"нажатие кнопки подписать и отправить", true,"подписываем и отправляем");
				}
			}


		}catch (Exception e) {
			logger.error(e.getMessage());
		}
		logger.info("Окончание работы бота" + LocalDateTime.now());
	}
}
