import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Морський бій
 * Author: Герасимчук Олександр
 *
 * Клас SmartAI:
 * Визначає ШІ, який шукатиме випадковим чином, доки не знайде кораблі.
 * Потім намагається атакувати клітини навколо виявленого місця
 * нещадно йти за кораблями.
 */
public class SmartAI extends BattleshipAI {
    /**
     * Список позицій, де були ранені кораблі, які ще не знищені.
     */
    private List<Coordinates> hitShips;
    /**
     * Перевірка налагодження ШІ.
     * true - ввімкнена, false - вимкнена.
     */
    private final boolean debugAI = false;
    /**
     * Якщо значення true, суміжні рухи оцінюються для формування лінії з існуючими позиціями кораблів.
     * Якщо false, хід вибирається випадковим чином із дійсних сусідніх ходів.
     */
    private boolean preferMovesFormingLine;
    /**
     * Якщо значення true, випадковий вибір ходів знайде або перший випадковий хід з
     * чотирма сусідніми неатакованими плитками, або з найбільшою кількістю неатакованих плиток.
     * Якщо false, буде використано лише наступний випадковий вибір.
     */
    private boolean maximiseAdjacentRandomisation;

    /**
     * Створює базові налаштування для ШІ, встановлюючи посилання на сітку гравця,
     * і створює список усіх дійсних ходів.
     *
     * @param playerGrid Посилання на сітку, якою керує гравець.
     * @param preferMovesFormingLine True дозволить найрозумнішій версії штучного інтелекту намагатися формувати ряди під час атаки кораблів.
     * @param maximiseAdjacentRandomisation True змушує рандомним атакам віддавати перевагу позиціям сітки, навколо яких є більше неатакованих точок.
     */
    public SmartAI(GameGrid playerGrid, boolean preferMovesFormingLine, boolean maximiseAdjacentRandomisation) {
        super(playerGrid);
        hitShips = new ArrayList<>();
        this.preferMovesFormingLine = preferMovesFormingLine;
        this.maximiseAdjacentRandomisation = maximiseAdjacentRandomisation;
        Collections.shuffle(moves);
    }

    /**
     * Скидає кораблі, які були вражені, і рандомізує порядок руху.
     */
    @Override
    public void reset() {
        super.reset();
        hitShips.clear();
        Collections.shuffle(moves);
    }

    /**
     * Вибирає відповідний хід залежно від того, чи були якісь кораблі наразі вражені та ще не знищені.
     * ШІ вибере атаку поруч із відомими місцями ураження корабля, якщо корабель був ранений, інакше
     * він вибере наступний випадковий хід.
     *
     * @return Вибрана позиція для атаки.
     */
    @Override
    public Coordinates selectMove() {
        if(debugAI) System.out.println("\nПОЧАТОК ХОДУ===========");
        Coordinates selectedMove;
        // Якщо корабель був уражений, але не знищений
        if(hitShips.size() > 0) {
            if(preferMovesFormingLine) {
                selectedMove = smarterAttack();
            } else {
                selectedMove = smartAttack();
            }
        } else {
            if(maximiseAdjacentRandomisation) {
                selectedMove = findLeastAttackedCoords();
            } else {
                // Використовуйте випадковий хід
                selectedMove = moves.get(0);
            }
        }
        updateShipHits(selectedMove);
        moves.remove(selectedMove);
        if(debugAI) {
            System.out.println("Хід ШІ: " + selectedMove);
            System.out.println("КІНЕЦЬ ХОДУ===========");
        }
        return selectedMove;
    }

    /**
     * Отримує список ходів, що прилягають до враженої клітинки корабля, і вибирає один випадковим чином.
     *
     * @return Випадковий хід, який має хороший шанс знову влучити в корабель.
     */
    private Coordinates smartAttack() {
        List<Coordinates> suggestedMoves = getAdjacentSmartMoves();
        Collections.shuffle(suggestedMoves);
        return  suggestedMoves.get(0);
    }


    /**
     * Отримує список ходів, що прилягають до враженої клітинки корабля, і вибирає один на основі
     * чи утворює він лінію принаймні з двох елементів із сусідніми ударами корабля.
     * Якщо оптимального припущення не знайдено, вибирається випадковий сусідній хід.
     *
     * @return Дійсний хід, який є суміжним із hitShips, віддаючи перевагу тому, який утворює лінію.
     */
    private Coordinates smarterAttack() {
        List<Coordinates> suggestedMoves = getAdjacentSmartMoves();
        for(Coordinates possibleOptimalMove : suggestedMoves) {
            if(atLeastTwoHitsInDirection(possibleOptimalMove, Coordinates.LEFT)) return possibleOptimalMove;
            if(atLeastTwoHitsInDirection(possibleOptimalMove, Coordinates.RIGHT)) return possibleOptimalMove;
            if(atLeastTwoHitsInDirection(possibleOptimalMove, Coordinates.DOWN)) return possibleOptimalMove;
            if(atLeastTwoHitsInDirection(possibleOptimalMove, Coordinates.UP)) return possibleOptimalMove;
        }
        // Оптимального вибору не знайдено, просто вибере хід випадковим чином.
        Collections.shuffle(suggestedMoves);
        return  suggestedMoves.get(0);
    }

    /**
     * Шукає дійсний хід із найбільшою кількістю суміжних клітинок, які не зазнали атаки.
     *
     * @return Перша позиція з найвищим балом у списку дійсних ходів.
     */
    private Coordinates findLeastAttackedCoords() {
        Coordinates coords = moves.get(0);;
        int leastAttacked = -1;
        for(int i = 0; i < moves.size(); i++) {
            int count = countAdjacentNotAttackedCoords(moves.get(i));
            if(count == 4) {
                return moves.get(i);
            } else if(count > leastAttacked) {
                leastAttacked = count;
                coords = moves.get(i);
            }
        }
        return coords;
    }

    /**
     * Підраховує кількість суміжних комірок, які не були позначені навколо вказаної позиції.
     *
     * @param coords Положення для підрахунку суміжних клітинок.
     * @return Кількість суміжних клітинок, які не були позначені навколо позиції.
     */
    private int countAdjacentNotAttackedCoords(Coordinates coords) {
        List<Coordinates> adjacentCoords = getAdjacentCells(coords);
        int notAttacked = 0;
        for(Coordinates adjacentCell : adjacentCoords) {
            if(!gameGrid.getMarkAtPosition(adjacentCell).isMarked()) {
                notAttacked++;
            }
        }
        return notAttacked;
    }

    /**
     * Перевіряє, чи є два сусідніх удари корабля в напрямку від початкової точки тесту.
     *
     * @param startCoord Позиція для початку.
     * @param direction Напрямок руху з початкової позиції.
     * @return True, якщо є два сусідніх удари корабля у вказаному напрямку.
     */
    private boolean atLeastTwoHitsInDirection(Coordinates startCoord, Coordinates direction) {
        Coordinates testCoords = new Coordinates(startCoord);
        testCoords.add(direction);
        if(!hitShips.contains(testCoords)) return false;
        testCoords.add(direction);
        if(!hitShips.contains(testCoords)) return false;
        if(debugAI) System.out.println("Знайдено кращий хід ВІД: " + startCoord + " ДО: " + testCoords);
        return true;
    }

    /**
     * Отримує суміжні комірки навколо кожної враженої клітинки і створює унікальний список
     * елементів, які також ще перебувають у дійсному списку переміщення.
     *
     * @return Список усіх дійсних ходів, які є суміжними клітинками з поточними ударами корабля.
     */
    private List<Coordinates> getAdjacentSmartMoves() {
        List<Coordinates> res = new ArrayList<>();
        for(Coordinates shipHitPos : hitShips) {
            List<Coordinates> adjacentCoords = getAdjacentCells(shipHitPos);
            for(Coordinates adjacentCoord : adjacentCoords) {
                if(!res.contains(adjacentCoord) && moves.contains(adjacentCoord)) {
                    res.add(adjacentCoord);
                }
            }
        }
        if(debugAI) {
            printPositionList("Ранений корабель: ", hitShips);
            printPositionList("Суміжні кращі ходи: ", res);
        }
        return res;
    }

    /**
     * Метод для друку списку позицій.
     *
     * @param prefix Повідомлення налагодження для показу перед даними.
     * @param data Список елементів для відображення у формі [,,,]
     */
    private void printPositionList(String prefix, List<Coordinates> data) {
        String res = "[";
        for(int i = 0; i < data.size(); i++) {
            res += data.get(i);
            if(i != data.size()-1) {
                res += ", ";
            }
        }
        res += "]";
        System.out.println(prefix + " " + res);
    }

    /**
     * Створює список усіх суміжних клітинок навколо позиції, за винятком будь-якої з них поза сіткою.
     *
     * @param coord Позиція для пошуку суміжних комірок.
     * @return Список усіх суміжних позицій, які знаходяться всередині простору сітки.
     */
    private List<Coordinates> getAdjacentCells(Coordinates coord) {
        List<Coordinates> res = new ArrayList<>();
        if(coord.x != 0) {
            Coordinates left = new Coordinates(coord);
            left.add(Coordinates.LEFT);
            res.add(left);
        }
        if(coord.x != GameGrid.GRID_WIDTH-1) {
            Coordinates right = new Coordinates(coord);
            right.add(Coordinates.RIGHT);
            res.add(right);
        }
        if(coord.y != 0) {
            Coordinates up = new Coordinates(coord);
            up.add(Coordinates.UP);
            res.add(up);
        }
        if(coord.y != GameGrid.GRID_HEIGHT-1) {
            Coordinates down = new Coordinates(coord);
            down.add(Coordinates.DOWN);
            res.add(down);
        }
        return res;
    }

    /**
     * Перевіряє, чи позиція "влучає" в корабель. Потім оцінює, чи корабель буде знищено.
     * Якщо його буде знищено, усі дані про корабель буде очищено.
     *
     * @param testCoords Позиція, яка оцінюється для влучення в корабель.
     */
    private void updateShipHits(Coordinates testCoords) {
        Mark mark = gameGrid.getMarkAtPosition(testCoords);
        if(mark.isHasShip()) {
            hitShips.add(testCoords);
            // Перевірте, чи це було останнє місце, куди влучили на кораблі
            List<Coordinates> allPositionsOfLastShip = mark.getShip().getNumberOfOccupiedCoordinates();
            if(debugAI) printPositionList("Останній корабель", allPositionsOfLastShip);
            boolean hitAllOfShip = containsAllPositions(allPositionsOfLastShip, hitShips);
            // Якщо так, видалити дані корабля
            if(hitAllOfShip) {
                for(Coordinates shipPosition : allPositionsOfLastShip) {
                    for(int i = 0; i < hitShips.size(); i++) {
                        if(hitShips.get(i).equals(shipPosition)) {
                            hitShips.remove(i);
                            if(debugAI) System.out.println("Видалений " + shipPosition);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Перевіряє, чи всі позиції в coordsToSearch знаходяться в listToSearchIn.
     *
     * @param coordsToSearch Список позицій для пошуку в усіх.
     * @param listToSearchIn Список позицій для пошуку всередині.
     * @return True, якщо всі позиції в coordsToSearch знаходяться в listToSearchIn.
     */
    private boolean containsAllPositions(List<Coordinates> coordsToSearch, List<Coordinates> listToSearchIn) {
        for(Coordinates searchCoords : coordsToSearch) {
            boolean found = false;
            for(Coordinates searchInPosition : listToSearchIn) {
                if(searchInPosition.equals(searchCoords)) {
                    found = true;
                    break;
                }
            }
            if(!found) return false;
        }
        return true;
    }
}