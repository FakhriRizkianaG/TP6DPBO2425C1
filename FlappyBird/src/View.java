import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class View extends JPanel {

    int width = 360;
    int height = 640;

    private Logic logic;

    // UI elements
    private JLabel scoreLabel;
    private JPanel overlayPanel;
    private JPanel translucentBg;
    private JLabel overlayMessage;
    private boolean isGameOverOverlay = false;

    public View(Logic logic){
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);
        setLayout(null);

        // Skor label (pojok kiri atas)
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setBounds(10, 10, 200, 30);
        add(scoreLabel);

        // --- Overlay (Menu awal dan Game Over) ---
        overlayPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                // semi transparan sedikit gelap
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 120)); // sedikit gelap saja
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setBounds(0, 0, width, height);

        add(overlayPanel);
        setupStartMenu();

        setFocusable(true);
        addKeyListener(logic);
        overlayPanel.setVisible(true);
    }

    /** Menampilkan menu awal (Play / Exit) */
    private void setupStartMenu(){
        overlayPanel.removeAll();
        isGameOverOverlay = false;

        JLabel title = new JLabel("Flappy Bird", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 180, width, 40);
        overlayPanel.add(title);

        JButton playBtn = new JButton("Play");
        playBtn.setFont(new Font("Arial", Font.BOLD, 16));
        playBtn.setBounds(width/2 - 60, 250, 120, 40);
        playBtn.addActionListener(e -> {
            overlayPanel.setVisible(false);
            requestFocusInWindow();
            logic.startGame();
        });
        overlayPanel.add(playBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        exitBtn.setBounds(width/2 - 60, 300, 120, 40);
        exitBtn.addActionListener(e -> System.exit(0));
        overlayPanel.add(exitBtn);
    }

    /** Menampilkan overlay Game Over (pesan, skor, tombol restart / quit) */
    public void showGameOverOverlay(int finalScore){
        overlayPanel.removeAll();
        isGameOverOverlay = true;

        JLabel overLabel = new JLabel("Skill Issue!", SwingConstants.CENTER);
        overLabel.setFont(new Font("Arial", Font.BOLD, 28));
        overLabel.setForeground(Color.WHITE);
        overLabel.setBounds(0, 180, width, 40);
        overlayPanel.add(overLabel);

        JLabel scoreText = new JLabel("Total Score: " + finalScore, SwingConstants.CENTER);
        scoreText.setFont(new Font("Arial", Font.PLAIN, 22));
        scoreText.setForeground(Color.YELLOW);
        scoreText.setBounds(0, 230, width, 40);
        overlayPanel.add(scoreText);

        JButton restartBtn = new JButton("Restart");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 16));
        restartBtn.setBounds(width/2 - 60, 300, 120, 40);
        restartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                overlayPanel.setVisible(false);
                requestFocusInWindow();
                logic.restartGame();
            }
        });
        overlayPanel.add(restartBtn);

        JButton quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        quitBtn.setBounds(width/2 - 60, 350, 120, 40);
        quitBtn.addActionListener(e -> System.exit(0));
        overlayPanel.add(quitBtn);

        overlayPanel.setVisible(true);
        repaint();
    }

    public void hideOverlayAndRequestFocus(){
        overlayPanel.setVisible(false);
        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        Player player = logic.getPlayer();
        if (player != null){
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);
        }

        ArrayList<Pipe> pipes = logic.getPipes();
        if (pipes != null){
            for (Pipe pipe : pipes){
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
            }
        }
    }

    // update skor
    public void updateScore(int newScore){
        scoreLabel.setText("Score: " + newScore);
    }
}
