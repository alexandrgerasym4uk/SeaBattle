import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * Морський бій
 * Author: Герасимчук Олександр
 *
 * Клас GameController:
 * Керує двома сітками, однією для гравця, а іншою для комп’ютера, за допомогою
 * панелі стану між ними. Залежно від стану гри гравець може
 * розмістити кораблі на своїй сітці або атакувати сітку комп'ютера.
 */
public class GameController extends JPanel implements MouseListener, MouseMotionListener {
    /**
     * GameStates, які змінюють спосіб взаємодії.
     * Розміщення кораблів: у цьому стані гравець може розміщувати кораблі на своїй дошці.
     * Стан закінчується, коли всі кораблі розміщені.
     * Game: гравець може атакувати сітку комп’ютера та отримувати відповідь.
     * Стан закінчується, коли всі кораблі на будь-якій сітці були знищені.
     * GameOver: коли гравець або комп’ютер знищено, щоб запобігти введенню.
     * Закінчується, коли гравець виходить або вирішує перезапустити.
     */
    public enum GameState { PlacingShips, Game, GameOver }

    /**
     * Посилання на панель стану для передачі текстових повідомлень, щоб показати, що відбувається.
     */
    private StatusPanel statusPanel;
    /**
     * Комп'ютерна сітка для атаки гравця.
     */
    private GameGrid computer;
    /**
     * Сітка гравця для атаки комп'ютера.
     */
    private GameGrid player;
    /**
     * AI для керування тим, що комп’ютер робитиме кожного ходу.
     */
    private BattleshipAI ai;

    /**
     * Посилання на тимчасове судно, яке розміщується під час стану PlacingShips.
     */
    private Ship placingShip;
    /**
     *
     * Положення сітки, де розміщено корабель розміщення.
     */
    private Coordinates tempPlacingCoords;
    /**
     * Посилання на те, який корабель має бути розміщений наступним під час стану PlacingShips.
     */
    private int placingShipIndex;
    /**
     * Стан гри, який показує, чи може гравець розміщувати кораблі, атакувати комп’ютер,
     * або якщо гра вже закінчена.
     */
    private GameState gameState;
    /**
     * Стан, який можна перемикати за допомогою D, щоб показати кораблі комп’ютера.
     */
    public static boolean debugMode;


    /**
     * Ініціалізує все необхідне для початку гри.
     * Штучний інтелект налаштований і все готово для початку гри з розміщення корабля для гравця.
     */
    public GameController(int aiChoice) {
        computer = new GameGrid(0,0);
        player = new GameGrid(0,computer.getHeight()+50);
        setBackground(new Color(219, 219, 225));
        setPreferredSize(new Dimension(computer.getWidth(), player.getStartPoint().y + player.getHeight()));
        addMouseListener(this);
        addMouseMotionListener(this);
        if(aiChoice == 0) ai = new SimpleAI(player);
        else ai = new SmartAI(player,aiChoice == 2,aiChoice == 2);
        statusPanel = new StatusPanel(new Coordinates(0,computer.getHeight()+1),computer.getWidth(),49);
        reset();
    }

    /**
     * Малює сітки для обох гравців, будь-який розміщений корабель і панель стану.
     *
     * @param g Посилання на графічний об’єкт для малювання.
     */
    public void paint(Graphics g) {
        super.paint(g);
        computer.draw(g);
        player.draw(g);
        if(gameState == GameState.PlacingShips) {
            placingShip.draw(g);
        }
        statusPanel.draw(g);
    }

    /**
     * Обробляє введення на основі натиснутих клавіш.
     * Escape завершує роботу програми.
     * R перезапускається.
     * T обертає корабель у стані PlacingShips.
     * D активує режим налагодження, щоб показати комп’ютерні кораблі.
     *
     * @param keyCode Натиснута клавіша.
     */
    public void keyInput(int keyCode) {
        if(keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(1);
        } else if(keyCode == KeyEvent.VK_R) {
            reset();
        } else if(gameState == GameState.PlacingShips && keyCode == KeyEvent.VK_T) {
            placingShip.flipOrientation();
            updateShipPlacement(tempPlacingCoords);
        } else if(keyCode == KeyEvent.VK_D) {
            debugMode = !debugMode;
        }
        repaint();
    }

    /**
     * Скидає всі властивості класу до стандартних значень, готових до початку нової гри.
     */
    public void reset() {
        computer.reset();
        player.reset();
        player.setShowAllShips(true);
        ai.reset();
        tempPlacingCoords = new Coordinates(0,0);
        placingShip = new Ship(new Coordinates(0,0),
                new Coordinates(player.getStartPoint().x,player.getStartPoint().y),
                GameGrid.BOATS[0], true);
        placingShipIndex = 0;
        updateShipPlacement(tempPlacingCoords);
        computer.makeShips();
        debugMode = false;
        statusPanel.reset();
        gameState = GameState.PlacingShips;
    }

    /**
     * Використовує позицію миші, щоб перевірити оновлення корабля, розміщеного під час стану PlacingShip.
     * Тоді, якщо місце його розміщення дійсне, корабель можна установити.
     *
     * @param mouseCoords Координати миші всередині панелі.
     */
    private void tryPlaceShip(Coordinates mouseCoords) {
        Coordinates targetPosition = player.getMousePosition(mouseCoords.x, mouseCoords.y);
        updateShipPlacement(targetPosition);
        if(player.isValidShipPlacement(targetPosition.x, targetPosition.y,
                GameGrid.BOATS[placingShipIndex],placingShip.getShipOrientation())) {
            placeShip(targetPosition);
        }
    }

    /**
     * Завершує вставлення розміщеного корабля, зберігаючи його в сітці гравця.
     * Потім або готує наступний корабель до розміщення, або переходить до наступного стану.
     *
     * @param targetCoords Позиція на сітці, куди потрібно вставити корабель.
     */
    private void placeShip(Coordinates targetCoords) {
        placingShip.setPlacementColour(Ship.PlacementColour.Placed);
        player.placeShips(placingShip, tempPlacingCoords.x, tempPlacingCoords.y);
        placingShipIndex++;
        if(placingShipIndex < GameGrid.BOATS.length) {
            placingShip = new Ship(new Coordinates(targetCoords.x, targetCoords.y),
                    new Coordinates(player.getStartPoint().x + targetCoords.x * GameGrid.SIZE_OF_CELL,
                            player.getStartPoint().y + targetCoords.y * GameGrid.SIZE_OF_CELL),
                    GameGrid.BOATS[placingShipIndex], true);
            updateShipPlacement(tempPlacingCoords);
        } else {
            gameState = GameState.Game;
            statusPanel.setUpLine("Ваш хід!");
            statusPanel.setDownLine("Знищіть усі кораблі, щоб перемогти!");
        }
    }

    /**
     * Спроби обстрілу позиції на сітці комп'ютера.
     * Після черги гравця, ШІ отримує хід, якщо гра ще не закінчена.
     *
     * @param mousePosition Координати миші всередині панелі.
     */
   private void tryFireAtComputer(Coordinates mousePosition) {
        Coordinates targetPosition = computer.getMousePosition(mousePosition.x,mousePosition.y);
        if(!computer.isPosMarked(targetPosition)) {
            doPlayerTurn(targetPosition);
            if(!computer.allShipsDestroyed()) {
                doAITurn();
            }
        }
    }

    /**
     * Обробляє хід гравця залежно від того, де він вибрав атаку.
     * Залежно від результату атаки гравцеві відображається повідомлення,
     * і якщо він знищив останній корабель, гра оновлюється до виграного стану.
     *
     * @param targetPosition Позиція сітки, яку клацнув гравець.
     */
    private void doPlayerTurn(Coordinates targetPosition) {
        boolean hit = computer.markCoord(targetPosition);
        String hitMiss = hit ? "Влучив" : "Промах";
        String destroyed = "";
        if (hit && computer.getMarkAtPosition(targetPosition).getShip().shipDestroyed()) {
            destroyed = "(Знищено)";
        }
        statusPanel.setUpLine("Гравець " + hitMiss + " " + targetPosition + destroyed);
        if (computer.allShipsDestroyed()) {
            gameState = GameState.GameOver;
            statusPanel.showGameOver(true);
        }
    }

    /**
     * Обробляє хід ШІ за допомогою контролера ШІ для вибору ходу.
     * Потім обробляє результат, щоб відобразити його гравцеві.
     * Якщо ШІ знищить останній корабель, гра завершиться перемогою ШІ.
     */
    private void doAITurn() {
        Coordinates aiMove = ai.selectMove();
        boolean hit = player.markCoord(aiMove);
        String hitMiss = hit ? "Влучив" : "Промах";
        String destroyed = "";
        if(hit && player.getMarkAtPosition(aiMove).getShip().shipDestroyed()) {
            destroyed = "(Знищено)";
        }
        statusPanel.setDownLine("Комп'ютер " + hitMiss + " " + aiMove + destroyed);
        if(player.allShipsDestroyed()) {
            gameState = GameState.GameOver;
            statusPanel.showGameOver(false);
        }
    }

    /**
     * Оновлює розташування корабля, якщо миша знаходиться всередині сітки.
     *
     * @param mouseCoords Координати миші всередині панелі.
     */
    private void tryMovePlacingShip(Coordinates mouseCoords) {
        if(player.isInside(mouseCoords)) {
            Coordinates targetPos = player.getMousePosition(mouseCoords.x, mouseCoords.y);
            updateShipPlacement(targetPos);
        }
    }

    /**
     * Обмежує корабель, щоб він поміщався в сітку. Оновлює намальовану позицію корабля,
     * і змінює колір корабля залежно від того, дійсне чи недійсне розміщення.
     *
     * @param targetCoords Координата сітки, на яку має змінитися розміщений корабель.
     */
    private void updateShipPlacement(Coordinates targetCoords) {
        if(placingShip.getShipOrientation()) {
            targetCoords.x = Math.min(targetCoords.x, GameGrid.GRID_WIDTH - GameGrid.BOATS[placingShipIndex]);
        } else {
            targetCoords.y = Math.min(targetCoords.y, GameGrid.GRID_HEIGHT - GameGrid.BOATS[placingShipIndex]);
        }
        placingShip.setDrawPosition(new Coordinates(targetCoords),
                new Coordinates(player.getStartPoint().x + targetCoords.x * GameGrid.SIZE_OF_CELL,
                        player.getStartPoint().y + targetCoords.y * GameGrid.SIZE_OF_CELL));
        tempPlacingCoords = targetCoords;
        if(player.isValidShipPlacement(tempPlacingCoords.x, tempPlacingCoords.y,
                GameGrid.BOATS[placingShipIndex],placingShip.getShipOrientation())) {
            placingShip.setPlacementColour(Ship.PlacementColour.Аvailable);
        } else {
            placingShip.setPlacementColour(Ship.PlacementColour.NotАvailable);
        }
    }

    /**
     * Спрацьовує після відпускання кнопки миші.
     * Якщо в стані PlacingShips і курсор знаходиться всередині сітки гравця, він намагатиметься розмістити корабель.
     * Якщо в стані Game і курсор знаходиться в сітці комп’ютера, він спробує стріляти по комп'ютеру.
     *
     * @param e Подробиці про те, де сталася подія миші.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        Coordinates mouseCoords = new Coordinates(e.getX(), e.getY());
        if(gameState == GameState.PlacingShips && player.isInside(mouseCoords)) {
            tryPlaceShip(mouseCoords);
        } else if(gameState == GameState.Game && computer.isInside(mouseCoords)) {
            tryFireAtComputer(mouseCoords);
        }
        repaint();
    }

    /**
     * Спрацьовує, коли миша рухається всередині панелі. Нічого не робить, якщо не в стані PlacingShips.
     * Спробує перемістити корабель, який зараз розміщено на основі координат миші.
     *
     * @param e Подробиці про те, де сталася подія миші.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if(gameState != GameState.PlacingShips) return;
        tryMovePlacingShip(new Coordinates(e.getX(), e.getY()));
        repaint();
    }

    /**
     * Не використовується.
     *
     * @param e Не використовується.
     */
    @Override
    public void mouseClicked(MouseEvent e) {}
    /**
     * Не використовується.
     *
     * @param e Не використовується.
     */
    @Override
    public void mousePressed(MouseEvent e) {}
    /**
     * Not Не використовується.
     *
     * @param e Не використовується.
     */
    @Override
    public void mouseEntered(MouseEvent e) {}
    /**
     * Не використовується.
     *
     * @param e Не використовується.
     */
    @Override
    public void mouseExited(MouseEvent e) {}
    /**
     * Не використовується.
     *
     * @param e Не використовується.
     */
    @Override
    public void mouseDragged(MouseEvent e) {}
}