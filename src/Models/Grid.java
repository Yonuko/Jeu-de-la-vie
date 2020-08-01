package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import javax.swing.JOptionPane;

public class Grid {

	// Attributs priv�s
	// Cr�er un tableau � double dimension d'entier stockant les cellules
	private ArrayList<ArrayList<Integer>> tab = new ArrayList<ArrayList<Integer>>();
	// Cr�er un tableau � double dimension d'entier stockant le nombre de cellules vivante autour de chaque cellule
	private ArrayList<ArrayList<Integer>> neighboursTab = new ArrayList<ArrayList<Integer>>();
	// Garde en m�moire 10 �tapes du jeu de la vie
	private ArrayList<ArrayList<ArrayList<Integer>>> undoTab = new ArrayList<ArrayList<ArrayList<Integer>>>();
	
	// Ce thread permet de mettre � jour la grille jusqu'� ce qu'il y ait un boutton d'arr�t qui soit press�
	private StopingButtonThread buttonThread = null;
	
	private HashMap<String, Integer> gridInfos = new LinkedHashMap<String, Integer>();
	
	// Constructeur de la class
	public Grid(int x, int y, boolean isRandom) {
		// V�rifie si la grille poss�de des valeurs correcte
		if(x > 250 || y > 250) {
			JOptionPane.showMessageDialog(null, "Erreur, la taille maximum pour une grille et de 240 x 250, le programme va ajuster les valeurs", 
					"Taille incorrecte", JOptionPane.ERROR_MESSAGE);
			x = x > 240 ? 240 : x;
			y = y > 250 ? 250 : y;
		}
		
		// Stock dans la hashmap toutes les informations de la grille
		gridInfos.put("rowSize", x);
		gridInfos.put("colSize", y);
		gridInfos.put("genCount", 0);
		
		Random rand = new Random();
		// Cr�ation de la grille
		for(int i = 0; i < x; i++) {
			this.tab.add(new ArrayList<Integer>());
			for(int j = 0; j < y; j++) {
				// Si isRandom est egal � true, g�n�re la grille de mani�re al�atoire
				this.tab.get(i).add(isRandom ? rand.nextInt(2) : 0);
			}
		}
		// Cr�ation de la grille comportant le nombre de voisins
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			this.neighboursTab.add(new ArrayList<Integer>());
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.neighboursTab.get(i).add(0);
			}
		}
	}
	
	/** Cette m�thode inverse la case s�l�ctionn�e*/
	public void ChangeCellState(int x, int y) {
		this.tab.get(x).set(y, (this.tab.get(x).get(y) == 1) ? 0 : 1);
	}
	
	/** Cette m�thode met � jour les valeur du tableau et passe � la g�n�ration suivante*/
	public void UpdateGrid() {
		if(gridInfos.get("genCount") == 0) {
			this.undoTab.clear();
			AddUndo();
		}
		// Stock dans un tableau le nombre de cellules vivantes autour de chaque cases
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.neighboursTab.get(i).set(j, CheckNeighbours(i, j));
			}
		}
		
		// Met a jour la grille en fonction des voisins de chaque cellules
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				int countOfCells = this.neighboursTab.get(i).get(j);
				if(this.tab.get(i).get(j) == 0 && countOfCells == 3) {
					// Si une cellule morte est entour�e de 3 vivantes, elle vie
					this.tab.get(i).set(j, 1);
				}else if(countOfCells > 3) {
					// Si une cellule est entour�e de plus de 3 cellules vivante, elle meurt
					this.tab.get(i).set(j, 0);
				}else if(countOfCells > 1 && this.tab.get(i).get(j) != 0){
					// Si une cellule est entour�e de 2 ou 3 cellules vivante, elle vie
					this.tab.get(i).set(j, 1);
				}else {
					// Si une cellule est entour�e de moins de 1 cellule, elle meurt.
					this.tab.get(i).set(j, 0);
				}
			}
		}
		
		// Augmente le nombre de g�n�ration
		gridInfos.replace("genCount", gridInfos.get("genCount") + 1);
		AddUndo();
	}
	
	/** Cette m�thode calcule le nombre de voisins de chaque celulues*/
	private int CheckNeighbours(int actualRow, int actualCol) {
		int countOfNeiboors = 0;
		for(int i = -1; i < 2; i++) {
			for(int j = -1; j < 2; j++) {
				if((i == 0 && j == 0) || (actualRow + i < 0 || actualRow + i >= gridInfos.get("rowSize")) 
						|| (actualCol + j < 0 || actualCol + j >= gridInfos.get("colSize"))) {
					continue;
				}
				countOfNeiboors += this.tab.get(actualRow+i).get(actualCol+j);
			}
		}
		return countOfNeiboors;
	}
	
	/**Cette m�thode permet de garder en m�moire la g�n�ration actuelle avant de passer � la suivante pour pouvoir revenir en arri�re*/
	private void AddUndo() {
		// Ajoute commme premier �l�ments de la liste Undo, l'�tat actuel du tableau
		this.undoTab.add(new ArrayList<ArrayList<Integer>>());
		// Ajoute les valeur de la grille actuelle
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			this.undoTab.get(this.undoTab.size() - 1).add(new ArrayList<Integer>());
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.undoTab.get(this.undoTab.size() - 1).get(i).add(this.tab.get(i).get(j));
			}
		}
	}
	
	/** Cette m�thode permet de revenir une g�n�ration en avant*/
	public void Undo() {
		if(gridInfos.get("genCount") == 0) {return;}
		gridInfos.replace("genCount", gridInfos.get("genCount") - 1);
		this.undoTab.remove(gridInfos.get("genCount") + 1);
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.tab.get(i).set(j, this.undoTab.get(gridInfos.get("genCount")).get(i).get(j));
			}
		}
	}
	
	/**Cette m�thode permet de revenir � la g�n�ration 0*/
	public void Reset() {
		if(this.undoTab.size() == 0) {
			return;
		}
		gridInfos.replace("genCount", 0);
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.tab.get(i).set(j, this.undoTab.get(gridInfos.get("genCount")).get(i).get(j));
			}
		}
		undoTab.clear();
		AddUndo();
	}
	
	/**Cette m�thode permet de vider la grille /!\ ne fonctionne que si la g�n�ration actuelle est la g�n�ration 0*/
	public void ClearGrid() {
		if(gridInfos.get("genCount") != 0) {
			return;
		}
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.tab.get(i).set(j, 0);
			}
		}
	}
	
	/**Cette class permet de passer � la g�n�ration suivante jusqu'� se que le joueur utilise le boutton d'arr�t*/
	private class StopingButtonThread extends Thread{
		Runnable vueUpdateMethod, disableButtonMethod;
		int iterationCount = 0, threadSpeed;
		
		public StopingButtonThread(Runnable vueUpdateMethod, Runnable disableButtonMethod, int threadSpeed) {
			this.vueUpdateMethod = vueUpdateMethod;
			this.disableButtonMethod = disableButtonMethod;
			this.threadSpeed = threadSpeed;
		}
		
		public void run() {
			// Si le thread a �xecut� 2000 it�ration, on l'arr�t pour �viter le StackOverflow
			if(++iterationCount > 2000) {
				if(disableButtonMethod != null) {					
					disableButtonMethod.run();
				}
				StopUpdating();
				return;
			}
			UpdateGrid();
			// Appelle la m�thode d'affichage passer en param�tre par la vue
			vueUpdateMethod.run();
			try {
				Thread.sleep(threadSpeed);
			} catch (InterruptedException e) {
				return;
			}
			run();
		}
	}
	
	/**Cette m�thode d�clanche le thread qui va passer � la g�n�ration suivante jusqu'� ce que la m�thode StopUpdating() soit appell�e*/
	public void UpdateUntilStop(Runnable vueUpdateMethod, Runnable disableButtonMethod, int threadSpeed) {
		if(buttonThread == null) {
			buttonThread = new StopingButtonThread(vueUpdateMethod, disableButtonMethod, threadSpeed);
		}
		buttonThread.start();
	}
	
	/**Cette m�thode permet de modifier la vitesse que va mettre le Thread pour passer � la g�n�ration suivante*/
	public void UpdateThreadSpeed(int threadSpeed) {
		buttonThread.threadSpeed = threadSpeed;
	}
	
	/**Cette m�thode permet d'arr�ter le Thread et donc arr�ter de passer � la g�n�ration suivante de mani�re "automatique"*/
	public void StopUpdating() {
		if(buttonThread != null) {
			buttonThread.interrupt();
			buttonThread = null;
		}
	}
	
	/**Cette m�thode peremet de renvoyer les param�tres de la grille (ex : rowSize => nombre de ligne)*/
	public int GetInfo(String name) {
		return gridInfos.get(name);
	}
	
	/**Cette m�thode permet de r�cup�rer si la cellules est vivantes ou morte*/
	public boolean GetCellState(int x, int y) {
		return tab.get(x).get(y) == 1;
	}
	
	/**Cette m�thode indique s'il est possible de revenir une g�n�ration en arri�re ou non*/
	public boolean CanGoBackward() {
		return gridInfos.get("genCount") > 0;
	}
}
