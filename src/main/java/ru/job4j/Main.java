package ru.job4j;

import org.apache.log4j.Logger;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.service.Web;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        try (var connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password"));
             var scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);
            new Web(store).start(Integer.parseInt(config.get("server.port")));
            System.out.println("App is running. Press Enter to stop...");
            System.in.read();
        } catch (SQLException | IOException e) {
            LOG.error("When create a connection", e);
        }
    }
}