package intro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GUI extends JFrame {
	/*
	 * define control buttons
	 */
	private JButton sa_Door_OPEN   = new JButton("OPEN");
	private JButton sa_Door_CLOSE  = new JButton("CLOSE");
	
	/*
	 * define gui labels
	 */
	private JLabel  sa_Door_LABEL  = new JLabel("Door Control");
	
	/*
	 * create gui
	 */
public GUI() {

	setTitle("Elevator HMI Group D");
    setSize(800,400);
    setLocationRelativeTo(null);
    setLayout(null);    
    setResizable(false);
    sa_add_constraints();
    sa_constraints_ctrl_handler();
	}

private void sa_add_constraints() {
	/*
	 * Label & button constraints
	 */
	sa_Door_LABEL.setBounds(10,10, 200,40);
	sa_Door_OPEN.setBounds(220,60, 200,40);
	sa_Door_CLOSE.setBounds(10,60, 200,40);
	
	/*
	 * add constraints to gui
	 */
	add(sa_Door_LABEL);
	add(sa_Door_OPEN);
	add(sa_Door_CLOSE);
}

/*
 * create constraints event handler
 */
private void sa_constraints_ctrl_handler() {
	
	// event close the window
	this.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.exit(1);
		}
	});
	// event click to open the door
	sa_Door_OPEN.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// TO-DO create function or hook to open the door
		}
	});
	// event click to close the door
	sa_Door_CLOSE.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// TO-DO: create function or hook to close the door
		}
	});
}

}

