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
package net.sourceforge.plantuml.creole.rosetta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Display;

public class Rosetta {

	private final List<String> unicodeHtml;

	public static Rosetta fromUnicodeHtml(List<String> lines) {
		return new Rosetta(lines);
	}

	public static Rosetta fromSyntax(WikiLanguage syntaxSource, String... wiki) {
		return new Rosetta(syntaxSource, Arrays.asList(wiki));
	}

	public static Rosetta fromSyntax(WikiLanguage syntaxSource, List<String> wiki) {
		return new Rosetta(syntaxSource, wiki);
	}

	public static Rosetta fromSyntax(WikiLanguage syntaxSource, Display display) {
		return new Rosetta(syntaxSource, from(display));
	}

	private static List<String> from(Display display) {
		final List<String> result = new ArrayList<String>();
		for (CharSequence cs : display) {
			result.add(cs.toString());
		}
		return result;
	}

	private Rosetta(List<String> lines) {
		this.unicodeHtml = new ArrayList<String>(lines);
	}

	private Rosetta(WikiLanguage syntaxSource, List<String> wiki) {
		final ReaderWiki reader;
		if (syntaxSource == WikiLanguage.DOKUWIKI) {
			reader = new ReaderDokuwiki();
		} else if (syntaxSource == WikiLanguage.CREOLE) {
			reader = new ReaderCreole();
//			} else if (syntaxSource == WikiLanguage.MARKDOWN) {
//			reader = new ReaderMarkdown();
//		} else if (syntaxSource == WikiLanguage.ASCIIDOC) {
//			reader = new ReaderAsciidoc();
		} else {
			throw new UnsupportedOperationException();
		}
		this.unicodeHtml = reader.transform(wiki);
	}

	public List<String> translateTo(WikiLanguage syntaxDestination) {
		final List<String> html = new ArrayList<String>();
		final WriterWiki writer = new WriterWiki(syntaxDestination);
		html.addAll(writer.transform(unicodeHtml));
		return Collections.unmodifiableList(html);
	}

}
