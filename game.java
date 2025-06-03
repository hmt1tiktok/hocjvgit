import javax.swing.*; // Thư viện giao diện đồ họa Swing
import java.awt.*; // Thư viện vẽ đồ họa
import java.awt.event.*; // Thư viện xử lý sự kiện
import java.util.Random; // Thư viện tạo số ngẫu nhiên

public class game extends JPanel implements ActionListener, KeyListener {
    static final int WIDTH = 400; // Chiều rộng cửa sổ game (pixel)
    static final int HEIGHT = 200; // Chiều cao cửa sổ game (pixel)
    static final int UNIT_SIZE = 20; // Kích thước mỗi ô (pixel)
    static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE); // Số ô tối đa
    static final int DELAY = 120; // Thời gian delay giữa các frame (ms)

    final int x[] = new int[GAME_UNITS]; // Mảng lưu vị trí x của từng phần rắn
    final int y[] = new int[GAME_UNITS]; // Mảng lưu vị trí y của từng phần rắn
    int bodyParts = 3; // Độ dài ban đầu của rắn
    int foodX, foodY; // Vị trí mồi
    int score = 0; // Điểm số
    char direction = 'R'; // Hướng di chuyển ban đầu (Right)
    boolean running = false; // Trạng thái game
    Timer timer; // Đối tượng Timer để điều khiển game loop
    Random random; // Đối tượng Random để sinh mồi

    public game() {
        random = new Random(); // Khởi tạo đối tượng Random
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // Đặt kích thước panel
        this.setBackground(Color.black); // Đặt màu nền panel
        this.setFocusable(true); // Cho phép nhận sự kiện bàn phím
        this.addKeyListener(this); // Đăng ký lắng nghe phím
        startGame(); // Bắt đầu game
    }

    public void startGame() {
        newFood(); // Sinh mồi mới
        running = true; // Đặt trạng thái game đang chạy
        timer = new Timer(DELAY, this); // Tạo timer với delay
        timer.start(); // Bắt đầu timer
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Vẽ lại panel
        draw(g); // Gọi hàm vẽ game
    }

    public void draw(Graphics g) {
        if (running) {
            // Vẽ mồi
            g.setColor(Color.red);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            // Vẽ rắn
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green); // Đầu rắn màu xanh lá
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0)); // Thân rắn màu xanh đậm
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // Vẽ điểm số
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + score, 10, 20);
        } else {
            gameOver(g); // Nếu thua thì vẽ màn hình kết thúc
        }
    }

    public void newFood() {
        // Sinh vị trí mồi ngẫu nhiên theo lưới
        foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        // Di chuyển từng phần thân rắn theo phần trước nó
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        // Di chuyển đầu rắn theo hướng
        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE; // Lên
                break;
            case 'D':
                y[0] += UNIT_SIZE; // Xuống
                break;
            case 'L':
                x[0] -= UNIT_SIZE; // Trái
                break;
            case 'R':
                x[0] += UNIT_SIZE; // Phải
                break;
        }
    }

    public void checkFood() {
        // Nếu đầu rắn trùng vị trí mồi
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++; // Tăng độ dài rắn
            score += 10; // Tăng điểm
            newFood(); // Sinh mồi mới
        }
    }

    public void checkCollisions() {
        // Kiểm tra đầu rắn chạm thân
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false; // Kết thúc game
            }
        }
        // Kiểm tra đầu rắn chạm tường
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false; // Kết thúc game
        }
        if (!running) {
            timer.stop(); // Dừng timer nếu thua
        }
    }

    public void gameOver(Graphics g) {
        // Vẽ chữ Game Over
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Game Over", WIDTH / 2 - 80, HEIGHT / 2);
        // Vẽ điểm số
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, WIDTH / 2 - 40, HEIGHT / 2 + 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Hàm này được gọi mỗi lần timer chạy
        if (running) {
            move(); // Di chuyển rắn
            checkFood(); // Kiểm tra ăn mồi
            checkCollisions(); // Kiểm tra va chạm
        }
        repaint(); // Vẽ lại màn hình
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Xử lý sự kiện nhấn phím mũi tên để đổi hướng
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {} // Không dùng
    @Override
    public void keyTyped(KeyEvent e) {} // Không dùng

    public static void main(String[] args) {
        // Tạo cửa sổ game
        JFrame frame = new JFrame("Snake Game");
        game gamePanel = new game(); // Tạo panel game
        frame.add(gamePanel); // Thêm panel vào frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Đóng cửa sổ khi thoát
        frame.pack(); // Đặt kích thước frame vừa panel
        frame.setLocationRelativeTo(null); // Đặt frame ở giữa màn hình
        frame.setVisible(true); // Hiển thị frame
    }
}
