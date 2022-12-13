package intro;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	private JLabel sa_Door_LABEL  = new JLabel("Door Control");
	private JLabel sa_Motor_LABEL = new JLabel("Motor Control");

	/*
	 * define circle labels
	 */
	private JLabel sa_Door_is_OPEN   = new JLabel("Door is open");
	private JLabel sa_Door_is_CLOSED = new JLabel("Door is closed");
	private JLabel sa_Motor_is_READY = new JLabel("Motor is ready");
	private JLabel sa_Motor_is_ON    = new JLabel("Motor is on");
	/*
	 * booleans represent the test flags for the status lights
	 * (GREEN/RED) Test/Change the color by click on the button DOOR Control: CLOSE,OPEN;
	 */
	private boolean sa_EMERGENCY_STOP_FLAG = false;
	private boolean sa_Door_is_OPEN_FLAG   = false;
	private boolean sa_Door_is_CLOSED_FLAG = false;
	private boolean sa_Motor_is_READY_FLAG = false;
	private boolean sa_Motor_is_ON_FLAG    = false;

	/*
	 * create gui
	 */
	public GUI() {

		setTitle("Elevator HMI Group D");
		setSize(750, 300);
		getContentPane().setBackground(Color.white);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		sa_add_constraints();
		sa_constraints_ctrl_handler();
	}

	/*
	 * Label & button & status constraints
	 */
	private void sa_add_constraints() {

		// Reset button
		sa_RESET.setBounds(610, 200, 100, 40);
		sa_RESET.setBackground(Color.LIGHT_GRAY);
		add(sa_RESET);

		// Door text label
		sa_Door_LABEL.setFont(new Font("Verdana", Font.PLAIN, 25));
		sa_Door_LABEL.setBounds(10, 10, 200, 40);
		add(sa_Door_LABEL);

		// Door button OPEN
		sa_Door_OPEN.setBackground(Color.LIGHT_GRAY);
		sa_Door_OPEN.setBounds(110, 60, 100, 40);
		add(sa_Door_OPEN);

		// Door button CLOSE
		sa_Door_CLOSE.setBackground(Color.LIGHT_GRAY);
		sa_Door_CLOSE.setBounds(10, 60, 100, 40);
		add(sa_Door_CLOSE);

		// Motor text Label
		sa_Motor_LABEL.setFont(new Font("Verdana", Font.PLAIN, 25));
		sa_Motor_LABEL.setBounds(330, 10, 200, 40);
		add(sa_Motor_LABEL);

		// Motor UP V1 button
		sa_Motor_UP_V1.setBackground(Color.LIGHT_GRAY);
		sa_Motor_UP_V1.setBounds(330, 60, 100, 40);
		add(sa_Motor_UP_V1);

		// Motor UP V2 button
		sa_Motor_UP_V2.setBackground(Color.LIGHT_GRAY);
		sa_Motor_UP_V2.setBounds(430, 60, 100, 40);
		add(sa_Motor_UP_V2);

		// Motor Down V1 button
		sa_Motor_DOWN_V1.setBackground(Color.LIGHT_GRAY);
		sa_Motor_DOWN_V1.setBounds(530, 60, 100, 40);
		add(sa_Motor_DOWN_V1);

		// Motor Down V2 button
		sa_Motor_DOWN_V2.setBackground(Color.LIGHT_GRAY);
		sa_Motor_DOWN_V2.setBounds(630, 60, 100, 40);
		add(sa_Motor_DOWN_V2);

		// Status text labels
		sa_Door_is_OPEN.setBounds(100, 130, 100, 40);
		add(sa_Door_is_OPEN);

		sa_Door_is_CLOSED.setBounds(100, 200, 100, 40);
		add(sa_Door_is_CLOSED);

		sa_Motor_is_READY.setBounds(410, 130, 100, 40);
		add(sa_Motor_is_READY);

		sa_Motor_is_ON.setBounds(410, 200, 100, 40);
		add(sa_Motor_is_ON);

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
				/*
				 *  TO-DO create function or hook to reset the simuation
				 */
				update_GUI_FLAGS();
			}
		});
		// event click to open the door
		sa_Door_OPEN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// poll,update registers before run action
				update_GUI_FLAGS();
				// if no crash on the end sensors, run your function... 
				if(!sa_EMERGENCY_STOP_FLAG) {
				/*
				 *  TO-DO create function or hook to open the door
				 *  changes in function update_GUI_FLAGS() are needed.
				 */
				}
				
				// delete from here... Just for testing the status lights, can be deleted
				sa_Door_is_OPEN_FLAG   = true;
				sa_Door_is_CLOSED_FLAG = true;
				sa_Motor_is_READY_FLAG = true;
				sa_Motor_is_ON_FLAG    = true;
				repaint();
				// to here
			}
		});
		// event click to close the door
		sa_Door_CLOSE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// poll,update registers before run action
				update_GUI_FLAGS();
				// if no crash on the end sensors, run your function... 
				if(!sa_EMERGENCY_STOP_FLAG) {
				/*
				 *  TO-DO: create function or hook to close the door
				 *  changes in function update_GUI_FLAGS() are needed.
				 */
				}
				// delete from here... Just for testing the status lights, can be deleted
				sa_Door_is_OPEN_FLAG   = false;
				sa_Door_is_CLOSED_FLAG = false;
				sa_Motor_is_READY_FLAG = false;
				sa_Motor_is_ON_FLAG    = false;
				repaint();
				// to here
			}
		});
		// event click to send the elevator upwards with v1
		sa_Motor_UP_V1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// poll,update registers before run action
				update_GUI_FLAGS();
				// if no crash on the end sensors, run your function...
				if (!sa_EMERGENCY_STOP_FLAG) {
					/*
					 * TO-DO create function or hook to send the elevator upwards with v1
					 * changes in function update_GUI_FLAGS() are needed.
					 */
				}
			}
		});
		// event click to send the elevator upwards with v2
		sa_Motor_UP_V2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// poll,update registers before run action
				update_GUI_FLAGS();
				// if no crash on the end sensors, run your function...
				if (!sa_EMERGENCY_STOP_FLAG) {
					/*
					 *  TO-DO: create function or hook to send the elevator upwards with v2
					 *  changes in function update_GUI_FLAGS() are needed.
					 */
				}
			}
		});
		// event click to send the elevator downwards with v1
		sa_Motor_DOWN_V1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// poll,update registers before run action
				update_GUI_FLAGS();
				// if no crash on the end sensors, run your function...
				if (!sa_EMERGENCY_STOP_FLAG) {
					/*
					 *  TO-DO: create function or hook to send the elevator downwards with v1
					 *  changes in function update_GUI_FLAGS() are needed.
					 */
				}
			}
		});
		// event click to send the elevator downwards with v2
		sa_Motor_DOWN_V2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// poll,update registers before run action
				update_GUI_FLAGS();
				// if no crash on the end sensors, run your function...
				if (!sa_EMERGENCY_STOP_FLAG) {
					/*
					 *  TO-DO: create function or hook to send the elevator downwards with v2
					 *  changes in function update_GUI_FLAGS() are needed.
					 */
				}
			}
		});
	}

	/*
	 * update all important flags from controller to update the GUI
	 */
	public void update_GUI_FLAGS() {
		sa_EMERGENCY_STOP_FLAG = false; // just for understanding sa_EMERGENCY_STOP_FLAG = client.ReadCoils(0, 1)[0]; something like this
		sa_Door_is_OPEN_FLAG   = false;
		sa_Door_is_CLOSED_FLAG = false;
		sa_Motor_is_READY_FLAG = false;
		sa_Motor_is_ON_FLAG    = false;
		repaint();
	}
	

	/*
	 * simulate status lights
	 */
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graphics2d = (Graphics2D) g;
		// init status lights black
		g.setColor(Color.black);
		// round status lights
		Ellipse2D.Double sa_Door_Circle_is_OPEN   = new Ellipse2D.Double(30, 150, 60, 60);
		Ellipse2D.Double sa_Door_Circle_is_CLOSED = new Ellipse2D.Double(30, 220, 60, 60);
		Ellipse2D.Double sa_Motor_Circle_is_Ready = new Ellipse2D.Double(340, 150, 60, 60);
		Ellipse2D.Double sa_Motor_Circle_is_ON    = new Ellipse2D.Double(340, 220, 60, 60);
		// fill status lights
		graphics2d.fill(sa_Door_Circle_is_OPEN);
		graphics2d.fill(sa_Door_Circle_is_CLOSED);
		graphics2d.fill(sa_Motor_Circle_is_Ready);
		graphics2d.fill(sa_Motor_Circle_is_ON);

		// check state of booleans, fill green if true... fill red if false
	    if(sa_EMERGENCY_STOP_FLAG) {
	    	sa_Door_is_OPEN_FLAG   = false;
	    	sa_Door_is_CLOSED_FLAG = false;
	    	sa_Motor_is_READY_FLAG = false;
	    	sa_Motor_is_ON_FLAG	   = false;
	    }	
		if (sa_Door_is_OPEN_FLAG) {
			g.setColor(Color.GREEN);
			graphics2d.fill(sa_Door_Circle_is_OPEN);
		} else {
			g.setColor(Color.RED);
			graphics2d.fill(sa_Door_Circle_is_OPEN);
		}

		if (sa_Door_is_CLOSED_FLAG) {
			g.setColor(Color.GREEN);
			graphics2d.fill(sa_Door_Circle_is_CLOSED);
		} else {
			g.setColor(Color.RED);
			graphics2d.fill(sa_Door_Circle_is_CLOSED);
		}

		if (sa_Motor_is_READY_FLAG) {
			g.setColor(Color.GREEN);
			graphics2d.fill(sa_Motor_Circle_is_Ready);
		} else {
			g.setColor(Color.RED);
			graphics2d.fill(sa_Motor_Circle_is_Ready);
		}

		if (sa_Motor_is_ON_FLAG) {
			g.setColor(Color.GREEN);
			graphics2d.fill(sa_Motor_Circle_is_ON);
		} else {
			g.setColor(Color.RED);
			graphics2d.fill(sa_Motor_Circle_is_ON);
		}
		
	}

}
