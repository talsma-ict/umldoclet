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



public interface __ptr__ extends __c__fields {

	public __ptr__ castTo(Class dest);
	public Object addVirtualBytes(int bytes);
	public __ptr__ unsupported();
	public __ptr__ plus(int pointerMove);
	public int comparePointer(__ptr__ other);
	public int minus(__ptr__ other);

	public String getDebug(String fieldName);

	public void copyDataFrom(__ptr__ other);
	public void copyDataFrom(__struct__ other);
	
	public int getInt();
	public void setInt(int value);
	public double getDouble();
	public void setDouble(double value);
	public __ptr__ getPtr();
	public void setPtr(__ptr__ value);
	public __struct__ getStruct();
	public void setStruct(__struct__ value);
	

}
