import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        JFrame window =new JFrame("First Game"); // Frame
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setContentPane(new GamePanel());

        window.pack();   //метод pack() устанавливает такой минимальный размер контейнера, который достаточен для отображения всех компонентов. Если метод у вас работает иначе, значит вы делаете что-то неверно.
        window.setVisible(true);
    }
}
