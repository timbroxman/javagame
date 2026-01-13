package ru.samsung.gamestudio;

public class GameResources {

    // Images for textures

    public static final String BACKGROUND_IMG_PATH = "textures/background.png";

    public static final String BLACKOUT_FULL_IMG_PATH = "textures/blackout_full.png";
    public static final String BLACKOUT_TOP_IMG_PATH = "textures/blackout_top.png";
    public static final String BLACKOUT_MIDDLE_IMG_PATH = "textures/blackout_middle.png";

    public static final String BUTTON_SHORT_BG_IMG_PATH = "textures/button_background_short.png";
    public static final String BUTTON_LONG_BG_IMG_PATH = "textures/button_background_long.png";

    public static final String PAUSE_IMG_PATH = "textures/pause_icon.png";
    public static final String LIVE_IMG_PATH = "textures/life.png";
    
    // Joystick textures
    public static final String JOYSTICK_OUTER_IMG_PATH = "textures/outer_circle.png";
    public static final String JOYSTICK_INNER_IMG_PATH = "textures/iner_circle.png";

    public static final String BULLET_IMG_PATH = "textures/bullet.png";
    public static final String SHIP_IMG_PATH = "textures/ship.png";
    public static final String ENEMY_SHIP_IMG_PATH = "textures/enemy_ship.png";
    
    // Текстуры для разных типов врагов
    public static final String ENEMY_FAST_IMG_PATH = "textures/enemy_fast.png"; // Быстрый враг
    public static final String ENEMY_NORMAL_IMG_PATH = "textures/enemy_ship.png"; // Обычный враг
    public static final String ENEMY_SLOW_IMG_PATH = "textures/enemy_slow.png"; // Медленный враг
    public static final String ENEMY_TANK_IMG_PATH = "textures/enemy_tank.png"; // Танк
    public static final String ENEMY_BOSS_IMG_PATH = "textures/enemy_boss.png"; // Босс
    
    public static final String TRASH_IMG_PATH = "textures/trash.png";
    
    /**
     * Получить путь к текстуре для указанного типа врага
     * @param enemyType тип врага
     * @return путь к текстуре
     */
    public static String getEnemyTexturePath(ru.samsung.gamestudio.objects.EnemyShipObject.EnemyType enemyType) {
        switch (enemyType) {
            case FAST:
                return ENEMY_FAST_IMG_PATH;
            case NORMAL:
                return ENEMY_NORMAL_IMG_PATH;
            case SLOW:
                return ENEMY_SLOW_IMG_PATH;
            case TANK:
                return ENEMY_TANK_IMG_PATH;
            case BOSS:
                return ENEMY_BOSS_IMG_PATH;
            default:
                return ENEMY_NORMAL_IMG_PATH;
        }
    }
    
    // Bonus images
    public static final String BONUS_HEALTH_IMG_PATH = "textures/bonus_health.png";
    public static final String BONUS_RAPIDFIRE_IMG_PATH = "textures/bonus_rapidfire.png";
    public static final String BONUS_SHIELD_IMG_PATH = "textures/bonus_shield.png";
    public static final String BONUS_MULTISHOT_IMG_PATH = "textures/bonus_multishot.png";

    // Sounds and music for audio
    // Все звуковые файлы должны быть размещены в папке: assets/sounds/
    
    // Основные звуки
    public static final String BACKGROUND_MUSIC_PATH = "sounds/background_music.mp3";
    public static final String DESTROY_SOUND_PATH = "sounds/destroy.mp3";
    public static final String SHOOT_SOUND_PATH = "sounds/shoot.mp3";
    
    // Дополнительные звуки
    public static final String BONUS_SOUND_PATH = "sounds/bonus.mp3";
    public static final String DAMAGE_SOUND_PATH = "sounds/damage.mp3";
    public static final String BUTTON_CLICK_SOUND_PATH = "sounds/button_click.mp3";
    public static final String GAME_OVER_SOUND_PATH = "sounds/game_over.mp3";
    public static final String BOSS_SPAWN_SOUND_PATH = "sounds/boss_spawn.mp3";

    // Fonts for text

    public static final String FONT_PATH = "fonts/Montserrat-Bold.ttf";

}
