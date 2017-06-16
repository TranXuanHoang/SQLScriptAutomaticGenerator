package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This class provides a GUI that enables user to enter database connection
 * information (e.g., Server Name, DB name, ...)
 * 
 * @author Tran Xuan Hoang
 */
public class DBConnectionSetup extends JDialog {
	/**
	 * Default serial version.
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	public JTextField serverNameTextField;
	public JTextField dbNameTextField;

	/**
	 * Create the dialog.
	 */
	public DBConnectionSetup(GeneratorFrame mainFrame) {
		setTitle("\u30C7\u30FC\u30BF\u30D9\u30FC\u30B9\u3078\u306E\u63A5\u7D9A\u306E\u8A2D\u5B9A");
		setBounds(100, 100, 450, 170);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(15, 5, 5, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			JPanel labelPanel = new JPanel();
			labelPanel.setBorder(new EmptyBorder(0, 15, 0, 4));
			contentPanel.add(labelPanel);
			labelPanel.setLayout(new GridLayout(0, 1, 0, 0));
			{
				JLabel lblNewLabel = new JLabel("Server Name: ");
				lblNewLabel.setBorder(new EmptyBorder(0, 0, 3, 0));
				lblNewLabel.setFont(new Font("Calibri", Font.BOLD, 14));
				labelPanel.add(lblNewLabel);
			}
			{
				JLabel label = new JLabel("");
				labelPanel.add(label);
			}
			{
				JLabel lblNewLabel_1 = new JLabel("Database Name: ");
				lblNewLabel_1.setBorder(new EmptyBorder(4, 0, 0, 0));
				lblNewLabel_1.setFont(new Font("Calibri", Font.BOLD, 14));
				labelPanel.add(lblNewLabel_1);
			}
		}
		{
			JPanel fieldPanel = new JPanel();
			contentPanel.add(fieldPanel);
			fieldPanel.setLayout(new GridLayout(0, 1, 0, 0));
			{
				serverNameTextField = new JTextField();
				serverNameTextField.setToolTipText("\u30B5\u30FC\u30D0\u30FC\u540D\u3092\u5165\u529B");
				serverNameTextField.setForeground(new Color(0, 0, 205));
				serverNameTextField.setFont(new Font("Calibri", Font.PLAIN, 14));
				fieldPanel.add(serverNameTextField);
				serverNameTextField.setColumns(20);
			}
			{
				JLabel lblNewLabel_2 = new JLabel("");
				fieldPanel.add(lblNewLabel_2);
			}
			{
				dbNameTextField = new JTextField();
				dbNameTextField.setToolTipText("\u30C7\u30FC\u30BF\u30D9\u30FC\u30B9\u540D\u3092\u5165\u529B");
				dbNameTextField.setForeground(new Color(0, 0, 205));
				dbNameTextField.setFont(new Font("Calibri", Font.PLAIN, 14));
				fieldPanel.add(dbNameTextField);
				dbNameTextField.setColumns(20);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setToolTipText("\u4FDD\u5B58\u3059\u308B");
				okButton.setFont(new Font("Calibri", Font.PLAIN, 14));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						mainFrame.setDataBaseConnection(
								serverNameTextField.getText().trim(),
								dbNameTextField.getText().trim());
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(new Font("Calibri", Font.PLAIN, 14));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
} // end class DBConnectionSetup