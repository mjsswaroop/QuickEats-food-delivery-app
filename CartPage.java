import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CartPage {
    public static void main(String[] args) {
        Frame frame = new Frame("QuickEats - Cart");
        frame.setSize(500, 800);
        frame.setLayout(null);
        frame.setBackground(Color.DARK_GRAY);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setBounds(50, 50, 400, 540);
        scrollPane.setBackground(Color.BLACK);
        frame.add(scrollPane);

        Panel contentPanel = new Panel(null);
        contentPanel.setBackground(Color.BLACK);
        scrollPane.add(contentPanel);

        int yPos = 20;
        float totalPrice = 0;

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, * FROM cart");  // Fetch the id column

            while (rs.next()) {
                int id = rs.getInt("id");  // Get the id from the result set
                String dish = rs.getString("dish_name");
                String rest = rs.getString("restaurant_name");
                float price = rs.getFloat("price");
                int qty = rs.getInt("quantity");
                float subtotal = price * qty;

                addCartCard(contentPanel, id, dish, rest, price, qty, subtotal, yPos, frame);
                yPos += 90;
                totalPrice += subtotal;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPanel.setSize(400, yPos);

        Label totalLbl = new Label("Total: ₹" + totalPrice);
        totalLbl.setFont(new Font("Arial", Font.BOLD, 16));
        totalLbl.setForeground(Color.WHITE);
        totalLbl.setBounds(50, 600, 200, 30);
        frame.add(totalLbl);
        Button orderBtn = new Button("Place Order");
        orderBtn.setBounds(300, 600, 100, 30);
        orderBtn.setBackground(Color.GREEN);
        orderBtn.setForeground(Color.BLACK);
        orderBtn.setFont(new Font("Arial", Font.BOLD, 12));
        frame.add(orderBtn);

        orderBtn.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");

                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO orders (dish_name, restaurant_name, price, quantity) " +
                    "SELECT dish_name, restaurant_name, price, quantity FROM cart"
                );
                insert.executeUpdate();
                insert.close();

                Statement stmt = conn.createStatement();
                stmt.executeUpdate("DELETE FROM cart");
                stmt.close();
                conn.close();

                Dialog confirm = new Dialog(frame, "Order Placed!", true);
                confirm.setSize(300, 100);
                confirm.setLayout(new BorderLayout());
                confirm.add(new Label("Your order has been placed successfully!", Label.CENTER), BorderLayout.CENTER);
                Button ok = new Button("OK");
                ok.addActionListener(ae -> {
                    confirm.dispose();
                    frame.dispose();
                    QuickEatsHome.main(null); // Go back to home after order
                });
                Panel okPanel = new Panel();
                okPanel.add(ok);
                confirm.add(okPanel, BorderLayout.SOUTH);
                confirm.setLocationRelativeTo(frame);
                confirm.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

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

    private static void addCartCard(Panel panel, int id, String dish, String rest, float price, int qty, float subtotal, int y, Frame frame) {
        Panel card = new Panel(null);
        card.setBounds(15, y, 370, 80);
        card.setBackground(Color.DARK_GRAY);

        Label nameLbl = new Label(dish);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setBounds(10, 10, 200, 20);
        card.add(nameLbl);

        Label restLbl = new Label("From: " + rest);
        restLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        restLbl.setForeground(Color.LIGHT_GRAY);
        restLbl.setBounds(10, 30, 200, 20);
        card.add(restLbl);

        Label qtyLbl = new Label("Qty: " + qty);
        qtyLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        qtyLbl.setForeground(Color.CYAN);
        qtyLbl.setBounds(10, 50, 80, 20);
        card.add(qtyLbl);

        Label subLbl = new Label("Subtotal: ₹" + subtotal);
        subLbl.setFont(new Font("Arial", Font.BOLD, 12));
        subLbl.setForeground(Color.YELLOW);
        subLbl.setBounds(100, 50, 150, 20);
        card.add(subLbl);

        Button removeBtn = new Button("Remove");
        removeBtn.setBounds(270, 25, 80, 30);
        removeBtn.setBackground(Color.RED);
        removeBtn.setForeground(Color.WHITE);
        card.add(removeBtn);

        removeBtn.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");
                PreparedStatement ps = conn.prepareStatement("DELETE FROM cart WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
                conn.close();

                frame.dispose();
                main(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(card);
    }
}
