import javax.swing.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(350,350);
        setLocation(400,400);
        add(new GameField());
        setVisible(true);
    }
}
