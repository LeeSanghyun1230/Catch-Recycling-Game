package com.recycling;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    // BGM(배경음악) 전용 클립
    private Clip bgmClip;

    // 🎵 배경음악 재생 메서드 (무한 반복)
    public void playBGM(String soundFileName) {
        try {
            URL soundURL = getClass().getResource(soundFileName);
            if (soundURL == null) {
                System.err.println("음원 파일을 찾을 수 없습니다: " + soundFileName);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioIn);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // BGM 무한 반복 설정
            bgmClip.start();
        } catch (Exception e) {
            System.err.println("오디오 재생 중 오류 발생: " + e.getMessage());
        }
    }

    // 🎵 배경음악 정지 메서드
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    // 💥 효과음 재생 메서드 (1회 재생)
    public void playSFX(String soundFileName) {
        try {
            URL soundURL = getClass().getResource(soundFileName);
            if (soundURL == null) {
                System.err.println("효과음 파일을 찾을 수 없습니다: " + soundFileName);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip sfxClip = AudioSystem.getClip(); // 효과음이 겹쳐도 재생되도록 매번 새 클립 생성
            sfxClip.open(audioIn);
            sfxClip.start(); // loop()가 없으므로 딱 한 번만 재생됨

        } catch (Exception e) {
            System.err.println("효과음 재생 오류: " + e.getMessage());
        }
    }
}