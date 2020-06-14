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

package smetana.core;

import java.util.ArrayList;
import java.util.List;

import smetana.core.amiga.Area;

public class CObject implements Area {

	private static int ID;

	private final int size;
	private int id = ++ID;

	public CObject(int size, Class tobeAllocated) {
		this.size = size;
	}

	@Override
	public String toString() {
		return super.toString() + " id=" + id;
	}

	public List<CString> convertToListOfCString() {
		final ArrayList<CString> result = new ArrayList<CString>();
		for (int i = 0; i < size; i++) {
			result.add(null);
		}
		return result;
	}

//	public AreaArray convertToAreaArray() {
//		return new AreaArray(size, new BuilderArea() {
//			public Area createArea() {
//				return null;
//			}
//		});
//	}

	public void memcopyFrom(Area source) {
		throw new UnsupportedOperationException();
	}
	
}
