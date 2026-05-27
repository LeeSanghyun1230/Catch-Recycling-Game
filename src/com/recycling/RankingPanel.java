package com.recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RankingPanel extends JPanel {

    private GameFrame frame;
    private JTextArea rankingArea;
    private JButton backButton;

    // ✨ 1. 배경 이미지를 담을 변수 추가
    private Image backgroundImage;

    public RankingPanel(GameFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());

        // ✨ 2. 배경 이미지 불러오기
        String bgPath = "/com/recycling/images/bg_ranking.png";
        try {
            backgroundImage = new ImageIcon(getClass().getResource(bgPath)).getImage();
        } catch (Exception e) {
            System.out.println("⚠️ 랭킹 배경 이미지를 찾을 수 없습니다: " + bgPath);
        }

        JLabel titleLabel = new JLabel("명예의 전당", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE); // ✨ 배경에 묻히지 않게 흰색 텍스트로 변경

        rankingArea = new JTextArea();
        rankingArea.setEditable(false);
        rankingArea.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 가독성을 위해 BOLD 처리
        rankingArea.setForeground(Color.WHITE); // ✨ 텍스트 흰색

        // ✨ 3. JTextArea의 배경을 투명하게 설정
        rankingArea.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(rankingArea);
        // ✨ 4. JScrollPane(스크롤)과 그 안의 Viewport까지 투명하게 설정해야 배경이 뚫려 보입니다!
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null); // 지저분한 테두리 선 제거

        backButton = new JButton("뒤로 가기");
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));

        // ✨ 화면 외곽 여백(Margin)을 주어서 컴포넌트들이 화면 끝에 너무 딱 붙지 않게 설정
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

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
        List<RankingManager.RankingEntry> rankings = RankingManager.loadRankingEntries();

        if (rankings.isEmpty()) {
            rankingArea.setText("\n\n\t아직 저장된 랭킹이 없습니다.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n"); // 위쪽 여백

        int rank = 1;

        for (RankingManager.RankingEntry entry : rankings) {
            // ✨ 콘솔창처럼 딱딱하게 붙어있지 않게 탭(\t)과 줄바꿈(\n)으로 간격 조절
            sb.append("\t")
                    .append(rank)
                    .append("등 : ")
                    .append(entry.getNickname())
                    .append(" - ")
                    .append(entry.getScore())
                    .append("점\n\n");

            rank++;

            if (rank > 10) {
                break;
            }
        }

        rankingArea.setText(sb.toString());
    }

    // ✨ 5. 배경 이미지와 반투명 검은 박스를 그리는 메서드 오버라이딩
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. 배경 이미지 가장 밑에 깔기
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 이미지 오류 시 기본 어두운 배경
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. 글씨가 잘 보이도록 반투명한 검은색 둥근 박스를 위에 그리기
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0, 150)); // Alpha(투명도)값이 150인 검은색
        // 여백을 뺀 중앙 위치에 박스 그리기
        g2d.fillRoundRect(30, 70, getWidth() - 60, getHeight() - 150, 20, 20);
    }
}