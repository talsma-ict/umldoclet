/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.project.core3;

public class Solver10 {

	private final Histogram workLoad;

	public Solver10(Histogram workLoad) {
		this.workLoad = workLoad;
	}

	public TaskLoad solver(long totalLoad) {
		final HistogramSimple resultLoad = new HistogramSimple();
		final TaskLoadImpl result = new TaskLoadImpl(resultLoad);

		final long start = workLoad.getNext(0);
		result.setStart(start);
		long currentTime = start;
		while (totalLoad > 0) {
			final long tmpWorkLoad = workLoad.getValueAt(currentTime);
			final long nextChange = workLoad.getNext(currentTime);
			final long duration = nextChange - currentTime;
			final long partialLoad = duration * tmpWorkLoad;
			resultLoad.put(currentTime, tmpWorkLoad);

			if (partialLoad >= totalLoad) {
				final long end = currentTime + totalLoad / tmpWorkLoad;
				result.setEnd(end);
				resultLoad.put(end, 0);
				return result;
			}
			totalLoad -= partialLoad;
			currentTime = nextChange;
		}
		throw new UnsupportedOperationException();
	}

}
