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
 * Contribution :  Hisashi Miyashita
 */
package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.baraye.EntityImp;
import net.sourceforge.plantuml.baraye.EntityUtils;
import net.sourceforge.plantuml.baraye.IEntity;
import net.sourceforge.plantuml.baraye.IGroup;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.CucaNote;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.ICucaDiagram;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizVersion;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.USymbols;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.posimo.Moveable;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
import net.sourceforge.plantuml.svek.image.EntityImageState;
import net.sourceforge.plantuml.svek.image.EntityImageStateCommon;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class Cluster implements Moveable {

	// /* private */ static final String RANK_SAME = "same";
	/* private */ static final String RANK_SOURCE = "source";
	/* private */ static final String RANK_SINK = "sink";
	public final static String CENTER_ID = "za";

	private final Cluster parentCluster;
	private final IGroup group;
	private final List<SvekNode> nodes = new ArrayList<>();
	private final List<Cluster> children = new ArrayList<>();
	private final int color;
	private final int colorTitle;
	private final int colorNoteTop;
	private final int colorNoteBottom;
	private final ISkinParam skinParam;
	protected final ICucaDiagram diagram;

	private ClusterHeader clusterHeader;

	private XPoint2D xyTitle;

	private XPoint2D xyNoteTop;
	private XPoint2D xyNoteBottom;

	private ClusterPosition clusterPosition;

	public void moveSvek(double deltaX, double deltaY) {
		if (this.xyNoteTop != null)
			this.xyNoteTop = this.xyNoteTop.move(deltaX, deltaY);
		if (this.xyNoteBottom != null)
			this.xyNoteBottom = this.xyNoteBottom.move(deltaX, deltaY);
		if (this.xyTitle != null)
			this.xyTitle = this.xyTitle.move(deltaX, deltaY);
		if (this.clusterPosition != null)
			this.clusterPosition = this.clusterPosition.move(deltaX, deltaY);

	}

	private Set<EntityPosition> entityPositionsExceptNormal() {
		final Set<EntityPosition> result = EnumSet.<EntityPosition>noneOf(EntityPosition.class);
		for (SvekNode sh : nodes)
			if (sh.getEntityPosition() != EntityPosition.NORMAL)
				result.add(sh.getEntityPosition());

		return Collections.unmodifiableSet(result);
	}

	public Cluster(ICucaDiagram diagram, ColorSequence colorSequence, ISkinParam skinParam, IGroup root) {
		this(diagram, null, colorSequence, skinParam, root);
	}

	private Cluster(ICucaDiagram diagram, Cluster parentCluster, ColorSequence colorSequence, ISkinParam skinParam,
			IGroup group) {
		if (group == null)
			throw new IllegalStateException();

		this.parentCluster = parentCluster;
		this.group = group;
		this.diagram = diagram;

		this.color = colorSequence.getValue();
		this.colorTitle = colorSequence.getValue();
		this.colorNoteTop = colorSequence.getValue();
		this.colorNoteBottom = colorSequence.getValue();
		this.skinParam = group.getColors().mute(skinParam);
	}

	@Override
	public String toString() {
		return super.toString() + " " + group;
	}

	public final Cluster getParentCluster() {
		return parentCluster;
	}

	public void addNode(SvekNode node) {
		this.nodes.add(Objects.requireNonNull(node));
		node.setCluster(this);
	}

	public final List<SvekNode> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public final List<SvekNode> getNodes(EnumSet<EntityPosition> position) {
		final List<SvekNode> result = new ArrayList<>();
		for (SvekNode node : nodes)
			if (position.contains(node.getEntityPosition()))
				result.add(node);
		return Collections.unmodifiableList(result);
	}

	private List<SvekNode> getNodesOrderedTop(Collection<SvekLine> lines) {
		final List<SvekNode> firsts = new ArrayList<>();
		final Map<String, SvekNode> shs = new HashMap<String, SvekNode>();

		for (final Iterator<SvekNode> it = nodes.iterator(); it.hasNext();) {
			final SvekNode node = it.next();
			shs.put(node.getUid(), node);
		}

		for (SvekLine l : lines) {
			if (l.isInverted()) {
				final SvekNode sh = shs.get(l.getStartUidPrefix());
				if (sh != null && sh.getEntityPosition() == EntityPosition.NORMAL)
					firsts.add(0, sh);
			}
		}

		return firsts;
	}

	private List<SvekNode> getNodesOrderedWithoutTop(Collection<SvekLine> lines) {
		final List<SvekNode> all = new ArrayList<>(nodes);
		final Map<String, SvekNode> shs = new HashMap<String, SvekNode>();

		for (final Iterator<SvekNode> it = all.iterator(); it.hasNext();) {
			final SvekNode sh = it.next();
			if (sh.getEntityPosition() != EntityPosition.NORMAL) {
				it.remove();
				continue;
			}
			shs.put(sh.getUid(), sh);
		}

		for (SvekLine l : lines) {
			if (l.isInverted()) {
				final SvekNode sh = shs.get(l.getStartUidPrefix());
				if (sh != null)
					all.remove(sh);
			}
		}

		return all;
	}

	public final List<Cluster> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public Cluster createChild(ClusterHeader clusterHeader, ColorSequence colorSequence, ISkinParam skinParam,
			IGroup g) {
		final Cluster child = new Cluster(diagram, this, colorSequence, skinParam, g);
		child.clusterHeader = clusterHeader;
		this.children.add(child);
		return child;
	}

	public final Set<IGroup> getGroups() {
		return Collections.singleton(group);
	}

	final IGroup getGroup() {
		return group;
	}

	public final int getTitleAndAttributeWidth() {
		return clusterHeader.getTitleAndAttributeWidth();
	}

	public final int getTitleAndAttributeHeight() {
		return clusterHeader.getTitleAndAttributeHeight();
	}

	public ClusterPosition getClusterPosition() {
		return clusterPosition;
	}

	public void setTitlePosition(XPoint2D pos) {
		this.xyTitle = pos;
	}

	public void setNoteTopPosition(XPoint2D pos) {
		this.xyNoteTop = pos;
	}

	public void setNoteBottomPosition(XPoint2D pos) {
		this.xyNoteBottom = pos;
	}

	static public StyleSignatureBasic getDefaultStyleDefinition(SName diagramStyleName, USymbol symbol) {
		if (diagramStyleName == SName.stateDiagram)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.stateDiagram, SName.state, SName.group);
		if (symbol == null)
			return StyleSignatureBasic.of(SName.root, SName.element, diagramStyleName, SName.group);
		return StyleSignatureBasic.of(SName.root, SName.element, diagramStyleName, SName.group, symbol.getSName());
	}

	public void drawU(UGraphic ug, UmlDiagramType umlDiagramType, ISkinParam skinParam2unused) {
		if (group.isHidden())
			return;

		if (diagram.getPragma().useKermor()) {
			if (xyNoteTop != null)
				getCucaNote(Position.TOP).drawU(ug.apply(new UTranslate(xyNoteTop)));
			if (xyNoteBottom != null)
				getCucaNote(Position.BOTTOM).drawU(ug.apply(new UTranslate(xyNoteBottom)));
		}

		final String fullName = group.getCodeGetName();
		if (fullName.startsWith("##") == false)
			ug.draw(new UComment("cluster " + fullName));

		final USymbol uSymbol = group.getUSymbol() == null ? USymbols.PACKAGE : group.getUSymbol();
		Style style = getDefaultStyleDefinition(umlDiagramType.getStyleName(), uSymbol)
				.withTOBECHANGED(group.getStereotype()).getMergedStyle(skinParam.getCurrentStyleBuilder());
		final double shadowing = style.value(PName.Shadowing).asDouble();
		HColor borderColor;
		if (group.getColors().getColor(ColorType.LINE) != null)
			borderColor = group.getColors().getColor(ColorType.LINE);
		else
			borderColor = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
		final double rounded = style.value(PName.RoundCorner).asDouble();
		final double diagonalCorner = style.value(PName.DiagonalCorner).asDouble();

		ug.startGroup(Collections.singletonMap(UGroupType.ID, "cluster_" + fullName));

		final Url url = group.getUrl99();
		if (url != null)
			ug.startUrl(url);

		try {
			if (entityPositionsExceptNormal().size() > 0)
				manageEntryExitPoint(ug.getStringBounder());

			if (skinParam.useSwimlanes(umlDiagramType)) {
				drawSwinLinesState(ug, borderColor);
				return;
			}
			final boolean isState = umlDiagramType == UmlDiagramType.STATE;

			if (isState && group.getUSymbol() == null) {
				drawUState(ug, umlDiagramType, rounded, shadowing);
				return;
			}
			PackageStyle packageStyle = group.getPackageStyle();
			if (packageStyle == null)
				packageStyle = skinParam.packageStyle();

			final UStroke stroke = getStrokeInternal(group, style);

			HColor backColor = getBackColor(umlDiagramType, style);
			backColor = getBackColor(backColor, group.getStereotype(), umlDiagramType.getStyleName(),
					group.getUSymbol(), skinParam.getCurrentStyleBuilder(), skinParam.getIHtmlColorSet());

			final ClusterDecoration decoration = new ClusterDecoration(packageStyle, group.getUSymbol(),
					clusterHeader.getTitle(), clusterHeader.getStereo(), clusterPosition, stroke);
			decoration.drawU(ug, backColor, borderColor, shadowing, rounded,
					skinParam.getHorizontalAlignment(AlignmentParam.packageTitleAlignment, null, false, null),
					skinParam.getStereotypeAlignment(), diagonalCorner);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (url != null)
				ug.closeUrl();
			ug.closeGroup();
		}
	}

	EntityImageNoteLink getCucaNote(Position position) {
		final List<CucaNote> notes = getGroup().getNotes(position);
		if (notes.size() == 0)
			return null;
		final CucaNote note = notes.get(0);
		return new EntityImageNoteLink(note.getDisplay(), note.getColors(), skinParam,
				skinParam.getCurrentStyleBuilder());
	}

	static public UStroke getStrokeInternal(IGroup group, Style style) {
		final Colors colors = group.getColors();
		if (colors.getSpecificLineStroke() != null)
			return colors.getSpecificLineStroke();

		return style.getStroke();
	}

	void manageEntryExitPoint(StringBounder stringBounder) {
		final Collection<ClusterPosition> insides = new ArrayList<>();
		final List<XPoint2D> points = new ArrayList<>();
		for (SvekNode sh : nodes)
			if (sh.getEntityPosition() == EntityPosition.NORMAL)
				insides.add(sh.getClusterPosition());
			else
				points.add(sh.getClusterPosition().getPointCenter());

		for (Cluster in : children)
			insides.add(in.getClusterPosition());

		final FrontierCalculator frontierCalculator = new FrontierCalculator(getClusterPosition(), insides, points);
		if (getTitleAndAttributeWidth() > 0 && getTitleAndAttributeHeight() > 0)
			frontierCalculator.ensureMinWidth(getTitleAndAttributeWidth() + 10);

		this.clusterPosition = frontierCalculator.getSuggestedPosition();

		final double widthTitle = clusterHeader.getTitle().calculateDimension(stringBounder).getWidth();
		final double minX = clusterPosition.getMinX();
		final double minY = clusterPosition.getMinY();
		this.xyTitle = new XPoint2D(minX + ((clusterPosition.getWidth() - widthTitle) / 2), minY + IEntityImage.MARGIN);
	}

	private void drawSwinLinesState(UGraphic ug, HColor borderColor) {
		clusterHeader.getTitle().drawU(ug.apply(UTranslate.dx(xyTitle.x)));

		final ULine line = ULine.vline(clusterPosition.getHeight());
		ug = ug.apply(borderColor);
		ug.apply(UTranslate.dx(clusterPosition.getMinX())).draw(line);
		ug.apply(UTranslate.dx(clusterPosition.getMaxX())).draw(line);

	}

	// GroupPngMakerState

	private void drawUState(UGraphic ug, UmlDiagramType umlDiagramType, double rounded, double shadowing) {
		final XDimension2D total = clusterPosition.getDimension();
		final double suppY = clusterHeader.getTitle().calculateDimension(ug.getStringBounder()).getHeight()
				+ IEntityImage.MARGIN;

		HColor borderColor = group.getColors().getColor(ColorType.LINE);
		if (borderColor == null)
			borderColor = EntityImageStateCommon.getStyleState(group, skinParam).value(PName.LineColor)
					.asColor(skinParam.getIHtmlColorSet());

		HColor backColor = group.getColors().getColor(ColorType.BACK);
		if (backColor == null)
			backColor = EntityImageStateCommon.getStyleState(group, skinParam).value(PName.BackGroundColor)
					.asColor(skinParam.getIHtmlColorSet());

		final HColor imgBackcolor = EntityImageStateCommon.getStyleStateBody(group, skinParam)
				.value(PName.BackGroundColor).asColor(skinParam.getIHtmlColorSet());

		final TextBlock attribute = ((EntityImp) group).getStateHeader(skinParam);
		final double attributeHeight = attribute.calculateDimension(ug.getStringBounder()).getHeight();
		if (total.getWidth() == 0) {
			System.err.println("Cluster::drawUState issue");
			return;
		}

		UStroke stroke = group.getColors().getSpecificLineStroke();
		if (stroke == null)
			stroke = EntityImageStateCommon.getStyleState(group, skinParam).getStroke();

		final RoundedContainer r = new RoundedContainer(total, suppY,
				attributeHeight + (attributeHeight > 0 ? IEntityImage.MARGIN : 0), borderColor, backColor, imgBackcolor,
				stroke, rounded, shadowing);
		r.drawU(ug.apply(clusterPosition.getPosition()));

		clusterHeader.getTitle().drawU(ug.apply(new UTranslate(xyTitle)));

		if (attributeHeight > 0)
			attribute.drawU(ug.apply(new UTranslate(clusterPosition.getMinX() + IEntityImage.MARGIN,
					clusterPosition.getMinY() + suppY + IEntityImage.MARGIN / 2.0)));

		final Stereotype stereotype = group.getStereotype();
		final boolean withSymbol = stereotype != null && stereotype.isWithOOSymbol();
		if (withSymbol)
			EntityImageState.drawSymbol(ug.apply(borderColor), clusterPosition.getMaxX(), clusterPosition.getMaxY());

	}

	public void setPosition(XPoint2D min, XPoint2D max) {
		this.clusterPosition = new ClusterPosition(min, max);
	}

	public boolean printCluster1(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder) {
		final List<SvekNode> tmp = getNodesOrderedTop(lines);
		if (tmp.size() == 0)
			return false;
		for (SvekNode node : tmp)
			node.appendShape(sb, stringBounder);
		return true;

	}

	public SvekNode printCluster2(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder,
			DotMode dotMode, GraphvizVersion graphvizVersion, UmlDiagramType type) {

		SvekNode added = null;
		for (SvekNode node : getNodesOrderedWithoutTop(lines)) {
			node.appendShape(sb, stringBounder);
			added = node;
		}

		if (skinParam.useRankSame() && dotMode != DotMode.NO_LEFT_RIGHT_AND_XLABEL
				&& graphvizVersion.ignoreHorizontalLinks() == false)
			appendRankSame(sb, lines);

		for (Cluster child : getChildren())
			child.printInternal(sb, lines, stringBounder, dotMode, graphvizVersion, type);

		return added;
	}

	public void printCluster3_forKermor(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder,
			DotMode dotMode, GraphvizVersion graphvizVersion, UmlDiagramType type) {
		final List<SvekNode> tmp = getNodes(EntityPosition.getNormals());

		if (tmp.size() == 0) {
			sb.append(getClusterId() + "empty [shape=point,label=\"\"];");
			SvekUtils.println(sb);
		} else
			for (SvekNode node : tmp) {
				node.appendShape(sb, stringBounder);
			}

		for (Cluster child : getChildren())
			child.printInternal(sb, lines, stringBounder, dotMode, graphvizVersion, type);
	}

	private void printInternal(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder,
			DotMode dotMode, GraphvizVersion graphvizVersion, UmlDiagramType type) {
		new ClusterDotString(this, skinParam).printInternal(sb, lines, stringBounder, dotMode, graphvizVersion, type);
	}

	private void appendRankSame(StringBuilder sb, Collection<SvekLine> lines) {
		for (String same : getRankSame(lines)) {
			sb.append(same);
			SvekUtils.println(sb);
		}
	}

	private Set<String> getRankSame(Collection<SvekLine> lines) {
		final Set<String> rankSame = new HashSet<>();
		for (SvekLine l : lines) {
			if (l.hasEntryPoint())
				continue;

			final String startUid = l.getStartUidPrefix();
			final String endUid = l.getEndUidPrefix();
			if (isInCluster(startUid) && isInCluster(endUid)) {
				final String same = l.rankSame();
				if (same != null)
					rankSame.add(same);

			}
		}
		return rankSame;
	}

	private boolean isInCluster(String uid) {
		for (SvekNode node : nodes)
			if (node.getUid().equals(uid))
				return true;

		return false;
	}

	public String getClusterId() {
		return "cluster" + color;
	}

	static String getSpecialPointId(IEntity group) {
		return CENTER_ID + group.getUid();
	}

	String getMinPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type))
			return "minPoint" + color;

		return null;
	}

	String getMaxPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type))
			return "maxPoint" + color;

		return null;
	}

	public boolean isLabel() {
		return getTitleAndAttributeHeight() > 0 && getTitleAndAttributeWidth() > 0;
	}

	int getColor() {
		return color;
	}

	int getTitleColor() {
		return colorTitle;
	}

	private final HColor getBackColor(UmlDiagramType umlDiagramType, Style style) {
		if (EntityUtils.groupRoot(group))
			return null;

		final HColor result = group.getColors().getColor(ColorType.BACK);
		if (result != null)
			return result;

		final Stereotype stereo = group.getStereotype();

		return style.value(PName.BackGroundColor).asColor(skinParam.getIHtmlColorSet());

//		final USymbol sym = group.getUSymbol() == null ? USymbols.PACKAGE : group.getUSymbol();
//		final ColorParam backparam = umlDiagramType == UmlDiagramType.ACTIVITY ? ColorParam.partitionBackground
//				: sym.getColorParamBack();
//		final HColor c1 = skinParam.getHtmlColor(backparam, stereo, false);
//		if (c1 != null)
//			return c1;
//
//		if (parentCluster == null)
//			return null;
//
//		return parentCluster.getBackColor(umlDiagramType, style);
	}

	boolean isClusterOf(IEntity ent) {
		if (ent.isGroup() == false)
			return false;

		return group == ent;
	}

	public static HColor getBackColor(HColor backColor, Stereotype stereotype, SName styleName, USymbol symbol,
			StyleBuilder styleBuilder, HColorSet colorSet) {

		final Style style = getDefaultStyleDefinition(styleName, symbol).getMergedStyle(styleBuilder);
		if (backColor == null)
			backColor = style.value(PName.BackGroundColor).asColor(colorSet);

		if (backColor == null || backColor.equals(HColors.transparent()))
			backColor = HColors.transparent();

		return backColor;
	}

	double checkFolderPosition(XPoint2D pt, StringBounder stringBounder) {
		if (getClusterPosition().isPointJustUpper(pt)) {

			final XDimension2D dimTitle = clusterHeader.getTitle().calculateDimension(stringBounder);

			if (pt.getX() < getClusterPosition().getMinX() + dimTitle.getWidth())
				return 0;

			return getClusterPosition().getMinY() - pt.getY() + dimTitle.getHeight();
		}
		return 0;
	}

	public final int getColorNoteTop() {
		return colorNoteTop;
	}

	public final int getColorNoteBottom() {
		return colorNoteBottom;
	}

	public XDimension2D getTitleDimension(StringBounder stringBounder) {
		return clusterHeader.getTitle().calculateDimension(stringBounder);
	}

	// public XPoint2D projection(double x, double y) {
	// final double v1 = Math.abs(minX - x);
	// final double v2 = Math.abs(maxX - x);
	// final double v3 = Math.abs(minY - y);
	// final double v4 = Math.abs(maxY - y);
	// if (v1 <= v2 && v1 <= v3 && v1 <= v4) {
	// return new XPoint2D(minX, y);
	// }
	// if (v2 <= v1 && v2 <= v3 && v2 <= v4) {
	// return new XPoint2D(maxX, y);
	// }
	// if (v3 <= v1 && v3 <= v2 && v3 <= v4) {
	// return new XPoint2D(x, minY);
	// }
	// if (v4 <= v1 && v4 <= v1 && v4 <= v3) {
	// return new XPoint2D(x, maxY);
	// }
	// throw new IllegalStateException();
	// }

}
