package co.edu.iudigital.rrhh.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Funcionario de la institución, con su grupo familiar e información académica. */
public class Funcionario {
    private Integer id;
    private Catalogo tipoIdentificacion;
    private String numeroIdentificacion;
    private String nombres;
    private String apellidos;
    private Catalogo estadoCivil;
    private Catalogo sexo;
    private String direccion;
    private String telefono;
    private LocalDate fechaNacimiento;
    private List<MiembroFamiliar> grupoFamiliar = new ArrayList<>();
    private List<InformacionAcademica> informacionAcademica = new ArrayList<>();

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
    public Catalogo getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(Catalogo v) { this.estadoCivil = v; }
    public Catalogo getSexo() { return sexo; }
    public void setSexo(Catalogo v) { this.sexo = v; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String v) { this.direccion = v; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String v) { this.telefono = v; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate v) { this.fechaNacimiento = v; }
    public List<MiembroFamiliar> getGrupoFamiliar() { return grupoFamiliar; }
    public void setGrupoFamiliar(List<MiembroFamiliar> v) { this.grupoFamiliar = v; }
    public List<InformacionAcademica> getInformacionAcademica() { return informacionAcademica; }
    public void setInformacionAcademica(List<InformacionAcademica> v) { this.informacionAcademica = v; }

    public String getNombreCompleto() { return nombres + " " + apellidos; }
}
