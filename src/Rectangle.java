/**
 * Морський бій
 * Автор: Герасимчук Олександр
 *
 * Клас Rectangle
 * Визначає прямокутник із положенням верхнього лівого кута,
 * і ширина/висота, щоб відобразити розмір прямокутника.
 */
public class Rectangle {
    /**
     * Верхній лівий кут прямокутника.
     */
    protected Coordinates startPoint;
    /**
     * Ширина прямокутника.
     */
    protected int width;
    /**
     * Висота прямокутника.
     */
    protected int height;

    /**
     * Створює новий прямокутник із наданими властивостями.
     *
     * @param startPoint Верхній лівий кут прямокутника.
     * @param width Ширина прямокутника.
     * @param height Висота прямокутника.
     */
    public Rectangle(Coordinates startPoint, int width, int height) {
        this.startPoint = startPoint;
        this.width = width;
        this.height = height;
    }

    /**
     * @param x X координата верхнього лівого кута.
     * @param y Y координата верхнього лівого кута.
     * @param width Ширина прямокутника.
     * @param height Висота прямокутника.
     */
    public Rectangle(int x, int y, int width, int height) {
        this(new Coordinates(x,y),width,height);
    }

    /**
     * Отримує висоту прямокутника.
     *
     * @return Висота прямокутника.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Отримує ширину прямокутника.
     *
     * @return Ширина прямокутника.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Отримує верхній лівий кут прямокутника.
     *
     * @return Верхній лівий кут прямокутника.
     */
    public Coordinates getStartPoint() {
        return startPoint;
    }

    /**
     * Перевіряє, чи coords знаходиться всередині прямокутника.
     *
     * @param coords Позиція, щоб перевірити, чи знаходиться вона всередині прямокутника.
     * @return True, якщо coords знаходиться всередині цього прямокутника.
     */
    public boolean isInside(Coordinates coords) {
        return coords.x >= startPoint.x && coords.y >= startPoint.y
                && coords.x < startPoint.x + width && coords.y < startPoint.y + height;
    }
}