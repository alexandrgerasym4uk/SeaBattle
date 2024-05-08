import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Морський бій
 * Автор: Герасимчук Олександр
 *
 * Клас Ship
 * Простий корабель, який можна відобразити на екрані.
 * Надає інформацію про те, скільки палуб має корабель, напрямок корабля, кількість знищених палуб та властивості кольору.
 */
public class Ship {
    /**
     * Використовується для зміни кольору розміщення.
     * Аvailable: корабель можна розмістити в поточному місці, яке показано зеленим кольором.
     * NotАvailable: корабель не можна розмістити в поточному місці, яке показано червоним кольором.
     * Placed: корабель розміщений і використовуватиме налаштування кольору за замовчуванням.
     */
    public enum PlacementColour {Аvailable, NotАvailable, Placed}
    /**
     * Положення корабля.
     */
    private Coordinates shipCoord;
    /**
     * Позиція в пікселях для відбраження корабля.
     */
    private Coordinates drawCoord;
    /**
     * Кількість палуб корабля.
     */
    private int deck;
    /**
     * True - корабель горизонтальний.
     * False - корабель вертикальний.
     */
    private boolean shipOrientation;
    /**
     * Кількість знищених палуб, щоб визначити, чи все судно було знищено.
     */
    private int destroyedDecks;
    /**
     * Зміна кольору під час розміщення корабля, щоб показати доступне чи недоступне розміщення.
     */
    private PlacementColour placementColour;

    /**
     * Створює корабель із властивостями за замовчуванням. Припускає, що його вже було розміщено під час створення.
     *
     * @param shipCoord Позиція, де знаходиться корабель.
     * @param drawCoord Верхній лівий кут комірки, щоб відобразити корабель.
     * @param deck Кількість палуб корабля.
     * @param shipOrientation True - горизонтальний, а false - вертикальний.
     */
    public Ship(Coordinates shipCoord, Coordinates drawCoord, int deck, boolean shipOrientation) {
        this.shipCoord = shipCoord;
        this.drawCoord = drawCoord;
        this.deck = deck;
        this.shipOrientation = shipOrientation;
        destroyedDecks = 0;
        placementColour = PlacementColour.Placed;
    }

    /**
     * Малює корабель корабель, спочатку вибираючи колір, а потім малюючи корабель у правильному напрямку.
     * Вибрано такий колір: зелений, якщо наразі розміщено та він дійсний, червоний, якщо розміщено та недійсний.
     * Якщо його вже було розміщено, він буде червоним, якщо знищено, або темно-сірим у будь-якому іншому випадку.
     *
     * @param g Посилання на графічний об’єкт для візуалізації.
     */
    public void draw(Graphics g) {
        if(placementColour == PlacementColour.Placed) {
            g.setColor(destroyedDecks >= deck ? new Color(246, 0, 0 ): new Color(99, 99, 234));
        } else {
            g.setColor(placementColour == PlacementColour.Аvailable ? new Color(36, 250, 0 ) : new Color(246, 0, 0 ));
        }
        if(shipOrientation) drawHorizontal(g);
        else drawVertical(g);
    }

    /**
     * Встановлює колір розташування, щоб вказати стан корабля.
     *
     * @param placementColour Аvailable встановлює зелений колір, NotАvailable встановлює червоний, Placed встановлює значення за замовчуванням.
     */
    public void setPlacementColour(PlacementColour placementColour) {
        this.placementColour = placementColour;
    }

    /**
     * Перемикає поточний стан між вертикальним і горизонтальним.
     */
    public void flipOrientation() {
        shipOrientation = !shipOrientation;
    }

    /**
     * Лічильник знищених палуб.
     */
    public void destroyedDecks() {
        destroyedDecks++;
    }

    /**
     * Перевіряє чи всі палуби знищені.
     *
     * @return True, якщо всі палуби було знищено.
     */
    public boolean shipDestroyed() {
        return destroyedDecks >= deck;
    }

    /**
     * Оновлює позицію для зображення корабля.
     *
     * @param shipCoord Позиція корабля.
     * @param drawCoord Позиція для малювання корабля в пікселях.
     */
    public void setDrawPosition(Coordinates shipCoord, Coordinates drawCoord) {
        this.drawCoord = drawCoord;
        this.shipCoord = shipCoord;
    }

    /**
     * Отримує напрямок корабля.
     *
     * @return True, якщо корабель зараз горизонтальний, або false, якщо вертикальний.
     */
    public boolean getShipOrientation() {
        return shipOrientation;
    }

    /**
     * Отримує кількість палуб.
     *
     * @return Кількість палуб корабля.
     */
    public int getNumberOfDeck() {
        return deck;
    }

    /**
     * Отримує список усіх координат, які займає цей корабель, щоб використовувати їх для перевірки ШІ.
     *
     * @return Список усіх координат, які займає цей корабель.
     */
    public List<Coordinates> getNumberOfOccupiedCoordinates() {
        List<Coordinates> result = new ArrayList<>();
        if(shipOrientation) {
            for(int x = 0; x < deck; x++) {
                result.add(new Coordinates(shipCoord.x+x, shipCoord.y));
            }
        } else {
            for(int y = 0; y < deck; y++) {
                result.add(new Coordinates(shipCoord.x, shipCoord.y+y));
            }
        }
        return result;
    }

    /**
     * Малює вертикальний корабель на основі кількості палуб.
     *
     * @param g Посилання на графічний об’єкт для візуалізації.
     */
    public void drawVertical(Graphics g) {
        int shipWidth = (int)(GameGrid.SIZE_OF_CELL);
        int shipX = drawCoord.x + GameGrid.SIZE_OF_CELL / 2 - shipWidth / 2;
        g.fillRect(shipX, (drawCoord.y + GameGrid.SIZE_OF_CELL / 4 - shipWidth / 4), shipWidth, (int) (GameGrid.SIZE_OF_CELL * (deck)));

    }

    /**
     * Малює горизонтальний корабель на основі кількості палуб.
     *
     * @param g Посилання на графічний об’єкт для візуалізації.
     */
    public void drawHorizontal(Graphics g) {
        int shipWidth = (int)(GameGrid.SIZE_OF_CELL);
        int shipY = drawCoord.y + GameGrid.SIZE_OF_CELL / 2 - shipWidth / 2;
        g.fillRect((drawCoord.x + GameGrid.SIZE_OF_CELL / 2 - shipWidth / 2), shipY, (int) (GameGrid.SIZE_OF_CELL * (deck)), shipWidth);

    }
}