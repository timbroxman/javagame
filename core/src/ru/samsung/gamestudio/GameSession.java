package ru.samsung.gamestudio;
import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.managers.MemoryManager;

import java.util.ArrayList;


public class GameSession {

    public GameState state;
    long nextTrashSpawnTime;
    long nextEnemyShipSpawnTime;
    long nextBonusSpawnTime;
    long sessionStartTime;
    long pauseStartTime;
    private int score;
    int destructedTrashNumber;
    
    // Комбо-система
    private int comboCount;
    private long lastDestructionTime;
    private static final long COMBO_TIMEOUT = 3000; // 3 секунды для поддержания комбо
    private static final int MAX_COMBO_MULTIPLIER = 5; // Максимальный множитель комбо

    public GameSession() {
    }

    public void startGame() {
        state = GameState.PLAYING;
        score = 0;
        destructedTrashNumber = 0;
        comboCount = 0;
        lastDestructionTime = 0;
        sessionStartTime = TimeUtils.millis();
        nextTrashSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                * getTrashPeriodCoolDown());
        nextEnemyShipSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_ENEMY_SHIP_APPEARANCE_COOL_DOWN
                * getEnemyShipPeriodCoolDown());
        nextBonusSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_BONUS_APPEARANCE_COOL_DOWN
                * getBonusPeriodCoolDown());
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        sessionStartTime += TimeUtils.millis() - pauseStartTime;
    }

    public void endGame() {
        updateScore();
        state = GameState.ENDED;
        ArrayList<Integer> recordsTable = MemoryManager.loadRecordsTable();
        if (recordsTable == null) {
            recordsTable = new ArrayList<>();
        }
        int foundIdx = 0;
        for (; foundIdx < recordsTable.size(); foundIdx++) {
            if (recordsTable.get(foundIdx) < getScore()) break;
        }
        recordsTable.add(foundIdx, getScore());
        MemoryManager.saveTableOfRecords(recordsTable);
    }

    public void destructionRegistration() {
        destructedTrashNumber += 1;
        
        // Обновление комбо
        long currentTime = TimeUtils.millis();
        if (currentTime - lastDestructionTime < COMBO_TIMEOUT) {
            comboCount++;
        } else {
            comboCount = 1; // Начинаем новое комбо
        }
        lastDestructionTime = currentTime;
        
        // Ограничиваем максимальное комбо
        if (comboCount > MAX_COMBO_MULTIPLIER * 10) {
            comboCount = MAX_COMBO_MULTIPLIER * 10;
        }
    }

    public void updateScore() {
        int baseScore = (int) (TimeUtils.millis() - sessionStartTime) / 100;
        int destructionScore = destructedTrashNumber * 100 * getComboMultiplier();
        score = baseScore + destructionScore;
    }
    
    public void penaltyForMissedTrash() {
        // Штраф 10% от текущего счёта за пропущенный мусор
        int penalty = (int) (score * 0.1f);
        score = Math.max(0, score - penalty); // Не позволяем счёту стать отрицательным
    }
    
    public int getComboCount() {
        // Проверяем, не истекло ли комбо
        long currentTime = TimeUtils.millis();
        if (currentTime - lastDestructionTime >= COMBO_TIMEOUT) {
            comboCount = 0;
        }
        return comboCount;
    }
    
    public int getComboMultiplier() {
        int combo = getComboCount();
        if (combo >= 30) return MAX_COMBO_MULTIPLIER;
        if (combo >= 20) return 4;
        if (combo >= 10) return 3;
        if (combo >= 5) return 2;
        return 1;
    }
    
    public int getDifficultyLevel() {
        // Уровень сложности на основе времени игры
        long gameTime = TimeUtils.millis() - sessionStartTime;
        return (int) (gameTime / 30000) + 1; // Уровень увеличивается каждые 30 секунд
    }

    public int getScore() {
        return score;
    }

    public boolean shouldSpawnTrash() {
        if (nextTrashSpawnTime <= TimeUtils.millis()) {
            nextTrashSpawnTime = TimeUtils.millis() + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                    * getTrashPeriodCoolDown());
            return true;
        }
        return false;
    }

    private float getTrashPeriodCoolDown() {
        return (float) Math.exp(-0.001 * (TimeUtils.millis() - sessionStartTime + 1) / 1000);
    }

    public boolean shouldSpawnEnemyShip() {
        if (nextEnemyShipSpawnTime <= TimeUtils.millis()) {
            nextEnemyShipSpawnTime = TimeUtils.millis() + (long) (GameSettings.STARTING_ENEMY_SHIP_APPEARANCE_COOL_DOWN
                    * getEnemyShipPeriodCoolDown());
            return true;
        }
        return false;
    }

    private float getEnemyShipPeriodCoolDown() {
        return (float) Math.exp(-0.0005 * (TimeUtils.millis() - sessionStartTime + 1) / 1000);
    }

    public boolean shouldSpawnBonus() {
        if (nextBonusSpawnTime <= TimeUtils.millis()) {
            nextBonusSpawnTime = TimeUtils.millis() + (long) (GameSettings.STARTING_BONUS_APPEARANCE_COOL_DOWN
                    * getBonusPeriodCoolDown());
            return true;
        }
        return false;
    }

    private float getBonusPeriodCoolDown() {
        return (float) Math.exp(-0.0003 * (TimeUtils.millis() - sessionStartTime + 1) / 1000);
    }
}
