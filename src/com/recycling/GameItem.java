package com.recycling;

import java.awt.*;
import javax.swing.ImageIcon;

public class GameItem {
    int x, y, size;
    private ItemType type;
    private Image image;

    public GameItem(int x, int y, int size, ItemType type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;

        String imageFileName = "";

        if (type == ItemType.HEART) {
            imageFileName = "heart.png";
        } else if (type == ItemType.HINT) {
            imageFileName = "hint.png";
        } else if (type == ItemType.SHIELD) {
            imageFileName = "shield.png";
        }

        String imagePath = "/com/recycling/images/" + imageFileName;

        try {
            image = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.out.println("아이템 이미지를 찾을 수 없습니다: " + imagePath);
        }
    }

    public void fall(int speed) {
        y += speed;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public ItemType getType() {
        return type;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
        } else {
            // 이미지가 없을 때만 임시 원형 아이템으로 표시
            if (type == ItemType.HEART) {
                g.setColor(Color.PINK);
                g.fillOval(x, y, size, size);
                g.setColor(Color.RED);
                g.drawString("+1", x + 18, y + 32);
            } else if (type == ItemType.HINT) {
                g.setColor(Color.GREEN);
                g.fillOval(x, y, size, size);
                g.setColor(Color.BLACK);
                g.drawString("HINT", x + 10, y + 32);
            } else {
                g.setColor(Color.CYAN);
                g.fillOval(x, y, size, size);
                g.setColor(Color.BLUE);
                g.drawString("SH", x + 18, y + 32);
            }
        }
    }
}
