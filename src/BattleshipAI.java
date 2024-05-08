import java.util.ArrayList;
import java.util.List;

/**
 * Морський бій
 * Author: Герасимчук Олександр
 *
 * Клас BattleShipAI:
 * Клас шаблону ШІ, забезпечує поведінку ШІ.
 */
public class BattleshipAI {
    /**
     * Посилання на сітку, якою керує гравець.
     */
    protected GameGrid gameGrid;
    /**
     * Список усіх дійсних ходів.
     */
    protected List<Coordinates> moves;

    /**
     * Створює базові налаштування для ШІ, встановлюючи посилання на сітку гравця,
     * і створює список усіх дійсних ходів.
     *
     * @param gameGrid Посилання на сітку, якою керує гравець.
     */
    public BattleshipAI(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
        createMoveList();
    }

    /**
     * Перевизначте цей метод, щоб забезпечити логіку ШІ для вибору позиції для атаки.
     * За умовчанням повертає Position.ZERO.
     *
     * @return Позиція, яка була обрана як місце для атаки.
     */
    public Coordinates selectMove() {
        return Coordinates.ZERO;
    }

    /**
     * Відтворює дійсний список переміщень.
     */
    public void reset() {
        createMoveList();
    }

    /**
     * Створює дійсний список ходів шляхом заповнення списку позиціями.
     */
    private void createMoveList() {
        moves = new ArrayList<>();
        for(int x = 0; x < GameGrid.GRID_WIDTH; x++) {
            for(int y = 0; y < GameGrid.GRID_HEIGHT; y++) {
                moves.add(new Coordinates(x,y));
            }
        }
    }
}