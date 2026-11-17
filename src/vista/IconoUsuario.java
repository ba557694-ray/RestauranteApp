package vista;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class IconoUsuario implements Icon {
    private final int tamano;
    private final Color color;

    public IconoUsuario(int tamano, Color color) {
        this.tamano = tamano;
        this.color = color;
    }

    @Override
    public void paintIcon(Component componente, Graphics grafico, int x, int y) {
        Graphics2D dibujo = (Graphics2D) grafico.create();
        dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        dibujo.setColor(color);
        dibujo.setStroke(new BasicStroke(1.6f));
        int diametroCabeza = tamano / 2;
        dibujo.drawOval(x + (tamano - diametroCabeza) / 2, y + 1, diametroCabeza, diametroCabeza);
        dibujo.drawArc(x + 1, y + diametroCabeza + 1, tamano - 2, tamano - 4, 0, 180);
        dibujo.dispose();
    }

    @Override
    public int getIconWidth() {
        return tamano;
    }

    @Override
    public int getIconHeight() {
        return tamano;
    }
}
