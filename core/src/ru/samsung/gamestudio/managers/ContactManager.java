package ru.samsung.gamestudio.managers;

import com.badlogic.gdx.physics.box2d.*;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.objects.GameObject;

public class ContactManager {

    World world;

    public ContactManager(World world) {
        this.world = world;
        world.setContactListener(new GameContactListener());
    }

    private static class GameContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            Fixture fixA = contact.getFixtureA();
            Fixture fixB = contact.getFixtureB();

            int cDef = fixA.getFilterData().categoryBits;
            int cDef2 = fixB.getFilterData().categoryBits;

            // Trash hits player bullet
            if (cDef == GameSettings.TRASH_BIT && cDef2 == GameSettings.BULLET_BIT
                    || cDef2 == GameSettings.TRASH_BIT && cDef == GameSettings.BULLET_BIT) {

                ((GameObject) fixA.getUserData()).hit();
                ((GameObject) fixB.getUserData()).hit();
            }

            // Player bullet hits enemy ship
            if (cDef == GameSettings.ENEMY_SHIP_BIT && cDef2 == GameSettings.BULLET_BIT
                    || cDef2 == GameSettings.ENEMY_SHIP_BIT && cDef == GameSettings.BULLET_BIT) {
                
                GameObject enemyObj = (cDef == GameSettings.ENEMY_SHIP_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                GameObject bulletObj = (cDef == GameSettings.BULLET_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                
                // Сохраняем информацию о попадании для отображения урона
                if (enemyObj instanceof ru.samsung.gamestudio.objects.EnemyShipObject) {
                    ru.samsung.gamestudio.objects.EnemyShipObject enemy = 
                        (ru.samsung.gamestudio.objects.EnemyShipObject) enemyObj;
                    int livesBefore = enemy.getLivesLeft();
                    enemy.hit();
                    int livesAfter = enemy.getLivesLeft();
                    
                    // Если жизни уменьшились, создаем текст урона
                    if (livesAfter < livesBefore && enemy.isAlive()) {
                        // Это будет обработано в GameScreen через специальный механизм
                        // Пока просто наносим урон
                    }
                }
                
                bulletObj.hit();
            }

            // Enemy bullet hits player ship
            if (cDef == GameSettings.SHIP_BIT && cDef2 == GameSettings.ENEMY_BULLET_BIT
                    || cDef2 == GameSettings.SHIP_BIT && cDef == GameSettings.ENEMY_BULLET_BIT) {
                
                GameObject shipObj = (cDef == GameSettings.SHIP_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                GameObject bulletObj = (cDef == GameSettings.ENEMY_BULLET_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                
                // Проверяем неуязвимость корабля
                if (shipObj instanceof ru.samsung.gamestudio.objects.ShipObject) {
                    ru.samsung.gamestudio.objects.ShipObject ship = 
                        (ru.samsung.gamestudio.objects.ShipObject) shipObj;
                    if (!ship.isInvincible()) {
                        ship.hit();
                        bulletObj.hit();
                    }
                }
            }
            
            // Trash hits player ship (тоже учитываем неуязвимость)
            if (cDef == GameSettings.SHIP_BIT && cDef2 == GameSettings.TRASH_BIT
                    || cDef2 == GameSettings.SHIP_BIT && cDef == GameSettings.TRASH_BIT) {
                
                GameObject shipObj = (cDef == GameSettings.SHIP_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                GameObject trashObj = (cDef == GameSettings.TRASH_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                
                if (shipObj instanceof ru.samsung.gamestudio.objects.ShipObject) {
                    ru.samsung.gamestudio.objects.ShipObject ship = 
                        (ru.samsung.gamestudio.objects.ShipObject) shipObj;
                    if (!ship.isInvincible()) {
                        ship.hit();
                        trashObj.hit();
                    }
                }
            }
            
            // Bonus collected by player ship
            if (cDef == GameSettings.SHIP_BIT && cDef2 == GameSettings.BONUS_BIT
                    || cDef2 == GameSettings.SHIP_BIT && cDef == GameSettings.BONUS_BIT) {
                
                GameObject shipObj = (cDef == GameSettings.SHIP_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                GameObject bonusObj = (cDef == GameSettings.BONUS_BIT) ? 
                    (GameObject) fixA.getUserData() : (GameObject) fixB.getUserData();
                
                if (shipObj instanceof ru.samsung.gamestudio.objects.ShipObject 
                        && bonusObj instanceof ru.samsung.gamestudio.objects.BonusObject) {
                    ru.samsung.gamestudio.objects.ShipObject ship = 
                        (ru.samsung.gamestudio.objects.ShipObject) shipObj;
                    ru.samsung.gamestudio.objects.BonusObject bonus = 
                        (ru.samsung.gamestudio.objects.BonusObject) bonusObj;
                    
                    if (!bonus.isCollected()) {
                        bonus.collect();
                        ship.applyBonus(bonus.getBonusType());
                    }
                }
            }
        }

        @Override
        public void endContact(Contact contact) {
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    }

}
