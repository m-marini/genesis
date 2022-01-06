/*
 *
 * Copyright (c) 2021 Marco Marini, marco.marini@mmarini.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 *    END OF TERMS AND CONDITIONS
 *
 */

package org.mmarini.genesis.swing;

import org.mmarini.Tuple2;
import org.mmarini.genesis.model3.SimEngine;
import org.mmarini.genesis.model3.SimStatus;
import org.mmarini.genesis.model3.SimulatorEngineImpl;
import org.mmarini.genesis.model3.Topology;
import org.mmarini.genesis.yaml.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;

import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.mmarini.yaml.Utils.fromFile;

/**
 *
 */
public class Main1 {
    public static final long NANOS_PER_SEC = 1000000000L;
    public static final int FPS = 60;
    public static final double SPEED = 5;
    private static final Logger logger = LoggerFactory.getLogger(Main1.class);
    private static final int WIDTH_SPARE = 100;
    private static final int HEIGHT_SPARE = 100;
    public static String PROJECT = "Water";

    /**
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            Loader loader = Loader.create(fromFile("config.yml"));
            SimEngine engine = loader.createEngine();
            SimStatus status0 = loader.createStatus();
            String prjStr = args.length > 0 ? args[0] : PROJECT;
            if (!loader.resourceNames().contains(prjStr)) {
                throw new IllegalArgumentException(format("Missing projection resource %s", prjStr));
            }
            int prj = loader.resourceNames().indexOf(prjStr);
            new Main1(engine, status0, prj).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final JFrame frame;
    private final PlaneChart chart;
    private final InfoBar infoBar;
    private final int project;
    private final FrequencyMeter fps;
    private final FrequencyMeter tps;
    private final SimEngine engine;
    private final Random random;
    private final SimulatorEngineImpl<SimStatus, SimStatus> sim;

    /**
     * @param engine  the engine
     * @param status0 the initial status
     * @param prj     the projection
     */
    public Main1(SimEngine engine, SimStatus status0, int prj) {
        this.engine = requireNonNull(engine);
        requireNonNull(status0);
        this.project = prj;
        this.frame = new JFrame(Messages.getString("Main.title"));
        this.chart = new PlaneChart();
        this.infoBar = new InfoBar(SwingConstants.VERTICAL);
        this.fps = FrequencyMeter.create();
        this.tps = FrequencyMeter.create();
        Topology topology = engine.getTopology();
        this.random = new Random();
        this.sim = SimulatorEngineImpl.create(status0,
                        this::next,
                        this::emit
                )
                .setEventInterval(Duration.ofNanos(NANOS_PER_SEC / FPS));
        this.sim.setSpeed(SPEED);
        chart.setTopology(topology);
        Rectangle wnd = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        frame.setSize(wnd.width - WIDTH_SPARE, wnd.height - HEIGHT_SPARE);
        frame.setLocation(wnd.x, wnd.y);
        bind();
    }

    /**
     *
     */
    Main1 bind() {
        // Update ui status, refresh panels and send new event
        fps.getFlowable().doOnNext(infoBar::setFps).subscribe();
        tps.getFlowable().doOnNext(infoBar::setTps).subscribe();
        sim.setOnSpeed(infoBar::setSpeed);
        return this;
    }

    private SimStatus emit(SimStatus s) {
        rebuild(s);
        fps.tick();
        return s;
    }

    private Tuple2<SimStatus, Double> next(SimStatus status, double dt) {
        tps.tick();
        double t = status.getT();
        SimStatus next = engine.next(status, t + dt, random);
        return Tuple2.of(next, dt);
    }

    /**
     * @param status redraw the chart
     */
    private void rebuild(SimStatus status) {
        double t = status.getT();
        infoBar.setTime(round(t));
        //chart.setField(status.getResources().extractRow(project));
        chart.setPopulations(status.getPopulations(), project);
        infoBar.setMinimum(chart.getMinimum());
        infoBar.setMaximum(chart.getMaximum());
        infoBar.setIndividualCount(status.getIndividualCount());
    }

    /**
     *
     */
    private void run() {
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(chart, BorderLayout.CENTER);
        content.add(infoBar, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        this.sim.start();
    }

    /**
     * Shows an error message from an exception.
     *
     * @param e the exception
     * @return the controller
     */
    public Main1 showError(final Throwable e) {
        logger.error(e.getMessage(), e);
        return this;
    }
}
