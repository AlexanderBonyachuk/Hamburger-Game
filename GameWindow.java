package Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GameWindow extends JFrame {
    private static GameWindow game_Window;
    private static Image backGround;        // картинки
    private static Image drop;
    private static Image gameOver;
    private static Image restart;
    // 29.09.2021 тут остановился
    private static long lastFrameTime;
    private static float drop_Y = -200;
    private static float drop_X;
    private static float drop_V = 220;      // скорость перемещения бургера
    private static int score = 0;           // счет
    private static int n = 40;              // координаты рестарта
    private static int m = 60;              // координаты рестарта
    private static float restart_X = n;
    private static float restart_Y = m;

    public static void main(String[] args) throws IOException {
        backGround = ImageIO.read(GameWindow.class.getResourceAsStream("background.png"));  // обозначение адреса картинки
        drop = ImageIO.read(GameWindow.class.getResourceAsStream("burger.png"));
        gameOver = ImageIO.read(GameWindow.class.getResourceAsStream("game_over.png"));
        restart = ImageIO.read(GameWindow.class.getResourceAsStream("reboot.png"));

        JLabel record = new JLabel("Счет: " + score);                         // панель счета
        record.setSize(220, 150);
        record.setPreferredSize(new Dimension(100, 25));
        record.setFont(new Font("Счет: " + score, Font.PLAIN, 19));     //стиль текста счета
        record.setOpaque(true);
        record.setBackground(Color.WHITE);

        game_Window = new GameWindow();
        game_Window.setSize(900,600);
        game_Window.setResizable(false);                         // запрет на растягивания окна

        lastFrameTime = System.nanoTime();
        game_Window.setTitle("Бургер Судьбы");
        game_Window.setDefaultCloseOperation(EXIT_ON_CLOSE);    // закрытие окна
        game_Window.setLocation(1400,300);

        GameField game_Field = new GameField();                  // добавление картинки игрового поля
        game_Field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {        //отслеживание нажатия мыши
                int x = e.getX();
                int y = e.getY();
                float drop_x_right = drop_X + drop.getWidth(null);
                float drop_t_bottom = drop_Y + drop.getHeight(null);

                boolean isDrop = x >= drop_X && x<= drop_x_right && y>=drop_Y && y<= drop_t_bottom ;    // проверка на попадание по бургеру
                if (isDrop) {
                    score++;
                    /* 30.09.2021 иногда первые несколько бургеров не записываются в счет, он растет не с первого бургера
                    вероятно, что-то свзяно с многопоточностью или чрезмерно быстрым ПК */
                    drop_Y = -100;
                    drop_X = (int) (Math.random() * (game_Field.getWidth() - drop.getWidth(null)));
                    record.setText("Счет: " + score);
                    drop_V = drop_V + 20;                       // ускорение падения
                }
                // есть баг с некорректным подсчетом очков
                else if (isDrop == false) {                   // для того чтобы счетчик не шел вниз (?)
                    if (score < 0) {
                        score++;
                    }
                }

                float restart_x_left = restart_X + restart.getWidth(null);
                float restart_y_bottom = restart_Y + restart.getHeight(null);
                boolean if_Restart = x >= restart_X && x <= restart_x_left && y >= restart_Y && y <= restart_y_bottom;  // нажатие рестарта

                if (if_Restart) {
                    drop_Y = -100;
                    drop_X = (int) (Math.random() * (game_Field.getWidth() - drop.getWidth(null)));
                    score = 0;
                    drop_V = 220;
                    record.setText("Счет: " + score);
                }
            }
        });
        game_Window.add(game_Field);
        game_Field.add(record);
        game_Window.setVisible(true);
    }
    public static void onRepaint (Graphics g) {
        // сначала прорисовываем задний фон, потом бургер!
        g.drawImage(backGround, 0,0,null);      // загрузка картинки игрового поля

        long correntTime = System.nanoTime();                // смешение бургера вниз
        float deltaTime = (correntTime - lastFrameTime) * 0.000000001f;
        lastFrameTime = correntTime;
        drop_Y = drop_Y + drop_V * deltaTime;

        g.drawImage(drop, (int) drop_X, (int) drop_Y, null);        //положение бургера
        if (drop_Y > game_Window.getHeight()) {                             // условие отображения конца игры
            g.drawImage(gameOver, 350 ,150, null);            // отображение game over
            g.drawImage(restart, (int) restart_X, (int) restart_Y, null);            // отображение game restart
        }
    }
    private static class GameField extends JPanel {
        @Override
        protected void paintComponent (Graphics g) {
            super.paintComponent(g);
            onRepaint(g);
            repaint();
        }
    }

}
