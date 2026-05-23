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

    // ✨ [수정] 단일 배경 대신, 4단계 배경 이미지를 담을 배열로 변경
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
    private Timer gameTimer;
    private Random random = new Random();
    private int spawnCounter = 0;

    public GamePanel(GameFrame frame, int typeIndex) {
        this.frame = frame;
        this.selectedType = TrashType.values()[typeIndex];

        // ✨ [수정] 4단계 배경 이미지 미리 불러오기 (bg_stage1 ~ bg_stage4)
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
        fallSpeed = 4 + (score / 50);
    }

    private void gameOver() {
        gameTimer.stop(); // 게임 일시 정지

        if (!hasRevived) {
            // 1. 8개의 퀴즈 문제 목록
            String[] questions = {
                    "💡 [부활 찬스] 양파 껍질, 대파 뿌리의 올바른 배출 방법은?",
                    "💡 [부활 찬스] 국물이 배어 지워지지 않는 컵라면 용기는?",
                    "💡 [부활 찬스] 마트 영수증이나 택배 전표는 어디에 버려야 할까요?",
                    "💡 [부활 찬스] 맛있게 먹고 남은 치킨 뼈와 돼지갈비 뼈는?",
                    "💡 [부활 찬스] 물컹물컹한 젤 형태의 아이스팩 버리는 방법은?",
                    "💡 [부활 찬스] 다 쓴 칫솔은 어떻게 버려야 할까요?",
                    "💡 [부활 찬스] 실수로 깬 유리컵을 버리는 가장 올바른 방법은?",
                    "💡 [부활 찬스] 사용하고 난 물티슈의 올바른 배출 방법은?"
            };

            // 2. 각 문제의 3가지 선택지 (버튼으로 나옴)
            String[][] options = {
                    {"음식물쓰레기", "일반쓰레기", "퇴비로 사용"},                  // 1번 문제 보기
                    {"일반쓰레기", "플라스틱류", "스티로폼류"},                   // 2번 문제 보기
                    {"종이류", "비닐류", "일반쓰레기"},                           // 3번 문제 보기
                    {"일반쓰레기", "음식물쓰레기", "플라스틱류"},                  // 4번 문제 보기
                    {"하수구에 짜서 버림", "통째로 일반쓰레기", "비닐류로 배출"}, // 5번 문제 보기
                    {"플라스틱류", "일반쓰레기", "고철류"},                       // 6번 문제 보기
                    {"유리수거함에 넣는다", "신문지에 싸서 일반쓰레기", "플라스틱류에 넣는다"}, // 7번 문제 보기
                    {"종이류", "변기에 버린다", "일반쓰레기"}                      // 8번 문제 보기
            };

            // 3. 각 문제의 정답 번호 (0번째, 1번째, 2번째 버튼 중 어느 것인지)
            int[] answers = {1, 0, 2, 0, 1, 1, 1, 2};

            // 4. 정답 또는 오답 시 보여줄 해설 (교육 효과!)
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

            // 🎲 0부터 7 사이의 숫자 중 하나를 랜덤으로 뽑습니다!
            int qIdx = random.nextInt(questions.length);


            // 👇👇👇 [여기에 추가!] 팝업창 폰트 크기를 키워서 창 전체를 크게 만드는 마법의 코드 👇👇👇
            UIManager.put("OptionPane.messageFont", new Font("맑은 고딕", Font.BOLD, 18)); // 문제 글씨 크기 (원하는 대로 조절)
            UIManager.put("OptionPane.buttonFont", new Font("맑은 고딕", Font.PLAIN, 16)); // 버튼 글씨 크기
            // 👆👆👆 ------------------------------------------------------------------------ 👆👆👆

            // 선택된 랜덤 문제로 팝업창을 띄웁니다.
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


            // 결과 처리 로직
            if (choice == answers[qIdx]) {
                // 정답을 맞췄을 때
                JOptionPane.showMessageDialog(this, "정답입니다! 🎉\n" + explanations[qIdx] + "\n\n목숨을 1개 얻고 게임을 다시 시작합니다.");
                lives = 1;
                hasRevived = true;
                gameTimer.start(); // 게임 다시 시작
            } else {
                // 틀렸거나 창을 껐을 때
                JOptionPane.showMessageDialog(this, "오답입니다! 😢\n" + explanations[qIdx] + "\n\n게임을 종료합니다.");

                // 👇👇👇 [수정된 부분 1] 확인/취소를 선택할 수 있는 팝업창 띄우기 👇👇👇
                int confirm = JOptionPane.showConfirmDialog(this,
                        "분리수거에 관한 정보에 대해 알려주는 사이트로 이동하시겠습니까?",
                        "안내",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                // 사용자가 [확인]을 눌렀을 때(OK_OPTION)만 사이트를 엽니다!
                if (confirm == JOptionPane.OK_OPTION) {
                    openRandomInfoWebpage();
                }
                // 👆👆👆 ---------------------------------------------------- 👆👆👆

                isGameOver = true;
                frame.changePanel(new ResultPanel(frame, score)); // 결과 창으로 이동
            }
        }
        // 이미 한 번 부활했는데 또 죽은 경우
        else {

            // 👇👇👇 [수정된 부분 2] 부활 못하고 죽었을 때도 똑같이 적용 👇👇👇
            int confirm = JOptionPane.showConfirmDialog(this,
                    "분리수거에 관한 정보에 대해 알려주는 사이트로 이동하시겠습니까?",
                    "안내",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            // 사용자가 [확인]을 눌렀을 때만 사이트를 엽니다!
            if (confirm == JOptionPane.OK_OPTION) {
                openRandomInfoWebpage();
            }
            // 👆👆👆 ---------------------------------------------------- 👆👆👆

            isGameOver = true;
            frame.changePanel(new ResultPanel(frame, score));
        }
    } // 👈 gameOver() 메소드가 끝나는 중괄호

    // 👇👇👇 [새로 추가하는 메소드] 랜덤으로 웹사이트를 열어주는 전용 로직 👇👇👇
    private void openRandomInfoWebpage() {
        // 1. 보여주고 싶은 영상이나 사이트 주소 3개를 목록에 넣습니다. (쌍따옴표 안에 주소를 넣어주세요!)
        String[] infoUrls = {
                "https://www.youtube.com/shorts/pHhG5fClttw", //
                "https://www.youtube.com/shorts/TJMzCXRylLI?si=DcYoljo-uaRipVIM",                             // 두 번째 주소 (원하는 유튜브 영상 링크로 교체!)
                "youtube.com/shorts/UXpXtxaUcN0?si=qdOG4rfLbx_Rudir"                               // 세 번째 주소 (원하는 유튜브 영상 링크로 교체!)
        };

        // 2. 0, 1, 2 중 랜덤으로 숫자 하나를 뽑습니다.
        int randomIdx = random.nextInt(infoUrls.length);

        // 3. 뽑힌 숫자에 해당하는 주소를 인터넷 창으로 엽니다.
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(infoUrls[randomIdx]));
            }
        } catch (Exception ex) {
            System.out.println("웹사이트를 열 수 없습니다.");
        }
    }
    // 👆👆👆 ------------------------------------------------------------- 👆👆👆

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (player != null && getHeight() > 0) {
            // 화면 높이에서 100(캐릭터 크기+여백)을 뺀 위치를 Y좌표로 설정
            player.y = getHeight() - 100;
        }
        // ✨ [추가] 점수에 따라 어떤 배경을 그릴지 결정하는 로직
        Image currentBg = backgroundStages[0]; // 기본 1단계 배경 (쓰레기 가득)

        if (score >= 300) {
            currentBg = backgroundStages[3]; // 4단계: 150점 이상 (엄청 깨끗한 자연)
        } else if (score >= 200) {
            currentBg = backgroundStages[2]; // 3단계: 100점 이상 (많이 깨끗해짐)
        } else if (score >=100 ) {
            currentBg = backgroundStages[1]; // 2단계: 50점 이상 (쓰레기 조금 사라짐)
        }

        // 결정된 배경 이미지를 게임 화면 크기에 맞게 그림
        if (currentBg != null) {
            g.drawImage(currentBg, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 방어 코드: 배경이 없을 때 흰색 바탕
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (player != null) {
            player.draw(g);
        }

        for (Trash t : trashList) {
            t.draw(g);
        }

        // 텍스트 글씨가 배경에 묻히지 않게 하려면 색상을 흰색 등으로 바꾸셔도 좋습니다!
        g.setColor(Color.BLACK);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        g.drawString("Target Type: " + selectedType.name(), 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("Lives: " + lives, 10, 60);
        g.drawString("Speed: " + fallSpeed, 10, 80);
    }
}