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
package net.sourceforge.plantuml.command.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringLocated;

public abstract class RegexComposed implements IRegex {

	protected static final AtomicInteger nbCreateMatches = new AtomicInteger();
	protected final List<IRegex> partials;

	abstract protected String getFullSlow();

	private Pattern2 fullCached;

	private synchronized Pattern2 getPattern2() {
		if (fullCached == null) {
			final String fullSlow = getFullSlow();
			fullCached = MyPattern.cmpile(fullSlow, Pattern.CASE_INSENSITIVE);
		}
		return fullCached;
	}
	
	protected boolean isCompiled() {
		return fullCached != null;
	}

	public RegexComposed(IRegex... partial) {
		this.partials = Arrays.asList(partial);
	}

	public Map<String, RegexPartialMatch> createPartialMatch(Iterator<String> it) {
		nbCreateMatches.incrementAndGet();
		final Map<String, RegexPartialMatch> result = new HashMap<String, RegexPartialMatch>();
		for (IRegex r : partials) {
			result.putAll(r.createPartialMatch(it));
		}
		return result;
	}

	final public int count() {
		int cpt = getStartCount();
		for (IRegex r : partials) {
			cpt += r.count();
		}
		return cpt;
	}

	protected int getStartCount() {
		return 0;
	}

	public RegexResult matcher(String s) {
		final Matcher2 matcher = getPattern2().matcher(s);
		if (matcher.find() == false) {
			return null;
		}

		final Iterator<String> it = new MatcherIterator(matcher);
		return new RegexResult(createPartialMatch(it));
	}

	public boolean match(StringLocated s) {
		return getPattern2().matcher(s.getString()).find();
	}

	final public String getPattern() {
		return getPattern2().pattern();
	}

	final protected List<IRegex> getPartials() {
		return partials;
	}

}
