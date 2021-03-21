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
package net.sourceforge.plantuml.gitlog;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Scale;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.ImageParameter;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GitDiagram extends UmlDiagram {

	private final Collection<GNode> gnodes;

	public GitDiagram(GitTextArea textArea) {
		super(UmlDiagramType.GIT);
		this.gnodes = new GNodeBuilder(textArea.getAllCommits()).getAllNodes();
		new GNodeBuilder(textArea.getAllCommits());
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Git)");
	}


	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final Scale scale = getScale();

		final double dpiFactor = scale == null ? 1 : scale.getScale(100, 100);
		final ISkinParam skinParam = getSkinParam();
		final int margin1;
		final int margin2;
		if (UseStyle.useBetaStyle()) {
			margin1 = SkinParam.zeroMargin(10);
			margin2 = SkinParam.zeroMargin(10);
		} else {
			margin1 = 10;
			margin2 = 10;
		}
		final ImageParameter imageParameter = new ImageParameter(new ColorMapperIdentity(), false, null, dpiFactor, "",
				"", ClockwiseTopRightBottomLeft.margin1margin2(margin1, margin2), null);
		final ImageBuilder imageBuilder = ImageBuilder.build(imageParameter);
		TextBlock result = getTextBlock();
		result = new AnnotatedWorker(this, skinParam, fileFormatOption.getDefaultStringBounder(getSkinParam()))
				.addAdd(result);
		imageBuilder.setUDrawable(result);

		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, 0, os);
	}

	private void drawInternal(UGraphic ug) {

		new SmetanaForGit(ug, getSkinParam()).drawMe(gnodes);

//		final Display display = Display.getWithNewlines("Your data does not sound like GIT data");
//		final FontConfiguration fontConfiguration = FontConfiguration.blackBlueTrue(UFont.courier(14));
//		TextBlock result = display.create(fontConfiguration, HorizontalAlignment.LEFT, getSkinParam());
//		result = TextBlockUtils.withMargin(result, 5, 2);
//		result.drawU(ug);

	}

	private TextBlockBackcolored getTextBlock() {
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				drawInternal(ug);
			}

			public MinMax getMinMax(StringBounder stringBounder) {
				return null;
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return null;
			}

			public HColor getBackcolor() {
				return null;
			}
		};
	}

}
