package nn_hw3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application{
	
	public int row = 12;
	public int element = 6;
	public int[] size;
	
	protected static final int[] data_size = new int[1];
	public final String[] file_name = new String[1];
	public double data[][] ;
	public double data_noise[][] ;
	public double weight[][] ;
	public double theta[] ;
	double[][] orignal;
	public double similar=0;
	
	public int click_start_time = 0;
	public double time = 0;
	public int iterate_time = 0;
	public int prob=15;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		AnchorPane mainPane = (FXMLLoader.load(getClass().getResource("main.fxml")));
		Scene scene = new Scene(mainPane,800,600);
		primaryStage.setTitle("NN homework");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		Button start = (Button) scene.lookup("#start_button");
		Button file = (Button) scene.lookup("#file_button");
		Button start_test = (Button) scene.lookup("#test_button");
		Button file_test = (Button) scene.lookup("#file_test_button");
		TextArea output = (TextArea) scene.lookup("#text3");
		TextArea information = (TextArea) scene.lookup("#text4");
		TextArea status = (TextArea) scene.lookup("#text5");
		TextArea proability = (TextArea) scene.lookup("#text6");
		start.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				time=System.currentTimeMillis();
				Hopfield(time,output,information,status);
			}
		});
		
		file.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			private BufferedReader br;

			@Override
			public void handle(MouseEvent event) {
				prob = Integer.parseInt(proability.getText().toString());
				int element_number = 0;
				int element_row = 0 ;
				String in  = new String("");
				String out = "";
				in+="\n********Hopfield network********\n\n";
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());				
				try {
					FileReader reader = new FileReader(file);
					br = new BufferedReader(reader);
					String str = null;
					
					file_name[0] = file.getName();
					System.out.println("file name is "+file_name[0]);
					in+=("Train file name  :  "+file_name[0]+"\n\n");
					out+="input data: \n";
					
					if(file_name[0].equals("Basic_Training.txt")||file_name[0].equals("Basic_Testing.txt")) {
						 System.out.println("Basic!");
						 data = new double[108][3];
						 data_noise = new double[108][3];
						 weight = new double[108][108];
						 theta = new double[108];
						 row = 108;
						 size = new int[2];
						 size[0]=12;size[1]=9;
						 element = 3;
					}
					else if (file_name[0].equals("test.txt")) {
						System.out.println("Test!");
						 data = new double[3][2];
						 data_noise = new double[3][2]; 
						 weight = new double[3][3];
						 theta = new double[3];
						 size = new int[2];
						 size[0]=3;size[1]=2;
						 row = 3;
						 element = 2;
					}
					else {
						 System.out.println("Bonus!");
						 data = new double[100][15];
						 data_noise = new double[100][15];
						 weight = new double[100][100];
						 theta = new double[100];
						 size = new int[2];
						 row = 100;
						 size[0]=10;size[1]=10;
						 element = 15;
					} 
						for(int ele =0 ; ele<element ; ele++) {
							for(int row_run = 0 ; row_run < row;row_run++) {
									data[row_run][ele] = 0;
									data_noise[row_run][ele] = 0;
							}
						}
						for(int row_run =0 ; row_run<row ; row_run++) {
							for(int col_run = 0 ; col_run < row; col_run++) {
									weight[row_run][col_run]=0;
							}
						}
						while((str = br.readLine())!=null ) {
							if(str.isEmpty()) {
								System.out.println("detect \\n! ");
								element_row = 0;
								element_number++;
							}
							else {
								for(int i = 0 ; i < str.length(); i++) {
									//System.out.printf("row is %d , column is %d\n", element_row, element_column);
									if(str.charAt(i) == ' ') {
										data[element_row][element_number] = -1;
										data_noise[element_row][element_number] = -1;
										if((int)(Math.random()*prob)%prob==0) {
											data_noise[element_row][element_number]*=-1;
										}
										element_row++;
									}
									else if(str.charAt(i) == '1') {
										data[element_row][element_number] = 1;
										data_noise[element_row][element_number] = 1;										
										if((int)(Math.random()*prob)%prob==0) {
											data_noise[element_row][element_number]*=-1;
										}
										element_row++;
									} 
								}
							}	
						}
				}catch( Exception e) {
					e.printStackTrace();
				}	
				for(int i = 0 ; i < element ; i ++) {
					out+=("\n"+i+".   : \n");
					for(int j = 0 ; j < size[0] ;  j ++ ) {
						for(int k =0 ; k < size[1] ; k++) {
							out+=(" ");
							if(data[j*size[1]+k][i]==-1) {
								out+=("  ");
							}
							else {
								out+=("1");
							}
						}
						out+="\n";
					}
				}
				out+="\n------training data with noise (with proability :"+(1.0/prob)+")------\n";
				for(int i = 0 ; i < element ; i ++) {
					out+=("\n"+i+".   : \n");
					for(int j = 0 ; j < size[0] ;  j ++ ) {
						for(int k =0 ; k < size[1] ; k++) {
							out+=(" ");
							if(data_noise[j*size[1]+k][i]==-1) {
								out+=("  ");
							}
							else {
								out+=("1");
							}
						}
						out+="\n";
					}
				}
				in+=("********complete init********\n");
				status.setText("__init__open training file :"+file_name[0]);
				output.setText(out);
				information.setText(in);
				 
				
			}		
		});
		
		file_test.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			private BufferedReader br;

			@Override
			public void handle(MouseEvent event) {
				 int element_number = 0;
				 int element_row = 0 ;
				String in  = information.getText().toString();
				String out = "";
				in+=("\n**********input testing data****************\n");
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());				
				try {
					FileReader reader = new FileReader(file);
					br = new BufferedReader(reader);
					String str = null;
					
					file_name[0] = file.getName();
					System.out.println("file name is "+file_name[0]);
					in+=("Test file name  :  "+file_name[0]+"\n\n");
					out+="input data: \n"; 
					for(int ele =0 ; ele<element ; ele++) {
						for(int row_run = 0 ; row_run < row;row_run++) {
							data[row_run][ele] = 0;
						}
					}
					while((str = br.readLine())!=null ) {
						if(str.isEmpty()) {
							System.out.println("detect \\n! ");
							element_row = 0;
							element_number++;
						}
						else {
							for(int i = 0 ; i < str.length(); i++) {
								//System.out.printf("row is %d , column is %d\n", element_row, element_column);
								if(str.charAt(i) == ' ') {
									data[element_row][element_number] = -1;
									element_row++;
								}
								else if(str.charAt(i) == '1') {
									data[element_row][element_number] = 1;
									element_row++;
								} 
							}
						}	
					}
				}catch( Exception e) {
					e.printStackTrace();
				}	
				for(int i = 0 ; i < element ; i ++) {
					out+=("\n"+i+".   : \n");
					for(int j = 0 ; j < size[0] ;  j ++ ) {
						for(int k =0 ; k < size[1] ; k++) {
							out+=(" ");
							if(data[j*size[1]+k][i]==-1) {
								out+=("  ");
							}
							else {
								out+=("1");
							}
						}
						out+="\n";
					}
				}
				in+=("********complete init********\n");

				status.setText("_init_open testing file :"+file_name[0]);
				output.setText(out);
				information.setText(in);
				 
			}
		});
		start_test.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				time=System.currentTimeMillis();
				
				Test(time,output,information,status);
			}
		});
	
		
		
	}
	
	protected void Hopfield(double time,TextArea output, TextArea information,TextArea status) {
		iterate_time = 0;
		String out =  "after recall the result is  :\n ";
		String info = information.getText().toString();
		// building reference answer &  answer check
		
		// building weight
		System.out.println("row is :"+size[0]+"   column is :"+size[1]);
			for(int col_run = 0 ; col_run < element ; col_run++) {	
				for(int transpose_row = 0 ; transpose_row < row ; transpose_row++) {	
					for(int i = 0 ; i < row ; i ++) {
							weight[i][transpose_row]+= 
								data[i][col_run]*
								data[transpose_row][col_run];	
								
							//System.out.println("weight in changed : "+weight[element_num][i][col_run]);
					}
				}
			}		
			for(int i = 0 ; i <row ; i++) {
				for(int j = 0 ; j <row ; j++) {
					weight[i][j]*=(1.0/row);	
					if(i==j) {
						//System.out.println("before : "+weight[ele][i][j]);
						//System.out.println("indentity matrix : "+((double)column/row)+"  column is :"+column+"  row is: "+row);
						weight[i][j]-=((double)element/row);
						//System.out.println("after : "+weight[ele][i][j]);
					}
				}
			}
		
		/*System.out.println("weight is : ");
			for(int row_run = 0 ; row_run < row ; row_run++) {
				for(int col_run = 0 ; col_run < row ; col_run++) {			
					System.out.print( weight[row_run][col_run]+" ");
				}
				System.out.println("");
			}
			System.out.println("");
		*/
		// end of building weight;
		
			for(int row_num= 0 ; row_num < row ; row_num++) {
				double sum = 0 ; 
				for(int j = 0 ; j < row ; j++) {
					sum+=weight[row_num][j];
				}
				theta[row_num] = sum;
			}
		/*System.out.println("theta is : ");
				for(int row_run = 0 ; row_run < row ; row_run++) {			
					System.out.print(theta[row_run]+" ");
				}
				System.out.println("");	
		*/
		// end of building  theta 
		
		//transpose x in order to compute concise
		double[][] x = new double[element][row];
		orignal = new double[element][row];
	for(int turn = 0 ; turn <2 ; turn++) {
		if(turn ==1) {
			out+="-----------------data with noise test--------------------\n";
			info+="-------------------data with noise--------------------\n";
		}
		for(int row_run = 0 ; row_run < row ; row_run++) {
			for(int col_run = 0 ; col_run < element ; col_run++) {
				if(turn==1) {
					x[col_run][row_run]=data_noise[row_run][col_run];
				}
				else {
					x[col_run][row_run]=data[row_run][col_run];
					orignal[col_run][row_run]=data[row_run][col_run];
				}
			}
		}
		
		//end of transpose
		int x_row= element;
		int x_column = row;
		/*System.out.println("x is : ");
		for(int ele = 0 ; ele < element ; ele++) {
			for(int row_run = 0 ; row_run < x_row ; row_run++) {
				for(int col_run = 0 ; col_run < x_column ; col_run++) {			
					System.out.print(x[ele][row_run][col_run]+" ");
				}
				System.out.println("");
			}
			System.out.println("");
		}*/
		
		double[][] xplus1 = new double[element][row];
		
			for(int row_run = 0 ; row_run < x_row ; row_run++) {
				//compute each column 
				 for(int column_run = 0; column_run < x_column ; column_run++) {
					 xplus1[row_run][column_run] = 0;
				 }
			}
		
		for(int ele = 0 ; ele< element ; ele++) {
			iterate_time = 0;
			while(true) {
				iterate_time++;
				System.out.println(iterate_time);
				//System.out.println("iterate_time is :"+iterate_time+" element*row is "+element*row);
						//compute each column 
						for(int column_run = 0; column_run < x_column ; column_run++) {
							for(int i = 0 ; i < x_column ;i++) {
								xplus1[ele][column_run] +=
									x[ele][i]*weight[column_run][i];
							}		 
						}
						
						//compute each column 
						for(int column_run = 0; column_run < x_column ; column_run++) {
							xplus1[ele][column_run]-=theta[column_run];
							// exactly xplus1
							if(xplus1[ele][column_run]>0) {
								xplus1[ele][column_run] = 1; 
							}
							else if (xplus1[ele][column_run] ==0) {
								xplus1[ele][column_run] = x[ele][column_run];
							}
							else if (xplus1[ele][column_run] <0) {
								xplus1[ele][column_run] = -1;
							}	 
						}
						if(VectorCompare(xplus1[ele],x[ele],x_column,false)) {
			    	 		// duplicate count 
						out+=(ele+". : \n");	
						for(int i = 0 ; i <size[0] ; i++) {
							for(int j = 0 ; j < size[1] ; j++) {
								//System.out.print(x[ele][i*size[1]+j]+" ");
								out+=" ";
								if(x[ele][i*size[1]+j]==1) {
									out+=("1");
								}
								else if(x[ele][i*size[1]+j]==-1)  {
									out+=("  ");						
								}
							}
							//System.out.println("");
							out+="\n";
						}
						VectorCompare(orignal[ele],x[ele],x_column,true);
						System.out.println("similarity is :"+similar);
						out+="\n";
						info+="\nTesk"+ele+".  :\n";
						info+=("use time : "+((System.currentTimeMillis()-time)/1000)+"'s\n");
						info+=("iterate time "+iterate_time+" times\n");
						info+=("similarity of orignal pattern: "+(similar/x_column)+"\n");
						break;
					}
					else {
						for(int column_run = 0 ;column_run < x_column ;column_run++ ) {
							x[ele][column_run] = xplus1[ele][column_run];
						}
					}
			}
		}
		// fill out the answer matrix
		/*double[] zero_test = new double[x_column];
		for(int col_run = 0 ; col_run < x_column ; col_run++) {
			zero_test[col_run] = 0;
		}
		for(int ele = 0 ; ele < element ; ele++) {
			for(int row_run = 0; row_run < x_row; row_run++) {
				if(VectorCompare(answer[ele][row_run],zero_test,x_column)) {
					System.out.println("fill answer with xplus1");
					for(int col_run = 0 ; col_run < x_column ; col_run++) {
						answer[ele][row_run][col_run] = xplus1[ele][row_run][col_run];
					}
				}
			}
		}*/
		System.out.println("going to print answer");
	}
		
		info+="\n********complete recall********\n";
		info+="weight is :\n";
		DecimalFormat df = new DecimalFormat("##.0000");
		for(int ele = 0 ; ele<element ; ele++) {
			for(int row_run = 0 ; row_run < row ; row_run++) {
				info+=(Double.parseDouble(df.format(weight[ele][row_run]))+" ");
			}
			info+="\n";
		}
		info+="\n\n";
		status.setText("complete training");
		output.setText(out);
		information.setText(info);
	}
	protected boolean VectorCompare(double[] x, double[] xplus1, int size,boolean return_count) {
		//System.out.println("enter comparison");
		int count = 0 ;
		for(int i = 0 ; i< size ; i++) {
			if(x[i]==xplus1[i]) {
				count++;
				//System.out.println("i is :"+i+" x :"+x[i]+"  x+1 : "+xplus1[i]);
			}
		}
		if(return_count==true) {
			similar=count;
		}
		//System.out.println("count is"+count);
		if(count == size) {
			//System.out.print("True!  "+"count is"+count);
			return true;
		}
		else {
			if(return_count) {
				System.out.print("Flase!  "+"count is"+count);
			}
			return false;
		}	
	}
	
	protected void Test(double time,TextArea output, TextArea information,TextArea status) {
		//transpose x in order to compute concise
		iterate_time = 0;
		String out =  "after recall the result is  :\n ";
		String info = information.getText().toString();
		
		double[][] x = new double[element][row];
		for(int row_run = 0 ; row_run < row ; row_run++) {
			for(int col_run = 0 ; col_run < element ; col_run++) {			
				x[col_run][row_run]=data[row_run][col_run];	
			}
		}
		
		//end of transpose
		int x_row= element;
		int x_column = row;
		/*System.out.println("x is : ");
		for(int ele = 0 ; ele < element ; ele++) {
			for(int row_run = 0 ; row_run < x_row ; row_run++) {
				for(int col_run = 0 ; col_run < x_column ; col_run++) {			
					System.out.print(x[ele][row_run][col_run]+" ");
				}
				System.out.println("");
			}
			System.out.println("");
		}*/
		
		double[][] xplus1 = new double[element][row];
		
			for(int row_run = 0 ; row_run < x_row ; row_run++) {
				//compute each column 
				 for(int column_run = 0; column_run < x_column ; column_run++) {
					 xplus1[row_run][column_run] = 0;
				 }
			}
		
		for(int ele = 0 ; ele< element ; ele++) {
			iterate_time = 0;
			while(true) {
				iterate_time++;
				System.out.println(iterate_time);
				//System.out.println("iterate_time is :"+iterate_time+" element*row is "+element*row);
						//compute each column 
						for(int column_run = 0; column_run < x_column ; column_run++) {
							for(int i = 0 ; i < x_column ;i++) {
								xplus1[ele][column_run] +=
									x[ele][i]*weight[column_run][i];
							}		 
						}
						
						//compute each column 
						for(int column_run = 0; column_run < x_column ; column_run++) {
							xplus1[ele][column_run]-=theta[column_run];
							// exactly xplus1
							if(xplus1[ele][column_run]>0) {
								xplus1[ele][column_run] = 1; 
							}
							else if (xplus1[ele][column_run] ==0) {
								xplus1[ele][column_run] = x[ele][column_run];
							}
							else if (xplus1[ele][column_run] <0) {
								xplus1[ele][column_run] = -1;
							}	 
						}
						if(VectorCompare(xplus1[ele],x[ele],x_column,false)) {
			    	 		// duplicate count 
						out+=(ele+". : \n");	
						for(int i = 0 ; i <size[0] ; i++) {
							for(int j = 0 ; j < size[1] ; j++) {
								//System.out.print(x[ele][i*size[1]+j]+" ");
								out+=" ";
								if(x[ele][i*size[1]+j]==1) {
									out+=("1");
								}
								else if(x[ele][i*size[1]+j]==-1)  {
									out+=("  ");						
								}
							}
							//System.out.println("");
							out+="\n";
						}
						VectorCompare(orignal[ele],x[ele],x_column,true);
						System.out.println("similarity is :"+similar);
						out+="\n";
						info+="\nTesk"+ele+".  :\n";
						info+=("use time : "+((System.currentTimeMillis()-time)/1000)+"'s\n");
						info+=("iterate time "+iterate_time+" times\n");
						info+=("similarity of orignal pattern: "+(similar/x_column)+"\n");
						break;
					}
					else {
						for(int column_run = 0 ;column_run < x_column ;column_run++ ) {
							x[ele][column_run] = xplus1[ele][column_run];
						}
					}
			}
		}
		// fill out the answer matrix
		/*double[] zero_test = new double[x_column];
		for(int col_run = 0 ; col_run < x_column ; col_run++) {
			zero_test[col_run] = 0;
		}
		for(int ele = 0 ; ele < element ; ele++) {
			for(int row_run = 0; row_run < x_row; row_run++) {
				if(VectorCompare(answer[ele][row_run],zero_test,x_column)) {
					System.out.println("fill answer with xplus1");
					for(int col_run = 0 ; col_run < x_column ; col_run++) {
						answer[ele][row_run][col_run] = xplus1[ele][row_run][col_run];
					}
				}
			}
		}*/
		System.out.println("going to print answer");
		
		info+="\n********complete recall********\n";
		status.setText("complete testing");
		output.setText(out);
		information.setText(info);
	}
}