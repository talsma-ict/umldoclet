/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.style;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.stereo.Stereostyles;
import net.sourceforge.plantuml.stereo.Stereotype;

public class StyleSignatures implements StyleSignature {
    // ::remove file when __HAXE__

	private final List<StyleSignature> all = new ArrayList<StyleSignature>();

	public void add(StyleSignature signature) {
		all.add(signature);

	}

	@Override
	public String toString() {
		return all.toString();
	}

	@Override
	public Style getMergedStyle(StyleBuilder currentStyleBuilder) {
		if (all.size() == 0)
			throw new UnsupportedOperationException();
		Style result = null;
		for (StyleSignature basic : all) {
			final Style tmp = basic.getMergedStyle(currentStyleBuilder);
			if (result == null)
				result = tmp;
			else
				result = result.mergeWith(tmp, MergeStrategy.KEEP_EXISTING_VALUE_OF_STEREOTYPE);
		}
		return result;

	}

	@Override
	public StyleSignature withTOBECHANGED(Stereotype stereotype) {
		if (all.size() == 0)
			throw new UnsupportedOperationException();
		throw new UnsupportedOperationException();
	}

	@Override
	public StyleSignature with(Stereostyles stereostyles) {
		if (all.size() == 0)
			throw new UnsupportedOperationException();
		final StyleSignatures result = new StyleSignatures();
		for (StyleSignature basic : all)
			result.add(basic.with(stereostyles));

		return result;
	}

}
