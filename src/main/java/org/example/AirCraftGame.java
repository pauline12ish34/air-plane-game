package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AirCraftGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int aircraftX = 250, aircraftY = 50;
    private int gunX = 250, gunY = 450;
    private ArrayList<int[]> bullets = new ArrayList<>();
    private boolean gameOver = false;
    private boolean exploded = false;
    private boolean movingRight = true;
    private int score = 0;
    private int timeElapsed = 0;
    private Image aircraftImage, gunImage, explosionImage, fireImage;

    private Clip shootClip, gameOverClip;
//    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public AirCraftGame() {
        timer = new Timer(50, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        aircraftImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\Aircraft.png").getImage();
        gunImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\140mm.png").getImage();

//        explosionImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\explosion.png").getImage();
        fireImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\explfire.png").getImage();

        loadSounds();

        new Timer(1000, e -> shootBullet()).start();
        new Timer(1000, e -> timeElapsed++).start();
    }

    private void loadSounds() {
        try {
            File shootSoundFile = new File("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\gunsound.wav");
            AudioInputStream shootAudio = AudioSystem.getAudioInputStream(shootSoundFile);
            shootClip = AudioSystem.getClip();
            shootClip.open(shootAudio);

            File gameOverSoundFile = new File("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\explode.wav");
            AudioInputStream gameOverAudio = AudioSystem.getAudioInputStream(gameOverSoundFile);
            gameOverClip = AudioSystem.getClip();
            gameOverClip.open(gameOverAudio);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void shootBullet() {
        if (!gameOver) {
            bullets.add(new int[]{gunX + 20, gunY});
            playSound(shootClip);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image backgroundImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\bg.jpg").getImage();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (exploded) {
//            if (explosionImage != null) {
//                g.drawImage(explosionImage, aircraftX, aircraftY, 100, 80, this);
//            }
            if (fireImage != null) {
                g.drawImage(fireImage, aircraftX + 10, aircraftY + 10, 300, 300, this);
            }
        } else {
            if (aircraftImage != null) {
                g.drawImage(aircraftImage, aircraftX, aircraftY, 100, 80, this);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(aircraftX, aircraftY, 60, 30);
            }
        }

        if (gunImage != null) {
            g.drawImage(gunImage, gunX, gunY, 100, 80, this);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(gunX, gunY, 40, 20);
        }

        if (gunX < aircraftX) gunX += 2;
        if (gunX > aircraftX) gunX -= 2;

        g.setColor(Color.RED);
        Iterator<int[]> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            int[] bullet = iterator.next();
            g.fillRect(bullet[0], bullet[1], 5, 10);
            bullet[1] -= 10;

            if (!exploded && new Rectangle(bullet[0], bullet[1], 5, 10)
                    .intersects(new Rectangle(aircraftX, aircraftY, 100, 80))) {
                gameOver = true;
                exploded = true;
                timer.stop();
                playSound(gameOverClip);
                repaint();
                showGameOverDialog();
            }

            if (bullet[1] < 0) iterator.remove();
        }

        g.setColor(Color.WHITE);
        g.drawString("Time: " + timeElapsed + "s", 20, 20);
        g.drawString("Score: " + score, 20, 40);
//        g.drawString("date",+ datee, 20, 60);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over!", 200, 250);
        }
    }

    private void showGameOverDialog() {
        int response = JOptionPane.showConfirmDialog(this, "Game Over! Do you want to restart?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        aircraftX = 250;
        aircraftY = 50;
        gunX = 250;
        gunY = 450;
        bullets.clear();
        gameOver = false;
        exploded = false;
        score = 0;
        timeElapsed = 0;
        movingRight = true;
        timer.start();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (movingRight) {
                aircraftX += 5;
                if (aircraftX > getWidth() - 100) {
                    movingRight = false;
                }
            } else {
                aircraftX -= 5;
                if (aircraftX < 0) {
                    movingRight = true;
                }
            }

            score++;
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Aircraft Escape Game");
        AirCraftGame gamePanel = new AirCraftGame();
        frame.add(gamePanel);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
