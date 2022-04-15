/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.preproc.ReadLine;

public class ReadFilterQuoteComment implements ReadFilter {

	public ReadLine applyFilter(final ReadLine source) {
		return new ReadLine() {
			public void close() throws IOException {
				source.close();
			}

			public StringLocated readLine() throws IOException {
				boolean longComment = false;
				while (true) {
					final StringLocated result = source.readLine();
					if (result == null) {
						return null;
					}
					final String trim = result.getString().replace('\t', ' ').trim();
					if (longComment && trim.endsWith("'/")) {
						longComment = false;
						continue;
					}
					if (longComment) {
						continue;
					}
					if (trim.startsWith("'")) {
						continue;
					}
					if (trim.startsWith("/'") && trim.endsWith("'/")) {
						continue;
					}
					if (trim.startsWith("/'") && trim.contains("'/") == false) {
						longComment = true;
						continue;
					}
					return ((StringLocated) result).removeInnerComment();
				}
			}
		};
	}

}
