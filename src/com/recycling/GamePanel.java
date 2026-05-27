package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Desktop;
import java.net.URI;

public class GamePanel extends JPanel implements ActionListener {

    private GameFrame frame;

    // ✨ 단일 배경 대신, 4단계 배경 이미지를 담을 배열로 변경
    private Image[] backgroundStages = new Image[4];

    // 1. 게임 상태 변수
    private int score = 0;
    private int lives = 3;
    private int fallSpeed = 5;
    private TrashType selectedType;
    private boolean isGameOver = false;

    // 부활 기회를 썼는지 안 썼는지 기억하는 변수 추가!
    private boolean hasRevived = false;

    // 2. 게임 객체
    private Player player;
    private ArrayList<Trash> trashList;
    private ArrayList<GameItem> itemList;
    private Timer gameTimer;
    private Random random = new Random();
    private int spawnCounter = 0;

    // 아이템 관련 변수
    private int itemSpawnCounter = 0;
    private int hintTicks = 0;
    private boolean shieldOn = false;

    public GamePanel(GameFrame frame, int typeIndex) {
        this.frame = frame;
        this.selectedType = TrashType.values()[typeIndex];

        // ✨ 4단계 배경 이미지 미리 불러오기 (bg_stage1 ~ bg_stage4)
        for (int i = 0; i < 4; i++) {
            String bgPath = "/com/recycling/images/bg_stage" + (i + 1) + ".png";
            try {
                backgroundStages[i] = new ImageIcon(getClass().getResource(bgPath)).getImage();
            } catch (Exception e) {
                System.err.println("⚠️ 배경 이미지를 찾을 수 없습니다: " + bgPath);
            }
        }

        // 플레이어 초기화
        // 플레이어 이미지 크기 80x80, y 위치 430
        this.player = new Player(350, 430, 80, 80, 15, selectedType);
        this.trashList = new ArrayList<>();
        this.itemList = new ArrayList<>();

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

        // 핵심: 마 편집 없이 자동으로 키보드 포커스를 잡아주는 리스너
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
            spawnItem();
            updateLogic();
            repaint();
        }
    }

    private void spawnTrash() {
        spawnCounter++;
        if (spawnCounter >= 50) {
            int panelWidth = getWidth() > 50 ? getWidth() : 800;
            int trashSize = 65;
            TrashType[] types = TrashType.values();

            // ✨ 한 번에 생성할 개수
            int spawnCount = 2;

            // ✨ 화면을 생성할 개수만큼 N등분 합니다. (2개면 화면을 반으로 나눔)
            int sectionWidth = panelWidth / spawnCount;

            for (int i = 0; i < spawnCount; i++) {
                // 구역의 시작점 계산 (i=0이면 0부터, i=1이면 절반부터)
                int startX = i * sectionWidth;

                // 해당 구역 안에서 쓰레기가 화면 밖으로 나가지 않게 랜덤 값 뽑기
                int maxRandom = sectionWidth - trashSize;
                if (maxRandom <= 0) maxRandom = 1; // 화면이 너무 작을 때를 대비한 안전장치

                // 시작점 + 구역 내 랜덤 위치
                int x = startX + random.nextInt(maxRandom);

                TrashType randomType = types[random.nextInt(types.length)];
                trashList.add(new Trash(x, 0, trashSize, randomType));
            }

            spawnCounter = 0;
        }
    }

    private void spawnItem() {
        itemSpawnCounter++;

        if (itemSpawnCounter >= 80) {
            int panelWidth = getWidth() > 50 ? getWidth() : 800;
            int itemSize = 55;

            // 낮은 확률로 아이템 등장
            if (random.nextInt(100) < 8) {
                int x = random.nextInt(Math.max(1, panelWidth - itemSize));
                int pick = random.nextInt(100);

                ItemType itemType;

                if (pick < 25) {
                    itemType = ItemType.HEART;
                } else if (pick < 65) {
                    itemType = ItemType.HINT;
                } else {
                    itemType = ItemType.SHIELD;
                }

                itemList.add(new GameItem(x, 0, itemSize, itemType));
            }

            itemSpawnCounter = 0;
        }
    }

    private void updateLogic() {
        // 🛠️ [수정됨] 충돌 판정을 하기 전에 플레이어의 위치를 최신화(동기화) 합니다.
        if (player != null && getHeight() > 0) {
            player.y = getHeight() - 100;
        }

        for (int i = 0; i < trashList.size(); i++) {
            Trash t = trashList.get(i);
            t.fall(fallSpeed);

            // 1. 플레이어와 쓰레기가 부딪혔을 때
            if (player.getBounds().intersects(t.getBounds())) {
                checkCatch(t); // 점수 판정 및 게임 오버 처리

                // checkCatch 때문에 게임이 끝나서 trashList가 비워졌다면 지우지 않고 반복문을 끝냅니다.
                if (!trashList.isEmpty() && i < trashList.size()) {
                    trashList.remove(i);
                    i--; // 지웠으니 인덱스를 하나 당겨줌
                } else {
                    break; // 리스트가 비었다면(게임 종료 등) 반복문 즉시 탈출!
                }
                continue;
            }

            // 2. 쓰레기가 바닥으로 떨어졌을 때
            if (t.y > getHeight()) {
                trashList.remove(i);
                i--;
            }
        }

        for (int i = 0; i < itemList.size(); i++) {
            GameItem item = itemList.get(i);
            item.fall(fallSpeed);

            if (player.getBounds().intersects(item.getBounds())) {
                applyItem(item);
                itemList.remove(i);
                i--;
                continue;
            }

            if (item.y > getHeight()) {
                itemList.remove(i);
                i--;
            }
        }

        if (hintTicks > 0) {
            hintTicks--;
        }

        adjustDifficulty();
    }

    private void applyItem(GameItem item) {
        if (item.getType() == ItemType.HEART) {
            lives++;
        } else if (item.getType() == ItemType.HINT) {
            hintTicks = 250; // 약 5초
        } else if (item.getType() == ItemType.SHIELD) {
            shieldOn = true;
        }
    }

    private void checkCatch(Trash t) {
        if (t.getType() == selectedType) {
            score += 10;
        } else {
            if (shieldOn) {
                shieldOn = false;
            } else {
                lives--;
                if (lives <= 0) {
                    gameOver();
                }
            }
        }
    }

    private void adjustDifficulty() {
        fallSpeed = 4 + (score / 50);
    }

    private void gameOver() {
        gameTimer.stop(); // 게임 일시 정지

        if (!hasRevived) {
            // 1. 8개의 퀴즈 문제 목록
            String[] questions = {
                    " [부활 찬스] 양파 껍질, 대파 뿌리의 올바른 배출 방법은?",
                    " [부활 찬스] 국물이 배어 지워지지 않는 컵라면 용기는?",
                    " [부활 찬스] 마트 영수증이나 택배 전표는 어디에 버려야 할까요?",
                    " [부활 찬스] 맛있게 먹고 남은 치킨 뼈와 돼지갈비 뼈는?",
                    " [부활 찬스] 물컹물컹한 젤 형태의 아이스팩 버리는 방법은?",
                    " [부활 찬스] 다 쓴 칫솔은 어떻게 버려야 할까요?",
                    " [부활 찬스] 실수로 깬 유리컵을 버리는 가장 올바른 방법은?",
                    " [부활 찬스] 사용하고 난 물티슈의 올바른 배출 방법은?"
            };

            // 2. 각 문제의 3가지 선택지 (버튼으로 나옴)
            String[][] options = {
                    {"음식물쓰레기", "일반쓰레기", "퇴비로 사용"},
                    {"일반쓰레기", "플라스틱류", "스티로폼류"},
                    {"종이류", "비닐류", "일반쓰레기"},
                    {"일반쓰레기", "음식물쓰레기", "플라스틱류"},
                    {"하수구에 짜서 버림", "통째로 일반쓰레기", "비닐류로 배출"},
                    {"플라스틱류", "일반쓰레기", "고철류"},
                    {"유리수거함에 넣는다", "신문지에 싸서 일반쓰레기", "플라스틱류에 넣는다"},
                    {"종이류", "변기에 버린다", "일반쓰레기"}
            };

            // 3. 각 문제의 정답 번호
            int[] answers = {1, 0, 2, 0, 1, 1, 1, 2};

            // 4. 정답 또는 오답 시 보여줄 해설
            String[] explanations = {
                    "양파/마늘 껍질 등은 동물의 사료로 쓸 수 없어 '일반쓰레기'입니다.",
                    "음식물이 배어 씻어도 지워지지 않는 컵라면 용기는 재활용이 안 돼서 '일반쓰레기'입니다.",
                    "영수증은 열에 반응하는 특수 코팅 종이(감열지)라서 '일반쓰레기'입니다.",
                    "치킨 뼈, 돼지 뼈, 조개 껍데기 등은 딱딱해서 사료로 못 쓰기 때문에 '일반쓰레기'입니다.",
                    "젤 형태의 아이스팩 내용물은 미세플라스틱이라 하수구에 버리면 안 되고 '일반쓰레기'로 버려야 합니다.",
                    "칫솔은 고무 등 여러 재질이 섞인 복합 플라스틱이라 재활용이 안 되어 '일반쓰레기'입니다.",
                    "깨진 유리는 재활용이 불가능하므로, 다치지 않게 신문지에 싸서 '일반쓰레기'로 버려야 합니다.",
                    "물티슈는 종이가 아니라 합성수지(플라스틱) 재질이 포함되어 있어 '일반쓰레기'입니다."
            };

            int qIdx = random.nextInt(questions.length);

            UIManager.put("OptionPane.messageFont", new Font("맑은 고딕", Font.BOLD, 18));
            UIManager.put("OptionPane.buttonFont", new Font("맑은 고딕", Font.PLAIN, 16));

            int choice = JOptionPane.showOptionDialog(
                    this,
                    questions[qIdx],
                    " ♻️ 분리수거 퀴즈",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options[qIdx],
                    options[qIdx][0]
            );

            if (choice == answers[qIdx]) {
                JOptionPane.showMessageDialog(this, "정답입니다! \n" + explanations[qIdx] + "\n\n목숨을 1개 얻고 게임을 다시 시작합니다.");

                lives = 1;
                hasRevived = true;

                trashList.clear();
                itemList.clear();
                spawnCounter = 0;
                itemSpawnCounter = 0;
                hintTicks = 0;
                shieldOn = false;

                gameTimer.start();
            } else {
                JOptionPane.showMessageDialog(this, "오답입니다! \n" + explanations[qIdx] + "\n\n게임을 종료합니다.");

                int confirm = JOptionPane.showConfirmDialog(this,
                        "분리수거에 관한 정보에 대해 알려주는 사이트로 이동하시겠습니까?",
                        "안내",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (confirm == JOptionPane.OK_OPTION) {
                    openRandomInfoWebpage();
                }

                isGameOver = true;
                frame.changePanel(new ResultPanel(frame, score));
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "분리수거에 관한 정보에 대해 알려주는 사이트로 이동하시겠습니까?",
                    "안내",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (confirm == JOptionPane.OK_OPTION) {
                openRandomInfoWebpage();
            }

            isGameOver = true;
            frame.changePanel(new ResultPanel(frame, score));
        }
    }

    private void openRandomInfoWebpage() {
        String[] infoUrls = {
                "https://www.youtube.com/shorts/pHhG5fClttw",
                "https://www.youtube.com/shorts/TJMzCXRylLI?si=DcYoljo-uaRipVIM",
                "https://youtube.com/shorts/UXpXtxaUcN0?si=qdOG4rfLbx_Rudir"
        };

        int randomIdx = random.nextInt(infoUrls.length);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(infoUrls[randomIdx]));
            }
        } catch (Exception ex) {
            System.out.println("웹사이트를 열 수 없습니다.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 🛠️ [수정됨] 기존에 있던 player.y 업데이트 로직은 updateLogic()으로 이동하여 삭제되었습니다.

        Image currentBg = backgroundStages[0];

        if (score >= 300) {
            currentBg = backgroundStages[3];
        } else if (score >= 200) {
            currentBg = backgroundStages[2];
        } else if (score >= 100) {
            currentBg = backgroundStages[1];
        }

        if (currentBg != null) {
            g.drawImage(currentBg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (player != null) {
            player.draw(g);
        }

        for (Trash t : trashList) {
            t.draw(g);
            drawHintBorder(g, t);
        }

        for (GameItem item : itemList) {
            item.draw(g);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(15, 15, 230, 170, 15, 15);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(15, 15, 230, 170, 15, 15);

        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        g2d.setColor(Color.WHITE);

        String koreanType = "";
        switch (selectedType.name()) {
            case "PLASTIC":
                koreanType = "플라스틱";
                break;
            case "CAN":
                koreanType = "캔";
                break;
            case "PAPER":
                koreanType = "종이";
                break;
            case "VINYL":
                koreanType = "비닐";
                break;
            case "GENERAL":
                koreanType = "일반쓰레기";
                break;
            case "GLASS":
                koreanType = "유리병";
                break;
            default:
                koreanType = selectedType.name();
                break;
        }

        String targetText = "수거 종류 : " + koreanType;
        String scoreText  = "현재 점수 : " + score;
        String livesText  = "남은 목숨 : " + lives;
        String speedText  = "낙하 속도 : " + fallSpeed;
        String shieldText = "보호막 : " + (shieldOn ? "ON" : "OFF");
        String hintText = "힌트 : " + (hintTicks > 0 ? ((hintTicks / 50) + 1) + "초" : "OFF");

        g2d.drawString(targetText, 30, 40);
        g2d.drawString(scoreText, 30, 65);
        g2d.drawString(livesText, 30, 90);
        g2d.drawString(speedText, 30, 115);
        g2d.drawString(shieldText, 30, 140);
        g2d.drawString(hintText, 30, 165);
    }

    private void drawHintBorder(Graphics g, Trash trash) {
        if (hintTicks <= 0 || trash.getType() != selectedType) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(trash.x - 4, trash.y - 4, trash.size + 8, trash.size + 8, 12, 12);
        g2.dispose();
    }
}