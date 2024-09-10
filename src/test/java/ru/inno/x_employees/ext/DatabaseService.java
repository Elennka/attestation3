package ru.inno.x_employees.ext;

import java.io.IOException;
import java.sql.*;

public class DatabaseService {

    private Connection connection;

    static final String SQL_GET_ANY_COMPANY_ID = "select c.id, count(c.id) from company c left join employee e on c.id = e.company_id group by c.id having count(c.id) > 0 limit 1;";
    static final String SQL_GET_LAST_COMPANY_ID = "SELECT c.id from company c order by c.id desc limit 1;";
    static final String SQL_GET_LAST_EMPLOYEE_ID = "select e.id from employee e order by e.id desc limit 1;";
    static final String SQL_ADD_NEW_COMPANY = "insert into company (name,description)values ('VasilevaTest', 'Vasileva decription');";
    static final String SQL_GET_SPECIAL_COMPANY_ID = "select id from company where description ='Vasileva decription' order by id desc limit 1;";
    static final String SQL_DELETE_EMPLOYEES_OF_SPECIAL_COMPANY = "delete from employee e where e.company_id in (select c.id from company c where c.description ='Vasileva decription');";
    static final String SQL_DELETE_SPECIAL_COMPANY = "delete from company c where description ='Vasileva decription'";
    static final String SQL_GET_EMPLOYEE_INFO = "select e.id, e.first_name,e.last_name,e.middle_name,e.company_id,e.email, e.avatar_url, e.phone,e.birthdate,e.is_active from employee e where e.id  =?;";
    static final String SQL_GET_ANY_EMPLOYEE_ID = "select e.id from employee e limit  1;";
    static final String SQL_ADD_NEW_EMPLOYEE="insert into employee (is_active, last_name, first_name, phone, company_id) values (true, 'VasilevaTest', 'Test', '79519778018', ?);";
    static final String SQL_GET_SPECIAL_EMPLOYEE="select e.id from employee e where e.last_name ='VasilevaTest' order by e.id desc limit 1;";

    public void connectToDb() throws SQLException, IOException {

        connection = DriverManager.getConnection(EnvProperties.getEnvProperties("dbConnectionString"),
                EnvProperties.getEnvProperties("dbUsername"),
                EnvProperties.getEnvProperties("dbPassword"));
    }

    public int getAnyCompanyID() throws SQLException {

        String sqlQuery = SQL_GET_ANY_COMPANY_ID;


        ResultSet resultSet = connection.createStatement().executeQuery(sqlQuery);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int getLastCompanyID() throws SQLException {

        String sqlQuery = SQL_GET_LAST_COMPANY_ID;
        ResultSet resultSet = connection.createStatement().executeQuery(sqlQuery);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int getLastEmployeeID() throws SQLException {

        String sqlQuery = SQL_GET_LAST_EMPLOYEE_ID;
        ResultSet resultSet = connection.createStatement().executeQuery(sqlQuery);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int createNewCompany() throws SQLException {
        String sqlQuery = SQL_ADD_NEW_COMPANY;
        connection.createStatement().executeUpdate(sqlQuery);
        sqlQuery = SQL_GET_SPECIAL_COMPANY_ID;
        ResultSet resultSet = connection.createStatement().executeQuery(sqlQuery);
        resultSet.next();
        resultSet.getInt(1);
        return resultSet.getInt(1);
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public void deleteCompanyAndItsEmloyees(int companyId) throws SQLException {
        String sqlQuery = SQL_DELETE_EMPLOYEES_OF_SPECIAL_COMPANY;
        connection.createStatement().executeUpdate(sqlQuery);
        sqlQuery = SQL_DELETE_SPECIAL_COMPANY;
        connection.createStatement().executeUpdate(sqlQuery);
    }


    public int createNewEmployee(int companyId) throws SQLException {

        String sqlQuery = SQL_ADD_NEW_EMPLOYEE;
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        statement.setInt(1, companyId);
        statement.executeUpdate();
        sqlQuery = SQL_GET_SPECIAL_EMPLOYEE;
        ResultSet resultSet = connection.createStatement().executeQuery(sqlQuery);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public ResultSet getEmployeeInfo(int id) throws SQLException {
        String sqlQuery = SQL_GET_EMPLOYEE_INFO;
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }


    public int getAnyEmployeeId() throws SQLException {
        String sqlQuery = SQL_GET_ANY_EMPLOYEE_ID;
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }
}
