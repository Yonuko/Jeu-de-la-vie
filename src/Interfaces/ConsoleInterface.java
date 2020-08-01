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
		System.out.println("De quelle taille doit �tre votre grille ? ");
		try {
			System.out.print("Ligne ? ");
			x = input.nextInt();
			System.out.print("Colone ? ");
			y = input.nextInt();
		}catch(InputMismatchException e) {
			System.out.println("La valeur entr�e n'est pas correcte");
			return;
		}
		System.out.println("Voulez vous g�n�rer la grille al�atoirement ? (y/n)");
		char awnser = input.next().toLowerCase().charAt(0);
		boolean isRandom = (awnser == 'y');
		this.controller.GenerateGrid(x, y, isRandom);
		PlaceCells(isRandom);
		ConsoleOptionMessage();
	}
	
	private void PlaceCells(boolean isRandom) {
		/**Cette m�thode permet de modifier l'�tat de la cellules choisit par l'utilisateur*/
		boolean endModify = false;
		int x, y;
		// Si la grille n'est pas g�n�r�e al�atoirement, et que l'utilisateur n'a pas quitter la modification, on continue
		while(!endModify && !isRandom) {
			try {
				System.out.print("Quelle ligne voulez vous modifier (0 pour quitter) ? ");
				x = input.nextInt();
				System.out.print("Quelle cologne voulez vous modifier (0 pour quitter) ? ");
				y = input.nextInt();
			}catch(InputMismatchException e) {
				System.out.println("La valeur entr�e n'est pas correcte");
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
	
	/**Cette m�thode permet d'effectuer l'action choisit par l'utilisateur*/
	private void ConsoleOption() {
		int option;
		try{
			option = input.nextInt();
		}catch(InputMismatchException e) {
			System.out.println("La valeur entr�e n'est pas un entier");
			return;
		}
		switch(option)
		{
		case 0:
			// Quitte le programme
			Runtime.getRuntime().exit(0);
		case 1:
			// Passe � la g�n�ration suivante jusqu'� se que le boutton d'arr�t soit appui�
			GenerateGenUntilEnd();
			break;
		case 2:
			// Passe le nombre de g�n�ration choisit par l'utilisateur
			MakeACountOfGen();
			break;
		case 3:
			// Revins � la g�n�ration 0
			controller.ResetCurrentGame();
			break;
		case 4:
			// Reviens � la g�n�ration pr�c�dente si c'est possible
			if(this.controller.CanGoBackWard()) {
				this.controller.UndoGrid();
			}
			break;
		default:
			System.out.println("La valeur entr�e ne corresponds a aucune option");
			break;
		}
		// Affiche de nouveau le menu des choix
		ConsoleOptionMessage();
	}
	
	/**Cette m�thode affiche la grille actuel, affiche le menu de choix et permet au programme de r�cup�rer le choix de l'utilisateur*/
	private void ConsoleOptionMessage() {
		ShowGrid();
		ShowMainMessage();
		ConsoleOption();
	}
	
	/**Cette m�thode permet d'avancer de x nombre de g�n�ration, x �tant une valeur entr� par l'utilisateur*/
	private void MakeACountOfGen() {
		int number;
		try {
			System.out.println("Combien de g�n�ration voulez vous voir ?");
			number = input.nextInt();
		}catch(InputMismatchException e) {
			System.out.println("Vous n'avez pas entr� un nombre");
			return;
		}
		// Execute les g�n�ration jusqu'� la fin du compte
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
	
	/**Cette m�thode passe les g�n�rations jusqu'� ce que la touche d'arr�t sois press�e*/
	private void GenerateGenUntilEnd() {
		System.out.println("/!\\ Attention, pour arr�ter l'it�rateur, appuier sur x + entr�e\n(appuier sur une touche + entr�e pour continuer)");
		// Attends que l'utilisateur appuie sur la touche entr�e pour commencer les it�rations, cela permet � l'utilisateur de lire le message
		while(input.next() == null) {continue;}
		controller.UpdateUntilStop(this::ShowGrid, 
				new Runnable() {@Override public void run() {
						System.out.println("Appuier sur x + entr�e pour continuer...");
						return;
					}
				}, 20);
		while(true) {
			// Si on appuie sur x, on arr�te la mise � jour automatique
			if(input.next().charAt(0) == 'x') {
				controller.StopUpdating();
				return;
			}
		}
	}
	
	/**Cette m�thode permet d'afficher la grille*/
	private void ShowGrid() {
		for(int i = 0; i < controller.GetGridInfo("rowSize"); i++) {
			for(int j = 0; j < controller.GetGridInfo("colSize"); j++) {
				System.out.print(controller.GetCellState(i, j) ? "X" : "0");
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	/**Cette m�thode affiche toutes les options de l'utilisateur*/
	private void ShowMainMessage() {
		System.out.println("Nous sommes a la g�n�ration n�" + controller.GetGridInfo("genCount"));
		System.out.println("Que voulez vous faire ? (0 pour quitter) : ");
		System.out.println("- Lancer des g�n�rations jusqu'� la touche d'arr�t ? (1)");
		System.out.println("- Executer un certain nombre de g�n�ration ? (2)");
		System.out.println("- Revenir � la premi�re g�n�ration ? (3)");
		if(controller.CanGoBackWard()) {
			System.out.println("- Revenir une g�n�ration en arri�re ? (4)");
		}
	}
	
}
