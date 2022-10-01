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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizVersion;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.USymbols;

public class ClusterDotString {

	private final Cluster cluster;
	private final ISkinParam skinParam;
	private static final String ID_EE = "ee";

	public ClusterDotString(Cluster cluster, ISkinParam skinParam) {
		this.cluster = cluster;
		this.skinParam = skinParam;
	}

	void printInternal(StringBuilder sb, Collection<SvekLine> lines, StringBounder stringBounder, DotMode dotMode,
			GraphvizVersion graphvizVersion, UmlDiagramType type) {
		if (cluster.diagram.getPragma().useKermor()) {
			new ClusterDotStringKermor(cluster, skinParam).printInternal(sb, lines, stringBounder, dotMode,
					graphvizVersion, type);
			return;
		}
		final boolean thereALinkFromOrToGroup2 = isThereALinkFromOrToGroup(lines);
		boolean thereALinkFromOrToGroup1 = thereALinkFromOrToGroup2;
		final boolean useProtectionWhenThereALinkFromOrToGroup = graphvizVersion
				.useProtectionWhenThereALinkFromOrToGroup();
		if (useProtectionWhenThereALinkFromOrToGroup == false)
			thereALinkFromOrToGroup1 = false;

		if (thereALinkFromOrToGroup1)
			subgraphClusterNoLabel(sb, "a");

		final Set<EntityPosition> entityPositionsExceptNormal = entityPositionsExceptNormal();
		if (entityPositionsExceptNormal.size() > 0)
			for (SvekLine line : lines)
				if (line.isLinkFromOrTo(cluster.getGroup()))
					line.setProjectionCluster(cluster);

		boolean protection0 = protection0(type);
		boolean protection1 = protection1(type);
		if (entityPositionsExceptNormal.size() > 0 || useProtectionWhenThereALinkFromOrToGroup == false) {
			protection0 = false;
			protection1 = false;
		}

		if (protection0)
			subgraphClusterNoLabel(sb, "p0");

		sb.append("subgraph " + cluster.getClusterId() + " {");
		sb.append("style=solid;");
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

		if (entityPositionsExceptNormal.size() > 0) {
			printRanks(Cluster.RANK_SOURCE, withPosition(EntityPosition.getInputs()), sb, stringBounder);
			printRanks(Cluster.RANK_SINK, withPosition(EntityPosition.getOutputs()), sb, stringBounder);
			if (hasPort())
				subgraphClusterNoLabel(sb, ID_EE);
			else
				subgraphClusterWithLabel(sb, ID_EE, label);

		} else {
			sb.append("label=" + label + ";");
			SvekUtils.println(sb);
		}

		if (thereALinkFromOrToGroup2)
			sb.append(Cluster.getSpecialPointId(cluster.getGroup()) + " [shape=point,width=.01,label=\"\"];");

		if (thereALinkFromOrToGroup1)
			subgraphClusterNoLabel(sb, "i");

		if (protection1)
			subgraphClusterNoLabel(sb, "p1");

		if (skinParam.useSwimlanes(type)) {
			sb.append("{rank = source; ");
			sb.append(getSourceInPoint(type));
			sb.append(" [shape=point,width=.01,label=\"\"];");
			sb.append(cluster.getMinPoint(type) + "->" + getSourceInPoint(type) + "  [weight=999];");
			sb.append("}");
			SvekUtils.println(sb);
			sb.append("{rank = sink; ");
			sb.append(getSinkInPoint(type));
			sb.append(" [shape=point,width=.01,label=\"\"];");
			sb.append("}");
			sb.append(getSinkInPoint(type) + "->" + cluster.getMaxPoint(type) + "  [weight=999];");
			SvekUtils.println(sb);
		}
		SvekUtils.println(sb);
		cluster.printCluster1(sb, lines, stringBounder);

		final SvekNode added = cluster.printCluster2(sb, lines, stringBounder, dotMode, graphvizVersion, type);
		if (entityPositionsExceptNormal.size() > 0)
			if (hasPort()) {
				sb.append(empty() + " [shape=rect,width=.01,height=.01,label=");
				sb.append(label);
				sb.append("];");
			} else if (added == null) {
				sb.append(empty() + " [shape=point,width=.01,label=\"\"];");
			}
		SvekUtils.println(sb);

		sb.append("}");
		if (protection1)
			sb.append("}");

		if (thereALinkFromOrToGroup1) {
			sb.append("}");
			sb.append("}");
		}
		if (entityPositionsExceptNormal.size() > 0)
			sb.append("}");

		if (protection0)
			sb.append("}");

		SvekUtils.println(sb);
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
			if (hasPort()) {
				boolean arrow = false;
				String node = null;
				for (SvekNode sh : entries) {
					if (arrow)
						sb.append("->");

					arrow = true;
					node = sh.getUid();
					sb.append(node);
				}
				if (arrow)
					sb.append(" [arrowhead=none]");

				sb.append(';');
				SvekUtils.println(sb);
				sb.append(node + "->" + empty() + ";");
				SvekUtils.println(sb);
			}
		}
	}

	private List<SvekNode> withPosition(Set<EntityPosition> positions) {
		final List<SvekNode> result = new ArrayList<>();
		for (final Iterator<SvekNode> it = cluster.getNodes().iterator(); it.hasNext();) {
			final SvekNode sh = it.next();
			if (positions.contains(sh.getEntityPosition()))
				result.add(sh);

		}
		return result;
	}

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
