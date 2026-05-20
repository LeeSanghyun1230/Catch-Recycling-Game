package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private GameFrame frame;

    // [추가] 게임 배경 이미지
    private Image backgroundImage;

    // 1. 게임 상태 변수
    private int score = 0;
    private int lives = 3;
    private int fallSpeed = 5;
    private TrashType selectedType;
    private boolean isGameOver = false;

    // 2. 게임 객체
    private Player player;
    private ArrayList<Trash> trashList;
    private Timer gameTimer;
    private Random random = new Random();
    private int spawnCounter = 0;

    public GamePanel(GameFrame frame, int typeIndex) {
        this.frame = frame;
        this.selectedType = TrashType.values()[typeIndex];

        // [추가] 배경 이미지 불러오기
        backgroundImage = new ImageIcon(
                getClass().getResource("/com/recycling/images/background.png")
        ).getImage();

        // 플레이어 초기화
        // [수정] 플레이어 이미지 크기 50x50 -> 80x80
        // [수정] y 위치 450 -> 430, 크기가 커져서 화면 아래로 나가지 않게 조정
        this.player = new Player(350, 430, 80, 80, 15, selectedType);

        this.trashList = new ArrayList<>();

        // 3. 키보드 입력 이벤트
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    player.moveLeft();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    player.moveRight(getWidth());
                }
            }
        });

        // 핵심: 마우스 클릭 없이 자동으로 키보드 포커스를 잡아주는 리스너
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });

        setFocusable(true);

        // 게임 루프 시작
        gameTimer = new Timer(20, this);
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            spawnTrash();
            updateLogic();
            repaint();
        }
    }

    private void spawnTrash() {
        spawnCounter++;
        if (spawnCounter >= 50) {
            int panelWidth = getWidth() > 50 ? getWidth() : 800;

            // [수정] 쓰레기 크기 40 -> 65
            int trashSize = 65;

            // [수정] 쓰레기가 오른쪽 화면 밖으로 나가지 않게 trashSize 기준으로 계산
            int x = random.nextInt(panelWidth - trashSize);

            TrashType[] types = TrashType.values();
            TrashType randomType = types[random.nextInt(types.length)];

            // [수정] 쓰레기 이미지 크기 40 -> 65
            trashList.add(new Trash(x, 0, trashSize, randomType));
            spawnCounter = 0;
        }
    }

    private void updateLogic() {
        for (int i = 0; i < trashList.size(); i++) {
            Trash t = trashList.get(i);
            t.fall(fallSpeed);

            if (player.getBounds().intersects(t.getBounds())) {
                checkCatch(t);
                trashList.remove(i);
                i--;
                continue;
            }

            if (t.y > getHeight()) {
                trashList.remove(i);
                i--;
            }
        }
        adjustDifficulty();
    }

    private void checkCatch(Trash t) {
        if (t.getType() == selectedType) {
            score += 10;
        } else {
            lives--;
            if (lives <= 0) {
                gameOver();
            }
        }
    }

    private void adjustDifficulty() {
        fallSpeed = 5 + (score / 50);
    }

    private void gameOver() {
        isGameOver = true;
        gameTimer.stop();
        frame.changePanel(new ResultPanel(frame, score));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // [추가] 배경 이미지를 게임 화면 크기에 맞게 그림
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (player != null) {
            player.draw(g);
        }

        for (Trash t : trashList) {
            t.draw(g);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        g.drawString("Target Type: " + selectedType.name(), 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("Lives: " + lives, 10, 60);
        g.drawString("Speed: " + fallSpeed, 10, 80);
    }
}