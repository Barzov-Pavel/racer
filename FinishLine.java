package com.javarush.games.racer;

public class FinishLine extends GameObject {
    private boolean isVisible = false;

    public FinishLine() {
        super(RacerGame.ROADSIDE_WIDTH, -1 * ShapeMatrix.FINISH_LINE.length, ShapeMatrix.FINISH_LINE);
    }

    public void show() {                // видимость финишной черты
        isVisible = true;
    }

    public void move(int boost) {       // если финишная черта на игровом поле она двигается
        if (isVisible) {
            y = y + boost;
        }
    }

    public boolean isCrossed(PlayerCar playerCar) {     // проверка на пересечение финишной черты
        boolean condition = false;
        if (playerCar.y < this.y) {
            condition = true;
        }
        return condition;
    }
}
