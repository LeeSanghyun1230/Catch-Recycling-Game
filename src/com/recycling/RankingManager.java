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

    public static void saveScore(int score) {
        try {
            URL url = new URL(FIREBASE_URL + "/scores.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            String json = "{\"score\":" + score + ",\"time\":" + System.currentTimeMillis() + "}";

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

    public static List<Integer> loadScores() {
        List<Integer> scores = new ArrayList<>();

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
                return scores;
            }

            Pattern pattern = Pattern.compile("\"score\"\\s*:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(json);

            while (matcher.find()) {
                scores.add(Integer.parseInt(matcher.group(1)));
            }

        } catch (Exception e) {
            System.out.println("Firebase 랭킹 불러오기 실패");
            e.printStackTrace();
        }

        Collections.sort(scores, Collections.reverseOrder());
        return scores;
    }
}