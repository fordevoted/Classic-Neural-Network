package nn_hw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage; 

public class Main extends Application{
	
	public final boolean[] dataset_result_range_2to1 =new boolean[1]; 
	public final int[] data_size= new int[1];
	public final double[] data_feature1 = new double[1000];
	public final double[] data_feature2 = new double[1000];
	public final double[][] class1   = new double[10000][2];	//point set
	public final double[][] class2   = new double[10000][2];	//point set
	public  final String[] file_name = new String[1];
	public final double[][] number_feature = new double[1000][25];
	public final double[] number_result = new double[1000];
	double weight[][][];
	double weight_pocket[][][];
	double max_acc_pocket = 0;
	double data_feature1_max=-5,data_feature1_min=5;
	double data_feature2_max=-5,data_feature2_min=5;
	double time = 0 ;
	int class1_size = 0;
	int  class2_size = 0;
	int train_time = 0;
	boolean after_train = false;		//use when drawing chart
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		AnchorPane mainPane = (FXMLLoader.load(getClass().getResource("main.fxml")));
		Scene scene = new Scene(mainPane,800,600);
		primaryStage.setTitle("NN homework");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		Button start = (Button) scene.lookup("#start_button");
		Button chart = (Button) scene.lookup("#chart_button");
		Button file = (Button) scene.lookup("#file_button");
		TextArea learn_rate = (TextArea) scene.lookup("#text1");
		TextArea epoch = (TextArea) scene.lookup("#text2");
		TextArea output = (TextArea) scene.lookup("#text3");
		TextArea momentum = (TextArea) scene.lookup("#text4");
		TextArea Dense = (TextArea) scene.lookup("#text5");
		TextArea unit = (TextArea) scene.lookup("#text6");
		
		final double[] data_expected_value = new double[1000];
		
		data_size[0] = 0 ;
		dataset_result_range_2to1[0] = true;
		
		start.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				double learn_rate_number = Double.parseDouble(learn_rate.getText());
				int epoch_number =Integer.parseInt(epoch.getText());
				double momentum_num = 	Double.parseDouble(momentum.getText());
				int dense = Integer.parseInt(Dense.getText());
				int units = Integer.parseInt(unit.getText());
				train_time++;
				max_acc_pocket = 0;
				time=System.currentTimeMillis();
				if(file_name[0].equals("Number.txt")) {
					NumberRecongnition(momentum_num,learn_rate_number,epoch_number,data_size[0],output);
				}
				else {
					NeuralNetwork(data_expected_value,momentum_num,learn_rate_number,epoch_number,dense,units,data_size[0],output);			
				}
				after_train = true;
			}

			
		});
		
		file.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				train_time = 0;
				class1_size = 0;
				class2_size = 0; 
				data_feature1_max=-5;data_feature1_min=5;
				data_feature2_max=-5;data_feature2_min=5;
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());				
				after_train = false;
				try {
					FileReader reader = new FileReader(file);
					BufferedReader br = new BufferedReader(reader);
					String str = null;
					String out;
					file_name[0] = file.getName(); 
					out = output.getText().toString();
					out= ("  "+file_name[0]+"  input data :\n");
					int data_index = 0 ;
					data_size[0]=0;
					System.out.println("file name is "+file.getName());
					if(file.getName().equals("perceptron1.txt")||file.getName().equals("perceptron2.txt")||file.getName().equals("xor.txt")) {
						System.out.println("match "+file.getName());
						dataset_result_range_2to1[0] = false;
					}
					else {
						dataset_result_range_2to1[0] = true;
					}
					if(file.getName().equals("Number.txt")) {
						out=NumberDataProcessing(br,out);
					}
					else {
						while((str = br.readLine())!=null ) {
							// tackle string
							int str_div_head = 0;
							int count_time = 0 ;
							out+=(str+"\n");
							data_size[0]++;
							for(int i = 0 ; i< str.length();i++) {
							
								if(str.charAt(i) == ' '||i == str.length()-1) {
									
									if(i == str.length()-1)
										i++;
									if(count_time == 0) {
										//System.out.println("i is "+i+"and str head is  "+str_div_head+"count_time is "+count_time);
										data_feature1[data_index] = Double.parseDouble(str.substring(str_div_head,i));
										if(data_feature1[data_index]<data_feature1_min+1) {	// bias return
											data_feature1_min = data_feature1[data_index]-1;	//bias 
										}
										if(data_feature1[data_index]>data_feature1_max-1) {
											data_feature1_max = data_feature1[data_index]+1;
										}
										str_div_head = i+1;
										count_time++;
									}
									else if(count_time == 1) {
										//System.out.println("i is "+i+"and str head is  "+str_div_head+"count_time is "+count_time);
										data_feature2[data_index] = Double.parseDouble(str.substring(str_div_head,i));
									
										if(data_feature2[data_index]<data_feature2_min+1) {	// bias return
										data_feature2_min = data_feature2[data_index]-1;	//bias 
										}
										if(data_feature2[data_index]>data_feature2_max-1) {
											data_feature2_max = data_feature2[data_index]+1;
										}
										str_div_head = i+1;
										count_time++;
									}
									else if(count_time == 2) {
										//System.out.println("i is "+i+"and str head is  "+str_div_head+"count_time is "+count_time);
										data_expected_value[data_index] = Double.parseDouble(str.substring(str_div_head,i));
										str_div_head = i+1;
										count_time++;
									}								
								}
							}
							data_index++;
						}
						out+="\n--------------------------------------------------\ntransform to number: \n";
						for(int i = 0 ; i < data_index ;i ++) {
						out+=("feature 1:  "+data_feature1[i]+"  data_feature2: "+data_feature2[i]+"  data_expected_output :  "+data_expected_value[i]+"\n");
						}
						//correct num of feature
					}
					output.setText(out);
					br.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		chart.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(after_train) {
					CreateChart(false);	// use train result
				}
				else {
					CreateChart(true); // use classify result
				}				
			}
		});
	}
	// dense exclude input layer 
	private void NeuralNetwork( double[] result,double momentum,double learn,int epoch,int dense,int units,int data_size,TextArea output) { 		 
	 	double weight_orignal[][][] = new double[dense][units][units+1];
	 	weight = new double[dense][units][units+1];
	 	weight_pocket = new double[dense][units][units+1];
	 	double[] y = new double[units+1];
	 	double[][] input = new double[dense][units+1];
	 	double[] sigma= new double[(int)Math.pow(units, 2)+1];
	 	double[] sigma_copy= new double[(int)Math.pow(units, 2)+1];
	 	double pre_sigma0=0.5;
	 	int seq_length;
	 	String ot = output.getText();
	 	ot="train is complete, the following is result(learning rate :"+learn+" momentum :"+momentum+" trainning time:+"+train_time+"):\n";
	 	ot+="\n---------------------\norignal weight is : \n";
	 	for(int i = 0 ; i< dense ; i++) {
	 		for(int j = 0 ; j < units ; j++) {
	 			for(int k = 0 ; k < units+1 ; k++) {
	 				weight[i][j][k] = Math.random()*2-1;	// [-1 , 1] 
	 				/*if(dataset_result_range_2to1[0]==false) {
	 			 		//System.out.println("modify");
	 					weight[0][0][0]  = -1.2;
	 			 		weight[0][0][1]  = 1;
	 			 		weight[0][0][2]  = 1;
	 			 		weight[0][1][0]  = 0.3;
	 			 		weight[0][1][1]  = 1;
	 			 		weight[0][1][2]  = 1;
	 			 		weight[1][0][0]  = 0.5;
	 			 		weight[1][0][1]  = 0.4;
	 			 		weight[1][0][2]  = 0.8;
	 			 		weight_orignal[i][j][k] = weight[i][j][k];
	 				}*/
	 				weight_pocket[i][j][k] = weight[i][j][k];
	 				weight_orignal[i][j][k] = weight[i][j][k];
	 				//System.out.println(weight_orignal[i][j][k]+" "+weight[i][j][k] );
	 				ot+=("weight["+i+"]["+j+"]["+k+"]  : "+weight[i][j][k]+"\n");
	 			}
	 		}
	 	}
	 	
	 	ot+="\n---------------------\nafter training, weight is : \n";
	 	System.out.println("epoch is "+epoch+"\n");
	 	System.out.println("data size is "+ data_size+"\n");
	 	double delta;
 		if(data_size<10) {
 			delta=1;
 		}
 		else{
			delta=1.5;
 		}
 		int[] seq = new int[data_size*data_size];
 		int index=0;
 		for(double z = 0 ; z < data_size ; z+=delta) {
 			seq[index]=(int)(Math.random()*data_size);
 			if(data_size<10) {
 				seq[index]=(int)z;
 			}
 			index++;
 		}
 		seq_length = index;
	 	while(epoch>0) {
	 		//System.out.println("enter while loop\n");
	 		System.out.println(seq_length);
	 		for(int z = 0 ; z < seq_length ; z++) {
	 			int i = seq[z];
	 			//System.out.println("i is : "+i);
	 			input[0][0] = -1; 
	 			input[0][1] = data_feature1[i];
	 			input[0][2]=  data_feature2[i];
	 			double out_result = 0;
	 			// for first layer
	 			for(int units_running = -1 ; units_running<units ; units_running++) {
	 				if(units_running == -1 ) {
	 					y[0] = -1;
	 				}
	 				else {
	 					y[units_running+1] = input[0][0]*weight[0][units_running][0]
 							+input[0][1]*weight[0][units_running][1]
 							+input[0][2]*weight[0][units_running][2];
	 					y[units_running+1] =  1/(1+Math.exp(-y[units_running+1]));// y[0]<0?0:y[0]; activation function
	 					
	 				}
	 			}
	 			for(int units_running = 0 ; units_running < units+1 ; units_running++) {
	 					input[1][units_running] = y[units_running];
	 			}
	 			// input layer to hidden layer
	 			
	 			for(int dense_running = 1; dense_running < dense-1 ; dense_running++) {
	 				//System.out.println("enter hidden layer\n");
	 				y[0] = -1;
	 				for(int units_running = 0 ; units_running< units ; units_running++) {
	 						for(int j = 0 ; j <units+1 ; j++) {
	 							y[units_running+1] += input[dense_running][j]*weight[dense_running][units_running][j];
	 							//System.out.println(" j :"+j+" unit_running: "+units_running+" dense_running:"+dense_running);
	 						}
	 						y[units_running+1] =  1/(1+Math.exp(-y[units_running+1]));// y[0]<0?0:y[0]; activation function
	 				}
	 				for(int units_running = 0 ; units_running< units+1 ; units_running++) {
	 					input[dense_running+1][units_running]= y[units_running];
	 					//System.out.println("dense_running+1 : "+dense_running+1+" dense: "+dense+" y[units_running]"+y[units_running]);
	 				}
	 			}
	 			for(int j = 0 ; j < units+1 ; j++) {
	 					out_result+=y[j]*weight[dense-1][0][j];
	 			}
	 			out_result =  1/(1+Math.exp(-out_result));
	 			double normaliztion_out=out_result;
	 			if(dataset_result_range_2to1[0]) {
	 				normaliztion_out=out_result+1;
	 			}
	 			if(epoch==10) {
					// System.out.println(i+" times out results :  "+out_result+" "+data_feature1[i]+" "+data_feature2[i]); 
				 }
	 			//System.out.println(i+" times out results :  "+out_result);
	 			 //System.out.println(i+" times out results :  "+out_result);
	 			//----------------------- output layer
	 			 
	 			 
	 			 
	 			//------------------------------ back propagation
	 				
	 				double[][][] weight_copy = new double[dense][units][units+1]; 
	 				for(int j = 0 ; j < dense ; j ++) {
	 					for(int k = 0 ; k < units ; k++) {
	 						for(int p = 0;p<units+1 ; p++ ) {
	 							weight_copy[j][k][p] = weight[j][k][p];
	 						}
	 					}
	 				}
	 				
	 				//System.out.println("\n\noutput layer:\n");
	 				for(int j = 0 ; j<units+1 ; j++) {
	 					weight[dense-1][0][j] = momentum*weight_copy[dense-1][0][j]+learn*(result[i]-normaliztion_out)*out_result*(1-out_result)*input[dense-1][j]; 		 				
	 					//System.out.println(weight[dense-1][0][j]);
	 				}
	 				//System.out.println("\n\noutput");
	 				
	 				sigma[0] = (result[i]-normaliztion_out)*out_result*(1-out_result);
	 				sigma_copy[0]=(result[i]-normaliztion_out)*out_result*(1-out_result);
	 				
	 				
	 				if(Math.abs((pre_sigma0-sigma[0])/pre_sigma0)>2){
	 					learn+=0.0005;
	 				}
	 				if(Math.abs((pre_sigma0-sigma[0])/pre_sigma0)<0.005){
	 					learn-=0.0005;
	 				}
	 				//System.out.println(Math.abs((pre_sigma0-sigma[0])/pre_sigma0));
	 				
	 				pre_sigma0 = sigma[0];
	 				//System.out.println("sigma[0] : "+sigma[0]);
	 				for(int dense_running = dense-1 ; dense_running >0 ; dense_running--) {
	 					int temp = 1;
	 					if(dense_running == dense-1) {
	 						for(int units_running = 0 ; units_running< units ; units_running++) {
	 							for(int j = 0 ; j<units+1 ; j++) {
	 								weight[dense_running-1][units_running][j] = momentum*weight_copy[dense_running-1][units_running][j]
	 										+(learn*input[dense_running][units_running+1]
	 												*(1-input[dense_running][units_running+1])
	 												*sigma[0]*weight_copy[dense_running][0][units_running+1]
	 														*input[dense_running-1][j]);
	 								//System.out.println("weight[dense_running-1][units_running][j] "+weight[dense_running-1][units_running][j]);
	 								if(j!=0) {
 										//System.out.println("weight: "+j+"  "+weight[dense_running-1][units_running][j]);
 										//System.out.println("lambda: "+input[dense_running][units_running+1]+" "+(1-input[dense_running][units_running+1])+" "+sigma_copy[0]+" "+weight_copy[dense_running][0][units_running+1]+" "+input[dense_running-1][j]);										
 										//System.out.println("temp: "+temp);
 									}
	 							}
	 							//System.out.println(input[dense_running][units_running+1]+" "+(1-input[dense_running][units_running+1])+" "+sigma[0]+" "+weight_copy[dense_running][0][units_running+1]+" ");
	 							sigma_copy[temp]=input[dense_running][units_running+1]*(1-input[dense_running][units_running+1])*sigma[0]*weight_copy[dense_running][0][units_running+1];
								//System.out.println("sigma_copy[temp]: "+sigma_copy[temp]);
	 							temp++;
	 						}
	 						for(int k = 0 ; k <(int)Math.pow(units, 2)+1 ; k++) {
	 							sigma[k] = sigma_copy[k] ;
	 							sigma_copy[k]=0;
	 						}
	 						temp =1;
	 					}
	 					else {
	 						for(int units_running = 0 ; units_running< units ; units_running++) {
	 							for(int opposite = 0 ; opposite<units ; opposite++) {
 									sigma_copy[temp]+=sigma[opposite+1]*weight_copy[dense_running][opposite][units_running+1];
 								}
 								temp++;
	 							for(int j = 0 ; j<units+1 ; j++) {
	 								weight[dense_running-1][units_running][j] = momentum*weight_copy[dense_running-1][units_running][j]
	 										+(learn*input[dense_running][units_running+1]
	 												*(1-input[dense_running][units_running+1])
	 												*sigma_copy[temp-1]
	 														*input[dense_running-1][j]);
	 								//System.out.println("weight[dense_running-1][units_running][j] "+weight[dense_running-1][units_running][j]);
	 								//System.out.println(input[dense_running][units_running+1]+" "+(1-input[dense_running][units_running+1])+" "+sigma_copy[temp-1]+" "+weight_copy[dense_running][0][units_running+1]+" "+input[dense_running-1][j]);
	 								
	 							}
	 						}
	 						for(int k = 0 ; k <(int)Math.pow(units, 2)+1 ; k++) {
	 							sigma[k] = sigma_copy[k] ;
	 							sigma_copy[k]=0;
	 						}
	 						temp =1;	 						
	 					}
	 				}
	 				Predict(false,seq,seq_length,units,dense,y ,input ,result,weight,ot);
	 				//System.out.println("after pocket predict");
	 			}
	 		System.out.println(epoch);
	 		epoch--;
	 	}
	 	if(Predict(false,seq,seq_length,units,dense,y ,input ,result,weight,"?").charAt(0)=='X') {
	 		weight = weight_pocket;
	 		System.out.println("done pocket");
	 		System.out.println(weight == weight_pocket);
	 	}
	 	//train data test
		for(int p = 0 ; p< dense ; p++) {
			for(int j = 0 ; j < units ; j++) {
				for(int k = 0 ; k < units+1 ; k ++) {
					ot+=("weight["+p+"]["+j+"]["+k+"]  : "+weight[p][j][k]+"\nchange percentage:  "+((weight_orignal[p][j][k]-weight[p][j][k])/weight_orignal[p][j][k])*100+"%\n\n");
				}
			}
		}
 		ot+="\n---------------------\ntrain data test : \n\n";
 		ot=Predict(false,seq,seq_length,units,dense,y ,input ,result,weight,ot);
 		
	 	ot+="\n-------------------------------\ntest data test:\n\n";
	 	// 0 -data_size
	 	delta = 1;
	 	int tempi=index;
	 	index=0;
	 	int[] seq_test = new int[data_size*data_size];
 		for(double z = 0 ; z < data_size ; z++) {
 			for(int k = 0;k<tempi ; k++) {
 				if(seq[k]==z) {
 					break;
 				}
 				else if(k==tempi-1) {
 					seq_test[index]=(int)z;
 					index++;
 				}
 			}
 			if(data_size<10) {
 				seq_test[index]=(int)z;
 				index++;
 			}
 		}
 		seq_length = index;
	 	//System.out.println("come on");
	 	ot=Predict(true,seq_test,seq_length,units,dense,y ,input ,result,weight,ot);	
			 /*if(y[0] == 0||y[1] == 0) {
				 System.out.println("E04");	//angry programmer orzzz
			 }*/
	 	ot+=("----------------------------------------------\n learning rate is :"+learn+"\nMax accuracy in pocket is : "+max_acc_pocket);
	 	ot+=("\ntraining time :"+(System.currentTimeMillis()-time)/1000+" s");
	 	output.setText(ot);	
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void CreateChart(boolean data_use_feature) {
		System.out.println(data_feature1_min+" "+data_feature1_max+" "+data_feature2_min+" "+data_feature2_max);
        Stage secondStage = new Stage();
        secondStage.setTitle("ScatterChart Of Result");
        NumberAxis xAxis ;//(int)data_feature1_min, (int)data_feature1_max, (int)Math.abs((data_feature1_max-data_feature1_min)/10));
        NumberAxis yAxis;//(int)data_feature2_min,(int) data_feature2_max, (int)Math.abs((data_feature2_max-data_feature2_min)/10)); 
        if((Math.abs(data_feature1_max-data_feature1_min))<=0.05){
        	xAxis = new NumberAxis(0.1,0.9,0.05);//(int)data_feature1_min, (int)data_feature1_max, (int)Math.abs((data_feature1_max-data_feature1_min)/10));
        	yAxis = new NumberAxis(0.1,0.9,0.05);//(int)data_feature2_min,(int) data_feature2_max, (int)Math.abs((data_feature2_max-data_feature2_min)/10));        
        	System.out.println("modify");
         }
         else {
        	xAxis = new NumberAxis((float)data_feature1_min,(float)data_feature1_max,0.005);//(int)data_feature1_min, (int)data_feature1_max, (int)Math.abs((data_feature1_max-data_feature1_min)/10));
        	yAxis = new NumberAxis((float)data_feature2_min,(float)data_feature2_max,0.005);//(int)data_feature2_min,(int) data_feature2_max, (int)Math.abs((data_feature2_max-data_feature2_min)/10));        
         }
        final LineChart<Number,Number> sc = new LineChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("output of perceptron1");                
        yAxis.setLabel("output of perceptron2");
        sc.setTitle("Chart  Of test set in "+file_name[0]);
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
       // XYChart.Series line = new XYChart.Series();
        if(data_use_feature) { 
           xAxis = new NumberAxis((float)data_feature1_min,(float)data_feature1_max,0.01);//(int)data_feature1_min, (int)data_feature1_max, (int)Math.abs((data_feature1_max-data_feature1_min)/10));
      	   yAxis = new NumberAxis((float)data_feature2_min,(float)data_feature2_max,0.01);
           double[] data_show1=data_feature1; 
		   double[] data_show2=data_feature2;
		   series1.setName("class 1");
    	   for(int i = 0 ; i <data_size[0] ; i++) {
    		   series1.getData().add(new XYChart.Data(data_show1[i], data_show2[i]));	// x, y respectily 
    	   } 
    	 //  line.setName("divide line ");
        
    	 /*  for(double x = 0 ; x <= 1 ; x+=0.1 ) {
    		   double y =-3*x;// ((-weight[2][2]*x+weight[2][0])/weight[2][1]);
    		   line.getData().add(new XYChart.Data(x,y));
    		   // line.getData().add(new XYChart.Data(x+0.05,1/(1+Math.exp(-y))));
    	   }*/
    	   sc.getData().addAll(series1);
       }
       else {
    	   double[][] data_show1=class1; 
		   double[][] data_show2=class2;
    	   series1.setName("class 1");
    	   for(int i = 0 ; i <class1_size ; i++) {
    		   series1.getData().add(new XYChart.Data(data_show1[i][0], data_show1[i][1]));
    	   }
    	   series2.setName("class 2");
    	   for(int i = 0 ; i <class2_size ; i++) {
    		   series2.getData().add(new XYChart.Data(data_show2[i][0],data_show2[i][1]));
    	   }
    	  // line.setName("divide line ");
        
    	   /*for(double x =(float)data_feature1_min  ; x <=(float)data_feature1_max  ; x+=0.1 ) {
    		   double y = -3*x;//((-weight[2][2]*x+weight[2][0])/weight[2][1]);
    		   line.getData().add(new XYChart.Data(x,y));
    		   // line.getData().add(new XYChart.Data(x+0.05,1/(1+Math.exp(-y))));
    	   }*/
    	   sc.getData().addAll(series1, series2);
       }
        sc.setAnimated(false);
        sc.setCreateSymbols(true);
        
        
        /*ane pane = new Pane();
        pane.getChildren().add(sc);
        pane.getChildren().add(lc);
        */
        
        Scene second_scene = new Scene(sc,650,650);
        second_scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        secondStage.setScene(second_scene);
        secondStage.show();	
	}
	
	public String Predict(boolean test_data_type ,int[] seq,int seq_length,int units,int dense,double[] y ,double[][] input , double[] result,double[][][] weight,String ot) {
		//System.out.println("test_data_type: "+test_data_type);
		double acc = 0 ;
 		int count = 0 ;
 		double rmse=0;
 		
 		//System.out.println(weight==weight_pocket);
 		for(int z = 0 ; z < seq_length ; z++) {
 			count++;
 			int i = seq[z];
 			//System.out.println("i is : "+i);
 			input[0][0] = -1; 
 			input[0][1] = data_feature1[i];
 			input[0][2]=  data_feature2[i];
 			double out_result = 0;
 			// for first layer
 			for(int units_running = -1 ; units_running<units ; units_running++) {
 				if(units_running == -1 ) {
 					y[0] = -1;
 				}
 				else {
 					y[units_running+1] = input[0][0]*weight[0][units_running][0]
							+input[0][1]*weight[0][units_running][1]
							+input[0][2]*weight[0][units_running][2];
 					y[units_running+1] =  1/(1+Math.exp(-y[units_running+1]));// y[0]<0?0:y[0]; activation function
 					
 				}
 			}
 			for(int units_running = 0 ; units_running < units+1 ; units_running++) {
 					input[1][units_running] = y[units_running];
 			}
 			// input layer to hidden layer
 			
 			for(int dense_running = 1; dense_running < dense-1 ; dense_running++) {
 				//System.out.println("enter hidden layer\n");
 				y[0] = -1;
 				for(int units_running = 0 ; units_running< units ; units_running++) {
 						for(int j = 0 ; j <units+1 ; j++) {
 							y[units_running+1] += input[dense_running][j]*weight[dense_running][units_running][j];
 							//System.out.println(" j :"+j+" unit_running: "+units_running+" dense_running:"+dense_running);
 						}
 						y[units_running+1] =  1/(1+Math.exp(-y[units_running+1]));// y[0]<0?0:y[0]; activation function
 				}
 				for(int units_running = 0 ; units_running< units+1 ; units_running++) {
 					input[dense_running+1][units_running]= y[units_running];
 					//System.out.println("dense_running+1 : "+dense_running+1+" dense: "+dense+" y[units_running]"+y[units_running]);
 				}
 			}
 			for(int j = 0 ; j < units+1 ; j++) {
 					out_result+=y[j]*weight[dense-1][0][j];
 			}
 			out_result =  1/(1+Math.exp(-out_result));
 			double normaliztion_out=out_result;
 			if(dataset_result_range_2to1[0]) {
 				normaliztion_out=out_result+1;
 			}
 			rmse+=Math.pow(normaliztion_out-result[i], 2);
 			 int temp = (int) (normaliztion_out+0.5);
 			normaliztion_out = temp;
 			 
 			 ot+=(i+".predict :  "+normaliztion_out+" actual: "+result[i]+"\n");;
 			 if(normaliztion_out == result[i]) {
 				 acc++;
 			 }
 			 if(test_data_type) {
 				if(data_feature1_min > input[0][1]) {
 					 data_feature1_min = input[0][1];
 				 }
 				 if(data_feature1_max < input[0][1]) {
 					data_feature1_max = input[0][1];
 				 }
 				 if(data_feature2_min > input[0][2]) {
 					data_feature2_min = input[0][2];
 				 }
 				 if(data_feature2_max < input[0][2]) {
 					data_feature2_max = input[0][2];
 				 }
 			 
 				 if(normaliztion_out == (dataset_result_range_2to1[0]?2:1)) {
 					 class1[class1_size][0] = data_feature1[i];
 					 class1[class1_size][1] = data_feature2[i];
 					 class1_size++;
 				 }
 				 else { 
 					 class2[class2_size][0] = data_feature1[i];
 					 class2[class2_size][1] = data_feature2[i];
 					 class2_size++;
 					 
 				 }
 				 //System.out.println("i is "+i+"r is :"+r+" data_size: "+data_size[0]);

 			 }
	 	}
 		//System.out.println(acc+" "+count);
	 	ot+=("\naccuracy is :"+acc/count+"\t RMSE is : "+Math.sqrt(rmse/count)*100+"%\n");
	 	if((acc/count) >max_acc_pocket) {
	 		max_acc_pocket = acc/count;
	 		for(int i = 0 ; i< dense ; i++) {
		 		for(int j = 0 ; j < units ; j++) {
		 			for(int k = 0 ; k < units+1 ; k++) {
		 				weight_pocket[i][j][k] = weight[i][j][k];
		 			}
		 		}
		 	}
	 	}
	 	//System.out.println("666"+ot.charAt(0));
	 	if(ot.charAt(0)=='?'&&(acc/count) < max_acc_pocket) {
	 		ot='X'+ot.substring(1) ;
	 		weight = weight_pocket;
	 		System.out.println("packet is better");
	 	}
	 	return ot; 
	}
	
	private void NumberRecongnition(double momentum,double learn,double epoch,int data_size,TextArea output) {
		// TODO Auto-generated method stub
		
		System.out.println("number");
		String ot = output.getText();
		ot="train is complete, the following is result(learning rate :"+learn+" momentum :"+momentum+" trainning time:+"+train_time+"):\n";
	 	ot+="\n---------------------\n following is recongnition result:\n";
	 	int result_one_hotpot[][]= new int [data_size][4];
	 	for(int i = 0 ; i < data_size ; i++) {
	 		for(int j = 0 ;j <4 ; j++) {
	 			if(number_result[i]==j) {
	 				result_one_hotpot[i][j]=1;
	 			}
	 			else {
	 				result_one_hotpot[i][j]=0;
	 			}
	 		}
	 	}
	 	double hiddenlayer_weight[][] = new double[25][25];
	 	for(int i = 0 ; i < 25 ; i++) {
	 		for(int j = 0 ; j < 25 ; j++) {
	 			hiddenlayer_weight[i][j]=Math.random()*2-1;
	 		}
	 	}
	 	double outputlayer_weight[][] = new double[4][25];
	 	for(int i = 0 ; i < 4 ; i++) {
	 		for(int j = 0 ; j < 25 ; j++) {
	 			outputlayer_weight[i][j]=Math.random()*2-1;
	 		}
	 	}
	 	double outputlayer_output[] = new double[4]; 
	 	double hiddenlayer_output[] = new double[25];
	 	double max=0;
	 	int max_index =0;
	 	double acc = 0;
	 	double count = 0;
	 	double rmse=0;
	 	while(epoch>0){
	 		for(int data_running = 0 ; data_running < data_size;data_running++) {
	 			
	 			for(int units_running = 0 ;units_running <25; units_running++) {
	 				for(int i = 0 ;i<25 ; i ++) {
	 					hiddenlayer_output[units_running]+=number_feature[data_running][i]*hiddenlayer_weight[units_running][i];
	 				}
	 				hiddenlayer_output[units_running] = 1/(1+Math.exp(-hiddenlayer_output[units_running]));
	 			}
	 			for(int units_running = 0 ; units_running<4 ; units_running++) {
	 				for(int i = 0 ; i <25 ; i++) {
	 					outputlayer_output[units_running]+=hiddenlayer_output[i]*outputlayer_weight[units_running][i];
	 				}
	 				outputlayer_output[units_running] = 1/(1+Math.exp(-outputlayer_output[units_running]));
	 			}
	 			max = outputlayer_output[0];
	 			for(int i = 0 ; i < 4 ; i++) {
	 				if(outputlayer_output[i]>max) {
	 					max = outputlayer_output[i];
	 					max_index = i;
	 				}
	 			}
	 			System.out.println("training :"+max_index);
	 			//-----------------------back propagation--------------------------------------
	 				double[] delta = new double[4];
	 				int delta_index = 0;
	 				for(int units_running = 0 ; units_running < 4 ; units_running++) {
	 					for(int i = 0 ; i < 25 ; i ++) {
	 						outputlayer_weight[units_running][i]=momentum*outputlayer_weight[units_running][i]+learn*(result_one_hotpot[data_running][units_running]-outputlayer_output[units_running])
	 								*outputlayer_output[units_running]*(1-outputlayer_output[units_running])*hiddenlayer_output[i];
	 					}
	 					delta[delta_index] = (result_one_hotpot[data_running][units_running]-outputlayer_output[units_running])
 								*outputlayer_output[units_running]*(1-outputlayer_output[units_running]);
	 					delta_index++;
	 				}
	 				
	 				for(int units_running = 0 ; units_running < 25 ; units_running++) {
	 					double sigma_delta=0;
	 					for(int i = 0 ; i<4 ; i++) {
	 						sigma_delta+=delta[i]*outputlayer_weight[i][units_running];
	 					}
	 					for(int i = 0 ; i < 25 ; i ++) {
	 						hiddenlayer_weight[units_running][i] = momentum*hiddenlayer_weight[units_running][i]+learn*sigma_delta*number_feature[data_running][i];
	 					}
	 				}
	 		}
	 		epoch--;
	 	}
	 	
	 	//--------------------------data test ----------------------------------------------------
	 	for(int data_running = 0 ; data_running < data_size;data_running++) {
 			ot+=("data_pattern "+data_running+" :\n");
	 		for(int i = 0 ; i <5 ; i++) {
	 			for(int j = 0 ; j < 5 ; j++) {
	 				ot+=(int)number_feature[data_running][i*5+j];
	 			}
	 			ot+="\n";
	 		}
 			
 			for(int units_running = 0 ;units_running <25; units_running++) {
 				for(int i = 0 ;i<25 ; i ++) {
 					hiddenlayer_output[units_running]+=number_feature[data_running][i]*hiddenlayer_weight[units_running][i];	
 				}
 				hiddenlayer_output[units_running] = 1/(1+Math.exp(-hiddenlayer_output[units_running]));
 			}
 			for(int units_running = 0 ; units_running<4 ; units_running++) {
 				for(int i = 0 ; i <25 ; i++) {
 					outputlayer_output[units_running]+=hiddenlayer_output[i]*outputlayer_weight[units_running][i];
 				}
 				outputlayer_output[units_running] = 1/(1+Math.exp(-outputlayer_output[units_running]));
 			}
 			max = outputlayer_output[0];
 			for(int i = 0 ; i < 4 ; i++) {
 				rmse+=Math.pow(outputlayer_output[i]-result_one_hotpot[data_running][i], 2);
 				if(outputlayer_output[i]>max) {
 					max = outputlayer_output[i];
 					max_index = i;
 				}
 			}
 			count++;
 			System.out.println(max_index +" "+ (int )number_result[data_running]);
 			if(max_index == (int )number_result[data_running]) {
 				System.out.println("acc++");
 				acc++;
 			}
 			ot+=("predict number is :"+max_index+"  actual is "+number_result[data_running]+"\n\n");
 			System.out.println("predict result:"+max_index);
	 	}
	 	
	 	
	 	ot+=("accuracy is :"+acc/count+" RMSE is :"+Math.sqrt(rmse/(4*data_size)*100)+"%\n");
	 	output.setText(ot);
	}
	
	public String NumberDataProcessing(BufferedReader br,String out) {
		String str;
		try {
			int number_index = 0;
			int data_index=0;
			while((str = br.readLine())!=null ) {
				int str_div_head = 0;
				out+=(str+"\n");
				data_size[0]++;
				for(int i = 0 ; i< str.length();i++) {				
					if(str.charAt(i) == ' '||i == str.length()-1) {
						if(i == str.length()-1) {
							i++;
							number_result[number_index] = Double.parseDouble(str.substring(str_div_head,i));
							//System.out.println("i is "+i+"and str head is  "+str_div_head+"count_time is "+count_time);
						}
						else {	
							number_feature[number_index][data_index] = Double.parseDouble(str.substring(str_div_head,i));
							data_index++;
							str_div_head = i+1;
						}
					}
				}
				number_index++;
				data_index = 0;
			}
			out+="\n--------------------------------------------------\n";
			out+="notice!!  setting the  dense & units is useless  in the number recongnition\n"
					+ "\nand the chart function won't show the  result with scatter chart,because it's useless";
			out+="\n--------------------------------------------------\n";
			out+="transform to number: \n";
			for(int  i = 0 ; i < number_index ;i ++) {
				out+="\ndata_pattern+"+i+" :\n";
				System.out.println(number_result[i]);
				for(int j = 0 ; j < 5 ; j++) {
					for(int k = 0 ;k < 5 ; k++) {
						out+=(int)number_feature[i][j*5+k];
					}
					out+="\n";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return  out ;
	}
	
}
