package View;

import Controller.Controller;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Controller controlador = new Controller();
        controlador.start();
    }
}
