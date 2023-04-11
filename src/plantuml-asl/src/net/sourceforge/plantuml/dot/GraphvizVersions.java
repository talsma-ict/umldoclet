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
package net.sourceforge.plantuml.dot;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GraphvizVersions {
	// ::remove file when __CORE__

	private final static GraphvizVersions singleton = new GraphvizVersions();

	private final Map<File, GraphvizVersion> map = new ConcurrentHashMap<File, GraphvizVersion>();

	private GraphvizVersions() {
	}

	public static GraphvizVersions getInstance() {
		return singleton;
	}

	public GraphvizVersion getVersion(File f) {
		if (f == null)
			return null;

		GraphvizVersion result = map.get(f);
		if (result != null)
			return result;

		result = checkVersionSlow(f.getAbsolutePath());
		map.put(f, result);
		return result;
	}

	static GraphvizVersion checkVersionSlow(String pathExecutable) {
		final GraphvizVersionFinder finder = new GraphvizVersionFinder(new File(pathExecutable));
		return finder.getVersion();
	}

}
