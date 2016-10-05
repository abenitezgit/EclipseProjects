package frm;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class frmGroup extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtGrupoID;
	private JTextField txtDescripcion;
	private JTable tableProcess;
	private JTextField txtStatus;
	private JTextField txtUStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmGroup frame = new frmGroup();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public frmGroup() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 759, 427);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnFinish.setBounds(678, 370, 75, 29);
		contentPane.add(btnFinish);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnCancel.setBounds(613, 370, 75, 29);
		contentPane.add(btnCancel);
		
		JLabel lblGrupoId = new JLabel("Grupo ID:");
		lblGrupoId.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblGrupoId.setBounds(6, 16, 61, 16);
		contentPane.add(lblGrupoId);
		
		JLabel lblDescripcin = new JLabel("Descripción:");
		lblDescripcin.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblDescripcin.setBounds(6, 44, 66, 16);
		contentPane.add(lblDescripcin);
		
		JCheckBox chckbxEnable = new JCheckBox("Enable");
		chckbxEnable.setSelected(true);
		chckbxEnable.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		chckbxEnable.setBounds(79, 66, 75, 23);
		contentPane.add(chckbxEnable);
		
		JLabel lblHorarioId = new JLabel("Horario ID:");
		lblHorarioId.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblHorarioId.setBounds(6, 98, 61, 16);
		contentPane.add(lblHorarioId);
		
		JComboBox cmbHorarioID = new JComboBox();
		cmbHorarioID.setBounds(79, 93, 165, 27);
		contentPane.add(cmbHorarioID);
		
		JLabel lblClienteId = new JLabel("Cliente ID:");
		lblClienteId.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblClienteId.setBounds(6, 126, 61, 16);
		contentPane.add(lblClienteId);
		
		JComboBox cmbClienteID = new JComboBox();
		cmbClienteID.setBounds(79, 121, 165, 27);
		contentPane.add(cmbClienteID);
				
		txtGrupoID = new JTextField();
		txtGrupoID.setBounds(79, 10, 181, 26);
		contentPane.add(txtGrupoID);
		txtGrupoID.setColumns(10);
		
		txtDescripcion = new JTextField();
		txtDescripcion.setBounds(79, 38, 268, 26);
		contentPane.add(txtDescripcion);
		txtDescripcion.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 166, 736, 195);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 5, 724, 184);
		panel.add(scrollPane);
		
		tableProcess = new JTable();
		tableProcess.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		scrollPane.setViewportView(tableProcess);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnAdd.setBounds(14, 373, 51, 23);
		contentPane.add(btnAdd);
		
		JButton btnDelete = new JButton("delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDelete.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnDelete.setBounds(54, 373, 67, 23);
		contentPane.add(btnDelete);
		
		JButton btnUp = new JButton("UP");
		btnUp.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnUp.setBounds(121, 370, 67, 29);
		contentPane.add(btnUp);
		
		JButton btnDown = new JButton("DOWN");
		btnDown.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnDown.setBounds(177, 370, 67, 29);
		contentPane.add(btnDown);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblStatus.setBounds(472, 15, 61, 16);
		contentPane.add(lblStatus);
		
		txtStatus = new JTextField();
		txtStatus.setColumns(10);
		txtStatus.setBounds(545, 10, 181, 26);
		contentPane.add(txtStatus);
		
		JLabel lblUstatus = new JLabel("UStatus:");
		lblUstatus.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblUstatus.setBounds(472, 43, 61, 16);
		contentPane.add(lblUstatus);
		
		txtUStatus = new JTextField();
		txtUStatus.setColumns(10);
		txtUStatus.setBounds(545, 38, 181, 26);
		contentPane.add(txtUStatus);
		
	}
}
