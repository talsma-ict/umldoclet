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
package net.sourceforge.plantuml.dot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.cucadiagram.ICucaDiagram;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.svek.GroupMakerActivity;
import net.sourceforge.plantuml.svek.IEntityImage;

public final class CucaDiagramSimplifierActivity {

	private final ICucaDiagram diagram;
	private final StringBounder stringBounder;

	public CucaDiagramSimplifierActivity(ICucaDiagram diagram, List<String> dotStrings, StringBounder stringBounder)
			throws IOException, InterruptedException {
		this.diagram = diagram;
		this.stringBounder = stringBounder;
		boolean changed;
		do {
			changed = false;
			final Collection<Entity> groups = new ArrayList<>(diagram.getEntityFactory().groups());
			for (Entity g : groups)
				if (g.isAutarkic()) {
					final IEntityImage img = computeImage(g);
					g.overrideImage(img, LeafType.ACTIVITY);

					changed = true;
				}

		} while (changed);
	}

	private IEntityImage computeImage(Entity g) throws IOException, InterruptedException {
		final GroupMakerActivity maker = new GroupMakerActivity(diagram, g, stringBounder);
		return maker.getImage();
	}

}
