package co.edu.iudigital.rrhh.modelo;

/** Estudio realizado por un funcionario. */
public class InformacionAcademica {
    private Integer id;
    private Catalogo universidad;
    private Catalogo nivelEstudio;
    private String titulo;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Catalogo getUniversidad() { return universidad; }
    public void setUniversidad(Catalogo v) { this.universidad = v; }
    public Catalogo getNivelEstudio() { return nivelEstudio; }
    public void setNivelEstudio(Catalogo v) { this.nivelEstudio = v; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }
}
