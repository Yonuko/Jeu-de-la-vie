package Interfaces;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Controller.Controller;

@SuppressWarnings("serial")
public class GraphiqueGenerateGrid extends JFrame{
	/**Cette classe sers à rentrer les valeurs de la grille pour la version graphique*/
	
	Controller controller;
	
	JTextField rowInput, colInput;
	JCheckBox isRandomInput;
	
	// Ce listener permet de créer une sort de placeHolder, c'est à dire afficher un text informatif dans le champ, et le supprime lorsque l'on écrit
	MouseListener placeHolderMouseListerner = new MouseListener() {
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseClicked(MouseEvent e) {
			if(e.getSource().equals(rowInput)) {
				if(rowInput.getText().equals("Entrer le nombre de ligne...")) {
					rowInput.setText("");
				}
				if(colInput.getText().equals("")) {					
					colInput.setText("Entrer le nombre de colone...");
				}
			}else {
				if(colInput.getText().equals("Entrer le nombre de colone...")) {
					colInput.setText("");
				}
				if(rowInput.getText().equals("")) {					
					rowInput.setText("Entrer le nombre de ligne...");
				}
			}
		}
	};
	
	public GraphiqueGenerateGrid(Controller controller) {
		/**Ce constructeur gère l'agensement des différents éléments de l'interface graphique*/
		super("Jeu de la vie");
		this.setResizable(false);
		this.setSize(500, 150);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.controller = controller;
		
		JPanel mainPanel = (JPanel)this.getContentPane();
		JPanel valuesPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		rowInput = new JTextField("Entrer le nombre de ligne...");
		colInput = new JTextField("Entrer le nombre de colone...");
		isRandomInput = new JCheckBox("Remplir aléatoirement ?");
		
		rowInput.addMouseListener(placeHolderMouseListerner);
		rowInput.addActionListener((s) -> GiveFocus());
		colInput.addMouseListener(placeHolderMouseListerner);
		colInput.addActionListener((s) -> GenerateGrid());
		
		JButton validateButton = new JButton("Valider");
		validateButton.addActionListener((s) -> GenerateGrid());
		
		valuesPanel.add(rowInput);
		valuesPanel.add(colInput);
		valuesPanel.add(isRandomInput);
		
		buttonPanel.add(validateButton);
		
		mainPanel.add(valuesPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	private void GenerateGrid() {
		/**Cette fonction permet de créer la grille et l'afficher grâce à l'interface graphique*/
		int x = 0, y = 0;
		try {
			x = Integer.parseInt(rowInput.getText());
			y = Integer.parseInt(colInput.getText());
		}catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Le nombre de ligne ou le nombre de colone entée n'est pas un nombre entier.",
					"Erreur dans une valeur", JOptionPane.ERROR_MESSAGE);
			return;
		}
		controller.GenerateGrid(x, y, isRandomInput.isSelected());
		// Change le curseur de la souris par le curseur d'attente de windows le temps que l'interface graphique affiche la grille
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		new GraphicInterface(controller);
		this.dispose();
	}
	
	private void GiveFocus() {
		/**Cette méthode donne le focus à l'input colone, elle est appellée lorsque l'on appuis sur entrer dans le champ rowInput*/
		if(colInput.getText().equals("Entrer le nombre de colone...")) {
			colInput.setText("");
		}
		colInput.requestFocus();
	}
}
