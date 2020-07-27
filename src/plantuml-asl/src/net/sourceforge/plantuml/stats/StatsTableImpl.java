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
package net.sourceforge.plantuml.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import net.sourceforge.plantuml.stats.api.StatsColumn;
import net.sourceforge.plantuml.stats.api.StatsLine;
import net.sourceforge.plantuml.stats.api.StatsTable;

public class StatsTableImpl implements StatsTable {

	private final String name;
	private final Collection<StatsColumn> columnHeaders;
	private final List<StatsLine> lines = new ArrayList<StatsLine>();

	public StatsTableImpl(String name) {
		this.name = name;
		this.columnHeaders = EnumSet.noneOf(StatsColumn.class);
	}

	public String getName() {
		return name;
	}

	public Collection<StatsColumn> getColumnHeaders() {
		return Collections.unmodifiableCollection(columnHeaders);
	}

	public List<StatsLine> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public void addLine(StatsLine line) {
		this.columnHeaders.addAll(line.getColumnHeaders());
		lines.add(line);
	}

}
