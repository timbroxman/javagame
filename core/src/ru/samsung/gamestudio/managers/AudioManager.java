package ru.samsung.gamestudio.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import ru.samsung.gamestudio.GameResources;

public class AudioManager {

    public boolean isSoundOn;
    public boolean isMusicOn;

    public Music backgroundMusic;
    public Sound shootSound;
    public Sound explosionSound;
    public Sound bonusSound;
    public Sound damageSound;
    public Sound buttonClickSound;
    public Sound gameOverSound;
    public Sound bossSpawnSound;

    public AudioManager() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.BACKGROUND_MUSIC_PATH));
        shootSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.SHOOT_SOUND_PATH));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.DESTROY_SOUND_PATH));
        
        // Дополнительные звуки (используют пути из GameResources)
        bonusSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.BONUS_SOUND_PATH));
        damageSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.DAMAGE_SOUND_PATH));
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.BUTTON_CLICK_SOUND_PATH));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.GAME_OVER_SOUND_PATH));
        bossSpawnSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.BOSS_SPAWN_SOUND_PATH));

        backgroundMusic.setVolume(0.2f);
        backgroundMusic.setLooping(true);

        updateSoundFlag();
        updateMusicFlag();
    }

    public void updateSoundFlag() {
        isSoundOn = MemoryManager.loadIsSoundOn();
    }

    public void updateMusicFlag() {
        isMusicOn = MemoryManager.loadIsMusicOn();

        if (isMusicOn) backgroundMusic.play();
        else backgroundMusic.stop();
    }

}
