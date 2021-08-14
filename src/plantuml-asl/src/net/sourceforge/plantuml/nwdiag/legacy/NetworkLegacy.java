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
package net.sourceforge.plantuml.nwdiag.legacy;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.nwdiag.core.Network;
import net.sourceforge.plantuml.nwdiag.next.NStage;

public class NetworkLegacy extends Network {

	private final Map<NServerLegacy, String> localServers = new LinkedHashMap<NServerLegacy, String>();
	private final int stage;

	@Override
	public String toString() {
		return super.toString() + "(" + stage + ")";
	}

	public NetworkLegacy(NStage nstage, String name, int stage) {
		super(nstage, name);
		this.stage = stage;
	}

	public String getAdress(NServerLegacy server) {
		return localServers.get(server);
	}

	public void addServer(NServerLegacy server, Map<String, String> props) {
		String address = props.get("address");
		if (address == null) {
			address = "";
		}
		if (address.length() == 0 && localServers.containsKey(server)) {
			return;
		}
		localServers.put(server, address);
	}

	public boolean constainsLocally(String name) {
		for (NServerLegacy server : localServers.keySet()) {
			if (server.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public final int getStage() {
		return stage;
	}

	private double xmin;
	private double xmax;

	public void setMinMax(double xmin, double xmax) {
		this.xmin = xmin;
		this.xmax = xmax;
	}

	public final double getXmin() {
		return xmin;
	}

	public final double getXmax() {
		return xmax;
	}

}
