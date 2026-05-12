package com.recycling;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingManager {

    private static final String FILE_NAME = "ranking.txt";

    public static void saveScore(int score) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true));
            bw.write(String.valueOf(score));
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("점수 저장 실패");
            e.printStackTrace();
        }
    }

    public static List<Integer> loadScores() {
        List<Integer> scores = new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return scores;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
            String line;

            while ((line = br.readLine()) != null) {
                scores.add(Integer.parseInt(line));
            }

            br.close();
        } catch (IOException e) {
            System.out.println("점수 불러오기 실패");
            e.printStackTrace();
        }

        Collections.sort(scores, Collections.reverseOrder());
        return scores;
    }
}
