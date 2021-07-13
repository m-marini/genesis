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

import org.mmarini.genesis.model.GridSnapshot;
import org.mmarini.genesis.model.SimulationHandler;
import org.mmarini.genesis.model.Snapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author US00852
 * 
 */
public class Main {
	private static final double SNAPSHOT_INTERVAL = 1;
	private static final double MILLISEC = 1000.;
	private static final int INTERVAL = 30;
	private static Logger log = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// try {
		// UIManager
		//					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); //$NON-NLS-1$
		// } catch (Exception e) {
		// log.error(e.getMessage(), e);
		// }
		final Main main = new Main();
		main.start();
	}

	private final JFrame frame;
	private final AbstractAction startAction;
	private final AbstractAction stopAction;
	private final AbstractAction resumeAction;
	private final AbstractAction exitAction;
	private final SimParamPane simParamPane;
	private final SimulationHandler handler;
	private final ActionListener timeAction;
	private final Timer timer;
	private final MonitorPane monitorPane;
	private long last;
	private Thread threadSimulator;
	private final Chart chemicalChartsPane;
	private final ChemicalChartData chemicalChartsData;
	private final Chart popChartsPane;
	private final PopulationChartData popChartsData;
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
			public void actionPerformed(final ActionEvent e) {
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
			public void actionPerformed(final ActionEvent e) {
				startNewSimulation();
			}
		};
		stopAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2368134504226456856L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				stopSimulation();
			}
		};
		exitAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2368134504226456856L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		};
		resumeAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2368134504226456856L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				resume();
			}
		};

		monitorPane.setHandler(handler);

		startAction.putValue(Action.NAME,
				Messages.getString("Main.start.label")); //$NON-NLS-1$
		startAction.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(Messages.getString("Main.start.keystroke"))); //$NON-NLS-1$

		resumeAction.putValue(Action.NAME,
				Messages.getString("Main.resume.label")); //$NON-NLS-1$
		resumeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(Messages.getString("Main.resume.keystroke"))); //$NON-NLS-1$
		resumeAction.setEnabled(false);

		stopAction.putValue(Action.NAME, Messages.getString("Main.stop.label")); //$NON-NLS-1$
		stopAction.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(Messages.getString("Main.stop.keystroke"))); //$NON-NLS-1$
		stopAction.setEnabled(false);

		exitAction.putValue(Action.NAME, Messages.getString("Main.exit.label")); //$NON-NLS-1$
		exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(Messages.getString("Main.exit.keystroke"))); //$NON-NLS-1$

		final JMenuBar menuBar = new JMenuBar();
		final JMenu menu = new JMenu();
		menu.setText(Messages.getString("Main.fileMenu.label")); //$NON-NLS-1$

		menu.add(new JMenuItem(startAction));
		menu.add(new JMenuItem(resumeAction));
		menu.add(new JMenuItem(stopAction));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(exitAction));

		menuBar.add(menu);

		frame.setJMenuBar(menuBar);
		frame.setTitle(Messages.getString("Main.title")); //$NON-NLS-1$
		frame.setSize(1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		final JTabbedPane tabPane = new JTabbedPane();
		tabPane.add(Messages.getString("Main.monitor.label"), monitorPane); //$NON-NLS-1$
		tabPane.add(
				Messages.getString("Main.chemical.label"), chemicalChartsPane); //$NON-NLS-1$
		tabPane.add(Messages.getString("Main.population.label"), popChartsPane); //$NON-NLS-1$
		c.add(tabPane, BorderLayout.CENTER);

		chemicalChartsPane.setBorder(BorderFactory
				.createTitledBorder("Chemicals"));
		popChartsPane.setBorder(BorderFactory.createTitledBorder("Population"));
		chemicalChartsPane.setTable(chemicalChartsData);
		popChartsPane.setTable(popChartsData);

		simParamPane.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("Main.simulation.label"))); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	private void notifyTime() {
		final long now = System.currentTimeMillis();
		monitorPane.setRefreshRate(MILLISEC / (now - last));
		last = now;
		monitorPane.refresh();
		final GridSnapshot snapshot = monitorPane.getSnapshot();
		if (snapshot.getTime() >= snapshotTimer) {
			final Snapshot data = snapshot.getSnapshot().clone();
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
		log.info("Start simulation"); //$NON-NLS-1$
		long last = System.currentTimeMillis();
		final double updateInterval = handler.getParameters()
				.getUpdateInterval();
		final long interval = Math.round(updateInterval * MILLISEC);
		long next = last + interval;
		final Thread currentThread = Thread.currentThread();
		try {
			while (threadSimulator == currentThread) {
				final long now = System.currentTimeMillis();
				if (now < next) {
					Thread.sleep(next - now);
				} else {
					final double time = (now - last) / MILLISEC;
					handler.update(updateInterval);
					monitorPane.setUpdateRate(1 / time);
					if (handler.isEmpty()) {
						stopSimulation();
					}
					last = now;
					next = now + interval;
				}
			}
		} catch (final InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		log.info("Simulation completed"); //$NON-NLS-1$
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
	private void start() {
		frame.setVisible(true);
	}

	/**
	 *  
	 */
	private void startNewSimulation() {
		final int selection = JOptionPane
				.showConfirmDialog(
						null,
						simParamPane,
						Messages.getString("Main.startSelection.title"), JOptionPane.OK_CANCEL_OPTION); //$NON-NLS-1$
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
		threadSimulator = new Thread("Simulator") { //$NON-NLS-1$

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
