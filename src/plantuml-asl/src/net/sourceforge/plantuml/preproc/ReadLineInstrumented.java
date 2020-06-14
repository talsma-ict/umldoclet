/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.preproc;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringLocated;

public abstract class ReadLineInstrumented implements ReadLine {

	private static final boolean TRACE = false;

	private static ConcurrentMap<Class, AtomicLong> durations = new ConcurrentHashMap<Class, AtomicLong>();
	private static ConcurrentMap<Class, AtomicLong> maxes = new ConcurrentHashMap<Class, AtomicLong>();

	private long current = 0;

	private AtomicLong get(ConcurrentMap<Class, AtomicLong> source) {
		AtomicLong result = source.get(getClass());
		if (result == null) {
			result = new AtomicLong();
			source.put(getClass(), result);
		}
		return result;
	}

	public final StringLocated readLine() throws IOException {
		if (TRACE == false) {
			return readLineInst();
		}
		final long now = System.currentTimeMillis();
		try {
			return readLineInst();
		} finally {
			final long time = System.currentTimeMillis() - now;
			current += time;
			get(durations).addAndGet(time);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " current=" + current;
	}

	abstract StringLocated readLineInst() throws IOException;

	public final void close() throws IOException {
		if (TRACE) {
			if (current > get(maxes).get()) {
				get(maxes).set(current);
			}
			Log.info("DURATION::" + getClass() + " duration= " + get(durations).get() + " current=" + current + " max="
					+ get(maxes).get());
		}
		closeInst();
	}

	abstract void closeInst() throws IOException;

}
