public class Trash {
    int x, y;
    int size;
    TrashType type;

    public Trash(int x, int y, int size, TrashType type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
    }

    public void fall(int speed) {
        y += speed;
    }
}
