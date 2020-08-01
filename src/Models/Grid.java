package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import javax.swing.JOptionPane;

public class Grid {

	// Attributs privés
	// Créer un tableau à double dimension d'entier stockant les cellules
	private ArrayList<ArrayList<Integer>> tab = new ArrayList<ArrayList<Integer>>();
	// Créer un tableau à double dimension d'entier stockant le nombre de cellules vivante autour de chaque cellule
	private ArrayList<ArrayList<Integer>> neighboursTab = new ArrayList<ArrayList<Integer>>();
	// Garde en mémoire 10 étapes du jeu de la vie
	private ArrayList<ArrayList<ArrayList<Integer>>> undoTab = new ArrayList<ArrayList<ArrayList<Integer>>>();
	
	// Ce thread permet de mettre à jour la grille jusqu'à ce qu'il y ait un boutton d'arrêt qui soit pressé
	private StopingButtonThread buttonThread = null;
	
	private HashMap<String, Integer> gridInfos = new LinkedHashMap<String, Integer>();
	
	// Constructeur de la class
	public Grid(int x, int y, boolean isRandom) {
		// Vérifie si la grille possède des valeurs correcte
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
		// Création de la grille
		for(int i = 0; i < x; i++) {
			this.tab.add(new ArrayList<Integer>());
			for(int j = 0; j < y; j++) {
				// Si isRandom est egal à true, génère la grille de manière aléatoire
				this.tab.get(i).add(isRandom ? rand.nextInt(2) : 0);
			}
		}
		// Création de la grille comportant le nombre de voisins
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			this.neighboursTab.add(new ArrayList<Integer>());
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.neighboursTab.get(i).add(0);
			}
		}
	}
	
	/** Cette méthode inverse la case séléctionnée*/
	public void ChangeCellState(int x, int y) {
		this.tab.get(x).set(y, (this.tab.get(x).get(y) == 1) ? 0 : 1);
	}
	
	/** Cette méthode met à jour les valeur du tableau et passe à la génération suivante*/
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
					// Si une cellule morte est entourée de 3 vivantes, elle vie
					this.tab.get(i).set(j, 1);
				}else if(countOfCells > 3) {
					// Si une cellule est entourée de plus de 3 cellules vivante, elle meurt
					this.tab.get(i).set(j, 0);
				}else if(countOfCells > 1 && this.tab.get(i).get(j) != 0){
					// Si une cellule est entourée de 2 ou 3 cellules vivante, elle vie
					this.tab.get(i).set(j, 1);
				}else {
					// Si une cellule est entourée de moins de 1 cellule, elle meurt.
					this.tab.get(i).set(j, 0);
				}
			}
		}
		
		// Augmente le nombre de génération
		gridInfos.replace("genCount", gridInfos.get("genCount") + 1);
		AddUndo();
	}
	
	/** Cette méthode calcule le nombre de voisins de chaque celulues*/
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
	
	/**Cette méthode permet de garder en mémoire la génération actuelle avant de passer à la suivante pour pouvoir revenir en arrière*/
	private void AddUndo() {
		// Ajoute commme premier éléments de la liste Undo, l'état actuel du tableau
		this.undoTab.add(new ArrayList<ArrayList<Integer>>());
		// Ajoute les valeur de la grille actuelle
		for(int i = 0; i < gridInfos.get("rowSize"); i++) {
			this.undoTab.get(this.undoTab.size() - 1).add(new ArrayList<Integer>());
			for(int j = 0; j < gridInfos.get("colSize"); j++) {
				this.undoTab.get(this.undoTab.size() - 1).get(i).add(this.tab.get(i).get(j));
			}
		}
	}
	
	/** Cette méthode permet de revenir une génération en avant*/
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
	
	/**Cette méthode permet de revenir à la génération 0*/
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
	
	/**Cette méthode permet de vider la grille /!\ ne fonctionne que si la génération actuelle est la génération 0*/
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
	
	/**Cette class permet de passer à la génération suivante jusqu'à se que le joueur utilise le boutton d'arrêt*/
	private class StopingButtonThread extends Thread{
		Runnable vueUpdateMethod, disableButtonMethod;
		int iterationCount = 0, threadSpeed;
		
		public StopingButtonThread(Runnable vueUpdateMethod, Runnable disableButtonMethod, int threadSpeed) {
			this.vueUpdateMethod = vueUpdateMethod;
			this.disableButtonMethod = disableButtonMethod;
			this.threadSpeed = threadSpeed;
		}
		
		public void run() {
			// Si le thread a éxecuté 2000 itération, on l'arrêt pour éviter le StackOverflow
			if(++iterationCount > 2000) {
				if(disableButtonMethod != null) {					
					disableButtonMethod.run();
				}
				StopUpdating();
				return;
			}
			UpdateGrid();
			// Appelle la méthode d'affichage passer en paramètre par la vue
			vueUpdateMethod.run();
			try {
				Thread.sleep(threadSpeed);
			} catch (InterruptedException e) {
				return;
			}
			run();
		}
	}
	
	/**Cette méthode déclanche le thread qui va passer à la génération suivante jusqu'à ce que la méthode StopUpdating() soit appellée*/
	public void UpdateUntilStop(Runnable vueUpdateMethod, Runnable disableButtonMethod, int threadSpeed) {
		if(buttonThread == null) {
			buttonThread = new StopingButtonThread(vueUpdateMethod, disableButtonMethod, threadSpeed);
		}
		buttonThread.start();
	}
	
	/**Cette méthode permet de modifier la vitesse que va mettre le Thread pour passer à la génération suivante*/
	public void UpdateThreadSpeed(int threadSpeed) {
		buttonThread.threadSpeed = threadSpeed;
	}
	
	/**Cette méthode permet d'arrêter le Thread et donc arrêter de passer à la génération suivante de manière "automatique"*/
	public void StopUpdating() {
		if(buttonThread != null) {
			buttonThread.interrupt();
			buttonThread = null;
		}
	}
	
	/**Cette méthode peremet de renvoyer les paramètres de la grille (ex : rowSize => nombre de ligne)*/
	public int GetInfo(String name) {
		return gridInfos.get(name);
	}
	
	/**Cette méthode permet de récupérer si la cellules est vivantes ou morte*/
	public boolean GetCellState(int x, int y) {
		return tab.get(x).get(y) == 1;
	}
	
	/**Cette méthode indique s'il est possible de revenir une génération en arrière ou non*/
	public boolean CanGoBackward() {
		return gridInfos.get("genCount") > 0;
	}
}
