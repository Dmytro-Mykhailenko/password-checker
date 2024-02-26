package api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;

public class RestApiMockedTest {

    private final int[] VALID_ID = {1, 2, 5, 9, 10};
    private final int[] INVALID_ID = {-1, 0, 11, 12, 20};
    private final String VALID_API_KEY = "1234567890123456";
    private final String[] INVALID_API_KEY = {"", "3", "67594063", "123456789012345",
            "qwertyuiopasdfgh", "5w2r4y7i3p1s6f8h", "$&*()_+-=|:<>'`~", "123456789.098765",
            "98765432109876543", "09876543210987654321"};

    @BeforeAll
    public static void setup(){
        
        baseURI="http://35.208.34.242";
        port=8080;

    }
    @BeforeEach void eachSetup(){
        System.out.println("\n+++++++++++++++++++++++++++");
    }

    @Test
    public void getAllOrderAndCheckResponseCodeIsOk(){

        System.out.println("\n===========================");
        given()
                .log()
                .all()
                .when()
                .get("/test-orders/get_orders")
                .then()
                .log()
                .all()
                .statusCode(200);

    }

    @Test
    public void getOrderByValidIdAndCheckResponseCodeIsOk(){

        for (int x: VALID_ID){
            System.out.println("\n===========================");
            given()
                    .log()
                    .all()
                    .when()
                    .get("/test-orders/" + x)
                    .then()
                    .log()
                    .all()
                    .statusCode(200);

        }
    }

    @Test
    public void getOrderByInvalidIdAndCheckResponseCodeIsBadRequest(){

        for (int x: INVALID_ID){
            System.out.println("\n===========================");
            given()
                .log()
                .all()
                .when()
                .get("/test-orders/" + x)
                .then()
                .log()
                .all()
                .statusCode(400);
        }
    }

    @Test
    public void deleteOrderIfValidIdAndApiKeyCheckResponseCodeIsOk(){

        for (int x: VALID_ID){
            System.out.println("\n===========================");
            given()
                    .log()
                    .all()
                    .when()
                    .header("api_key", VALID_API_KEY)
                    .delete("/test-orders/" + x)
                    .then()
                    .log()
                    .all()
                    .statusCode(204);
        }
    }

    @Test
    public void deleteOrderIfInvalidIdAndCheckResponseCodeIsBadRequest(){

        for (int x: INVALID_ID){
            System.out.println("\n===========================");
            given()
                .log()
                .all()
                .when()
                .header("api_key", VALID_API_KEY)
                .delete("/test-orders/" + x)
                .then()
                .log()
                .all()
                .statusCode(400);
        }
    }

    @Test
    public void deleteOrderIfInvalidApiKeyAndCheckResponseCodeIsUnauthorized(){

        for (String x: INVALID_API_KEY){
            System.out.println("\n===========================");
            given()
                    .log()
                    .all()
                    .when()
                    .header("api_key", x)
                    .delete("/test-orders/7")
                    .then()
                    .log()
                    .all()
                    .statusCode(401);

        }
    }

    @Test
    public void deleteOrderIfNoHeaderFieldAndCheckResponseCodeIsBadRequest(){

            System.out.println("\n===========================");
            given()
                    .log()
                    .all()
                    .when()
                    .delete("/test-orders/7")
                    .then()
                    .log()
                    .all()
                    .statusCode(400);


    }
}