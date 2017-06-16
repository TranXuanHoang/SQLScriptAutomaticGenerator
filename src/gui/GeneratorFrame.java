package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import sql.SQLExecutor;
import sql.SQLGenerator;
import java.awt.Toolkit;

/**
 * This class implements the main GUI of the application.
 * 
 * @author Tran Xuan Hoang
 */
public class GeneratorFrame extends JFrame {
	/**
	 * Default serial version id.
	 */
	private static final long serialVersionUID = 1L;

	protected String excelFilePath = null;
	protected String sqlFolderPath = null;
	protected File[] sqlFilePaths = null;

	protected String serverName = null;
	protected String dbName = null;

	private JPanel contentPane;
	JFileChooser fileChooser;
	JFileChooser folderChooser;
	JFileChooser sqlFileChooser;
	JLabel loadedFileSymbol;
	JLabel generatedSQLSymbol;
	JLabel statusLabel;
	JTextArea executeSQLTextArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Error: unable to set look and feel");
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GeneratorFrame frame = new GeneratorFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
	} // end method main

	/**
	 * Create the frame (the GUI of the app).
	 */
	public GeneratorFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(GeneratorFrame.class.getResource("/gui/icons/App Icon.PNG")));
		setMinimumSize(new Dimension(530, 345));
		setTitle("APIのSQLファイル作成・実行");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 400);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem openExcelFileMenuItem = new JMenuItem("Excel\u30D5\u30A1\u30A4\u30EB\u3092\u958B\u304F");
		openExcelFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openExcelFileButton_actionPerformed(e);
			}
		});
		fileMenu.add(openExcelFileMenuItem);

		JMenuItem destinationFolderMenuItem = new JMenuItem("SQL\u30D5\u30A1\u30A4\u30EB\u4FDD\u5B58\u5834\u6240");
		destinationFolderMenuItem.setToolTipText("SQLファイル保存場所を指定する");
		destinationFolderMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				specifySQLFileSavingFolderButton_actionPerformed(e);
			}
		});
		fileMenu.add(destinationFolderMenuItem);

		JCheckBoxMenuItem openSQLFilesMenuItem = new JCheckBoxMenuItem("SQLファイルを選択");
		openSQLFilesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openSQLFilesMenuItem_actionPerformed(e);
			}
		});
		openSQLFilesMenuItem.setToolTipText("実行されるSQLファイルを選択する");
		fileMenu.add(openSQLFilesMenuItem);

		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);

		JMenu runMenu = new JMenu("Run");
		menuBar.add(runMenu);

		JMenuItem genSQLFiles = new JMenuItem("SQL\u30D5\u30A1\u30A4\u30EB\u3092\u4F5C\u6210");
		genSQLFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				genSQLButton_actionPerformed(e);
			}
		});
		genSQLFiles.setToolTipText("SQLファイルを作成する");
		runMenu.add(genSQLFiles);

		JMenuItem executeSQLsMenuItem = new JMenuItem("SQLファイルを実行");
		executeSQLsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeSQLsMenuItem_actionPerformed(e);
			}
		});
		executeSQLsMenuItem.setToolTipText("選択したSQLファイルを実行し、データベースを更新する");
		runMenu.add(executeSQLsMenuItem);

		JMenu setupMenu = new JMenu("Setting");
		menuBar.add(setupMenu);

		JMenuItem setupDBMenuItem = new JMenuItem("データベースへの接続を設定");
		setupDBMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setupDBMenuItem_actionPerformed(e);
			}
		});
		setupDBMenuItem.setToolTipText("データベースに接続するための設定をする");
		setupMenu.add(setupDBMenuItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(2, 2, 0, 0));

		JPanel generateSQLsPanel = new JPanel();
		centerPanel.add(generateSQLsPanel);
		generateSQLsPanel.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel loadFileResultPanel = new JPanel();
		generateSQLsPanel.add(loadFileResultPanel);
		loadFileResultPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		loadFileResultPanel.setLayout(new BorderLayout(0, 0));

		loadedFileSymbol = new JLabel("Excelファイルをロードしてください");
		loadFileResultPanel.add(loadedFileSymbol, BorderLayout.CENTER);

		JPanel generatedSQLPanel = new JPanel();
		generateSQLsPanel.add(generatedSQLPanel);
		generatedSQLPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
		generatedSQLPanel.setLayout(new BorderLayout(0, 0));

		generatedSQLSymbol = new JLabel("");
		generatedSQLPanel.add(generatedSQLSymbol);

		JPanel executeSQLsPanel = new JPanel();
		centerPanel.add(executeSQLsPanel);
		executeSQLsPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		executeSQLsPanel.add(scrollPane);

		executeSQLTextArea = new JTextArea();
		executeSQLTextArea.setEditable(false);
		executeSQLTextArea.setFont(new Font("ＭＳ Ｐゴシック", Font.PLAIN, 12));
		scrollPane.setViewportView(executeSQLTextArea);

		JPanel southPanel = new JPanel();
		contentPane.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(0, 2, 10, 10));

		statusLabel = new JLabel("");
		southPanel.add(statusLabel);

		JPanel progressPanel = new JPanel();
		progressPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		FlowLayout flowLayout = (FlowLayout) progressPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		southPanel.add(progressPanel);

		//		JLabel progressLabel = new JLabel("\u9032\u6357\uFF1A");
		//		progressLabel.setToolTipText("Progress");
		//		progressLabel.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 14));
		//		progressPanel.add(progressLabel);
		//		
		//		JProgressBar progressBar = new JProgressBar();
		//		progressBar.setStringPainted(true);
		//		progressBar.setFont(new Font("Calibri", Font.PLAIN, 14));
		//		progressPanel.add(progressBar);

		JPanel northPanel = new JPanel();
		northPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(northPanel, BorderLayout.NORTH);

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel File", "xlsx");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Excelファイルを選択");

		folderChooser = new JFileChooser();
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		folderChooser.setDialogTitle("SQLファイルの保存場所を指定");

		sqlFileChooser = new JFileChooser();
		sqlFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		sqlFileChooser.setMultiSelectionEnabled(true);
		FileNameExtensionFilter sqlFilter = new FileNameExtensionFilter("SQL File", "sql");
		sqlFileChooser.setFileFilter(sqlFilter);
		sqlFileChooser.setDialogTitle("SQLファイルを選択");

		JButton openExcelFileButton = new JButton();
		openExcelFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openExcelFileButton_actionPerformed(e);
			}
		});
		openExcelFileButton.setToolTipText("Excelファイルを開く");
		openExcelFileButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		openExcelFileButton.setIcon(getIcon("/gui/icons/Open.png"));
		northPanel.add(openExcelFileButton);

		JButton specifySQLFileSavingFolderButton = new JButton("");
		specifySQLFileSavingFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				specifySQLFileSavingFolderButton_actionPerformed(e);
			}
		});
		specifySQLFileSavingFolderButton.setToolTipText("SQLファイル保存場所を指定");
		specifySQLFileSavingFolderButton.setIcon(getIcon("/gui/icons/Folder.png"));
		northPanel.add(specifySQLFileSavingFolderButton);

		JButton genSQLButton = new JButton("SQL作成");
		genSQLButton.setPreferredSize(new Dimension(100, 33));
		genSQLButton.setForeground(new Color(30, 144, 255));
		genSQLButton.setFont(new Font("MS UI Gothic", Font.BOLD, 16));
		genSQLButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				genSQLButton_actionPerformed(e);
			}
		});
		genSQLButton.setToolTipText("SQLファイルを作成");
		northPanel.add(genSQLButton);
	}

	/**
	 * Handles the event when the user clicks the button to load the excel file.
	 * @param e the mouse event of clicking the [Excelファイルを開く] button.
	 */
	protected void openExcelFileButton_actionPerformed(ActionEvent e) {
		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		// Clear the icon and text of the label that notifies whether
		// the previous Excel file was read correctly
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				statusLabel.setIcon(null);
				statusLabel.setText("");
			}
		});

		// save the directory currently have just been loaded
		String currentDir = fileChooser.getSelectedFile().toString();
		currentDir = currentDir.substring(0, currentDir.lastIndexOf(File.separatorChar));
		fileChooser.setCurrentDirectory(new File(currentDir));

		File selectedFile = fileChooser.getSelectedFile();

		loadedFileSymbol.setIcon(getIcon("/gui/icons/Excel.png", 55, 55));

		String showLoadedFilePath = selectedFile.toString();
		showLoadedFilePath = "..." + showLoadedFilePath.substring(
				showLoadedFilePath.lastIndexOf("\\"));

		loadedFileSymbol.setText(showLoadedFilePath);
		loadedFileSymbol.setToolTipText(selectedFile.toString());

		if (sqlFolderPath == null) {
			generatedSQLSymbol.setText("作成されるSQLファイルの保存場所を指定してください");
		}

		excelFilePath = selectedFile.toString();
	}

	/**
	 * Handles the event when the user clicks the button to specify a location to save
	 * the SQL scripts that will be generated by the application.
	 * @param e the mouse event of clicking the [SQLファイルの保存場所を指定] button.
	 */
	protected void specifySQLFileSavingFolderButton_actionPerformed(ActionEvent e) {
		int result = folderChooser.showOpenDialog(this);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		// save the directory currently have just been selected
		String currentDir = folderChooser.getSelectedFile().toString();
		currentDir = currentDir.substring(0, currentDir.lastIndexOf(File.separatorChar));
		folderChooser.setCurrentDirectory(new File(currentDir));

		File selectedFolder = folderChooser.getSelectedFile();

		generatedSQLSymbol.setIcon(getIcon("/gui/icons/SQL Folder.png", 55, 55));

		String showLoadedFolderPath = selectedFolder.toString();
		showLoadedFolderPath = "..." + showLoadedFolderPath.substring(
				showLoadedFolderPath.lastIndexOf("\\"));

		generatedSQLSymbol.setText(showLoadedFolderPath);
		generatedSQLSymbol.setToolTipText(selectedFolder.toString());

		if (excelFilePath == null) {
			loadedFileSymbol.setText("Excelファイルをロードしてください");
		}

		sqlFolderPath = selectedFolder.toString();
	}

	/**
	 * Handles the event when user clicks the button to generate SQL scripts.
	 * @param e the event of clicking the [SQL作成] button.
	 */
	protected void genSQLButton_actionPerformed(ActionEvent e) {
		if (sqlFolderPath == null) {
			generatedSQLSymbol.setText("作成されるSQLファイルの保存場所を指定してください");
		} else if (excelFilePath == null) {
			loadedFileSymbol.setText("Excelファイルをロードしてください");
		} else {
			try {
				SQLGenerator.readDataFromExcelFile(excelFilePath);
			} catch (Exception exception) {
				statusLabel.setIcon(getIcon("/gui/icons/Incorrect.png"));
				statusLabel.setText("Excelファイルの構造が正しくありません");
				return;
			}

			try {
				SQLGenerator.generateSQLScripts(sqlFolderPath);
			} catch (Exception exception) {
				statusLabel.setIcon(getIcon("/gui/icons/Incorrect.png"));
				statusLabel.setText("SQLファイルを作成できません");
				return;
			}

			statusLabel.setIcon(getIcon("/gui/icons/Complete.png"));
			statusLabel.setText("SQLファイルの作成が成功");
		}
	}

	/**
	 * Handles the event when user clicks the menu item to execute one or all selected SQL scripts.
	 * @param e the event of clicking the [Run > SQLファイルを実行] menu item.
	 */
	protected void executeSQLsMenuItem_actionPerformed(ActionEvent e) {
		if (sqlFilePaths == null) {
			displayMSG("\n\nまず、「File」　>　「SQLファイルを選択」 をクリックし、SQLファイルをロードしてください。\n");
		} else if (serverName == null || dbName == null ||
				serverName.equals("") || dbName.equals("")) {
			displayMSG("\n\nSQLファイルを実行する前に、「Setting」　"
					+ ">　「タベースへの接続を設定」 をクリックし、データベースへの接続を設定してください。\n");
		} else {
			int successfullyExecuted = 0;

			for (File sqlFile : sqlFilePaths) {
				String sqlFilePath = sqlFile.toString();

				if (SQLExecutor.runSQLScript(serverName, dbName, sqlFilePath)) {
					displayMSG("\n  実行が成功しました　－　" + sqlFilePath);
					successfullyExecuted++;
				} else {
					displayMSG("\n  実行が失敗しました\\　－　" + sqlFilePath);
				}
			}

			if (successfullyExecuted == sqlFilePaths.length) {
				displayMSG("\n\nすべての" + successfullyExecuted + "個のSQLファイルが成功に実行されました。\n");
			} else if (successfullyExecuted > 0) {
				displayMSG("\n\n" + successfullyExecuted + "個のSQLファイルの実行が成功しましたが、" +
						"\n" + (sqlFilePaths.length - successfullyExecuted) + "個のSQLファイルの実行が失敗しました。" +
						"\n実行が失敗したファイルの内容が正しいかどうかもう一度チェックしてください。\n");
			} else {
				displayMSG("\n\nすべてのSQLファイルの実行が失敗しました。\n" +
						"データベースへの接続の設定とファイルの内容が正しいかどうかもう一度チェックしてください。\n");
			}
		}
	}

	/**
	 * Displays String messages into the {@link #executeSQLTextArea} text area.
	 * The message will be appended at the end of the text area.
	 * @param msg the message to be displayed.
	 */
	protected void displayMSG(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				executeSQLTextArea.append(msg);
			}
		});
	}

	/**
	 * Handles the event when user clicks the menu item to load SQL scripts that
	 * will be executed later on.
	 * @param e the event of clicking the [Run > SQLファイル保存場所を指定] menu item.
	 */
	protected void openSQLFilesMenuItem_actionPerformed(ActionEvent e) {
		int result = sqlFileChooser.showOpenDialog(this);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		// save the directory currently have just been loaded
		String currentDir = sqlFileChooser.getSelectedFile().toString();
		currentDir = currentDir.substring(0, currentDir.lastIndexOf(File.separatorChar));
		sqlFileChooser.setCurrentDirectory(new File(currentDir));

		sqlFilePaths = sqlFileChooser.getSelectedFiles();

		// Update GUI
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				executeSQLTextArea.setText("次のSQLファイルが選択されました。\n");

				for (File sqlFilePath : sqlFilePaths) {
					executeSQLTextArea.append("\n　　" + sqlFilePath.toString());
				}

				executeSQLTextArea.append("\n\n上記のSQLファイルを実行すれば、「Run」　>　「SQLファイルを実行」 をクリックしてください。");
			}
		});
	}

	/**
	 * Handles the event when user clicks the menu item to specify the information
	 * of the database that will be updated by executing selected SQL scripts.
	 * @param e the event of clicking the [Setting > データベースへの接続を設定] menu item.
	 */
	protected void setupDBMenuItem_actionPerformed(ActionEvent e) {
		try {
			DBConnectionSetup dialog = new DBConnectionSetup(this);

			dialog.dbNameTextField.setText(dbName == null ? "" : dbName);
			dialog.serverNameTextField.setText(serverName == null ? "" : serverName);

			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(this);
			dialog.setModal(true);
			dialog.setResizable(false);
			dialog.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Sets up the information for connecting to the <b>database</b>
	 * that is located in the <b>server</b>.
	 * @param serverName the name of the server in which the <b>database</b> is located.
	 * @param dbName the name of the database to which the application will
	 * connect.
	 */
	public void setDataBaseConnection(String serverName, String dbName) {
		this.serverName = serverName;
		this.dbName = dbName;
	}

	/**
	 * Extract icon for menu items in menu bar.
	 * @param fileName the name of the icon image file.
	 * @return <code>ImageIcon</code> for the image file.
	 */
	private ImageIcon getIcon(String fileName) {
		ImageIcon icon = new ImageIcon(
				getClass().getResource(fileName));
		icon = resizeIcon(icon, 25, 25);

		return icon;
	}

	/**
	 * Resize (increase/reduce) the image size (width and height) to a required size.
	 * @param fileName the path of the image to be resized.
	 * @param width the new width of the image.
	 * @param height the new height of the image.
	 * @return the resized image.
	 */
	private ImageIcon getIcon(String fileName, int width, int height) {
		ImageIcon icon = new ImageIcon(
				getClass().getResource(fileName));
		icon = resizeIcon(icon, width, height);

		return icon;
	}

	/**
	 * Resizes an icon to the new icon with specified width and height.
	 * @param icon the old icon to be resized.
	 * @param width the width of the new icon.
	 * @param height the height of the new icon.
	 * @return a new icon with the new size.
	 */
	public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

		return new ImageIcon(newImage);
	}
} // end class GeneratorFrame