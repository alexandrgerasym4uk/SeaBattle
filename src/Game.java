import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Морський бій
 * Author: Герасимчук Олександр
 *
 * Клас Game
 * Визначає точку входу для гри шляхом створення рамки, і заповнення його GameController.
 */
public class Game implements KeyListener {
    /**
     * Точка входу для програми для створення екземпляра класу Game.
     *
     * @param args Не використовується.
     */
    public static void main(String[] args) {
        Game battleShip = new Game();
    }

    /**
     * Посилання на об’єкт GameController для передачі ключових подій.
     */
    private GameController gameController;

    /**
     * Створює JFrame з GameController всередині нього і робить все видимим.
     */
    public Game() {
        // Вибір складності
        String[] options = new String[] {"Легкий", "Середній", "Важкий"};
        String message = "Легкий буде робити ходи абсолютно випадковим чином,\n\n" +
                "Середній зосереджуватиметься на місцях, де знаходить кораблі,"
                + "\n\n" +
                "Важкий зробить розумніший вибір, ніж Середній.";
        int choiseDifficulty = JOptionPane.showOptionDialog(null, message,
                "Виберіть складність",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        JFrame frame = new JFrame("Морський бій");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        gameController = new GameController(choiseDifficulty);
        frame.getContentPane().add(gameController);

        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Викликається при натисканні клавіші. Передає натискання клавіші на GameController.
     *
     * @param e Інформація про те, яку клавішу було натиснуто.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        gameController.keyInput(e.getKeyCode());
    }

    /**
     * Не використовується.
     *
     * @param e Не використовується.
*/
    @Override
    public void keyTyped(KeyEvent e) {}
    /**
     * Не використовується.
     *
     * @param e Не використовується.
     */
    @Override
    public void keyReleased(KeyEvent e) {}
}
