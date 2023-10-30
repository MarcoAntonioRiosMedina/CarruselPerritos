package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.imageio.ImageIO;

public class CarruselDePerritos extends JFrame {
    private List<String> imagenesPerros;
    private int indiceActual;
    public Timer temporizador;

    private JLabel etiquetaImagen;
    private JComboBox<String> razaComboBox;

    public CarruselDePerritos() {
        setTitle("Carrusel de Imágenes de Perros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        imagenesPerros = obtenerImagenesDePerros(null);
        indiceActual = 0;

        etiquetaImagen = new JLabel();
        add(etiquetaImagen, BorderLayout.CENTER);

        razaComboBox = new JComboBox<>(new String[]{"Imagenes Aleatorias", "husky", "beagle", "bulldog", "labrador", "golden", "poodle", "dachshund"});
        razaComboBox.addActionListener(e -> {
            String razaSeleccionada = (String) razaComboBox.getSelectedItem();
            imagenesPerros = obtenerImagenesDePerros(razaSeleccionada);
            indiceActual = 0;
            mostrarImagen();
        });
        add(razaComboBox, BorderLayout.NORTH);

        temporizador = new Timer(15000, e -> {
            indiceActual = (indiceActual + 1) % imagenesPerros.size();
            mostrarImagen();
        });
    }

    public void mostrarImagen() {
        String urlImagen = imagenesPerros.get(indiceActual);
        try {
            URL url = new URL(urlImagen);
            BufferedImage imagenOriginal = ImageIO.read(url);

            int anchoEtiqueta = etiquetaImagen.getWidth();
            int altoEtiqueta = etiquetaImagen.getHeight();

            BufferedImage imagenRedimensionada = redimensionarImagen(imagenOriginal, anchoEtiqueta, altoEtiqueta);
            ImageIcon icono = new ImageIcon(imagenRedimensionada);
            etiquetaImagen.setIcon(icono);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private BufferedImage redimensionarImagen(BufferedImage imagen, int ancho, int alto) {
        BufferedImage imagenRedimensionada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = imagenRedimensionada.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(imagen, 0, 0, ancho, alto, null);
        graphics2D.dispose();
        return imagenRedimensionada;
    }

    private List<String> obtenerImagenesDePerros(String raza) {
        List<String> resultado = new ArrayList<>();
        int numImagenesPorRaza = 20;

        String apiUrl = "https://dog.ceo/api/breeds/image/random/" + numImagenesPorRaza;
        if (raza != null && !raza.isEmpty()) {
            apiUrl = "https://dog.ceo/api/breed/" + raza + "/images/random/" + numImagenesPorRaza;
        }

        try {
            String jsonData = obtenerJson(apiUrl);
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(jsonData).getAsJsonObject();
            JsonArray urlsImagen = jsonObject.getAsJsonArray("message");

            for (int i = 0; i < urlsImagen.size(); i++) {
                resultado.add(urlsImagen.get(i).getAsString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return resultado;
    }


    private String obtenerJson(String apiUrl) throws IOException {
        StringBuilder respuestaJson = new StringBuilder();

        URL url = new URL(apiUrl);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

        conexion.setRequestMethod("GET");
        conexion.setConnectTimeout(5000);
        conexion.setReadTimeout(5000);

        int codigoRespuesta = conexion.getResponseCode();
        if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
            BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String linea;
            while ((linea = lector.readLine()) != null) {
                respuestaJson.append(linea);
            }
            lector.close();
        } else {
            throw new IOException("Solicitud HTTP fallida: " + codigoRespuesta);
        }

        conexion.disconnect();
        return respuestaJson.toString();
    }
}
