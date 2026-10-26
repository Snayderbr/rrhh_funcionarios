package co.edu.iudigital.rrhh.vista;

import co.edu.iudigital.rrhh.excepciones.DAOException;
import co.edu.iudigital.rrhh.modelo.Catalogos;
import co.edu.iudigital.rrhh.modelo.Funcionario;
import co.edu.iudigital.rrhh.servicio.FuncionarioService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Ventana principal: listado de funcionarios y acciones del CRUD. */
public class MainFrame extends JFrame {
    private final FuncionarioService service = new FuncionarioService();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Tipo ID", "Número", "Nombres", "Apellidos",
                          "Estado civil", "Sexo", "Teléfono", "Nacimiento"}, 0) {
        private static final long serialVersionUID = 1L;
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private Catalogos catalogos;

    public MainFrame() {
        super("Gestión de Funcionarios - Recursos Humanos IU Digital");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.removeColumn(tabla.getColumnModel().getColumn(0)); // oculta ID (se lee del modelo)

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar lista");
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnActualizar.addActionListener(e -> cargarLista());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnNuevo);
        barra.add(btnEditar);
        barra.add(btnEliminar);
        barra.add(btnActualizar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        getContentPane().add(barra, BorderLayout.NORTH);
        getContentPane().add(centro, BorderLayout.CENTER);

        iniciar();
    }

    private void iniciar() {
        try {
            catalogos = service.cargarCatalogos();
            cargarLista();
        } catch (DAOException e) {
            UiUtil.error(this, e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarLista() {
        try {
            List<Funcionario> lista = service.listar();
            modelo.setRowCount(0);
            for (Funcionario f : lista) {
                modelo.addRow(new Object[] {
                    f.getId(), f.getTipoIdentificacion(), f.getNumeroIdentificacion(), f.getNombres(),
                    f.getApellidos(), f.getEstadoCivil(), f.getSexo(), f.getTelefono(),
                    f.getFechaNacimiento().format(UiUtil.FECHA)});
            }
        } catch (DAOException e) {
            UiUtil.error(this, e.getMessage());
            e.printStackTrace();
        }
    }

    private Integer idSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            UiUtil.info(this, "Seleccione un funcionario de la lista.");
            return null;
        }
        return (Integer) modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0);
    }

    private void nuevo() {
        if (catalogos == null) {
            iniciar();
            if (catalogos == null) {
                return;
            }
        }
        FuncionarioDialog d = new FuncionarioDialog(this, service, catalogos, null);
        d.setVisible(true);
        if (d.fueGuardado()) {
            cargarLista();
        }
    }

    private void editar() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        try {
            Funcionario f = service.buscar(id);
            FuncionarioDialog d = new FuncionarioDialog(this, service, catalogos, f);
            d.setVisible(true);
            if (d.fueGuardado()) {
                cargarLista();
            }
        } catch (DAOException e) {
            UiUtil.error(this, e.getMessage());
            cargarLista();
        }
    }

    private void eliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el funcionario seleccionado junto con su grupo familiar e información académica?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.eliminar(id);
            cargarLista();
        } catch (DAOException e) {
            UiUtil.error(this, e.getMessage());
            cargarLista();
        }
    }
}
