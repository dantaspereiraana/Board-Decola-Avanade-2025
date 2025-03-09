package me.dio;

import me.dio.persistence.migration.MigrationStrategy;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

import	static me.dio.persistence.config.ConnectionConfig.getConnection;


@SpringBootApplication
public class Application {

	public static void main(String[] args) throws SQLException {
		try (var connection = getConnection()){
			new MigrationStrategy(connection).executeMigration();
		}

	}

}
