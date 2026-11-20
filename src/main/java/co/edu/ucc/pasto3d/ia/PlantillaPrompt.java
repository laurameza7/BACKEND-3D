package co.edu.ucc.pasto3d.ia;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón PROTOTYPE: la plantilla con todo el conocimiento del campus (edificios, lugares,
 * programas, preguntas frecuentes) es costosa de armar. Se construye UNA vez con el BUILDER
 * y, para cada pregunta, se CLONA y a la copia se le agregan los datos propios de esa pregunta
 * (perfil de quien pregunta, fecha). El prototipo original nunca se modifica.
 */
public class PlantillaPrompt implements Cloneable {

    private List<String> secciones;

    public PlantillaPrompt(String base) {
        this.secciones = new ArrayList<>(List.of(base));
    }

    /** Clona el prototipo (copia profunda de la lista de secciones). */
    public PlantillaPrompt copiar() {
        try {
            PlantillaPrompt copia = (PlantillaPrompt) super.clone();
            copia.secciones = new ArrayList<>(this.secciones);
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public PlantillaPrompt agregar(String seccion) {
        if (seccion != null && !seccion.isBlank()) secciones.add(seccion);
        return this;
    }

    public int tamano() { return secciones.size(); }

    public String texto() { return String.join("\n\n", secciones); }
}
