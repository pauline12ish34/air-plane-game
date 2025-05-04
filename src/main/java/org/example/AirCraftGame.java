package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AirCraftGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int aircraftX = 250, aircraftY = 50; // Aircraft position
    private int gunX = 250, gunY = 450; // Gun position
    private ArrayList<int[]> bullets = new ArrayList<>(); // Stores bullet positions
    private boolean gameOver = false;
    private int score = 0;
    private int timeElapsed = 0;
    private Image aircraftImage, gunImage;

    // Sound clips
    private Clip shootClip, gameOverClip;

    public AirCraftGame() {
        timer = new Timer(50, this); // Refresh every 50ms
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Load aircraft image
        aircraftImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\Aircraft.png").getImage();

        // Load gun image
        gunImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\140mm.png").getImage();

        // Load sound effects
        loadSounds();

        // Bullet firing every second
        new Timer(1000, e -> shootBullet()).start();

        // Timer counter
        new Timer(1000, e -> timeElapsed++).start();
    }

    // Method to load sound effects
    private void loadSounds() {
        try {
            // Load shoot sound
            File shootSoundFile = new File("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\gunsound.wav");
            AudioInputStream shootAudio = AudioSystem.getAudioInputStream(shootSoundFile);
            shootClip = AudioSystem.getClip();
            shootClip.open(shootAudio);

            // Load game over sound
            File gameOverSoundFile = new File("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\explode.wav");
            AudioInputStream gameOverAudio = AudioSystem.getAudioInputStream(gameOverSoundFile);
            gameOverClip = AudioSystem.getClip();
            gameOverClip.open(gameOverAudio);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to play sound
    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the start
            clip.start(); // Play the sound
        }
    }

    public void shootBullet() {
        if (!gameOver) {
            bullets.add(new int[]{gunX + 20, gunY});
            playSound(shootClip); // Play the shoot sound
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Load background image
        Image backgroundImage = new ImageIcon("C:\\Users\\USER\\Desktop\\backg.jpeg").getImage();

        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw aircraft image with fallback if not loaded
        if (aircraftImage != null) {
            g.drawImage(aircraftImage, aircraftX, aircraftY, 100, 80, this);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(aircraftX, aircraftY, 60, 30);
        }

        // Draw gun image with fallback
        if (gunImage != null) {
            g.drawImage(gunImage, gunX, gunY, 80, 60, this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(gunX, gunY, 40, 20);
        }

        // Move gun towards aircraft
        if (gunX < aircraftX) gunX += 2;
        if (gunX > aircraftX) gunX -= 2;

        // Draw bullets
        g.setColor(Color.RED);
        Iterator<int[]> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            int[] bullet = iterator.next();
            g.fillRect(bullet[0], bullet[1], 5, 10);
            bullet[1] -= 10;

            // Collision detection
            if (new Rectangle(bullet[0], bullet[1], 5, 10).intersects(new Rectangle(aircraftX, aircraftY, 60, 30))) {
                gameOver = true;
                timer.stop();
                playSound(gameOverClip); // Play the game over sound
                showGameOverDialog();
            }

            // Remove bullets that go off screen
            if (bullet[1] < 0) iterator.remove();
        }

        // Display score and timer
        g.setColor(Color.WHITE);
        g.drawString("Time: " + timeElapsed + "s", 20, 20);
        g.drawString("Score: " + score, 20, 40);

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
        score = 0;
        timeElapsed = 0;
        timer.start();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            score++;
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT && aircraftX > 0) {
                aircraftX -= 20;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT && aircraftX < getWidth() - 60) {
                aircraftX += 20;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Aircraft Escape Game");
        AirCraftGame gamePanel = new AirCraftGame();
        frame.add(gamePanel);
        frame.setSize(800, 800);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}