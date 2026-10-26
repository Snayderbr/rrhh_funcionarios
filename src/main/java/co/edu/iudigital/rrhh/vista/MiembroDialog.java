package co.edu.iudigital.rrhh.vista;

import co.edu.iudigital.rrhh.excepciones.ValidacionException;
import co.edu.iudigital.rrhh.modelo.Catalogo;
import co.edu.iudigital.rrhh.modelo.Catalogos;
import co.edu.iudigital.rrhh.modelo.MiembroFamiliar;
import co.edu.iudigital.rrhh.servicio.FuncionarioService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Formulario modal para agregar o editar un miembro del grupo familiar. */
class MiembroDialog extends JDialog {
    private final FuncionarioService service;
    private final JComboBox<Catalogo> cbTipoId;
    private final JTextField txtNumero = new JTextField(18);
    private final JTextField txtNombres = new JTextField(18);
    private final JTextField txtApellidos = new JTextField(18);
    private final JTextField txtFecha = new JTextField(10);
    private final JComboBox<Catalogo> cbSexo;
    private final JComboBox<Catalogo> cbRol;
    private final MiembroFamiliar miembro;
    private boolean aceptado;

    private MiembroDialog(Window owner, FuncionarioService service, Catalogos cat, MiembroFamiliar existente) {
        super(owner, existente == null ? "Nuevo familiar" : "Editar familiar", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.miembro = new MiembroFamiliar();   // copia: no se altera el original si se cancela
        this.miembro.setId(existente != null ? existente.getId() : null);

        cbTipoId = UiUtil.combo(cat.tiposIdentificacion());
        cbSexo = UiUtil.combo(cat.sexos());
        cbRol = UiUtil.combo(cat.rolesFamiliares());

        JPanel form = new JPanel(new GridBagLayout());
        UiUtil.fila(form, 0, "Tipo de identificación:", cbTipoId);
        UiUtil.fila(form, 1, "Número de identificación:", txtNumero);
        UiUtil.fila(form, 2, "Nombres:", txtNombres);
        UiUtil.fila(form, 3, "Apellidos:", txtApellidos);
        UiUtil.fila(form, 4, "Fecha de nacimiento (AAAA-MM-DD):", txtFecha);
        UiUtil.fila(form, 5, "Sexo:", cbSexo);
        UiUtil.fila(form, 6, "Rol en la familia:", cbRol);

        if (existente != null) {
            UiUtil.seleccionar(cbTipoId, existente.getTipoIdentificacion());
            txtNumero.setText(existente.getNumeroIdentificacion());
            txtNombres.setText(existente.getNombres());
            txtApellidos.setText(existente.getApellidos());
            txtFecha.setText(existente.getFechaNacimiento().format(UiUtil.FECHA));
            UiUtil.seleccionar(cbSexo, existente.getSexo());
            UiUtil.seleccionar(cbRol, existente.getRolFamiliar());
        }

        JButton ok = new JButton("Aceptar");
        JButton cancelar = new JButton("Cancelar");
        ok.addActionListener(e -> aceptar());
        cancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(ok);
        botones.add(cancelar);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(ok);
        pack();
        setLocationRelativeTo(owner);
    }

    private void aceptar() {
        try {
            MiembroFamiliar m = miembro;
            m.setTipoIdentificacion(UiUtil.valor(cbTipoId));
            m.setNumeroIdentificacion(txtNumero.getText().trim());
            m.setNombres(txtNombres.getText().trim());
            m.setApellidos(txtApellidos.getText().trim());
            m.setFechaNacimiento(UiUtil.parseFecha(txtFecha.getText(), "de nacimiento"));
            m.setSexo(UiUtil.valor(cbSexo));
            m.setRolFamiliar(UiUtil.valor(cbRol));
            service.validarMiembro(m);
            aceptado = true;
            dispose();
        } catch (ValidacionException ex) {
            UiUtil.aviso(this, ex.getMessage());
        }
    }

    /** Muestra el diálogo; devuelve el miembro o null si se canceló. */
    static MiembroFamiliar mostrar(Window owner, FuncionarioService service, Catalogos cat, MiembroFamiliar existente) {
        MiembroDialog d = new MiembroDialog(owner, service, cat, existente);
        d.setVisible(true);
        return d.aceptado ? d.miembro : null;
    }
}
