package api;

import com.google.gson.Gson;
import dto.OrderDtoMocked;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import utils.GetAllOrdersPerformanceTest;
import utils.RandomDataGenerator;

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

    @Test
    public void createOrderWithCorrectJsonDataUsingDTOCheckMatchesInResponseBodyAndResponseCodeIsOk() {

        OrderDtoMocked orderDtoMocked = new OrderDtoMocked();
        orderDtoMocked.setStatus("OPEN");
        orderDtoMocked.setCourierId(0);
        orderDtoMocked.setCustomerName(RandomDataGenerator.generateName());
        orderDtoMocked.setCustomerPhone(RandomDataGenerator.generatePhone());
        orderDtoMocked.setComment(RandomDataGenerator.generateComment());
        orderDtoMocked.setId(0);

        String name = given()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .body(new Gson().toJson(orderDtoMocked))
                .log()
                .all()
                .when()
                .post("/test-orders")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("customerName");

        assertEquals(orderDtoMocked.getCustomerName(), name);

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
    public void createOrderWithCorrectJsonDataButWithoutHeadersCheckResponseCodeIsUnsupportedMediaType() {

        given()
                .body(str)
                .log()
                .all()
                .when()
                .post("/test-orders")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);

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
            "15, 20, 60"
    })
    public void performanceTestOfGetAllOrdersEndpoint(int threadsAmount, int load, int timer)
            throws InterruptedException {

        assertEquals(200, GetAllOrdersPerformanceTest.test(threadsAmount, load, timer));

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 9, 10})
    public void updateStatusWithValidIdAndApiKeyCheckUpdatesInResponseBodyAndResponseCodeIsOk(int ID) {

        OrderDtoMocked orderDtoMocked = new OrderDtoMocked();
        orderDtoMocked.setStatus("ACCEPTED");
        orderDtoMocked.setCourierId(0);
        orderDtoMocked.setCustomerName(RandomDataGenerator.generateName());
        orderDtoMocked.setCustomerPhone(RandomDataGenerator.generatePhone());
        orderDtoMocked.setComment(RandomDataGenerator.generateComment());
        orderDtoMocked.setId(0);

        String status = given()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("api_key", RandomDataGenerator.generateValidApiKey())
                .body(new Gson().toJson(orderDtoMocked))
                .log()
                .all()
                .when()
                .put("/test-orders/{ID}", ID)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("status");

        assertEquals(orderDtoMocked.getStatus(), status);

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "3", "67594063", "123456789012345",
            "qwertyuiopasdfgh", "5w2r4y7i3p1s6f8h", "$&*()_+-=|:<>'`~", "123456789.098765",
            "98765432109876543", "09876543210987654321"})
    public void tryingToUpdateStatusWithInvalidApiKeyCheckThatResponseCodeIsUnauthorized(String invalidApiKey) {

        OrderDtoMocked orderDtoMocked = new OrderDtoMocked();
        orderDtoMocked.setStatus("ACCEPTED");
        orderDtoMocked.setCourierId(0);
        orderDtoMocked.setCustomerName(RandomDataGenerator.generateName());
        orderDtoMocked.setCustomerPhone(RandomDataGenerator.generatePhone());
        orderDtoMocked.setComment(RandomDataGenerator.generateComment());
        orderDtoMocked.setId(0);

        given()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("api_key", invalidApiKey)
                .body(new Gson().toJson(orderDtoMocked))
                .log()
                .all()
                .when()
                .put("/test-orders/" + RandomDataGenerator.generateValidId())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);

    }

    @Test
    public void tryingToUpdateStatusWithoutContentTypeHeaderCheckThatResponseCodeIsUnsupportedMediaType() {

        OrderDtoMocked orderDtoMocked = new OrderDtoMocked();
        orderDtoMocked.setStatus("ACCEPTED");
        orderDtoMocked.setCourierId(0);
        orderDtoMocked.setCustomerName(RandomDataGenerator.generateName());
        orderDtoMocked.setCustomerPhone(RandomDataGenerator.generatePhone());
        orderDtoMocked.setComment(RandomDataGenerator.generateComment());
        orderDtoMocked.setId(0);

        given()
                .header("Accept", "application/json")
                .header("api_key", RandomDataGenerator.generateValidApiKey())
                .body(new Gson().toJson(orderDtoMocked))
                .log()
                .all()
                .when()
                .put("/test-orders/" + RandomDataGenerator.generateValidApiKey())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0", "1", "2", "3", "qwerty"})
    public void tryingToUpdateStatusUsingInvalidDataInBodyCheckThatResponseCodeIsBadRequest(String str) {

        OrderDtoMocked orderDtoMocked = new OrderDtoMocked();
        orderDtoMocked.setStatus(str);
        orderDtoMocked.setCourierId(0);
        orderDtoMocked.setCustomerName(RandomDataGenerator.generateName());
        orderDtoMocked.setCustomerPhone(RandomDataGenerator.generatePhone());
        orderDtoMocked.setComment(RandomDataGenerator.generateComment());
        orderDtoMocked.setId(0);

        given()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("api_key", RandomDataGenerator.generateValidApiKey())
                .body(new Gson().toJson(orderDtoMocked))
                .log()
                .all()
                .when()
                .put("/test-orders/" + RandomDataGenerator.generateValidId())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11, 12, 20})
    public void tryingToUpdateStatusWithInvalidIdCheckThatResponseCodeIsBadRequest(int ID) {

        OrderDtoMocked orderDtoMocked = new OrderDtoMocked();
        orderDtoMocked.setStatus("ACCEPTED");
        orderDtoMocked.setCourierId(0);
        orderDtoMocked.setCustomerName(RandomDataGenerator.generateName());
        orderDtoMocked.setCustomerPhone(RandomDataGenerator.generatePhone());
        orderDtoMocked.setComment(RandomDataGenerator.generateComment());
        orderDtoMocked.setId(0);

        given()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("api_key", RandomDataGenerator.generateValidApiKey())
                .body(new Gson().toJson(orderDtoMocked))
                .log()
                .all()
                .when()
                .put("/test-orders/{ID}", ID)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.SC_NOT_FOUND);

    }
}