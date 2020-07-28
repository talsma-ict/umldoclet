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
package net.sourceforge.plantuml.syntax;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.error.PSystemError;

public class SyntaxResult {

	private UmlDiagramType umlDiagramType;
	private boolean isError;
	private String description;
	private Collection<String> errors = new TreeSet<String>();
	private boolean hasCmapData;
	private PSystemError systemError;
	private LineLocation lineLocation;

	public UmlDiagramType getUmlDiagramType() {
		return umlDiagramType;
	}

	public boolean isError() {
		return isError;
	}

	public String getDescription() {
		return description;
	}

	public Collection<String> getErrors() {
		return Collections.unmodifiableCollection(errors);
	}

	public void setUmlDiagramType(UmlDiagramType umlDiagramType) {
		this.umlDiagramType = umlDiagramType;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addErrorText(String error) {
		this.errors.add(error);
	}

	public final boolean hasCmapData() {
		return hasCmapData;
	}

	public final void setCmapData(boolean hasCmapData) {
		this.hasCmapData = hasCmapData;
	}

	public void setSystemError(PSystemError systemError) {
		this.systemError = systemError;
	}

	public void generateDiagramDescriptionForError(OutputStream os, FileFormatOption fileFormatOption)
			throws IOException {
		if (systemError == null) {
			throw new IllegalStateException();
		}
		systemError.exportDiagram(os, 0, fileFormatOption);
	}

	public void setLineLocation(LineLocation lineLocation) {
		this.lineLocation = lineLocation;
	}

	public LineLocation getLineLocation() {
		return lineLocation;
	}

}
