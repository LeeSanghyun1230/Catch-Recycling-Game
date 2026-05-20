package com.recycling;

import java.awt.*;
import javax.swing.ImageIcon;

public class Trash {
    int x, y;
    int size;
    TrashType type;

    // [추가] 쓰레기 이미지를 저장할 변수
    private Image image;

    public Trash(int x, int y, int size, TrashType type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;

        // [추가] 쓰레기 타입에 맞는 이미지 경로 만들기
        // 예: CAN이면 trash_can.png
        String imagePath = "/com/recycling/images/trash_"
                + type.name().toLowerCase() + ".png";

        // [추가] 이미지 파일을 불러와서 image 변수에 저장
        image = new ImageIcon(getClass().getResource(imagePath)).getImage();
    }

    public void fall(int speed) {
        y += speed;
    }

    // 충돌 판정을 위한 사각형 영역 반환
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    // 점수 계산을 위해 쓰레기 종류(Type) 반환
    public TrashType getType() {
        return type;
    }

    // 화면에 쓰레기를 그리는 기능
    public void draw(Graphics g) {
        // [수정] 네모 대신 이미지를 그림
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
        } else {
            // [예비] 혹시 이미지가 안 불러와졌을 때만 네모 표시
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, size, size);
        }
    }
}