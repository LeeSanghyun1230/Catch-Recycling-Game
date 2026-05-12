package com.recycling; // 패키지 이름은 팀 프로젝트에 맞게 확인해 주세요!

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 1. JFrame이 아니라 JPanel을 상속받도록 수정했습니다.
public class StartPanel extends JPanel {

    private GameFrame frame; // 메인 프레임을 조종하기 위한 변수

    // 2. 민욱님이 요청하신 정확한 생성자 형식입니다!
    public StartPanel(GameFrame frame) {
        this.frame = frame;

        // setTitle, setSize, setDefaultCloseOperation 등은 GameFrame에서 하므로 삭제했습니다.
        setLayout(new BorderLayout());

        JLabel label = new JLabel("플레이할 캐릭터(쓰레기 종류)를 선택하세요!", SwingConstants.CENTER);
        label.setFont(new Font("나눔고딕", Font.BOLD, 20));
        add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 10, 10));

        String[] characters = {"일반쓰레기", "비닐", "캔", "플라스틱"};

        for (String name : characters) {
            JButton button = new JButton(name);
            button.setFont(new Font("나눔고딕", Font.PLAIN, 18));

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedCharacter = e.getActionCommand();
                    startGame(selectedCharacter); // 클릭 시 아래 startGame 실행
                }
            });
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    // 3. 버튼을 눌렀을 때 상현님의 GamePanel로 넘겨주는 핵심 로직입니다.
    private void startGame(String characterName) {
        // 상현님 코드는 숫자를 받으므로(int selectedType), 글자를 숫자로 바꿔줍니다.
        int type = 0;
        switch (characterName) {
            case "일반쓰레기": type = 0; break;
            case "비닐": type = 1; break;
            case "캔": type = 2; break;
            case "플라스틱": type = 3; break;
        }

        // 민욱님이 제안한 방식대로 패널 교체! (StartPanel -> GamePanel)
        frame.changePanel(new GamePanel(frame, type));
    }
}