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
 * Contribution :  Hisashi Miyashita
 */
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
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
import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizVersion;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.posimo.Moveable;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.image.EntityImageState;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorBackground;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.utils.UniqueSequence;

public class Cluster implements Moveable {

	private static final String RANK_SAME = "same";
	private static final String RANK_SOURCE = "source";
	private static final String RANK_SINK = "sink";
	private static final String ID_EE = "ee";
	public final static String CENTER_ID = "za";

	private final Cluster parentCluster;
	private final IGroup group;
	private final List<SvekNode> nodes = new ArrayList<>();
	private final List<Cluster> children = new ArrayList<>();
	private final int color;
	private final int colorTitle;
	private final ISkinParam skinParam;

	private int titleAndAttributeWidth;
	private int titleAndAttributeHeight;
	private TextBlock ztitle;
	private TextBlock zstereo;

	private double xTitle;
	private double yTitle;

	private double minX;
	private double minY;
	private double maxX;
	private double maxY;

	public void moveSvek(double deltaX, double deltaY) {
		this.xTitle += deltaX;
		this.minX += deltaX;
		this.maxX += deltaX;
		this.yTitle += deltaY;
		this.minY += deltaY;
		this.maxY += deltaY;

	}

	private Set<EntityPosition> entityPositionsExceptNormal() {
		final Set<EntityPosition> result = EnumSet.<EntityPosition>noneOf(EntityPosition.class);
		for (SvekNode sh : nodes) {
			if (sh.getEntityPosition() != EntityPosition.NORMAL) {
				result.add(sh.getEntityPosition());
			}
		}
		return Collections.unmodifiableSet(result);
	}

	public Cluster(ColorSequence colorSequence, ISkinParam skinParam, IGroup root) {
		this(null, colorSequence, skinParam, root);
	}

	private ColorParam border;

	private Cluster(Cluster parentCluster, ColorSequence colorSequence, ISkinParam skinParam, IGroup group) {
		if (group == null) {
			throw new IllegalStateException();
		}
		this.parentCluster = parentCluster;
		this.group = group;
		if (group.getUSymbol() != null) {
			border = group.getUSymbol().getColorParamBorder();
		}
		this.color = colorSequence.getValue();
		this.colorTitle = colorSequence.getValue();
		this.skinParam = group.getColors(skinParam).mute(skinParam);
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

	private List<SvekNode> getNodesOrderedTop(Collection<SvekLine> lines) {
		final List<SvekNode> firsts = new ArrayList<>();
		final Set<String> tops = new HashSet<>();
		final Map<String, SvekNode> shs = new HashMap<String, SvekNode>();

		for (final Iterator<SvekNode> it = nodes.iterator(); it.hasNext();) {
			final SvekNode node = it.next();
			shs.put(node.getUid(), node);
			if (node.isTop() && node.getEntityPosition() == EntityPosition.NORMAL) {
				firsts.add(node);
				tops.add(node.getUid());
			}
		}

		for (SvekLine l : lines) {
			if (tops.contains(l.getStartUidPrefix())) {
				final SvekNode sh = shs.get(l.getEndUidPrefix());
				if (sh != null && sh.getEntityPosition() == EntityPosition.NORMAL) {
					firsts.add(0, sh);
				}
			}

			if (l.isInverted()) {
				final SvekNode sh = shs.get(l.getStartUidPrefix());
				if (sh != null && sh.getEntityPosition() == EntityPosition.NORMAL) {
					firsts.add(0, sh);
				}
			}
		}

		return firsts;
	}

	private List<SvekNode> getNodesOrderedWithoutTop(Collection<SvekLine> lines) {
		final List<SvekNode> all = new ArrayList<>(nodes);
		final Set<String> tops = new HashSet<>();
		final Map<String, SvekNode> shs = new HashMap<String, SvekNode>();

		for (final Iterator<SvekNode> it = all.iterator(); it.hasNext();) {
			final SvekNode sh = it.next();
			if (sh.getEntityPosition() != EntityPosition.NORMAL) {
				it.remove();
				continue;
			}
			shs.put(sh.getUid(), sh);
			if (sh.isTop()) {
				tops.add(sh.getUid());
				it.remove();
			}
		}

		for (SvekLine l : lines) {
			if (tops.contains(l.getStartUidPrefix())) {
				final SvekNode sh = shs.get(l.getEndUidPrefix());
				if (sh != null) {
					all.remove(sh);
				}
			}

			if (l.isInverted()) {
				final SvekNode sh = shs.get(l.getStartUidPrefix());
				if (sh != null) {
					all.remove(sh);
				}
			}
		}

		return all;
	}

	public final List<Cluster> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public Cluster createChild(int titleAndAttributeWidth, int titleAndAttributeHeight, TextBlock title,
			TextBlock stereo, ColorSequence colorSequence, ISkinParam skinParam, IGroup g) {
		final Cluster child = new Cluster(this, colorSequence, skinParam, g);
		child.titleAndAttributeWidth = titleAndAttributeWidth;
		child.titleAndAttributeHeight = titleAndAttributeHeight;
		child.ztitle = title;
		child.zstereo = stereo;
		this.children.add(child);
		return child;
	}

	public final Set<IGroup> getGroups() {
		return Collections.singleton(group);
	}

	public final int getTitleAndAttributeWidth() {
		return titleAndAttributeWidth;
	}

	public final int getTitleAndAttributeHeight() {
		return titleAndAttributeHeight;
	}

	public double getWidth() {
		return maxX - minX;
	}

	public double getMinX() {
		return minX;
	}

	public ClusterPosition getClusterPosition() {
		return new ClusterPosition(minX, minY, maxX, maxY);
	}

	public void setTitlePosition(double x, double y) {
		this.xTitle = x;
		this.yTitle = y;
	}

	private static HColor getColor(ColorParam colorParam, ISkinParam skinParam, Stereotype stereotype) {
		return SkinParamUtils.getColor(skinParam, stereotype, colorParam);
	}

	static public StyleSignature getDefaultStyleDefinition(SName styleName) {
		return StyleSignature.of(SName.root, SName.element, styleName, SName.group);
	}

	public void drawU(UGraphic ug, UStroke strokeForState, UmlDiagramType umlDiagramType, ISkinParam skinParam2) {
		if (group.isHidden()) {
			return;
		}
		final String fullName = group.getCodeGetName();
		if (fullName.startsWith("##") == false) {
			ug.draw(new UComment("cluster " + fullName));
		}
		HColor borderColor;
		if (UseStyle.useBetaStyle()) {
			final Style style = getDefaultStyleDefinition(umlDiagramType.getStyleName())
					.getMergedStyle(skinParam.getCurrentStyleBuilder());
			borderColor = style.value(PName.LineColor).asColor(skinParam2.getThemeStyle(),
					skinParam2.getIHtmlColorSet());

		} else {
			if (umlDiagramType == UmlDiagramType.STATE) {
				borderColor = getColor(ColorParam.stateBorder, skinParam, group.getStereotype());
			} else if (umlDiagramType == UmlDiagramType.ACTIVITY) {
				borderColor = getColor(ColorParam.packageBorder, skinParam, group.getStereotype());
			} else {
				borderColor = getColor(ColorParam.packageBorder, skinParam, group.getStereotype());
			}
		}

		final Url url = group.getUrl99();
		if (url != null) {
			ug.startUrl(url);
		}
		try {
			if (entityPositionsExceptNormal().size() > 0) {
				manageEntryExitPoint(ug.getStringBounder());
			}
			if (skinParam.useSwimlanes(umlDiagramType)) {
				drawSwinLinesState(ug, borderColor);
				return;
			}
			final boolean isState = umlDiagramType == UmlDiagramType.STATE;
			if (isState) {
				if (group.getColors(skinParam).getSpecificLineStroke() != null) {
					strokeForState = group.getColors(skinParam).getSpecificLineStroke();
				}
				if (group.getColors(skinParam).getColor(ColorType.LINE) != null) {
					borderColor = group.getColors(skinParam).getColor(ColorType.LINE);
				}
				drawUState(ug, borderColor, skinParam2, strokeForState, umlDiagramType);
				return;
			}
			PackageStyle packageStyle = group.getPackageStyle();
			if (packageStyle == null) {
				packageStyle = skinParam2.packageStyle();
			}
			if (border != null) {
				final HColor tmp = skinParam2.getHtmlColor(border, group.getStereotype(), false);
				if (tmp != null) {
					borderColor = tmp;
				}
			}

			final double shadowing;
			final UStroke stroke;
			if (UseStyle.useBetaStyle()) {
				final Style style = getDefaultStyleDefinition(umlDiagramType.getStyleName())
						.getMergedStyle(skinParam.getCurrentStyleBuilder());
				shadowing = style.value(PName.Shadowing).asDouble();
				stroke = style.getStroke();
			} else {
				if (group.getUSymbol() == null) {
					shadowing = skinParam2.shadowing2(group.getStereotype(), USymbol.PACKAGE.getSkinParameter()) ? 3
							: 0;
				} else {
					shadowing = skinParam2.shadowing2(group.getStereotype(), group.getUSymbol().getSkinParameter()) ? 3
							: 0;
				}
				stroke = getStrokeInternal(group, skinParam2);
			}
			HColor backColor = getBackColor(umlDiagramType);
			backColor = getBackColor(backColor, skinParam2, group.getStereotype(), umlDiagramType.getStyleName());
			if (ztitle != null || zstereo != null) {
				final double roundCorner = group.getUSymbol() == null ? 0
						: group.getUSymbol().getSkinParameter().getRoundCorner(skinParam, group.getStereotype());

				final ClusterDecoration decoration = new ClusterDecoration(packageStyle, group.getUSymbol(), ztitle,
						zstereo, minX, minY, maxX, maxY, stroke);
				decoration.drawU(ug, backColor, borderColor, shadowing, roundCorner,
						skinParam2.getHorizontalAlignment(AlignmentParam.packageTitleAlignment, null, false, null),
						skinParam2.getStereotypeAlignment());
				return;
			}
			final URectangle rect = new URectangle(maxX - minX, maxY - minY);
			rect.setDeltaShadow(shadowing);
			ug = ug.apply(backColor.bg()).apply(borderColor);
			ug.apply(new UStroke(2)).apply(new UTranslate(minX, minY)).draw(rect);

		} finally {
			if (url != null) {
				ug.closeUrl();
			}
		}

	}

	static public UStroke getStrokeInternal(IGroup group, ISkinParam skinParam) {
		final Colors colors = group.getColors(skinParam);
		if (colors.getSpecificLineStroke() != null) {
			return colors.getSpecificLineStroke();
		}
		if (group.getUSymbol() != null && group.getUSymbol() != USymbol.PACKAGE) {
			return group.getUSymbol().getSkinParameter().getStroke(skinParam, group.getStereotype());
		}
		return GeneralImageBuilder.getForcedStroke(group.getStereotype(), skinParam);
//		UStroke stroke = skinParam.getThickness(LineParam.packageBorder, group.getStereotype());
//		if (stroke == null) {
//			stroke = new UStroke(1.5);
//		}
//		return stroke;
	}

	public void manageEntryExitPoint(StringBounder stringBounder) {
		final Collection<ClusterPosition> insides = new ArrayList<>();
		final List<Point2D> points = new ArrayList<>();
		for (SvekNode sh : nodes) {
			if (sh.getEntityPosition() == EntityPosition.NORMAL) {
				insides.add(sh.getClusterPosition());
			} else {
				points.add(sh.getClusterPosition().getPointCenter());
			}
		}
		for (Cluster in : children) {
			insides.add(in.getClusterPosition());
		}
		final FrontierCalculator frontierCalculator = new FrontierCalculator(getClusterPosition(), insides, points);
		if (titleAndAttributeHeight > 0 && titleAndAttributeWidth > 0) {
			frontierCalculator.ensureMinWidth(titleAndAttributeWidth + 10);
		}
		final ClusterPosition forced = frontierCalculator.getSuggestedPosition();
		xTitle += ((forced.getMinX() - minX) + (forced.getMaxX() - maxX)) / 2;
		minX = forced.getMinX();
		minY = forced.getMinY();
		maxX = forced.getMaxX();
		maxY = forced.getMaxY();
		yTitle = minY + IEntityImage.MARGIN;
		final double widthTitle = ztitle.calculateDimension(stringBounder).getWidth();
		xTitle = minX + ((maxX - minX - widthTitle) / 2);
	}

	private void drawSwinLinesState(UGraphic ug, HColor borderColor) {
		if (ztitle != null) {
			ztitle.drawU(ug.apply(UTranslate.dx(xTitle)));
		}
		final ULine line = ULine.vline(maxY - minY);
		ug = ug.apply(borderColor);
		ug.apply(UTranslate.dx(minX)).draw(line);
		ug.apply(UTranslate.dx(maxX)).draw(line);

	}

	private HColor getColor(ISkinParam skinParam, ColorParam colorParam, Stereotype stereo) {
		return new Rose().getHtmlColor(skinParam, stereo, colorParam);
	}

	private void drawUState(UGraphic ug, HColor borderColor, ISkinParam skinParam2, UStroke stroke,
			UmlDiagramType umlDiagramType) {
		final Dimension2D total = new Dimension2DDouble(maxX - minX, maxY - minY);
		final double suppY;
		if (ztitle == null) {
			suppY = 0;
		} else {
			suppY = ztitle.calculateDimension(ug.getStringBounder()).getHeight() + IEntityImage.MARGIN
					+ IEntityImage.MARGIN_LINE;
		}

		HColor stateBack = getBackColor(umlDiagramType);
		if (stateBack == null) {
			stateBack = getColor(skinParam2, ColorParam.stateBackground, group.getStereotype());
		}
		final HColor background = getColor(skinParam2, ColorParam.background, null);
		final Style style = getStyle(FontParam.STATE_ATTRIBUTE, skinParam2);
		final TextBlock attribute = GeneralImageBuilder.stateHeader(group, style, skinParam2);
		final double attributeHeight = attribute.calculateDimension(ug.getStringBounder()).getHeight();
		if (total.getWidth() == 0) {
			System.err.println("Cluster::drawUState issue");
			return;
		}
		final RoundedContainer r = new RoundedContainer(total, suppY,
				attributeHeight + (attributeHeight > 0 ? IEntityImage.MARGIN : 0), borderColor, stateBack, background,
				stroke);
		r.drawU(ug.apply(new UTranslate(minX, minY)), skinParam2.shadowing(group.getStereotype()));

		if (ztitle != null) {
			ztitle.drawU(ug.apply(new UTranslate(xTitle, yTitle)));
		}

		if (attributeHeight > 0) {
			attribute.drawU(
					ug.apply(new UTranslate(minX + IEntityImage.MARGIN, minY + suppY + IEntityImage.MARGIN / 2.0)));
		}

		final Stereotype stereotype = group.getStereotype();
		final boolean withSymbol = stereotype != null && stereotype.isWithOOSymbol();
		if (withSymbol) {
			EntityImageState.drawSymbol(ug.apply(borderColor), maxX, maxY);
		}

	}

	public void setPosition(double minX, double minY, double maxX, double maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	private Style getStyle(FontParam fontParam, ISkinParam skinParam) {
		return fontParam.getStyleDefinition(SName.stateDiagram).getMergedStyle(skinParam.getCurrentStyleBuilder());
	}

	private boolean isThereALinkFromOrToGroup(Collection<SvekLine> lines) {
		for (SvekLine line : lines) {
			if (line.isLinkFromOrTo(group)) {
				return true;
			}
		}
		return false;
	}

	public void printCluster1(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder) {
		for (SvekNode node : getNodesOrderedTop(lines)) {
			node.appendShape(sb, stringBounder);
		}
	}

	private List<IShapePseudo> addProtection(List<? extends IShapePseudo> entries, double width) {
		final List<IShapePseudo> result = new ArrayList<>();
		result.add(entries.get(0));
		for (int i = 1; i < entries.size(); i++) {
			// Pseudo space for the label
			result.add(new ShapePseudoImpl("psd" + UniqueSequence.getValue(), width, 5));
			result.add(entries.get(i));
		}
		return result;
	}

	private double getMaxWidthFromLabelForEntryExit(List<? extends IShapePseudo> entries, StringBounder stringBounder) {
		double result = -Double.MAX_VALUE;
		for (IShapePseudo node : entries) {
			final double w = getMaxWidthFromLabelForEntryExit(node, stringBounder);
			if (w > result) {
				result = w;
			}
		}
		return result;
	}

	private double getMaxWidthFromLabelForEntryExit(IShapePseudo node, StringBounder stringBounder) {
		return node.getMaxWidthFromLabelForEntryExit(stringBounder);
	}

	private void printRanks(String rank, List<? extends IShapePseudo> entries, StringBuilder sb,
			StringBounder stringBounder) {
		if (entries.size() > 0) {
			sb.append("{rank=" + rank + ";");
			for (IShapePseudo sh1 : entries) {
				sb.append(sh1.getUid() + ";");
			}
			sb.append("}");
			SvekUtils.println(sb);
			for (IShapePseudo sh2 : entries) {
				sh2.appendShape(sb, stringBounder);
			}
			SvekUtils.println(sb);
			if (hasPort()) {
				boolean arrow = false;
				String node = null;
				for (IShapePseudo sh : entries) {
					if (arrow) {
						sb.append("->");
					}
					arrow = true;
					node = sh.getUid();
					sb.append(node);
				}
				sb.append(';');
				SvekUtils.println(sb);
				sb.append(node + "->" + empty() + ";");
				SvekUtils.println(sb);
			}
		}
	}

	private List<? extends IShapePseudo> withPositionProtected(StringBounder stringBounder,
			Set<EntityPosition> targets) {
		final List<SvekNode> result = withPosition(targets);
		final double maxWith = getMaxWidthFromLabelForEntryExit(result, stringBounder);
		final double naturalSpace = 70;
		if (maxWith > naturalSpace) {
			return addProtection(result, maxWith - naturalSpace);
		}
		return result;
	}

	private List<SvekNode> withPosition(Set<EntityPosition> positions) {
		final List<SvekNode> result = new ArrayList<>();
		for (final Iterator<SvekNode> it = nodes.iterator(); it.hasNext();) {
			final SvekNode sh = it.next();
			if (positions.contains(sh.getEntityPosition())) {
				result.add(sh);
			}
		}
		return result;
	}

	private void printClusterEntryExit(StringBuilder sb, StringBounder stringBounder) {
		printRanks(RANK_SOURCE, withPositionProtected(stringBounder, EntityPosition.getInputs()), sb, stringBounder);
		printRanks(RANK_SAME, withPositionProtected(stringBounder, EntityPosition.getSame()), sb, stringBounder);
		printRanks(RANK_SINK, withPositionProtected(stringBounder, EntityPosition.getOutputs()), sb, stringBounder);
	}

	public SvekNode printCluster2(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder,
			DotMode dotMode, GraphvizVersion graphvizVersion, UmlDiagramType type) {

		SvekNode added = null;
		for (SvekNode node : getNodesOrderedWithoutTop(lines)) {
			node.appendShape(sb, stringBounder);
			added = node;
		}

		if (skinParam.useRankSame() && dotMode != DotMode.NO_LEFT_RIGHT_AND_XLABEL
				&& graphvizVersion.ignoreHorizontalLinks() == false) {
			appendRankSame(sb, lines);
		}

		for (Cluster child : getChildren()) {
			child.printInternal(sb, lines, stringBounder, dotMode, graphvizVersion, type);
		}

		return added;
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
			if (l.hasEntryPoint()) {
				continue;
			}
			final String startUid = l.getStartUidPrefix();
			final String endUid = l.getEndUidPrefix();
			if (isInCluster(startUid) && isInCluster(endUid)) {
				final String same = l.rankSame();
				if (same != null) {
					rankSame.add(same);
				}
			}
		}
		return rankSame;
	}

	public void fillRankMin(Set<String> rankMin) {
		for (SvekNode sh : getNodes()) {
			if (sh.isTop()) {
				rankMin.add(sh.getUid());
			}
		}

		for (Cluster child : getChildren()) {
			child.fillRankMin(rankMin);
		}
	}

	private boolean isInCluster(String uid) {
		for (SvekNode node : nodes) {
			if (node.getUid().equals(uid)) {
				return true;
			}
		}
		return false;
	}

	public String getClusterId() {
		return "cluster" + color;
	}

	public static String getSpecialPointId(IEntity group) {
		return CENTER_ID + group.getUid();
	}

	private boolean protection0(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return false;
		}
		return true;
	}

	private boolean protection1(UmlDiagramType type) {
		if (group.getUSymbol() == USymbol.NODE) {
			return true;
		}
		if (skinParam.useSwimlanes(type)) {
			return false;
		}
		return true;
	}

	public String getMinPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "minPoint" + color;
		}
		return null;
	}

	public String getMaxPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "maxPoint" + color;
		}
		return null;
	}

	private String getSourceInPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "sourceIn" + color;
		}
		return null;
	}

	private String getSinkInPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "sinkIn" + color;
		}
		return null;
	}

	private void printInternal(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder,
			DotMode dotMode, GraphvizVersion graphvizVersion, UmlDiagramType type) {
		final boolean thereALinkFromOrToGroup2 = isThereALinkFromOrToGroup(lines);
		boolean thereALinkFromOrToGroup1 = thereALinkFromOrToGroup2;
		final boolean useProtectionWhenThereALinkFromOrToGroup = graphvizVersion
				.useProtectionWhenThereALinkFromOrToGroup();
		if (useProtectionWhenThereALinkFromOrToGroup == false) {
			thereALinkFromOrToGroup1 = false;
		}
		// final boolean thereALinkFromOrToGroup1 = false;
		if (thereALinkFromOrToGroup1) {
			subgraphClusterNoLabel(sb, "a");
		}
		final Set<EntityPosition> entityPositionsExceptNormal = entityPositionsExceptNormal();
		if (entityPositionsExceptNormal.size() > 0) {
			for (SvekLine line : lines) {
				if (line.isLinkFromOrTo(group)) {
					line.setProjectionCluster(this);
				}
			}
		}
		boolean protection0 = protection0(type);
		boolean protection1 = protection1(type);
		if (entityPositionsExceptNormal.size() > 0 || useProtectionWhenThereALinkFromOrToGroup == false) {
			protection0 = false;
			protection1 = false;
		}
		// if (graphvizVersion.modeSafe()) {
		// protection0 = false;
		// protection1 = false;
		// }
		if (protection0) {
			subgraphClusterNoLabel(sb, "p0");
		}
		sb.append("subgraph " + getClusterId() + " {");
		sb.append("style=solid;");
		sb.append("color=\"" + DotStringFactory.sharp000000(color) + "\";");

		final String label;
		if (isLabel()) {
			final StringBuilder sblabel = new StringBuilder("<");
			SvekLine.appendTable(sblabel, getTitleAndAttributeWidth(), getTitleAndAttributeHeight() - 5, colorTitle);
			sblabel.append(">");
			label = sblabel.toString();
			final HorizontalAlignment align = skinParam.getHorizontalAlignment(AlignmentParam.packageTitleAlignment,
					null, false, null);
			sb.append("labeljust=\"" + align.getGraphVizValue() + "\";");
		} else {
			label = "\"\"";
		}

		if (entityPositionsExceptNormal.size() > 0) {
			printClusterEntryExit(sb, stringBounder);
			if (hasPort()) {
				subgraphClusterNoLabel(sb, ID_EE);
			} else {
				subgraphClusterWithLabel(sb, ID_EE, label);
			}
		} else {
			sb.append("label=" + label + ";");
			SvekUtils.println(sb);
		}

		if (thereALinkFromOrToGroup2) {
			sb.append(getSpecialPointId(group) + " [shape=point,width=.01,label=\"\"];");
		}
		if (thereALinkFromOrToGroup1) {
			subgraphClusterNoLabel(sb, "i");
		}
		if (protection1) {
			subgraphClusterNoLabel(sb, "p1");
		}
		if (skinParam.useSwimlanes(type)) {
			sb.append("{rank = source; ");
			sb.append(getSourceInPoint(type));
			sb.append(" [shape=point,width=.01,label=\"\"];");
			sb.append(getMinPoint(type) + "->" + getSourceInPoint(type) + "  [weight=999];");
			sb.append("}");
			SvekUtils.println(sb);
			sb.append("{rank = sink; ");
			sb.append(getSinkInPoint(type));
			sb.append(" [shape=point,width=.01,label=\"\"];");
			sb.append("}");
			sb.append(getSinkInPoint(type) + "->" + getMaxPoint(type) + "  [weight=999];");
			SvekUtils.println(sb);
		}
		SvekUtils.println(sb);
		printCluster1(sb, lines, stringBounder);

		final SvekNode added = printCluster2(sb, lines, stringBounder, dotMode, graphvizVersion, type);
		if (entityPositionsExceptNormal.size() > 0) {
			if (hasPort()) {
				sb.append(empty() + " [shape=rect,width=.01,height=.01,label=");
				sb.append(label);
				sb.append("];");
			} else if (added == null) {
				sb.append(empty() + " [shape=point,width=.01,label=\"\"];");
			}
			SvekUtils.println(sb);
		}

		sb.append("}");
		if (protection1) {
			sb.append("}");
		}
		if (thereALinkFromOrToGroup1) {
			sb.append("}");
			sb.append("}");
		}
		if (entityPositionsExceptNormal.size() > 0) {
			sb.append("}");
		}
		if (protection0) {
			sb.append("}");
		}
		SvekUtils.println(sb);
	}

	private boolean hasPort() {
		for (EntityPosition pos : entityPositionsExceptNormal()) {
			if (pos.isPort()) {
				return true;
			}
		}
		return false;
	}

	private String empty() {
		// return "empty" + color;
		// We use the same node with one for thereALinkFromOrToGroup2 as an empty
		// because we cannot put a new node in the nested inside of the cluster
		// if thereALinkFromOrToGroup2 is enabled.
		return getSpecialPointId(group);
	}

	public boolean isLabel() {
		return getTitleAndAttributeHeight() > 0 && getTitleAndAttributeWidth() > 0;
	}

	private void subgraphClusterNoLabel(StringBuilder sb, String id) {
		subgraphClusterWithLabel(sb, id, "\"\"");
	}

	private void subgraphClusterWithLabel(StringBuilder sb, String id, String label) {
		sb.append("subgraph " + getClusterId() + id + " {");
		sb.append("label=" + label + ";");
	}

	public int getColor() {
		return color;
	}

	public int getTitleColor() {
		return colorTitle;
	}

	private final HColor getBackColor(final UmlDiagramType umlDiagramType) {
		if (EntityUtils.groupRoot(group)) {
			return null;
		}
		final HColor result = group.getColors(skinParam).getColor(ColorType.BACK);
		if (result != null) {
			return result;
		}
		final Stereotype stereo = group.getStereotype();
		final USymbol sym = group.getUSymbol() == null ? USymbol.PACKAGE : group.getUSymbol();
		final ColorParam backparam = umlDiagramType == UmlDiagramType.ACTIVITY ? ColorParam.partitionBackground
				: sym.getColorParamBack();
		final HColor c1 = skinParam.getHtmlColor(backparam, stereo, false);
		if (c1 != null) {
			return c1;
		}
		if (parentCluster == null) {
			return null;
		}
		return parentCluster.getBackColor(umlDiagramType);
	}

	public boolean isClusterOf(IEntity ent) {
		if (ent.isGroup() == false) {
			return false;
		}
		return group == ent;
	}

	public static HColor getBackColor(HColor backColor, ISkinParam skinParam, Stereotype stereotype, SName styleName) {
		if (UseStyle.useBetaStyle()) {
			final Style style = getDefaultStyleDefinition(styleName).getMergedStyle(skinParam.getCurrentStyleBuilder());
			if (backColor == null) {
				backColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
						skinParam.getIHtmlColorSet());
			}
			if (backColor == null || backColor.equals(HColorUtils.transparent())) {
				backColor = new HColorBackground(skinParam.getBackgroundColor(false));
			}
			return backColor;
		}
		if (backColor == null) {
			backColor = skinParam.getHtmlColor(ColorParam.packageBackground, stereotype, false);
		}
		if (backColor == null) {
			backColor = skinParam.getHtmlColor(ColorParam.background, stereotype, false);
		}
		if (backColor == null
				|| backColor.equals(HColorUtils.transparent()) /* || stateBack instanceof HtmlColorTransparent */) {
			final HColor tmp = skinParam.getBackgroundColor(false);
			backColor = new HColorBackground(tmp);
		}
		return backColor;
	}

	public double checkFolderPosition(Point2D pt, StringBounder stringBounder) {
		if (getClusterPosition().isPointJustUpper(pt)) {
			if (ztitle == null) {
				return 0;
			}
			final Dimension2D dimTitle = ztitle.calculateDimension(stringBounder);

			if (pt.getX() < getClusterPosition().getMinX() + dimTitle.getWidth()) {
				return 0;
			}

			return getClusterPosition().getMinY() - pt.getY() + dimTitle.getHeight();
		}
		return 0;
	}

	// public Point2D projection(double x, double y) {
	// final double v1 = Math.abs(minX - x);
	// final double v2 = Math.abs(maxX - x);
	// final double v3 = Math.abs(minY - y);
	// final double v4 = Math.abs(maxY - y);
	// if (v1 <= v2 && v1 <= v3 && v1 <= v4) {
	// return new Point2D.Double(minX, y);
	// }
	// if (v2 <= v1 && v2 <= v3 && v2 <= v4) {
	// return new Point2D.Double(maxX, y);
	// }
	// if (v3 <= v1 && v3 <= v2 && v3 <= v4) {
	// return new Point2D.Double(x, minY);
	// }
	// if (v4 <= v1 && v4 <= v1 && v4 <= v3) {
	// return new Point2D.Double(x, maxY);
	// }
	// throw new IllegalStateException();
	// }

}
