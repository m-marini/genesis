/**
 * 
 */
package org.mmarini.genesis.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmarini.genesis.model.GridSnapshot;
import org.mmarini.genesis.model.SimulationHandler;
import org.mmarini.genesis.model.Snapshot;

/**
 * @author US00852
 * 
 */
public class Main {
	private static final double SNAPSHOT_INTERVAL = 1;
	private static final double MILLISEC = 1000.;
	private static final int INTERVAL = 30;
	private static Log log = LogFactory.getLog(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// for (LookAndFeelInfo i : UIManager.getInstalledLookAndFeels())
		// System.out.println(i);
		try {
			UIManager
					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Main main = new Main();
		main.start();
	}

	private JFrame frame;
	private AbstractAction startAction;
	private AbstractAction stopAction;
	private AbstractAction resumeAction;
	private AbstractAction exitAction;
	private SimParamPane simParamPane;
	private SimulationHandler handler;
	private ActionListener timeAction;
	private Timer timer;
	private MonitorPane monitorPane;
	private long last;
	private Thread threadSimulator;
	private Chart chemicalChartsPane;
	private ChemicalChartData chemicalChartsData;
	private Chart popChartsPane;
	private PopulationChartData popChartsData;
	private double snapshotTimer;

	/**
	 * 
	 */
	public Main() {
		frame = new JFrame();
		chemicalChartsPane = new Chart();
		popChartsPane = new Chart();
		simParamPane = new SimParamPane();
		handler = new SimulationHandler();
		monitorPane = new MonitorPane();
		chemicalChartsData = new ChemicalChartData();
		popChartsData = new PopulationChartData();
		timeAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				notifyTime();
			}
		};
		timer = new Timer(INTERVAL, timeAction);
		startAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2368134504226456856L;

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewSimulation();
			}
		};
		stopAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2368134504226456856L;

			@Override
			public void actionPerformed(ActionEvent e) {
				stopSimulation();
			}
		};
		exitAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2368134504226456856L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		resumeAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2368134504226456856L;

			@Override
			public void actionPerformed(ActionEvent e) {
				resume();
			}
		};

		monitorPane.setHandler(handler);

		startAction.putValue(Action.NAME, "Start");
		startAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("ctrl N"));
		
		resumeAction.putValue(Action.NAME, "Resume");
		resumeAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("ctrl R"));
		resumeAction.setEnabled(false);
		
		stopAction.putValue(Action.NAME, "Stop");
		stopAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("ctrl S"));
		stopAction.setEnabled(false);
		
		exitAction.putValue(Action.NAME, "Exit");
		exitAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("ctrl X"));

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu();
		menu.setText("File");

		menu.add(new JMenuItem(startAction));
		menu.add(new JMenuItem(resumeAction));
		menu.add(new JMenuItem(stopAction));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(exitAction));

		menuBar.add(menu);

		frame.setJMenuBar(menuBar);
		frame.setTitle("Genesis");
		frame.setSize(1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Monitor", monitorPane);
		tabPane.add("Chemical Charts", chemicalChartsPane);
		tabPane.add("Population Charts", popChartsPane);
		c.add(tabPane, BorderLayout.CENTER);

		chemicalChartsPane.setBorder(BorderFactory
				.createTitledBorder("Chemicals"));
		popChartsPane.setBorder(BorderFactory.createTitledBorder("Population"));
		chemicalChartsPane.setTable(chemicalChartsData);
		popChartsPane.setTable(popChartsData);

		simParamPane.setBorder(BorderFactory
				.createTitledBorder("Simulation Parameters"));
	}

	/**
	 * 
	 */
	private void resume() {
		startAction.setEnabled(false);
		resumeAction.setEnabled(false);
		stopAction.setEnabled(true);
		startThread();
	}

	/**
	 * 
	 */
	private void notifyTime() {
		long now = System.currentTimeMillis();
		monitorPane.setRefreshRate(MILLISEC / (now - last));
		last = now;
		monitorPane.refresh();
		GridSnapshot snapshot = monitorPane.getSnapshot();
		if (snapshot.getTime() >= snapshotTimer) {
			Snapshot data = snapshot.getSnapshot().clone();
			chemicalChartsData.add(data);
			popChartsData.add(data);
			snapshotTimer += SNAPSHOT_INTERVAL;
		}
		if (threadSimulator == null) {
			timer.stop();
			startAction.setEnabled(true);
			resumeAction.setEnabled(true);
			stopAction.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	private void performSimulation() {
		log.info("Start simulation");
		long last = System.currentTimeMillis();
		double updateInterval = handler.getParameters().getUpdateInterval();
		long interval = Math.round(updateInterval * MILLISEC);
		long next = last + interval;
		Thread currentThread = Thread.currentThread();
		try {
			while (threadSimulator == currentThread) {
				long now = System.currentTimeMillis();
				if (now < next) {
					Thread.sleep(next - now);
				} else {
					double time = (now - last) / MILLISEC;
					handler.update(updateInterval);
					monitorPane.setUpdateRate(1 / time);
					if (handler.isEmpty()) {
						stopSimulation();
					}
					last = now;
					next = now + interval;
				}
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		log.info("Simulation completed");
	}

	/**
	 * 
	 */
	private void start() {
		frame.setVisible(true);
	}

	/**
	 *  
	 */
	private void startNewSimulation() {
		int selection = JOptionPane.showConfirmDialog(null, simParamPane,
				"Start new selection", JOptionPane.OK_CANCEL_OPTION);
		if (selection != JOptionPane.OK_OPTION) {
			return;
		}
		startAction.setEnabled(false);
		resumeAction.setEnabled(false);
		stopAction.setEnabled(true);

		handler.createSession(simParamPane.getParameters());
		snapshotTimer = 0;
		chemicalChartsData.clear();
		popChartsData.clear();
		startThread();
	}

	/**
	 * 
	 */
	private void startThread() {
		threadSimulator = new Thread("Simulator") {

			/**
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				performSimulation();
			}
		};
		threadSimulator.setPriority(Thread.NORM_PRIORITY - 1);
		threadSimulator.start();
		timer.start();
	}

	/**
	 * 
	 */
	private void stopSimulation() {
		threadSimulator = null;
	}
}
