package api;

//import com.google.gson.JsonObject;

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

//    @Test
//    public void createAnOrderWithCorrectJsonDataCheckResponseBodyAndResponseCodeIsOk() {
//
//        JsonObject body = new JsonObject();
//        body.addProperty("status", "OPEN");
//        body.addProperty("courierId", 0);
//        body.addProperty("customerName", "dm");
//        body.addProperty("customerPhone", 123456);
//        body.addProperty("comment", "qwerty");
//        body.addProperty("id", 0);
//
//        Response response = given()
//                .header("Accept", "application/json")
//                .header("Content-type", "application/json")
//                .body(body)
//                .log()
//                .all()
//                .when()
//                .post("/test-orders")
//                .then()
//                .log()
//                .all()
//                .statusCode(HttpStatus.SC_OK)
//                .and()
//                .extract()
//                .response();
//
//    }

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
            "30, 30, 30",
            "20, 20, 240"
    })
    public void performanceTestOfGetAllOrdersEndpoint(int threadsAmount, int load, int timer)
            throws InterruptedException {

        PerformanceTest.threadsAmount = threadsAmount;
        PerformanceTest.timer = timer * 1000;
        PerformanceTest.load = load;
        PerformanceTest.startTime = PerformanceTest.timer;
        PerformanceTest.caughtCode = 0;
        PerformanceTest.stop = false;

        PerformanceTest[] thrd = new PerformanceTest[threadsAmount];

        for (int i = 0; i < thrd.length; i++) {

            thrd[i] = PerformanceTest.createAndStart("Thread-" + i);

        }

        while (!PerformanceTest.stop && PerformanceTest.timer > 0) {

            PerformanceTest.timer -= 1000;
            Thread.sleep(1000);

            double minLoad = 0;
            double maxLoad = 0;
            int minRequestsCount = 0;
            int maxRequestsCount = 0;
            int totalRequestsCount = 0;

            for (int i = 0; i < thrd.length; i++) {

                totalRequestsCount += thrd[i].requestsCount;

                if (i == 0) {
                    minLoad = thrd[i].threadCurrentLoad;
                    minRequestsCount = thrd[i].requestsCount;
                }
                if (maxLoad < thrd[i].threadCurrentLoad) maxLoad = thrd[i].threadCurrentLoad;
                if (minLoad > thrd[i].threadCurrentLoad) minLoad = thrd[i].threadCurrentLoad;

                if (maxRequestsCount < thrd[i].requestsCount) maxRequestsCount = thrd[i].requestsCount;
                if (minRequestsCount > thrd[i].requestsCount) minRequestsCount = thrd[i].requestsCount;

            }

            double i = timer - PerformanceTest.timer * 0.001;
            double s = (double) totalRequestsCount / thrd.length;

            System.out.println("Avg. req/sec: " + String.format("%.3f", totalRequestsCount / i) +
                    " | Req/sec from each thread(" + thrd.length + ") min-avg-max: " +
                    String.format("%.3f", minLoad) + " - " + String.format("%.3f", s / i) + " - " + String.format("%.3f", maxLoad) +
                    " | Total req.: " + totalRequestsCount + " | Time rem.: " + PerformanceTest.timer / 1000);

        }

        Thread.sleep(5000);

        PerformanceTest.clear(thrd);

        assertEquals(200, PerformanceTest.caughtCode);

    }
}

class PerformanceTest implements Runnable {

    Thread thisThread;
    static String threadThatStoppedTest;
    static int timer;
    static int startTime;
    static int threadsAmount;
    static volatile int caughtCode;
    static int load;
    static boolean stop;
    double threadCurrentLoad;
    int threadStatusCode;
    int requestsCount;
    int time;

    PerformanceTest(String name) {

        thisThread = new Thread(this, name);

    }

    static void clear(PerformanceTest[] tth) {

        for (PerformanceTest testThread : tth) {

            if (testThread.thisThread.isAlive()) testThread.thisThread.interrupt();

        }

        PerformanceTest.threadThatStoppedTest = "";

    }

    public static PerformanceTest createAndStart(String name) {

        PerformanceTest myThrd = new PerformanceTest(name);
        myThrd.thisThread.start();

        return myThrd;

    }

    @Override
    public void run() {

        while (caughtCode != 429 && timer != 0 && !stop) {

            time = startTime - timer;

            threadCurrentLoad = requestsCount / (time * 0.001);

            if (requestsCount / (time * 0.001) <= (double) load / PerformanceTest.threadsAmount) {

                threadStatusCode = get("http://35.208.34.242:8080/test-orders/get_orders")
                        .statusCode();

                requestsCount++;


                if (stop) return;

                else if (threadStatusCode == 429) {

                    caughtCode = threadStatusCode;
                    threadThatStoppedTest = thisThread.getName();
                    stop = true;
                    System.out.println(threadThatStoppedTest + " stopped the test!");
                    return;

                } else caughtCode = threadStatusCode;

            } else {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}