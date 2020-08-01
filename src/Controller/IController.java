package Controller;

public interface IController {
	/**Cette interface indique toutes les méthode que doit utiliser le controller pour que le jeu de la vie puisse fonctionner*/
	public abstract void GenerateGrid(int x, int y, boolean isRandom);
	public abstract void UndoGrid();
	public abstract void UpdateGrid();
	public abstract void UpdateUntilStop(Runnable vueUpdateMethod, Runnable disableButtonMethod, int threadSpeed);
	public abstract void StopUpdating();
	public abstract void ResetCurrentGame();
	public abstract boolean GetCellState(int x, int y);
	public abstract void ChangeCellState(int x, int y);
	public abstract int GetGridInfo(String name);
	public abstract boolean CanGoBackWard();
	public abstract void ClearGrid();
	
}
