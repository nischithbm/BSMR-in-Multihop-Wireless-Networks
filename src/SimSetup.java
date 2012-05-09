
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import node_pkg.Node;
import node_pkg.NodeTable;


class SimSetup extends JFrame implements ActionListener, ItemListener{
	
	Container c;
	
	//JButton jbtn_nwSize,jbtn_grpSize,jbtn_trRange;
	JButton jbtn_cancel,jbtn_apply;

	
	
	int fontSize;
	int xPos,yPos,width,height;
	
	JLabel jlb_simSetup1,jlb_simSetup2;
	
	JLabel jlb_gif1,jlb_gif2;
	ImageIcon ii_gif;

	JLabel jlb_nwSize,jlb_grpSize,jlb_trRange,jlb_advSize;
	JTextField jtxt_nwSize,jtxt_grpSize,jtxt_trRange;

	JComboBox jcb_advSize;

	
    
	SimSetup(){
		//NodeTable.nodeTable.clear();
		
		windowSettings();
		
		addControls();
	}

	public void windowSettings(){
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/livewire2.gif"));
		this.setTitle ("Simulation Setup");
		this.setBounds(300,100,400, 500);	
		
		UIManager.LookAndFeelInfo lookAndFeels[] = UIManager.getInstalledLookAndFeels();
		try{
			UIManager.setLookAndFeel(lookAndFeels[1].getClassName());
			SwingUtilities.updateComponentTreeUI(this);
		}
	    catch(Exception e){System.out.println("error in look and feel"+e);}
	    
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    this.setVisible(true);
		
		c=this.getContentPane();
		c.setBackground(Color.black);
		c.setLayout(null);
	}
	
	public void addControls(){
		JLabel jlb_bg = new JLabel("",new ImageIcon("images/simSetup.png"),JLabel.HORIZONTAL);
		jlb_bg.setBounds(0, 0,412,480);
		
		jlb_simSetup1 = new JLabel("Simulation");
		jlb_simSetup2 = new JLabel("Setup");
		
		ii_gif = new ImageIcon("images/aa.gif");
		
		jlb_gif1 = new JLabel("",ii_gif,JLabel.HORIZONTAL);
		jlb_gif2 = new JLabel("",ii_gif,JLabel.HORIZONTAL);
		
		
		jlb_nwSize = new JLabel("Network Size");
		jlb_grpSize = new JLabel("Group Size");
		jlb_trRange = new JLabel("Transmission Range");
		jlb_advSize = new JLabel("Adversarial Node Size");
		
		
		jtxt_nwSize = new JTextField();
		jtxt_grpSize = new JTextField();
		jtxt_trRange = new JTextField();
		
		
		jcb_advSize=new JComboBox();
		
		
		jbtn_cancel=new JButton("Cancel");
		jbtn_apply=new JButton("Apply");
		
		
		
		// Settings
		
		jlb_simSetup1.setForeground(Color.white);
		jlb_simSetup1.setFont(new Font(null,Font.BOLD,35));
		
		jlb_simSetup2.setForeground(Color.white);
		jlb_simSetup2.setFont(new Font(null,Font.BOLD,35));
		
		
		jlb_gif1.setVerticalTextPosition(JLabel.BOTTOM);
		jlb_gif1.setHorizontalTextPosition(JLabel.CENTER);
		
		jlb_gif2.setVerticalTextPosition(JLabel.BOTTOM);
		jlb_gif2.setHorizontalTextPosition(JLabel.CENTER);

		
		// Labels
		
		
		fontSize = 16;
		
		jlb_nwSize.setForeground(Color.white);
		jlb_grpSize.setForeground(Color.white);
		jlb_trRange.setForeground(Color.white);
		jlb_advSize.setForeground(Color.white);
		jlb_nwSize.setFont(new Font(null,Font.BOLD,fontSize));
		jlb_grpSize.setFont(new Font(null,Font.BOLD,fontSize));
		jlb_trRange.setFont(new Font(null,Font.BOLD,fontSize));
		jlb_advSize.setFont(new Font(null,Font.BOLD,fontSize));
		
		
		
		jtxt_nwSize.setDocument(new IntegerDocument());
		jtxt_grpSize.setDocument(new IntegerDocument());
		jtxt_trRange.setDocument(new IntegerDocument());
		

		
		jcb_advSize.setFont(new Font(null,Font.BOLD,fontSize));
		jcb_advSize.addItem("Select");
		jcb_advSize.setEnabled(true);
		jcb_advSize.addItem(1);
		jcb_advSize.addActionListener(this);
	   	
		
		
		
		jbtn_cancel.setForeground(Color.red);
		jbtn_cancel.setFont(new Font(null,Font.BOLD,fontSize));
		jbtn_cancel.addActionListener(this);
		

		jbtn_apply.setForeground(new Color(0,100,0));
		jbtn_apply.setFont(new Font(null,Font.BOLD,fontSize));
		jbtn_apply.addActionListener(this);
		
		
		
		

		
	   	
        
        
		// Set Locations
        jlb_simSetup1.setBounds(110,15,175,35);
		jlb_simSetup2.setBounds(140,55,140,35);

		jlb_gif1.setBounds(0, 0,100,100);
		jlb_gif2.setBounds(295, 0,100,100);
		
		
		xPos=40;	
		yPos=140;
		width=170;
		height=25;
		
		jlb_nwSize.setBounds(xPos,yPos,width,height);		yPos+=height+10;
		jlb_grpSize.setBounds(xPos,yPos,width,height);		yPos+=height+10;
		jlb_trRange.setBounds(xPos,yPos,width,height);		yPos+=height+10;
		jlb_advSize.setBounds(xPos,yPos,width,height);		yPos+=height+10;
		
	
		xPos=250;	
		yPos=140;
		width=100;
		height=25;
		
		jtxt_nwSize.setBounds(xPos,yPos,width,height);		yPos+=height+10;
		jtxt_grpSize.setBounds(xPos,yPos,width,height);		yPos+=height+10;
		jtxt_trRange.setBounds(xPos,yPos,width,height);		yPos+=height+10;
		jcb_advSize.setBounds(xPos,yPos,width,height);		yPos+=height+10;

		



		



		
		
		
		
		
		xPos=40;	
		yPos=400;
		width=140;
		height=30;
		
		jbtn_cancel.setBounds(xPos,yPos,width,height);		xPos+=width+30;
	   	jbtn_apply.setBounds(xPos,yPos,width,height);		
	   		
	   	
		
	   	
	   		


	   	// Add buttons to the frame

		
		this.add(jlb_simSetup1);
		this.add(jlb_simSetup2);
		
		this.add(jlb_gif1);
		this.add(jlb_gif2);
		
		this.add(jlb_nwSize);
		//this.add(jlb_grpSize);
		this.add(jlb_trRange);
		//this.add(jlb_advSize);
		
		this.add(jtxt_nwSize);
		//this.add(jtxt_grpSize);
		this.add(jtxt_trRange);
		//this.add(jcb_advSize);
		

		
		this.add(jbtn_cancel);
		this.add(jbtn_apply);
		
		this.add(jlb_bg);
		
		//jtxt_grpSize.setEnabled(false);
		//jtxt_trRange.setEnabled(false);
		//jcb_advSize.setEnabled(false);
		
	}
	
	
	
	
	// Event Handlers
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		
		if(ae.getSource()==jbtn_apply){
			String str ="";
			
			
			str = jtxt_nwSize.getText();
			if(str.equals("")){
				str = "20";
			}
			Node.nwSize = Integer.parseInt(str);
			
			
			str = jtxt_trRange.getText();
			if(str.equals("")){
				str = "20";
			}
				
			Node.tRange = Integer.parseInt(str);
			
			
			
			Main.mainFrame.jbtn_startSim.setEnabled(true);
			
			this.dispose();
	    }
		
		if(ae.getSource()==jbtn_cancel){
			Main.mainFrame.jbtn_simSetup.setEnabled(true);
			this.dispose();
	    }
		
	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*************************************************************************************************************
	 Text Box validation reference =>   http://awaiswaheed.wordpress.com/2011/10/18/input-text-validation-in-jtextbox/   
	 **************************************************************************************************************/
	class IntegerDocument extends PlainDocument {

        public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {

            if (string == null) {
                return;
            } else {
                String newValue;
                int length = getLength();
                if (length == 0) {
                    newValue = string;
                } else {
                    String currentContent = getText(0, length);
                    StringBuffer currentBuffer = new StringBuffer(currentContent);
                    currentBuffer.insert(offset, string);
                    newValue = currentBuffer.toString();
                }
                try {
                    checkInput(newValue);
                    super.insertString(offset, string, attributes);
                } catch (Exception exception) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }

        public void remove(int offset, int length)
                throws BadLocationException {

            int currentLength = getLength();
            String currentContent = getText(0,
                    currentLength);
            String before = currentContent.substring(
                    0, offset);
            String after = currentContent.substring(
                    length + offset, currentLength);
            String newValue = before + after;
            try {
                checkInput(newValue);
                super.remove(offset, length);
            } catch (Exception exception) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private int checkInput(String proposedValue)
                throws NumberFormatException {

            int newValue = 0;
            if (proposedValue.length() > 0) {
                newValue = Integer.parseInt(proposedValue);
            }
            return newValue;
        }
    }
	
	
}
