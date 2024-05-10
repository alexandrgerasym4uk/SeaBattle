import java.awt.*;

/**
 * Battleship
 * Author:
 *
 * Клас StatusPanel:
 * Проста текстова панель для відображення верхнього та нижнього рядків тексту.
 * Деякі з них уже визначені в класі.
 * Він надає додаткові методи встановлення користувацьких значень для повідомлень.
 */
public class StatusPanel extends Rectangle{
    /**
     * Шрифт для відображення повідомлень.
     */
    private final Font font = new Font("Sans-serif", Font.BOLD, 17);
    /**
     * Повідомлення у верхньому рядку під час розміщення корабля.
     */
    private final String placingShipUpLine = "Розмістіть свої кораблі!";
    /**
     * Повідомлення в нижньому рядку під час розміщення корабля.
     */
    private final String placingShipDownLine = "Натисніть T для обертання.";
    /**
     * Повідомлення у верхньому рядку, коли гра програна.
     */
    private final String gameOverLineLost = "Гра завершена! Ви програли :(";
    /**
     * Повідомлення у верхньому рядку, коли гру виграно.
     */
    private final String gameOverLineWin = "Ви Перемогли! Вітаю!";
    /**
     *
     * Повідомлення в нижньому рядку, коли гру виграно чи програно.
     */
    private final String gameOverDownLine = "Натисніть R щоб почати спочатку";

    /**
     * Поточне повідомлення для відображення у верхньому рядку.
     */
    private String upLine;
    /**
     * Поточне повідомлення для відображення в нижньому рядку.
     */
    private String downLine;

    /**
     * Налаштовує панель стану, щоб вона була готова і встановлює початковий текст за замовчуванням.
     *
     * @param coords Верхній лівий кут панелі.
     * @param width Ширина області для малювання.
     * @param height Висота області для малювання.
     */
    public StatusPanel(Coordinates coords, int width, int height) {
        super(coords, width, height);
        reset();
    }

    /**
     * Скидає повідомлення до стандартних для розміщення корабля.
     */
    public void reset() {
        upLine = placingShipUpLine;
        downLine = placingShipDownLine;
    }

    /**
     * Встановлює відображення повідомлення залежно від того, виграв чи програв гравець.
     *
     * @param playerWin True, якщо гравець виграв, або false, якщо гравець програв.
     */
    public void showGameOver(boolean playerWin) {
        upLine = (playerWin) ? gameOverLineWin : gameOverLineLost;
        downLine = gameOverDownLine;
    }

    /**
     * Встановлює повідомлення у верхньому рядку виводу на будь-який вказаний рядок.
     *
     * @param message Повідомлення для відображення у верхньому рядку.
     */
    public void setUpLine(String message) {
        upLine = message;
    }

    /**
     * Встановлює повідомлення в нижньому рядку виводу на будь-який вказаний рядок.
     *
     * @param message Повідомлення для відображення в нижньому рядку.
     */
    public void setDownLine(String message) {
        downLine = message;
    }

    /**
     * Малювання фону для виведення повідомлень.
     *
     * @param g Посилання на графічний об’єкт для візуалізації.
     */
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(startPoint.x, startPoint.y, width, height);
        g.setColor(Color.BLACK);
        g.setFont(font);
        int strWidth = g.getFontMetrics().stringWidth(upLine);
        g.drawString(upLine, startPoint.x+width/2-strWidth/2, startPoint.y+20);
        strWidth = g.getFontMetrics().stringWidth(downLine);
        g.drawString(downLine, startPoint.x+width/2-strWidth/2, startPoint.y+40);
    }
}