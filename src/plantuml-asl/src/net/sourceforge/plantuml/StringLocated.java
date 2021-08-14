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
package net.sourceforge.plantuml;

import java.util.Objects;

import net.sourceforge.plantuml.command.regex.FoxSignature;
import net.sourceforge.plantuml.tim.TLineType;

final public class StringLocated {

	private final String s;
	private final LineLocation location;
	private final String preprocessorError;

	private StringLocated trimmed;
	private long fox = -1;
	private TLineType type;

	public StringLocated(String s, LineLocation location) {
		this(s, location, null);
	}

	@Override
	public String toString() {
		return s;
	}

	public StringLocated append(String endOfLine) {
		return new StringLocated(s + endOfLine, location, preprocessorError);
	}

	public StringLocated mergeEndBackslash(StringLocated next) {
		if (StringUtils.endsWithBackslash(s) == false) {
			throw new IllegalArgumentException();
		}
		return new StringLocated(s.substring(0, s.length() - 1) + next.s, location, preprocessorError);
	}

	public StringLocated(String s, LineLocation location, String preprocessorError) {
		this.s = Objects.requireNonNull(s);
		this.location = location;
		this.preprocessorError = preprocessorError;
	}

	public StringLocated withErrorPreprocessor(String preprocessorError) {
		return new StringLocated(s, location, preprocessorError);
	}

	public StringLocated substring(int start, int end) {
		return new StringLocated(this.getString().substring(start, end), this.getLocation(),
				this.getPreprocessorError());
	}

	public StringLocated substring(int start) {
		return new StringLocated(this.getString().substring(start), this.getLocation(), this.getPreprocessorError());
	}

	public StringLocated getTrimmed() {
		if (trimmed == null) {
			this.trimmed = new StringLocated(StringUtils.trin(this.getString()), location, preprocessorError);
			trimmed.fox = this.fox;
			trimmed.trimmed = trimmed;
		}
		return trimmed;
	}

//	public StringLocated getTrimmedRight() {
//		return new StringLocated(StringUtils.trinEnding(this.getString()), location, preprocessorError);
//	}

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

	public long getFoxSignature() {
		if (fox == -1) {
			fox = FoxSignature.getFoxSignature(getString());
		}
		return fox;
	}

	public TLineType getType() {
		if (type == null) {
			type = TLineType.getFromLineInternal(s);
		}
		return type;
	}

}
