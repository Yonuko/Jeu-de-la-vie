package Interfaces;

import java.util.InputMismatchException;
import java.util.Scanner;

import Controller.Controller;

public class ConsoleInterface {
	
	Controller controller;
	
	Scanner input = new Scanner(System.in);

	public ConsoleInterface(Controller controller) {
		this.controller = controller;
		int x, y;
		System.out.println("De quelle taille doit être votre grille ? ");
		try {
			System.out.print("Ligne ? ");
			x = input.nextInt();
			System.out.print("Colone ? ");
			y = input.nextInt();
		}catch(InputMismatchException e) {
			System.out.println("La valeur entrée n'est pas correcte");
			return;
		}
		System.out.println("Voulez vous générer la grille aléatoirement ? (y/n)");
		char awnser = input.next().toLowerCase().charAt(0);
		boolean isRandom = (awnser == 'y');
		this.controller.GenerateGrid(x, y, isRandom);
		PlaceCells(isRandom);
		ConsoleOptionMessage();
	}
	
	private void PlaceCells(boolean isRandom) {
		/**Cette méthode permet de modifier l'état de la cellules choisit par l'utilisateur*/
		boolean endModify = false;
		int x, y;
		// Si la grille n'est pas générée aléatoirement, et que l'utilisateur n'a pas quitter la modification, on continue
		while(!endModify && !isRandom) {
			try {
				System.out.print("Quelle ligne voulez vous modifier (0 pour quitter) ? ");
				x = input.nextInt();
				System.out.print("Quelle cologne voulez vous modifier (0 pour quitter) ? ");
				y = input.nextInt();
			}catch(InputMismatchException e) {
				System.out.println("La valeur entrée n'est pas correcte");
				return;
			}
			// Entrer 0 quitte la modification
			if(x == 0 || y == 0) {
				break;
			}
			this.controller.ChangeCellState(x - 1, y - 1);
			ShowGrid();
		}
	}
	
	/**Cette méthode permet d'effectuer l'action choisit par l'utilisateur*/
	private void ConsoleOption() {
		int option;
		try{
			option = input.nextInt();
		}catch(InputMismatchException e) {
			System.out.println("La valeur entrée n'est pas un entier");
			return;
		}
		switch(option)
		{
		case 0:
			// Quitte le programme
			Runtime.getRuntime().exit(0);
		case 1:
			// Passe à la génération suivante jusqu'à se que le boutton d'arrêt soit appuié
			GenerateGenUntilEnd();
			break;
		case 2:
			// Passe le nombre de génération choisit par l'utilisateur
			MakeACountOfGen();
			break;
		case 3:
			// Revins à la génération 0
			controller.ResetCurrentGame();
			break;
		case 4:
			// Reviens à la génération précédente si c'est possible
			if(this.controller.CanGoBackWard()) {
				this.controller.UndoGrid();
			}
			break;
		default:
			System.out.println("La valeur entrée ne corresponds a aucune option");
			break;
		}
		// Affiche de nouveau le menu des choix
		ConsoleOptionMessage();
	}
	
	/**Cette méthode affiche la grille actuel, affiche le menu de choix et permet au programme de récupérer le choix de l'utilisateur*/
	private void ConsoleOptionMessage() {
		ShowGrid();
		ShowMainMessage();
		ConsoleOption();
	}
	
	/**Cette méthode permet d'avancer de x nombre de génération, x étant une valeur entré par l'utilisateur*/
	private void MakeACountOfGen() {
		int number;
		try {
			System.out.println("Combien de génération voulez vous voir ?");
			number = input.nextInt();
		}catch(InputMismatchException e) {
			System.out.println("Vous n'avez pas entré un nombre");
			return;
		}
		// Execute les génération jusqu'à la fin du compte
		for(int i = 0; i < number; i++) {
			controller.UpdateGrid();
			ShowGrid();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**Cette méthode passe les générations jusqu'à ce que la touche d'arrêt sois pressée*/
	private void GenerateGenUntilEnd() {
		System.out.println("/!\\ Attention, pour arrêter l'itérateur, appuier sur x + entrée\n(appuier sur une touche + entrée pour continuer)");
		// Attends que l'utilisateur appuie sur la touche entrée pour commencer les itérations, cela permet à l'utilisateur de lire le message
		while(input.next() == null) {continue;}
		controller.UpdateUntilStop(this::ShowGrid, 
				new Runnable() {@Override public void run() {
						System.out.println("Appuier sur x + entrée pour continuer...");
						return;
					}
				}, 20);
		while(true) {
			// Si on appuie sur x, on arrête la mise à jour automatique
			if(input.next().charAt(0) == 'x') {
				controller.StopUpdating();
				return;
			}
		}
	}
	
	/**Cette méthode permet d'afficher la grille*/
	private void ShowGrid() {
		for(int i = 0; i < controller.GetGridInfo("rowSize"); i++) {
			for(int j = 0; j < controller.GetGridInfo("colSize"); j++) {
				System.out.print(controller.GetCellState(i, j) ? "X" : "0");
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	/**Cette méthode affiche toutes les options de l'utilisateur*/
	private void ShowMainMessage() {
		System.out.println("Nous sommes a la génération n°" + controller.GetGridInfo("genCount"));
		System.out.println("Que voulez vous faire ? (0 pour quitter) : ");
		System.out.println("- Lancer des générations jusqu'à la touche d'arrêt ? (1)");
		System.out.println("- Executer un certain nombre de génération ? (2)");
		System.out.println("- Revenir à la première génération ? (3)");
		if(controller.CanGoBackWard()) {
			System.out.println("- Revenir une génération en arrière ? (4)");
		}
	}
	
}
