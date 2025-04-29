// //"C:\Users\Lenovo\OneDrive\Desktop\QuickEats_JavaApp"
// //javac -cp ".;sqlite-jdbc-3.49.1.0.jar" QuickEatsHome.java RestaurantsPage.java DishesPage.java CartPage.java ProfilePage.java
// //java -cp ".;sqlite-jdbc-3.49.1.0.jar" QuickEatsHome
// //java --enable-native-access=ALL-UNNAMED -cp ".;sqlite-jdbc-3.49.1.0.jar"  RestaurantsPage
//javac -cp ".;sqlite-jdbc-3.49.1.0.jar" *.java
//java -cp ".;sqlite-jdbc-3.49.1.0.jar" QuickEatsLogin

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class RestaurantsPage {

    public static void main(String[] args) {
        Frame mainFrame = new Frame("QuickEats - Restaurants");
        mainFrame.setSize(500, 800);
        mainFrame.setLayout(null);
        mainFrame.setBackground(Color.DARK_GRAY);

        // ── Scrollable Content Area ─────────────────────────────
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setBounds(50, 50, 400, 650);
        scrollPane.setBackground(Color.BLACK);
        mainFrame.add(scrollPane);

        Panel contentPanel = new Panel(null);
        contentPanel.setBackground(Color.BLACK);
        scrollPane.add(contentPanel);

        // ── Red Search Bar ───────────────────────────────────────
        TextField searchBar = new TextField();
        searchBar.setBounds(15, 10, 370, 30);
        searchBar.setBackground(Color.DARK_GRAY);
        searchBar.setForeground(Color.WHITE);
        contentPanel.add(searchBar);

        // Add search functionality
        searchBar.addTextListener(e -> {
            String searchText = searchBar.getText().toLowerCase();
            Component[] components = contentPanel.getComponents();
            
            // Skip the search bar itself (first component)
            for (int i = 1; i < components.length; i++) {
                Component comp = components[i];
                if (comp instanceof Panel) {
                    Panel card = (Panel) comp;
                    Component[] cardComponents = card.getComponents();
                    if (cardComponents.length > 0 && cardComponents[0] instanceof Label) {
                        Label nameLabel = (Label) cardComponents[0];
                        String restaurantName = nameLabel.getText().toLowerCase();
                        
                        // Use regex pattern matching
                        boolean matches = restaurantName.matches(".*" + searchText + ".*");
                        card.setVisible(matches);
                    }
                }
            }
        });

        // ── Load Restaurants from SQLite ─────────────────────────
        int yPos = 60;  // start below search bar

        try {
            Class.forName("org.sqlite.JDBC");
            File dbFile = new File("javaapp.db");
            System.out.println("Opening DB at: " + dbFile.getAbsolutePath());
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            Statement stmt = conn.createStatement();

            ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM restaurants;");
            if (countRs.next()) {
                System.out.println("Found " + countRs.getInt(1) + " restaurants in DB.");
            }
            countRs.close();

            ResultSet rs = stmt.executeQuery("SELECT id, name, offer, rating FROM restaurants;");
            while (rs.next()) {
                String name  = rs.getString("name");
                String offer = rs.getString("offer");
                float  rate  = rs.getFloat("rating");

                addRestaurantCard(contentPanel, name, offer, rate, yPos);
                yPos += 100;
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPanel.setSize(400, yPos);

        // ── Fixed Bottom Navbar ──────────────────────────────────
        Panel navBar = new Panel(null);
        navBar.setBounds(50, 660, 400, 50);
        navBar.setBackground(Color.DARK_GRAY); // Set to light gray

        String[] navItems = {"Home", "Restaurants", "Dishes", "Cart", "Profile"};
        int[] navX = {5, 85, 165, 245, 325};

        for (int i = 0; i < navItems.length; i++) {
            String label = navItems[i];
            Button btn = new Button(label);
            btn.setBounds(navX[i], 10, 70, 30);
            btn.setBackground(Color.green);
            btn.setForeground(Color.black);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setFocusable(false);

            btn.addActionListener(e -> {
                mainFrame.dispose();
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

        mainFrame.add(navBar);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                mainFrame.dispose();
            }
        });

        mainFrame.setVisible(true);
    }

    // ── Custom Panel for Restaurant Card ───────────────────────
    private static void addRestaurantCard(
            Panel parent, String name, String offer, float rating, int y) {

        Panel card = new Panel(null) {
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.GRAY);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g.setColor(Color.DARK_GRAY);
                g.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };

        card.setBounds(15, y, 370, 80);
        card.setBackground(Color.DARK_GRAY);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Add hand cursor

        Label nameLbl = new Label(name);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 16));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setBounds(10, 10, 250, 20);
        card.add(nameLbl);

        Label offerLbl = new Label(offer);
        offerLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        offerLbl.setForeground(Color.LIGHT_GRAY);
        offerLbl.setBounds(10, 35, 300, 20);
        card.add(offerLbl);

        Label ratingLbl = new Label("★ " + rating);
        ratingLbl.setFont(new Font("Arial", Font.BOLD, 14));
        ratingLbl.setForeground(Color.yellow);
        ratingLbl.setBounds(320, 10, 50, 20);
        card.add(ratingLbl);

        // Add click listener to the entire card
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Pass the restaurant name to DishesPage
                String[] args = {name};
                DishesPage.main(args);
                ((Frame)card.getParent().getParent().getParent().getParent()).dispose();
            }
        });

        parent.add(card);
    }
}

