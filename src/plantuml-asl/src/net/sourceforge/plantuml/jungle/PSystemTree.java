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
package net.sourceforge.plantuml.jungle;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.UDrawableUtils;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.LimitFinder;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PSystemTree extends AbstractPSystem {

	private GNode root;
	private List<GNode> stack = new ArrayList<GNode>();
	private final Rendering rendering = Rendering.NEEDLE;

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Tree)");
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		final ImageBuilder builder = new ImageBuilder(new ColorMapperIdentity(), 1.0, HColorUtils.WHITE, null, null,
				5, 5, null, false);
		if (rendering == Rendering.NEEDLE) {
			final UDrawable tmp = Needle.getNeedle(root, 200, 0, 60);
			final LimitFinder limitFinder = new LimitFinder(fileFormat.getDefaultStringBounder(), true);
			tmp.drawU(limitFinder);
			final double minY = limitFinder.getMinY();
			builder.setUDrawable(UDrawableUtils.move(tmp, 0, -minY));
		} else {
			builder.setUDrawable(new GTileOneLevelFactory().createGTile(root));
		}
		return builder.writeImageTOBEMOVED(fileFormat, seed, os);
	}

	public CommandExecutionResult addParagraph(int level, String label) {

		if (level == 1 && root == null) {
			root = new GNode(Display.create(label));
			stack.add(root);
			return CommandExecutionResult.ok();
		} else if (level == 1 && root != null) {
			return CommandExecutionResult.error("Not allowed 1");
		}

		final GNode parent = stack.get(level - 2);
		final GNode newNode = parent.addChild(Display.create(label));

		if (level > stack.size() + 1) {
			return CommandExecutionResult.error("Not allowed 2");
		} else if (level - 1 == stack.size()) {
			stack.add(newNode);
		} else {
			stack.set(level - 1, newNode);
		}

		return CommandExecutionResult.ok();
	}

}
