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

import net.sourceforge.plantuml.StringLocated;

public class ReadLineSimple implements ReadLine {

	private final StringLocated data;
	private final String error;
	private int current = 0;

	public ReadLineSimple(StringLocated s2, String error) {
		this.data = s2;
		this.error = error;
	}

	public void close() {
	}

	public StringLocated readLine() {
		if (current > 0) {
			return null;
		}
		current++;
		// return new CharSequence2Impl(line, null);
		// return CharSequence2Impl.errorPreprocessor(data, error);
		return data.withErrorPreprocessor(error);
	}

}
