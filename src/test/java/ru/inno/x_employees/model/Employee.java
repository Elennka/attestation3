package ru.inno.x_employees.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Employee(int id,
                       String firstName,
                       String lastName,
                       String middleName,
                       int companyId,
                       String email,
                       @JsonProperty("url")
                       @JsonAlias({"url", "avatar_url"}) String avatar_url,
                       String phone,
                       String birthdate,
                       boolean isActive,
                       @JsonIgnore String createDateTime,
                       @JsonIgnore String lastChangedDateTime
                       ){


}
