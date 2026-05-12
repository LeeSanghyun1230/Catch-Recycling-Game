package com.recycling;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("분리수거 게임");
        setSize(800, 600); // 전체 화면 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 프로그램이 시작되면 가장 먼저 StartPanel을 보여줍니다.
        changePanel(new StartPanel(this));
        setVisible(true);
    }

    // 🌟 민욱님이 요청한 화면 전환 공통 메소드
    public void changePanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }
}