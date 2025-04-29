import java.awt.*;
import java.awt.event.*;
import java.sql.*;
// import javax.swing.*; 

public class QuickEatsSignup {
    public static void main(String[] args) {
        Frame frame = new Frame("QuickEats Sign Up");
        frame.setSize(400, 650);
        frame.setLayout(null);
        frame.setBackground(Color.BLACK);

        // JLabel logoLabel = new JLabel();
        // logoLabel.setBounds(100, 40, 200, 100);
        // Image img = Toolkit.getDefaultToolkit().getImage("quickeats_logo.png");
        // logoLabel.setIcon(new ImageIcon(img));
        // frame.add(logoLabel);

        Label title = new Label("QuickEats Sign Up", Label.CENTER);
        title.setBounds(50, 150, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.RED);
        frame.add(title);

        int yStart = 190;

        Label nameLbl = new Label("Name:");
        nameLbl.setBounds(60, yStart, 80, 25);
        nameLbl.setForeground(Color.WHITE);
        frame.add(nameLbl);

        TextField name = new TextField();
        name.setBounds(160, yStart, 180, 25);
        frame.add(name);

        Label phoneLbl = new Label("Phone:");
        phoneLbl.setBounds(60, yStart + 40, 80, 25);
        phoneLbl.setForeground(Color.WHITE);
        frame.add(phoneLbl);

        TextField phone = new TextField();
        phone.setBounds(160, yStart + 40, 180, 25);
        frame.add(phone);

        Label emailLbl = new Label("Email:");
        emailLbl.setBounds(60, yStart + 80, 80, 25);
        emailLbl.setForeground(Color.WHITE);
        frame.add(emailLbl);

        TextField email = new TextField();
        email.setBounds(160, yStart + 80, 180, 25);
        frame.add(email);

        Label userLbl = new Label("Username:");
        userLbl.setBounds(60, yStart + 120, 80, 25);
        userLbl.setForeground(Color.WHITE);
        frame.add(userLbl);

        TextField username = new TextField();
        username.setBounds(160, yStart + 120, 180, 25);
        frame.add(username);

        Label passLbl = new Label("Password:");
        passLbl.setBounds(60, yStart + 160, 80, 25);
        passLbl.setForeground(Color.WHITE);
        frame.add(passLbl);

        TextField password = new TextField();
        password.setBounds(160, yStart + 160, 180, 25);
        password.setEchoChar('*');
        frame.add(password);

        Button signupBtn = new Button("Sign Up");
        signupBtn.setBounds(140, yStart + 210, 100, 30);
        signupBtn.setBackground(Color.RED);
        signupBtn.setForeground(Color.WHITE);
        frame.add(signupBtn);

        Label message = new Label("", Label.CENTER);
        message.setBounds(50, yStart + 250, 300, 25);
        message.setFont(new Font("Arial", Font.BOLD, 14));
        message.setForeground(Color.RED);
        frame.add(message);

        Button goToLogin = new Button("Already have an account? Login");
        goToLogin.setBounds(100, yStart + 290, 200, 30);
        goToLogin.setBackground(Color.GRAY);
        goToLogin.setForeground(Color.WHITE);
        frame.add(goToLogin);

        goToLogin.addActionListener(ae -> {
            frame.dispose();
            QuickEatsLogin.main(null);
        });

        signupBtn.addActionListener(e -> {
            try {
                String uname = username.getText().trim();
                String pass = password.getText().trim();
                String n = name.getText().trim();
                String p = phone.getText().trim();
                String em = email.getText().trim();

                if (uname.isEmpty() || pass.isEmpty() || n.isEmpty() || p.isEmpty() || em.isEmpty()) {
                    message.setText("Please fill all fields.");
                    return;
                }

                Connection conn = DriverManager.getConnection("jdbc:sqlite:javaapp.db");
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, password, name, phone, email) VALUES (?, ?, ?, ?, ?)"
                );
                ps.setString(1, uname);
                ps.setString(2, pass);
                ps.setString(3, n);
                ps.setString(4, p);
                ps.setString(5, em);
                ps.executeUpdate();
                ps.close();
                conn.close();

                Session.loggedInUsername = uname;
                frame.dispose();
                QuickEatsHome.main(null);

            } catch (Exception ex) {
                message.setText("Username already exists!");
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
