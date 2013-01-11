package com.labyrinthdash;

import android.util.Log;

/**
 * Creates and holds all the cells that make up the map
 * 
 * @author Isaac Doub
 */
public class GameMap {
	/** 2D array defining the cells in the map */
	public GameCell[][] Map;
	/** Cell the player starts in */
	public GameCell startCell;
	/** Cell the player is trying to reach*/
	public GameCell endCell;
	/** How many cells horizontally */
	protected int columns = 20;
	/** How many cells vertically */
	protected int rows = 20;
	
	/**
	 * Finds and returns a cell in which the provided point lies.
	 * @param position screen position a cell will cover
	 * @return GameCell in which the point provided lies
	 */
	public GameCell getCellContaining(Vector2D position) {
		GameCell tmpCell = Map[0][0];
		int x = (int)position.x/tmpCell.width;
		int y = (int)position.y/tmpCell.height;
		return Map[x][y];
	}
	
	/**
	 * Finds and returns an array of cells containing and surrounding the
	 * position provided. This is used to limit the number of cells the player
	 * calls collision on.
	 * @param position
	 * @return Array of GameCell surrounding the position provided
	 */
	public GameCell[] getCellsInProximity(Vector2D position) {
		GameCell tmpCell = Map[0][0];
		int x = (int)position.x/tmpCell.width;
		int y = (int)position.y/tmpCell.height;
		GameCell[] returnable = null;
		if(x == 0) {
			if(y == 0) {
				returnable = new GameCell[]{Map[x][y], Map[x][y+1], Map[x+1][y], Map[x+1][y+1]};
			} else if(y == rows){
				returnable = new GameCell[]{Map[x][y-1], Map[x][y], Map[x+1][y-1], Map[x+1][y]};
			} else {
				returnable = new GameCell[]{Map[x][y-1], Map[x][y], Map[x][y+1], Map[x+1][y-1], Map[x+1][y], Map[x+1][y+1]};
			}
		} else if(x == columns){
			if(y == 0) {
				returnable = new GameCell[]{Map[x-1][y], Map[x-1][y+1], Map[x][y], Map[x][y+1]};
			} else if(y == rows){
				returnable = new GameCell[]{Map[x-1][y-1], Map[x-1][y], Map[x][y-1], Map[x][y]};
			} else {
				returnable = new GameCell[]{Map[x-1][y-1], Map[x-1][y], Map[x-1][y+1], Map[x][y-1], Map[x][y], Map[x][y+1]};
			}
		} else {
			if(y == 0) {
				returnable = new GameCell[]{Map[x-1][y], Map[x-1][y+1], Map[x][y], Map[x][y+1], Map[x+1][y], Map[x+1][y+1]};
			} else if(y == rows){
				returnable = new GameCell[]{Map[x-1][y-1], Map[x-1][y], Map[x][y-1], Map[x][y],  Map[x+1][y-1], Map[x+1][y]};
			} else {
				returnable = new GameCell[]{Map[x-1][y-1], Map[x-1][y], Map[x-1][y+1], Map[x][y-1], Map[x][y], Map[x][y+1], Map[x+1][y-1], Map[x+1][y], Map[x+1][y+1]};
			}
		}
		return returnable;
	}
}

class Map1 extends GameMap {
	public Map1() {
		constructMap();
		Log.i("INFO", "New map created");
	}
	
	public Map1(int r, int c) {
		rows = r;
		columns = c;
		constructMap();
		Log.i("INFO", "New map created");
	}
	
	private void constructMap() {
		Map = new GameCell[columns][rows];

		for(int i=0; i<columns; i++) {
			for(int j=0; j<rows; j++) {
				Map[i][j] = new EmptyCell(R.drawable.spaceblack,i,j);
			}
		}

		Map[1][1] = new Platform(R.drawable.metalplatform,1,1);
		Map[2][1] = new Platform(R.drawable.metalplatform,2,1);
		Map[2][0] = new Platform(R.drawable.metalstart,2,0);
		
		Map[0][1] = new Platform(R.drawable.metalplatform,0,1);
		Map[0][2] = new Platform(R.drawable.metalplatform,0,2);
		Map[0][3] = new Platform(R.drawable.metalplatform,0,3);
		Map[0][4] = new Platform(R.drawable.metalplatform,0,4);
		Map[0][5] = new Jump(R.drawable.metaljump,0,5);
		Map[0][6] = new Platform(R.drawable.metalplatform,0,6);
		Map[0][7] = new Platform(R.drawable.metalplatform,0,7);
		Map[0][8] = new Platform(R.drawable.metalplatform,0,8);
		Map[0][9] = new Platform(R.drawable.metalplatform,0,9);
		Map[0][10] = new Platform(R.drawable.metalplatform,0,10);

		Map[1][10] = new Platform(R.drawable.metalplatform,1,10);
		Map[2][10] = new Platform(R.drawable.metalplatform,2,10);
		Map[3][10] = new Platform(R.drawable.metalplatform,3,10);
		Map[4][10] = new Platform(R.drawable.metalplatform,4,10);
		Map[5][10] = new BoostUp(R.drawable.metalboostup,5,10);	
		Map[5][9] = new Platform(R.drawable.metalplatform,5,9);
		Map[5][8] = new Platform(R.drawable.metalplatform,5,8);
		Map[5][7] = new Jump(R.drawable.metaljump,5,7);
		Map[4][7] = new Platform(R.drawable.metalplatform,4,7);		
		Map[3][7] = new Platform(R.drawable.metalplatform,3,7);

		Map[4][8] = new Platform(R.drawable.metalplatform,4,8);
		Map[4][9] = new Platform(R.drawable.metalplatform,4,9);
		Map[3][6] = new Platform(R.drawable.metalplatform,3,6);
		Map[3][5] = new Platform(R.drawable.metalplatform,3,5);

		Map[2][7] = new Platform(R.drawable.metalplatform,2,7);
		Map[2][6] = new Platform(R.drawable.metalplatform,2,6);
		Map[2][5] = new Platform(R.drawable.metalplatform,2,5);
		Map[2][4] = new BoostRight(R.drawable.metalboostright,2,4);
		
		Map[3][4] = new Platform(R.drawable.metalplatform,3,4);
		Map[4][4] = new Platform(R.drawable.metalplatform,4,4);
		
		Map[5][4] = new Platform(R.drawable.metalplatform,5,4);
		Map[5][3] = new Platform(R.drawable.metalplatform,5,3);
		Map[5][2] = new Platform(R.drawable.metalplatform,5,2);
		Map[5][1] = new Platform(R.drawable.metalplatform,5,1);
	
		Map[4][0] = new PlatformEnd(R.drawable.metalend,4,0);
		Map[4][1] = new Platform(R.drawable.metalplatform,4,1);		
		
		startCell = Map[2][0];
	}
}

class Map2 extends GameMap {
	public Map2() {
		constructMap();
		Log.i("INFO", "New map created");
	}
	
	public Map2(int r, int c) {
		rows = r;
		columns = c;
		constructMap();
		Log.i("INFO", "New map created");
	}
	
	private void constructMap() {
		Map = new GameCell[columns][rows];

		for(int i=0; i<columns; i++) {
			for(int j=0; j<rows; j++) {
				Map[i][j] = new EmptyCell(R.drawable.spaceblack,i,j);
			}
		}

		Map[1][1] = new Platform(R.drawable.metalplatform,1,1);
		Map[2][1] = new Platform(R.drawable.metalplatform,2,1);
		Map[2][0] = new Platform(R.drawable.metalstart,2,0);
		
		Map[0][1] = new Platform(R.drawable.metalplatform,0,1);
		Map[0][2] = new Platform(R.drawable.metalplatform,0,2);
		Map[0][3] = new Platform(R.drawable.metalplatform,0,3);
		Map[0][4] = new Platform(R.drawable.metalplatform,0,4);
		Map[0][5] = new Jump(R.drawable.metaljump,0,5);
		Map[0][6] = new Platform(R.drawable.metalplatform,0,6);
		
		Map[5][4] = new Platform(R.drawable.metalplatform,5,4);
		Map[5][3] = new Platform(R.drawable.metalplatform,5,3);
		Map[5][2] = new Platform(R.drawable.metalplatform,5,2);
		Map[5][1] = new Platform(R.drawable.metalplatform,5,1);
	
		Map[4][0] = new PlatformEnd(R.drawable.metalend,4,0);
		Map[4][1] = new Platform(R.drawable.metalplatform,4,1);		
		
		startCell = Map[2][0];
	}
}
