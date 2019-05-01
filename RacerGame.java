package com.javarush.games.racer;

import com.javarush.engine.cell.*;
import com.javarush.games.racer.road.*;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2; // середина игрового поля для разделительной полосы
    public static final int ROADSIDE_WIDTH = 14;  // размер обочины
    private RoadMarking roadMarking;         // разметка
    private PlayerCar player;                // машина игрока
    private RoadManager roadManager;         // дорожный объект
    private boolean isGameStopped;           // хранение текущего состояния игры
    private FinishLine finishLine;
    private static final int RACE_GOAL_CARS_COUNT = 40;  // сколько автомобилей нужно объекхать для победы
    private ProgressBar progressBar;
    private int score;

    private void createGame() {              // старт новой игры
        roadMarking = new RoadMarking();     // создаем объект дополнительной дорожной разметки
        player = new PlayerCar();            // создаем объект игрока
        setTurnTimer(60);                    // время хода мс
        isGameStopped = false;               // игра запущена
        roadManager = new RoadManager();     // создаем дорожный объект
        finishLine = new FinishLine();       // создаем финишную черту
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);    // создаем окно прогресса
        score = 3500;
        drawScene();
    }

    private void drawScene() {               // отрисовка всех игровых объектов
        drawField();
        roadMarking.draw(this);       // отрисовываем объект дополнительной дорожной разметки
        player.draw(this);            // отрисовываем игрока
        roadManager.draw(this);       // отрисовка дорожных объектов
        finishLine.draw(this);        // отрисовка финишной линии
        progressBar.draw(this);       // отрисовка прогресса
    }

    private void drawField() {               // отрисовка фона игрового поля
        for (int i = 0; i < HEIGHT; i++) {   // сплошная по центру во всю высоту
            setCellColor(CENTER_X, i, Color.WHITE);     // отрисовываем сплошную между полосами
        }
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (i >= ROADSIDE_WIDTH && i < WIDTH - ROADSIDE_WIDTH && i != CENTER_X) {   // кроме обочины и сплошной
                    setCellColor(i, j, Color.DIMGRAY);          // дорогу делаем серой
                } else if (i != CENTER_X) {                     // кроме сплошной
                    setCellColor(i, j, Color.GREEN);            // обочину зеленой как трава
                }
            }
        }
    }

    private void moveAll() {
        roadMarking.move(player.speed);                // разметка двигается
        player.move();                                 // игрок двигается
        roadManager.move(player.speed);                // дорожные объекты двигаются
        finishLine.move(player.speed);                 // финишная линия двигается
        progressBar.move(roadManager.getPassedCarsCount());     // движение прогресса
    }

    private void gameOver() {
        isGameStopped = true;                           // игра остановлена
        showMessageDialog(Color.BLACK, "Game Over!", Color.RED, 70);
        stopTurnTimer();                                // останавливаем время игры
        player.stop();                                  // меняем отображение машины игрока
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "You Win!", Color.YELLOW, 70);
        stopTurnTimer();
    }

    @Override
    public void initialize() {
        showGrid(false);          // убираем отображение сетки
        setScreenSize(WIDTH, HEIGHT);    // устанавливаем размер игрового поля
        createGame();
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        try {
            if (x >= 0 && x <= HEIGHT) {
                if (y >= 0 && y <= WIDTH) {
                    super.setCellColor(x, y, color);  // если значения входят в диапазон игрового поля отрисовываем игровое поле
                }
            }
        } catch (Exception e) {                       // если значения не входят в игровое поле игнорируем ошибку, ничего не делаем
        }
    }

    @Override
    public void onTurn(int step) {
        score = score - 5;          // на каждом шаге уменьшаем очки на 5
        setScore(score);
        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) {  // если количество автомобилей мимо которых проехали равно количеству автомобилей для победы
            finishLine.show();
        }
        if (roadManager.checkCrush(player) == true) {       // в начале хода проверяем на наличие аварии
            gameOver();
            drawScene();
        } else if (finishLine.isCrossed(player)) {          // если машина игрока пересекла финшную линию
            win();
            drawScene();
        } else {
            moveAll();
            roadManager.generateNewRoadObjects(this);
            drawScene();
        }

    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.RIGHT) {
            player.setDirection(Direction.RIGHT);           // если нажата клавиша направо едем направо
        } else if (key == Key.LEFT) {
            player.setDirection(Direction.LEFT);            // если нажата клавиша налево едем налево
        }
        if (key == Key.SPACE && isGameStopped == true) {     // перезапуск игры при нажатии пробела
            createGame();
        }
        if (key == Key.UP) {                                // если нажата клавиша вверх устанавливаем скорость
            player.setSpeed(4);
        }
        if (key == Key.DOWN) {                                // если нажата клавиша вверх устанавливаем скорость
            player.setSpeed(1);
        }
    }

    @Override
    public void onKeyReleased(Key key) {                            // при отпускании кнопки игрок перестает двигаться
        if (key == Key.RIGHT && player.getDirection() == Direction.RIGHT) {
            player.setDirection(Direction.NONE);
        } else if (key == Key.LEFT && player.getDirection() == Direction.LEFT) {
            player.setDirection(Direction.NONE);
        }
        if (key == Key.UP) {                                // если отпущена клавиша вверх устанавливаем скорость
            player.setSpeed(2);
        }
        if (key == Key.DOWN) {                                // если отпущена клавиша вверх устанавливаем скорость
            player.setSpeed(2);
        }
    }
}
