import javax.swing.*; // Thư viện giao diện đồ họa Swing
import java.awt.*; // Thư viện vẽ đồ họa
import java.awt.event.*; // Thư viện xử lý sự kiện

public class gamaxephinh extends JPanel implements ActionListener, KeyListener {
    final int ROWS = 20, COLS = 10, CELL = 30; // Số hàng, cột, kích thước mỗi ô
    Timer timer; // Đối tượng Timer để điều khiển game loop
    int[][] board = new int[ROWS][COLS]; // Ma trận lưu trạng thái bảng game
    int[][][] shapes = { // Các hình khối Tetris (I, O, T, S, Z, J, L)
        {{1,1,1,1}}, // I
        {{1,1},{1,1}}, // O
        {{0,1,0},{1,1,1}}, // T
        {{0,1,1},{1,1,0}}, // S
        {{1,1,0},{0,1,1}}, // Z
        {{1,0,0},{1,1,1}}, // J
        {{0,0,1},{1,1,1}}  // L
    };
    Color[] colors = {Color.cyan, Color.yellow, Color.magenta, Color.green, Color.red, Color.blue, Color.orange}; // Màu cho từng khối
    int curShape, curX, curY, rot; // Loại khối hiện tại, vị trí x, y, trạng thái xoay
    int[][] curBlock; // Ma trận khối hiện tại
    boolean gameOver = false; // Trạng thái kết thúc game
    int score = 0; // Điểm số

    public gamaxephinh() {
        setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL)); // Đặt kích thước panel
        setBackground(Color.black); // Đặt màu nền
        setFocusable(true); // Cho phép nhận sự kiện bàn phím
        addKeyListener(this); // Đăng ký lắng nghe phím
        spawn(); // Sinh khối đầu tiên
        timer = new Timer(400, this); // Tạo timer với delay 400ms
        timer.start(); // Bắt đầu timer
    }

    void spawn() {
        curShape = (int)(Math.random() * shapes.length); // Chọn ngẫu nhiên loại khối
        curBlock = shapes[curShape]; // Lấy ma trận khối
        rot = 0; // Trạng thái xoay ban đầu
        curY = 0; // Vị trí y ban đầu (trên cùng)
        curX = COLS / 2 - curBlock[0].length / 2; // Vị trí x giữa bảng
        if (!canMove(curBlock, curX, curY)) gameOver = true; // Nếu không đặt được thì thua
    }

    boolean canMove(int[][] block, int nx, int ny) {
        // Kiểm tra khối có thể di chuyển đến vị trí (nx, ny) không
        for (int i = 0; i < block.length; i++)
            for (int j = 0; j < block[0].length; j++)
                if (block[i][j] != 0) {
                    int x = nx + j, y = ny + i;
                    if (x < 0 || x >= COLS || y < 0 || y >= ROWS) return false; // Ra ngoài biên
                    if (board[y][x] != 0) return false; // Đụng khối khác
                }
        return true;
    }

    void merge() {
        // Gắn khối hiện tại vào bảng
        for (int i = 0; i < curBlock.length; i++)
            for (int j = 0; j < curBlock[0].length; j++)
                if (curBlock[i][j] != 0)
                    board[curY + i][curX + j] = curShape + 1; // Đánh dấu ô thuộc khối
    }

    void clearLines() {
        // Xóa các dòng đầy
        for (int i = ROWS - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < COLS; j++)
                if (board[i][j] == 0) full = false; // Nếu còn ô trống thì không xóa
            if (full) {
                score += 100; // Tăng điểm
                for (int k = i; k > 0; k--)
                    board[k] = board[k - 1].clone(); // Dồn các dòng phía trên xuống
                board[0] = new int[COLS]; // Dòng trên cùng rỗng
                i++; // Kiểm tra lại dòng này
            }
        }
    }

    int[][] rotate(int[][] block) {
        // Xoay ma trận khối 90 độ
        int m = block.length, n = block[0].length;
        int[][] res = new int[n][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                res[j][m - 1 - i] = block[i][j];
        return res;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ bảng game
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                if (board[i][j] != 0) {
                    g.setColor(colors[board[i][j] - 1]);
                    g.fillRect(j * CELL, i * CELL, CELL, CELL);
                    g.setColor(Color.black);
                    g.drawRect(j * CELL, i * CELL, CELL, CELL);
                }
        // Vẽ khối hiện tại
        if (!gameOver) {
            g.setColor(colors[curShape]);
            for (int i = 0; i < curBlock.length; i++)
                for (int j = 0; j < curBlock[0].length; j++)
                    if (curBlock[i][j] != 0) {
                        int x = curX + j, y = curY + i;
                        g.fillRect(x * CELL, y * CELL, CELL, CELL);
                        g.setColor(Color.black);
                        g.drawRect(x * CELL, y * CELL, CELL, CELL);
                        g.setColor(colors[curShape]);
                    }
        }
        // Vẽ điểm số
        g.setColor(Color.white);
        g.drawString("Score: " + score, 10, 20);
        // Vẽ chữ Game Over nếu thua
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.red);
            g.drawString("Game Over", 30, getHeight() / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Hàm này được gọi mỗi lần timer chạy
        if (gameOver) return;
        if (canMove(curBlock, curX, curY + 1)) {
            curY++; // Di chuyển khối xuống
        } else {
            merge(); // Gắn khối vào bảng
            clearLines(); // Xóa dòng đầy
            spawn(); // Sinh khối mới
        }
        repaint(); // Vẽ lại màn hình
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Xử lý sự kiện nhấn phím
        if (gameOver) return;
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (canMove(curBlock, curX - 1, curY)) curX--; // Di chuyển trái
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (canMove(curBlock, curX + 1, curY)) curX++; // Di chuyển phải
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (canMove(curBlock, curX, curY + 1)) curY++; // Di chuyển xuống nhanh
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            int[][] rotBlock = rotate(curBlock); // Xoay khối
            if (canMove(rotBlock, curX, curY)) curBlock = rotBlock;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            while (canMove(curBlock, curX, curY + 1)) curY++; // Rơi nhanh xuống đáy
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {} // Không dùng
    @Override
    public void keyTyped(KeyEvent e) {} // Không dùng

    public static void main(String[] args) {
        // Tạo cửa sổ game
        JFrame f = new JFrame("Tetris");
        gamaxephinh panel = new gamaxephinh(); // Tạo panel game
        f.add(panel); // Thêm panel vào frame
        f.pack(); // Đặt kích thước frame vừa panel
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Đóng cửa sổ khi thoát
        f.setLocationRelativeTo(null); // Đặt frame ở giữa màn hình
        f.setVisible(true); // Hiển thị frame
    }
}
