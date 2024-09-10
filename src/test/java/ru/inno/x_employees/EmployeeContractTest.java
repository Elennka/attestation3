package ru.inno.x_employees;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.inno.x_employees.ext.DatabaseService;
import ru.inno.x_employees.ext.EnvProperties;
import ru.inno.x_employees.helper.CompanyApiHelper;
import ru.inno.x_employees.helper.EmployeeApiHelper;
import ru.inno.x_employees.model.*;


import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static ru.inno.x_employees.helper.EmployeeService.generateEmployee;

public class EmployeeContractTest {

    static EmployeeApiHelper employeeHelper;
    static CompanyApiHelper companyHelper;
    static DatabaseService databaseService;
    static int companyId;
    static int employeeId;


    @BeforeAll
    public static void setUp() throws SQLException, IOException {
        RestAssured.baseURI = EnvProperties.getEnvProperties("url");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        databaseService = new DatabaseService();
        databaseService.connectToDb();
        companyId = databaseService.createNewCompany();
        employeeId=databaseService.createNewEmployee(companyId);

        companyHelper = new CompanyApiHelper();
        employeeHelper = new EmployeeApiHelper();
    }


    @AfterAll
    public static void tearDown() throws SQLException {
        databaseService.deleteCompanyAndItsEmloyees(companyId);
        databaseService.closeConnection();
    }

    @Test
    @Description("Получает список работников по существующему id компании")
    public void status200OnGetEmployeesByCompany() throws SQLException {
        int id = databaseService.getAnyCompanyID();

        given()
                .basePath("employee")
                .queryParam("company", id)
                .when()
                .get()
                .then()
                .statusCode(200)
                .header("Content-Type", "application/json; charset=utf-8");
    }

    @Test
    @Description("Ожидаем пустое тело по НЕ существующему id компании")
    public void status200AndEmptyBodyOnGetEmployeesByCompany() throws SQLException {
        int id = databaseService.getLastCompanyID();
        given()
                .basePath("employee")
                .queryParam("company", id + 1)
                .when()
                .get()
                .then()
                .statusCode(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .body(equalTo("[]"));
    }

    @Test
    @Description("Ожидается статус 200, при получение сотрудника по его id")
    public void status200OnGettingEmployeerById() throws SQLException {

        int id = databaseService.getAnyEmployeeId();
        given()
                .basePath("employee")
                .when()
                .get("{Id}", id)
                .then()
                .statusCode(200)
                .header("Content-Type", "application/json; charset=utf-8");

    }

    @Test
    @Description("Ожидается статус 200 и Content-length=0, при получение сотрудника по несуществующему id")
    public void status200OnGettingEmployeerByInvalidId() throws SQLException {

        int id = databaseService.getLastEmployeeID();
        given()
                .basePath("employee")
                .when()
                .get("{Id}", id + 1)
                .then()
                .statusCode(200)
                .header("Content-length", equalTo("0"));
    }

    @Test
    @Description("Проверяем, что не  можем создать сотрудника без токеном, status-401")
    public void iCannotAddNewEmployee() {
        Faker faker = new Faker();
        Employee createEmployeeRequest = generateEmployee(faker, companyId);
        given()
                .basePath("employee")
                .body(createEmployeeRequest)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    @Description("Проверяем что можем создать сотрудника c токеном")
    public void iCanAddNewEmployee() throws IOException {

        AuthResponse info = employeeHelper.auth(EnvProperties.getEnvProperties("app_user.login"), EnvProperties.getEnvProperties("app_user.pass"));
        Faker faker = new Faker();
        Employee createEmployeeRequest = generateEmployee(faker, companyId);
        given()
                .basePath("employee")
                .body(createEmployeeRequest)
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    @Description("Проверяем, что можем изменить информацию о сотруднике")
    public void iCanEditEmployee() throws SQLException, IOException {
        AuthResponse info = employeeHelper.auth(EnvProperties.getEnvProperties("app_user.login"), EnvProperties.getEnvProperties("app_user.pass"));
        Faker faker = new Faker();
        PatchEmployeeRequest patchEmployeeRequest = new PatchEmployeeRequest
                (faker.name().lastName(),
                        faker.internet().emailAddress(),
                        faker.internet().url(),
                        faker.phoneNumber().cellPhone(),
                        faker.bool().bool());
        given()
                .basePath("employee")
                .body(patchEmployeeRequest)
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .patch("{id}",employeeId)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @Description("Проверяем, что при отправке запросы на изменение несуществующего сотрудника получаем 500")
    public void iCannotEditEmployee() throws SQLException, IOException {
        AuthResponse info = employeeHelper.auth(EnvProperties.getEnvProperties("app_user.login"), EnvProperties.getEnvProperties("app_user.pass"));
        Faker faker = new Faker();
        PatchEmployeeRequest patchEmployeeRequest = new PatchEmployeeRequest
                (faker.name().lastName(),
                        faker.internet().emailAddress(),
                        faker.internet().url(),
                        faker.phoneNumber().phoneNumber(),
                        faker.bool().bool());


        given()
                .basePath("employee")
                .body(patchEmployeeRequest)
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .patch("{id}",employeeId+1000)
                .then()
                .assertThat()
                .statusCode(500);
    }
}
