package frm;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import threads.thKeepAlive;
import threads.thUpdateList;
import utiles.globalAreaData;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class frmMain {
	globalAreaData gDatos = new globalAreaData();
	static Logger logger = Logger.getLogger("frmMain");

	private JFrame frame;
	private JTable tableGroup;
	private JTable tableGroupActive;
	private JTable tablePool;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmMain window = new frmMain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public frmMain() {
		try {
			initialize();
			
		} catch (Exception e) {
			logger.error("Error frmMain: "+e.getMessage());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 786, 554);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnUpdate = new JButton("update");
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//Asigna Tabla Local a Tabla Generica
					gDatos.setlTableGroup(tableGroup);
					tableGroup.setGridColor(Color.GRAY);
					Thread thUpdateListGroup = new thUpdateList(gDatos);
					thUpdateListGroup.setName("thUpdateListGroup");
					thUpdateListGroup.start();
				} catch (Exception ex) {
					logger.error("Error Starting thUpdateList: "+ex.getMessage());
				}
			}
		});
		btnUpdate.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnUpdate.setBounds(117, 6, 87, 29);
		frame.getContentPane().add(btnUpdate);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnCancel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnCancel.setBounds(608, 338, 117, 29);
		frame.getContentPane().add(btnCancel);
		
		Panel panel = new Panel();
		panel.setBounds(10, 50, 440, 108);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 0, 428, 102);
		panel.add(scrollPane);
		
		tableGroup = new JTable();
		tableGroup.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		scrollPane.setViewportView(tableGroup);
		
		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Asigna Tabla Local a Tabla Generica
					gDatos.setlTableGroup(tableGroup);
					tableGroup.setGridColor(Color.GRAY);
					Thread thKeepAlive = new thKeepAlive(gDatos);
					thKeepAlive.setName("thKeepAlive");
					thKeepAlive.start();
				} catch (Exception ex) {
					logger.error("Error Starting thKeepAlive: "+ex.getMessage());
				}

			}
		});
		btnNew.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnNew.setBounds(197, 5, 87, 29);
		frame.getContentPane().add(btnNew);

		JButton btnNewBase = new JButton("New from Base");
		btnNewBase.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnNewBase.setBounds(280, 5, 87, 29);
		frame.getContentPane().add(btnNewBase);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 221, 440, 91);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(tableGroupActive.getValueAt(tableGroupActive.getSelectedRow(), 1).toString());
			}
		});
		scrollPane_1.setBounds(0, 0, 440, 91);
		panel_1.add(scrollPane_1);
		
		tableGroupActive = new JTable();
		tableGroupActive.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tableGroupActive.getRowCount()>0) {
					System.out.println(tableGroupActive.getValueAt(tableGroupActive.getSelectedRow(), 1).toString());
					
					DefaultTableModel dtmProcess = new DefaultTableModel();
					
			        Vector<Vector<Object>>  data = new  Vector<>();
			        Vector<String>          cols = new Vector<>();

			        cols.add("procID");
			        cols.add("order");
			        cols.add("critical");
			        
			        List<dataClass.Process> lstProcess;
			        
			        for (int i=0; i<gDatos.getLstGroups().size(); i++) {
			        	if (gDatos.getLstGroups().get(i).getGrpID().equals(tableGroupActive.getValueAt(tableGroupActive.getSelectedRow(), 0))) {
				        	lstProcess = new ArrayList<>();
				        	lstProcess = gDatos.getLstGroups().get(i).getLstProcess();
				        	
				        	for (int j=0; j<lstProcess.size(); j++) {
				        		Vector<Object> row = new Vector<>();
				        		row.add(lstProcess.get(j).getProcID());
				        		row.add(lstProcess.get(j).getnOrder());
				        		row.add(lstProcess.get(j).getCritical());
				        		data.add(row);
				        		
				        	}
			        	}
			        }
			        dtmProcess.setDataVector(data, cols);
			        tablePool.setModel(dtmProcess);
				}
			}
		});
		tableGroupActive.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		scrollPane_1.setViewportView(tableGroupActive);
		
		JButton btnGrpActive = new JButton("Grupos Activos");
		btnGrpActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Asigna Tabla Local a Tabla Generica
					gDatos.setlTableGroup(tableGroup);
					gDatos.setJtableGroupActive(tableGroupActive);
					gDatos.setjTablePools(tablePool);
					tableGroup.setGridColor(Color.GRAY);
					tableGroupActive.setGridColor(Color.GRAY);
					tablePool.setGridColor(Color.GRAY);
					Thread thKeepAlive = new thKeepAlive(gDatos);
					thKeepAlive.setName("thKeepAlive");
					thKeepAlive.start();
				} catch (Exception ex) {
					logger.error("Error Starting thKeepAlive: "+ex.getMessage());
				}
			}
		});
		btnGrpActive.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnGrpActive.setBounds(117, 176, 129, 29);
		frame.getContentPane().add(btnGrpActive);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(20, 382, 430, 84);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(0, 0, 430, 84);
		panel_2.add(scrollPane_2);
		
		tablePool = new JTable();
		tablePool.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		scrollPane_2.setViewportView(tablePool);
	}
}
