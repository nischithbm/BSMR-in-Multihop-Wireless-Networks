//Importing custom packages
import node_pkg.*;
import crypt_pkg.*;
import packet_pkg.*;

//Importing libraries
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;



	// For Drawing Arrow
import static java.awt.geom.AffineTransform.getRotateInstance;
import static java.awt.geom.AffineTransform.getTranslateInstance;



public class Main extends JFrame implements ActionListener, ItemListener, ChangeListener{
	
    // private JLabel Tag;
	
	static Main mainFrame;
	MenuBar mbar=new MenuBar();
	Menu menu;
	MenuItem mi_help,mi_aboutUs,mi_exit;
	
	// Threads used
	Timer tplgyThread = new Timer("tplgyThread");
	Timer grpHelloThread = new Timer("grpHelloThread");
	Timer rchbltyThread = new Timer("rchbltyThread");
	
	Timer dataPktThread = null;
	Timer mRatePktThread = new Timer("mRatePktThread");
	
	
	public Node n1,n2,n3,n4;

	JToggleButton jtbtn_range;
	
	JButton jbtn_createNode,jbtn_ntwTplgy,jbtn_getReachableNodes,jbtn_simSetup,jbtn_startSim,jbtn_stopSim,jbtn_exit;
	JButton jbtn_startData,jbtn_stopData,jbtn_monitorNode;
	
	JButton jbtn_rreq;
	
	
	JComboBox jcbx_selNode,jc2;
	
	JSlider jsldr_movNodX,jsldr_movNodY,jsldr_trustFactr; 
	
	
	Color btn_bgColor = Color.blue;
	Color btn_fgColor = Color.white;
	int btn_fontSize;
	int btn_xPos,btn_yPos,btn_width,btn_height;
	
	
	
	Container c; 
	
	
	JFrame tplgyFrame=null;			// To draw network topology
	JFrame abtUsFrame=null;			// To draw network topology
	


	
	
	int xPosArray[] = {40,90,140,190,240,290,340,390,440,490,540,590,640,690,740,790,840};
	int yPosArray[] = {40,75,110,145,180,215,250,285,320,355,390,425,460,495,530};
	
	
	//int xPosArray[] = {40,70,100,130,160,190,220,250,280,310,340,370,400,430,460,490,520,550,580,610,640,670,700,730,760,790,820,850,880};
	//int yPosArray[] = {40,70,100,130,160,190,220,250,280,310,340,370,400,430,460,490,520};
	
	int placedPos[][]=new int[100][100];
	
	
	Main(){
		
		c=this.getContentPane();
		c.setBackground(Color.white);
		c.setLayout(null);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/livewire2.gif"));
	   	//this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


		addMenuItems();
		addControls();
	   
		showTopology();
		
		
		
		
		startThread(tplgyThread,50);


		for(int m=0;m<80;m++){
			for(int n=0;n<80;n++){
				placedPos[m][n]=0;
			}
		}
	   		
	}
	
	public void addMenuItems(){
		setMenuBar(mbar);

		menu=new Menu("HELP");
		mi_help=new MenuItem("How to Run");
		mi_exit=new MenuItem("Exit");
		mi_aboutUs=new MenuItem("About Us");
			
		mi_help.addActionListener(this);
		mi_aboutUs.addActionListener(this);
		mi_exit.addActionListener(this);
		    
		menu.add(mi_help);
		menu.add(mi_aboutUs);
		menu.add(mi_exit);
		mbar.add(menu);
	}
	
	
	public void addControls(){
	    jbtn_createNode=new JButton("Create Node");
	    jbtn_ntwTplgy=new JButton("Draw Network Topology");
	    jtbtn_range=new JToggleButton("Show Node Range");
	    jbtn_getReachableNodes=new JButton("Get Reachable Nodes");
	    jbtn_simSetup=new JButton("Simulation Setup");
	    jbtn_startSim=new JButton("Start Simulation");
	    jbtn_stopSim=new JButton("Stop Simulation");
	    jcbx_selNode=new JComboBox();
	    jbtn_monitorNode=new JButton("Monitor Node status");
	    jbtn_rreq=new JButton("RREQ");
	    jbtn_startData=new JButton("Start Sending Data");
	    jbtn_stopData=new JButton("Stop Sending Data");
	    jbtn_exit=new JButton("EXIT");
	    
	    
	    // Specify display attributes
	    btn_bgColor = Color.blue;
		btn_fgColor = Color.white;
		btn_fontSize = 10;
		
	    jbtn_createNode.setBackground(btn_bgColor);
	    jbtn_createNode.setForeground(btn_fgColor);
	    jbtn_createNode.setFont(new Font(null,Font.BOLD,btn_fontSize));
	    jbtn_createNode.addActionListener(this);
	  
		
		
		
		
		jbtn_ntwTplgy.setBackground(btn_bgColor);
		jbtn_ntwTplgy.setForeground(btn_fgColor);
		jbtn_ntwTplgy.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_ntwTplgy.addActionListener(this);
		
		
		jtbtn_range.setBackground(btn_bgColor);
		jtbtn_range.setForeground(btn_fgColor);
		jtbtn_range.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jtbtn_range.addItemListener(this);
		
		
		jbtn_getReachableNodes.setBackground(btn_bgColor);
		jbtn_getReachableNodes.setForeground(btn_fgColor);
		jbtn_getReachableNodes.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_getReachableNodes.addActionListener(this);
		
		
		
		
		
		jbtn_simSetup.setBackground(Color.black);
		jbtn_simSetup.setForeground(btn_fgColor);
		jbtn_simSetup.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_simSetup.addActionListener(this);
		
		
		jbtn_startSim.setBackground(Color.black);
		jbtn_startSim.setForeground(btn_fgColor);
		jbtn_startSim.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_startSim.setEnabled(false);
		jbtn_startSim.addActionListener(this);

		jbtn_stopSim.setBackground(Color.red);
		jbtn_stopSim.setForeground(Color.white);
		jbtn_stopSim.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_stopSim.setEnabled(false);
		jbtn_stopSim.addActionListener(this);
		
		
		jcbx_selNode.setBackground(btn_bgColor);
   		jcbx_selNode.setFont(new Font(null,Font.BOLD,btn_fontSize));
   		jcbx_selNode.addItem("Select a Node");
   		jcbx_selNode.setEnabled(false);
   		jcbx_selNode.addActionListener(this);
   		
   		jbtn_monitorNode.setBackground(btn_bgColor);
   		jbtn_monitorNode.setForeground(Color.gray);
   		jbtn_monitorNode.setFont(new Font(null,Font.BOLD,btn_fontSize));
   		jbtn_monitorNode.setEnabled(false);
   		jbtn_monitorNode.addActionListener(this);
		
   		jbtn_rreq.setBackground(btn_bgColor);
   		jbtn_rreq.setForeground(Color.gray);
   		jbtn_rreq.setFont(new Font(null,Font.BOLD,btn_fontSize));
   		jbtn_rreq.setEnabled(false);
   		jbtn_rreq.addActionListener(this);
		

		
   		jbtn_startData.setBackground(btn_bgColor);
		jbtn_startData.setForeground(btn_fgColor);
		jbtn_startData.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_startData.setEnabled(false);
		jbtn_startData.addActionListener(this);
		
		jbtn_stopData.setBackground(btn_bgColor);
		jbtn_stopData.setForeground(btn_fgColor);
		jbtn_stopData.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_stopData.setEnabled(false);
		jbtn_stopData.addActionListener(this);
		

   		jbtn_exit.setBackground(Color.red);
		jbtn_exit.setForeground(btn_fgColor);
		jbtn_exit.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_exit.addActionListener(this);
		
		
		
		
   		
   		
   		
   		
   		// Specify button locations
		btn_xPos=10;	//950
		btn_yPos=20;
		btn_width=150;
		btn_height=25;
		
		//btn_xPos=500;	//950
		//btn_yPos=50;
		//btn_width=220;
		//btn_height=35;
		
   		
		jbtn_createNode.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jbtn_ntwTplgy.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jtbtn_range.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		
   		jbtn_getReachableNodes.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);	btn_yPos+=btn_height+5;
   		jbtn_simSetup.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jbtn_startSim.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jbtn_stopSim.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		
   		
   		jcbx_selNode.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		jbtn_monitorNode.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jbtn_rreq.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		
   		jbtn_startData.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jbtn_stopData.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		
   		jbtn_exit.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		
   		
   		
   		
   		
   		jsldr_movNodX = new JSlider();
   		jsldr_movNodY = new JSlider();
   		
   		jsldr_movNodX.setOrientation(JSlider.VERTICAL);
   		jsldr_movNodX.setMinimum(50);
   		jsldr_movNodX.setMaximum(880);
   		jsldr_movNodX.setMajorTickSpacing(100);
   		jsldr_movNodX.setMinorTickSpacing(25);
   		jsldr_movNodX.setPaintTicks(true);
   		jsldr_movNodX.setPaintLabels(true);
   		jsldr_movNodX.setEnabled(false);
   		jsldr_movNodX.addChangeListener(this);
   		
   		jsldr_movNodY.setOrientation(JSlider.VERTICAL);
   		jsldr_movNodY.setMinimum(50);
   		jsldr_movNodY.setMaximum(550);
   		jsldr_movNodY.setMajorTickSpacing(100);
   		jsldr_movNodY.setMinorTickSpacing(25);
   		jsldr_movNodY.setPaintTicks(true);
   		jsldr_movNodY.setPaintLabels(true);
   		jsldr_movNodY.setEnabled(false);
   		jsldr_movNodY.addChangeListener(this);
   		
   		jsldr_movNodX.setBounds(10,400,60,200);
   		jsldr_movNodY.setBounds(70,400,60,200);
   		
   		
   		
   		
   		
   		// Add buttons to the frame
   		this.add(jbtn_createNode);
   		this.add(jbtn_ntwTplgy);
   		this.add(jtbtn_range);
   		this.add(jbtn_getReachableNodes);
   		
   		this.add(jbtn_simSetup);
   		this.add(jbtn_startSim);
   		this.add(jbtn_stopSim);
   		
   		this.add(jcbx_selNode);
   		this.add(jbtn_monitorNode);
   		this.add(jbtn_rreq);
   		
   		this.add(jbtn_startData);
   		this.add(jbtn_stopData);
   		this.add(jbtn_exit);
   		
   		
   		this.add(jsldr_movNodX);
   		this.add(jsldr_movNodY);
   		
   		
	}
	
	
	public void showTopology(){
		Topology tp = new Topology();
		
		tplgyFrame=new JFrame("Network Topology");
		tplgyFrame.getContentPane().add(tp);
		tplgyFrame.setBackground(Color.white);
		tplgyFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		tplgyFrame.setBounds(210,10,950,650);
		tplgyFrame.setResizable(false);
		tplgyFrame.setVisible(true);
	}
	


	  public static void main(String[] args) {
		new SplashScreen();

		mainFrame = new Main();
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/livewire2.gif"));
		mainFrame.setTitle("BSMR in Multihop Wireless Networks");
		mainFrame.setBounds(5, 5, 200, 700); // mainFrame.setBounds(50,20,1200,800);

		UIManager.LookAndFeelInfo lookAndFeels[] = UIManager.getInstalledLookAndFeels();

		try {
			UIManager.setLookAndFeel(lookAndFeels[1].getClassName());
			SwingUtilities.updateComponentTreeUI(mainFrame);
		} catch (Exception e) {
		}

		mainFrame.setResizable(false);
		mainFrame.setLayout(null);

		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		mainFrame.setVisible(true);
	}
  
	  
	  
	  public void genInitialTopology(){
		  
			int i=0,j=0;
			
			int midX = (int)(xPosArray.length)/2;
			int midY = (int)(yPosArray.length)/2;

			
			int repeat=0;
			
			// Place the source at the center
				 i = midX;
				 j = midY;

				placedPos[i][j]=1;
				n1 = new Node(NodeType.SOURCE,xPosArray[i],yPosArray[j]);
				tplgyFrame.repaint();
				jcbx_selNode.addItem(n1.getIpAddress());
				
				jcbx_selNode.setEnabled(true);
			 
				
				for(int k=1;k<Node.nwSize;){
					boolean canPlace = false;
					repeat++;
					//Generate Random indexes i and j
					if(k<5){
						 i = Main.random(midX-3,midX+3);
						 j = Main.random(midY-3,midY+3);
					}
					else if(k<10){
						 i = Main.random(midX-5,midX+5);
						 j = Main.random(midY-5,midY+5);
					}
					
					else{
						i = Main.random(xPosArray.length);
						j = Main.random(yPosArray.length);
					}
					if(placedPos[i][j]==1){
						// check if place is already occupied
					 }
					 else if( (i+1<=xPosArray.length && placedPos[i+1][j]==1) ||
					 		(j+1<=yPosArray.length && placedPos[i][j+1]==1) ||
					 		(i-1>=0 && placedPos[i-1][j]==1) ||
					 		(j-1>=0 && placedPos[i][j-1]==1) 	){
						// check if next place is already occupied
					 }
					 else{
						 canPlace = true;
					 }
					 
					if(canPlace){
						placedPos[i][j]=1;
						repeat=0;
						n1 = new Node(NodeType.NON_MEMBER_NODE,xPosArray[i],yPosArray[j]);
					//	tplgyFrame.repaint();
					//	System.out.println("ip="+n1.getIpAddress());
						jcbx_selNode.addItem(n1.getIpAddress());
						
						
						k++;
					}
					if(repeat>10){
						System.out.println("cant place more nodes..");
						break;
					}
					
				}
				


		}
	  
	
	  
	  
	  
@Override
public void actionPerformed(ActionEvent ae) {
	
	if(ae.getSource()==jbtn_createNode)
	{


		//int xPosArray[] = {350,100,414,200,200,300,100,500,250,150,700,500,250,450,350,700,650,800,600};
		//int yPosArray[] = {300,100,180,455,400,100,400,100,300,220,550,500,570,400,750,150,350,500,450};
		
		
		int i=0,j=0;
		while(true){
			boolean canPlace = false;	
			//Generate Random indexes i and j
			 i = Main.random(xPosArray.length);
			 j = Main.random(yPosArray.length);

			 
			 // check if a;ready placed
			 if(placedPos[i][j]==1){
				
			 }
			 else if( (i+1<=xPosArray.length && placedPos[i+1][j]==1) ||
			 		(j+1<=yPosArray.length && placedPos[i][j+1]==1) ||
			 		(i-1>=0 && placedPos[i-1][j]==1) ||
			 		(j-1>=0 && placedPos[i][j-1]==1) 	){
				
			 }
			 else{
				 canPlace = true;
			 }
			 
			if(canPlace){
				placedPos[i][j]=1;
				n1 = new Node(NodeType.NON_MEMBER_NODE,xPosArray[i],yPosArray[j]);
				//System.out.println("ip="+n1.getIpAddress());
				jcbx_selNode.addItem(n1.getIpAddress());
				//tplgyFrame.repaint();
				break;
			}
			
				
		}



		for(int i1=0;i1<Node.totalNodes;i1++){
			NodeTable.getNode(i1+1).findReachableNodes();
		}


		
    }
	
	
	if(ae.getSource()==jbtn_ntwTplgy){
		tplgyFrame.setVisible(true);
	}
	
	
	if(ae.getSource()==jbtn_startData){
		jbtn_stopData.setEnabled(true);
		
		dataPktThread = new Timer("dataPktThread");
		startThread(dataPktThread,2 * 1000);

		//LineChart.chart();
	}
	
	
	if(ae.getSource()==jbtn_stopData){
		stopThread(dataPktThread);	
		jbtn_stopData.setEnabled(false);
	}
	

	if(ae.getSource()==jbtn_getReachableNodes)
	{
		for(int i1=0;i1<Node.totalNodes;i1++){
			NodeTable.getNode(i1+1).findReachableNodes();
		}
		
		
		/*
		
		
		if(NodeTable.getNode(1).canSend()){
			System.out.println("I will send");
		}
		else{
			System.out.println("I will Not send");
		}
		
		//NodeTable.getNode(1).multicastData("Hello this is 1st message");
		//NodeTable.getNode(2).multicastData("Hai from node 2");
		//NodeTable.getNode(3).multicastData("Hellooo from node 3");
		
		*/

    }
	
	
	if (ae.getSource() == jbtn_simSetup) {
		jbtn_simSetup.setEnabled(false);
		SimSetup simSetup = new SimSetup();
		jbtn_startSim.setEnabled(true);
		jbtn_startData.setEnabled(true);
	}
	
	if (ae.getSource() == jbtn_startSim) {
		jbtn_startSim.setEnabled(false);
		System.out.println("Network Size = " + Node.nwSize);

		genInitialTopology();
			
		startThread(grpHelloThread, 9000);
		startThread(rchbltyThread, 9000);

		jbtn_stopSim.setEnabled(true);
		jbtn_startData.setEnabled(true);
	}
	
	
	if(ae.getSource()==jbtn_stopSim){
		// update simulation summary
		
		
		
		// clear all threads..
		stopThread(dataPktThread);			// 	Stop sending data packets
		stopThread(grpHelloThread);	
		stopThread(mRatePktThread);			
		
		//this.jcbx_selNode.removeAllItems();
		resetControls();
		
		
		for(int k=0;k<Node.totalNodes;k++){
			// clear all threads in each node..
			Node tmp = NodeTable.getNode(k+1);
			
			//tmp.stopThread(tmp.bufferThread);
			tmp.stopThread(tmp.beaconThread);
			tmp.stopThread(tmp.stickToMeThread);
			tmp.stopThread(tmp.beaconChkThread);
			tmp.stopThread(tmp.connThread);
			tmp.stopThread(tmp.RA_Timer);
			tmp.stopThread(tmp.StickToMe_Timer);
			
			tmp.setNodeMonitorVisibility(false);
		}
		
		//Node.reset();
		
		//NodeTable.nodeTable.clear();
		jbtn_simSetup.setEnabled(true);
	}
	
		
	if(ae.getSource()==jcbx_selNode)
	{
	     int indx=(Integer)jcbx_selNode.getSelectedIndex();
	     
	      if(indx==0)
			{
	    	  jbtn_monitorNode.setForeground(Color.gray);
	    	  jbtn_monitorNode.setEnabled(false);
	    	  
	    	  jbtn_rreq.setForeground(Color.gray);
	    	  jbtn_rreq.setEnabled(false);
	    	  
	    	  
	    	  jsldr_movNodX.setEnabled(false);
	    	  jsldr_movNodY.setEnabled(false);
	    	  
			}
	      else{
	    	  jbtn_monitorNode.setForeground(Color.white);
	    	  jbtn_monitorNode.setEnabled(true);
	    	  
	    	  
	    	 
    		  
    		  if(!NodeTable.getNode(indx).Connected){
    			  jbtn_rreq.setForeground(Color.white);
        		  jbtn_rreq.setEnabled(true);
    		  }
    		  else{
    			  jbtn_rreq.setForeground(Color.gray);
    			  jbtn_rreq.setEnabled(false);
    		  }
    		  
			    	 // if(NodeTable.getNode(indx).getNodeType() == NodeType.NON_MEMBER_NODE){
			    		  
			    	 // }
			    	 // else{
			    	//	  jbtn_rreq.setForeground(Color.gray);
			    	//	  jbtn_rreq.setEnabled(false);
			    	 // }
	    	  
	    	  jsldr_movNodX.setValue(NodeTable.getNode(indx).getPosX());
	    	  jsldr_movNodY.setValue(NodeTable.getNode(indx).getPosY());
	    	  jsldr_movNodX.setEnabled(true);
	    	  jsldr_movNodY.setEnabled(true);
	    	  
	      }
	}
	
	if(ae.getSource()==jbtn_monitorNode){
	      int indx=(Integer)jcbx_selNode.getSelectedIndex();
	   
			//NodeTable.getNode(indx).findReachableNodes();
			//NodeTable.getNode(indx).getReachableNodes();
	      
	      NodeTable.getNode(indx).setNodeMonitorVisibility(true);
	     // NodeTable.getNode(indx).dumpNodeStatus("Hello..", 1);
	      
	      

			//int indx=(Integer)jcbx_selNode.getSelectedIndex();
			
		//NodeTable.getNode(indx).printLinkHistory();
	     
	   		
	      
		System.out.println(indx+"=>Data Rate: "+NodeTable.getNode(indx).calcPercvdDataRate());
		
	}
	
	
	if(ae.getSource()==jbtn_rreq)
	{
		
		int indx=(Integer)jcbx_selNode.getSelectedIndex();
	
		NodeTable.getNode(indx).attempts =0;
		NodeTable.getNode(indx).initiateRouteDiscovery();
		NodeTable.getNode(indx).Connected = false;		// just to test
	    jbtn_rreq.setEnabled(false);
    }
	
	
	if(ae.getSource()==mi_help)
	{
		
		//this.generateValidPositions();		// No need in the final project code
		
	}
	
	
	if(ae.getSource()==mi_aboutUs){
		
		//startThread(mRatePktThread,500);
		//genMRate();
		NodeTable.getNode(1).createSignSend(PacketType.MRATE);
		
		
		
		/*int indx=(Integer)jcbx_selNode.getSelectedIndex();
		NodeTable.getNode(indx).Connected=false;
		NodeTable.getNode(indx).sendTreePrune();
		*/
		
		
		/*		// remove comment later
		if(this.abtUsFrame!=null){
			this.abtUsFrame.dispose();
		}
		this.abtUsFrame = new AboutUs();
		*/
	}
	
	
	/*if(ae.getSource()==mi_exit){
		// Menu Item Exit
		System.exit(0);
	}*/
	
	if(ae.getSource()==jbtn_exit || ae.getSource()==mi_exit){
		System.exit(0);
		//new CloseDialog(new JFrame(), "Alert!", "Do you really want to exit ?");
    }
	
}

	@Override
	public void itemStateChanged(ItemEvent ie) {

		if (ie.getSource() == jtbtn_range) {
			if (jtbtn_range.isSelected()) {
				Node.showRange = true;
				jtbtn_range.setText("Hide Node Range");
			} else {
				Node.showRange = false;
				jtbtn_range.setText("Show Node Range");
			}
		}

	}

	@Override
	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==jsldr_movNodX){
			int indx=(Integer)jcbx_selNode.getSelectedIndex();
			int value = jsldr_movNodX.getValue();
			
			NodeTable.getNode(indx).setPosX(value);
		}
		if(ce.getSource()==jsldr_movNodY){
			int indx=(Integer)jcbx_selNode.getSelectedIndex();
			int value = jsldr_movNodY.getValue();
			
		    NodeTable.getNode(indx).setPosY(value);
		}
	}


	public void refreshReachableNodes(){
		for(int i1=0;i1<Node.totalNodes;i1++){
			NodeTable.getNode(i1+1).findReachableNodes();
		}
	}

	
	public void startThread(Timer t,int milSec){
		t.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				String curThreadName = Thread.currentThread().getName();
					
				if(curThreadName.equals("tplgyThread")){
					//System.out.println("tp thread");
					tplgyFrame.repaint();
				}
				else if(curThreadName.equals("rchbltyThread")){
					refreshReachableNodes();
				}
				else if(curThreadName.equals("grpHelloThread")){
					// send GrpHello Message
				}
				else if(curThreadName.equals("dataPktThread")){
					NodeTable.getNode(1).createSignSend(PacketType.DATA_PKT);
				}
			}
        }, 0,milSec);
		
	}
	

	public void resetControls(){
			jbtn_startSim.setEnabled(false);
		
			jbtn_stopSim.setEnabled(false);
		
	   		jbtn_monitorNode.setEnabled(false);
	   		
	   		jbtn_rreq.setEnabled(false);
	   		
	   		
			
	   		jcbx_selNode.setEnabled(false);
	   
	   		
		
	}
	
	public void stopThread(Timer t) {
		if(t!=null){
			t.cancel();
			t.purge();
			t=null;
		}
	}
	

	public static int random(int max) {
		// generates a random integer within the given range
		Random r = new Random();
		return r.nextInt(max);
	}

	public static int random(int min, int max) {
		// generates a random integer within the given range
		Random r = new Random();
		return r.nextInt(max - min + 1) + min;
	}


	protected void shutdown() {
	    // To perform any other shutdown tasks
	    
		
	}

	
	

	// not necessary in final code
	public void generateValidPositions(){
		int PAD = 40;
		int PAD2 = (int) (PAD * 1.5);
		int PAD3 = (int) (PAD * 2.5);
		n1 = new Node(NodeType.SOURCE,400,400);

		
		int i=0,j=0;
		int x,y;
		int xInc = 30;
		int yInc = 30;
		String xStr = "int xPosArray[] = {"+PAD;
		String yStr = "int yPosArray[] = {"+PAD;
		
		for(i=0,x=PAD; x+xInc < this.tplgyFrame.getWidth()-PAD2;i++){
			x += xInc;
			xStr += ","+x;
		}
		for(j=0,y=PAD; y+yInc < this.tplgyFrame.getHeight()-PAD3;j++){
			y += yInc;
			yStr += ","+y;
		}
		xStr += "};";
		yStr += "};";
		System.out.println("i="+i+" j="+j);
		
		System.out.println(xStr);
		System.out.println();
		System.out.println(yStr);
		
	}
	
}