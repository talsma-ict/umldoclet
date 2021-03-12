/*
 * Copyright 2016-2021 Talsma ICT
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

/**
 * Writer that throws exceptions when writing, flushing or closing.
 *
 * @author Sjoerd Talsma
 */
public class ThrowingWriter extends Writer {

    private final Throwable throwable;

    private ThrowingWriter(Throwable throwable) {
        this.throwable = throwable;
    }

    public static ThrowingWriter throwing(Throwable throwable) {
        return new ThrowingWriter(throwable);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        sneakyThrow(throwable);
        throw (IOException) throwable;
    }

    @Override
    public void flush() throws IOException {
        sneakyThrow(throwable);
        throw (IOException) throwable;
    }

    @Override
    public void close() throws IOException {
        sneakyThrow(throwable);
        throw (IOException) throwable;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}
