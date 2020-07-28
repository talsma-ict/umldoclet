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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileWatcher {

	private final Map<File, Long> modified2 = new HashMap<File, Long>();

	public FileWatcher(Set<File> files) {
		if (files.size() == 0) {
			throw new IllegalArgumentException();
		}
		for (File f : files) {
			modified2.put(f, f.lastModified());
		}
	}

	@Override
	public String toString() {
		return modified2.toString();
	}

	public boolean hasChanged() {
		for (Map.Entry<File, Long> ent : modified2.entrySet()) {
			final long nowModified = ent.getKey().lastModified();
			if (ent.getValue().longValue() != nowModified) {
				return true;
			}
		}
		return false;
	}

}
