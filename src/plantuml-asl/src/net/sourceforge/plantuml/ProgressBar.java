/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgressBar {

	private static boolean enable;
	private static String last = null;
	private static final AtomicInteger total = new AtomicInteger();
	private static final AtomicInteger done = new AtomicInteger();

	private synchronized static void print(String message) {
		clear();
		System.err.print(message);
		last = message;
	}

	public synchronized static void clear() {
		if (last != null) {
			for (int i = 0; i < last.length(); i++) {
				System.err.print('\b');
			}
			for (int i = 0; i < last.length(); i++) {
				System.err.print(' ');
			}
			for (int i = 0; i < last.length(); i++) {
				System.err.print('\b');
			}
		}
		last = null;
	}

	public static void incTotal(int nb) {
		total.addAndGet(nb);
		printBar(done.intValue(), total.intValue());
	}

	private synchronized static void printBar(int done, int total) {
		if (enable == false) {
			return;
		}
		if (total == 0) {
			return;
		}
		final String message = "[" + getBar(done, total) + "] " + done + "/" + total;
		print(message);

	}

	private static String getBar(int done, int total) {
		final StringBuilder sb = new StringBuilder();
		final int width = 30;
		final int value = width * done / total;
		for (int i = 0; i < width; i++) {
			sb.append(i < value ? '#' : ' ');
		}
		return sb.toString();
	}

	public static void incDone(boolean error) {
		done.incrementAndGet();
		printBar(done.intValue(), total.intValue());
	}

	public static void setEnable(boolean value) {
		enable = value;
	}

}
