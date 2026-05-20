package com.recycling;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Player {
    int x, y;
    int width;
    int height;
    int speed;
    TrashType selectedType;

    // 이미지 삽입
    private Image image;

    public Player(int x, int y, int width, int height, int speed, TrashType selectedType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.selectedType = selectedType;

        String imagePath = "/com/recycling/images/player_"
                + selectedType.name().toLowerCase() + ".png";

        image = new ImageIcon(getClass().getResource(imagePath)).getImage();
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

    // 1. 충돌 판정용: 플레이어의 현재 위치와 크기를 사각형(Rectangle)으로 만들어 반환
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // 2. 화면 출력용: GamePanel이 플레이어를 그릴 때 실행되는 메소드
    public void draw(Graphics g) {
        // 파란 네모 대신 이미지를 그림
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // 혹시 이미지가 안 불러와졌을 때만 파란 네모 표시
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }

        // [삭제됨] 캐릭터 위에 글씨를 출력하던 코드 제거
    }
}