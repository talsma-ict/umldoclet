/*
 * Copyright 2016-2019 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.umldoclet.rendering.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation that delegates writing to one or more delegate writers.
 *
 * @author Sjoerd Talsma
 */
public class DelegatingWriter extends Writer {
    /**
     * The list of {@link Writer delegate writers} to write to.
     */
    protected final List<Writer> delegates;

    /**
     * Constructor. Creates a new writer that writes to all provided delegates when written to.
     *
     * @param delegates The delegates to write to.
     */
    public DelegatingWriter(Writer... delegates) {
        final List<Writer> writers = new ArrayList<>(requireNonNull(delegates, "Delegates were null!").length);
        for (Writer delegate : delegates) {
            writers.add(requireNonNull(delegate, "Delegate writer was null!"));
        }
        this.delegates = unmodifiableList(writers);
    }

    /**
     * Delegates the write operation to all delegates and merges any occurred exceptions into a single {@link IOException}.
     *
     * @param cbuf The buffer containing the characters to be written.
     * @param off  The offset index to write from.
     * @param len  The number of characters to write.
     * @throws IOException in case at least one of the delegate writers threw an exception while writing.
     *                     <em>Please note:</em> It is very well possible that other delegates were succesfully written.
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        List<Exception> writeExceptions = new ArrayList<>(delegates.size());
        for (Writer delegate : delegates) {
            try {
                delegate.write(cbuf, off, len);
            } catch (IOException | RuntimeException writeException) {
                writeExceptions.add(writeException);
            }
        }
        if (!writeExceptions.isEmpty()) {
            throw mergeExceptions("writing", writeExceptions);
        }
    }

    /**
     * Delegates the flush operation to all delegates and merges any occurred exceptions into a single {@link IOException}.
     *
     * @throws IOException in case at least one of the delegate writers threw an exception while flushing.
     */
    @Override
    public void flush() throws IOException {
        List<Exception> flushExceptions = new ArrayList<>(delegates.size());
        for (Writer delegate : delegates) {
            try {
                delegate.flush();
            } catch (IOException | RuntimeException flushException) {
                flushExceptions.add(flushException);
            }
        }
        if (!flushExceptions.isEmpty()) {
            throw mergeExceptions("flushing", flushExceptions);
        }
    }

    /**
     * Delegates the close operation to all delegates and merges any occurred exceptions into a single {@link IOException}.
     *
     * @throws IOException in case at least one of the delegate writers threw an exception while closing.
     *                     <em>Please note:</em> Attempts are made to close all delegates.
     */
    @Override
    public void close() throws IOException {
        List<Exception> closeExceptions = new ArrayList<>(delegates.size());
        for (Writer delegate : delegates) {
            try {
                delegate.close();
            } catch (IOException | RuntimeException closeException) {
                closeExceptions.add(closeException);
            }
        }
        if (!closeExceptions.isEmpty()) {
            throw mergeExceptions("closing", closeExceptions);
        }
    }

    /**
     * Creates a single {@link IOException} merging potentially multiple cause exceptions into it.
     * Having this as a separate method helps avoiding unnecessary wrapping for the 'single exception' case.
     * <p>
     * Only in case a non-<code>IO</code> checked exception or multiple exceptions occurred,
     * this method will create a new IOException with message <code>"Error [ACTIONVERB] delegate writer!"</code> or
     * <code>"Error [ACTIONVERB] N delegate writers!</code> whatever may be the case.
     *
     * @param actionVerb A verb describing the action, e.g. <code>"writing"</code>, <code>"flushing"</code>
     *                   or <code>"closing"</code>.
     * @param exceptions The exceptions to merge into one IOException.
     * @return The merged IOException.
     */
    private IOException mergeExceptions(String actionVerb, Collection<Exception> exceptions) {
        if (exceptions.size() == 1) {
            Exception singleException = exceptions.iterator().next();
            if (singleException instanceof RuntimeException) {
                throw (RuntimeException) singleException;
            } else if (singleException instanceof IOException) {
                return (IOException) singleException;
            }
        }
        IOException ioe = new IOException("Error " + actionVerb + " " + exceptions.size() + " delegate writers!");
        for (Exception suppressed : exceptions) {
            ioe.addSuppressed(suppressed);
        }
        return ioe;
    }

    /**
     * @return The classname plus the delegate writers.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + delegates;
    }
}
