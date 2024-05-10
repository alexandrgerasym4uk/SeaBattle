import java.util.Collections;

/**
 * Морський бій
 * Author: Герасимчук Олександр
 *
 * Клас SimpleAI:
 * Дуже простий штучний інтелект. Він перебирає список дійсних ходів у довільному порядку.
 */
public class SimpleAI extends BattleshipAI{
    /**
     * Ініціалізує простий ШІ шляхом рандомізації порядку ходів.
     *
     * @param playerGrid Посилання на сітку гравця для атаки.
     */
    public SimpleAI(GameGrid playerGrid) {
        super(playerGrid);
        Collections.shuffle(moves);
    }

    /**
     * Скидання ШІ шляхом скидання батьківського класу.
     */
    @Override
    public void reset() {
        super.reset();
        Collections.shuffle(moves);
    }

    /**
     * Бере хід з початку списку та повертає його.
     *
     * @return Координати ходу.
     */
    @Override
    public Coordinates selectMove() {
        Coordinates nextMove = moves.get(0);
        moves.remove(0);
        return nextMove;
    }
}