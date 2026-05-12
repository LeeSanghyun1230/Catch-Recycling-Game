package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private GameFrame frame;

    // 1. 게임 상태 변수
    private int score = 0;
    private int lives = 3;
    private int fallSpeed = 5; // 기본 떨어지는 속도
    private TrashType selectedType; // 플레이어가 잡아야 할 타겟 쓰레기 종류
    private boolean isGameOver = false;

    // 2. 게임 객체
    private Player player;
    private ArrayList<Trash> trashList;
    private Timer gameTimer;
    private Random random = new Random();
    private int spawnCounter = 0; // 쓰레기 생성 주기 조절용 카운터

    public GamePanel(GameFrame frame, int typeIndex) {
        this.frame = frame;

        // StartPanel에서 넘겨준 숫자(0~3)를 TrashType 열거형(Enum)으로 변환
        this.selectedType = TrashType.values()[typeIndex];

        // 💡 서현님의 Player 생성자 규격에 맞게 초기화 (x: 350, y: 450 위치, 50x50 크기, 15의 속도)
        this.player = new Player(350, 450, 50, 50, 15, selectedType);
        this.trashList = new ArrayList<>();

        // 3. 키보드 입력 이벤트 (좌우 방향키)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    player.moveLeft();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    // 💡 서현님 코드에 맞춰 화면 넓이를 인자로 넘겨줌 (벽에 막히도록)
                    player.moveRight(getWidth());
                }
            }
        });

        // 20ms마다 actionPerformed를 호출 (약 50 FPS)
        gameTimer = new Timer(20, this);
        gameTimer.start();

        setFocusable(true);
        requestFocusInWindow(); // 키보드 입력을 받기 위해 포커스 활성화
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            spawnTrash();   // 쓰레기 무작위 생성
            updateLogic();  // 위치 이동 및 충돌 판정
            repaint();      // 화면 다시 그리기
        }
    }

    private void spawnTrash() {
        spawnCounter++;
        // 약 1초(50프레임)마다 쓰레기 하나씩 생성
        if (spawnCounter >= 50) {
            // 패널이 아직 그려지기 전 가로가 0일 때를 대비해 방어 코드 작성
            int panelWidth = getWidth() > 50 ? getWidth() : 800;
            int x = random.nextInt(panelWidth - 50);

            // 쓰레기 종류를 랜덤으로 선택
            TrashType[] types = TrashType.values();
            TrashType randomType = types[random.nextInt(types.length)];

            // 바구니에 쓰레기 객체 추가 (x, y, 크기, 타입)
            trashList.add(new Trash(x, 0, 40, randomType));

            spawnCounter = 0;
        }
    }

    private void updateLogic() {
        for (int i = 0; i < trashList.size(); i++) {
            Trash t = trashList.get(i);

            // 서현님이 만든 fall 메소드로 쓰레기 낙하
            t.fall(fallSpeed);

            // 플레이어와 쓰레기 충돌 판정
            if (player.getBounds().intersects(t.getBounds())) {
                checkCatch(t);
                trashList.remove(i);
                i--;
                continue;
            }

            // 바닥에 닿아서 놓쳤을 때
            if (t.y > getHeight()) {
                trashList.remove(i);
                i--;
            }
        }

        // 난이도 조절 로직
        adjustDifficulty();
    }

    private void checkCatch(Trash t) {
        if (t.getType() == selectedType) {
            score += 10; // 정답 쓰레기일 경우 점수 증가
        } else {
            lives--;     // 오답 쓰레기일 경우 목숨 감소
            if (lives <= 0) {
                gameOver();
            }
        }
    }

    private void adjustDifficulty() {
        // 50점마다 쓰레기 낙하 속도 1씩 증가
        fallSpeed = 5 + (score / 50);
    }

    private void gameOver() {
        isGameOver = true;
        gameTimer.stop();

        // 💡 민욱님이 요청한 화면 전환 코드 (점수와 함께 결과 창으로 이동)
        frame.changePanel(new ResultPanel(frame, score));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 플레이어 그리기
        if (player != null) {
            player.draw(g);
        }

        // 화면에 있는 모든 쓰레기 그리기
        for (Trash t : trashList) {
            t.draw(g);
        }

        // 왼쪽 위에 점수 등 게임 UI 텍스트 표시
        g.setColor(Color.BLACK);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        g.drawString("Target Type: " + selectedType.name(), 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("Lives: " + lives, 10, 60);
        g.drawString("Speed: " + fallSpeed, 10, 80);
    }
}