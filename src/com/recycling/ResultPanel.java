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

        // 1. 점수 라벨 설정
        scoreLabel = new JLabel("최종 점수 : " + score + "점", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);

        // 2. 점수를 감싸는 반투명 박스 패널 생성
        JPanel scoreBoxPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };

        scoreBoxPanel.setOpaque(false);
        scoreBoxPanel.setLayout(new BorderLayout());
        scoreBoxPanel.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        scoreBoxPanel.add(scoreLabel, BorderLayout.CENTER);

        // 3. 메인 센터 패널 설정
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(scoreBoxPanel);

        restartButton = new JButton("다시 시작");
        rankingButton = new JButton("랭킹 보기");
        exitButton = new JButton("종료");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(restartButton);
        buttonPanel.add(rankingButton);
        buttonPanel.add(exitButton);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 닉네임 입력 후 Firebase 랭킹 저장
        String nickname = JOptionPane.showInputDialog(
                this,
                "랭킹에 등록할 닉네임을 입력하세요.",
                "닉네임 입력",
                JOptionPane.PLAIN_MESSAGE
        );

        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "익명";
        }

        RankingManager.saveScore(nickname, score);

        // 버튼 이벤트 리스너
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            int imgWidth = 450;
            int imgHeight = 650;

            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;

            g.drawImage(backgroundImage, x, y, imgWidth, imgHeight, this);
        }
    }
}