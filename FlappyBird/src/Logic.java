import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Logic implements ActionListener, KeyListener{

    int frameWidth = 360;
    int frameHeight = 640;

    int playerStartPosX = frameWidth / 2;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    //atribut posisi dan ukuran pipa
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    View view;
    Image birdImage;
    Player player;

    //list pipa dan gambarnya
    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    int pipeVelocityX = -2;

    // game state
    private boolean isGameOver = false;
    private boolean isRunning = false; // apakah game sudah dimulai (setelah Play)
    private int score = 0;

    public Logic(){
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);

        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();
        pipes = new ArrayList<Pipe>();

        // jangan start timer di constructor — start ketika Play ditekan
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                placePipes();
            }
        });

        gameLoop = new Timer(1000/60, this);
    }

    public void setView(View view){
        this.view = view;
        // initial update skor
        if (view != null) view.updateScore(score);
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes(){
        return pipes;
    }

    public void placePipes(){
        int randomPosY = (int) (pipeStartPosY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, (randomPosY + openingSpace + pipeHeight), pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void move(){
        if (!isRunning) return;

        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (!isRunning) return;

        move();
        checkCollisionsAndScore();

        if (view != null){
            view.repaint();
        }
    }

    private void checkCollisionsAndScore(){
        // player rectangle
        Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight());

        // check if fell below the bottom -> game over
        if (player.getPosY() + player.getHeight() >= frameHeight){
            endGame();
            return;
        }

        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            Rectangle pipeRect = new Rectangle(pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight());

            // collision detection
            if (playerRect.intersects(pipeRect)){
                endGame();
                return;
            }

            // scoring: only count upper pipe once when it passed player (to give +1 per pair)
            if (!pipe.isPassed() && pipe.getPosY() < 0) { // upper pipe has negative y in this placement logic
                if (pipe.getPosX() + pipe.getWidth() < player.getPosX()){
                    pipe.setPassed(true);
                    score += 1;
                    if (view != null) view.updateScore(score);
                }
            }
        }

        // optional: remove off-screen pipes to prevent list growing indefinitely
        pipes.removeIf(p -> p.getPosX() + p.getWidth() < -50);
    }

    private void endGame(){
        isGameOver = true;
        isRunning = false;
        pipesCooldown.stop();
        gameLoop.stop();

        if (view != null){
            view.showGameOverOverlay(score); // ubah agar kirim skor
        }
    }

    public void startGame(){
        // start game from initial menu (fresh start)
        resetGameState();
        isRunning = true;
        isGameOver = false;
        pipesCooldown.start();
        gameLoop.start();
    }

    public void restartGame(){
        // restart after game over: reset player, pipes, score and restart timers
        resetGameState();
        isRunning = true;
        isGameOver = false;
        pipesCooldown.restart();
        gameLoop.restart();
        if (view != null) view.hideOverlayAndRequestFocus();
    }

    private void resetGameState(){
        // reset player position and velocity
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);

        // clear pipes
        pipes.clear();

        // reset passed flags & score
        score = 0;
        if (view != null) view.updateScore(score);
    }

    @Override
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            // only make player jump if game is running
            if (isRunning && !isGameOver) {
                player.setVelocityY(-10);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_R){
            if (isGameOver){
                restartGame();
            } else {
                // if game not started yet (from menu), pressing R does nothing
            }
        }
    }
    public void keyReleased(KeyEvent e){}
}
