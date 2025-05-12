package rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.rest.ApplicationConfig;
import dk.cphbusiness.rest.RestRoutes;
import dk.cphbusiness.security.SecurityRoutes;
import dk.cphbusiness.utils.IIdProvider;
import dk.cphbusiness.utils.Populator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;

//@Disabled
public class SecurityTest {

    private static ApplicationConfig appConfig;
    private static EntityManagerFactory emf;
    private static ObjectMapper jsonMapper = new ObjectMapper();

    private Map<String, IIdProvider<String>> populatedUsers;
    private Map<String, IIdProvider<Long>> populatedTrips;


//    @BeforeAll
//    static void setUpAll() {
//        RestAssured.baseURI = "http://localhost:7777/api/";
//
//        HibernateConfig.setTestMode(true); // IMPORTANT leave this at the very top of this method in order to use the test database
//        RestRoutes restRoutes = new RestRoutes();
//
//        // Setup test database using docker testcontainers
//        emf = HibernateConfig.getEntityManagerFactory();
//
//        // Start server
//        appConfig = ApplicationConfig
//                .getInstance()
//                .initiateServer()
//                .checkSecurityRoles() // check for role when route is called
//                .setRoute(SecurityRoutes.getSecurityRoutes())
//                .setRoute(SecurityRoutes.getSecuredRoutes())
//                .startServer(7777)
//                .setCORS()
//                .setGeneralExceptionHandling()
//                .setApiExceptionHandling();
////                getInstance()
////                .initiateServer()
////                .checkSecurityRoles()
////                .setErrorHandling()
////                .setGeneralExceptionHandling()
//////                .setRoute(restRoutes.getOpenRoutes())
////                .setRoute(SecurityRoutes.getSecurityRoutes())
////                .setRoute(SecurityRoutes.getSecuredRoutes())
//////                .setRoute(restRoutes.personEntityRoutes) // A different way to get the EndpointGroup. Getting data from DB
////                .setCORS()
////                .setApiExceptionHandling()
////                .startServer(7777)
//        ;
//    }
//
//    @AfterAll
//    static void afterAll() {
//        HibernateConfig.setTestMode(false);
//        appConfig.stopServer();
//        HibernateConfig.stopDBServer(); // close emf and set to null to avoid memory leaks and make ready for next test suite
//    }
//
//    @BeforeEach
//    void setUpEach() {
//        // Setup test database for each test
//        populatedUsers = new Populator().createUsersAndRoles(emf);
//        // Setup DB Poems
//        securityToken = null;
//    }
//
//    @Test
//    @DisplayName("Test if server is up")
//    public void testServerIsUp() {
//        System.out.println("Testing is server UP");
//        given().when().get("/auth/test").then().statusCode(200);
//    }
//
//    private static String securityToken;
//
//    private static void login(String username, String password) {
//        ObjectNode objectNode = jsonMapper.createObjectNode()
//                .put("username", username)
//                .put("password", password);
//        String loginInput = objectNode.toString();
//        securityToken = given()
//                .header("Origin", "http://localhost:5173")
//                .contentType("application/json")
//                .body(loginInput)
//                .when().post("/auth/login")
//                .then()
//                .log().all()
//                .extract().path("token");
//
//
//    }
//
//    @Test
//    @DisplayName("Test login ")
//    public void testLogin() {
//        login("admin", "admin123");
//        assertThat("Security token is not null", securityToken, notNullValue());
//        // get the time from the token
//        securityToken = securityToken.split("\\.")[1];
//        securityToken = new String(Base64.getDecoder().decode(securityToken));
//        JsonPath jsonPath = new JsonPath(securityToken);
//        System.out.println("Token content: " + jsonPath.prettyPrint());
//        System.out.println("Expire time: " + jsonPath.getString("exp"));
//        // test that expire time is more than 25 minutes in the future
//        long expireTimeStamp = jsonPath.getLong("exp");
//        long currentTimeStamp = System.currentTimeMillis() / 1000;
//        LocalDateTime expireTime = LocalDateTime.ofEpochSecond(expireTimeStamp, 0, ZoneOffset.UTC);
//        LocalDateTime now = LocalDateTime.ofEpochSecond(currentTimeStamp, 0, ZoneOffset.UTC);
//        System.out.println("Expire time: " + expireTime + " Current time: " + now + " Diff: " + (expireTimeStamp - currentTimeStamp) + " seconds");
//        assertThat("Token is valid for about 5 minutes", expireTimeStamp, greaterThan(currentTimeStamp+290));
//    }
//
//    @Test
//    @DisplayName("Test login for user and access protected endpoint")
//    public void testRestForUser() {
//        login("user", "user123");
//        given()
//                .contentType("application/json")
//                .accept("application/json")
//                .header("Authorization", "Bearer "+securityToken)
//                .header("Origin", "http://localhost:5173")
//                .when()
//                .get("/protected/user_demo").then()
//                .log().all()
//                .statusCode(200)
//                .body("msg", equalTo("Hello from USER Protected"));
//    }
//
//    @Test
//    @DisplayName("Test access ADMIN protected endpoint with USER role failing")
//    public void testRestForUserProtection() {
//        login("user", "user123");
//        given()
//                .contentType("application/json")
//                .accept("application/json")
//                .header("Authorization", "Bearer "+securityToken)
//                .header("Origin", "http://localhost:5173")
//                .when()
//                .get("/protected/admin_demo").then()
//                .log().all()
//                .statusCode(403)
//                .body("title", equalTo("User was not authorized with roles: [user]. Needed roles are: [ADMIN]"));
//    }
//
//    @Test
//    @DisplayName("Test CORS Headers")
//    public void testCorsHeaders() {
//        given()
//                .header("Origin", "http://localhost:5173")
//                .when()
//                .get("/auth/test")
//                .then()
//                .log().all()
//                .header("Access-Control-Allow-Origin", "http://localhost:5173")
//                .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS")
//                .header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Username")
//                .statusCode(200);
//    }
//
//    @Test
//    @DisplayName("Test CORS Preflight against a protected route")
//    public void testCorsPreflight() {
//        given()
//                .header("Origin", "http://localhost:5173")
//                .when()
//                .header("Access-Control-Request-Method", "POST")
////                .header("Origin", "http://localhost:7777")
//                .options("/protected/admin_demo")
//                .then()
//                .header("Access-Control-Allow-Origin", "http://localhost:5173")
//                .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS")
//                .header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Username")
//                .statusCode(200);
//    }
//
//    @Test
//    @DisplayName("Test verify endpoint")
//    public void testVerifyEndpoint() {
//        login("user", "user123");
//        given()
//                .header("Origin", "http://localhost:5173")
//                .contentType("application/json")
//                .accept("application/json")
//                .header("Authorization", "Bearer "+securityToken)
//                .when()
//                .get("/auth/verify").then()
//                .log().all()
//                .statusCode(200)
//                .body("msg", equalTo("Token is valid"));
//    }
//
//    @Test
//    @DisplayName("Test time to live")
//    public void testTimeToLive() {
//        login("user", "user123");
//        given()
//                .header("Origin", "http://localhost:5173")
//                .contentType("application/json")
//                .accept("application/json")
//                .header("Authorization", "Bearer "+securityToken)
//                .when()
//                .get("/auth/tokenlifespan").then()
//                .log().all()
//                .statusCode(200)
//                .body("secondsToLive", is(both(greaterThan(290)).and(lessThanOrEqualTo(300))));
//    }
//
//
//
//
}
