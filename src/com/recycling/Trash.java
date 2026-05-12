package com.recycling;

import java.awt.*; // Rectangle, Graphics 클래스를 사용하기 위해 추가해야 합니다.

public class Trash {
    int x, y;
    int size;
    TrashType type; // 💡 TrashType은 아마 Enum(열거형)으로 만들어져 있을 것 같네요!

    public Trash(int x, int y, int size, TrashType type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
    }

    public void fall(int speed) {
        y += speed;
    }

    // =======================================================
    // 🚀 상현님의 GamePanel과 연동하기 위해 추가된 부분 🚀
    // =======================================================

    // 1. 충돌 판정을 위한 사각형 영역 반환
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    // 2. 점수 계산을 위해 쓰레기 종류(Type) 반환
    public TrashType getType() {
        return type;
    }

    // 3. 화면에 쓰레기를 그리는 기능
    public void draw(Graphics g) {
        // 임시로 쓰레기를 네모 모양으로 그립니다. (나중에 이미지로 변경 가능)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, size, size);

        // 어떤 쓰레기인지 글씨로 표시 (디버깅용)
        g.setColor(Color.WHITE);
        g.drawString(type.name(), x + 5, y + 20);
    }
}