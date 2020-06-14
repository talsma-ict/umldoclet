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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class DefineSignature {

	private final String key;
	private final String fonctionName;
	private final List<Variables> variables = new ArrayList<Variables>();
	private final boolean isMethod;

	public DefineSignature(String key, String definitionQuoted) {
		this.key = key;
		this.isMethod = key.contains("(");

		final StringTokenizer st = new StringTokenizer(key, "(),");
		this.fonctionName = st.nextToken().trim();
		final Variables master = new Variables(fonctionName, definitionQuoted);

		while (st.hasMoreTokens()) {
			final String var1 = st.nextToken().trim();
			master.add(new DefineVariable(var1));
		}

		final int count = master.countDefaultValue();
		for (int i = 0; i <= count; i++) {
			variables.add(master.removeSomeDefaultValues(i));
		}
	}

	@Override
	public String toString() {
		return key + "/" + fonctionName;
	}

	public boolean isMethod() {
		return isMethod;
	}

	public String getKey() {
		return key;
	}

	public List<Variables> getVariationVariables() {
		return Collections.unmodifiableList(variables);
	}

	public final String getFonctionName() {
		return fonctionName;
	}

}
