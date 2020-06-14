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
package net.sourceforge.plantuml.project3;

public class Failable<O> {

	private final O data;
	private final String error;

	public static <O> Failable<O> ok(O data) {
		return new Failable<O>(data, null);
	}

	public static <O> Failable<O> error(String error) {
		return new Failable<O>(null, error);
	}

	private Failable(O data, String error) {
		if (data == null && error == null) {
			throw new IllegalArgumentException();
		}
		if (data != null && error != null) {
			throw new IllegalArgumentException();
		}
		this.data = data;
		this.error = error;
	}

	public O get() {
		if (data == null) {
			throw new IllegalStateException();
		}
		return data;
	}

	public boolean isFail() {
		return data == null;
	}

	public String getError() {
		if (error == null) {
			throw new IllegalStateException();
		}
		return error;
	}

}
