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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.IOException;

public class ProcessState {

	private final String name;
	private final IOException cause;

	private ProcessState(String name, IOException cause) {
		this.name = name;
		this.cause = cause;
	}

	@Override
	public String toString() {
		if (cause == null) {
			return name;
		}
		return name + " " + cause.toString();
	}

	private final static ProcessState INIT = new ProcessState("INIT", null);
	private final static ProcessState RUNNING = new ProcessState("RUNNING", null);
	private final static ProcessState TERMINATED_OK = new ProcessState("TERMINATED_OK", null);
	private final static ProcessState TIMEOUT = new ProcessState("TIMEOUT", null);

	// INIT, RUNNING, TERMINATED_OK, TIMEOUT, IO_EXCEPTION1, IO_EXCEPTION2;

	public static ProcessState INIT() {
		return INIT;
	}

	public static ProcessState RUNNING() {
		return RUNNING;
	}

	public static ProcessState TERMINATED_OK() {
		return TERMINATED_OK;
	}

	public static ProcessState TIMEOUT() {
		return TIMEOUT;
	}

	public static ProcessState IO_EXCEPTION1(IOException e) {
		return new ProcessState("IO_EXCEPTION1", e);
	}

	public static ProcessState IO_EXCEPTION2(IOException e) {
		return new ProcessState("IO_EXCEPTION2", e);
	}

	public boolean differs(ProcessState other) {
		return name.equals(other.name) == false;
	}

	@Override
	public boolean equals(Object o) {
		final ProcessState other = (ProcessState) o;
		return name.equals(other.name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public Throwable getCause() {
		return cause;
	}
}
