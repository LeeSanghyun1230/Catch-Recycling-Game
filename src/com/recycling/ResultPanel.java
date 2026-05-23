package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ResultPanel extends JPanel {

    private GameFrame frame;
    private int score;
    private Image backgroundImage; // ✨ 배경 이미지를 저장할 변수

    private JLabel scoreLabel;
    private JButton restartButton;
    private JButton rankingButton;
    private JButton exitButton;

    public ResultPanel(GameFrame frame, int score) {
        this.frame = frame;
        this.score = score;

        // 1. 점수에 따라 불러올 배경 이미지 경로 설정
        String imagePath = "";
        if (score <= 100) {
            imagePath = "/com/recycling/images/result_sad.png";          // 100점 이하: 슬픈 표정
        } else if (score <= 200) {
            imagePath = "/com/recycling/images/result_determined.png";   // 200점 이하: 결의에 찬 표정
        } else {
            imagePath = "/com/recycling/images/result_happy.png";        // 200점 초과: 행복한 표정
        }

        // 2. 이미지 파일 로드하기
        try {
            URL url = getClass().getResource(imagePath);
            if (url != null) {
                backgroundImage = new ImageIcon(url).getImage();
            } else {
                System.out.println("⚠️ 배경 이미지를 찾을 수 없습니다. 경로를 확인해주세요: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());

        // 1. 점수 라벨 설정 (박스 안에서 잘 보이도록 글자색을 흰색으로 변경)
        scoreLabel = new JLabel("최종 점수 : " + score + "점", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24)); // 크기를 보기 좋게 조절
        scoreLabel.setForeground(Color.WHITE);                   // ✨ 어두운 박스 위에는 흰색 글씨가 가장 잘 보입니다.

        // 2. ✨ 점수를 감싸는 예쁜 반투명 박스 패널 생성
        JPanel scoreBoxPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // 테두리와 글씨를 부드럽게 표현하는 안티앨리어싱
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 반투명한 검은색 박스 그리기 (가로/세로 꽉 차게)
                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // 깔끔한 흰색 테두리 선 그리기
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        scoreBoxPanel.setOpaque(false); // 배경을 투명하게 해야 paintComponent에서 그린 둥근 박스가 보입니다.
        scoreBoxPanel.setLayout(new BorderLayout());

        // 3. ✨ 박스 내부의 여백(Padding) 설정 (위, 왼쪽, 아래, 오른쪽 순서)
        // 이 숫자를 조절하면 글자 주변 박스 크기가 늘어나거나 줄어듭니다.
        scoreBoxPanel.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        // 박스 패널 안에 점수 글자 넣기
        scoreBoxPanel.add(scoreLabel, BorderLayout.CENTER);

        // 4. 메인 센터 패널 설정
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setOpaque(false);

        // 최종적으로 scoreLabel 대신, 예쁘게 꾸민 scoreBoxPanel을 추가합니다!
        centerPanel.add(scoreBoxPanel);

        restartButton = new JButton("다시 시작");
        rankingButton = new JButton("랭킹 보기");
        exitButton = new JButton("종료");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);               // ✨ 중요: 버튼 패널도 투명하게 설정
        buttonPanel.add(restartButton);
        buttonPanel.add(rankingButton);
        buttonPanel.add(exitButton);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 기존 랭킹 저장 로직 유지
        RankingManager.saveScore(score);

        // 버튼 이벤트 리스너 연동 유지
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.changePanel(new StartPanel(frame));
            }
        });

        rankingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.changePanel(new RankingPanel(frame));
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // 4. ✨ 배경그림을 찌그러지지 않게 정해진 크기로 조절하여 화면 중앙에 그리는 메소드
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 그림 주변의 여백 공간을 원하는 색상으로 채우고 싶다면 아래 두 줄의 주석(//)을 지워주세요.
        // g.setColor(new Color(245, 245, 245)); // 연한 회색 배경 예시
        // g.fillRect(0, 0, getWidth(), getHeight());

        if (backgroundImage != null) {
            // 👇👇👇 [원하는 크기로 직접 조절하는 부분] 👇👇👇
            // 이미지가 너무 크거나 작으면 아래 숫자(가로, 세로 픽셀)를 원하는 대로 수정하세요!
            int imgWidth = 450;  // 가로 크기
            int imgHeight = 650; // 세로 크기
            // 👆👆👆 ---------------------------------------- 👆👆👆

            // 화면의 정중앙 좌표를 계산하는 공식
            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;

            // 계산된 중앙 위치(x, y)에 설정한 크기(imgWidth, imgHeight)로 이미지를 그립니다.
            g.drawImage(backgroundImage, x, y, imgWidth, imgHeight, this);
        }
    }
}