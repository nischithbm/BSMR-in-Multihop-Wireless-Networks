
import java.awt.Color;

import javax.swing.*;

public class SplashScreen extends JWindow{
	
	JLabel jlb_bg = new JLabel("",new ImageIcon("images/Splash-screen.png"),JLabel.HORIZONTAL);
	JLabel jlb_loader = new JLabel("Loading...",new ImageIcon("images/ajax-loader.gif"),JLabel.HORIZONTAL);

	SplashScreen(){
		this.setBounds(300, 100, 768, 480);
		this.setLayout(null);
		this.setVisible(true);
		
		jlb_bg.setBounds(0, 0, 768, 480);
		jlb_loader.setForeground(Color.white);
		jlb_loader.setBounds(580, 430,150, 32);
		
		this.add(jlb_loader);
		this.add(jlb_bg);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		
		this.setVisible(false);
	
	}
}
