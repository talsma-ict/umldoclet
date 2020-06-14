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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.LineLocationImpl;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringLocated;

public class ReadLineReader extends ReadLineInstrumented implements ReadLine {

	// private static final int LIMIT = 850;
	private final BufferedReader br;
	private LineLocationImpl location;
	private final String description;

	private ReadLineReader(Reader reader, String description, LineLocation parent) {
		if (description == null) {
			description = "?";
		}
		this.br = new BufferedReader(reader);
		this.location = new LineLocationImpl(description, parent);
		this.description = description;
		Log.info("Reading from " + description);
	}

	@Override
	public String toString() {
		return super.toString() + " " + description;
	}

	private ReadLineReader(Reader reader, String desc) {
		this(reader, desc, null);
	}

	public static ReadLine create(Reader reader, String description) {
		return new ReadLineReader(reader, description, null);
	}

	public static ReadLine create(Reader reader, String description, LineLocation parent) {
		return new ReadLineReader(reader, description, parent);
	}

	@Override
	StringLocated readLineInst() throws IOException {
		String s = br.readLine();
		location = location.oneLineRead();
		if (s == null) {
			return null;
		}
		// if (s.length() > LIMIT) {
		// Log.debug("Line truncated from " + s.length() + " to " + LIMIT);
		// s = s.substring(0, LIMIT);
		// }
		if (s.startsWith("\uFEFF")) {
			s = s.substring(1);
		}
		s = s.replace('\u2013', '-');
		// s = BackSlash.convertHiddenNewLine(s);
		// s = s.replace('\u00A0', ' ');
		// s = s.replace('\u201c', '\"');
		// s = s.replace('\u201d', '\"');
		// s = s.replace('\u00ab', '\"');
		// s = s.replace('\u00bb', '\"');
		// s = s.replace('\u2018', '\'');
		// s = s.replace('\u2019', '\'');
		// for (int i = 0; i < s.length(); i++) {
		// char c = s.charAt(i);
		// System.err.println("X " + Integer.toHexString((int) c) + " " + c);
		// }
		return new StringLocated(s, location);
	}

	@Override
	void closeInst() throws IOException {
		br.close();
	}

}
