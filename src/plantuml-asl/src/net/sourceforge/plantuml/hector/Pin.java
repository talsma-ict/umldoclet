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
package net.sourceforge.plantuml.hector;


public class Pin {

	private int row;
	private int uid = -1;

	private final Object userData;

	public Pin(int row, Object userData) {
		this.row = row;
		this.userData = userData;
	}

	public void setUid(int uid) {
		if (this.uid != -1) {
			throw new IllegalStateException();
		}
		this.uid = uid;
	}

	public int getRow() {
		return row;
	}

	public int getUid() {
		return uid;
	}

	public Object getUserData() {
		return userData;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void push(int push) {
		setRow(getRow() + push);
	}

}
