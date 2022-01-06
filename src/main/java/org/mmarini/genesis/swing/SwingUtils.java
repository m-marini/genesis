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

import hu.akarnokd.rxjava3.swing.SwingObservable;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.String.format;

/**
 * Various functionalities used in the user interface.
 */
public abstract class SwingUtils {

    public static final double BRIGHTNESS_ZERO = 0.7;
    public static final double BRIGHTNESS_ONE = 1;
    public static final double HUE_ZERO = 0.8;
    public static final double HUE_ONE = 0;
    public static final double NODE_SATURATION = 1;
    static final Logger logger = LoggerFactory.getLogger(SwingUtils.class);

    /**
     * Returns the flowable of action events for an abstract button
     *
     * @param button the button
     */
    public static Flowable<ActionEvent> actions(final AbstractButton button) {
        return SwingObservable.actions(button).toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Returns the flowable of action event for a text field.
     *
     * @param field the field
     */
    public static Flowable<ActionEvent> actions(final JTextField field) {
        assert field != null;
        return Flowable.create(emitter -> {
            logger.debug("register listener on {}", field);
            field.addActionListener(ev -> emitter.onNext(ev));
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * Returns the flowable of change event for a viewport
     *
     * @param component the viewport
     */
    public static Flowable<ChangeEvent> change(final JViewport component) {
        return SwingObservable.change(component).toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Returns the rainbow color depending on a control value.
     * <p>
     * > The result color are between violet color to red color varying from zero
     * level to one level of the control value
     * </p>
     *
     * @param value      the control value
     * @param saturation the saturation of result color
     */
    public static Color computeColor(final double value, final double saturation) {
        final double b = min(max(0, interpolate(value, BRIGHTNESS_ZERO, BRIGHTNESS_ONE)), 1);
        final double h = min(max(0, interpolate(value, HUE_ZERO, HUE_ONE)), 1);
        final Color color = Color.getHSBColor((float) h, (float) saturation, (float) b);
        return color;
    }

    /**
     * Returns the GridBagConstraintsBuilder for a field.
     * <p>
     * The field is left aligned e horizontal filled
     * </p>
     *
     * @param x horizontal position
     * @param y vertical position
     * @param w width
     * @param h height
     */
    public static GridBagConstraintsBuilder createFieldConstraints(final int x, final int y, final int w, final int h) {
        return createGridConstraints(x, y, w, h).west().horizontal().inset(2);
    }

    /**
     * Returns the default GridBagConstraintsBuilder.
     */
    public static GridBagConstraintsBuilder createGridConstraints() {
        return new GridBagConstraintsBuilder(new GridBagConstraints());
    }

    /**
     * Returns the default GridBagConstraintsBuilder for a cell.
     *
     * @param x horizontal position
     * @param y vertical position
     * @param w width
     * @param h height
     */
    public static GridBagConstraintsBuilder createGridConstraints(final int x, final int y, final int w, final int h) {
        return new GridBagConstraintsBuilder(new GridBagConstraints()).grid(x, y, w, h);
    }

    /**
     * Returns the GridBagConstraintsBuilder for a field.
     * <p>
     * The field is right aligned
     * </p>
     *
     * @param x horizontal position
     * @param y vertical position
     * @param w width the width
     * @param h height the height
     */
    public static GridBagConstraintsBuilder createLabelConstraints(final int x, final int y, final int w, final int h) {
        return createGridConstraints(x, y, w, h).east().inset(2);
    }

    /**
     * Returns the flowable of focus event for a component
     *
     * @param component the component
     */
    public static Flowable<FocusEvent> focus(final Component component) {
        return SwingObservable.focus(component).toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Returns the formatted string representing a time interval.
     *
     * @param t the interval time in seconds
     */
    public static String formatTime(final double t) {
        final double h = floor(t / 3600);
        final double tm = t - h * 3600;
        final double m = floor(tm / 60);
        final double s = tm - m * 60;
        if (h > 0) {
            return format("%02.0f:%02.0f:%02.0f", h, m, s);
        } else if (m > 0) {
            return format("%02.0f:%02.0f", m, s);
        } else {
            return format("%02.0f", s);
        }
    }

    /**
     * Interpolates linearly the result from two levels depending on a control
     * value.
     *
     * @param control   the control value
     * @param zeroLevel the start value (associated to the 0 level of control value)
     * @param oneLevel  the end value (associated to the 1 level of control value)
     * @return the linear interpolated value
     */
    private static double interpolate(final double control, final double zeroLevel, final double oneLevel) {
        return control * (oneLevel - zeroLevel) + zeroLevel;
    }

    /**
     * Returns the flowable of keyboard event for a component
     *
     * @param component the component
     */
    public static Flowable<KeyEvent> keyboard(final Component component) {
        return SwingObservable.keyboard(component).toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Returns the flwable of list selection event for a JList
     *
     * @param jList the JList
     */
    public static Flowable<ListSelectionEvent> listSelection(final JList<?> jList) {
        return SwingObservable.listSelection(jList).toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Returns the list of patterns by key.
     *
     * @param key the key
     */
    public static List<String> loadPatterns(final String key) {
        final List<String> list = new ArrayList<String>(0);
        int i = 0;
        for (; ; ) {
            final String text = Messages.getString(key + "." + i);
            if (text.startsWith("!")) {
                break;
            }
            list.add(text);
            ++i;
        }
        return list;
    }

    /**
     * Returns the flowable of mouse event for a component
     *
     * @param component the component
     */
    public static Flowable<MouseEvent> mouse(final Component component) {
        return SwingObservable.mouse(component).toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Returns the flowable of mouse wheel event for a component
     *
     * @param component the component
     */
    public static Flowable<MouseWheelEvent> mouseWheel(final Component component) {
        return SwingObservable.mouseWheel(component).toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> observeOnEdt() {
        return SwingObserveOn::new;
    }

    /**
     * Returns the flowable of value changed of a text field.
     *
     * @param <T>   the type of value
     * @param field the text field
     */
    public static <T> Flowable<T> value(final JFormattedTextField field) {
        final Flowable<JFormattedTextField> focusFlow = focus(field).filter(ev -> ev.getID() == FocusEvent.FOCUS_LOST)
                .map(ev -> field);
        final Flowable<JFormattedTextField> actionFlow = actions(field).map(ev -> field);
        @SuppressWarnings("unchecked") final Flowable<T> result = focusFlow.mergeWith(actionFlow).filter(c -> c.isEditValid()).map(c -> {
            c.commitEdit();
            return (T) c.getValue();
        });
        return result;
    }

    /**
     * Returns the utility builder of content container.
     *
     * @param <T>       the type of container
     * @param container the container
     */
    public static <T extends Container> WithGridBagConstraints<T> withGridBagConstraints(final T container) {
        return new WithGridBagConstraints<>(container);
    }

    /**
     * Builder of grid bag constraints
     */
    public static class GridBagConstraintsBuilder {
        private final GridBagConstraints constraints;

        /**
         * Creates a grid bag constraints builder.
         *
         * @param constraints the constraints
         */
        protected GridBagConstraintsBuilder(final GridBagConstraints constraints) {
            super();
            this.constraints = constraints;
        }

        /**
         * Returns a builder for above anchor.
         */
        public GridBagConstraintsBuilder above() {
            constraints.anchor = GridBagConstraints.ABOVE_BASELINE;
            return this;
        }

        /**
         * Returns the builder for above leading anchor.
         */
        public GridBagConstraintsBuilder aboveLeading() {
            constraints.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
            return this;
        }

        /**
         * Returns a builder for above trailing anchor.
         */
        public GridBagConstraintsBuilder aboveTrailing() {
            constraints.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
            return this;
        }

        /**
         * Returns the builder for an anchor.
         *
         * @param anchor the anchor
         */
        public GridBagConstraintsBuilder anchor(final int anchor) {
            constraints.anchor = anchor;
            return this;
        }

        /**
         * Returns the builder for a baseline anchor.
         */
        public GridBagConstraintsBuilder baseline() {
            constraints.anchor = GridBagConstraints.BASELINE;
            return this;
        }

        /**
         * Returns the builder for a below anchor.
         */
        public GridBagConstraintsBuilder below() {
            constraints.anchor = GridBagConstraints.BELOW_BASELINE;
            return this;
        }

        /**
         * Returns the builder for a below leading anchor.
         */
        public GridBagConstraintsBuilder belowLeading() {
            constraints.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
            return this;
        }

        /**
         * Returns the builder for a below trailing anchor.
         */
        public GridBagConstraintsBuilder belowTrailing() {
            constraints.fill = GridBagConstraints.BELOW_BASELINE_TRAILING;
            return this;
        }

        /**
         * Returns the builder for a both fill.
         */
        public GridBagConstraintsBuilder both() {
            constraints.fill = GridBagConstraints.BOTH;
            return this;
        }

        /**
         * Returns the built constraints.
         */
        public GridBagConstraints build() {
            return constraints;
        }

        /**
         * Returns the builder for a center anchor.
         */
        public GridBagConstraintsBuilder center() {
            constraints.anchor = GridBagConstraints.CENTER;
            return this;
        }

        /**
         * Returns the builder for a east anchor.
         */
        public GridBagConstraintsBuilder east() {
            constraints.anchor = GridBagConstraints.EAST;
            return this;
        }

        /**
         * Returns the builder for a fill.
         *
         * @param fill the fill mode
         */
        public GridBagConstraintsBuilder fill(final int fill) {
            constraints.fill = fill;
            return this;
        }

        /**
         * Returns the builder for a first line anchor.
         */
        public GridBagConstraintsBuilder firstLineEnd() {
            constraints.anchor = GridBagConstraints.FIRST_LINE_END;
            return this;
        }

        /**
         * Returns the builder for a first line start anchor.
         */
        public GridBagConstraintsBuilder firstLineStart() {
            constraints.anchor = GridBagConstraints.FIRST_LINE_START;
            return this;
        }

        /**
         * Returns the builder for a grid position.
         *
         * @param gridx      x cell index
         * @param gridy      y cell index
         * @param gridwidth  cell width
         * @param gridheight cell height
         */
        public GridBagConstraintsBuilder grid(final int gridx, final int gridy, final int gridwidth,
                                              final int gridheight) {
            constraints.gridx = gridx;
            constraints.gridy = gridy;
            constraints.gridwidth = gridwidth;
            constraints.gridheight = gridheight;
            return this;
        }

        /**
         * Returns the builder for a grid height.
         *
         * @param gridheight the height
         */
        public GridBagConstraintsBuilder height(final int gridheight) {
            constraints.gridheight = gridheight;
            return this;
        }

        /**
         * Returns the builder for a horizontal fill.
         */
        public GridBagConstraintsBuilder horizontal() {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            return this;
        }

        /**
         * Returns the builder for an insets.
         *
         * @param size the insets
         */
        public GridBagConstraintsBuilder inset(final int size) {
            constraints.insets = new Insets(size, size, size, size);
            return this;
        }

        /**
         * Returns the builder for an insets..
         *
         * @param vertical   vertical insets
         * @param horizontal horizontal insets
         */
        public GridBagConstraintsBuilder inset(final int vertical, final int horizontal) {
            constraints.insets = new Insets(vertical, horizontal, vertical, horizontal);
            return this;
        }

        /**
         * Returns the builder for an insets.
         *
         * @param top        top insets
         * @param horizontal horizontal insets
         * @param bottom     vertical insets
         */
        public GridBagConstraintsBuilder inset(final int top, final int horizontal, final int bottom) {
            constraints.insets = new Insets(top, horizontal, bottom, horizontal);
            return this;
        }

        /**
         * Returns the builder for an insets.
         *
         * @param top    top insets
         * @param left   left insets
         * @param bottom vertical insets
         * @param right  right insets
         */
        public GridBagConstraintsBuilder inset(final int top, final int left, final int bottom, final int right) {
            constraints.insets = new Insets(top, left, bottom, right);
            return this;
        }

        /**
         * Returns the builder for an insets.
         *
         * @param insets insets
         */
        public GridBagConstraintsBuilder insets(final Insets insets) {
            constraints.insets = insets;
            return this;
        }

        /**
         * Returns the builder for ipad.
         *
         * @param ipadx x ipad
         * @param ipady y ipad
         */
        public GridBagConstraintsBuilder ipad(final int ipadx, final int ipady) {
            constraints.ipadx = ipadx;
            constraints.ipady = ipady;
            return this;
        }

        /**
         * Returns the builder for ipad.
         *
         * @param ipadx x ipad
         */
        public GridBagConstraintsBuilder ipadx(final int ipadx) {
            constraints.ipadx = ipadx;
            return this;
        }

        /**
         * Returns the builder for ipad.
         *
         * @param ipady y ipad
         */
        public GridBagConstraintsBuilder ipady(final int ipady) {
            constraints.ipady = ipady;
            return this;
        }

        /**
         * Returns the builder for a last in column width.
         */
        public GridBagConstraintsBuilder lastInColumn() {
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            return this;
        }

        /**
         * Returns the builder for a last in row height.
         */
        public GridBagConstraintsBuilder lastInRow() {
            constraints.gridheight = GridBagConstraints.REMAINDER;
            return this;
        }

        /**
         * Returns the builder for a last line anchor.
         */
        public GridBagConstraintsBuilder lastLineEnd() {
            constraints.anchor = GridBagConstraints.LAST_LINE_END;
            return this;
        }

        /**
         * Returns the builder for a last line start anchor.
         */
        public GridBagConstraintsBuilder lastLineStart() {
            constraints.anchor = GridBagConstraints.LAST_LINE_START;
            return this;
        }

        /**
         * Returns the builder for a baseline leading anchor.
         */
        public GridBagConstraintsBuilder leading() {
            constraints.anchor = GridBagConstraints.BASELINE_LEADING;
            return this;
        }

        /**
         * Returns the builder for a line end.
         */
        public GridBagConstraintsBuilder lineEnd() {
            constraints.anchor = GridBagConstraints.LINE_END;
            return this;
        }

        /**
         * Returns the builder for a line start anchor.
         */
        public GridBagConstraintsBuilder lineStart() {
            constraints.anchor = GridBagConstraints.LINE_START;
            return this;
        }

        /**
         * Returns the builder for a next last in column width.
         */
        public GridBagConstraintsBuilder nextLastInColumn() {
            constraints.gridwidth = GridBagConstraints.RELATIVE;
            return this;
        }

        /**
         * Returns the builder for a next last in row height.
         */
        public GridBagConstraintsBuilder nextLastInRow() {
            constraints.gridheight = GridBagConstraints.RELATIVE;
            return this;
        }

        /**
         * Returns the builder for a next x position.
         */
        public GridBagConstraintsBuilder nextx() {
            constraints.gridx = GridBagConstraints.RELATIVE;
            return this;
        }

        /**
         * Returns the builder for a next y position.
         */
        public GridBagConstraintsBuilder nexty() {
            constraints.gridy = GridBagConstraints.RELATIVE;
            return this;
        }

        /**
         * Returns the builder for a none fill.
         */
        public GridBagConstraintsBuilder none() {
            constraints.fill = GridBagConstraints.NONE;
            return this;
        }

        /**
         * Returns the builder for a north anchor.
         */
        public GridBagConstraintsBuilder north() {
            constraints.anchor = GridBagConstraints.NORTH;
            return this;
        }

        /**
         * Returns the builder for a north east anchor.
         */
        public GridBagConstraintsBuilder northEast() {
            constraints.anchor = GridBagConstraints.NORTHEAST;
            return this;
        }

        /**
         * Returns the builder for a north west anchor.
         */
        public GridBagConstraintsBuilder northWest() {
            constraints.anchor = GridBagConstraints.NORTHWEST;
            return this;
        }

        /**
         * Returns the builder for a page end anchor.
         */
        public GridBagConstraintsBuilder pageEnd() {
            constraints.anchor = GridBagConstraints.PAGE_END;
            return this;
        }

        /**
         * Returns the builder for a page start.
         */
        public GridBagConstraintsBuilder pageStart() {
            constraints.anchor = GridBagConstraints.PAGE_START;
            return this;
        }

        /**
         * Returns the builder for a south anchor.
         */
        public GridBagConstraintsBuilder south() {
            constraints.anchor = GridBagConstraints.SOUTH;
            return this;
        }

        /**
         * Returns the builder for a south end anchor.
         */
        public GridBagConstraintsBuilder southEast() {
            constraints.anchor = GridBagConstraints.SOUTHEAST;
            return this;
        }

        /**
         * Returns the builder for a south west anchor.
         */
        public GridBagConstraintsBuilder southWest() {
            constraints.anchor = GridBagConstraints.SOUTHWEST;
            return this;
        }

        /**
         * Returns the builder for a trailing anchor.
         */
        public GridBagConstraintsBuilder trailing() {
            constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
            return this;
        }

        /**
         * Returns the builder for a vertical fill.
         */
        public GridBagConstraintsBuilder vertical() {
            constraints.fill = GridBagConstraints.VERTICAL;
            return this;
        }

        /**
         * Returns the builder for a weights.
         *
         * @param weightx the x weight
         * @param weighty the y weight
         */
        public GridBagConstraintsBuilder weight(final double weightx, final double weighty) {
            constraints.weightx = weightx;
            constraints.weighty = weighty;
            return this;
        }

        /**
         * Returns the builder for x weight.
         *
         * @param weightx the x weight
         */
        public GridBagConstraintsBuilder weightx(final double weightx) {
            constraints.weightx = weightx;
            return this;
        }

        /**
         * Returns the builder for y weight.
         *
         * @param weighty the y weight
         */
        public GridBagConstraintsBuilder weighty(final double weighty) {
            constraints.weighty = weighty;
            return this;
        }

        /**
         * Returns the builder for west anchor.
         */
        public GridBagConstraintsBuilder west() {
            constraints.anchor = GridBagConstraints.WEST;
            return this;
        }

        /**
         * Returns the builder for width.
         *
         * @param gridwidth the width
         */
        public GridBagConstraintsBuilder width(final int gridwidth) {
            constraints.gridwidth = gridwidth;
            return this;
        }

        /**
         * Returns the builder for x position.
         *
         * @param x the position
         */
        public GridBagConstraintsBuilder x(final int x) {
            constraints.gridx = x;
            return this;
        }

        /**
         * Returns the builder for y position.
         *
         * @param y the position
         */
        public GridBagConstraintsBuilder y(final int y) {
            constraints.gridy = y;
            return this;
        }
    }

    /**
     * Builder of grid bag constrained content for a container.
     *
     * @param <T> the type of container
     */
    public static class WithGridBagConstraints<T extends Container> {
        private final T container;

        /**
         * Creates the builder.
         *
         * @param container the container
         */
        public WithGridBagConstraints(final T container) {
            super();
            this.container = container;
            container.setLayout(new GridBagLayout());
        }

        /**
         * Adds a component with its constraints.
         *
         * @param component   the component
         * @param constraints the constraints
         * @return the builder
         */
        public WithGridBagConstraints<T> add(final Component component, final GridBagConstraints constraints) {
            ((GridBagLayout) container.getLayout()).setConstraints(component, constraints);
            container.add(component);
            return this;
        }

        /**
         * Returns the container.
         */
        public T getContainer() {
            return container;
        }

    }
}
