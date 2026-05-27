package com.recycling;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RankingManager {

    private static final String FIREBASE_URL =
            "https://catch-recycling-game-bb5c1-default-rtdb.asia-southeast1.firebasedatabase.app";

    public static class RankingEntry {
        private String nickname;
        private int score;

        public RankingEntry(String nickname, int score) {
            this.nickname = nickname;
            this.score = score;
        }

        public String getNickname() {
            return nickname;
        }

        public int getScore() {
            return score;
        }
    }

    public static void saveScore(String nickname, int score) {
        try {
            URL url = new URL(FIREBASE_URL + "/scores.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            nickname = nickname.replace("\\", "").replace("\"", "").trim();

            if (nickname.isEmpty()) {
                nickname = "익명";
            }

            String json = "{\"nickname\":\"" + nickname + "\",\"score\":" + score + ",\"time\":" + System.currentTimeMillis() + "}";

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.out.println("Firebase 점수 저장 실패: " + responseCode);
            }

            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Firebase 점수 저장 실패");
            e.printStackTrace();
        }
    }

    public static void saveScore(int score) {
        saveScore("익명", score);
    }

    public static List<RankingEntry> loadRankingEntries() {
        List<RankingEntry> rankings = new ArrayList<>();

        try {
            URL url = new URL(FIREBASE_URL + "/scores.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8")
            );

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            conn.disconnect();

            String json = sb.toString();

            if (json.equals("null") || json.isEmpty()) {
                return rankings;
            }

            Pattern objectPattern = Pattern.compile("\\{[^{}]*\\}");
            Matcher objectMatcher = objectPattern.matcher(json);

            while (objectMatcher.find()) {
                String object = objectMatcher.group();

                Pattern scorePattern = Pattern.compile("\"score\"\\s*:\\s*(\\d+)");
                Matcher scoreMatcher = scorePattern.matcher(object);

                if (scoreMatcher.find()) {
                    int score = Integer.parseInt(scoreMatcher.group(1));

                    String nickname = "익명";
                    Pattern nicknamePattern = Pattern.compile("\"nickname\"\\s*:\\s*\"([^\"]*)\"");
                    Matcher nicknameMatcher = nicknamePattern.matcher(object);

                    if (nicknameMatcher.find()) {
                        nickname = nicknameMatcher.group(1);
                    }

                    rankings.add(new RankingEntry(nickname, score));
                }
            }

        } catch (Exception e) {
            System.out.println("Firebase 랭킹 불러오기 실패");
            e.printStackTrace();
        }

        Collections.sort(rankings, (a, b) -> b.getScore() - a.getScore());
        return rankings;
    }

    public static List<Integer> loadScores() {
        List<Integer> scores = new ArrayList<>();

        for (RankingEntry entry : loadRankingEntries()) {
            scores.add(entry.getScore());
        }

        Collections.sort(scores, Collections.reverseOrder());
        return scores;
    }
}
