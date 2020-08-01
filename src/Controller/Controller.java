package Controller;

import Models.Grid;

public class Controller implements IController {

	private Grid grid;
	
	public static void main(String[] args) {
		new ChoseVue();
	}
	
	@Override
	public void GenerateGrid(int x, int y, boolean isRandom) {
		/**Cette méthode permet de générer une nouvelle grille avec les parametres entrés par l'utilisateur dans la vue*/
		grid = new Grid(x, y, isRandom);
	}
	
	@Override
	public void UndoGrid() {
		/**Cette methode permet de faire revenir la grille à l'étape précédente*/
		grid.Undo();
	}
	
	@Override
	public void ChangeCellState(int x, int y) {
		/**Cette méthode permet de modifie la valeur de la cellules (ne peut être appeller qu'à la première génération)*/
		if(grid.GetInfo("genCount") == 0) {
			grid.ChangeCellState(x, y);
		}
	}
	
	@Override
	public void UpdateGrid() {
		/**Cette méthode permet de mettre à jour la grille /!\ Met à jour dans le model pas dans la vue*/
		grid.UpdateGrid();
	}
	
	@Override
	public void UpdateUntilStop(Runnable vueUpdateMethod, Runnable disableButtonMethod, int threadSpeed) {
		/**Cette méthode permet de mettre à jour la grille et son affichage jusqu'à ce que le boutton d'arrêt soit utilisé*/
		grid.UpdateUntilStop(vueUpdateMethod, disableButtonMethod, threadSpeed);
	}
	
	@Override
	public void StopUpdating() {
		/**Cette méthode est appellée une fois que le boutton d'arrêt ai été utilisé, elle arrête la mise a jour automatique de la grille*/
		grid.StopUpdating();
	}
	
	@Override
	public boolean GetCellState(int x, int y) {
		/**Cette méthode indique si la cellules positionnée en x et y est vivante ou non*/
		return grid.GetCellState(x, y);
	}
	
	@Override
	public int GetGridInfo(String name) {
		/**Cette méthode permet de récupérer les différentes information de la grille (nb_ligne, nb_colone, numéro de génération)*/
		return grid.GetInfo(name);
	}

	@Override
	public void ResetCurrentGame() {
		/**Cette méthode permet de revenir à la première génération*/
		grid.Reset();
	}
	
	@Override
	public boolean CanGoBackWard() {
		/**Cette méthode permet d'indiquer s'il est possible ou non de revenir en arrière*/
		return grid.CanGoBackward();
	}
	
	@Override
	public void ClearGrid() {
		/**Cette méthode permet de tuer toutes les cellules de la grille*/
		grid.ClearGrid();
	}
	
	public void UpdateThreadSpeed(int threadSpeed) {
		/**Cette fonction permet de modifier la vitesse de chaque génération lorsque le programme est en mode "automatique"*/
		grid.UpdateThreadSpeed(threadSpeed);
	}
}