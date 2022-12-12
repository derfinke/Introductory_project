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
	private JButton sa_RESET         = new JButton("RESET");
	private JButton sa_Door_OPEN     = new JButton("OPEN");
	private JButton sa_Door_CLOSE    = new JButton("CLOSE");
	private JButton sa_Motor_UP_V1   = new JButton("UP V1");
	private JButton sa_Motor_UP_V2   = new JButton("UP V2");
	private JButton sa_Motor_DOWN_V1 = new JButton("DOWN V1");
	private JButton sa_Motor_DOWN_V2 = new JButton("DOWN V2");
	
	/*
	 * define gui labels
	 */
	private JLabel  sa_Door_LABEL  = new JLabel("Door Control");
	private JLabel  sa_Motor_LABEL = new JLabel("Motor Control");

	
	/*
	 * create gui
	 */
public GUI() {

	setTitle("Elevator HMI Group D");
    setSize(800,300);
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
	// Reset
	sa_RESET.setBounds(630,200, 100,40);
	add(sa_RESET);

	// Door
	sa_Door_LABEL.setBounds(10,10, 200,40);
	add(sa_Door_LABEL);

	sa_Door_OPEN.setBounds(110,60, 100,40);
	add(sa_Door_OPEN);

	sa_Door_CLOSE.setBounds(10,60, 100,40);
	add(sa_Door_CLOSE);

	// Motor 
	sa_Motor_LABEL.setBounds(330,10, 200,40);
	add(sa_Motor_LABEL);

	// Motor UP
	sa_Motor_UP_V1.setBounds(330,60, 100,40);
	add(sa_Motor_UP_V1);
	
	sa_Motor_UP_V2.setBounds(430,60, 100,40);
	add(sa_Motor_UP_V2);

	// Motor Down
	sa_Motor_DOWN_V1.setBounds(530,60, 100,40);
	add(sa_Motor_DOWN_V1);

	sa_Motor_DOWN_V2.setBounds(630,60, 100,40);
	add(sa_Motor_DOWN_V2);

}

/*
 * create event handler
 */
private void sa_constraints_ctrl_handler() {

	// event close the window
	this.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.exit(1);
		}
	});
	// event click to RESET the simulation
	sa_RESET.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// TO-DO create function or hook to reset the simuation
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
	// event click to send the elevator upwards with v1
	sa_Motor_UP_V1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// TO-DO create function or hook to send the elevator upwards with v1
		}
	});
	// event click to send the elevator upwards with v2
	sa_Motor_UP_V2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// TO-DO: create function or hook to send the elevator upwards with v2
		}
	});
	// event click to send the elevator downwards with v1
	sa_Motor_DOWN_V1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// TO-DO: create function or hook to send the elevator downwards with v1
		}
	});
	// event click to send the elevator downwards with v2
	sa_Motor_DOWN_V2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// TO-DO: create function or hook to send the elevator downwards with v2
		}
	});
}

}

