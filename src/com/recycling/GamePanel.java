package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    // 1. 게임 상태 변수 (상현님 담당 로직)
    private int score = 0;
    private int lives = 3;
    private int fallSpeed = 5; // 기본 속도
    private int selectedType;  // 박세온님이 넘겨줄 선택된 캐릭터 타입
    private boolean isGameOver = false;

    // 2. 게임 객체 (김서현님 담당 객체 활용)
    private Player player;
    private ArrayList<Trash> trashList;
    private Timer gameTimer;

    public GamePanel(int selectedType) {
        this.selectedType = selectedType;
        this.player = new Player(); // 초기화
        this.trashList = new ArrayList<>();
        
        // 게임 루프 시작 (20ms마다 actionPerformed 호출 -> 50fps)
        gameTimer = new Timer(20, this);
        gameTimer.start();
        
        setFocusable(true);
        // 키보드 입력 등 추가 가능
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            updateLogic();  // 3번 로직 처리
            repaint();      // 화면 갱신
        }
    }

    private void updateLogic() {
        // A. 쓰레기 이동 및 생성 로직
        for (int i = 0; i < trashList.size(); i++) {
            Trash t = trashList.get(i);
            t.y += fallSpeed; // 상현님이 관리하는 속도 적용

            // B. 충돌 판정 로직 (상현님 핵심 역할)
            if (player.getBounds().intersects(t.getBounds())) {
                checkCatch(t);
                trashList.remove(i);
                i--;
                continue;
            }

            // C. 바닥에 닿았을 때 처리
            if (t.y > getHeight()) {
                trashList.remove(i);
                i--;
            }
        }

        // D. 난이도 조절 로직
        adjustDifficulty();
    }

    private void checkCatch(Trash t) {
        if (t.getType() == selectedType) {
            score += 10; // 올바른 쓰레기
        } else {
            lives--;    // 잘못된 쓰레기
            if (lives <= 0) {
                gameOver();
            }
        }
    }

    private void adjustDifficulty() {
        // 점수 50점당 속도 1씩 증가 (상현님 설계)
        fallSpeed = 5 + (score / 50);
    }

    private void gameOver() {
        isGameOver = true;
        gameTimer.stop();
        System.out.println("게임 오버! 최종 점수: " + score);
        // 이후 이민욱님의 ResultPanel로 전환하는 코드 연동
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 배경, 플레이어, 쓰레기 그리기 로직 (서현/민욱님 협업)
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);
        g.drawString("Speed: " + fallSpeed, 10, 60);
    }
}
