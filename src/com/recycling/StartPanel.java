package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPanel extends JPanel {

    private GameFrame frame; // 메인 프레임을 조종하기 위한 변수

    public StartPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // 상단 제목 텍스트 설정
        JLabel label = new JLabel("플레이할 캐릭터(쓰레기 종류)를 선택하세요!", SwingConstants.CENTER);
        label.setFont(new Font("나눔고딕", Font.BOLD, 20));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // 위아래 여백
        add(label, BorderLayout.NORTH);

        // 버튼들을 담을 패널 설정 (2x2 격자 모양)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 10, 10));

        // 화면에 보여줄 버튼 한글 이름들
        String[] characters = {"일반쓰레기", "비닐", "캔", "플라스틱"};

        for (String name : characters) {

            // 1. 한글 버튼 이름에 맞춰서 영어 이미지 파일 이름을 짝지어줍니다.
            String fileName = "";
            switch (name) {
                case "일반쓰레기": fileName = "player_general.png"; break;
                case "비닐":       fileName = "player_vinyl.png"; break;
                case "캔":         fileName = "player_can.png"; break;
                case "플라스틱":   fileName = "player_plastic.png"; break;
            }

            // 2. 완성된 파일 이름으로 안전한 경로를 만듭니다. (images 폴더 경로 적용)
            String imagePath = "src/com/recycling/images/" + fileName;

            // 3. 이미지 불러오기 및 크기 조절 (100x100)
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaledImg = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(scaledImg);

            // 4. 버튼 생성 (텍스트와 이미지를 동시에 넣기)
            JButton button = new JButton(name, finalIcon);
            button.setFont(new Font("나눔고딕", Font.BOLD, 18));

            // 5. 버튼 디자인 설정 (글자를 이미지 아래로, 배경은 흰색)
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false); // 클릭 시 생기는 못생긴 점선 테두리 제거

            // 6. 버튼 클릭 시 넘어가는 액션 설정
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startGame(name);
                }
            });

            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    // 버튼을 눌렀을 때 실행되는 핵심 로직 (StartPanel -> GamePanel 패널 교체)
    private void startGame(String characterName) {
        // 상현님의 GamePanel이 알아들을 수 있도록 글자를 숫자로 번역합니다.
        int type = 0;
        switch (characterName) {
            case "일반쓰레기": type = 0; break;
            case "비닐":       type = 1; break;
            case "캔":         type = 2; break;
            case "플라스틱":   type = 3; break;
        }

        // 완성된 숫자를 들고 다음 화면(GamePanel)으로 넘어갑니다!
        frame.changePanel(new GamePanel(frame, type));
    }
}