//Importing custom packages
import node_pkg.*;
import crypt_pkg.*;
import packet_pkg.*;

//Importing libraries
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

	JToggleButton jtbtn_range,jtbtn_data,jtbtn_blkHole;
	
	JButton jbtn_createNode,jbtn_getReachableNodes,jbtn_simSetup,jbtn_genTplgy,jbtn_exit;
	JButton jbtn_monitorNode;
	
	JButton jbtn_rreq,jbtn_treePrune;
	
	
	JComboBox jcbx_selNode,jc2;
	
	JSlider jsldr_movNodX,jsldr_movNodY,jsldr_trustFactr; 
	JLabel jlb_posX,jlb_posY;
	
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
		//c.setLayout(null);
		//this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/livewire2.gif"));
	   	//this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addMenuItems();
		addControls();
	   
		showTopology();
		
		startThread(tplgyThread,50);

		for(int m=0;m<100;m++){
			for(int n=0;n<100;n++){
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
	   
	    jtbtn_range=new JToggleButton("Show Node Range");
	    
	    jbtn_simSetup=new JButton("Simulation Setup");
	    jbtn_genTplgy=new JButton("Generate Topology");
	  
	    jtbtn_data=new JToggleButton("Start Sending Data");
	    
	    jcbx_selNode=new JComboBox();
	    jbtn_monitorNode=new JButton("Monitor Node status");
	    jbtn_rreq=new JButton("RREQ");
	    jbtn_treePrune=new JButton("Tree Prune");
	    jtbtn_blkHole = new JToggleButton("Make it Black Hole");
	    
	    jbtn_exit=new JButton("EXIT");
	    
	    
	    jlb_posX = new JLabel("pos X");
	    jlb_posY = new JLabel("pos Y");
	    
	    // Specify display attributes
	    btn_bgColor = Color.blue;
		btn_fgColor = Color.white;
		btn_fontSize = 10;
		
	    jbtn_createNode.setBackground(btn_bgColor);
	    jbtn_createNode.setForeground(btn_fgColor);
	    jbtn_createNode.setFont(new Font(null,Font.BOLD,btn_fontSize));
	    jbtn_createNode.addActionListener(this);
	  
	    


		jtbtn_range.setBackground(btn_bgColor);
		jtbtn_range.setForeground(btn_fgColor);
		jtbtn_range.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jtbtn_range.addItemListener(this);
		
		
		jbtn_simSetup.setBackground(Color.black);
		jbtn_simSetup.setForeground(btn_fgColor);
		jbtn_simSetup.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_simSetup.addActionListener(this);
		
		
		jbtn_genTplgy.setBackground(Color.black);
		jbtn_genTplgy.setForeground(btn_fgColor);
		jbtn_genTplgy.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_genTplgy.setEnabled(false);
		jbtn_genTplgy.addActionListener(this);

		
		
		jtbtn_data.setBackground(btn_bgColor);
		jtbtn_data.setForeground(btn_fgColor);
		jtbtn_data.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jtbtn_data.setEnabled(false);
		jtbtn_data.addItemListener(this);
		
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
		
   		
   		jbtn_treePrune.setBackground(btn_bgColor);
   		jbtn_treePrune.setForeground(Color.white);
   		jbtn_treePrune.setFont(new Font(null,Font.BOLD,btn_fontSize));
   		jbtn_treePrune.setEnabled(false);
   		jbtn_treePrune.addActionListener(this);
		
   		
   		jbtn_rreq.setBackground(btn_bgColor);
   		jbtn_rreq.setForeground(Color.gray);
   		jbtn_rreq.setFont(new Font(null,Font.BOLD,btn_fontSize));
   		jbtn_rreq.setEnabled(false);
   		jbtn_rreq.addActionListener(this);
		
   		
   		jtbtn_blkHole.setBackground(btn_bgColor);
   		jtbtn_blkHole.setForeground(btn_fgColor);
   		jtbtn_blkHole.setFont(new Font(null,Font.BOLD,btn_fontSize));
   		jtbtn_blkHole.setEnabled(false);
   		jtbtn_blkHole.addItemListener(this);
			

   		jbtn_exit.setBackground(Color.red);
		jbtn_exit.setForeground(btn_fgColor);
		jbtn_exit.setFont(new Font(null,Font.BOLD,btn_fontSize));
		jbtn_exit.addActionListener(this);
		
		
		
		
   		
   		
   		
   		// Specify button locations
		btn_xPos=10;
		btn_yPos=10;
		btn_width=150;
		btn_height=25;
		
   		
		jbtn_createNode.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jtbtn_range.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		
   		jbtn_simSetup.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jbtn_genTplgy.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;

   		jtbtn_data.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		jcbx_selNode.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		jbtn_monitorNode.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		
   		jbtn_rreq.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		jbtn_treePrune.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		
   		jtbtn_blkHole.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);			btn_yPos+=btn_height+5;
   		 		
   		jbtn_exit.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_yPos+=btn_height+5;
   		
   		
   		btn_xPos=10;	
		btn_yPos=600;
		btn_width=50;
		btn_height=25;
		
   		jlb_posX.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				btn_xPos+=btn_width+20;
   		jlb_posY.setBounds(btn_xPos,btn_yPos,btn_width,btn_height);				
   		
   		
   		
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
   		this.add(jtbtn_range);
   		
   		this.add(jbtn_simSetup);
   		this.add(jbtn_genTplgy);
   		this.add(jtbtn_data);
   		
   		
   		this.add(jcbx_selNode);
   		this.add(jbtn_monitorNode);
   		this.add(jbtn_rreq);
   		this.add(jbtn_treePrune);
   		this.add(jtbtn_blkHole);
   		this.add(jbtn_exit);
   		
   		
   		this.add(jsldr_movNodX);
   		this.add(jsldr_movNodY);
   		
   		this.add(jlb_posX);
   		this.add(jlb_posY);
   		
	}
	
	
	public void showTopology(){
		Topology tp = new Topology();
		
		tplgyFrame=new JFrame("Network Topology");
		tplgyFrame.getContentPane().add(tp);
		tplgyFrame.setBackground(Color.white);
		tplgyFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		tplgyFrame.setBounds(210,10,950,650);
		tplgyFrame.setResizable(false);
		tplgyFrame.setVisible(true);
	}
	


	public static void main(String[] args) {
		//new SplashScreen();

		mainFrame = new Main();
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/livewire2.gif"));
		mainFrame.setTitle("BSMR in Multihop Wireless Networks");
		mainFrame.setBounds(5, 5, 200, 700); 	// mainFrame.setBounds(50,20,1200,800);

		UIManager.LookAndFeelInfo lookAndFeels[] = UIManager.getInstalledLookAndFeels();

		try {
			UIManager.setLookAndFeel(lookAndFeels[1].getClassName());
			SwingUtilities.updateComponentTreeUI(mainFrame);
		} catch (Exception e) {	}

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
						System.out.println(k+" Trying to place nodes more closely..");
						placeNodesCloser(k);
						break;
					}
					
				}
				


		}
	  
	
	  public void placeNodesCloser(int p){
		  int repeat=0;
			
		  int i=0;
		  int j=0;
		  int midX = (int)(xPosArray.length)/2;
		  int midY = (int)(yPosArray.length)/2;

		  
		  i = midX;
		  j = midY;
			 
		  for(int k=p;k<Node.nwSize;){
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
				 else{
					 canPlace = true;
				 }
				 
				if(canPlace){
					placedPos[i][j]=1;
					repeat=0;
					n1 = new Node(NodeType.NON_MEMBER_NODE,xPosArray[i],yPosArray[j]);
				
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
		int i=0,j=0;
		while(true){
			boolean canPlace = false;	
			//Generate Random indexes i and j
			 i = Main.random(xPosArray.length);
			 j = Main.random(yPosArray.length);

			 
			 // check if already placed
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
				break;
			}
			
				
		}


		for(int i1=0;i1<Node.totalNodes;i1++){
			NodeTable.getNode(i1+1).findReachableNodes();
		}

		
    }


	
	if (ae.getSource() == jbtn_simSetup) {
		jbtn_simSetup.setEnabled(false);
		SimSetup simSetup = new SimSetup();
		//jbtn_startSim.setEnabled(true);
	}
	
	if (ae.getSource() == jbtn_genTplgy) {
		jbtn_genTplgy.setEnabled(false);
		
		genInitialTopology();
			
		startThread(grpHelloThread, 9000);
		startThread(rchbltyThread, 9000);

		jtbtn_data.setEnabled(true);
		//jbtn_startData.setEnabled(true);
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
	    	  jbtn_treePrune.setEnabled(false);
	    	  this.jtbtn_blkHole.setEnabled(false);
			}
	      else{
	    	  jbtn_monitorNode.setForeground(Color.white);
	    	  jbtn_monitorNode.setEnabled(true);
	    	  
	    	  
	    	 
    		  
    		  if(!NodeTable.getNode(indx).Connected){
    			  jbtn_rreq.setForeground(Color.white);
        		  jbtn_rreq.setEnabled(true);
        		  jbtn_treePrune.setEnabled(false);
    		  }
    		  else{
    			  if(NodeTable.getNode(indx).getNodeType()==NodeType.TREE_NODE){
    				  jbtn_rreq.setForeground(Color.white);
            		  jbtn_rreq.setEnabled(true);
            		  jbtn_treePrune.setEnabled(false);
    			  }
    			  else{
    				  jbtn_rreq.setForeground(Color.gray);
    				  jbtn_rreq.setEnabled(false);
    			  }
    			  
    			  if(chkCanPrune(indx)){
    				  jbtn_treePrune.setEnabled(true);
    			  }
    			  else{
    				  jbtn_treePrune.setEnabled(false);
    			  }
    		  }
    		  

	    	  this.jtbtn_blkHole.setEnabled(true); 
    		  
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
		
	    if(!NodeTable.getNode(indx).Connected){
	    	NodeTable.getNode(indx).attempts =0;
			NodeTable.getNode(indx).initiateRouteDiscovery();
	    }
	    else if(NodeTable.getNode(indx).getNodeType()==NodeType.TREE_NODE){
	    	NodeTable.getNode(indx).setNodeType(NodeType.MEMBER_NODE);
	    }
	    jbtn_rreq.setEnabled(false);
    }
	
	
	if(ae.getSource()==jbtn_treePrune){
		int indx=(Integer)jcbx_selNode.getSelectedIndex();
		NodeTable.getNode(indx).createSignSend(PacketType.TREE_PRUNE); 
		
		NodeTable.getNode(indx).stopThread(NodeTable.getNode(indx).beaconThread);
		NodeTable.getNode(indx).stopThread(NodeTable.getNode(indx).StickToMe_Timer);
		
		
		NodeTable.getNode(indx).setNodeType(NodeType.NON_MEMBER_NODE);
		NodeTable.getNode(indx).Connected=false;
		jbtn_treePrune.setEnabled(false);
	}
	
	
	if(ae.getSource()==mi_help)
	{
		
		//this.generateValidPositions();		// No need in the final project code
		
	}
	
	
	if(ae.getSource()==mi_aboutUs){
		
		//startThread(mRatePktThread,500);
		//genMRate();
		NodeTable.getNode(1).createSignSend(PacketType.MRATE);
		
		
		/*		// remove comment later
		if(this.abtUsFrame!=null){
			this.abtUsFrame.dispose();
		}
		this.abtUsFrame = new AboutUs();
		*/
	}
	
	
	
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
		
		if (ie.getSource() == jtbtn_data) {
			if (jtbtn_data.isSelected()) {
				dataPktThread = new Timer("dataPktThread");
				startThread(dataPktThread,2 * 1000);
				jtbtn_data.setText("Stop Sending Data");
			} else {
				stopThread(dataPktThread);	
				jtbtn_data.setText("Start Sending Data");
			}
		}
		
		if (ie.getSource() == jtbtn_blkHole) {
			if (jtbtn_blkHole.isSelected()) {
				int indx=(Integer)jcbx_selNode.getSelectedIndex();
				NodeTable.getNode(indx).setAttackType(AttackType.BLACK_HOLE);
				jtbtn_blkHole.setText("Undo Black Hole");
				
			} else {
				int indx=(Integer)jcbx_selNode.getSelectedIndex();
				NodeTable.getNode(indx).setAttackType(AttackType.NONE);
				jtbtn_blkHole.setText("Make it Black Hole");
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
	
	
	public boolean chkCanPrune(int index){
		// check if it can prune itself
		boolean flag=false;
		
		Node tmpNode = NodeTable.getNode(index);
		
		if(tmpNode.Connected){
			if(tmpNode.type==NodeType.SOURCE){
				flag=false;
			}
			else if(tmpNode.type==NodeType.MEMBER_NODE || tmpNode.type==NodeType.TREE_NODE){
				if(tmpNode.downStreamNodes.size()==0){
					flag=true;
				}
				else{
					flag=false;
				}
			}
		}
		else{
			flag=false;
		}
		
		return flag;
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