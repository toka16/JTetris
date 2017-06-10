package com.coolcompany.jtetris;

public interface BoardAPI {

	int getWidth();

	int getHeight();

	int getMaxHeight();

	int getColumnHeight(int x);

	int getRowWidth(int y);

	boolean getGrid(int x, int y);

	int place(Piece piece, int x, int y);

	int clearRows();

	void undo();

	void commit();

}
