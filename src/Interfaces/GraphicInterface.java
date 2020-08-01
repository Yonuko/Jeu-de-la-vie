package Interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Controller.Controller;

@SuppressWarnings("serial")
public class GraphicInterface extends JFrame {
	
	private Controller controller;

	private JPanel mainGame = new JPanel();
	private JLabel genLabel = new JLabel();
	private JSlider cellSpeed = new JSlider(JSlider.HORIZONTAL, 10, 200, 50);
	JButton undoButton;
	JButton updateButton;
	
	private boolean StopUpdate = false;
	
	public GraphicInterface(Controller controller) {
		super("Game of life");
		
		this.controller = controller;
		
		this.setLocationRelativeTo(null);
		// Maximise la fenêtre.
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel panel = (JPanel)this.getContentPane();
		
		mainGame.setBackground(Color.gray);
		// On modifie le layout en GridLayout ayant pour valeur la taille de la grille, pour pouvoir afficher toutes les cellules dans le bon format
		mainGame.setLayout(new GridLayout(controller.GetGridInfo("rowSize"), controller.GetGridInfo("colSize"), 3, 3));
		
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout());
		headerPanel.add(genLabel);
		// Ce boutton permet de remettre vider la grille si on est sur la première génération
		JButton clearButton = new JButton("Effacer");
		clearButton.addActionListener((s) -> ClearGrid());
		// Ce boutton permet de revenir à la première génération
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener((s) -> Reset());
		// Ce boutton permet de revenir à la génération précédente
		undoButton = new JButton("Précédent");
		undoButton.addActionListener((s) -> Undo());
		// Ce boutton permet de passer à la génération suivante
		JButton nextButton = new JButton("Suivant");
		nextButton.addActionListener((s) -> UpdateGame());
		// Ce boutton permet de passer automatiquement à la génération suivante jusqu'à ce que le boutton soit de nouveau appuiés
		updateButton = new JButton("Mise à jour automatique");
		updateButton.addActionListener((s) -> ActivateUpdateButton());
		// Ajoute tous les bouttons sur le panel supérieur
		headerPanel.add(clearButton);
		headerPanel.add(resetButton);
		headerPanel.add(undoButton);
		headerPanel.add(nextButton);
		headerPanel.add(updateButton);
		headerPanel.add(cellSpeed);
		
		// Génère tous les Labels, et ajoute un listener qui permet de changer l'état de la cellules lorsque l'on clique dessus
		for(int i = 0; i < controller.GetGridInfo("rowSize"); i++) {
			for(int j = 0; j < controller.GetGridInfo("colSize"); j++) {
				JLabel labelTemp = new JLabel(" ");
				labelTemp.setOpaque(true);
				labelTemp.addMouseListener(new MouseListener() {
					@Override public void mouseReleased(MouseEvent arg0) {}
					@Override public void mousePressed(MouseEvent arg0) {}
					@Override public void mouseExited(MouseEvent arg0) {}
					@Override public void mouseEntered(MouseEvent arg0) {}
					@Override public void mouseClicked(MouseEvent e) {
						if(controller.GetGridInfo("genCount") != 0) { return; }
						int i = 0, j = 0;
						// Récupère toutes les cellules du panel pricipale
						for(Component comp : mainGame.getComponents()) {
							if(comp instanceof JLabel) {
								// Si on a atteins la colone maximal de la ligne courante, on passe à la ligne suivante
								if(j == controller.GetGridInfo("colSize")) {
									j = 0;
									// On passe à la ligne suivante uniquement si on n'est pas sur la dernière ligne
									if(i < controller.GetGridInfo("rowSize") - 1) {
										i++;
									}
								}
								// Si l'element actuel est celui sur lequel on a cliqué, on modifie son état
								if(comp.equals(e.getSource())) {
									// Si la cellules était vivante, elle meurt, et inversement
									controller.ChangeCellState(i, j);
									((JLabel)comp).setBackground((controller.GetCellState(i, j)) ? Color.black : Color.white);
									return;
								}
								j++;
							}
						}
					}
				});
				mainGame.add(labelTemp);
			}
		}
		// Met à jour l'affichage de la grille
		UpdateShow();
		
		// Lorsque l'on change la valeur du slider, on met à jour la vitesse du Thread
		cellSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(StopUpdate) {
					controller.UpdateThreadSpeed(cellSpeed.getValue());
				}
			}
		});
		
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(mainGame, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	private void ActivateUpdateButton() {
		/**Cette méthode permet de passer à la génération suivante automatiquement tant que cette fonction n'est pas appeller de nouveau*/
		if(!StopUpdate) {
			updateButton.setText("Arrêter");
			controller.UpdateUntilStop(this::UpdateShow, new Runnable(){public void run() {StopUpdate = false;}}, cellSpeed.getValue());
		}else {
			updateButton.setText("Mise à jour automatique");
			controller.StopUpdating();
		}
		StopUpdate = !StopUpdate;
	}
	
	void UpdateGame() {
		/**Cette méthode permet de passer à la génération suivante*/
		controller.UpdateGrid();
		UpdateShow();
	}
	
	void Undo() {
		/**Cette méthode permet de revenir à la génération précedente*/
		controller.UndoGrid();
		UpdateShow();
	}
	
	void Reset() {
		/**Cette méthode permet de revenir à la génération 0*/
		// Si le boutton de mise à jour est pressé, on le désactive
		if(StopUpdate) {
			ActivateUpdateButton();
			try {
				// On fait patienter le programme le temps d'une génération pour éviter que les première ligne de la grille soit modifier
				// par la mise à jour automatique
				Thread.sleep(cellSpeed.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Affiche la grille de la génération 0
		controller.ResetCurrentGame();
		UpdateShow();
	}
	
	private void ClearGrid() {
		/**Cette méthode permet de tuer toutes les cellules de la grille (ne fonctionne que sur la génération 0)*/
		controller.ClearGrid();
		UpdateShow();
	}
	
	void UpdateShow() {
		/**Cette méthode met à jour l'affichage de la grille*/
		this.genLabel.setText(Integer.toString(controller.GetGridInfo("genCount")));
		int i = 0, j = 0;
		for(Component comp : mainGame.getComponents()) {
			// On récupère tous les JLabel du panel principal
			if(comp instanceof JLabel) {
				// Si on atteinds la dernière colone, on passe à la ligne suivante
				if(j == controller.GetGridInfo("colSize")) {
					j = 0;
					// Si on n'est pas sur la dernière ligne, on passe à la ligne suivante
					if(i < controller.GetGridInfo("rowSize") - 1) {
						i++;
					}
				}
				// Affiche la couleur de la cellules en fonction de son état (noir si vivante, blanche si morte)
				((JLabel)comp).setBackground((controller.GetCellState(i, j)) ? Color.black : Color.white);
				j++;
			}
		}
		// Si il est impossible de revenir en arrière, on désactive le boutton Undo
		undoButton.setEnabled(controller.CanGoBackWard());
		mainGame.repaint();
		mainGame.revalidate();
	}
}
