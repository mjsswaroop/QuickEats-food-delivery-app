import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class QuickEatsHome {

    public static void main(String[] args) {
        Frame mainFrame = new Frame("QuickEats - Home");
        mainFrame.setSize(500, 800);
        mainFrame.setLayout(null);
        mainFrame.setBackground(Color.darkGray);

     
        Panel contentPanel = new Panel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(50, 50, 400, 650);
        contentPanel.setBackground(Color.black);
        mainFrame.add(contentPanel);

       
        TextField searchBar = new TextField();
        searchBar.setBounds(15, 20, 370, 30);
        searchBar.setBackground(Color.black);
        searchBar.setForeground(Color.white);
        searchBar.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(searchBar);

       
        searchBar.addTextListener(e -> {
            String searchText = searchBar.getText().toLowerCase();
            Component[] components = contentPanel.getComponents();
            
          
            for (int i = 0; i < components.length; i++) {
                Component comp = components[i];
                if (comp instanceof Panel) {
                    Panel card = (Panel) comp;
                    Component[] cardComponents = card.getComponents();
                    if (cardComponents.length > 0 && cardComponents[0] instanceof Label) {
                        Label nameLabel = (Label) cardComponents[0];
                        String restaurantName = nameLabel.getText().toLowerCase();
                        
                       
                        boolean matches = restaurantName.matches(".*" + searchText + ".*");
                        card.setVisible(matches);
                    }
                }
            }
        });

        
        Label popularLabel = new Label("🔥 Popular Choices");
        popularLabel.setFont(new Font("Arial", Font.BOLD, 16));
        popularLabel.setForeground(Color.green);
        popularLabel.setBounds(15, 80, 200, 30);
        contentPanel.add(popularLabel);

        int yPos = 120; 
       
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");
            Statement stmt = conn.createStatement();
            
            
            ResultSet popularRs = stmt.executeQuery(
                "SELECT name, offer, rating FROM restaurants ORDER BY rating DESC LIMIT 2"
            );
            
            while (popularRs.next()) {
                String name = popularRs.getString("name");
                String offer = popularRs.getString("offer");
                float rating = popularRs.getFloat("rating");
                addRestaurant(contentPanel, name, offer, String.valueOf(rating), yPos);
                yPos += 90;
            }
            popularRs.close();

           
            Label budgetLabel = new Label("💰 Budget Meals");
            budgetLabel.setFont(new Font("Arial", Font.BOLD, 16));
            budgetLabel.setForeground(Color.green);
            budgetLabel.setBounds(15, yPos + 20, 200, 30);
            contentPanel.add(budgetLabel);

           
            ResultSet budgetRs = stmt.executeQuery(
                "SELECT r.name, r.offer, r.rating " +
                "FROM restaurants r " +
                "JOIN dishes d ON r.id = d.restaurant_id " +
                "GROUP BY r.id " +
                "ORDER BY MIN(d.price) ASC LIMIT 2"
            );

            yPos += 60;
            while (budgetRs.next()) {
                String name = budgetRs.getString("name");
                String offer = budgetRs.getString("offer");
                float rating = budgetRs.getFloat("rating");
                addRestaurant(contentPanel, name, offer, String.valueOf(rating), yPos);
                yPos += 90;
            }
            budgetRs.close();

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

       
        Button cartButton = new Button("View Cart");
        cartButton.setBounds(120, yPos + 20, 150, 40);
        cartButton.setBackground(Color.green);
        cartButton.setForeground(Color.black);
        cartButton.setFont(new Font("Arial", Font.BOLD, 12));
        cartButton.setPreferredSize(new Dimension(150, 40));
        cartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
       
        cartButton.addActionListener(e -> {
            mainFrame.dispose();
            CartPage.main(null);
        });
        
        contentPanel.add(cartButton);

        
        Panel navBar = new Panel();
        navBar.setLayout(null);
        navBar.setBounds(0, 600, 400, 50);
        navBar.setBackground(Color.darkGray);

        String[] navItems = {"Home", "Restaurants", "Dishes", "Cart", "Profile"};
        int[] navX = {5, 85, 165, 245, 325};

        for (int i = 0; i < navItems.length; i++) {
            String label = navItems[i];
            Button navBtn = new Button(label);
            navBtn.setBounds(navX[i], 10, 70, 30);
            navBtn.setBackground(Color.green);
            navBtn.setForeground(Color.black);
            navBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            navBtn.setFocusable(false);

            navBtn.addActionListener(e -> {
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
            
            navBar.add(navBtn);
        }

        contentPanel.add(navBar);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mainFrame.dispose();
            }
        });

        mainFrame.setVisible(true);
    }

    
    private static void addRestaurant(Panel panel, String name, String offer, String rating, int yPos) {
        Panel restPanel = new Panel() {
           
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.gray);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);  // Shadow effect
                g.setColor(Color.darkGray);
                g.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20); // Rounded corners
            }
        };

        restPanel.setLayout(null);
        restPanel.setBounds(15, yPos, 360, 80);
        restPanel.setBackground(Color.darkGray);
        restPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Label nameLabel = new Label(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(Color.white);
        nameLabel.setBounds(20, 10, 200, 20);
        restPanel.add(nameLabel);

        Label offerLabel = new Label(offer);
        offerLabel.setForeground(Color.lightGray);
        offerLabel.setBounds(20, 35, 250, 20);
        restPanel.add(offerLabel);

        Label ratingLabel = new Label("★ " + rating);
        ratingLabel.setForeground(Color.yellow);
        ratingLabel.setBounds(280, 10, 50, 20);
        restPanel.add(ratingLabel);

       
        restPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String[] args = {name};
                DishesPage.main(args);
                ((Frame)restPanel.getParent().getParent().getParent().getParent()).dispose();
            }
        });

        panel.add(restPanel);
    }
}
