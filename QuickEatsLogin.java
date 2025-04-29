
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.JLabel;

public class QuickEatsLogin {
    public static void main(String[] args) {
        Frame frame = new Frame("QuickEats Login");
        frame.setSize(400, 600);
        frame.setLayout(null);
        frame.setBackground(Color.BLACK);

        
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds((400 - 200) / 2, 50, 200, 200);  

        try {
            Image img = Toolkit.getDefaultToolkit().getImage("logo.png");
            Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new javax.swing.ImageIcon(scaledImg));
        } catch (Exception e) {
            System.out.println("⚠ Failed to load logo.");
        }
        frame.add(logoLabel);

        Label title = new Label("QuickEats Login", Label.CENTER);
        title.setBounds(50, 270, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.RED);
        frame.add(title);

        Label userLabel = new Label("Username:");
        userLabel.setBounds(60, 310, 80, 25);
        userLabel.setForeground(Color.WHITE);
        frame.add(userLabel);

        TextField username = new TextField();
        username.setBounds(160, 310, 180, 25);
        frame.add(username);

        Label passLabel = new Label("Password:");
        passLabel.setBounds(60, 350, 80, 25);
        passLabel.setForeground(Color.WHITE);
        frame.add(passLabel);

        TextField password = new TextField();
        password.setBounds(160, 350, 180, 25);
        password.setEchoChar('*');
        frame.add(password);

        Button loginBtn = new Button("Login");
        loginBtn.setBounds(140, 400, 100, 30);
        loginBtn.setBackground(Color.RED);
        loginBtn.setForeground(Color.WHITE);
        frame.add(loginBtn);

        Label message = new Label("", Label.CENTER);
        message.setBounds(50, 440, 300, 25);
        message.setFont(new Font("Arial", Font.BOLD, 14));
        message.setForeground(Color.RED);
        frame.add(message);

        Label suggestion = new Label("Don't have an account?", Label.CENTER);
        suggestion.setBounds(90, 470, 220, 20);
        suggestion.setForeground(Color.WHITE);
        suggestion.setFont(new Font("Arial", Font.PLAIN, 13));
        frame.add(suggestion);

        Button goToSignup = new Button("Sign Up");
        goToSignup.setBounds(140, 500, 120, 30);
        goToSignup.setBackground(Color.GRAY);
        goToSignup.setForeground(Color.WHITE);
        frame.add(goToSignup);

        goToSignup.addActionListener(ae -> {
            frame.dispose();
            QuickEatsSignup.main(null);
        });

        loginBtn.addActionListener(e -> {
            try {
                String uname = username.getText().trim();
                String pass = password.getText().trim();

                Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password= ?"
                );
                ps.setString(1, uname);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Session.loggedInUsername = uname;
                    frame.dispose();
                    QuickEatsHome.main(null);
                } else {
                    message.setText("Invalid credentials.");
                }

                rs.close();
                ps.close();
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Error occurred.");
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }
}

