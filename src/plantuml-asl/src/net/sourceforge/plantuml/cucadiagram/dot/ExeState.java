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

import java.io.File;

public enum ExeState {

	NULL_UNDEFINED, OK, DOES_NOT_EXIST, IS_A_DIRECTORY, NOT_A_FILE, CANNOT_BE_READ;

	public static ExeState checkFile(File dotExe) {
		if (dotExe == null) {
			return NULL_UNDEFINED;
		} else if (dotExe.exists() == false) {
			return DOES_NOT_EXIST;
		} else if (dotExe.isDirectory()) {
			return IS_A_DIRECTORY;
		} else if (dotExe.isFile() == false) {
			return NOT_A_FILE;
		} else if (dotExe.canRead() == false) {
			return CANNOT_BE_READ;
		}
		return OK;
	}

	public String getTextMessage() {
		switch (this) {
		case OK:
			return "File OK";
		case NULL_UNDEFINED:
			return "No dot executable found";
		case DOES_NOT_EXIST:
			return "File does not exist";
		case IS_A_DIRECTORY:
			return "It should be an executable, not a directory";
		case NOT_A_FILE:
			return "Not a valid file";
		case CANNOT_BE_READ:
			return "File cannot be read";
		}
		throw new IllegalStateException();
	}

	public String getTextMessage(File exe) {
		switch (this) {
		case OK:
			return "File " + exe.getAbsolutePath() + " OK";
		case NULL_UNDEFINED:
			return NULL_UNDEFINED.getTextMessage();
		case DOES_NOT_EXIST:
			return "File " + exe.getAbsolutePath() + " does not exist";
		case IS_A_DIRECTORY:
			return "File " + exe.getAbsolutePath() + " should be an executable, not a directory";
		case NOT_A_FILE:
			return "File " + exe.getAbsolutePath() + " is not a valid file";
		case CANNOT_BE_READ:
			return "File " + exe.getAbsolutePath() + " cannot be read";
		}
		throw new IllegalStateException();
	}

}
