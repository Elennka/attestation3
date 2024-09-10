package ru.inno.x_employees.helper;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import ru.inno.x_employees.ext.EnvProperties;
import ru.inno.x_employees.model.*;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;


public class EmployeeApiHelper {
    public AuthResponse auth(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username, password);

        return given()
                .basePath("/auth/login")
                .body(authRequest)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .as(AuthResponse.class);
    }

    public CreateEmployeeResponse createEmployee(Employee employee) {
        AuthResponse info = auth("leyla", "water-fairy");
        return given()
                .basePath("employee")
                .body(employee)
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .post().body().as(CreateEmployeeResponse.class);
    }

    public Employee getEmployeeInfo(int employeeId) throws IOException {

        AuthResponse info = auth(EnvProperties.getEnvProperties("app_user.login"), EnvProperties.getEnvProperties("app_user.pass"));

        return given()
                .basePath("employee")
                .when()
                .get("{Id}", employeeId).body().as(Employee.class);

    }

    public List<Employee> getListOfEmployee(int companyId) throws IOException {
        AuthResponse info = auth(EnvProperties.getEnvProperties("app_user.login"), EnvProperties.getEnvProperties("app_user.pass"));

        return given()
                .basePath("employee")
                .queryParam("company", companyId)
                .when()
                .get().body().as(new TypeRef<>() {
                });

    }

    public Employee editEmployee(int employeeId, PatchEmployeeRequest patchEmployeeRequest) throws IOException {
        AuthResponse info = auth(EnvProperties.getEnvProperties("app_user.login"), EnvProperties.getEnvProperties("app_user.pass"));

        return given()
                .basePath("employee")
                .body(patchEmployeeRequest)
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .patch("{id}",employeeId).body().as(Employee.class);
    }

}
