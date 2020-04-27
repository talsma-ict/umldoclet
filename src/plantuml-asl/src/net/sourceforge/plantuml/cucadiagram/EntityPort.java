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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.svek.Ports;

public class EntityPort {

	private final String entityUid;
	private final String portId;

	public EntityPort(String entityUid, String portName) {
		this.entityUid = entityUid;
		this.portId = portName == null ? null : Ports.encodePortNameToId(portName);
	}

	public String getFullString() {
		if (portId != null) {
			return entityUid + ":" + portId;
		}
		return entityUid;
	}

	private boolean isShielded() {
		return entityUid.endsWith(":h");
	}

	public String getPrefix() {
		if (isShielded()) {
			return entityUid.substring(0, entityUid.length() - 2);
		}
		return entityUid;
	}

	public boolean startsWith(String centerId) {
		return entityUid.startsWith(centerId);
	}

	public boolean equalsId(EntityPort other) {
		return this.entityUid.equals(other.entityUid);
	}
}
