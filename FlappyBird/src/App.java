import javax.swing.*;

public class App {
    static void main(String[] args){
        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        Logic logika = new Logic(); //inisiasi logic
        //instansiasi view sehingga view bisa berkomunikasi dengan logic
        View tampilan = new View(logika);
        //begitu pula kebalikannya
        logika.setView(tampilan);

        //tampilan.requestFocus(); // focus akan diminta ketika Play ditekan
        frame.add(tampilan);
        frame.pack();
        frame.setVisible(true);
    }
}
