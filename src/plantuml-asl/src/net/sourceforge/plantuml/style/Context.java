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
package net.sourceforge.plantuml.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class Context {

	private final List<String> data = new ArrayList<String>();

	public Context push(String newString) {
		final Context result = new Context();
		result.data.addAll(this.data);
		result.data.add(newString);
		return result;
	}

	public Context pop() {
		if (size() == 0)
			throw new IllegalStateException();
		final Context result = new Context();
		result.data.addAll(this.data.subList(0, this.data.size() - 1));
		return result;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public int size() {
		return data.size();
	}

	public Collection<StyleSignatureBasic> toSignatures() {
		List<StyleSignatureBasic> results = new ArrayList<>(Collections.singletonList(StyleSignatureBasic.empty()));
		boolean star = false;
		for (Iterator<String> it = data.iterator(); it.hasNext();) {
			String s = it.next();
			if (s.endsWith("*")) {
				star = true;
				s = s.substring(0, s.length() - 1);
			}
			final String[] names = s.split(",");
			final List<StyleSignatureBasic> tmp = new ArrayList<>();
			for (StyleSignatureBasic ss : results)
				for (String name : names)
					tmp.add(ss.add(name.trim()));
			results = tmp;
		}

		if (star)
			for (ListIterator<StyleSignatureBasic> it = results.listIterator(); it.hasNext();) {
				final StyleSignatureBasic tmp = it.next().addStar();
				it.set(tmp);
			}

		return Collections.unmodifiableCollection(results);
	}

}
