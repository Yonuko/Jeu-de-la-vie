package Controller;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Interfaces.ConsoleInterface;
import Interfaces.GraphiqueGenerateGrid;

@SuppressWarnings("serial")
public class ChoseVue extends JFrame{
	
	public ChoseVue() {
		/**Cette classe permet à l'utilisateur de choisir sa vue pour le jeu de la vie*/
		super("Jeu de la vie");
		this.setSize(300, 100);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		JPanel mainPanel = (JPanel)this.getContentPane();
		mainPanel.setLayout(new FlowLayout());
		
		JButton graphiqueButton = new JButton("Interface Graphique");
		graphiqueButton.addActionListener((s) -> LaunchVue(true));
		JButton consoleButton = new JButton("Interface console");
		consoleButton.addActionListener((s) -> LaunchVue(false));
		
		mainPanel.add(graphiqueButton);
		mainPanel.add(consoleButton);
		this.setVisible(true);
	}
	
	private void LaunchVue(boolean isGraphique) {
		/**Cette méthode lance la vue que l'utilisateur à choisit*/
		this.dispose();
		if(isGraphique) {
			new GraphiqueGenerateGrid(new Controller());
		}else {
			new ConsoleInterface(new Controller());
		}
	}

}
