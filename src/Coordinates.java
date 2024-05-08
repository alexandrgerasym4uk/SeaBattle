/**
 * Морський бій
 * Автор: Герасимчук Олександр
 *
 * Клас Coordinates
 * Використовується для представлення однієї позиції x,y.
 */
public class Coordinates {
    /**
     * Нульовий одиничний вектор.
     */
    public static final Coordinates ZERO = new Coordinates(0,0);
    /**
     * Одиничний вектор вправо.
     */
    public static final Coordinates RIGHT = new Coordinates(1,0);
    /**
     * Одиничний вектор вгору.
     */
    public static final Coordinates UP = new Coordinates(0,-1);
    /**
     * Одиничний вектор вліво.
     */
    public static final Coordinates LEFT = new Coordinates(-1,0);
    /**
     * Одиничний вектор вниз.
     */
    public static final Coordinates DOWN = new Coordinates(0,1);
    /**
     * координата X.
     */
    public int x;
    /**
     * Координата Y.
     */
    public int y;
    /**
     * Встановлює значення позиції.
     *
     * @param x координата X.
     * @param y координата Y.
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Копіює конструктор, щоб створити нову позицію, використовуючи значення в іншій.
     *
     * @param coordsToCopy Позиція для копіювання значень.
     */
    public Coordinates(Coordinates coordsToCopy) {
        this.x = coordsToCopy.x;
        this.y = coordsToCopy.y;
    }
    /**
     * Змінює позицію, додаючи значення з otherCoord.
     *
     * @param otherCoord Інша позиція, щоб додати до поточної.
     */
    public void add(Coordinates otherCoord) {
        this.x += otherCoord.x;
        this.y += otherCoord.y;
    }
    /**
     * Порівнює об’єкт Coordinates з іншим об’єктом.
     * Якщо об’єкт, відмінний від Coordinates, поверне false. В іншому випадку порівнює x і y.
     *
     * @param o Об’єкт для порівняння.
     * @return True, якщо об’єкт o дорівнює цій позиції для х і у.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates position = (Coordinates) o;
        return x == position.x && y == position.y;
    }
    /**
     * Отримує рядкову версію Coordinates.
     *
     * @return Рядок у формі (x, y)
     */
    @Override
    public String toString() {
        return "(" + (x+1) + ", " + (y+1) + ")";
    }
}