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
package net.sourceforge.plantuml.cucadiagram;

import java.io.IOException;
import java.util.Collection;

import net.atmp.ImageBuilder;
import net.sourceforge.plantuml.Annotated;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.abel.EntityFactory;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.skin.Pragma;
import net.sourceforge.plantuml.skin.UmlDiagramType;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.StyleBuilder;

public interface ICucaDiagram extends GroupHierarchy, PortionShower, Annotated {

	ISkinParam getSkinParam();

	UmlDiagramType getUmlDiagramType();

	EntityFactory getEntityFactory();

	StyleBuilder getCurrentStyleBuilder();

	boolean isHideEmptyDescriptionForState();

	Collection<Link> getLinks();

	Pragma getPragma();

	long seed();

	String getMetadata();

	String getFlashData();

	ImageBuilder createImageBuilder(FileFormatOption fileFormatOption) throws IOException;

	String getNamespaceSeparator();

	UmlSource getSource();

	String[] getDotStringSkek();

	// boolean isAutarkic(Entity g);

	int getUniqueSequence();

}
