
import java.awt.*;

import javax.swing.*;

class AboutUs extends JFrame {
	
	Container c;
	JLabel jlb_cptn_author,jlb_cptn_guide,jlb_cptn_college;
	JLabel jlb_title1,jlb_title2,jlb_name1,jlb_name2,jlb_name3,jlb_usn1,jlb_usn2,jlb_usn31,jlb_guide,jlb_dept,jlb_college;
	
	JLabel jlb_spathLogo;
	ImageIcon ii_sapthLogo;

	AboutUs(){
		
		 this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/livewire2.gif"));
		 this.setTitle ("About Us");
		 this.setBounds(500,100,630, 500);	
		 
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
		c.setBackground(Color.white);
		c.setLayout(null);
		
		
		
		jlb_title1 = new JLabel("Byzantine-Resilient Secure Multicast Routing in");
		jlb_title1.setFont(new Font(null,Font.BOLD,26));
		jlb_title1.setBounds(10,20,600,40);
		
		jlb_title2 = new JLabel("Multihop Wireless Networks");
		jlb_title2.setFont(new Font(null,Font.BOLD,26));
		jlb_title2.setBounds(110,60,350,40);
		
		
		c.add(jlb_title1);
		c.add(jlb_title2);
		
		
		ii_sapthLogo = new ImageIcon("images/sapthagiriLogo.png");
		jlb_spathLogo = new JLabel("",ii_sapthLogo,JLabel.HORIZONTAL);
		jlb_spathLogo.setBounds(10, 180,200,200);
		jlb_spathLogo.setVerticalTextPosition(JLabel.BOTTOM);
		jlb_spathLogo.setHorizontalTextPosition(JLabel.CENTER);
		c.add(jlb_spathLogo);
	}
	
}