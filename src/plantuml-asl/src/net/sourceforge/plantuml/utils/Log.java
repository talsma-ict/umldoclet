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
package net.sourceforge.plantuml.utils;

import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.ProgressBar;

public abstract class Log {

	private static final long start = System.currentTimeMillis();

	public synchronized static void debug(String s) {
	}

	public synchronized static void info(String s) {
		if (OptionFlags.getInstance().isVerbose()) {
			ProgressBar.clear();
			System.err.println(format(s));
		}
	}

	public synchronized static void error(String s) {
		ProgressBar.clear();
		System.err.println(s);
	}

	private static String format(String s) {
		final long delta = System.currentTimeMillis() - start;
		// final HealthCheck healthCheck = Performance.getHealthCheck();
		// final long cpu = healthCheck.jvmCpuTime() / 1000L / 1000L;
		// final long dot = healthCheck.dotTime().getSum();

		final long freeMemory = Runtime.getRuntime().freeMemory();
		final long maxMemory = Runtime.getRuntime().maxMemory();
		final long totalMemory = Runtime.getRuntime().totalMemory();
		final long usedMemory = totalMemory - freeMemory;
		final int threadActiveCount = Thread.activeCount();

		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(delta / 1000L);
		sb.append(".");
		sb.append(String.format("%03d", delta % 1000L));
		// if (cpu != -1) {
		// sb.append(" - ");
		// sb.append(cpu / 1000L);
		// sb.append(".");
		// sb.append(String.format("%03d", cpu % 1000L));
		// }
		// sb.append(" - ");
		// sb.append(dot / 1000L);
		// sb.append(".");
		// sb.append(String.format("%03d", dot % 1000L));
		// sb.append("(");
		// sb.append(healthCheck.dotTime().getNb());
		// sb.append(")");
		sb.append(" - ");
		final long total = totalMemory / 1024 / 1024;
		final long free = freeMemory / 1024 / 1024;
		sb.append(total);
		sb.append(" Mo) ");
		sb.append(free);
		sb.append(" Mo - ");
		sb.append(s);
		return sb.toString();

	}

	public static void println(Object s) {
		// if (header2.get() == null) {
		// System.err.println("L = " + s);
		// } else {
		// System.err.println(header2.get() + " " + s);
		// }
	}

	// private static final ThreadLocal<String> header2 = new ThreadLocal<>();
	//
	public static void header(String s) {
		// header2.set(s);
	}
}
