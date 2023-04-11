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
package net.sourceforge.plantuml.utils;

import java.util.List;

public abstract class InspectorUtils {
	// ::remove file when __HAXE__

	private InspectorUtils() {

	}

	public static <O> Inspector<O> inspector(final List<O> list) {
		return new Inspector<O>() {

			private int pos = 0;

			@Override
			public O peek(int ahead) {
				final int tmp = pos + ahead;
				if (tmp < list.size())
					return list.get(tmp);
				return null;
			}

			@Override
			public void jump() {
				pos++;
			}
		};
	}
}
