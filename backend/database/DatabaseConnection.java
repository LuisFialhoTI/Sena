package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import exception.DatabaseException;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static synchronized Connection getConnection() throws DatabaseException {
        if (connection == null || isClosed()) {
            try {
                Properties props = new Properties();
                String propPath = "backend/config/db.properties";
                if (!Files.exists(Paths.get(propPath))) {
                    throw new DatabaseException("Arquivo db.properties nao encontrado em: " + propPath);
                }
                try (InputStream input = Files.newInputStream(Paths.get(propPath))) {
                    props.load(input);
                } catch (IOException ex) {
                    throw new DatabaseException("Erro ao ler db.properties", ex);
                }

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String pass = props.getProperty("db.password");

                try {
                    Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException e) {
                    throw new DatabaseException("Driver do PostgreSQL nao encontrado no classpath", e);
                }

                connection = DriverManager.getConnection(url, user, pass);
                System.out.println("[DatabaseConnection] Conexao com Supabase estabelecida com sucesso!");
            } catch (SQLException e) {
                throw new DatabaseException("Erro ao conectar com o banco de dados Supabase. Verifique se o host, usuario e senha em db.properties estao corretos.", e);
            }
        }
        return connection;
    }

    private static boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DatabaseConnection] Conexao fechada.");
            } catch (SQLException e) {
                // Ignore
            } finally {
                connection = null;
            }
        }
    }
}
