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

package smetana.core.amiga;

public class AreaInt implements Area {

	private int data = 0;

	private final int UID = Counter.CPT++;

	private String getUID36() {
		return Integer.toString(UID, 36);
	}

	@Override
	public String toString() {
		return "AreaArray " + getUID36() + " " + data;
	}

	public void memcopyFrom(Area source) {
		AreaInt other = (AreaInt) source;
		this.data = other.data;
	}

	public void setInternal(int data) {
		this.data = data;
		if (trace()) {
			System.err.println("set I AM " + this);
		}
	}

	private boolean trace() {
		return false;
		//return getUID36().equals("2z7");
	}

	public int getInternal() {
		if (trace()) {
			// System.err.println("get I AM " + this);
		}
		return data;
	}

	// public void incInternal(int increment) {
	// data += increment;
	//
	// }
}
