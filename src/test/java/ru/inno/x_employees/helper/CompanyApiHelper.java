package ru.inno.x_employees.helper;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.specification.ProxySpecification;
import ru.inno.x_employees.model.AuthRequest;
import ru.inno.x_employees.model.AuthResponse;
import ru.inno.x_employees.model.CreateCompanyRequest;
import ru.inno.x_employees.model.CreateCompanyResponse;

import static io.restassured.RestAssured.given;


public class CompanyApiHelper {

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

    public CreateCompanyResponse createCompany() {
        Faker faker=new Faker();
        AuthResponse info = auth("leyla", "water-fairy");

        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest(faker.company().name(), faker.lorem().sentence());

        return given()
                .basePath("company")
                .body(createCompanyRequest)
                .header("x-client-token", info.userToken())
                .contentType(ContentType.JSON)
                .when()
                .post().body().as(CreateCompanyResponse.class);
    }
}
