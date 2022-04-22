import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game extends JFrame {

    private Board board;
    private static final int FLAG_COUNT = 10;
    private int boardSize = 20;
    private int barSize = 2;
    private int mineCount = 10;
    private int flagCount = FLAG_COUNT;
    private GridUI gridUI;
    private static final Color dark_green = new Color(0,102,0);
    private static final Color purple = new Color(102,0,153);
    private static Color[] COLOR_OF_NUMBER = {Color.blue, dark_green, Color.red, Color.black, purple};

    public Game() {
        board = new Board(boardSize, barSize, mineCount);
        gridUI = new GridUI();
        add(gridUI);
        pack();
    }

    public void start() {
        setVisible(true);
    }

    public void restart() {
        board = new Board(boardSize, barSize, mineCount);
        flagCount = FLAG_COUNT;
        repaint();
    }

    class GridUI extends JPanel {
        public static final int CELL_PIXEL_SIZE = 30;

        private Image imageCell;
        private Image imageFlag;
        private Image imageMine;

        private JButton restartButton = new JButton("Restart");

        public GridUI() {
            setPreferredSize(new Dimension(boardSize * CELL_PIXEL_SIZE,
                    (boardSize + barSize) * CELL_PIXEL_SIZE));

            imageCell = new ImageIcon("imgs/Cell.png").getImage();
            imageFlag = new ImageIcon("imgs/Flag.png").getImage();
            imageMine = new ImageIcon("imgs/Mine.png").getImage();

            this.add(restartButton);
            restartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    restart();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);

                    int row = e.getY() / CELL_PIXEL_SIZE;
                    int col = e.getX() / CELL_PIXEL_SIZE;
                    if (row < barSize) {  // to fix error when click on the top
                        return;
                    }

                    Cell cell = board.getCell(row, col);
                    if (!cell.isCovered()) {
                        return;
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (cell.isFlagged()){
                            ++flagCount;
                            cell.setFlagged(!cell.isFlagged());
                        } else if (flagCount > 0) {
                            cell.setFlagged(!cell.isFlagged());
                            --flagCount;
                        }
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        if (!cell.isFlagged()) {
                            board.uncover(row, col);
                            if (board.mineUncovered()) {
                                JOptionPane.showMessageDialog(
                                        Game.this,
                                        "You lose!",
                                        "You hit the mine",
                                        JOptionPane.WARNING_MESSAGE
                                );
                            }
                        }
                    }

                    repaint();

                    if (board.isWin()) {
                        JOptionPane.showMessageDialog(
                                Game.this,
                                "Congrats! You are excellent.",
                                "You win!",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            });
        }

        @Override
        public void paint(Graphics graphics) {
            super.paint(graphics);
            graphics.drawImage(imageFlag, (boardSize * CELL_PIXEL_SIZE / 2) - 25,
                    barSize * CELL_PIXEL_SIZE / 6 + 20, CELL_PIXEL_SIZE,
                    CELL_PIXEL_SIZE, null, null);
            graphics.drawString(flagCount + "",
                    boardSize * CELL_PIXEL_SIZE / 2,
                    barSize * CELL_PIXEL_SIZE / 2 + 20);
            for (int row = barSize; row < boardSize + barSize; ++row) {
                for (int col = 0; col < boardSize; ++col) {
                    paintCell(graphics, row, col);
                }
            }
        }

        private void paintCell(Graphics graphics, int row, int col) {
            int x = col * CELL_PIXEL_SIZE;
            int y = (row * CELL_PIXEL_SIZE);

            Cell cell = board.getCell(row, col);

            if (cell.isCovered()) {
                graphics.drawImage(imageCell, x, y, CELL_PIXEL_SIZE,
                        CELL_PIXEL_SIZE, null, null);
            } else {
                graphics.setColor(Color.gray);
                graphics.fillRect(x, y, CELL_PIXEL_SIZE, CELL_PIXEL_SIZE);
                graphics.setColor(Color.lightGray);
                graphics.fillRect(x + 1, y + 1, CELL_PIXEL_SIZE - 2, CELL_PIXEL_SIZE - 2);

                if (cell.isMine()) {
                    graphics.drawImage(imageMine, x, y, CELL_PIXEL_SIZE,
                            CELL_PIXEL_SIZE, null, null);
                } else if (cell.getAdjacentMines() > 0) {
                    if (cell.getAdjacentMines() < COLOR_OF_NUMBER.length) {
                        graphics.setColor(COLOR_OF_NUMBER[cell.getAdjacentMines() - 1]);
                    } else {
                        graphics.setColor(COLOR_OF_NUMBER[COLOR_OF_NUMBER.length - 1]);
                    }
                    graphics.drawString(cell.getAdjacentMines() + "",
                            x + (int) (CELL_PIXEL_SIZE * 0.35),
                            y + (int) (CELL_PIXEL_SIZE * 0.6));
                }
            }

            if (cell.isFlagged()) {
                graphics.drawImage(imageFlag, x, y, CELL_PIXEL_SIZE,
                        CELL_PIXEL_SIZE, null, null);
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}