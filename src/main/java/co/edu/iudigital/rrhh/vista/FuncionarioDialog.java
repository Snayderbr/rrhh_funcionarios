package co.edu.iudigital.rrhh.vista;

import co.edu.iudigital.rrhh.excepciones.DAOException;
import co.edu.iudigital.rrhh.excepciones.DuplicadoException;
import co.edu.iudigital.rrhh.excepciones.ValidacionException;
import co.edu.iudigital.rrhh.modelo.Catalogo;
import co.edu.iudigital.rrhh.modelo.Catalogos;
import co.edu.iudigital.rrhh.modelo.Funcionario;
import co.edu.iudigital.rrhh.modelo.InformacionAcademica;
import co.edu.iudigital.rrhh.modelo.MiembroFamiliar;
import co.edu.iudigital.rrhh.servicio.FuncionarioService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Formulario de creación/edición de un funcionario con sus relaciones (pestañas). */
class FuncionarioDialog extends JDialog {
    private final FuncionarioService service;
    private final Catalogos catalogos;
    private final Funcionario funcionario;

    private final JComboBox<Catalogo> cbTipoId;
    private final JTextField txtNumero = new JTextField(20);
    private final JTextField txtNombres = new JTextField(20);
    private final JTextField txtApellidos = new JTextField(20);
    private final JComboBox<Catalogo> cbEstadoCivil;
    private final JComboBox<Catalogo> cbSexo;
    private final JTextField txtDireccion = new JTextField(30);
    private final JTextField txtTelefono = new JTextField(15);
    private final JTextField txtFecha = new JTextField(10);

    private final List<MiembroFamiliar> miembros = new ArrayList<>();
    private final List<InformacionAcademica> estudios = new ArrayList<>();
    private final DefaultTableModel modeloMiembros = noEditable(
            "Identificación", "Nombres", "Apellidos", "Nacimiento", "Sexo", "Rol");
    private final DefaultTableModel modeloEstudios = noEditable("Universidad", "Nivel", "Título");

    private boolean guardado;

    FuncionarioDialog(Frame owner, FuncionarioService service, Catalogos catalogos, Funcionario existente) {
        super(owner, existente == null ? "Nuevo funcionario" : "Editar funcionario", true);
        this.service = service;
        this.catalogos = catalogos;
        this.funcionario = existente != null ? existente : new Funcionario();

        cbTipoId = UiUtil.combo(catalogos.tiposIdentificacion());
        cbEstadoCivil = UiUtil.combo(catalogos.estadosCiviles());
        cbSexo = UiUtil.combo(catalogos.sexos());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Datos personales", panelDatos());
        tabs.addTab("Grupo familiar", panelMiembros());
        tabs.addTab("Información académica", panelEstudios());

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        getContentPane().add(tabs, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);

        if (existente != null) {
            cargarFormulario(existente);
        }
        setSize(720, 460);
        setLocationRelativeTo(owner);
    }

    boolean fueGuardado() { return guardado; }

    // ----------------------------------------------------------------- paneles
    private JPanel panelDatos() {
        JPanel p = new JPanel(new GridBagLayout());
        UiUtil.fila(p, 0, "Tipo de identificación:", cbTipoId);
        UiUtil.fila(p, 1, "Número de identificación:", txtNumero);
        UiUtil.fila(p, 2, "Nombres:", txtNombres);
        UiUtil.fila(p, 3, "Apellidos:", txtApellidos);
        UiUtil.fila(p, 4, "Estado civil:", cbEstadoCivil);
        UiUtil.fila(p, 5, "Sexo:", cbSexo);
        UiUtil.fila(p, 6, "Dirección:", txtDireccion);
        UiUtil.fila(p, 7, "Teléfono:", txtTelefono);
        UiUtil.fila(p, 8, "Fecha de nacimiento (AAAA-MM-DD):", txtFecha);
        return p;
    }

    private JPanel panelMiembros() {
        JTable tabla = crearTabla(modeloMiembros);
        JButton agregar = new JButton("Agregar");
        JButton editar = new JButton("Editar");
        JButton quitar = new JButton("Quitar");

        agregar.addActionListener(e -> {
            MiembroFamiliar m = MiembroDialog.mostrar(this, service, catalogos, null);
            if (m != null) {
                miembros.add(m);
                refrescarMiembros();
            }
        });
        editar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                UiUtil.info(this, "Seleccione un familiar de la tabla.");
                return;
            }
            MiembroFamiliar m = MiembroDialog.mostrar(this, service, catalogos, miembros.get(fila));
            if (m != null) {
                miembros.set(fila, m);
                refrescarMiembros();
            }
        });
        quitar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                UiUtil.info(this, "Seleccione un familiar de la tabla.");
                return;
            }
            miembros.remove(fila);
            refrescarMiembros();
        });
        return panelLista(tabla, agregar, editar, quitar);
    }

    private JPanel panelEstudios() {
        JTable tabla = crearTabla(modeloEstudios);
        JButton agregar = new JButton("Agregar");
        JButton editar = new JButton("Editar");
        JButton quitar = new JButton("Quitar");

        agregar.addActionListener(e -> {
            InformacionAcademica ia = AcademicaDialog.mostrar(this, service, catalogos, null);
            if (ia != null) {
                estudios.add(ia);
                refrescarEstudios();
            }
        });
        editar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                UiUtil.info(this, "Seleccione un estudio de la tabla.");
                return;
            }
            InformacionAcademica ia = AcademicaDialog.mostrar(this, service, catalogos, estudios.get(fila));
            if (ia != null) {
                estudios.set(fila, ia);
                refrescarEstudios();
            }
        });
        quitar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                UiUtil.info(this, "Seleccione un estudio de la tabla.");
                return;
            }
            estudios.remove(fila);
            refrescarEstudios();
        });
        return panelLista(tabla, agregar, editar, quitar);
    }

    private JPanel panelLista(JTable tabla, JButton... botones) {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : botones) {
            barra.add(b);
        }
        p.add(barra, BorderLayout.SOUTH);
        return p;
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setRowHeight(22);
        return t;
    }

    private static DefaultTableModel noEditable(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    // ------------------------------------------------------------------- datos
    private void cargarFormulario(Funcionario f) {
        UiUtil.seleccionar(cbTipoId, f.getTipoIdentificacion());
        txtNumero.setText(f.getNumeroIdentificacion());
        txtNombres.setText(f.getNombres());
        txtApellidos.setText(f.getApellidos());
        UiUtil.seleccionar(cbEstadoCivil, f.getEstadoCivil());
        UiUtil.seleccionar(cbSexo, f.getSexo());
        txtDireccion.setText(f.getDireccion());
        txtTelefono.setText(f.getTelefono());
        txtFecha.setText(f.getFechaNacimiento().format(UiUtil.FECHA));
        miembros.addAll(f.getGrupoFamiliar());
        estudios.addAll(f.getInformacionAcademica());
        refrescarMiembros();
        refrescarEstudios();
    }

    private void refrescarMiembros() {
        modeloMiembros.setRowCount(0);
        for (MiembroFamiliar m : miembros) {
            modeloMiembros.addRow(new Object[] {
                m.getTipoIdentificacion() + " " + m.getNumeroIdentificacion(),
                m.getNombres(), m.getApellidos(), m.getFechaNacimiento().format(UiUtil.FECHA),
                m.getSexo(), m.getRolFamiliar()});
        }
    }

    private void refrescarEstudios() {
        modeloEstudios.setRowCount(0);
        for (InformacionAcademica ia : estudios) {
            modeloEstudios.addRow(new Object[] {ia.getUniversidad(), ia.getNivelEstudio(), ia.getTitulo()});
        }
    }

    private void guardar() {
        try {
            funcionario.setTipoIdentificacion(UiUtil.valor(cbTipoId));
            funcionario.setNumeroIdentificacion(txtNumero.getText().trim());
            funcionario.setNombres(txtNombres.getText().trim());
            funcionario.setApellidos(txtApellidos.getText().trim());
            funcionario.setEstadoCivil(UiUtil.valor(cbEstadoCivil));
            funcionario.setSexo(UiUtil.valor(cbSexo));
            funcionario.setDireccion(txtDireccion.getText().trim());
            funcionario.setTelefono(txtTelefono.getText().trim());
            funcionario.setFechaNacimiento(UiUtil.parseFecha(txtFecha.getText(), "de nacimiento"));
            funcionario.setGrupoFamiliar(new ArrayList<>(miembros));
            funcionario.setInformacionAcademica(new ArrayList<>(estudios));

            if (funcionario.getId() == null) {
                service.crear(funcionario);
            } else {
                service.actualizar(funcionario);
            }
            guardado = true;
            dispose();
        } catch (ValidacionException ex) {
            UiUtil.aviso(this, ex.getMessage());
        } catch (DuplicadoException ex) {
            UiUtil.aviso(this, ex.getMessage());
        } catch (DAOException ex) {
            UiUtil.error(this, ex.getMessage());
            ex.printStackTrace();
        }
    }
}
