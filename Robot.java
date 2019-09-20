package minesweeper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import project1.MazeFrame;

public class Robot {
	int[][] knowledge;
	boolean[][] visited;
	int[][] map;
	int width;
	int height;
	ArrayList<Point> checkList;
	private static double ep = 1e-10;
	public Robot(int x, int y){
		this.width = x;
		this.height = y;
		knowledge = new int[x][y];
		map = new int[x][y];
		visited = new boolean[x][y];
		checkList = new ArrayList<>();
		//initial knowledge
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				knowledge[i][j] = Integer.MAX_VALUE;
			}
		}
		printKnowledge();
	}
	
	public void explore(int x, int y, ArrayList<Point> checkList){
		if(!visited[x][y]){
			visited[x][y] = true;
			checkList.add(new Point(x, y));
		}
	}
	
	public int play(double p){		
		map = createMap(width, height, p);
		printRealMatrix();
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				knowledge[i][j] = Integer.MAX_VALUE;
			}
		}
		visited = new boolean[width][height];
		checkList = new ArrayList<>();
		boolean isPlay = true;
		checkList.add(firstPick());
		int step = 0;
		boolean update = false;
		
		while(isPlay && (update || !checkList.isEmpty())){
			step ++;
			update = false;
			// 1.query and click
			if(!checkList.isEmpty()){
				Point curr = checkList.get(0);
				checkList.remove(0);
				int currX = curr.x;
				int currY = curr.y;
				//System.out.println("Click X:" + currX + " Y:" + currY + " found:" + query(currX, currY));
				// if it is mine, game over!
				if(query(currX, currY) == -1){
					isPlay = false;
					break;
				}
				else{
					knowledge[currX][currY] = query(currX, currY);
					if(knowledge[currX][currY] == 0){
						if(currX - 1 >= 0 && currY - 1 >= 0 && knowledge[currX - 1][currY - 1] == Integer.MAX_VALUE) explore(currX - 1, currY - 1,checkList);
						if(currX - 1 >= 0 && knowledge[currX - 1][currY] == Integer.MAX_VALUE) explore(currX - 1, currY,checkList);
						if(currX - 1 >= 0 && currY + 1 < height && knowledge[currX - 1][currY + 1] == Integer.MAX_VALUE) explore(currX - 1, currY + 1,checkList);
						
						if(currY - 1 >= 0 && knowledge[currX][currY - 1] == Integer.MAX_VALUE) explore(currX, currY - 1,checkList);
						if(currY + 1 < height && knowledge[currX][currY + 1] == Integer.MAX_VALUE) explore(currX, currY + 1,checkList);
						
						if(currX + 1 < width && currY - 1 >= 0 && knowledge[currX + 1][currY - 1] == Integer.MAX_VALUE) explore(currX + 1, currY - 1,checkList);
						if(currX + 1 < width && knowledge[currX + 1][currY] == Integer.MAX_VALUE) explore(currX + 1, currY,checkList);
						if(currX + 1 < width && currY + 1 < height && knowledge[currX + 1][currY + 1] == Integer.MAX_VALUE) explore(currX + 1, currY + 1,checkList);
					}
				}
			}
			
			// 2. simple check
			update = update || simpleCheck(checkList);
			update = update || gaussianCheck(checkList);
			if(!update){
				if(!checkKnowledge()) break;
				else {
					if(checkList.isEmpty()) checkList.add(randomPick());
				}
			}
			
			printKnowledge();	
			//gaussianCheck(checkList);
		}
		//printKnowledge();
//		if(checkKnowledge()) System.out.println("true found ?");
//		if(checkList.isEmpty()) System.out.println("checklist is empty");
		if(isPlay) {
			//System.out.println(" you win");
			printKnowledge();
			return 1;
		}
		else {
			//System.out.println("you lose");
			return 0;
		}
		
	}
	
	public boolean checkKnowledge(){
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(knowledge[i][j] == Integer.MAX_VALUE) return true;
			}
		}
		
		return false;
	}
	
	public boolean gaussianCheck(ArrayList<Point> checkList) {
		int dim = width * height;
		double[][] GMatrix = new double[dim][dim + 1];
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(knowledge[x][y] != -1 && knowledge[x][y] != Integer.MAX_VALUE && knowledge[x][y] != 0){
					int totalMines = knowledge[x][y];
					int unCovered = 0;
					if(x - 1 >= 0 && y - 1 >= 0){
						if(knowledge[x - 1][y - 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x - 1][y - 1] == -1) totalMines--;
					}
					if(x - 1 >= 0){
						if(knowledge[x - 1][y] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x - 1][y] == -1) totalMines--;
					}
					if(x - 1 >= 0 && y + 1 < height){ 
						if(knowledge[x - 1][y + 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x - 1][y + 1] == -1) totalMines--;
					}
					
					if(y - 1 >= 0){
						if(knowledge[x][y - 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x][y - 1] == -1) totalMines--;
					}
					if(y + 1 < height){
						if(knowledge[x][y + 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x][y + 1] == -1) totalMines--;
					}
					
					if(x + 1 < width && y - 1 >= 0){ 
						if(knowledge[x + 1][y - 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x + 1][y - 1] == -1) totalMines--;
					}
					if(x + 1 < width){ 
						if(knowledge[x + 1][y] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x + 1][y] == -1) totalMines--;
					}
					if(x + 1 < width && y + 1 < height){ 
						if(knowledge[x + 1][y + 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x + 1][y + 1] == -1) totalMines--;
					}
					if(unCovered == 0) continue;
					
					// for each 8 squares, generate vectors
					int row = x * height + y;
					if(x - 1 >= 0 && y - 1 >= 0){
						if(knowledge[x - 1][y - 1] == Integer.MAX_VALUE) {
							int col = (x - 1) * height + y - 1;
							GMatrix[row][col] = 1;
						}
						
					}
					if(x - 1 >= 0){
						if(knowledge[x - 1][y] == Integer.MAX_VALUE){
							int col = (x - 1) * height + y;
							GMatrix[row][col] = 1;
						}
						
					}
					if(x - 1 >= 0 && y + 1 < height){ 
						if(knowledge[x - 1][y + 1] == Integer.MAX_VALUE){
							int col = (x - 1) * height + y + 1;
							GMatrix[row][col] = 1;
						}
					}
					
					if(y - 1 >= 0){
						if(knowledge[x][y - 1] == Integer.MAX_VALUE){
							int col = x * height + y - 1;
							GMatrix[row][col] = 1;
						}
					}
					if(y + 1 < height){
						if(knowledge[x][y + 1] == Integer.MAX_VALUE){
							int col = x * height + y + 1;
							GMatrix[row][col] = 1;
						}
					}
					
					if(x + 1 < width && y - 1 >= 0){ 
						if(knowledge[x + 1][y - 1] == Integer.MAX_VALUE){
							int col = (x + 1) * height + y - 1;
							GMatrix[row][col] = 1;
						}
						
					}
					if(x + 1 < width){ 
						if(knowledge[x + 1][y] == Integer.MAX_VALUE){
							int col = (x + 1) * height + y;
							GMatrix[row][col] = 1;
						}
					}
					if(x + 1 < width && y + 1 < height){ 
						if(knowledge[x + 1][y + 1] == Integer.MAX_VALUE){
							int col = (x + 1) * height + y + 1;
							GMatrix[row][col] = 1;
						}
					}
					GMatrix[row][dim] = totalMines;
					
//					gaussianEliminate(GMatrix, dim);
//					gaussianDecision(checkList, GMatrix, dim);
				}
			}
			
		}
		gaussianEliminate(GMatrix, dim);
//		System.out.println("================= Gaussian Matrix ================");
//		for(int i = 0; i < dim; i++){
//			for(int j = 0; j < dim + 1; j++)
//				System.out.print(" " + GMatrix[i][j]);
//			
//			System.out.print("\n");
//		}
		return gaussianDecision(checkList, GMatrix, dim);
		
//		System.out.println("================= Gaussian Matrix ================");
//		for(int i = 0; i < dim; i++){
//			for(int j = 0; j < dim + 1; j++)
//				System.out.print(" " + GMatrix[i][j]);
//			
//			System.out.print("\n");
//		}
	}
	
	
	public boolean gaussianDecision(ArrayList<Point> checkList, double[][] Matrix, int dim){
		boolean result = false;
		// 1. calculate positive number and negative number
//		int positive = 0;
//		int negative = 0;
		for(int i = 0; i < dim; i++){
			int positive = 0;
			int negative = 0;
			for(int j = 0; j < dim ; j++){
				//System.out.print(" "+Matrix[i][j]);
				if(Matrix[i][j] > ep) positive++;
				if(Matrix[i][j] < -ep) negative--;
			}
			//System.out.println("\npositive: " + positive + " negative:"+negative);
			// positive == mines  means that postive squares must be mines and negative must be safe
			if(positive == Matrix[i][dim]){
				for(int j = 0; j < dim ; j++){
					int row = j / height ;
					int col = j % height;
					if(Matrix[i][j] > ep) {
						knowledge[row][col] = -1; 
						result = true;
						//System.out.println("Gaussian find mines X:"+row +" Y:"+col);
					}
					if(Matrix[i][j] < -ep) {
						explore(row, col, checkList);
						//System.out.println("Gaussian find safe X:"+row +" Y:"+col);
						//checkList.add(new Point(row, col)); 
						result = true;
					}
				}
			}
			
			// negative = mines  means that negative squares must be mines and positive must be safe
			if(negative == Matrix[i][dim]){
				for(int j = 0; j < dim ; j++){
					int row = j / height ;
					int col = j % height;
					if(Matrix[i][j] > ep) {
						explore(row, col, checkList);
						//System.out.println("Gaussian find safe X:"+row +" Y:"+col);
						result = true;
					}
					if(Matrix[i][j] < -ep) {
						knowledge[row][col] = -1; 
						//System.out.println("Gaussian find mines X:"+row +" Y:"+col);
						result = true;
					}
				}
			}
		}
		//if(result) System.out.println("Gaussian is Making Decision!");
		return result;
	}
	
	
	
	public void gaussianEliminate(double[][] Matrix, int dim){
		int pos = 0;
		for(int i = 0; i < dim; i++){
			for(int j = pos; j < dim; j++){
				if(Math.abs(Matrix[j][i]) > ep){
					// swap
					for(int k = i; k < dim + 1; k++) {
						double tmp = Matrix[j][k];
						Matrix[j][k] = Matrix[pos][k];
						Matrix[pos][k] = tmp;
					}
					break;
				}
			}
			
			if(Math.abs(Matrix[pos][i]) < ep) continue;
			
			// eliminate!
			for(int j = 0; j < dim; j++){
				if(j != pos && Math.abs(Matrix[j][i]) > ep){
					double factor =  Matrix[j][i] / Matrix[pos][i];
					for(int k = i; k < dim + 1; k++){
						Matrix[j][k] -= factor * Matrix[pos][k];
					}
				}
			}
			
			pos++;
		}
		
		
	}
	
	public void printKnowledge(){
		System.out.print("===========  Knowledge Matrix ============\n");
		 try{
	            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("cs520text002.txt"),true));
	            writer.write("===========  Knowledge Matrix ============\n");	        
				for(int i = 0; i < width; i++){
					for(int j = 0; j < height; j++){
						if(knowledge[i][j] == -1) writer.write("  *");
						else if(knowledge[i][j] == Integer.MAX_VALUE) writer.write("  ?");
						else writer.write("  " + knowledge[i][j]);
					}
					writer.write("\n");
				}
				writer.write("=========================\n");
				writer.close();
		 }
		 catch(Exception e){
	            e.printStackTrace();
	     }
	}
	
	public void printRealMatrix(){
		//System.out.print("===========  Real Matrix ============\n");
		 try{
	            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("cs520text002.txt"),true));
	            writer.write("===========  Real Matrix ============\n");	        
				for(int i = 0; i < width; i++){
					for(int j = 0; j < height; j++){
						if(map[i][j] == -1) writer.write("  *");
						else if(map[i][j] == Integer.MAX_VALUE) writer.write("  ?");
						else writer.write("  " + map[i][j]);
					}
					writer.write("\n");
				}
				writer.write("=========================\n");
				writer.close();
		 }
		 catch(Exception e){
	            e.printStackTrace();
	     }
	}
	
	public boolean simpleCheck(ArrayList<Point> checkList){
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){			
				
				
				if(knowledge[x][y] != -1 && knowledge[x][y] != Integer.MAX_VALUE && knowledge[x][y] != 0){
					// 1. if the number of remaining mines equals the number of surrounding covered squares, each one of them must be a mine.
					int totalMines = knowledge[x][y];
					int unCovered = 0;
					if(x - 1 >= 0 && y - 1 >= 0){
						if(knowledge[x - 1][y - 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x - 1][y - 1] == -1) totalMines--;
					}
					if(x - 1 >= 0){
						if(knowledge[x - 1][y] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x - 1][y] == -1) totalMines--;
					}
					if(x - 1 >= 0 && y + 1 < height){ 
						if(knowledge[x - 1][y + 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x - 1][y + 1] == -1) totalMines--;
					}
					
					if(y - 1 >= 0){
						if(knowledge[x][y - 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x][y - 1] == -1) totalMines--;
					}
					if(y + 1 < height){
						if(knowledge[x][y + 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x][y + 1] == -1) totalMines--;
					}
					
					if(x + 1 < width && y - 1 >= 0){ 
						if(knowledge[x + 1][y - 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x + 1][y - 1] == -1) totalMines--;
					}
					if(x + 1 < width){ 
						if(knowledge[x + 1][y] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x + 1][y] == -1) totalMines--;
					}
					if(x + 1 < width && y + 1 < height){ 
						if(knowledge[x + 1][y + 1] == Integer.MAX_VALUE) unCovered++;
						if(knowledge[x + 1][y + 1] == -1) totalMines--;
					}
					if(unCovered == 0) continue;
					
					if(totalMines == unCovered){
						if(x - 1 >= 0 && y - 1 >= 0 && knowledge[x - 1][y - 1] == Integer.MAX_VALUE) knowledge[x - 1][y - 1] = -1;
						if(x - 1 >= 0 && knowledge[x - 1][y] == Integer.MAX_VALUE) knowledge[x - 1][y] = -1;
						if(x - 1 >= 0 && y + 1 < height && knowledge[x - 1][y + 1] == Integer.MAX_VALUE) knowledge[x - 1][y + 1] = -1;
						
						if(y - 1 >= 0 && knowledge[x][y - 1] == Integer.MAX_VALUE) knowledge[x][y - 1] = -1;
						if(y + 1 < height && knowledge[x][y + 1] == Integer.MAX_VALUE) knowledge[x][y + 1] = -1;
						
						if(x + 1 < width && y - 1 >= 0 && knowledge[x + 1][y - 1] == Integer.MAX_VALUE) knowledge[x + 1][y - 1] = -1;
						if(x + 1 < width && knowledge[x + 1][y] == Integer.MAX_VALUE) knowledge[x + 1][y] = -1;
						if(x + 1 < width && y + 1 < height && knowledge[x + 1][y + 1] == Integer.MAX_VALUE) knowledge[x + 1][y + 1] = -1;
						
						//System.out.println("found mines around X:" + x + " Y:" + y);
						return true;
					}
					
					// 2. if the number of surrounding mines is equal to the number of marked mines, all remaining surrounding squares must be clear.
					if(totalMines == 0 && unCovered > 0){
						if(x - 1 >= 0 && y - 1 >= 0 && knowledge[x - 1][y - 1] == Integer.MAX_VALUE) explore(x - 1, y - 1,checkList);
						if(x - 1 >= 0 && knowledge[x - 1][y] == Integer.MAX_VALUE) explore(x - 1, y,checkList);
						if(x - 1 >= 0 && y + 1 < height && knowledge[x - 1][y + 1] == Integer.MAX_VALUE) explore(x - 1, y + 1,checkList);
						
						if(y - 1 >= 0 && knowledge[x][y - 1] == Integer.MAX_VALUE) explore(x, y - 1,checkList);
						if(y + 1 < height && knowledge[x][y + 1] == Integer.MAX_VALUE) explore(x, y + 1,checkList);
						
						if(x + 1 < width && y - 1 >= 0 && knowledge[x + 1][y - 1] == Integer.MAX_VALUE) explore(x + 1, y - 1, checkList);
						if(x + 1 < width && knowledge[x + 1][y] == Integer.MAX_VALUE) explore(x + 1, y, checkList);
						if(x + 1 < width && y + 1 < height && knowledge[x + 1][y + 1] == Integer.MAX_VALUE) explore(x + 1,y + 1,checkList);
						//System.out.println("found safe places around X:" + x + " Y:" + y);
						return true;
					}
				}
				
			}
		}
		return false;	
	}
	
	public int query(int x, int y){
		return map[x][y];
	}
	
	public Point firstPick(){
		Random rowG = new Random();
		int row = rowG.nextInt(width);
		Random colG = new Random();
		int col = colG.nextInt(height);
		
		while(map[row][col] == -1){
			row = rowG.nextInt(width);
			col = colG.nextInt(height);
		}
		
		return new Point(row, col);
	}
	
	public Point randomPick(){
		Random rowG = new Random();
		int row = rowG.nextInt(width);
		Random colG = new Random();
		int col = colG.nextInt(height);
		while(knowledge[row][col] != Integer.MAX_VALUE){
			row = rowG.nextInt(width);
			col = colG.nextInt(height);
		}
		//System.out.println("random pick X:" + row + " Y:" + col);
		return new Point(row, col);
	}
	
	
	
	public int[][] createMap(int x, int y, double p){
		map = new int[x][y];
//	visited = new boolean[n][n];
	
		double rand = Math.random();
		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				rand = Math.random();
				if(rand<p)
					map[i][j] = -1;
			}
		}
		
		for(int i=0; i < x; i++){
			for(int j = 0; j < y; j++){
				if(map[i][j] == -1) continue;
				int sum = 0;
				if(i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] == -1) sum++;
				if(i - 1 >= 0 && map[i - 1][j] == -1) sum++;
				if(i - 1 >= 0 && j + 1 < y && map[i - 1][j + 1] == -1) sum++;
				
				if(j - 1 >= 0 && map[i][j - 1] == -1) sum++;
				if(j + 1 < y && map[i][j + 1] == -1) sum++;
				
				if(i + 1 < x && j - 1 >= 0 && map[i + 1][j - 1] == -1) sum++;
				if(i + 1 < x && map[i + 1][j] == -1) sum++;
				if(i + 1 < x && j + 1 < y && map[i + 1][j + 1] == -1) sum++;
				map[i][j] = sum;
			}			
		}
		
//		System.out.print("=========== Real Matrix ===============\n");
//		for(int i=0; i < x; i++){
//			for(int j = 0; j < y; j++){
//				if(map[i][j] == -1) System.out.print("  *");
//				else System.out.print("  "+map[i][j]);
//			}		
//			System.out.print("\n");
//		}
//		System.out.print("===================================\n");
		
	
		return map;
	}
}

class Point {
	public int x;
	public int y;
	
	public Point(int i, int j){
		this.x = i;
		this.y = j;
	}
}

class Test{
	public static void main(String[] args){
		Robot robt = new Robot(16, 30);
		//robt.play(0.1);
		double win = 0;
		double times = 1;
		for(int count = 0; count < times; count++){
			win += robt.play(0.2);
		}
		System.out.println("win:" + win + " ratio:" + win / times);
		
//		
	}
}