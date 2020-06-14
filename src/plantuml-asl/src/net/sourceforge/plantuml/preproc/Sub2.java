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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.EaterStartsub;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TLineType;
import net.sourceforge.plantuml.tim.TMemory;

public class Sub2 {

	private final String name;
	private final List<StringLocated> lines = new ArrayList<StringLocated>();

	public Sub2(String name) {
		this.name = name;
	}

	public void add(StringLocated s) {
		this.lines.add(s);
	}

	public final List<StringLocated> lines() {
		return Collections.unmodifiableList(lines);
	}

	public static Sub2 fromFile(ReadLine reader, String blocname, TContext context, TMemory memory) throws IOException,
			EaterException {
		Sub2 result = null;
		StringLocated s = null;
		while ((s = reader.readLine()) != null) {
			final TLineType type = TLineType.getFromLine(s.getTrimmed().getString());
			if (type == TLineType.STARTSUB) {
				final EaterStartsub eater = new EaterStartsub(s.getTrimmed().getString());
				eater.execute(context, memory);
				if (eater.getSubname().equals(blocname)) {
					result = new Sub2(blocname);
				}
				continue;
			}
			if (type == TLineType.ENDSUB && result != null) {
				reader.close();
				return result;
			}
			if (result != null) {
				result.add(s);
			}
		}
		reader.close();
		return null;
	}

}
