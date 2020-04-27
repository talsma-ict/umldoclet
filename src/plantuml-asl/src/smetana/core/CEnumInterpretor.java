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

package smetana.core;

import java.util.ArrayList;
import java.util.List;

public class CEnumInterpretor {

	private final List<String> keys = new ArrayList<String>();
	private String keyRef;
	private int valueRef;

	public CEnumInterpretor(Class enumClass) {
		List<String> def = CType.getDefinition(enumClass);
		JUtils.LOG("def1=" + def);
		if (def.get(0).equals("typedef enum") == false) {
			throw new IllegalArgumentException();
		}
		if (def.get(1).equals("{") == false) {
			throw new IllegalArgumentException();
		}
		if (def.get(def.size() - 2).equals("}") == false) {
			throw new IllegalArgumentException();
		}
		def = def.subList(2, def.size() - 2);
		JUtils.LOG("def2=" + def);

		for (String s1 : def) {
			for (String s2 : s1.split(",")) {
				s2 = s2.trim();
				final int idx = s2.indexOf('=');
				final String k = idx == -1 ? s2 : s2.substring(0, idx);
				keys.add(k.trim());
				if (idx == -1) {
					continue;
				}
				if (keyRef != null) {
					throw new IllegalStateException();
				}
				keyRef = k.trim();
				valueRef = Integer.parseInt(s2.substring(idx + 1).trim());
			}
		}
		JUtils.LOG("keys=" + keys);
	}

	public int valueOf(String name) {
		JUtils.LOG("keys=" + keys);
		final int idx = keys.indexOf(name);
		if (idx == -1) {
			throw new IllegalArgumentException(name + " is no enum value");
		}
		if (keyRef == null) {
			return idx;
		}
		final int keyRefIndex = keys.indexOf(keyRef);
		if (keyRefIndex == -1) {
			throw new IllegalStateException();
		}
		return idx - keyRefIndex + valueRef;
	}
}
