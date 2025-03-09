package me.dio.persistence.migration;


import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static me.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;

    public MigrationStrategy(Connection connection) {
        this.connection = connection;
    }


    public void executeMigration(){
        var originalOut = System.out;
        var originalErr = System.err;
        try {
            try(var fos = new FileOutputStream("liquibase.log")){
                System.setOut(new PrintStream(fos));
                System.setErr(new PrintStream(fos));
                try {
                    var jdbcConnection = new JdbcConnection(connection);
                    var liquibase = new Liquibase(
                            "db/changelog/db.changelog-master.yml",
                            new ClassLoaderResourceAccessor(), jdbcConnection);
                    liquibase.update();

//                        var connection = getConnection();
//                        var jdbcConnection = new JdbcConnection(connection);
//                ){
//
                } catch (DatabaseException e){
                    e.printStackTrace();
                    System.setErr(originalErr);
                } catch (LiquibaseException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
