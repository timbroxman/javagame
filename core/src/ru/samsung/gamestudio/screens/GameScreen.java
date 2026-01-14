package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.*;
import ru.samsung.gamestudio.components.*;
import ru.samsung.gamestudio.components.BonusIndicatorView;
import ru.samsung.gamestudio.components.JoystickView;
import ru.samsung.gamestudio.effects.CameraShake;
import ru.samsung.gamestudio.effects.ParticleSystem;
import ru.samsung.gamestudio.effects.ScreenEffects;
import ru.samsung.gamestudio.managers.ContactManager;
import ru.samsung.gamestudio.managers.MemoryManager;
import ru.samsung.gamestudio.objects.BonusObject;
import ru.samsung.gamestudio.objects.BonusType;
import ru.samsung.gamestudio.objects.BulletObject;
import ru.samsung.gamestudio.objects.EnemyBulletObject;
import ru.samsung.gamestudio.objects.EnemyShipObject;
import ru.samsung.gamestudio.objects.ShipObject;
import ru.samsung.gamestudio.objects.TrashObject;

import java.util.ArrayList;

public class GameScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    ShipObject shipObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;
    ArrayList<EnemyShipObject> enemyShipArray;
    ArrayList<EnemyBulletObject> enemyBulletArray;
    ArrayList<BonusObject> bonusArray;

    ContactManager contactManager;

    // Visual effects
    ParticleSystem particleSystem;
    CameraShake cameraShake;
    ScreenEffects screenEffects;
    private int previousShipLives; // Для отслеживания изменений здоровья
    private long lastBossSpawnTime; // Время последнего спавна босса

    // PLAY state UI
    MovingBackgroundView backgroundView;
    ImageView topBlackoutView;
    LiveView liveView;
    TextView scoreTextView;
    TextView levelTextView;
    BonusIndicatorView bonusIndicatorView;
    ButtonView pauseButton;
    JoystickView joystickView;

    // PAUSED state UI
    ImageView fullBlackoutView;
    TextView pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;

    // ENDED state UI
    TextView recordsTextView;
    RecordsListView recordsListView;
    ButtonView homeButton2;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();

        contactManager = new ContactManager(myGdxGame.world);

        // Initialize visual effects
        particleSystem = new ParticleSystem();
        screenEffects = new ScreenEffects();
        // CameraShake должен быть инициализирован после того, как камера установлена
        cameraShake = new CameraShake(myGdxGame.camera);
        lastBossSpawnTime = 0;

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        enemyShipArray = new ArrayList<>();
        enemyBulletArray = new ArrayList<>();
        bonusArray = new ArrayList<>();

        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH,
                myGdxGame.world);

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        scoreTextView = new TextView(myGdxGame.commonWhiteFont, 50, 1215);
        levelTextView = new TextView(myGdxGame.commonWhiteFont, 50, 1145);
        // BonusIndicatorView будет создан после shipObject в restartGame
        bonusIndicatorView = null;
        pauseButton = new ButtonView(
                605, 1200,
                46, 54,
                GameResources.PAUSE_IMG_PATH);

        // Джойстик по центру снизу экрана
        float joystickSize = 200;
        float joystickX = (GameSettings.SCREEN_WIDTH - joystickSize) / 2f;
        float joystickY = 50; // Отступ снизу
        joystickView = new JoystickView(joystickX, joystickY, joystickSize);

        fullBlackoutView = new ImageView(0, 0, GameResources.BLACKOUT_FULL_IMG_PATH);
        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 282, 842, "Pause");
        pauseTextView.setCentered(true);
        homeButton = new ButtonView(
                138, 695,
                200, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "Home");
        continueButton = new ButtonView(
                393, 695,
                200, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "Continue");

        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        recordsTextView.setCentered(true);
        homeButton2 = new ButtonView(
                280, 365,
                160, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "Home");

    }

    @Override
    public void show() {
        restartGame();
    }

    @Override
    public void render(float delta) {

        handleInput();

        if (gameSession.state == GameState.PLAYING) {
            // Прямое управление - корабль следует за касанием/курсором
            // Управление обрабатывается в handleInput()

            if (gameSession.shouldSpawnTrash()) {
                TrashObject trashObject = new TrashObject(
                        GameSettings.TRASH_WIDTH, GameSettings.TRASH_HEIGHT,
                        GameResources.TRASH_IMG_PATH,
                        myGdxGame.world);
                trashArray.add(trashObject);
            }

            if (gameSession.shouldSpawnEnemyShip()) {
                // Выбираем тип врага в зависимости от уровня сложности
                EnemyShipObject.EnemyType enemyType = selectEnemyType();

                // Для боссов используем больший размер
                int width = GameSettings.ENEMY_SHIP_WIDTH;
                int height = GameSettings.ENEMY_SHIP_HEIGHT;
                if (enemyType == EnemyShipObject.EnemyType.BOSS) {
                    width = (int) (GameSettings.ENEMY_SHIP_WIDTH * 1.5f);
                    height = (int) (GameSettings.ENEMY_SHIP_HEIGHT * 1.5f);
                }

                EnemyShipObject enemyShip = new EnemyShipObject(
                        width, height,
                        GameResources.getEnemyTexturePath(enemyType),
                        myGdxGame.world,
                        enemyType);
                enemyShipArray.add(enemyShip);

                if (enemyType == EnemyShipObject.EnemyType.BOSS) {
                    lastBossSpawnTime = com.badlogic.gdx.utils.TimeUtils.millis();
                    // Звук появления босса
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.bossSpawnSound.play(0.6f);
                }
            }

            // Спавн босса каждые 60 секунд
            long currentTime = com.badlogic.gdx.utils.TimeUtils.millis();
            if (currentTime - lastBossSpawnTime > 60000 && gameSession.getDifficultyLevel() >= 3) {
                if (gameSession.shouldSpawnEnemyShip()) {
                    int bossWidth = (int) (GameSettings.ENEMY_SHIP_WIDTH * 1.5f);
                    int bossHeight = (int) (GameSettings.ENEMY_SHIP_HEIGHT * 1.5f);
                    EnemyShipObject boss = new EnemyShipObject(
                            bossWidth, bossHeight,
                            GameResources.getEnemyTexturePath(EnemyShipObject.EnemyType.BOSS),
                            myGdxGame.world,
                            EnemyShipObject.EnemyType.BOSS);
                    enemyShipArray.add(boss);
                    lastBossSpawnTime = currentTime;
                    // Звук появления босса
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.bossSpawnSound.play(0.6f);
                }
            }

            if (gameSession.shouldSpawnBonus()) {
                java.util.Random random = new java.util.Random();
                BonusType[] types = BonusType.values();
                BonusType randomType = types[random.nextInt(types.length)];

                String bonusImagePath;
                switch (randomType) {
                    case HEALTH:
                        bonusImagePath = GameResources.BONUS_HEALTH_IMG_PATH;
                        break;
                    case RAPIDFIRE:
                        bonusImagePath = GameResources.BONUS_RAPIDFIRE_IMG_PATH;
                        break;
                    case SHIELD:
                        bonusImagePath = GameResources.BONUS_SHIELD_IMG_PATH;
                        break;
                    case MULTISHOT:
                        bonusImagePath = GameResources.BONUS_MULTISHOT_IMG_PATH;
                        break;
                    default:
                        bonusImagePath = GameResources.BONUS_HEALTH_IMG_PATH;
                }

                BonusObject bonus = new BonusObject(
                        GameSettings.BONUS_WIDTH, GameSettings.BONUS_HEIGHT,
                        bonusImagePath,
                        myGdxGame.world,
                        randomType);
                bonusArray.add(bonus);
            }

            if (shipObject.needToShoot()) {
                if (shipObject.isMultishotActive()) {
                    // Мультишот: стреляем 3 пулями
                    for (int i = -1; i <= 1; i++) {
                        BulletObject laserBullet = new BulletObject(
                                shipObject.getX() + i * 30, shipObject.getY() + shipObject.height / 2,
                                GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                                GameResources.BULLET_IMG_PATH,
                                myGdxGame.world);
                        bulletArray.add(laserBullet);
                    }
                } else {
                    BulletObject laserBullet = new BulletObject(
                            shipObject.getX(), shipObject.getY() + shipObject.height / 2,
                            GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                            GameResources.BULLET_IMG_PATH,
                            myGdxGame.world);
                    bulletArray.add(laserBullet);
                }
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.shootSound.play();
            }

            // Enemy ships shooting
            for (EnemyShipObject enemyShip : enemyShipArray) {
                if (enemyShip.needToShoot()) {
                    EnemyBulletObject enemyBullet = new EnemyBulletObject(
                            enemyShip.getX(), enemyShip.getY() - enemyShip.height / 2,
                            GameSettings.ENEMY_BULLET_WIDTH, GameSettings.ENEMY_BULLET_HEIGHT,
                            GameResources.BULLET_IMG_PATH,
                            myGdxGame.world);
                    enemyBulletArray.add(enemyBullet);
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.shootSound.play(0.3f);
                }
            }

            if (!shipObject.isAlive()) {
                gameSession.endGame();
                recordsListView.setRecords(MemoryManager.loadRecordsTable());
                // Звук окончания игры
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.gameOverSound.play(0.5f);
            }

            updateTrash();
            updateBullets();
            updateEnemyShips();
            updateEnemyBullets();
            updateBonuses();
            // Обновляем позиции вражеских кораблей
            for (EnemyShipObject enemyShip : enemyShipArray) {
                enemyShip.update();
            }
            backgroundView.move();
            gameSession.updateScore();
            scoreTextView.setText("Score: " + gameSession.getScore());
            liveView.setLeftLives(shipObject.getLiveLeft());

            // Проверяем, получил ли корабль урон (для тряски камеры и звука)
            if (shipObject.getLiveLeft() < previousShipLives) {
                cameraShake.shake(10f, 0.3f); // Тряска камеры при попадании
                // Звук получения урона
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.damageSound.play(0.3f);
            }
            previousShipLives = shipObject.getLiveLeft();

            levelTextView.setText("Level: " + gameSession.getDifficultyLevel());

            // Обновление визуальных эффектов
            particleSystem.update(delta);
            cameraShake.update(delta);
            screenEffects.updateHealthDarkness(shipObject.getLiveLeft(), 3);
            screenEffects.update(delta);

            myGdxGame.stepWorld();
        }

        draw();
    }

    private void handleInput() {
        // --- Управление мышью/тачем (прямое управление и кнопки) ---
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (gameSession.state == GameState.PLAYING) {
                // Проверяем, не нажали ли на кнопку паузы
                if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.buttonClickSound.play(0.3f);
                    gameSession.pauseGame();
                } else {
                    // Прямое управление - корабль следует за касанием
                    shipObject.move(myGdxGame.touch);
                }
            } else if (gameSession.state == GameState.PAUSED) {
                if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.buttonClickSound.play(0.3f);
                    gameSession.resumeGame();
                }
                if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.buttonClickSound.play(0.3f);
                    myGdxGame.setScreen(myGdxGame.menuScreen);
                }
            } else if (gameSession.state == GameState.ENDED) {
                if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.buttonClickSound.play(0.3f);
                    myGdxGame.setScreen(myGdxGame.menuScreen);
                }
            }
        }

        // --- Управление с клавиатуры (WASD и стрелки) ---
        if (gameSession.state == GameState.PLAYING) {
            float x = 0f;
            float y = 0f;

            // Горизонталь: A / LEFT и D / RIGHT
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                x -= 1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += 1f;
            }

            // Вертикаль: W / UP и S / DOWN
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                y += 1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                y -= 1f;
            }

            if (x != 0f || y != 0f) {
                com.badlogic.gdx.math.Vector2 dir = new com.badlogic.gdx.math.Vector2(x, y).nor();
                shipObject.moveByDirection(dir);
            }
        }
    }

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        for (TrashObject trash : trashArray)
            trash.draw(myGdxGame.batch);
        for (EnemyShipObject enemyShip : enemyShipArray)
            enemyShip.draw(myGdxGame.batch);
        for (BonusObject bonus : bonusArray)
            bonus.draw(myGdxGame.batch);
        shipObject.draw(myGdxGame.batch);
        for (BulletObject bullet : bulletArray)
            bullet.draw(myGdxGame.batch);
        for (EnemyBulletObject enemyBullet : enemyBulletArray)
            enemyBullet.draw(myGdxGame.batch);

        // Рисуем систему частиц
        particleSystem.draw(myGdxGame.batch);

        topBlackoutView.draw(myGdxGame.batch);
        scoreTextView.draw(myGdxGame.batch);
        levelTextView.draw(myGdxGame.batch);
        if (bonusIndicatorView != null) {
            bonusIndicatorView.draw(myGdxGame.batch);
        }
        liveView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);

        // Джойстик больше не используется - управление прямое (корабль следует за
        // касанием)

        // Рисуем эффекты экрана (затемнение, вспышки)
        screenEffects.draw(myGdxGame.batch);

        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.ENDED) {
            fullBlackoutView.draw(myGdxGame.batch);
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();

    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {
            TrashObject trash = trashArray.get(i);
            boolean isOutOfFrame = !trash.isInFrame();
            boolean hasToBeDestroyed = !trash.isAlive() || isOutOfFrame;

            if (!trash.isAlive()) {
                // Мусор уничтожен - засчитываем и создаем эффекты
                gameSession.destructionRegistration();
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.explosionSound.play(0.2f);

                // Создаем эффекты взрыва
                float x = trash.getX();
                float y = trash.getY();
                particleSystem.createExplosion(x, y, 20);
                particleSystem.createSparkExplosion(x, y, 15);
                particleSystem.createSmoke(x, y, 10);
            } else if (isOutOfFrame && trash.isAlive()) {
                // Мусор пропущен (ушёл за нижнюю границу живым) - штраф
                // Применяем штраф перед удалением, чтобы он применился только один раз
                gameSession.penaltyForMissedTrash();
            }

            if (hasToBeDestroyed) {
                myGdxGame.world.destroyBody(trash.body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }

    private void updateEnemyShips() {
        for (int i = 0; i < enemyShipArray.size(); i++) {
            EnemyShipObject enemyShip = enemyShipArray.get(i);
            boolean hasToBeDestroyed = !enemyShip.isAlive() || !enemyShip.isInFrame();

            if (!enemyShip.isAlive()) {
                gameSession.destructionRegistration();
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.explosionSound.play(0.2f);

                // Создаем эффекты взрыва для вражеского корабля
                float x = enemyShip.getX();
                float y = enemyShip.getY();

                // Больше частиц для боссов
                int particleCount = 30;
                if (enemyShip.getEnemyType() == EnemyShipObject.EnemyType.BOSS) {
                    particleCount = 60;
                }

                particleSystem.createExplosion(x, y, particleCount);
                particleSystem.createSparkExplosion(x, y, particleCount / 2);
                particleSystem.createSmoke(x, y, particleCount / 2);
            }

            if (hasToBeDestroyed) {
                myGdxGame.world.destroyBody(enemyShip.body);
                enemyShipArray.remove(i--);
            }
        }
    }

    private EnemyShipObject.EnemyType selectEnemyType() {
        int level = gameSession.getDifficultyLevel();
        java.util.Random random = new java.util.Random();
        float chance = random.nextFloat();

        // Вероятности зависят от уровня
        if (level >= 5) {
            // Высокий уровень - больше боссов и танков
            if (chance < 0.1f)
                return EnemyShipObject.EnemyType.BOSS;
            if (chance < 0.25f)
                return EnemyShipObject.EnemyType.TANK;
            if (chance < 0.45f)
                return EnemyShipObject.EnemyType.SLOW;
            if (chance < 0.7f)
                return EnemyShipObject.EnemyType.FAST;
            return EnemyShipObject.EnemyType.NORMAL;
        } else if (level >= 3) {
            // Средний уровень
            if (chance < 0.15f)
                return EnemyShipObject.EnemyType.TANK;
            if (chance < 0.35f)
                return EnemyShipObject.EnemyType.SLOW;
            if (chance < 0.6f)
                return EnemyShipObject.EnemyType.FAST;
            return EnemyShipObject.EnemyType.NORMAL;
        } else {
            // Низкий уровень - в основном обычные и быстрые
            if (chance < 0.1f)
                return EnemyShipObject.EnemyType.SLOW;
            if (chance < 0.4f)
                return EnemyShipObject.EnemyType.FAST;
            return EnemyShipObject.EnemyType.NORMAL;
        }
    }

    private void updateEnemyBullets() {
        for (int i = 0; i < enemyBulletArray.size(); i++) {
            if (enemyBulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(enemyBulletArray.get(i).body);
                enemyBulletArray.remove(i--);
            }
        }
    }

    private void updateBonuses() {
        for (int i = 0; i < bonusArray.size(); i++) {
            BonusObject bonus = bonusArray.get(i);
            boolean hasToBeDestroyed = bonus.isCollected() || !bonus.isInFrame();

            if (bonus.isCollected()) {
                // Создаем вспышку при получении бонуса
                float x = bonus.getX();
                float y = bonus.getY();

                // Цвет вспышки зависит от типа бонуса
                com.badlogic.gdx.graphics.Color flashColor;
                switch (bonus.getBonusType()) {
                    case HEALTH:
                        flashColor = new com.badlogic.gdx.graphics.Color(0f, 1f, 0f, 1f); // Зеленый
                        break;
                    case RAPIDFIRE:
                        flashColor = new com.badlogic.gdx.graphics.Color(1f, 0.5f, 0f, 1f); // Оранжевый
                        break;
                    case SHIELD:
                        flashColor = new com.badlogic.gdx.graphics.Color(0.5f, 0.7f, 1f, 1f); // Синий
                        break;
                    case MULTISHOT:
                        flashColor = new com.badlogic.gdx.graphics.Color(1f, 0f, 1f, 1f); // Фиолетовый
                        break;
                    default:
                        flashColor = new com.badlogic.gdx.graphics.Color(1f, 1f, 1f, 1f); // Белый
                }

                particleSystem.createFlash(x, y, flashColor);
                screenEffects.triggerBonusFlash(flashColor);
                // Звук получения бонуса
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.bonusSound.play(0.4f);
            }

            if (hasToBeDestroyed) {
                myGdxGame.world.destroyBody(bonus.body);
                bonusArray.remove(i--);
            }
        }
    }

    private void restartGame() {

        for (int i = 0; i < trashArray.size(); i++) {
            myGdxGame.world.destroyBody(trashArray.get(i).body);
            trashArray.remove(i--);
        }

        for (int i = 0; i < enemyShipArray.size(); i++) {
            myGdxGame.world.destroyBody(enemyShipArray.get(i).body);
            enemyShipArray.remove(i--);
        }

        for (int i = 0; i < bulletArray.size(); i++) {
            myGdxGame.world.destroyBody(bulletArray.get(i).body);
            bulletArray.remove(i--);
        }

        for (int i = 0; i < enemyBulletArray.size(); i++) {
            myGdxGame.world.destroyBody(enemyBulletArray.get(i).body);
            enemyBulletArray.remove(i--);
        }

        for (int i = 0; i < bonusArray.size(); i++) {
            myGdxGame.world.destroyBody(bonusArray.get(i).body);
            bonusArray.remove(i--);
        }

        if (shipObject != null) {
            myGdxGame.world.destroyBody(shipObject.body);
        }

        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH,
                myGdxGame.world);

        // Обновляем ссылку на shipObject в bonusIndicatorView
        if (bonusIndicatorView != null) {
            bonusIndicatorView.dispose();
        }
        bonusIndicatorView = new BonusIndicatorView(myGdxGame.commonWhiteFont, shipObject, 50, 1100);

        bulletArray.clear();
        enemyBulletArray.clear();
        bonusArray.clear();
        // Сразу обновляем количество жизней в индикаторе
        liveView.setLeftLives(shipObject.getLiveLeft());
        previousShipLives = shipObject.getLiveLeft();
        particleSystem.clear();
        lastBossSpawnTime = com.badlogic.gdx.utils.TimeUtils.millis();
        gameSession.startGame();
    }

}
