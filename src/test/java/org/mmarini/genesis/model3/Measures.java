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

package org.mmarini.genesis.model3;

import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

public class Measures {

    /**
     * @param capacity the capacity
     */
    public static Measures create(int capacity) {
        return new Measures(new double[capacity], 0);
    }

    private final double[] samples;
    private int size;

    /**
     * @param samples
     * @param size
     */
    public Measures(double[] samples, int size) {
        this.samples = samples;
        this.size = size;
    }

    /**
     * @param sample
     */
    public Measures addSample(double sample) {
        assert size < samples.length : "Data set overflow";
        samples[size++] = sample;
        return this;
    }

    /**
     * Returns the average value
     */
    public OptionalDouble average() {
        return DoubleStream.of(samples).limit(size).average();
    }

    /**
     * Returns the number of samples
     */
    public int size() {
        return size;
    }
}
