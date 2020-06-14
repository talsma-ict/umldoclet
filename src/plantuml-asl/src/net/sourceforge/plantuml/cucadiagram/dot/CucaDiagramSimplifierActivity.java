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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.svek.GroupPngMakerActivity;
import net.sourceforge.plantuml.svek.IEntityImage;

public final class CucaDiagramSimplifierActivity {

	private final CucaDiagram diagram;
	private final StringBounder stringBounder;

	public CucaDiagramSimplifierActivity(CucaDiagram diagram, List<String> dotStrings, StringBounder stringBounder)
			throws IOException, InterruptedException {
		this.diagram = diagram;
		this.stringBounder = stringBounder;
		boolean changed;
		do {
			changed = false;
			final Collection<IGroup> groups = new ArrayList<IGroup>(diagram.getGroups(false));
			for (IGroup g : groups) {
				if (diagram.isAutarkic(g)) {
					// final EntityType type;
					// if (g.zgetGroupType() == GroupType.INNER_ACTIVITY) {
					// type = EntityType.ACTIVITY;
					// } else if (g.zgetGroupType() == GroupType.CONCURRENT_ACTIVITY) {
					// type = EntityType.ACTIVITY_CONCURRENT;
					// } else {
					// throw new IllegalStateException();
					// }

					final IEntityImage img = computeImage(g);
					g.overrideImage(img, LeafType.ACTIVITY);

					changed = true;
				}
			}
		} while (changed);
	}

	// private void computeImageGroup(EntityMutable g, EntityMutable proxy, List<String> dotStrings) throws IOException,
	// InterruptedException {
	// final GroupPngMakerActivity maker = new GroupPngMakerActivity(diagram, g);
	// proxy.setSvekImage(maker.getImage());
	// }

	private IEntityImage computeImage(IGroup g) throws IOException, InterruptedException {
		final GroupPngMakerActivity maker = new GroupPngMakerActivity(diagram, g, stringBounder);
		return maker.getImage();
	}

}
