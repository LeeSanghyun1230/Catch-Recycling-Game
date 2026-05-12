package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultPanel extends JPanel {

    private GameFrame frame;
    private int score;

    private JLabel titleLabel;
    private JLabel scoreLabel;
    private JLabel messageLabel;
    private JButton restartButton;
    private JButton rankingButton;
    private JButton exitButton;

    public ResultPanel(GameFrame frame, int score) {
        // 3번 담당이 게임 종료 후 이 생성자로 점수를 넘겨줘야 함
        this.frame = frame;
        this.score = score;

        setLayout(new BorderLayout());

        titleLabel = new JLabel("게임 결과", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));

        scoreLabel = new JLabel("잡은 쓰레기 수 : " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 22));

        messageLabel = new JLabel(getResultMessage(score), SwingConstants.CENTER);
        messageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1, 10, 10));
        centerPanel.add(titleLabel);
        centerPanel.add(scoreLabel);
        centerPanel.add(messageLabel);

        restartButton = new JButton("다시 시작");
        rankingButton = new JButton("랭킹 보기");
        exitButton = new JButton("종료");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restartButton);
        buttonPanel.add(rankingButton);
        buttonPanel.add(exitButton);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        RankingManager.saveScore(score);

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

    private String getResultMessage(int score) {
        if (score <= 50) {
            return "지구를 지켜내지 못했어요ㅠㅠ";
        } else if (score <= 100) {
            return "조금만 더 노력하면 분리수거 달인!";
        } else {
            return "분리수거 왕이시당~";
        }
    }
}
