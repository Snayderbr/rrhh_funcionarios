package co.edu.iudigital.rrhh.modelo;

import java.util.Objects;

/** Elemento genérico de un catálogo (id + nombre): sexo, estado civil, universidad, etc. */
public class Catalogo {
    private final int id;
    private final String nombre;

    public Catalogo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override public String toString() { return nombre; }

    @Override public boolean equals(Object o) {
        return o instanceof Catalogo c && c.id == id;
    }

    @Override public int hashCode() { return Objects.hash(id); }
}
