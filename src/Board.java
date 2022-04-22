import java.util.Random;

public class Board {
    private Cell [][] cells;
    private int size;
    private int barSize;
    private int mineCount;
    private int currentUncoverMineCount;
    private Random random = new Random();

    public Board(int size, int barSize, int mineCount) {
        this.size = size;
        this.barSize = barSize;
        this.mineCount = mineCount;
        initCell();
        seedMines();
        generateNumbers();
    }

    private void initCell() {
        cells = new Cell[size + barSize][size];
        for (int row = barSize; row < size + barSize; ++row) {
            for (int col = 0; col < size; ++col) {
                cells[row][col] = new Cell();
            }
        }
    }

    private void seedMines() {
        int seeded = 0;
        while (seeded < mineCount) {
            int row = random.nextInt(size) + barSize;
            int col = random.nextInt(size);
            Cell cell = getCell(row, col);
            if (cell.isMine()) {
                continue;
            }
            cell.setMine(true);
            seeded++;
        }
    }

    private void generateNumbers() {
        for (int row = barSize; row < size + barSize; ++row) {
            for (int col = 0; col < size; ++col) {
                Cell cell = getCell(row, col);
                if (cell.isMine()) {
                    continue;
                }

                int [][] pairs = {
                        {-1, -1}, {-1, 0}, {-1, 1},
                        {0, -1}, /* Cell */ {0, 1},
                        {1, -1}, {1, 0}, {1, 1}
                };
                int count = 0;
                for (int [] pair: pairs) {
                    Cell adjacent = getCell(row + pair[0], col + pair[1]);
                    if (adjacent != null && adjacent.isMine()) {
                        count++;
                    }
                }
                cell.setAdjacentMines(count);
            }
        }
    }

    public void uncover(int row, int col) {
        Cell cell = getCell(row, col);
        if (cell == null || !cell.isCovered()) {
            return;
        }
        cell.setCovered(false);
        ++currentUncoverMineCount;
        if (cell.getAdjacentMines() == 0 && !cell.isMine()) {
            int[][] pairs = {
                    {-1, -1}, {-1, 0}, {-1, 1},
                    {0, -1}, /* Cell */ {0, 1},
                    {1, -1}, {1, 0}, {1, 1}
            };
            for (int[] pair : pairs) {
                int x = pair[0];
                int y = pair[1];
                uncover(row + x, col + y);
            }
        }
    }

    public boolean mineUncovered() {
        for (int row = barSize; row < size + barSize; ++row) {
            for (int col = 0; col < size; ++col) {
                Cell cell = getCell(row, col);
                if (cell.isMine() && !cell.isCovered()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWin() {
        // A player wins when all cells that don't contain mines are revealed.
        return currentUncoverMineCount == (size * size) - mineCount;
    }

    public Cell getCell(int row, int col) {
        if (row < barSize || col < 0 || row >= size + barSize || col >= size) {
            return null;
        }
        return cells[row][col];
    }
}
