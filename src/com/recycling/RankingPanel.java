import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RankingPanel extends JPanel {

    private GameFrame frame;
    private JTextArea rankingArea;
    private JButton backButton;

    public RankingPanel(GameFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("랭킹", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));

        rankingArea = new JTextArea();
        rankingArea.setEditable(false);
        rankingArea.setFont(new Font("맑은 고딕", Font.PLAIN, 20));

        JScrollPane scrollPane = new JScrollPane(rankingArea);

        backButton = new JButton("뒤로 가기");

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        showRanking();

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.changePanel(new StartPanel(frame));
            }
        });
    }

    private void showRanking() {
        List<Integer> scores = RankingManager.loadScores();

        if (scores.isEmpty()) {
            rankingArea.setText("아직 저장된 랭킹이 없습니다.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        int rank = 1;

        for (int score : scores) {
            sb.append(rank).append("등 : ").append(score).append("점\n");
            rank++;

            if (rank > 10) {
                break;
            }
        }

        rankingArea.setText(sb.toString());
    }
}