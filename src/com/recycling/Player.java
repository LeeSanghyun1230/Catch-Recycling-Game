package com.recycling;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Player {
    int x, y;
    int width;
    int height;
    int speed;
    TrashType selectedType;

    public Player(int x, int y, int width, int height, int speed, TrashType selectedType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.selectedType = selectedType;
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) {
            x = 0;
        }
    }

    public void moveRight(int panelWidth) {
        x += speed;
        if (x > panelWidth - width) {
            x = panelWidth - width;
        }
    }
// =======================================================
    // 🚀 GamePanel과의 완벽한 연동을 위해 추가된 필수 기능들 🚀
    // =======================================================

    // 1. 충돌 판정용: 플레이어의 현재 위치와 크기를 사각형(Rectangle)으로 만들어 반환
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // 2. 화면 출력용: GamePanel이 "플레이어 그려줘!" 할 때 실행되는 메소드
    public void draw(Graphics g) {
        // 플레이어 몸통을 파란색 네모로 그립니다. (나중에 캐릭터 이미지로 바꿀 수 있습니다!)
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);

        // 플레이어 머리 위에 자신이 어떤 쓰레기를 모아야 하는지 글씨로 띄워줍니다.
        g.setColor(Color.WHITE); // 텍스트 색상
        g.setFont(new java.awt.Font("맑은 고딕", java.awt.Font.BOLD, 12));
        g.drawString(selectedType.name() + " 수거자", x, y - 5);
    }
}