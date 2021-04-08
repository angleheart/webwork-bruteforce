import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private ExecutorService executor = Executors.newCachedThreadPool();

    private final String targetURL;
    private final int totalNumberBoxes;
    private final int startBox;
    private final int endBox;
    private final boolean includeNegative;
    private final double initialPrecision;
    private final int increasePrecisionAt;
    private final boolean showAttempts;
    private final boolean headless;

    private BigDecimal[] answers;
    private boolean allAnswersAcquired;
    private int workingIndex;
    private double currentPrecision;
    long startTime;
    private long totalAttemptsMade;
    private int targetBox;
    private BigDecimal attempt;
    private boolean firstStart;
    private String title;

    private boolean firstPrecisionIncrease;

    public Main(String URL, int totalNumberBoxes, int startBox, int endBox, boolean includeNegative, double initialPrecision, int increasePrecisionAt, boolean showAttempts, boolean headless) {
        this.targetURL = URL;
        this.totalNumberBoxes = totalNumberBoxes;
        this.endBox = endBox;
        this.includeNegative = includeNegative;
        this.initialPrecision = initialPrecision;
        this.increasePrecisionAt = increasePrecisionAt;
        this.showAttempts = showAttempts;
        this.headless = headless;

        this.answers = new BigDecimal[(endBox - startBox) + 1];
        this.allAnswersAcquired = false;
        this.workingIndex = 0;
        this.currentPrecision = initialPrecision;

        this.startBox = startBox;
        this.targetBox = startBox;
        this.attempt = BigDecimal.valueOf(0);
        firstStart = true;
        firstPrecisionIncrease = true;
        totalAttemptsMade = 0;
        startTime = System.currentTimeMillis();
    }


    public void launchInstances(int instances) {
        for (int i = 0; i < instances; i++) {
            executor.execute(() -> {
                BruteForceInstance instance = new BruteForceInstance();
                try {
                    instance.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public class BruteForceInstance {

        private WebDriver driver;
        int instanceTargetBox;
        BigDecimal instanceAttempt;

        public void start() throws InterruptedException {
            boolean clearRequired = false;
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-features=VizDisplayCompositor");

            if (headless)
                options.addArguments("--headless");

            driver = new ChromeDriver(options);
            Thread.sleep(3000);
            driver.get(targetURL);
            Thread.sleep(5000);

            if (firstStart) {
                firstStart = false;
                title = driver.findElement(By.className("page-title")).getText();
                System.out.println("Initiating brute force attack on " + title);
            }


            clearAll();

            while (!allAnswersAcquired) {
                instanceTargetBox = targetBox;
                instanceAttempt = getAttempt();

                if (clearRequired) {
                    clearRequired = false;
                    clearAll();
                }
                if (instanceTargetBox <= totalNumberBoxes) {
                    driver.findElement(By.id(getBoxName(instanceTargetBox))).clear();
                    driver.findElement(By.id(getBoxName(instanceTargetBox))).sendKeys(instanceAttempt.toString());
                    if (showAttempts)
                        System.out.println("Attempting " + instanceAttempt);
                    driver.findElement(By.id("submitAnswers_id")).click();
                    totalAttemptsMade++;
                }
                if (targetBox != instanceTargetBox) {
                    clearRequired = true;
                    Thread.sleep(1000);
                    continue;
                }

                if (isCorrect()) {
                    targetBox++;
                    clearRequired = true;
                    addFoundAnswerToArray(instanceAttempt);
                }

            }

            driver.quit();
        }

        private void clearAll() {
            for (int i = 1; i <= totalNumberBoxes; i++)
                driver.findElement(By.id(getBoxName(i))).clear();
        }

        private boolean isCorrect() {
            if (targetBox != instanceTargetBox)
                return false;
            String s = driver.findElement(By.className("scoreSummary")).getText();
            String[] lines = s.split("\\r?\\n");
            return lines[2].charAt(24) != '0';
        }

    }

    private String getBoxName(int boxNumber) {
        return boxNumber < 10 ? "AnSwEr000" + boxNumber : "AnSwEr00" + boxNumber;
    }


    private BigDecimal getAttempt() {
        BigDecimal attemptToReturn = attempt;

        if (includeNegative) {

            if (attempt.compareTo(BigDecimal.valueOf(0)) > 0)
                attempt = attempt.multiply(BigDecimal.valueOf(-1));
            else {

                if (firstPrecisionIncrease)
                    attempt = attempt.abs().add(BigDecimal.valueOf(initialPrecision));
                else
                    attempt = attempt.abs().add(BigDecimal.valueOf(currentPrecision * 2));

            }
        } else {
            if (firstPrecisionIncrease)
                attempt = attempt.add(BigDecimal.valueOf(initialPrecision));
            else
                attempt = attempt.add(BigDecimal.valueOf(currentPrecision * 2));

        }

        if (attempt.compareTo(BigDecimal.valueOf(increasePrecisionAt)) > 0)
            callPrecisionIncrease();

        return attemptToReturn;
    }


    private void callPrecisionIncrease() {
        firstPrecisionIncrease = false;
        System.out.println("Increasing guess precision to " + currentPrecision / 2);
        currentPrecision /= 2;
        attempt = BigDecimal.valueOf(currentPrecision);
    }

    private void addFoundAnswerToArray(BigDecimal answer) throws InterruptedException {
        workingIndex++;
        answers[workingIndex - 1] = answer;
        System.out.println("----------------------------------------------------------------");
        System.out.println("!!!ANSWER FOUND!!! for box " + (startBox + (workingIndex - 1)) + ":  " + answer.toString());
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        startTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + elapsed + " seconds");
        System.out.println("Number of attempts: " + totalAttemptsMade);
        System.out.println("Average attempts per second: " + (double) totalAttemptsMade / elapsed);
        System.out.println("----------------------------------------------------------------");
        totalAttemptsMade = 0;

        if (workingIndex >= answers.length) {
            allAnswersAcquired = true;
            submitAnswers();
            return;
        }
        firstPrecisionIncrease = true;
        currentPrecision = initialPrecision;
        attempt = BigDecimal.valueOf(0);
    }

    private void submitAnswers() throws InterruptedException {
        System.out.println("All answers found! Attempting submission...");
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(targetURL);
        Thread.sleep(500);
        System.out.println("---------");
        System.out.println(title);

        for (int i = 1; i <= totalNumberBoxes; i++)
            driver.findElement(By.id(getBoxName(i))).clear();

        int k = 0;
        for (int i = startBox; i <= endBox; i++) {
            driver.findElement(By.id(getBoxName(i))).sendKeys(answers[k].toString());
            System.out.print(i + ". ");
            System.out.println(answers[k].toString());
            k++;
        }
        System.out.println("---------");
        driver.findElement(By.id("submitAnswers_id")).click();
        String s = driver.findElement(By.className("scoreSummary")).getText();
        String[] lines = s.split("\\r?\\n");
        System.out.println(lines[0]);
        System.out.println(lines[1]);
        System.out.println(lines[3]);
        System.out.println(lines[4]);
    }

}
