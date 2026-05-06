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
}