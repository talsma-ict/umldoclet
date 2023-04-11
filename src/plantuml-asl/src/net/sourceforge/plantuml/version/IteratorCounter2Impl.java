/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.text.StringLocated;

public class IteratorCounter2Impl implements IteratorCounter2 {

	private List<StringLocated> data;
	private List<StringLocated> trace;
	private int nb;

	public void copyStateFrom(IteratorCounter2 other) {
		final IteratorCounter2Impl source = (IteratorCounter2Impl) other;
		this.nb = source.nb;
		this.data = source.data;
		this.trace = source.trace;
	}

	public IteratorCounter2Impl(List<StringLocated> data) {
		this(data, 0, new ArrayList<StringLocated>());
	}

	private IteratorCounter2Impl(List<StringLocated> data, int nb, List<StringLocated> trace) {
		this.data = data;
		this.nb = nb;
		this.trace = trace;
	}

	public IteratorCounter2 cloneMe() {
		return new IteratorCounter2Impl(data, nb, new ArrayList<>(trace));
	}

	public int currentNum() {
		return nb;
	}

	public boolean hasNext() {
		return nb < data.size();
	}

	public StringLocated next() {
		final StringLocated result = data.get(nb);
		nb++;
		trace.add(result);
		return result;
	}

	public StringLocated peek() {
		return data.get(nb);
	}

	public StringLocated peekPrevious() {
		if (nb == 0) {
			return null;
		}
		return data.get(nb - 1);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public final List<StringLocated> getTrace() {
		return Collections.unmodifiableList(trace);
	}

}
