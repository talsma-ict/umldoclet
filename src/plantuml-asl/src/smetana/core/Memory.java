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
package smetana.core;

import com.plantuml.api.cheerpj.WasmLog;

final public class Memory {

	public static void free(Object arg) {
	}

	public static int identityHashCode(Globals zz, CString data) {
		if (data == null)
			return 0;

		int result = data.getUid();
		zz.all.put(result, data);
		WasmLog.log("hashsize = " + zz.all.size());
		return result;
	}

	public static CString fromIdentityHashCode(Globals zz, int hash) {
		if (hash % 2 != 0)
			throw new IllegalArgumentException();

		Object result = zz.all.get(hash);
		if (result == null)
			throw new UnsupportedOperationException();

		return (CString) result;
	}

}
