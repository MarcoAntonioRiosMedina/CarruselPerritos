package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarruselDePerritos carrusel = new CarruselDePerritos();
            carrusel.setVisible(true);
            carrusel.mostrarImagen();
            carrusel.temporizador.start();
        });
    }
}



