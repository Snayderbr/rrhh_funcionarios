package co.edu.iudigital.rrhh.dao;

/** Catálogos disponibles. El nombre de tabla proviene de esta lista cerrada (evita inyección SQL). */
public enum TipoCatalogo {
    TIPO_IDENTIFICACION("tipo_identificacion"),
    ESTADO_CIVIL("estado_civil"),
    SEXO("sexo"),
    ROL_FAMILIAR("rol_familiar"),
    NIVEL_ESTUDIO("nivel_estudio"),
    UNIVERSIDAD("universidad");

    private final String tabla;

    TipoCatalogo(String tabla) { this.tabla = tabla; }

    public String getTabla() { return tabla; }
}
