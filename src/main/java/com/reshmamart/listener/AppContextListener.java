package com.reshmamart.listener;

import com.reshmamart.dao.*;
import com.reshmamart.service.*;
import com.reshmamart.util.DBConnectionPool;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        System.out.println(">>> [reshmamart] Initializing Application Context...");

        try {
            // 1. Load config.properties
            Properties props = new Properties();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (in != null) {
                    props.load(in);
                } else {
                    System.err.println(">>> [reshmamart] config.properties not found on classpath, using defaults.");
                }
            }

            // 2. Initialize DB Connection Pool
            DBConnectionPool.init(props);
            System.out.println(">>> [reshmamart] HikariCP Connection Pool Initialized.");

            // 3. Initialize Schema & Seed Data
            initDatabaseSchemaAndSeed();

            // 4. Initialize DAOs
            UserDAO userDAO = new UserDAOImpl();
            ProductDAO productDAO = new ProductDAOImpl();
            CartDAO cartDAO = new CartDAOImpl();
            OrderDAO orderDAO = new OrderDAOImpl();
            ReviewDAO reviewDAO = new ReviewDAOImpl();

            // 5. Initialize Services
            UserService userService = new UserServiceImpl(userDAO);
            ProductService productService = new ProductServiceImpl(productDAO);
            CartService cartService = new CartServiceImpl(cartDAO, productDAO);
            OrderService orderService = new OrderServiceImpl(orderDAO, cartDAO, productDAO);
            ReviewService reviewService = new ReviewServiceImpl(reviewDAO, orderDAO, productDAO);

            // 6. Register Services in ServletContext
            context.setAttribute("userService", userService);
            context.setAttribute("productService", productService);
            context.setAttribute("cartService", cartService);
            context.setAttribute("orderService", orderService);
            context.setAttribute("reviewService", reviewService);

            System.out.println(">>> [reshmamart] Application Context Initialized Successfully.");
        } catch (Exception e) {
            System.err.println(">>> [reshmamart] Fatal error during context initialization: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize reshmamart", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> [reshmamart] Destroying Application Context...");
        DBConnectionPool.shutdown();
        System.out.println(">>> [reshmamart] HikariCP Pool Shut Down Successfully.");
    }

    private void initDatabaseSchemaAndSeed() {
        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            // Execute schema.sql
            String schemaSql = loadResourceFile("schema.sql");
            if (schemaSql != null && !schemaSql.isEmpty()) {
                for (String sqlStatement : schemaSql.split(";")) {
                    String trimmed = sqlStatement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
                System.out.println(">>> [reshmamart] Database schema executed successfully.");
            }

            // Check if seed data is needed (if users table has 0 rows)
            boolean needSeed = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    needSeed = true;
                }
            }

            if (needSeed) {
                System.out.println(">>> [reshmamart] Populating database with seed data...");
                String seedSql = loadResourceFile("seed.sql");
                if (seedSql != null && !seedSql.isEmpty()) {
                    for (String sqlStatement : seedSql.split(";")) {
                        String trimmed = sqlStatement.trim();
                        if (!trimmed.isEmpty()) {
                            stmt.execute(trimmed);
                        }
                    }
                    System.out.println(">>> [reshmamart] Seed data populated successfully.");
                }
            } else {
                System.out.println(">>> [reshmamart] Database already contains data. Skipping seed.");
            }

        } catch (Exception e) {
            System.err.println(">>> [reshmamart] Error initializing schema/seed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String loadResourceFile(String fileName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) return null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            System.err.println(">>> [reshmamart] Could not load resource file " + fileName + ": " + e.getMessage());
            return null;
        }
    }
}
