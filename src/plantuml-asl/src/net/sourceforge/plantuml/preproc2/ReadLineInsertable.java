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
package net.sourceforge.plantuml.preproc2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.preproc.ReadLine;

public abstract class ReadLineInsertable implements ReadLine {

	private final List<ReadLine> sources = new ArrayList<ReadLine>();

	final protected void insert(ReadLine inserted) throws IOException {
		sources.add(0, inserted);
	}

	abstract StringLocated readLineInternal() throws IOException;

	final public StringLocated readLine() throws IOException {
		while (sources.size() > 0) {
			final ReadLine tmp = sources.get(0);
			final StringLocated result = tmp.readLine();
			if (result != null) {
				return result;
			}
			tmp.close();
			sources.remove(0);
		}
		return readLineInternal();
	}

	abstract void closeInternal() throws IOException;

	final public void close() throws IOException {
		for (ReadLine s : sources) {
			s.close();
		}
		closeInternal();
	}

}
