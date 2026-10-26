package co.edu.iudigital.rrhh.modelo;

import java.time.LocalDate;

/** Miembro del grupo familiar de un funcionario. */
public class MiembroFamiliar {
    private Integer id;
    private Catalogo tipoIdentificacion;
    private String numeroIdentificacion;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private Catalogo sexo;
    private Catalogo rolFamiliar;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Catalogo getTipoIdentificacion() { return tipoIdentificacion; }
    public void setTipoIdentificacion(Catalogo v) { this.tipoIdentificacion = v; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String v) { this.numeroIdentificacion = v; }
    public String getNombres() { return nombres; }
    public void setNombres(String v) { this.nombres = v; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String v) { this.apellidos = v; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate v) { this.fechaNacimiento = v; }
    public Catalogo getSexo() { return sexo; }
    public void setSexo(Catalogo v) { this.sexo = v; }
    public Catalogo getRolFamiliar() { return rolFamiliar; }
    public void setRolFamiliar(Catalogo v) { this.rolFamiliar = v; }
}
