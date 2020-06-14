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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.command.regex.FoxSignature;

final public class StringLocated {

	private final String s;
	private final LineLocation location;
	private final String preprocessorError;

	public StringLocated(String s, LineLocation location) {
		this(s, location, null);
	}

	@Override
	public String toString() {
		return super.toString() + " " + s;
	}

	public StringLocated(String s, LineLocation location, String preprocessorError) {
		if (s == null) {
			throw new IllegalArgumentException();
		}
		this.s = s;
		this.location = location;
		this.preprocessorError = preprocessorError;
	}

	public StringLocated withErrorPreprocessor(String preprocessorError) {
		return new StringLocated(s, location, preprocessorError);
	}

	public StringLocated sub(int start, int end) {
		return new StringLocated(this.getString().substring(start, end), this.getLocation(),
				this.getPreprocessorError());
	}

	private StringLocated trimmed;

	public StringLocated getTrimmed() {
		if (trimmed == null) {
			this.trimmed = new StringLocated(StringUtils.trin(this.getString()), location, preprocessorError);
			trimmed.fox = this.fox;
			trimmed.trimmed = trimmed;
		}
		return trimmed;
	}

	public StringLocated removeInnerComment() {
		final String string = s.toString();
		final String trim = string.replace('\t', ' ').trim();
		if (trim.startsWith("/'")) {
			final int idx = string.indexOf("'/");
			if (idx != -1) {
				return new StringLocated(removeSpecialInnerComment(s.substring(idx + 2, s.length())), location,
						preprocessorError);
			}
		}
		if (trim.endsWith("'/")) {
			final int idx = string.lastIndexOf("/'");
			if (idx != -1) {
				return new StringLocated(removeSpecialInnerComment(s.substring(0, idx)), location, preprocessorError);
			}
		}
		if (trim.contains("/'''") && trim.contains("'''/")) {
			return new StringLocated(removeSpecialInnerComment(s), location, preprocessorError);
		}
		return this;
	}

	private String removeSpecialInnerComment(String s) {
		if (s.contains("/'''") && s.contains("'''/")) {
			return s.replaceAll("/'''[-\\w]*'''/", "");

		}
		return s;
	}

	public String getString() {
		return s;
	}

	public LineLocation getLocation() {
		return location;
	}

	public String getPreprocessorError() {
		return preprocessorError;
	}

	private long fox = -1;

	public long getFoxSignature() {
		if (fox == -1) {
			fox = FoxSignature.getFoxSignature(getString());
		}
		return fox;
	}
}
