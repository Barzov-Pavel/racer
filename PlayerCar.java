package com.javarush.games.racer;

import com.javarush.games.racer.road.*;

public class PlayerCar extends GameObject {
    private static int playerCarHeight = ShapeMatrix.PLAYER.length;
    public int speed = 2;
    private Direction direction;

    public PlayerCar() {
        super(RacerGame.WIDTH / 2 + 2, RacerGame.HEIGHT - playerCarHeight - 1, ShapeMatrix.PLAYER);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void move() {                                    // движение игрока
        if (x < RoadManager.LEFT_BORDER) {                  // проверка границ дороги
            x = RoadManager.LEFT_BORDER;
        } else if (x > RoadManager.RIGHT_BORDER - width) {
            x = RoadManager.RIGHT_BORDER - width;
        }
        if (direction == Direction.LEFT) {                  // изменение координат машины игрока
            x = x - 1;
        } else if (direction == Direction.RIGHT) {
            x = x + 1;
        }
    }

    public void stop() {                    // при столкновении отображение машины игрока меняется
        matrix = ShapeMatrix.PLAYER_DEAD;
    }

    public void setSpeed(int speed) {       // установка скорости используем для ускорения
        this.speed = speed;
    }
}
