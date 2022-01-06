
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
import org.mmarini.genesis.model3.Matrix;
import org.mmarini.genesis.model3.Population;
import org.mmarini.genesis.model3.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.mmarini.Tuple2.stream;

/**
 *
 */
public class PlaneChart extends JComponent {
    public static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    static final Logger logger = LoggerFactory.getLogger(PlaneChart.class);

    static Color createColor(double xc) {
        double x = min(max(0, xc), 1);
        float h = (float) (0.8 * (1 - x));
        float b = (float) (0.7 * x + 0.3);
        return Color.getHSBColor(h, 1f, b);
    }

    private final double minSensitivity;
    private final double maxSensitivity;
    private Topology topology;
    private List<Path2D> polys;
    private List<Consumer<Graphics2D>> painters;
    private double minimum;
    private double maximum;

    /**
     *
     */
    public PlaneChart() {
        setBackground(Color.BLACK);
        topology = null;
        polys = List.of();
        painters = List.of();
        minSensitivity = 10e-3;
        maxSensitivity = 10;
    }

    /**
     * Returns the maxmium value
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * Returns the minimum value
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * @param min min value
     * @param max max value
     */
    private DoubleUnaryOperator norm(double min, double max) {
        if (min == max) {
            return (double x) -> 0.5;
        }
        final double range = Math.log(max / min);
        if (range < minSensitivity) {
            return (double x) -> 0.5;
        } else {
            final double mid = Math.sqrt(min * max);
            final double s = min(1 / range, maxSensitivity);

            return (double x) -> s * Math.log(x / mid) + 0.5;
        }
    }

    @Override
    protected void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (topology != null) {
            Dimension windowSize = getSize();
            Rectangle2D rect = topology.getBounds();
            double sx = windowSize.getWidth() / rect.getWidth();
            double sy = windowSize.getHeight() / rect.getHeight();
            AffineTransform tr = AffineTransform.getScaleInstance(sx, sy);
            g.setTransform(tr);
            painters.forEach(f -> f.accept(g));
        }
    }

    /**
     * @param field the field
     */
    public void setField(Matrix field) {
        minimum = field.min();
        maximum = field.max();
        final DoubleUnaryOperator f = norm(minimum, maximum);

        painters = IntStream.range(0, field.getNumCols())
                .mapToObj(i -> {
                    Color color = createColor(
                            f.applyAsDouble(
                                    field.get(0, i)));
                    Path2D shape = polys.get(i);
                    return (Consumer<Graphics2D>) g -> {
                        g.setColor(color);
                        g.fill(shape);
                    };
                })
                .collect(Collectors.toList());
        repaint();
    }

    /**
     * @param pops the populations
     * @param ref  resource index
     */
    public void setPopulations(List<Population> pops, int ref) {
        // Collect all locations and initialize result map
        Map<Integer, Double> y = pops.stream()
                .flatMap(pop -> Arrays.stream(pop.getLocations()).boxed())
                .collect(Collectors.toMap(
                        Function.identity(),
                        x -> Double.valueOf(0d)
                ));

        Map<Integer, List<Tuple2<Integer, Double>>> zzz = pops.stream()
                .flatMap(pop -> {
                    // For each individual
                    Matrix quantities = pop.getResources();
                    int noIndividuals = quantities.getNumCols();
                    int[] locations = pop.getLocations();
                    Stream<Tuple2<Integer, Double>> individuals = IntStream.range(0, noIndividuals)
                            .mapToObj(i -> {
                                return Tuple2.of(locations[i], quantities.get(ref, i));
                            });
                    return individuals;
                }).collect(Collectors.groupingBy(Tuple2::getV1));
        Map<Integer, Double> qtyByLocation = stream(zzz).collect(Collectors.toMap(
                Tuple2::getV1,
                t -> t._2.stream()
                        .mapToDouble(Tuple2::getV2)
                        .sum()
        ));
        if (qtyByLocation.isEmpty()) {
            painters = IntStream.range(0, topology.getNoCells())
                    .mapToObj(i -> {
                        Path2D shape = polys.get(i);
                        return (Consumer<Graphics2D>) g -> {
                            g.setColor(EMPTY_COLOR);
                            g.fill(shape);
                        };
                    }).collect(Collectors.toList());
        } else {
            minimum = qtyByLocation.values().stream().mapToDouble(x -> x).min().orElseThrow();
            maximum = qtyByLocation.values().stream().mapToDouble(x -> x).max().orElseThrow();
            final DoubleUnaryOperator f = norm(minimum, maximum);
            painters = IntStream.range(0, topology.getNoCells())
                    .mapToObj(i -> {
                        Path2D shape = polys.get(i);
                        if (qtyByLocation.containsKey(i)) {
                            Color color = createColor(
                                    f.applyAsDouble(qtyByLocation.get(i)));
                            return (Consumer<Graphics2D>) g -> {
                                g.setColor(color);
                                g.fill(shape);
                            };
                        } else {
                            return (Consumer<Graphics2D>) g -> {
                                g.setColor(EMPTY_COLOR);
                                g.fill(shape);
                            };
                        }
                    })
                    .collect(Collectors.toList());
        }
        repaint();
    }

    /**
     * @param top the topology
     */
    public void setTopology(Topology top) {
        this.topology = top;
        polys = Stream.ofNullable(top)
                .flatMap(t ->
                        IntStream.range(0, t.getNoCells()).mapToObj(i -> {
                            Point2D[] vert = t.getVertices(i);
                            Path2D polygon = new Path2D.Double();
                            polygon.moveTo(vert[0].getX(), vert[0].getY());
                            Arrays.stream(vert).skip(1).forEach(p -> polygon.lineTo(p.getX(), p.getY()));
                            polygon.lineTo(vert[0].getX(), vert[0].getY());
                            return polygon;
                        }))
                .collect(Collectors.toList());
        repaint();
    }
}
