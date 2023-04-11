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
package net.sourceforge.plantuml.board;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.skin.UmlDiagramType;

public class BoardDiagram extends UmlDiagram {

	private final List<Activity> activities = new ArrayList<>();

	public DiagramDescription getDescription() {
		return new DiagramDescription("Board");
	}

	public BoardDiagram(UmlSource source) {
		super(source, UmlDiagramType.BOARD, null);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	@Override
	protected TextBlock getTextBlock() {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				drawMe(ug);
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				final double width = 200;
				final double height = 200;
				return new XDimension2D(width, height);

			}
		};
	}

	private void drawMe(final UGraphic ug) {
		UGraphic mug = ug;
		for (Activity activity : activities) {
			activity.drawMe(mug);
			mug = mug.apply(UTranslate.dx(activity.getFullWidth()));
		}

		final ULine line = ULine.hline(getFullWidth());

		for (int i = 0; i < getMaxStage(); i++) {
			final double dy = (i + 1) * PostIt.getHeight() - 10;
			ug.apply(HColors.BLACK).apply(new UStroke(5, 5, 0.5)).apply(UTranslate.dy(dy)).draw(line);
		}
	}

	private double getFullWidth() {
		double width = 0;
		for (Activity activity : activities) {
			width += activity.getFullWidth();
		}
		return width;
	}

	private int getMaxStage() {
		int max = 0;
		for (Activity activity : activities) {
			max = Math.max(max, activity.getMaxStage());
		}
		return max;
	}

	private Activity getLastActivity() {
		return this.activities.get(this.activities.size() - 1);
	}

	public CommandExecutionResult addLine(String plus, String label) {
		if (plus.length() == 0) {
			final Activity activity = new Activity(this, label, getSkinParam());
			this.activities.add(activity);
			return CommandExecutionResult.ok();
		}
		getLastActivity().addRelease(plus.length(), label);
		return CommandExecutionResult.ok();
	}

}
