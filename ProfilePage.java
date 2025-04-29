import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProfilePage {
    public static void main(String[] args) {
        Frame frame = new Frame("QuickEats - Profile");
        frame.setSize(500, 800);
        frame.setLayout(null);
        frame.setBackground(Color.DARK_GRAY);

       
        Button logoutBtn = new Button("Logout");
        logoutBtn.setBounds(400, 50, 70, 30);
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.addActionListener(e -> {
            Session.loggedInUsername = null;
            frame.dispose();
            QuickEatsLogin.main(null);
        });
        frame.add(logoutBtn);

        Label profileTitle = new Label("Your Profile", Label.CENTER);
        profileTitle.setBounds(50, 50, 400, 30);
        profileTitle.setFont(new Font("Arial", Font.BOLD, 20));
        profileTitle.setForeground(Color.RED);
        frame.add(profileTitle);

        TextField nameField = new TextField();
        nameField.setBounds(70, 100, 300, 25);
        frame.add(nameField);

        TextField phoneField = new TextField();
        phoneField.setBounds(70, 140, 300, 25);
        frame.add(phoneField);

        TextField emailField = new TextField();
        emailField.setBounds(70, 180, 300, 25);
        frame.add(emailField);

       
        String username = "default_user"; 
        
        try {
            try {
                Class sessionClass = Class.forName("Session");
                java.lang.reflect.Field field = sessionClass.getDeclaredField("loggedInUsername");
                username = (String) field.get(null);
            } catch (ClassNotFoundException | NoSuchFieldException e) {
                System.out.println("Session class not found or loggedInUsername not defined. Using default username.");
            }
            
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText("Name: " + rs.getString("name"));
                phoneField.setText("Phone: " + rs.getString("phone"));
                emailField.setText("Email: " + rs.getString("email"));
            } else {
                // Default values if user not found
                nameField.setText("Name: Demo User");
                phoneField.setText("Phone: 1234567890");
                emailField.setText("Email: demo@example.com");
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            nameField.setText("Name: Demo User");
            phoneField.setText("Phone: 1234567890");
            emailField.setText("Email: demo@example.com");
        }

        Button updateBtn = new Button("Update Profile");
        updateBtn.setBounds(150, 220, 150, 30);
        updateBtn.setBackground(Color.GREEN);
        frame.add(updateBtn);

        final String finalUsername = username;
        updateBtn.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");

                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET name = ?, phone = ?, email = ? WHERE username = ?"
                );
                ps.setString(1, nameField.getText().replace("Name: ", ""));
                ps.setString(2, phoneField.getText().replace("Phone: ", ""));
                ps.setString(3, emailField.getText().replace("Email: ", ""));
                ps.setString(4, finalUsername);
                ps.executeUpdate();
                ps.close();
                conn.close();

                System.out.println("Profile updated.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Label orderTitle = new Label("Previous Orders:");
        orderTitle.setBounds(70, 270, 300, 25);
        orderTitle.setForeground(Color.YELLOW);
        orderTitle.setFont(new Font("Arial", Font.BOLD, 15));
        frame.add(orderTitle);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setBounds(50, 300, 400, 300);
        scrollPane.setBackground(Color.BLACK);
        frame.add(scrollPane);

        Panel ordersPanel = new Panel(null);
        ordersPanel.setBackground(Color.BLACK);
        scrollPane.add(ordersPanel);

        int yPos = 10;

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM orders ORDER BY ordered_at DESC");

            while (rs.next()) {
                String item = rs.getString("dish_name") + " x" + rs.getInt("quantity") +
                              " from " + rs.getString("restaurant_name") +
                              " - ₹" + (rs.getFloat("price") * rs.getInt("quantity"));

                Label order = new Label(item);
                order.setBounds(10, yPos, 370, 25);
                order.setForeground(Color.WHITE);
                ordersPanel.add(order);
                yPos += 30;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ordersPanel.setSize(400, yPos);

        Panel navBar = new Panel(null);
        navBar.setBounds(50, 660, 400, 50);
        navBar.setBackground(Color.DARK_GRAY);

        String[] navItems = {"Home", "Restaurants", "Dishes", "Cart", "Profile"};
        int[] navX = {5, 85, 165, 245, 325};

        for (int i = 0; i < navItems.length; i++) {
            String label = navItems[i];
            Button btn = new Button(label);
            btn.setBounds(navX[i], 10, 70, 30);
            btn.setBackground(Color.green);
            btn.setForeground(Color.black);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            
            btn.addActionListener(e -> {
                frame.dispose();
                switch (label) {
                    case "Home": 
                        QuickEatsHome.main(null); 
                        break;
                    case "Restaurants": 
                        RestaurantsPage.main(null); 
                        break;
                    case "Dishes": 
                        String[] emptyArgs = {};
                        DishesPage.main(emptyArgs); 
                        break;
                    case "Cart": 
                        CartPage.main(null); 
                        break;
                    case "Profile": 
                        ProfilePage.main(null); 
                        break;
                }
            });
            
            navBar.add(btn);
        }

        frame.add(navBar);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }
}

