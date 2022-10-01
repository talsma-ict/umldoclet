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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizVersion;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbols;

public class ClusterDotStringKermor {

	private final Cluster cluster;
	private final ISkinParam skinParam;
	private static final String ID_EE = "ee";

	public ClusterDotStringKermor(Cluster cluster, ISkinParam skinParam) {
		this.cluster = cluster;
		this.skinParam = skinParam;
	}

	void printInternal(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder, DotMode dotMode,
			GraphvizVersion graphvizVersion, UmlDiagramType type) {

		final Set<EntityPosition> entityPositionsExceptNormal = entityPositionsExceptNormal();
		if (entityPositionsExceptNormal.size() > 0)
			for (SvekLine line : lines)
				if (line.isLinkFromOrTo(cluster.getGroup()))
					line.setProjectionCluster(cluster);

		final boolean useAlphaAndBeta = useAlphaAndBeta();

		if (useAlphaAndBeta) {
			sb.append("subgraph " + cluster.getClusterId() + "alpha {");
			SvekUtils.println(sb);

			sb.append("color=\"#FFFF00\";");
			SvekUtils.println(sb);
			final TextBlock noteTop = cluster.getCucaNote(Position.TOP);
			if (noteTop == null) {
				sb.append("label=\"\";");
				SvekUtils.println(sb);
			} else {
				final XDimension2D dim = noteTop.calculateDimension(stringBounder);
				final StringBuilder sblabel = new StringBuilder("<");
				SvekLine.appendTable(sblabel, (int) dim.getWidth(), (int) dim.getHeight(), cluster.getColorNoteTop());
				sblabel.append(">");
				sb.append("label=" + sblabel + ";");
				SvekUtils.println(sb);
			}

		}

		SvekUtils.println(sb);
		sb.append("subgraph " + cluster.getClusterId() + "beta {");
		SvekUtils.println(sb);
		final TextBlock noteBottom = cluster.getCucaNote(Position.BOTTOM);

		sb.append("labelloc=\"b\";");
		SvekUtils.println(sb);
		sb.append("color=\"#FFFF00\";");
		SvekUtils.println(sb);
		if (noteBottom == null) {
			sb.append("label=\"\";");
			SvekUtils.println(sb);
		} else {
			final XDimension2D dim = noteBottom.calculateDimension(stringBounder);
			final StringBuilder sblabel = new StringBuilder("<");
			SvekLine.appendTable(sblabel, (int) dim.getWidth(), (int) dim.getHeight(), cluster.getColorNoteBottom());
			sblabel.append(">");
			sb.append("label=" + sblabel + ";");
			SvekUtils.println(sb);
		}

		SvekUtils.println(sb);
		printRanks(Cluster.RANK_SOURCE, cluster.getNodes(EntityPosition.getInputs()), sb, stringBounder);
		SvekUtils.println(sb);

		sb.append("subgraph " + cluster.getClusterId() + "gamma {");
		SvekUtils.println(sb);
		sb.append("labelloc=\"t\";");
		SvekUtils.println(sb);
		sb.append("style=solid;");
		SvekUtils.println(sb);
		sb.append("color=\"" + StringUtils.sharp000000(cluster.getColor()) + "\";");

		final String label;
		if (cluster.isLabel()) {
			final StringBuilder sblabel = new StringBuilder("<");
			SvekLine.appendTable(sblabel, cluster.getTitleAndAttributeWidth(), cluster.getTitleAndAttributeHeight() - 5,
					cluster.getTitleColor());
			sblabel.append(">");
			label = sblabel.toString();
			final HorizontalAlignment align = skinParam.getHorizontalAlignment(AlignmentParam.packageTitleAlignment,
					null, false, null);
			sb.append("labeljust=\"" + align.getGraphVizValue() + "\";");
		} else {
			label = "\"\"";
		}

		sb.append("label=" + label + ";");
		SvekUtils.println(sb);

		cluster.printCluster3_forKermor(sb, lines, stringBounder, dotMode, graphvizVersion, type);

		SvekUtils.println(sb);
		printRanks(Cluster.RANK_SINK, cluster.getNodes(EntityPosition.getOutputs()), sb, stringBounder);
		SvekUtils.println(sb);

		sb.append("}");
		sb.append("}");
		if (useAlphaAndBeta) {
			sb.append("}");
		}

		SvekUtils.println(sb);

	}

	private boolean useAlphaAndBeta() {
		if (cluster.getGroup().getNotes(Position.TOP).size() > 0)
			return true;
		if (cluster.getGroup().getNotes(Position.BOTTOM).size() > 0)
			return true;

		return false;
	}

	private String getSourceInPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type))
			return "sourceIn" + cluster.getColor();

		return null;
	}

	private String getSinkInPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type))
			return "sinkIn" + cluster.getColor();

		return null;
	}

	private String empty() {
		// return "empty" + color;
		// We use the same node with one for thereALinkFromOrToGroup2 as an empty
		// because we cannot put a new node in the nested inside of the cluster
		// if thereALinkFromOrToGroup2 is enabled.
		return Cluster.getSpecialPointId(cluster.getGroup());
	}

	private boolean hasPort() {
		for (EntityPosition pos : entityPositionsExceptNormal())
			if (pos.isPort())
				return true;

		return false;
	}

	private Set<EntityPosition> entityPositionsExceptNormal() {
		final Set<EntityPosition> result = EnumSet.<EntityPosition>noneOf(EntityPosition.class);
		for (SvekNode sh : cluster.getNodes())
			if (sh.getEntityPosition() != EntityPosition.NORMAL)
				result.add(sh.getEntityPosition());

		return Collections.unmodifiableSet(result);
	}

	private void subgraphClusterNoLabel(StringBuilder sb, String id) {
		subgraphClusterWithLabel(sb, id, "\"\"");
	}

	private void subgraphClusterWithLabel(StringBuilder sb, String id, String label) {
		sb.append("subgraph " + cluster.getClusterId() + id + " {");
		sb.append("label=" + label + ";");
	}

//	private void printClusterEntryExit(StringBuilder sb, StringBounder stringBounder) {
//		printRanks(Cluster.RANK_SOURCE, withPosition(EntityPosition.getInputs()), sb, stringBounder);
//		// printRanks(Cluster.RANK_SAME, withPosition(EntityPosition.getSame()), sb,
//		// stringBounder);
//		printRanks(Cluster.RANK_SINK, withPosition(EntityPosition.getOutputs()), sb, stringBounder);
//	}

	private void printRanks(String rank, List<? extends SvekNode> entries, StringBuilder sb,
			StringBounder stringBounder) {
		if (entries.size() > 0) {
			sb.append("{rank=" + rank + ";");
			for (SvekNode sh1 : entries)
				sb.append(sh1.getUid() + ";");

			sb.append("}");
			SvekUtils.println(sb);
			for (SvekNode sh2 : entries)
				sh2.appendShape(sb, stringBounder);

			SvekUtils.println(sb);
		}
	}

//	private List<SvekNode> withPosition(Set<EntityPosition> positions) {
//		final List<SvekNode> result = new ArrayList<>();
//		for (final Iterator<SvekNode> it = cluster.getNodes().iterator(); it.hasNext();) {
//			final SvekNode sh = it.next();
//			if (positions.contains(sh.getEntityPosition()))
//				result.add(sh);
//
//		}
//		return result;
//	}

	private boolean protection0(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type))
			return false;

		return true;
	}

	private boolean protection1(UmlDiagramType type) {
		if (cluster.getGroup().getUSymbol() == USymbols.NODE)
			return true;

		if (skinParam.useSwimlanes(type))
			return false;

		return true;
	}

	private boolean isThereALinkFromOrToGroup(Collection<SvekLine> lines) {
		for (SvekLine line : lines)
			if (line.isLinkFromOrTo(cluster.getGroup()))
				return true;

		return false;
	}

}
