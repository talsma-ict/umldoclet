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
package net.sourceforge.plantuml.stats;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.api.NumberAnalyzed;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.stats.api.StatsColumn;
import net.sourceforge.plantuml.stats.api.StatsLine;
import net.sourceforge.plantuml.stats.api.StatsTable;

public class FormatCounter {

	private ConcurrentMap<FileFormat, NumberAnalyzed> data = new ConcurrentHashMap<FileFormat, NumberAnalyzed>();

	public FormatCounter(String prefix) {
		for (FileFormat format : FileFormat.values()) {
			final String key = prefix + format.name();
			data.put(format, new NumberAnalyzed(key));
		}

	}

	public void plusOne(FileFormat fileFormat, long duration) {
		final NumberAnalyzed n = data.get(fileFormat);
		n.addValue(duration);
	}

	private StatsLine createLine(String name, NumberAnalyzed n) {
		final Map<StatsColumn, Object> result = new EnumMap<StatsColumn, Object>(StatsColumn.class);
		result.put(StatsColumn.FORMAT, name);
		result.put(StatsColumn.GENERATED_COUNT, n.getNb());
		result.put(StatsColumn.GENERATED_MEAN_TIME, n.getMean());
		result.put(StatsColumn.GENERATED_STANDARD_DEVIATION, n.getStandardDeviation());
		result.put(StatsColumn.GENERATED_MAX_TIME, n.getMax());
		return new StatsLineImpl(result);
	}

	public StatsTable getStatsTable(String name) {
		final StatsTableImpl result = new StatsTableImpl(name);
		final NumberAnalyzed total = new NumberAnalyzed();
		for (Map.Entry<FileFormat, NumberAnalyzed> ent : data.entrySet()) {
			final NumberAnalyzed n = ent.getValue();
			if (n.getNb() > 0) {
				result.addLine(createLine(ent.getKey().name(), n));
				total.add(n);
			}
		}
		result.addLine(createLine("Total", total));
		return result;
	}

	public void reload(String prefix, Preferences prefs) throws BackingStoreException {
		for (String key : prefs.keys()) {
			if (key.startsWith(prefix)) {
				try {
					final String name = removeDotSaved(key);
					final NumberAnalyzed value = NumberAnalyzed.load(name, prefs);
					if (value != null) {
						final FileFormat format = FileFormat.valueOf(name.substring(prefix.length()));
						data.put(format, value);
					}
				} catch (Exception e) {
					Logme.error(e);
				}
			}
		}
	}

	static String removeDotSaved(String key) {
		return key.substring(0, key.length() - ".saved".length());
	}

	public void save(Preferences prefs, FileFormat fileFormat) {
		data.get(fileFormat).save(prefs);
	}

}
