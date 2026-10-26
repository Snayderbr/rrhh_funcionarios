package co.edu.iudigital.rrhh.vista;

import co.edu.iudigital.rrhh.excepciones.ValidacionException;
import co.edu.iudigital.rrhh.modelo.Catalogo;
import co.edu.iudigital.rrhh.modelo.Catalogos;
import co.edu.iudigital.rrhh.modelo.InformacionAcademica;
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

/** Formulario modal para agregar o editar un estudio académico. */
class AcademicaDialog extends JDialog {
    private final FuncionarioService service;
    private final JComboBox<Catalogo> cbUniversidad;
    private final JComboBox<Catalogo> cbNivel;
    private final JTextField txtTitulo = new JTextField(28);
    private final InformacionAcademica academica;
    private boolean aceptado;

    private AcademicaDialog(Window owner, FuncionarioService service, Catalogos cat, InformacionAcademica existente) {
        super(owner, existente == null ? "Nuevo estudio" : "Editar estudio", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.academica = new InformacionAcademica();   // copia: no se altera el original si se cancela
        this.academica.setId(existente != null ? existente.getId() : null);

        cbUniversidad = UiUtil.combo(cat.universidades());
        cbNivel = UiUtil.combo(cat.nivelesEstudio());

        JPanel form = new JPanel(new GridBagLayout());
        UiUtil.fila(form, 0, "Universidad:", cbUniversidad);
        UiUtil.fila(form, 1, "Nivel de estudio:", cbNivel);
        UiUtil.fila(form, 2, "Título obtenido:", txtTitulo);

        if (existente != null) {
            UiUtil.seleccionar(cbUniversidad, existente.getUniversidad());
            UiUtil.seleccionar(cbNivel, existente.getNivelEstudio());
            txtTitulo.setText(existente.getTitulo());
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
            academica.setUniversidad(UiUtil.valor(cbUniversidad));
            academica.setNivelEstudio(UiUtil.valor(cbNivel));
            academica.setTitulo(txtTitulo.getText().trim());
            service.validarAcademica(academica);
            aceptado = true;
            dispose();
        } catch (ValidacionException ex) {
            UiUtil.aviso(this, ex.getMessage());
        }
    }

    /** Muestra el diálogo; devuelve el estudio o null si se canceló. */
    static InformacionAcademica mostrar(Window owner, FuncionarioService service, Catalogos cat,
                                        InformacionAcademica existente) {
        AcademicaDialog d = new AcademicaDialog(owner, service, cat, existente);
        d.setVisible(true);
        return d.aceptado ? d.academica : null;
    }
}
