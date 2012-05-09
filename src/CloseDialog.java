import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class CloseDialog extends JDialog implements ActionListener {
	JButton btn_yes = new JButton("Yes");
	JButton btn_no = new JButton("No");

	public CloseDialog(JFrame parent, String title, String message) {
		super(parent, title, true);

		setLocation(450, 200);
		JPanel messagePane = new JPanel();
		messagePane.add(new JLabel(message));
		getContentPane().add(messagePane);
		JPanel buttonPane = new JPanel();

		buttonPane.add(btn_no);
		btn_no.addActionListener(this);

		buttonPane.add(btn_yes);
		btn_yes.addActionListener(this);

		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == btn_yes) {
			System.exit(0);
		} else if (ae.getSource() == btn_no) {
			dispose();
		}
	}
}