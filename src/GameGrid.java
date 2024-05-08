import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Морський бій
 * Автор: Герасимчук Олександр
 *
 * Клас GameGrid
 * Визначає сітку для зберігання кораблів із сіткою точок
 * вказує на виявлення попадання/пропуску.
 */
public class GameGrid extends Rectangle {
    /**
     * Розмір кожної клітинки сітки в пікселях.
     */
    public static final int SIZE_OF_CELL = 35;
    /**
     * Кількість комірок сітки на горизонтальній осі.
     */
    public static final int GRID_WIDTH = 10;
    /**
     * Кількість клітинок сітки на вертикальній осі.
     */
    public static final int GRID_HEIGHT = 10;
    /**
     * Визначення кількості кораблів і кількості палуб кораблів.
     */
    public static final int[] BOATS = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    /**
     * Сітка точок для візуального вказівки попадання/промаху при атаках.
     */
    private Mark[][] marks = new Mark[GRID_WIDTH][GRID_HEIGHT];
    /**
     * Список усіх кораблів у цій сітці.
     */
    private List<Ship> ships;
    /**
     * Спільне випадкове посилання для рандомізації розміщення корабля.
     */
    private Random random;
    /**
     * Використовується для того, щоб кораблі гравця завжди відображалися.
     */
    private boolean playerShipsVisible;
    /**
     * True, коли всі елементи кораблів були знищені.
     */
    private boolean allShipsDestroyed;

    /**
     * Налаштовує сітку для створення стандартної конфігурації точок.
     *
     * @param x координата X для зміщення сітки в пікселях.
     * @param y Координата Y для зміщення сітки в пікселях.
     */
    public GameGrid(int x, int y) {
        super(x, y, SIZE_OF_CELL *GRID_WIDTH, SIZE_OF_CELL *GRID_HEIGHT);
        createMarksGrid();
        ships = new ArrayList<>();
        random = new Random();
        playerShipsVisible = false;
    }

    /**
     * Малює кораблі, якщо всі показані, або активний режим налагодження, або кожне судно позначено як знищене.
     * Потім малює всі точки, це показано для атак, здійснених до цього часу, і сітка ліній.
     *
     * @param g Посилання на графічний об’єкт для візуалізації.
     */
    public void draw(Graphics g) {
        for(Ship ship : ships) {
            if(playerShipsVisible || GameController.debugMode || ship.shipDestroyed()) {
                ship.draw(g);
            }
        }
        drawMarks(g);
        drawGrid(g);
    }

    /**
     * Змінює стан сітки, щоб відображати всі кораблі, якщо встановлено значення true.
     *
     * @param playerShipsVisible True зробить усі кораблі в цій сітці видимими.
     */
    public void setShowAllShips(boolean playerShipsVisible) {
        this.playerShipsVisible = playerShipsVisible;
    }

    /**
     * Скидає GameGrid, повідомляє всім точкам про скидання, видаляє всі кораблі із сітки, робить кораблі невидимими,
     * жодного корабля не знищено.
     */
    public void reset() {
        for(int x = 0; x < GRID_WIDTH; x++) {
            for(int y = 0; y < GRID_HEIGHT; y++) {
                marks[x][y].reset();
            }
        }
        ships.clear();
        playerShipsVisible = false;
        allShipsDestroyed = false;
    }

    /**
     * Позначає вказану позицію, а потім перевіряє всі кораблі, щоб визначити, чи вони знищені.
     *
     * @param markPos Позиція для позначення.
     * @return True, якщо позначена позиція була кораблем.
     */
    public boolean markCoord(Coordinates markPos) {
        marks[markPos.x][markPos.y].mark();
        allShipsDestroyed = true;
        for(Ship ship : ships) {
            if(!ship.shipDestroyed()) {
                allShipsDestroyed = false;
                break;
            }
        }
        return marks[markPos.x][markPos.y].isHasShip();
    }

    /**
     * Перевіряє, чи всі кораблі були знищені.
     *
     * @return True, якщо всі кораблі були знищені.
     */
    public boolean allShipsDestroyed() {
        return allShipsDestroyed;
    }

    /**
     * Перевіряє, чи вказана позиція позначена.
     *
     * @param markPos Позиція для перевірки, що вона позначена.
     * @return True, якщо маркер у вказаній позиції позначено.
     */
    public boolean isPosMarked(Coordinates markPos) {
        return marks[markPos.x][markPos.y].isMarked();
    }

    /**
     * Отримує точку у вказаній позиції, для надання штучному інтелекту більшого доступу до даних у сітці.
     *
     * @param markedPos Позиція на сітці для вибору точки.
     * @return Повертає посилання на точку у вказаній позиції.
     */
    public Mark getMarkAtPosition(Coordinates markedPos) {
        return marks[markedPos.x][markedPos.y];
    }

    /**
     * Передає позицію миші на координатну сітку.
     *
     * @param xMouse X координата миші.
     * @param yMouse Y координата миші.
     * @return Повертає або (-1,-1) для недійсної позиції, або відповідну позицію сітки, пов’язану з координатами.
     */
    public Coordinates getMousePosition(int xMouse, int yMouse) {
        if(!isInside(new Coordinates(xMouse, yMouse))) return new Coordinates(-1,-1);
        return new Coordinates((xMouse - startPoint.x)/ SIZE_OF_CELL, (yMouse - startPoint.y)/ SIZE_OF_CELL);
    }

    /**
     * Перевіряє, чи буде корабель із зазначеними властивостями дійсним для розміщення,
     * чи вписується корабель у межі сітки, чи всі палуби потраплять на місця, де ще не стоїть корабель.
     * Це обробляється окремо залежно від того, це горизонтальний чи вертикальний корабель.
     *
     * @param xGrid Координата X сітки.
     * @param yGrid Координата Y сітки.
     * @param decks Кількість палуб.
     * @param orientation True вказує на те, що він горизонтальний, false всередині – вертикальний.
     * @return True, якщо корабель може бути розміщений із зазначеними властивостями.
     */
    public boolean isValidShipPlacement(int xGrid, int yGrid, int decks, boolean orientation) {
        if (xGrid < 0 || yGrid < 0) return false;

        if (orientation) {
            if (yGrid > GRID_HEIGHT || xGrid + decks > GRID_WIDTH) return false;

            for (int x = xGrid - 1; x <= xGrid + decks; x++) {
                for (int y = yGrid - 1; y <= yGrid + 1; y++) {
                    if (x >= 0 && y >= 0 && x < GRID_WIDTH && y < GRID_HEIGHT) {
                        if (marks[x][y].isHasShip()) return false;
                    }
                }
            }
        } else {
            if (yGrid + decks > GRID_HEIGHT || xGrid > GRID_WIDTH) return false;

            for (int x = xGrid - 1; x <= xGrid + 1; x++) {
                for (int y = yGrid - 1; y <= yGrid + decks; y++) {
                    if (x >= 0 && y >= 0 && x < GRID_WIDTH && y < GRID_HEIGHT) {
                        if (marks[x][y].isHasShip()) return false;
                    }
                }
            }
        }

        return true;
    }



    /**
     * Малює сітку з чорних ліній.
     *
     * @param g Посилання на графічний об’єкт для візуалізації.
     */
    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        int y2 = startPoint.y;
        int y1 = startPoint.y+height;
        for(int x = 0; x <= GRID_WIDTH; x++)
            g.drawLine(startPoint.x+x * SIZE_OF_CELL, y1, startPoint.x+x * SIZE_OF_CELL, y2);
        int x2 = startPoint.x;
        int x1 = startPoint.x+width;
        for(int y = 0; y <= GRID_HEIGHT; y++)
            g.drawLine(x1, startPoint.y+y * SIZE_OF_CELL, x2, startPoint.y+y * SIZE_OF_CELL);
    }

    /**
     * Малює всі точки. Точки індивідуально визначать, чи потрібно їх малювати.
     *
     * @param g Посилання на графічний об’єкт для візуалізації.
     */
    private void drawMarks(Graphics g) {
        for(int x = 0; x < GRID_WIDTH; x++) {
            for(int y = 0; y < GRID_HEIGHT; y++) {
                marks[x][y].draw(g);
            }
        }
    }

    /**
     * Створює всі точки, встановлюючи їхні позиції малювання на сітці.
     */
    private void createMarksGrid() {
        for(int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                marks[x][y] = new Mark(startPoint.x+x* SIZE_OF_CELL, startPoint.y + y* SIZE_OF_CELL, SIZE_OF_CELL, SIZE_OF_CELL);
            }
        }
    }

    /**
     * Очищає всі поточні кораблі, а потім випадковим чином розміщує всі кораблі.
     * Цей метод передбачає наявність достатньо місця для розміщення всіх кораблів незалежно від конфігурації.
     */
    public void makeShips() {
        ships.clear();
        for(int i = 0; i < BOATS.length; i++) {
            boolean orientation = random.nextBoolean();
            int xGrid, yGrid;
            do {
                xGrid = random.nextInt(orientation ?GRID_WIDTH- BOATS[i]:GRID_WIDTH);
                yGrid = random.nextInt(orientation ?GRID_HEIGHT:GRID_HEIGHT- BOATS[i]);
            } while(!isValidShipPlacement(xGrid, yGrid, BOATS[i], orientation));
            placeShips(xGrid, yGrid, BOATS[i], orientation);
        }
    }

    /**
     * Розміщує на сітці корабель із зазначеними властивостями.
     * Вказує клітинкам точок, що це корабель поверх них, щоб використовувати для розміщення інших кораблів і виявлення ударів.
     *
     * @param xGrid X координата на сітці.
     * @param yGrid Координата Y на сітці.
     * @param decks Кількість комірок, які займає корабель.
     * @param orientation True вказує на горизонталь, а false вказує на вертикаль.
     */
    public void placeShips(int xGrid, int yGrid, int decks, boolean orientation) {
        placeShips(new Ship(new Coordinates(xGrid, yGrid),
                new Coordinates(startPoint.x+ xGrid * SIZE_OF_CELL, startPoint.y+ yGrid * SIZE_OF_CELL),
                decks, orientation), xGrid, yGrid);
    }

    /**
     * Розміщує на сітці корабель із зазначеними властивостями. Вказує клітинкам точок, що це корабель
     * поверх них, щоб використовувати для розміщення інших кораблів і виявлення ударів.
     *
     * @param ship Корабель для розміщення в сітці з уже налаштованими властивостями.
     * @param xGrid X координата на сітці.
     * @param yGrid Координата Y на сітці.
     */
    public void placeShips(Ship ship, int xGrid, int yGrid) {
        ships.add(ship);
        if(ship.getShipOrientation()) {
            for(int x = 0; x < ship.getNumberOfDeck(); x++) {
                marks[xGrid +x][yGrid].markShip(ships.get(ships.size()-1));
            }
        } else {
            for(int y = 0; y < ship.getNumberOfDeck(); y++) {
                marks[xGrid][yGrid +y].markShip(ships.get(ships.size()-1));
            }
        }
    }
}