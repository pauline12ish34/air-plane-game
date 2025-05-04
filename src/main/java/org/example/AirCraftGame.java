package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private Image aircraftImage, gunImage, fireImage, backgroundImage;
    private Clip shootClip, gameOverClip;
    public boolean gameStarted = false;
    public String playerName = "";

    public AirCraftGame() {
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        aircraftImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\Aircraft.png").getImage();
        gunImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\140mm.png").getImage();
        fireImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\explfire.png").getImage();
        backgroundImage = new ImageIcon("C:\\Users\\user\\Documents\\Java Ass\\JavaGameAirspace\\bg.jpg").getImage();

        loadSounds();

        timer = new Timer(50, this);
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
        if (!gameOver && gameStarted) {
            bullets.add(new int[]{gunX + 10, gunY});
            playSound(shootClip);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameStarted) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Welcome to plane Escape Game", 70, 200);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Enter your name above and click Start to begin.", 100, 240);
            return;
        }

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (exploded && fireImage != null) {
            g.drawImage(fireImage, aircraftX + 10, aircraftY + 10, 300, 300, this);
        } else if (aircraftImage != null) {
            g.drawImage(aircraftImage, aircraftX, aircraftY, 100, 80, this);
        }

        if (gunImage != null) {
            g.drawImage(gunImage, gunX, gunY, 100, 80, this);
        }

        if (gunX < aircraftX) gunX += 7;
        if (gunX > aircraftX) gunX -= 7;

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
        g.drawString("Player: " + playerName, 20, 60);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        g.drawString("Date: " + currentDateTime, 400, 20);

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
        if (!gameOver && gameStarted) {
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
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_J && !gameOver && gameStarted) {
            shootBullet();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Aircraft Escape Game");
        AirCraftGame gamePanel = new AirCraftGame();

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new FlowLayout());
        JTextField nameField = new JTextField(20);
        JButton startButton = new JButton("Start Game");
        startButton.setBackground(Color.blue);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 15));

        welcomePanel.add(new JLabel("Enter Player Name: "));
        welcomePanel.add(nameField);
        welcomePanel.add(startButton);

        frame.setLayout(new BorderLayout());
        frame.add(welcomePanel, BorderLayout.NORTH);
        frame.add(gamePanel, BorderLayout.CENTER);

        startButton.addActionListener(e -> {
            gamePanel.playerName = nameField.getText().trim();
            if (gamePanel.playerName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your name.");
                return;
            }
            gamePanel.gameStarted = true;
            frame.remove(welcomePanel);
            frame.revalidate();
            frame.repaint();

            gamePanel.timer.start();
            gamePanel.requestFocusInWindow(); // Allow key input
            gamePanel.repaint();
        });

        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
