import java.awt.*;
/**
 * Морський бій
 * Автор: Герасимчук Олександр
 *
 * Клас Mark
 * Представляє точку, яке може бути видима, або ні залежно від того, попав в корабель, чи ні.
 */
public class Mark extends Rectangle {
    /**
     * Колір попадання в корабель.
     */
    private final Color HIT_COLOUR = Color.RED;
    /**
     * Колір промаху.
     */
    private final Color MISS_COLOUR = Color.BLACK;
    /**
     * Відступ по краях.
     */
    private final int INIDENT = 13;
    /**
     * Якщо значення true, точка буде зафарбована.
     */
    private boolean showMark;
    /**
     * Змінює колір. Коли в корабель попали, використовуватиметься HIT_COLOUR,
     * коли значення null використовуватиме MISS_COLOUR.
     */
    private Ship markedShip;

    /**
     * Створює точку із стандартним станом, у якій вона готова до малювання, яку не буде видно.
     *
     * @param x координата X.
     * @param y Координата Y.
     * @param width Ширина.
     * @param height Висота.
     */
    public Mark(int x, int y, int width, int height) {
        super(x, y, width, height);
        reset();
    }

    /**
     * Скидає привязку до корабля, робиться невидимою.
     */
    public void reset() {
        markedShip = null;
        showMark = false;
    }

    /**
     * Позначення знищення корабля.
     */
    public void mark() {
        if(!showMark && isHasShip()) {
            markedShip.destroyedDecks();
        }
        showMark = true;
    }

    /**
     * Перевіряє чи стоїть точка.
     *
     * @return True, якщо точка є.
     */
    public boolean isMarked() {
        return showMark;
    }

    /**
     * Зберігає інфрмацію про корабель.
     *
     * @param ship Посилання на корабель у цьому місці.
     */
    public void markShip(Ship ship) {
        this.markedShip = ship;
    }

    /**
     * Перевіряє чи ця точка має пов’язаний корабель.
     *
     * @return True, якщо встановлено корабель.
     */
    public boolean isHasShip() {
        return markedShip != null;
    }

    /**
     * Отримує корабель, якщо він є, інакше він буде нульовим.
     *
     * @return Посилання на пов’язаний корабель для цієї точки.
     */
    public Ship getShip() {
        return markedShip;
    }

    /**
     * Малює точку.
     * Використовує колір залежно від того, чи цей об’єкт знаходиться над кораблем.
     *
     * @param g Посилання на графічний об’єкт для малювання.
     */
    public void draw(Graphics g) {
        if(!showMark) return;
        g.setColor(isHasShip() ? HIT_COLOUR : MISS_COLOUR);
        g.fillOval(startPoint.x + INIDENT + 1, startPoint.y + INIDENT + 1, width - INIDENT * 2, height - INIDENT * 2);
    }
}