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
package net.sourceforge.plantuml.preproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.EaterStartsub;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TLineType;
import net.sourceforge.plantuml.tim.TMemory;

public class Sub {

	private final String name;
	private final List<StringLocated> lines = new ArrayList<>();
//	private boolean indentationDone = false;

	public Sub(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return super.toString() + " " + name;
	}

	public void add(StringLocated s) {
//		if (indentationDone) {
//			throw new IllegalStateException();
//		}
		this.lines.add(s);
	}

	public final List<StringLocated> lines() {
//		if (indentationDone == false) {
//			CodeIteratorImpl.indentNow(lines);
//			indentationDone = true;
//		}
		return Collections.unmodifiableList(lines);
	}

	public static Sub fromFile(ReadLine reader, String blocname, TContext context, TMemory memory)
			throws IOException, EaterException {
		Sub result = null;
		StringLocated s = null;
		boolean skip = false;
		while ((s = reader.readLine()) != null) {
			final TLineType type = s.getTrimmed().getType();
			if (type == TLineType.STARTSUB) {
				final EaterStartsub eater = new EaterStartsub(s.getTrimmed());
				eater.analyze(context, memory);
				if (eater.getSubname().equals(blocname)) {
					skip = false;
					if (result == null) {
						result = new Sub(blocname);
					}
				}
				continue;
			}
			if (type == TLineType.ENDSUB && result != null) {
				skip = true;
			}
			if (result != null && skip == false) {
				result.add(s);
			}
		}
		return result;
	}

}
