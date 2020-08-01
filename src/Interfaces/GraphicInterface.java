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
		// Maximise la fen�tre.
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
		// Ce boutton permet de remettre vider la grille si on est sur la premi�re g�n�ration
		JButton clearButton = new JButton("Effacer");
		clearButton.addActionListener((s) -> ClearGrid());
		// Ce boutton permet de revenir � la premi�re g�n�ration
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener((s) -> Reset());
		// Ce boutton permet de revenir � la g�n�ration pr�c�dente
		undoButton = new JButton("Pr�c�dent");
		undoButton.addActionListener((s) -> Undo());
		// Ce boutton permet de passer � la g�n�ration suivante
		JButton nextButton = new JButton("Suivant");
		nextButton.addActionListener((s) -> UpdateGame());
		// Ce boutton permet de passer automatiquement � la g�n�ration suivante jusqu'� ce que le boutton soit de nouveau appui�s
		updateButton = new JButton("Mise � jour automatique");
		updateButton.addActionListener((s) -> ActivateUpdateButton());
		// Ajoute tous les bouttons sur le panel sup�rieur
		headerPanel.add(clearButton);
		headerPanel.add(resetButton);
		headerPanel.add(undoButton);
		headerPanel.add(nextButton);
		headerPanel.add(updateButton);
		headerPanel.add(cellSpeed);
		
		// G�n�re tous les Labels, et ajoute un listener qui permet de changer l'�tat de la cellules lorsque l'on clique dessus
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
						// R�cup�re toutes les cellules du panel pricipale
						for(Component comp : mainGame.getComponents()) {
							if(comp instanceof JLabel) {
								// Si on a atteins la colone maximal de la ligne courante, on passe � la ligne suivante
								if(j == controller.GetGridInfo("colSize")) {
									j = 0;
									// On passe � la ligne suivante uniquement si on n'est pas sur la derni�re ligne
									if(i < controller.GetGridInfo("rowSize") - 1) {
										i++;
									}
								}
								// Si l'element actuel est celui sur lequel on a cliqu�, on modifie son �tat
								if(comp.equals(e.getSource())) {
									// Si la cellules �tait vivante, elle meurt, et inversement
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
		// Met � jour l'affichage de la grille
		UpdateShow();
		
		// Lorsque l'on change la valeur du slider, on met � jour la vitesse du Thread
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
		/**Cette m�thode permet de passer � la g�n�ration suivante automatiquement tant que cette fonction n'est pas appeller de nouveau*/
		if(!StopUpdate) {
			updateButton.setText("Arr�ter");
			controller.UpdateUntilStop(this::UpdateShow, new Runnable(){public void run() {StopUpdate = false;}}, cellSpeed.getValue());
		}else {
			updateButton.setText("Mise � jour automatique");
			controller.StopUpdating();
		}
		StopUpdate = !StopUpdate;
	}
	
	void UpdateGame() {
		/**Cette m�thode permet de passer � la g�n�ration suivante*/
		controller.UpdateGrid();
		UpdateShow();
	}
	
	void Undo() {
		/**Cette m�thode permet de revenir � la g�n�ration pr�cedente*/
		controller.UndoGrid();
		UpdateShow();
	}
	
	void Reset() {
		/**Cette m�thode permet de revenir � la g�n�ration 0*/
		// Si le boutton de mise � jour est press�, on le d�sactive
		if(StopUpdate) {
			ActivateUpdateButton();
			try {
				// On fait patienter le programme le temps d'une g�n�ration pour �viter que les premi�re ligne de la grille soit modifier
				// par la mise � jour automatique
				Thread.sleep(cellSpeed.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Affiche la grille de la g�n�ration 0
		controller.ResetCurrentGame();
		UpdateShow();
	}
	
	private void ClearGrid() {
		/**Cette m�thode permet de tuer toutes les cellules de la grille (ne fonctionne que sur la g�n�ration 0)*/
		controller.ClearGrid();
		UpdateShow();
	}
	
	void UpdateShow() {
		/**Cette m�thode met � jour l'affichage de la grille*/
		this.genLabel.setText(Integer.toString(controller.GetGridInfo("genCount")));
		int i = 0, j = 0;
		for(Component comp : mainGame.getComponents()) {
			// On r�cup�re tous les JLabel du panel principal
			if(comp instanceof JLabel) {
				// Si on atteinds la derni�re colone, on passe � la ligne suivante
				if(j == controller.GetGridInfo("colSize")) {
					j = 0;
					// Si on n'est pas sur la derni�re ligne, on passe � la ligne suivante
					if(i < controller.GetGridInfo("rowSize") - 1) {
						i++;
					}
				}
				// Affiche la couleur de la cellules en fonction de son �tat (noir si vivante, blanche si morte)
				((JLabel)comp).setBackground((controller.GetCellState(i, j)) ? Color.black : Color.white);
				j++;
			}
		}
		// Si il est impossible de revenir en arri�re, on d�sactive le boutton Undo
		undoButton.setEnabled(controller.CanGoBackWard());
		mainGame.repaint();
		mainGame.revalidate();
	}
}
