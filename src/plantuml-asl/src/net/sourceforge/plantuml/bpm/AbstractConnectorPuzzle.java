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
package net.sourceforge.plantuml.bpm;

import java.util.EnumSet;
import java.util.Set;

abstract class AbstractConnectorPuzzle implements ConnectorPuzzle {

	private final EnumSet<Where> connections = EnumSet.noneOf(Where.class);

	public final boolean have(Where where) {
		return connections.contains(where);
	}

	public final void append(Where where) {
		this.connections.add(where);
	}

	public final void remove(Where where) {
		final boolean ok = connections.remove(where);
		if (ok == false) {
			throw new IllegalArgumentException();
		}
	}

	public final void append(ConnectorPuzzle other) {
		this.connections.addAll(((AbstractConnectorPuzzle) other).connections);
	}

	protected final Set<Where> connections() {
		return connections;
	}

}
