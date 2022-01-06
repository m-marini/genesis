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

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static java.text.MessageFormat.format;

public class InfoBar extends JPanel {
    private final String[] formats;
    private final JLabel[] labels;
    private double minimum;
    private double maximum;

    public InfoBar() {
        this(SwingConstants.HORIZONTAL);
    }

    /**
     * @param axis
     */
    public InfoBar(int axis) {
        formats = new String[]{"Time: {0}",
                "FPS: {0,number,integer}",
                "TPS: {0,number,integer}",
                "Speed: x {0,number,#0.0}",
                "Range: {0,number,#0.###} - {1,number,#0.###}",
                "Individuals {0}"};
        labels = Arrays.stream(formats).map(JLabel::new).toArray(JLabel[]::new);
        GridLayout mgr = axis == SwingConstants.HORIZONTAL
                ? new GridLayout(1, 0)
                : new GridLayout(0, 1);
        setLayout(mgr);
        for (JLabel label : labels) {
            label.setHorizontalAlignment(SwingConstants.LEFT);
            add(label);
        }
    }

    public void setFps(double fps) {
        labels[1].setText(format(formats[1], fps));
    }

    public void setIndividualCount(int individualCount) {
        labels[5].setText(format(formats[5], individualCount));
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
        labels[4].setText(format(formats[4], minimum, maximum));
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
//        labels[4].setText(format(formats[4], minimum, maximum));
        labels[4].setText(format(formats[4], minimum, maximum));
    }

    public void setSpeed(double speed) {
        labels[3].setText(format(formats[3], speed));
    }

    public void setTime(long time) {
        labels[0].setText(format(formats[0], time));
    }

    public void setTps(double tps) {
        labels[2].setText(format(formats[2], tps));
    }
}
