package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);
        String url = "jdbc:postgresql://ptpostgresql.postgres.database.azure.com:5432/PandaSQL";
        String user = "Panda";
        String password = "Tech1234";
        try{
            Connection connection = DriverManager.getConnection(url, user, password);
            
            Object stmt = connection.createStatement();
            ResultSet rs = stmt.ExecuteQuery("select * from weather");
            while (rs.next() ){
                int idClima = rs.getInt("idclima");
                String Clima = rs.getString("clima");
                String Pais = rs.getString("pais");
                String Ciudad = rs.getString("ciudad");
                String Nombre = rs.getString("nombre");
                String Cedula = rs.getString("cedula");
                int Registro = rs.getInt("registro");
                System.out.printf( "idclima = %s , clima = %s, pais = %s, ciudad = %s, nombre = %s, cedula = %s, registro = %s ", Clima, Pais, Ciudad, Nombre, Cedula, Registro);

                System.out.println();
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Ocurrio un error:" +e.getMessage());
        }
        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
}
