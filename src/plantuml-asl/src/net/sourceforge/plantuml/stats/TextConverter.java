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
package net.sourceforge.plantuml.stats;

import java.io.PrintStream;
import java.util.Date;

import net.sourceforge.plantuml.stats.api.Stats;
import net.sourceforge.plantuml.stats.api.StatsColumn;
import net.sourceforge.plantuml.stats.api.StatsLine;

public class TextConverter {

	private final Stats stats;
	private int linesUsed;

	public TextConverter(Stats stats) {
		this.stats = stats;
	}

	public void printMe(PrintStream ps) {
		final TextTable table = new TextTable();
		table.addSeparator();
		table.addLine("ID", "Start", "Duration", "Generated", "Mean(ms)");
		// table.addLine("ID", "Start", "Last", "Parsed", "Mean(ms)", "Generated", "Mean(ms)");
		table.addSeparator();
		for (StatsLine line : stats.getLastSessions().getLines()) {
			Object id = (Long) line.getValue(StatsColumn.SESSION_ID);
			if (id == null) {
				id = "";
			}
			final Date start = (Date) line.getValue(StatsColumn.STARTING);
			// final Date end = (Date) line.getValue(StatsColumn.LAST);
			// final Long parsed = (Long) line.getValue(StatsColumn.PARSED_COUNT);
			final String duration = line.getValue(StatsColumn.DURATION_STRING).toString();
			final Long generated = (Long) line.getValue(StatsColumn.GENERATED_COUNT);
			final Long generated_ms = (Long) line.getValue(StatsColumn.GENERATED_MEAN_TIME);
			table.addLine(id, start, duration, generated, generated_ms);

		}
		table.addSeparator();
		linesUsed = table.getLines();
		table.printMe(ps);
	}

	public int getLinesUsed() {
		return linesUsed;
	}

	public static void main(String[] args) {
		StatsUtils.dumpStats();

	}

}
