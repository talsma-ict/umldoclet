/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.svek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.GroupHierarchy;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.SuperGroup;
import net.sourceforge.plantuml.cucadiagram.dot.DotData;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.image.EntityImageState;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public final class GroupPngMakerActivity {

	private final CucaDiagram diagram;
	private final IGroup group;
	private final StringBounder stringBounder;

	class InnerGroupHierarchy implements GroupHierarchy {

		public Set<SuperGroup> getAllSuperGroups() {
			throw new UnsupportedOperationException();
		}

		public IGroup getRootGroup() {
			throw new UnsupportedOperationException();
		}

		public SuperGroup getRootSuperGroup() {
			throw new UnsupportedOperationException();
		}

		public Collection<IGroup> getChildrenGroups(IGroup parent) {
			if (EntityUtils.groupRoot(parent)) {
				return diagram.getChildrenGroups(group);
			}
			return diagram.getChildrenGroups(parent);
		}

		public boolean isEmpty(IGroup g) {
			return diagram.isEmpty(g);
		}

	}

	public GroupPngMakerActivity(CucaDiagram diagram, IGroup group, StringBounder stringBounder) {
		this.diagram = diagram;
		this.group = group;
		this.stringBounder = stringBounder;
	}

	private List<Link> getPureInnerLinks() {
		final List<Link> result = new ArrayList<>();
		for (Link link : diagram.getLinks()) {
			final IEntity e1 = (IEntity) link.getEntity1();
			final IEntity e2 = (IEntity) link.getEntity2();
			if (e1.getParentContainer() == group && e1.isGroup() == false && e2.getParentContainer() == group
					&& e2.isGroup() == false) {
				result.add(link);
			}
		}
		return result;
	}

	final public StyleSignatureBasic getDefaultStyleDefinitionGroup() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.group);
	}

	public IEntityImage getImage() throws IOException, InterruptedException {
		if (group.size() == 0) {
			return new EntityImageState(group, diagram.getSkinParam());
		}
		final List<Link> links = getPureInnerLinks();
		final ISkinParam skinParam = diagram.getSkinParam();

		final DotData dotData = new DotData(group, links, group.getLeafsDirect(), diagram.getUmlDiagramType(),
				skinParam, new InnerGroupHierarchy(), diagram.getColorMapper(), diagram.getEntityFactory(), false,
				DotMode.NORMAL, diagram.getNamespaceSeparator(), diagram.getPragma());

		final GeneralImageBuilder svek2 = new GeneralImageBuilder(false, dotData, diagram.getEntityFactory(),
				diagram.getSource(), diagram.getPragma(), stringBounder, SName.activityDiagram);

		if (group.getGroupType() == GroupType.INNER_ACTIVITY) {
			final Stereotype stereo = group.getStereotype();
			final HColor borderColor = getColor(ColorParam.activityBorder, stereo);
			final HColor backColor = group.getColors().getColor(ColorType.BACK) == null
					? getColor(ColorParam.background, stereo)
					: group.getColors().getColor(ColorType.BACK);
			final double shadowing;
			if (UseStyle.useBetaStyle()) {
				final Style style = getDefaultStyleDefinitionGroup().getMergedStyle(skinParam.getCurrentStyleBuilder());
				shadowing = style.value(PName.Shadowing).asDouble();
			} else {
				shadowing = skinParam.shadowing(group.getStereotype()) ? 4 : 0;
			}
			return new InnerActivity(svek2.buildImage(null, new String[0]), borderColor, backColor, shadowing);
		}

		throw new UnsupportedOperationException(group.getGroupType().toString());

	}

	private final Rose rose = new Rose();

	protected final HColor getColor(ColorParam colorParam, Stereotype stereo) {
		final ISkinParam skinParam = diagram.getSkinParam();
		return rose.getHtmlColor(skinParam, stereo, colorParam);
	}
}
