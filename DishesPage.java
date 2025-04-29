import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DishesPage {
    public static void main(String[] args) {
        String selectedRestaurant = args.length > 0 ? args[0] : null;
        
        Frame frame = new Frame("QuickEats - Dishes" + (selectedRestaurant != null ? " - " + selectedRestaurant : ""));
        frame.setSize(500, 800);
        frame.setLayout(null);
        frame.setBackground(Color.DARK_GRAY);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setBounds(50, 50, 400, 600);
        scrollPane.setBackground(Color.BLACK);
        frame.add(scrollPane);

        Panel contentPanel = new Panel(null);
        contentPanel.setBackground(Color.BLACK);
        scrollPane.add(contentPanel);

        int yPos = 20;

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");

            Statement stmt = conn.createStatement();
            String query;
            if (selectedRestaurant != null) {
                query = "SELECT d.name AS dish, d.price, r.name AS restaurant " +
                       "FROM dishes d JOIN restaurants r ON d.restaurant_id = r.id " +
                       "WHERE r.name = '" + selectedRestaurant + "'";
            } else {
                query = "SELECT d.name AS dish, d.price, r.name AS restaurant " +
                       "FROM dishes d JOIN restaurants r ON d.restaurant_id = r.id";
            }

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String dish = rs.getString("dish");
                String rest = rs.getString("restaurant");
                float price = rs.getFloat("price");

                addDishCard(contentPanel, dish, rest, price, yPos);
                yPos += 110;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPanel.setPreferredSize(new Dimension(400, yPos));

   
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
                    case "Home": QuickEatsHome.main(null); break;
                    case "Restaurants": RestaurantsPage.main(null); break;
                    case "Dishes": DishesPage.main(null); break;
                    case "Cart": CartPage.main(null); break;
                    case "Profile": ProfilePage.main(null); break;
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

    private static void addDishCard(Panel panel, String dish, String rest, float price, int y) {
        Panel card = new Panel(null) {
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY); // Border color
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Outer gray border
                g2.setColor(new Color(50, 50, 50)); // Inner background
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 18, 18); // Slightly inset background
            }
        };
        card.setBounds(10, y, 380, 100);
        card.setBackground(new Color(50, 50, 50)); // Background matches inner paint
        card.setLayout(null);

        Label dishLbl = new Label(dish);
        dishLbl.setFont(new Font("Arial", Font.BOLD, 14));
        dishLbl.setForeground(Color.WHITE);
        dishLbl.setBounds(20, 10, 200, 20);
        card.add(dishLbl);

        Label restLbl = new Label("From: " + rest);
        restLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        restLbl.setForeground(Color.LIGHT_GRAY);
        restLbl.setBounds(20, 35, 200, 20);
        card.add(restLbl);

        Label priceLbl = new Label("₹" + price);
        priceLbl.setForeground(Color.GREEN);
        priceLbl.setBounds(20, 60, 100, 20);
        card.add(priceLbl);

        Button addBtn = new Button("+");
        addBtn.setBounds(320, 35, 30, 30);
        addBtn.setBackground(Color.black);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");

                PreparedStatement check = conn.prepareStatement(
                    "SELECT quantity FROM cart WHERE dish_name = ? AND restaurant_name = ?"
                );
                check.setString(1, dish);
                check.setString(2, rest);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    int currentQty = rs.getInt("quantity");
                    PreparedStatement update = conn.prepareStatement(
                        "UPDATE cart SET quantity = ? WHERE dish_name = ? AND restaurant_name = ?"
                    );
                    update.setInt(1, currentQty + 1);
                    update.setString(2, dish);
                    update.setString(3, rest);
                    update.executeUpdate();
                    update.close();
                } else {
                    PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO cart (dish_name, restaurant_name, price, quantity) VALUES (?, ?, ?, ?)"
                    );
                    insert.setString(1, dish);
                    insert.setString(2, rest);
                    insert.setFloat(3, price);
                    insert.setInt(4, 1);
                    insert.executeUpdate();
                    insert.close();
                }

                rs.close();
                check.close();
                conn.close();
                System.out.println(dish + " added to cart.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(card);
    }
}
