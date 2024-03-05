package api;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class RestApiMockedTest {

    private final String VALID_API_KEY = "1234567890123456";

    @BeforeAll
    public static void setup() {

        baseURI = "http://35.208.34.242";
        port = 8080;

    }

    @ParameterizedTest
    @CsvSource({
            "Vasya, 12345",
            "Petya, 54321",
            "Masha, qwerty"
    })
    public void loginWithValidUsernameAndPasswordCheckResponseBodyAndResponseCodeIsOk(String username, String password) {

        Response response = given()
                .header("content-type", "application/json")
                .log()
                .all()
                .when()
                .get("/test-orders?username={username}&password={password}", username, password)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .extract()
                .response();

        assertTrue(response.path("message").toString().contains(username));
        assertEquals(16, response.path("apiKey").toString().length());

    }

    @Test
    public void loginWithoutUsernameAndPasswordCheckResponseBodyAndResponseCodeIsBadRequest() {

        Response response = given()
                .header("Content-type", "application/json")
                .log()
                .all()
                .when()
                .get("/test-orders?username=&password=")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .extract()
                .response();

        assertNull(response.path("apiKey"));
        assertEquals("Username or password is missing", response.path("message"));

    }

    @Test
    public void getAllOrderAndCheckResponseCodeIsOk() {

        System.out.println("\n===========================");
        //RestAssured
        given()
                .log()
                .all()
                .when()
                .get("/test-orders/get_orders")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK);

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 9, 10})
    public void getOrderByValidIdCheckMatchesInResponseBodyAndResponseCodeIsOk(int validId) {

        System.out.println("\n===========================");
        Response response = given()
                .log()
                .all()
                .when()
                .get("/test-orders/{validId}", validId)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .extract()
                .response();

        var id = response.path("id");

        assertEquals(validId, id);
        assertEquals("OPEN", response.path("status"));

    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11, 12, 20})
    public void getOrderByInvalidIdAndCheckResponseCodeIsBadRequest(int invalidId) {

        System.out.println("\n===========================");
        given()
                .log()
                .all()
                .when()
                .get("/test-orders/{invalidId}", invalidId)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 9, 10})
    public void deleteOrderIfValidIdAndApiKeyCheckResponseCodeIsOk(int validId) {

        System.out.println("\n===========================");
        given()
                .log()
                .all()
                .when()
                .header("api_key", VALID_API_KEY)
                .delete("/test-orders/{validId}", validId)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_NO_CONTENT);

    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11, 12, 20})
    public void deleteOrderIfInvalidIdAndCheckResponseCodeIsBadRequest(int invalidId) {

        System.out.println("\n===========================");
        given()
                .log()
                .all()
                .when()
                .header("api_key", VALID_API_KEY)
                .delete("/test-orders/{invalidId}", invalidId)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "3", "67594063", "123456789012345",
            "qwertyuiopasdfgh", "5w2r4y7i3p1s6f8h", "$&*()_+-=|:<>'`~", "123456789.098765",
            "98765432109876543", "09876543210987654321"})
    public void deleteOrderIfInvalidApiKeyAndCheckResponseCodeIsUnauthorized(String invalidApiKey) {

        System.out.println("\n===========================");
        given()
                .log()
                .all()
                .when()
                .header("api_key", invalidApiKey)
                .delete("/test-orders/7")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void deleteOrderIfNoHeaderFieldAndCheckResponseCodeIsBadRequest() {

        System.out.println("\n===========================");
        given()
                .log()
                .all()
                .when()
                .delete("/test-orders/7")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {

            "{\"status\": \"OPEN\", \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": \"ACCEPTED\", \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": \"INPROGRESS\", \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": \"DELIVERED\", \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",

    })
    public void createOrderWithCorrectJsonDataCheckMatchesInResponseBodyAndResponseCodeIsOk(String str) {

        var id = given()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .body(str)
                .log()
                .all()
                .when()
                .post("/test-orders")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("id");

        assertNotEquals(0, id);

    }

    String str = "\"status\": \"OPEN\", \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0";

    @Test
    public void createOrderWithCorrectJsonDataButWithoutHeadersCheckResponseCodeIsBadRequest() {

        System.out.println("\n===========================");
        given()
                .body(str)
                .log()
                .all()
                .when()
                .delete("/test-orders/7")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {

            "{\"status\": 12345, \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": 'F', \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": \"qwerty\", \"courierId\": 0, \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": \"OPEN\", \"courierId\": 'f', \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": \"OPEN\", \"courierId\": \"qwerty\", \"customerName\": \"dm\", \"customerPhone\": 123456, \"comment\": \"qwerty\", \"id\": 0}",
            "{\"status\": , \"courierId\": , \"customerName\": , \"customerPhone\": , \"comment\": , \"id\": }",

    })
    public void createOrderWithIncorrectJsonDataCheckResponseBodyAndResponseCodeIsBadRequest(String str) {

        Response response = given()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .body(str)
                .log()
                .all()
                .when()
                .post("/test-orders")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals("Incorrect query", response.asString());

    }

    @ParameterizedTest
    @CsvSource({
            //Threads, required requests per sec, timer
            "20, 30, 30",
            "15, 20, 240"
    })
    public void performanceTestOfGetAllOrdersEndpoint(int threadsAmount, int load, int timer)
            throws InterruptedException {

        assertEquals(200, PerformanceTest.test(threadsAmount, load, timer));

    }
}

class PerformanceTest implements Runnable {

    Thread thisThread;
    static String threadThatStoppedTest;
    static double load;
    static int timer;
    static int startTime;
    static int caughtCode;
    static volatile boolean stop;
    double threadCurrentLoad;
    int threadStatusCode;
    int requestsCount;
    int sumRespT;
    int respTime;
    int minResponseTime;
    int maxResponseTime;
    int time;
    int thrdTimer;

    PerformanceTest(String name) {

        thisThread = new Thread(this, name);

    }

    static void clear(PerformanceTest[] tth) {

        for (PerformanceTest testThread : tth) {

            if (testThread.thisThread.isAlive()) testThread.thisThread.interrupt();

        }

        PerformanceTest.threadThatStoppedTest = "";

    }

    static int test(int threadsAmount, int load, int timer) throws InterruptedException {

        PerformanceTest.startTime = PerformanceTest.timer = timer * 1000;
        PerformanceTest.load = (load * 0.001) / threadsAmount;
        PerformanceTest.caughtCode = 0;
        PerformanceTest.stop = false;

        PerformanceTest[] thrd = new PerformanceTest[threadsAmount];

        for (int i = 0; i < thrd.length; i++) {

            thrd[i] = PerformanceTest.createAndStart("Thread-" + i);

        }

        while (!PerformanceTest.stop && PerformanceTest.timer > 0) {

            Thread.sleep(1000);
            PerformanceTest.timer -= 1000;

            double minRespT = 0;
            double maxRespT = 0;
            double sumOfAvgRespT = 0;
            int totalRequestsCount = 0;
            double minLoad = 0;
            double maxLoad = 0;
            int isAlive = 0;

            for (PerformanceTest performanceTest : thrd) {

                if (performanceTest.thisThread.isAlive()) isAlive++;

                if (minRespT == 0 || minRespT > performanceTest.minResponseTime)
                    minRespT = performanceTest.minResponseTime;
                if (maxRespT == 0 || maxRespT < performanceTest.maxResponseTime)
                    maxRespT = performanceTest.maxResponseTime;

                totalRequestsCount += performanceTest.requestsCount;
                sumOfAvgRespT += (double) performanceTest.sumRespT / performanceTest.requestsCount;

                if (maxLoad == 0 || maxLoad < performanceTest.threadCurrentLoad)
                    maxLoad = performanceTest.threadCurrentLoad;

                if (minLoad == 0 || minLoad > performanceTest.threadCurrentLoad)
                    minLoad = performanceTest.threadCurrentLoad;

            }

            if (isAlive == 0) {

                System.out.println("All threads are interrupted!");
                PerformanceTest.stop = true;

            }

            double i = timer - PerformanceTest.timer * 0.001;
            double s = (double) totalRequestsCount / thrd.length;
            double avgRespT = sumOfAvgRespT / thrd.length;

            System.out.println("\u21cb\nTotal avg. req/sec:\t\t\t\t\t\t\t\t  " + String.format("%.2f", totalRequestsCount / i) +
                    "\nReq/sec from each thread(" + isAlive + ") min-avg-max: " + String.format("%.3f", minLoad * 1000) +
                    " - " + String.format("%.3f", s / i) + " - " + String.format("%.3f", maxLoad * 1000) +
                    "\nResp. time min-avg-max:\t\t\t\t\t  " + String.format("%.3f", minRespT * 0.001) +
                    " - " + String.format("%.3f", avgRespT * 0.001) + " - " + String.format("%.3f", maxRespT * 0.001) +
                    "\nTime rem.: \t\t\t\t\t\t\t\t\t\t\t" + PerformanceTest.timer / 1000);

        }

        Thread.sleep(5000);
        PerformanceTest.clear(thrd);

        return PerformanceTest.caughtCode;

    }

    public static PerformanceTest createAndStart(String name) {

        PerformanceTest myThrd = new PerformanceTest(name);
        myThrd.thisThread.start();

        return myThrd;

    }

    @Override
    public void run() {

        thrdTimer = time = timer;

        while (caughtCode != 429 && timer != 0 && !stop) {

            if (thrdTimer != timer) thrdTimer = time = timer;

            threadCurrentLoad = (double) requestsCount / (startTime - time);

            if (threadCurrentLoad <= load) {

                Response response = get("http://35.208.34.242:8080/test-orders/get_orders")
                        .then()
                        .extract()
                        .response();

                requestsCount++;
                threadStatusCode = response.statusCode();
                respTime = (int) response.getTime();
                sumRespT += respTime;
                time -= respTime;

                if (minResponseTime == 0 || minResponseTime > respTime) minResponseTime = respTime;

                if (maxResponseTime == 0 || maxResponseTime < respTime) maxResponseTime = respTime;

                if (stop) return;

                else if (threadStatusCode == 429) {

                    stop = true;
                    caughtCode = threadStatusCode;
                    threadThatStoppedTest = thisThread.getName();
                    System.out.println("\n================================\n" + threadThatStoppedTest +
                            " stopped the test!" + "\n================================");
                    return;

                } else caughtCode = threadStatusCode;

            } else {

                time = time - 400;

                try {
                    System.out.print('.');
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}