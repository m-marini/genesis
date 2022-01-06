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

package org.mmarini.genesis.yaml;

import com.fasterxml.jackson.databind.JsonNode;
import org.mmarini.genesis.model3.SimEngine;
import org.mmarini.genesis.model3.SimStatus;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.mmarini.yaml.schema.Locator.root;

public class Loader {
    /**
     * @param root the root node
     */
    public static Loader create(JsonNode root) {
        requireNonNull(root);
        SchemaValidators.config().apply(root())
                .andThen(CrossValidators.config().apply(root()))
                .accept(root);
        return new Loader(root);
    }

    private final JsonNode root;

    /**
     * @param root the root node
     */
    protected Loader(JsonNode root) {
        this.root = requireNonNull(root);
    }

    /**
     *
     */
    public SimEngine createEngine() {
        return Parsers.engine(root);
    }

    /**
     *
     */
    public SimStatus createStatus() {
        return Parsers.status(root);
    }

    /**
     * @return
     */
    public List<String> resourceNames() {
        return Parsers.names(root.path("mass"));
    }
}
