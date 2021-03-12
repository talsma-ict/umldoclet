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
package nl.talsmasoftware.umldoclet.rendering;

import java.io.IOException;

/**
 * An appendable that throws an {@link IOException} at the first opportunity it gets written to.
 *
 * @author Sjoerd Talsma
 */
final class ThrowingAppendable implements Appendable {
    @Override
    public Appendable append(CharSequence csq) throws IOException {
        return append(csq, 0, csq.length());
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        for (int i = start; i < end; i++) this.append(csq.charAt(i));
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        throw new IOException("Intentionally throwing an IO exception!");
    }
}
