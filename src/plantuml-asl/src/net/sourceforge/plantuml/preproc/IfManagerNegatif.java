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

import java.io.IOException;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.command.regex.Matcher2;

class IfManagerNegatif extends IfManager {

	private boolean skippingDone = false;

	public IfManagerNegatif(ReadLine source, DefinesGet defines) {
		super(source, defines);
	}

	@Override
	protected StringLocated readLineInternal() throws IOException {
		if (skippingDone == false) {
			skippingDone = true;
			do {
				final StringLocated s = readLine();
				if (s == null) {
					return null;
				}
				Matcher2 m = endifPattern.matcher(s.getString());
				if (m.find()) {
					return null;
				}
				m = elsePattern.matcher(s.getString());
				if (m.find()) {
					break;
				}
			} while (true);
		}

		final StringLocated s = super.readLineInternal();
		if (s == null) {
			return null;
		}
		final Matcher2 m = endifPattern.matcher(s.getString());
		if (m.find()) {
			return null;
		}
		return s;

	}

}
