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
package net.sourceforge.plantuml.tim.iterator;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.preproc.Sub;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.EaterExceptionLocated;
import net.sourceforge.plantuml.tim.EaterStartsub;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TLineType;
import net.sourceforge.plantuml.tim.TMemory;

public class CodeIteratorSub extends AbstractCodeIterator {

	private final Map<String, Sub> subs;

	private CodeIterator readingInProgress;

	private final TMemory memory;
	private final TContext context;

	public CodeIteratorSub(CodeIterator source, Map<String, Sub> subs, TContext context, TMemory memory) {
		super(source);
		this.context = context;
		this.memory = memory;
		this.subs = subs;
	}

	public Map<String, Sub> getSubs() {
		return Collections.unmodifiableMap(subs);
	}

	public StringLocated peek() throws EaterException, EaterExceptionLocated {
		if (readingInProgress != null) {
			return readingInProgress.peek();
		}
		StringLocated result = source.peek();
		if (result == null) {
			return null;
		}
		if (result.getType() == TLineType.STARTSUB) {
			final EaterStartsub eater = new EaterStartsub(result.getTrimmed());
			eater.analyze(context, memory);
			final Sub created = new Sub(eater.getSubname());
			this.subs.put(eater.getSubname(), created);
			source.next();
			StringLocated s = null;
			while ((s = source.peek()) != null) {
				if (s.getType() == TLineType.STARTSUB) {
					throw EaterException.located("Cannot nest sub");
				} else if (s.getType() == TLineType.ENDSUB) {
					source.next();
					readingInProgress = new CodeIteratorImpl(created.lines());
					break;
				} else {
					created.add(s);
					source.next();
				}
			}
		}
		if (readingInProgress != null) {
			return readingInProgress.peek();
		}
		return result;
	}

	@Override
	public void next() throws EaterException, EaterExceptionLocated {
		if (readingInProgress == null) {
			source.next();
			return;
		}
		readingInProgress.next();
		if (readingInProgress.peek() == null) {
			readingInProgress = null;
		}
	}

}
