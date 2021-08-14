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
package net.sourceforge.plantuml.preproc2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.preproc.ReadLine;
import net.sourceforge.plantuml.preproc.ReadLineReader;
import net.sourceforge.plantuml.preproc.ReadLineSimple;
import net.sourceforge.plantuml.preproc.StartDiagramExtractReader;
import net.sourceforge.plantuml.preproc.Stdlib;
import net.sourceforge.plantuml.security.SURL;
import net.sourceforge.plantuml.tim.EaterException;

public class PreprocessorUtils {

	public static String withEnvironmentVariable(String s) {
		final Pattern p = Pattern.compile("%(\\w+)%");

		final Matcher m = p.matcher(s);
		final StringBuffer sb = new StringBuffer();
		while (m.find()) {
			final String var = m.group(1);
			final String value = getenv(var);
			if (value != null) {
				m.appendReplacement(sb, Matcher.quoteReplacement(value));
			}
		}
		m.appendTail(sb);
		s = sb.toString();
		return s;
	}

	public static String getenv(String var) {
		final String env = System.getProperty(var);
		if (StringUtils.isNotEmpty(env)) {
			return StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(env);
		}
		final String getenv = System.getenv(var);
		if (StringUtils.isNotEmpty(getenv)) {
			return StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(getenv);
		}
		return null;
	}

	private static InputStream getStdlibInputStream(String filename) {
		final InputStream result = Stdlib.getResourceAsStream(filename);
		// Log.info("Loading sdlib " + filename + " ok");
		return result;
	}

	public static ReadLine getReaderStdlibInclude(StringLocated s, String filename) {
		Log.info("Loading sdlib " + filename);
		InputStream is = getStdlibInputStream(filename);
		if (is == null) {
			return null;
		}
		final String description = "<" + filename + ">";
		try {
			if (StartDiagramExtractReader.containsStartDiagram(is, s, description)) {
				is = getStdlibInputStream(filename);
				return StartDiagramExtractReader.build(is, s, description);
			}
			is = getStdlibInputStream(filename);
			if (is == null) {
				return null;
			}
			return ReadLineReader.create(new InputStreamReader(is), description);
		} catch (IOException e) {
			e.printStackTrace();
			return new ReadLineSimple(s, e.toString());
		}
	}

	public static ReadLine getReaderIncludeUrl(final SURL url, StringLocated s, String suf, String charset)
			throws EaterException {
		try {
			if (StartDiagramExtractReader.containsStartDiagram(url, s, charset)) {
				return StartDiagramExtractReader.build(url, s, suf, charset);
			}
			return getReaderInclude(url, s.getLocation(), charset);
		} catch (IOException e) {
			e.printStackTrace();
			throw EaterException.located("Cannot open URL " + e.getMessage());
		}

	}

	public static ReadLine getReaderInclude(SURL url, LineLocation lineLocation, String charset)
			throws EaterException, UnsupportedEncodingException {
		final InputStream is = url.openStream();
		if (is == null) {
			throw EaterException.located("Cannot open URL");
		}
		if (charset == null) {
			Log.info("Using default charset");
			return ReadLineReader.create(new InputStreamReader(is), url.toString(), lineLocation);
		}
		Log.info("Using charset " + charset);
		return ReadLineReader.create(new InputStreamReader(is, charset), url.toString(), lineLocation);
	}

}
