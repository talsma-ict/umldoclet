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
package net.sourceforge.plantuml.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter2;
import net.sourceforge.plantuml.version.IteratorCounter2Impl;

/**
 * Represents the textual source of some diagram. The source should start with a
 * <code>@startfoo</code> and end with <code>@endfoo</code>.
 * <p>
 * So the diagram does not have to be a UML one.
 * 
 * @author Arnaud Roques
 * 
 */
final public class UmlSource {

	final private List<StringLocated> source;
	final private List<StringLocated> rawSource;

	public UmlSource removeInitialSkinparam() {
		if (hasInitialSkinparam(source) == false) {
			return this;
		}
		final List<StringLocated> copy = new ArrayList<>(source);
		while (hasInitialSkinparam(copy)) {
			copy.remove(1);
		}
		return new UmlSource(copy, rawSource);
	}

	public boolean containsIgnoreCase(String searched) {
		for (StringLocated s : source) {
			if (StringUtils.goLowerCase(s.getString()).contains(searched)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasInitialSkinparam(final List<StringLocated> copy) {
		return copy.size() > 1 && (copy.get(1).getString().startsWith("skinparam ")
				|| copy.get(1).getString().startsWith("skinparamlocked "));
	}

	private UmlSource(List<StringLocated> source, List<StringLocated> rawSource) {
		this.source = source;
		this.rawSource = rawSource;
	}

	public UmlSource(List<StringLocated> data, boolean checkEndingBackslash) {
		this(data, checkEndingBackslash, new ArrayList<StringLocated>());
	}

	/**
	 * Build the source from a text.
	 * 
	 * @param data                 the source of the diagram
	 * @param checkEndingBackslash <code>true</code> if an ending backslash means
	 *                             that a line has to be collapsed with the
	 *                             following one.
	 */
	public UmlSource(List<StringLocated> data, boolean checkEndingBackslash, List<StringLocated> rawSource) {
		this(new ArrayList<StringLocated>(), rawSource);

		if (checkEndingBackslash) {
			final StringBuilder pending = new StringBuilder();
			for (StringLocated cs : data) {
				final String s = cs.getString();
				if (StringUtils.endsWithBackslash(s)) {
					pending.append(s.substring(0, s.length() - 1));
				} else {
					pending.append(s);
					this.source.add(new StringLocated(pending.toString(), cs.getLocation()));
					pending.setLength(0);
				}
			}
		} else {
			this.source.addAll(data);
		}
	}

	/**
	 * Retrieve the type of the diagram. This is based on the first line
	 * <code>@startfoo</code>.
	 * 
	 * @return the type of the diagram.
	 */
	public DiagramType getDiagramType() {
		return DiagramType.getTypeFromArobaseStart(source.get(0).getString());
	}

	/**
	 * Allows to iterator over the source.
	 * 
	 * @return a iterator that allow counting line number.
	 */
	public IteratorCounter2 iterator2() {
		return new IteratorCounter2Impl(source);
	}

//	public Iterator<StringLocated> iteratorRaw() {
//		return Collections.unmodifiableCollection(rawSource).iterator();
//	}

	/**
	 * Return the source as a single String with <code>\n</code> as line separator.
	 * 
	 * @return the whole diagram source
	 */
	public String getPlainString() {
		final StringBuilder sb = new StringBuilder();
		for (StringLocated s : source) {
			sb.append(s.getString());
			sb.append('\r');
			sb.append(BackSlash.CHAR_NEWLINE);
		}
		return sb.toString();
	}

	public String getRawString() {
		final StringBuilder sb = new StringBuilder();
		for (StringLocated s : rawSource) {
			sb.append(s.getString());
			sb.append('\r');
			sb.append(BackSlash.CHAR_NEWLINE);
		}
		return sb.toString();
	}

	public long seed() {
		return StringUtils.seed(getPlainString());
	}

	public String getLine(LineLocation n) {
		for (StringLocated s : source) {
			if (s.getLocation().compareTo(n) == 0) {
				return s.getString();
			}
		}
		return null;
	}

	/**
	 * Return the number of line in the diagram.
	 */
	public int getTotalLineCount() {
		return source.size();
	}

	public boolean getTotalLineCountLessThan5() {
		return getTotalLineCount() < 5;
	}

	/**
	 * Check if a source diagram description is empty. Does not take comment line
	 * into account.
	 * 
	 * @return <code>true</code> if the diagram does not contain information.
	 */
	public boolean isEmpty() {
		for (StringLocated s : source) {
			if (StartUtils.isArobaseStartDiagram(s.getString())) {
				continue;
			}
			if (StartUtils.isArobaseEndDiagram(s.getString())) {
				continue;
			}
			if (s.getString().matches("\\s*'.*")) {
				continue;
			}
			if (StringUtils.trin(s.getString()).length() != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieve the title, if defined in the diagram source. Never return
	 * <code>null</code>.
	 */
	public Display getTitle() {
		final Pattern2 p = MyPattern.cmpile("^[%s]*title[%s]+(.+)$");
		for (StringLocated s : source) {
			final Matcher2 m = p.matcher(s.getString());
			final boolean ok = m.matches();
			if (ok) {
				return Display.create(m.group(1));
			}
		}
		return Display.empty();
	}

	public boolean isStartDef() {
		return source.get(0).getString().startsWith("@startdef");
	}

	public String getId() {
		final Pattern p = Pattern.compile("id=([\\w]+)\\b");
		final Matcher m = p.matcher(source.get(0).getString());
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

}
