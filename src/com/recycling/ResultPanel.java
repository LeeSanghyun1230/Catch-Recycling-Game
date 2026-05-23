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

        // 3. 그림 안에 이미 타이틀과 캐릭터 멘트가 들어가 있으므로,
        // 기존의 titleLabel과 messageLabel은 제외하고 점수판만 중앙에 깔끔하게 띄웁니다.
        scoreLabel = new JLabel("최종 점수 : " + score + "점", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        scoreLabel.setForeground(Color.BLACK); // 글씨가 잘 안 보인다면 다른 색상으로 변경 가능

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout()); // 점수 위치를 화면 정중앙에 이쁘게 잡기 위해 변경
        centerPanel.setOpaque(false);               // ✨ 중요: 패널을 투명하게 해서 배경 그림이 보이도록 함
        centerPanel.add(scoreLabel);

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