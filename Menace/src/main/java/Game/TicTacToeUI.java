package Game;

import DataStructure.FileUtility;
import Layouts.Evaluate;
import Layouts.MatrixManipulation;
import Logs.Log;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class TicTacToeUI extends JFrame {

    public JLabel statsLabel;
    private JSlider slider;
    private JButton oButton, xButton;
    private Board board;
    private int lineThickness = 12;
    private Color oColor = Color.BLUE, xColor = Color.RED;
    static final char BLANK = '.', O = 'O', X = 'X';
    private char position[] = {  // Board position (BLANK, O, or X)
            BLANK, BLANK, BLANK,
            BLANK, BLANK, BLANK,
            BLANK, BLANK, BLANK};
    HashMap<String, String> uniqueLayouts;
    HashMap<String, int[]> mainLayouts;
    HashMap<String, Integer> stats = FileUtility.readStatsFromFile();
    ;
    MatrixManipulation m;
    Evaluate e;
    int moveCount = 0;
    HashMap<String, Integer> menaceMoves;
    String currentLayout = ".........";
    int won = stats.get("won"), lost = stats.get("lost"), draw = stats.get("draw");

    // Start the game
    public static void main(String args[]) throws IOException {
        new TicTacToeUI();


    }

    // Initialize
    public TicTacToeUI() throws IOException {

        super("Play Against Menace");
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        statsLabel = new JLabel("Menace Stats  WIN: " + won + "  LOST: " + lost + "  Draw: " + draw);
        statsLabel.setSize(15, 20);
        topPanel.add(statsLabel);
        add(topPanel, BorderLayout.AFTER_LAST_LINE);
        add(board = new Board(), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setVisible(true);

    }


    // Board is what actually plays and displays the game
    private class Board extends JPanel implements MouseListener {
        private Random random = new Random();
        private int rows[][] = {{0, 2}, {3, 5}, {6, 8}, {0, 6}, {1, 7}, {2, 8}, {0, 8}, {2, 6}};

        public Board() throws IOException {
            addMouseListener(this);
            setInitialLayout(currentLayout);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2d = (Graphics2D) g;

            // Draw the grid
            g2d.setPaint(Color.WHITE);
            g2d.fill(new Rectangle2D.Double(0, 0, w, h));
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(lineThickness));
            g2d.draw(new Line2D.Double(0, h / 3, w, h / 3));
            g2d.draw(new Line2D.Double(0, h * 2 / 3, w, h * 2 / 3));
            g2d.draw(new Line2D.Double(w / 3, 0, w / 3, h));
            g2d.draw(new Line2D.Double(w * 2 / 3, 0, w * 2 / 3, h));

            // Draw the Os and Xs
            for (int i = 0; i < 9; ++i) {
                double xpos = (i % 3 + 0.5) * w / 3.0;
                double ypos = (i / 3 + 0.5) * h / 3.0;
                double xr = w / 8.0;
                double yr = h / 8.0;
                if (position[i] == O) {
                    g2d.setPaint(oColor);
                    g2d.draw(new Ellipse2D.Double(xpos - xr, ypos - yr, xr * 2, yr * 2));
                } else if (position[i] == X) {
                    g2d.setPaint(xColor);
                    g2d.draw(new Line2D.Double(xpos - xr, ypos - yr, xpos + xr, ypos + yr));
                    g2d.draw(new Line2D.Double(xpos - xr, ypos + yr, xpos + xr, ypos - yr));
                }
            }
        }

        //To set the initial Layout of the Menace
        void setInitialLayout(String currentLayout) throws IOException {
            try {
                e = new Evaluate();
                m = new MatrixManipulation();
                mainLayouts = TicTacToe.readFromFile();
                uniqueLayouts = TicTacToe.getUniqueLayouts();
                moveCount = 0;
                menaceMoves = new HashMap<>();
                currentLayout = ".........";
                currentLayout = TicTacToe.meanceTurn(currentLayout, uniqueLayouts, mainLayouts, m, menaceMoves);
                Log.gameLog("MenaceMove: " + currentLayout);
                setLayout(currentLayout);
                repaint();
            } catch (Exception ex) {
                System.out.println("Exception in Class TicTacToeUI: " + ex);
            }
        }


        // Draw an X where the mouse is clicked
        public void mouseClicked(MouseEvent e) {

            try {
                int xpos = e.getX() * 3 / getWidth();
                int ypos = e.getY() * 3 / getHeight();
                int pos = xpos + 3 * ypos;
                if (pos >= 0 && pos < 9 && position[pos] == BLANK) {
                    position[pos] = X;
                    moveCount++;
                    repaint();
                    currentLayout = getStringLayout(position);
                    Log.gameLog("HumanMove: " + currentLayout);
                    if (moveCount >= 3 && TicTacToe.checkForWinner(m, currentLayout, menaceMoves, mainLayouts)) {
                        char winner = Evaluate.getFinalWinner(m.getMatrixLayout(currentLayout));
                        newGame(winner);
                        return;
                    }
                    if (moveCount <= 3) {
                        // Menace plays
                        currentLayout = TicTacToe.meanceTurn(currentLayout, uniqueLayouts, mainLayouts, m, menaceMoves);
                        setLayout(currentLayout);
                        Log.gameLog("MenaceMove: " + currentLayout);
                        if (moveCount >= 2 && TicTacToe.checkForWinner(m, currentLayout, menaceMoves, mainLayouts)) {

                            char winner = Evaluate.getFinalWinner(m.getMatrixLayout(currentLayout));
                            newGame(winner);
                            return;
                        }
                        repaint();
                    } else {

                        StringBuilder sb = new StringBuilder(currentLayout);
                        for (int i = 0; i < sb.length(); i++) {
                            char c = sb.charAt(i);
                            if (c == '.') {
                                position[i] = O;
                                sb.setCharAt(i, 'O');
                                repaint();
                            }
                        }
                        Log.gameLog("MenaceMove: " + sb.toString());
                        if (!TicTacToe.checkForWinner(m, sb.toString(), menaceMoves, mainLayouts)) {
                            Log.gameLog("Match Draw");
                            newGame(BLANK);
                            TicTacToe.updateMenace(menaceMoves, mainLayouts, '.');
                        } else {
                            char winner = Evaluate.getFinalWinner(m.getMatrixLayout(sb.toString()));
                            if (winner == 'O')
                                Log.gameLog("Menace Won the Game");
                            else
                                Log.gameLog("Human Won the Game");
                            newGame(winner);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Exception in Class TicTacToeUI: " + ex);
            }
        }

        // Ignore other mouse events
        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }


        String getStringLayout(char[] position) {
            try {
                StringBuilder sb = new StringBuilder();

                for (char c : position) {
                    sb.append(c);
                }
                return sb.toString();
            } catch (Exception ex) {
                System.out.println("Exception in Class TicTacToeUI: " + ex);
            }
            return "";
        }


        //Method to convert Layout from String to arrayFormat
        void setLayout(String str) {
            try {
                for (int i = 0; i < 9; i++) {
                    if (str.charAt(i) == BLANK) {
                        position[i] = BLANK;
                    }
                    if (str.charAt(i) == O) {
                        position[i] = O;
                    }
                    if (str.charAt(i) == X) {
                        position[i] = X;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Exception in Class TicTacToeUI: " + ex);
            }
        }

        //Method to write Stats to the Stats.txt File
        void writeStats() {
            try {
                stats.put("won", won);
                stats.put("lost", lost);
                stats.put("draw", draw);
                FileUtility.writeStatsToFile(stats);
            } catch (Exception ex) {
                System.out.println("Exception in Class TicTacToeUI: " + ex);
            }
        }

        // Start a new game
        void newGame(char winner) {
            repaint();

            // Announce result of last game.  Ask user to play again.
            String result;
            if (winner == 'O') {
                result = "Menace Win!";
                won++;
                statsLabel.setText("Menace Stats  WIN: " + won + "  LOST: " + lost + "  Draw: " + draw);
                writeStats();

            } else if (winner == 'X') {
                lost++;
                result = "You Win!";
                statsLabel.setText("Menace Stats  WIN: " + won + "  LOST: " + lost + "  Draw: " + draw);
                writeStats();
            } else {
                result = "DRAW";
                draw++;
                statsLabel.setText("Menace Stats  WIN: " + won + "  LOST: " + lost + "  Draw: " + draw);
                writeStats();
            }
            if (JOptionPane.showConfirmDialog(null, result + "  " +
                    "Play again?", result, JOptionPane.YES_NO_OPTION)
                    != JOptionPane.YES_OPTION) {
                writeStats();
                System.exit(0);

            }

            // Clear the board to start a new game
            for (int j = 0; j < 9; ++j)
                position[j] = BLANK;
            try {
                setInitialLayout(".........");
            } catch (Exception exception) {
            }
        }
    } // end inner class Board
} // end class TicTacToe

