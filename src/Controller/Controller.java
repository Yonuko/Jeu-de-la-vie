package Controller;

import Models.Grid;

public class Controller implements IController {

	private Grid grid;
	
	public static void main(String[] args) {
		new ChoseVue();
	}
	
	@Override
	public void GenerateGrid(int x, int y, boolean isRandom) {
		/**Cette m�thode permet de g�n�rer une nouvelle grille avec les parametres entr�s par l'utilisateur dans la vue*/
		grid = new Grid(x, y, isRandom);
	}
	
	@Override
	public void UndoGrid() {
		/**Cette methode permet de faire revenir la grille � l'�tape pr�c�dente*/
		grid.Undo();
	}
	
	@Override
	public void ChangeCellState(int x, int y) {
		/**Cette m�thode permet de modifie la valeur de la cellules (ne peut �tre appeller qu'� la premi�re g�n�ration)*/
		if(grid.GetInfo("genCount") == 0) {
			grid.ChangeCellState(x, y);
		}
	}
	
	@Override
	public void UpdateGrid() {
		/**Cette m�thode permet de mettre � jour la grille /!\ Met � jour dans le model pas dans la vue*/
		grid.UpdateGrid();
	}
	
	@Override
	public void UpdateUntilStop(Runnable vueUpdateMethod, Runnable disableButtonMethod, int threadSpeed) {
		/**Cette m�thode permet de mettre � jour la grille et son affichage jusqu'� ce que le boutton d'arr�t soit utilis�*/
		grid.UpdateUntilStop(vueUpdateMethod, disableButtonMethod, threadSpeed);
	}
	
	@Override
	public void StopUpdating() {
		/**Cette m�thode est appell�e une fois que le boutton d'arr�t ai �t� utilis�, elle arr�te la mise a jour automatique de la grille*/
		grid.StopUpdating();
	}
	
	@Override
	public boolean GetCellState(int x, int y) {
		/**Cette m�thode indique si la cellules positionn�e en x et y est vivante ou non*/
		return grid.GetCellState(x, y);
	}
	
	@Override
	public int GetGridInfo(String name) {
		/**Cette m�thode permet de r�cup�rer les diff�rentes information de la grille (nb_ligne, nb_colone, num�ro de g�n�ration)*/
		return grid.GetInfo(name);
	}

	@Override
	public void ResetCurrentGame() {
		/**Cette m�thode permet de revenir � la premi�re g�n�ration*/
		grid.Reset();
	}
	
	@Override
	public boolean CanGoBackWard() {
		/**Cette m�thode permet d'indiquer s'il est possible ou non de revenir en arri�re*/
		return grid.CanGoBackward();
	}
	
	@Override
	public void ClearGrid() {
		/**Cette m�thode permet de tuer toutes les cellules de la grille*/
		grid.ClearGrid();
	}
	
	public void UpdateThreadSpeed(int threadSpeed) {
		/**Cette fonction permet de modifier la vitesse de chaque g�n�ration lorsque le programme est en mode "automatique"*/
		grid.UpdateThreadSpeed(threadSpeed);
	}
}