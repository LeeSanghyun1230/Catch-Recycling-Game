import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartFrame extends JFrame {

    // void main 대신 'public StartFrame()'으로 시작해야 합니다 (생성자)
    public StartFrame() {
        setTitle("쓰레기 잡기 게임 - 캐릭터 선택");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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
                    startGame(selectedCharacter);
                }
            });
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void startGame(String characterName) {
        // 이 부분은 2번 담당자가 GameFrame을 만들어야 작동합니다.
        // 만약 에러가 나면 일단 아래 한 줄을 주석처리(//) 하세요.
        new GameFrame(characterName);
        this.dispose();
    }
}
