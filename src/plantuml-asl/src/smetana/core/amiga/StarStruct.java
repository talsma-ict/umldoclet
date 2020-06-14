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

import smetana.core.AllH;
import smetana.core.__ptr__;
import smetana.core.__struct__;

public interface StarStruct extends Area, AllH, InternalData {

	public boolean isSameThan(StarStruct other);

	public Class getRealClass();

	public __struct__ getStruct();

	public String getUID36();

	public String getDebug(String fieldName);

	public void setInt(String fieldName, int data);

	public void setDouble(String fieldName, double data);

	public __ptr__ plus(int pointerMove);

	public void setStruct(String fieldName, __struct__ newData);

	public __ptr__ setPtr(String fieldName, __ptr__ newData);

	public void memcopyFrom(Area source);

	public void copyDataFrom(__struct__ other);

	public void setStruct(__struct__ value);

	public void copyDataFrom(__ptr__ arg);

	public __ptr__ castTo(Class dest);

	public Object addVirtualBytes(int virtualBytes);

}
