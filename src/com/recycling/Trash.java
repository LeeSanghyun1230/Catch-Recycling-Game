package com.recycling;

import java.awt.*;
import javax.swing.ImageIcon;
import java.util.Random; // 랜덤 기능을 위해 추가

public class Trash {
    int x, y;
    int size;
    TrashType type;

    // 쓰레기 이미지와 화면에 띄울 이름을 저장할 변수
    private Image image;
    private String itemName = "";

    public Trash(int x, int y, int size, TrashType type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;

        Random random = new Random();

        // 기본 이미지 파일명 규칙 (예: trash_general)
        String imageFileName = "trash_" + type.name().toLowerCase();

        // 💡 일반쓰레기(인덱스 0번)일 경우
        if (type.ordinal() == 0) {

            // ✨ 추가된 핵심 로직: random.nextInt(10)을 쓰면 0~9까지 숫자가 나옵니다.
            // 즉, 5보다 작을 때(0, 1, 2, 3, 4)만 함정이 나오게 해서 '50% 확률'을 만듭니다!
            if (random.nextInt(10) < 5) {
                String[] trickNames = {
                        "양파껍질", "계란껍질", "오염된 컵라면", "영수증", "치킨뼈",
                        "조개껍데기", "아이스팩", "칫솔", "깨진 유리", "물티슈"
                };
                String[] trickFiles = {
                        "trick_onion", "trick_egg", "trick_noodle", "trick_receipt", "trick_bone",
                        "trick_clam", "trick_icepack", "trick_toothbrush", "trick_glass", "trick_wetwipe"
                };

                int r = random.nextInt(trickNames.length);
                this.itemName = trickNames[r];
                imageFileName = trickFiles[r];
            } else {
                // 나머지 50% 확률일 때는 평범한 일반 쓰레기가 나옵니다.
                // (선택) 평범한 쓰레기 위에도 이름을 띄우고 싶다면 아래 주석을 푸세요!
                // this.itemName = "일반쓰레기";
            }
        }
        // 완성된 이미지 경로 만들기
        String imagePath = "/com/recycling/images/" + imageFileName + ".png";

        // [수정] 이미지가 없을 때 게임이 튕기지 않도록 안전장치(try-catch) 추가
        try {
            image = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.out.println("⚠️ 이미지를 찾을 수 없습니다: " + imagePath);
        }
    }

    public void fall(int speed) {
        y += speed;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public TrashType getType() {
        return type;
    }

    public void draw(Graphics g) {
        // 이미지가 정상적으로 불러와졌을 때
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);

            // 💡 교육 효과 극대화: 함정 아이템(일반쓰레기)일 경우 이미지 위에 이름을 띄워줍니다!
            if (!itemName.isEmpty()) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("맑은 고딕", Font.BOLD, 12));
                // 글씨가 이미지 한가운데를 가리지 않도록 살짝 위(-5)로 올려서 출력
                g.drawString(itemName, x - 5, y - 5);
            }
        } else {
            // 이미지를 못 찾았을 때만 네모 표시
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, size, size);

            // 네모일 때도 이름은 보이게 처리
            g.setColor(Color.WHITE);
            g.drawString(itemName.isEmpty() ? type.name() : itemName, x, y + 20);
        }
    }
}