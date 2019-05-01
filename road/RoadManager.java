package com.javarush.games.racer.road;

import com.javarush.games.racer.*;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;             // границы проезжей части
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;              // крайняя левая позиция координат x матриц объектов-препятствий на проезжей части
    private static final int FOURTH_LANE_POSITION = 44;             // крайняя правая позиция координат x матриц объектов-препятствий на проезжей части
    private List<RoadObject> items = new ArrayList<>();
    private static final int PLAYER_CAR_DISTANCE = 15;              // дистанция между генерируемыми машинами на дороге
    private int passedCarsCount = 0;

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {    // создаем дорожный объект определенного типа
        if (type == RoadObjectType.THORN) {
            return new Thorn(x, y);
        } else if (type == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        } else return new Car(type, x, y);            // если объект не шип создаем машину
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);    // случайное число в пределах проезжей части
        int y = -1 * RoadObject.getHeight(type);                // изначально объект располагается за пределами игрового поля, чтобы появиться плавно
        RoadObject roadObject = createRoadObject(type, x, y);
        if (roadObject != null && isRoadSpaceFree(roadObject) == true) {           // если объект был создан и расстояние между объектами достаточное добавляем его в список
            items.add(roadObject);
        }
    }

    public void draw(Game game) {            // отрисовка дорожных объектов
        for (RoadObject object : items) {
            object.draw(game);
        }
    }

    public void move(int boost) {            // движение дорожных объектов
        for (RoadObject object : items) {
            object.move(boost + object.speed, items);
        }
        deletePassedItems();
    }

    private boolean isThornExists() {        // метод проверят наличие шипов
        boolean exist = false;
        for (RoadObject object : items) {
            if (object.type.equals(RoadObjectType.THORN)) {
                exist = true;
            }
        }
        return exist;
    }

    private boolean isMovingCarExists() {            // // метод проверят наличие пьяного водителя
        boolean exist = false;
        for (RoadObject object : items) {
            if (object.type.equals(RoadObjectType.DRUNK_CAR)) {
                exist = true;
            }
        }
        return exist;
    }

    private void generateThorn(Game game) {
        if (game.getRandomNumber(100) < 10 && !isThornExists()) {  // в 10% случаев должны создаваться шипы
            addRoadObject(RoadObjectType.THORN, game);
        }
    }

    private void generateMovingCar(Game game) {
        if (game.getRandomNumber(100) < 3 && !isMovingCarExists()) {  // в 4% случаев должны создаваться машины с пьяным водителем
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    public void generateNewRoadObjects(Game game) {     // генератор новых дорожных объектов
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void deletePassedItems() {                      // удаление старых шипов после выхода за пределы экрана
        Iterator<RoadObject> iterator = items.iterator();
        while (iterator.hasNext()) {
            RoadObject item = (RoadObject) iterator.next();
            if (item.y >= RacerGame.HEIGHT) {
                if (!(item instanceof Thorn)) {
                    passedCarsCount++;              // если не шипы увеличиваем количество машин мимо которых проехали
                }
                iterator.remove();
            }
        }
    }

    public boolean checkCrush(PlayerCar playerCar) {        // проверка столкнулась ли машина игрока с преградой
        boolean checkCrushBool = false;
        for (RoadObject object : items) {
            if (object.isCollision(playerCar) == true) {
                checkCrushBool = true;
            }
        }
        return checkCrushBool;
    }

    private void generateRegularCar(Game game) {                // генератор новых автомобилей с вероятностью 30%
        int carTypeNumber = game.getRandomNumber(4);
        if (game.getRandomNumber(100) < 35) {
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }
    }

    private boolean isRoadSpaceFree(RoadObject object) {        // проверка есть ли достаточно пространства для новой машины
        boolean isRoadSpaceFreeBool = true;
        for (RoadObject item : items) {
            if (item.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE) == true) {
                isRoadSpaceFreeBool = false;
            }
        }
        return isRoadSpaceFreeBool;
    }

    public int getPassedCarsCount() {
        return passedCarsCount;
    }
}
