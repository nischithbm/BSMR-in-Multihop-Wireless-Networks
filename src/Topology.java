import static java.awt.geom.AffineTransform.getRotateInstance;
import static java.awt.geom.AffineTransform.getTranslateInstance;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import node_pkg.AttackType;
import node_pkg.Node;
import node_pkg.NodeTable;
import node_pkg.NodeType;


public class Topology extends JPanel{


	Topology(){
		// default constructor
	}
	
	
	public void paint(Graphics g) {
		Node tmpPrevNode, tmpNode;
		Graphics2D g2d = (Graphics2D) g;

		// Paint background color white
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		g2d.setColor(Color.lightGray);
		int rangeWidth = 2 * Node.tRange;
		int nodeWidth = Node.nodeFillRadious;

		for (int i = 0; i < Node.totalNodes; i++) {

			tmpNode = NodeTable.getNode(i + 1);
			g2d.setBackground(Color.white);
				 
			 // g2d.drawLine(100, 80, 280, 100);
			  
			  		g2d.setColor(Color.lightGray);
			  		// show ranges
			  		if(Node.showRange){
			  			g2d.drawOval(tmpNode.getPosX()-(rangeWidth/2), tmpNode.getPosY()-(rangeWidth/2), rangeWidth, rangeWidth);
			  			
			  		}
			  		
			  		
			  		g2d.setColor(Color.black);
			  		
			  		switch(tmpNode.getNodeType()){
			  		case SOURCE:
			  			g2d.setColor(Color.red);
			  			break;
			  		case MEMBER_NODE:
			  			g2d.setColor(Color.blue);
			  			break;
			  		case TREE_NODE:
			  			g2d.setColor(Color.black);
			  			break;
			  		case NON_MEMBER_NODE:
			  			g2d.setColor(Color.gray);
			  			break;
			  		default:break;
			  		}
			  		
			  		
			  		
			  		
			  	//	g2d.drawString("(" + tmpNode.getPosX() +","+tmpNode.getPosY()+")", tmpNode.getPosX() - 35 , tmpNode.getPosY() + 25);
			  		
			  		if(tmpNode.attackType==AttackType.NONE){
			  			g2d.drawOval(tmpNode.getPosX()-(nodeWidth/2), tmpNode.getPosY()-(nodeWidth/2), nodeWidth, nodeWidth);
				  		g2d.drawString(""+tmpNode.getIpAddress(), tmpNode.getPosX() - 10 , tmpNode.getPosY() - 10);
				  		
				  		switch(tmpNode.getNodeType()){
				  		case SOURCE:
				  		case MEMBER_NODE:
				  		case TREE_NODE:
				  			g2d.fillOval(tmpNode.getPosX()-((nodeWidth)/2), tmpNode.getPosY()-((nodeWidth)/2), nodeWidth, nodeWidth);
				  			break;
				  		case NON_MEMBER_NODE:
				  			break;
				  		default:break;
				  		}
			  		}
			  		else if(tmpNode.attackType==AttackType.BLACK_HOLE){
				  		g2d.drawOval(tmpNode.getPosX()-( (nodeWidth+2)/2), tmpNode.getPosY()-( (nodeWidth+2)/2), nodeWidth+2, nodeWidth+2);
			  			g2d.drawOval(tmpNode.getPosX()-((nodeWidth-2)/2), tmpNode.getPosY()-((nodeWidth-2)/2), nodeWidth-2, nodeWidth-2);
				  		g2d.drawString(""+tmpNode.getIpAddress(), tmpNode.getPosX() - 10 , tmpNode.getPosY() - 10);
				  		g2d.fillOval(tmpNode.getPosX()-((nodeWidth-2)/2), tmpNode.getPosY()-((nodeWidth-2)/2), nodeWidth-2, nodeWidth-2);
			  			
			  		}
			  		
			  		if(tmpNode.receiving>0){
			  			g2d.setColor(Color.white);
			  			g2d.setColor(Color.green);
			  			g2d.drawOval(tmpNode.getPosX()-(nodeWidth/4), tmpNode.getPosY()-(nodeWidth/4), nodeWidth/2, nodeWidth/2);
				  		g2d.fillOval(tmpNode.getPosX()-(nodeWidth/4), tmpNode.getPosY()-(nodeWidth/4), nodeWidth/2, nodeWidth/2);
				  		tmpNode.receiving--;
				  		
			  		}
			  	
			  		
			  		if(tmpNode.dropping>0){
			  			tmpNode.dropping--;
			  			if(tmpNode.dropping<3){
			  				g2d.setColor(Color.red);
					  		g2d.drawLine(tmpNode.getPosX()-(nodeWidth/2)-20, tmpNode.getPosY()-(nodeWidth/2), tmpNode.getPosX()+(nodeWidth/2)-20, tmpNode.getPosY()+(nodeWidth/2));
					  		g2d.drawLine(tmpNode.getPosX()-(nodeWidth/2)-20, tmpNode.getPosY()+(nodeWidth/2), tmpNode.getPosX()+(nodeWidth/2)-20, tmpNode.getPosY()-(nodeWidth/2));
			  			}
				  		if(tmpNode.dropping==0){
				  			g2d.setColor(Color.white);
					  		g2d.drawLine(tmpNode.getPosX()-(nodeWidth/2)-20, tmpNode.getPosY()-(nodeWidth/2), tmpNode.getPosX()+(nodeWidth/2)-20, tmpNode.getPosY()+(nodeWidth/2));
					  		g2d.drawLine(tmpNode.getPosX()-(nodeWidth/2)-20, tmpNode.getPosY()+(nodeWidth/2), tmpNode.getPosX()+(nodeWidth/2)-20, tmpNode.getPosY()-(nodeWidth/2));
				  		}
			  		}
			  	
			  		
			  		if(tmpNode.sending>0){
			  			g2d.setColor(Color.blue);
			  			if(tmpNode.sending%5 == 0){
				  			g2d.drawArc(tmpNode.getPosX()-((nodeWidth+11)/2), tmpNode.getPosY()-((nodeWidth+11)/2), nodeWidth+11, nodeWidth+11, 30, 50);
				  			g2d.drawArc(tmpNode.getPosX()-((nodeWidth+10)/2), tmpNode.getPosY()-((nodeWidth+10)/2), nodeWidth+10, nodeWidth+10, 30, 50);
				  			
				  			//g2d.drawArc(tmpNode.getPosX()-((nodeWidth+10)/2), tmpNode.getPosY()-((nodeWidth+10)/2), nodeWidth+10, nodeWidth+10, 30, 50);
				  		}
				  		else if(tmpNode.sending%5 == 2){
			  				
				  			g2d.drawArc(tmpNode.getPosX()-((nodeWidth+21)/2), tmpNode.getPosY()-((nodeWidth+21)/2), nodeWidth+21, nodeWidth+21, 20, 60);
				  			g2d.drawArc(tmpNode.getPosX()-((nodeWidth+20)/2), tmpNode.getPosY()-((nodeWidth+20)/2), nodeWidth+20, nodeWidth+20, 20, 60);
				  		}
				  		else if(tmpNode.sending%5 == 3){
			  				
				  			g2d.drawArc(tmpNode.getPosX()-((nodeWidth+31)/2), tmpNode.getPosY()-((nodeWidth+31)/2), nodeWidth+31, nodeWidth+31, 10, 70);
				  			g2d.drawArc(tmpNode.getPosX()-((nodeWidth+30)/2), tmpNode.getPosY()-((nodeWidth+30)/2), nodeWidth+30, nodeWidth+30, 10, 70);
				  		}
				  		tmpNode.sending--;
				  		if(tmpNode.sending==0){
				  			g2d.setColor(Color.white);
				  			g2d.drawArc(tmpNode.getPosX()-((nodeWidth+10)/2), tmpNode.getPosY()-((nodeWidth+10)/2), nodeWidth+10, nodeWidth+10, 30, 30);
			  				g2d.drawArc(tmpNode.getPosX()-((nodeWidth+20)/2), tmpNode.getPosY()-((nodeWidth+20)/2), nodeWidth+20, nodeWidth+20, 20, 50);
			  				g2d.drawArc(tmpNode.getPosX()-((nodeWidth+30)/2), tmpNode.getPosY()-((nodeWidth+30)/2), nodeWidth+30, nodeWidth+30, 10, 70);
				  		}
			  		}
			  		
			  		
			  		
			  		showConnectivity(g);
			  		
			  	
		  }
		  
		 
		displayInfo(g);
		  
		  
	}

	
	// Generic function to draw a Inclined Arrow with Solid Filled Arrow Head
	void showConnectivity(Graphics g1) {
		
	    Graphics2D g2d = (Graphics2D) g1.create();
	    g2d.setColor(Color.black);
	    for(int i=0;i<Node.totalNodes;i++){
	    	Node tmp = NodeTable.getNode(i+1);
	    	
	    	for(Iterator<Integer> itr=tmp.downStreamNodes.iterator();itr.hasNext();){
	    		
	    		String val = itr.next().toString();
	    		int ipAddr = Integer.parseInt(val);
	    		if(tmp.isReachable(ipAddr)){
		    		//System.out.println(itr.next());
		    		g2d.drawLine(tmp.getPosX(), tmp.getPosY(), NodeTable.getNode(ipAddr).getPosX(), NodeTable.getNode(ipAddr).getPosY());
	    		}
	    		else{
	    			// show link breakage
	    			int baseWidth = Math.abs(tmp.getPosX() - NodeTable.getNode(ipAddr).getPosX());
		    		int oppSide = Math.abs(tmp.getPosY() - NodeTable.getNode(ipAddr).getPosY());
		    		
		    		int xPad = baseWidth/4;
		    		int yPad = oppSide/4;
		    		
		    		if(tmp.getPosX() > NodeTable.getNode(ipAddr).getPosX()){
		    			xPad = -xPad;
		    		}
		    		if(tmp.getPosY() > NodeTable.getNode(ipAddr).getPosY()){
		    			yPad = -yPad;
		    		}
		    		
	    			g2d.drawLine(tmp.getPosX(), tmp.getPosY(),tmp.getPosX()+xPad, tmp.getPosY()+yPad);
	    			g2d.drawLine(NodeTable.getNode(ipAddr).getPosX(), NodeTable.getNode(ipAddr).getPosY(),NodeTable.getNode(ipAddr).getPosX()-xPad, NodeTable.getNode(ipAddr).getPosY()-yPad);
		    			
	    		}


	         }
	    }
	    
	}

	
	
	void displayInfo(Graphics g1){
		// Display information
		 Graphics2D g2d = (Graphics2D) g1.create();
		 int nodeWidth = Node.nodeFillRadious;

		  g2d.setColor(Color.black);
		  g2d.drawLine(this.getWidth()-215,0, this.getWidth()-215, this.getHeight());
		  
		 
		  int dyPos = 40;
		  
		  
		  // Node Types
		  g2d.drawLine(this.getWidth()-215,dyPos-3, this.getWidth(), dyPos-3);
		  g2d.setFont(new Font(null,Font.BOLD,16));
		  g2d.drawString("Node Type", this.getWidth()-210+20 + nodeWidth+10 , dyPos+nodeWidth);
		  g2d.drawLine(this.getWidth()-215,dyPos+25, this.getWidth(), dyPos+25);
		  
		  
		  
		  g2d.setFont(new Font(null,Font.BOLD,14));
		  
		  dyPos+=35;
		  g2d.setColor(Color.red);
		  g2d.drawOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.fillOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.setColor(Color.black);
		  g2d.drawString("Source Node", this.getWidth()-210+20 + nodeWidth+25 , dyPos+nodeWidth);
		  
		  dyPos+=30;
		  g2d.setColor(Color.gray);
		  g2d.drawOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.setColor(Color.black);
		  g2d.drawString("Non Member Node", this.getWidth()-210+20 + nodeWidth+25 , dyPos+nodeWidth);
		  
		  dyPos+=30;
		  g2d.setColor(Color.black);
		  g2d.drawOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.fillOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.setColor(Color.black);
		  g2d.drawString("Tree Node", this.getWidth()-210+20 + nodeWidth+25 , dyPos+nodeWidth);
		  
		  dyPos+=30;
		  g2d.setColor(Color.blue);
		  g2d.drawOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.fillOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.setColor(Color.black);
		  g2d.drawString("Member Node", this.getWidth()-210+20 + nodeWidth+25 , dyPos+nodeWidth);
		  
		  g2d.drawLine(this.getWidth()-215,dyPos+25, this.getWidth(), dyPos+25);
		  // Node Types End
		
		  
		  
		 
		  
		  
		  g2d.setFont(new Font(null,Font.BOLD,14));

		  
		  
		  dyPos+=80;
		  g2d.setColor(Color.gray);
		  g2d.drawOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  
		  g2d.setColor(Color.blue);
		  g2d.drawArc(this.getWidth()-210+20 -11/2, dyPos-11/2, nodeWidth+11, nodeWidth+11, 30, 50);
		  g2d.drawArc(this.getWidth()-210+20 -10/2, dyPos-10/2, nodeWidth+10, nodeWidth+10, 30, 50);
		  
		  g2d.drawArc(this.getWidth()-210+20 -21/2, dyPos-21/2, nodeWidth+21, nodeWidth+21, 20, 60);
		  g2d.drawArc(this.getWidth()-210+20 -20/2, dyPos-20/2, nodeWidth+20, nodeWidth+20, 20, 60);
		  
		  g2d.drawArc(this.getWidth()-210+20 -31/2, dyPos-31/2, nodeWidth+31, nodeWidth+31, 10, 70);
		  g2d.drawArc(this.getWidth()-210+20 -30/2, dyPos-30/2, nodeWidth+30, nodeWidth+30, 10, 70);
		  
		  g2d.setColor(Color.black);
		  g2d.drawString("Transmitting packet", this.getWidth()-210+20 + nodeWidth+25 , dyPos+nodeWidth);
		  
		  
		  
		  dyPos+=30;
		  g2d.setColor(Color.gray);
		  g2d.drawOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.setColor(Color.green);
		  g2d.drawOval(this.getWidth()-210+20+4, dyPos+4, nodeWidth/2, nodeWidth/2);
		  g2d.fillOval(this.getWidth()-210+20+4, dyPos+4, nodeWidth/2, nodeWidth/2);
		  
		  g2d.setColor(Color.black);
		  g2d.drawString("Received packet", this.getWidth()-210+20 + nodeWidth+25 , dyPos+nodeWidth);
		  
		  
		  
		  dyPos+=30;
		  g2d.setColor(Color.gray);
		  g2d.drawOval(this.getWidth()-210+20, dyPos, nodeWidth, nodeWidth);
		  g2d.setColor(Color.red);
		  g2d.drawLine(this.getWidth()-210, dyPos, this.getWidth()-210+nodeWidth, dyPos+nodeWidth);
		  g2d.drawLine(this.getWidth()-210, dyPos+nodeWidth, this.getWidth()-210+nodeWidth, dyPos);
		  
		 
		  g2d.setColor(Color.black);
		  g2d.drawString("Dropping packet", this.getWidth()-210+20 + nodeWidth+25 , dyPos+nodeWidth);
		  
		  g2d.drawLine(this.getWidth()-215,dyPos+25, this.getWidth(), dyPos+25);
		  
		  
		  


		  
		  dyPos+=70;
		  
			
		  g2d.setFont(new Font(null,Font.BOLD,14));
		  
		  dyPos+=35;
		  g2d.setColor(Color.black);
		  g2d.drawOval(this.getWidth()-210+20-1, dyPos-1, nodeWidth+2, nodeWidth+2);
		  g2d.drawOval(this.getWidth()-210+20+1, dyPos+1, nodeWidth-2, nodeWidth-2);
		  g2d.fillOval(this.getWidth()-210+20+1, dyPos+1, nodeWidth-2, nodeWidth-2);
		  g2d.setColor(Color.black);
		  g2d.drawString("Black Hole", this.getWidth()-210+20 + nodeWidth+20 , dyPos+nodeWidth);
		  
		  g2d.drawLine(this.getWidth()-215,dyPos+25, this.getWidth(), dyPos+25);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	// Generic function to draw a Inclined Arrow with Solid Filled Arrow Head
	void drawArrow(Graphics g1, int x1, int y1, int x2, int y2,Color clr,int arr_siz) {
		int ARR_SIZE = arr_siz;		// size of the arrow head
		
	    Graphics2D g2d = (Graphics2D) g1.create();

	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx*dx + dy*dy);
	    
	    
	    AffineTransform at = getTranslateInstance(x1, y1);
	    at.concatenate(getRotateInstance(angle));
	    g2d.setTransform(at);

	    g2d.setColor(clr);
	    // Draw horizontal arrow starting in (0, 0)
	    g2d.drawLine(0, 0, (int) len, 0);
	    g2d.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
	                  new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
	}

	
	
}
