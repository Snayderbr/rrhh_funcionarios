package co.edu.iudigital.rrhh.modelo;

import java.util.List;

/** Contenedor con todos los catálogos que usan los formularios. */
public record Catalogos(
        List<Catalogo> tiposIdentificacion,
        List<Catalogo> estadosCiviles,
        List<Catalogo> sexos,
        List<Catalogo> rolesFamiliares,
        List<Catalogo> nivelesEstudio,
        List<Catalogo> universidades) {
}
