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
package net.sourceforge.plantuml.quantization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Uses k-means clustering for color quantization. This tends to yield good
 * results, but convergence can be slow. It is not recommended for large images.
 */
public final class KMeansQuantizer implements ColorQuantizer {
	public static final KMeansQuantizer INSTANCE = new KMeansQuantizer();

	private KMeansQuantizer() {
	}

	@Override
	public Set<QColor> quantize(Multiset<QColor> originalColors, int maxColorCount) {
		Map<QColor, Multiset<QColor>> clustersByCentroid = new LinkedHashMap<>();
		Set<QColor> centroidsToRecompute = getInitialCentroids(originalColors, maxColorCount);
		for (QColor centroid : centroidsToRecompute)
			clustersByCentroid.put(centroid, new HashMultiset<QColor>());

		for (QColor color : originalColors.getDistinctElements()) {
			final int count = originalColors.count(color);
			clustersByCentroid.get(color.getNearestColor(centroidsToRecompute)).add(color, count);
		}

		while (centroidsToRecompute.isEmpty() == false) {
			recomputeCentroids(clustersByCentroid, centroidsToRecompute);
			centroidsToRecompute.clear();

			Set<QColor> allCentroids = clustersByCentroid.keySet();
			for (QColor centroid : clustersByCentroid.keySet()) {
				Multiset<QColor> cluster = clustersByCentroid.get(centroid);
				for (QColor color : new ArrayList<>(cluster.getDistinctElements())) {
					QColor newCentroid = color.getNearestColor(allCentroids);
					if (newCentroid != centroid) {
						final int count = cluster.count(color);
						final Multiset<QColor> newCluster = clustersByCentroid.get(newCentroid);

						cluster.remove(color, count);
						newCluster.add(color, count);

						centroidsToRecompute.add(centroid);
						centroidsToRecompute.add(newCentroid);
					}
				}
			}
		}

		return clustersByCentroid.keySet();
	}

	private static void recomputeCentroids(Map<QColor, Multiset<QColor>> clustersByCentroid,
			Set<QColor> centroidsToRecompute) {
		for (QColor oldCentroid : centroidsToRecompute) {
			final Multiset<QColor> cluster = clustersByCentroid.get(oldCentroid);
			final QColor newCentroid = QColor.getCentroid(cluster);
			clustersByCentroid.remove(oldCentroid);
			clustersByCentroid.put(newCentroid, cluster);
		}
	}

	private static Set<QColor> getInitialCentroids(Multiset<QColor> originalColors, int maxColorCount) {
		// We use the Forgy initialization method: choose random colors as initial
		// cluster centroids.
		final List<QColor> colorList = new ArrayList<>(originalColors.getDistinctElements());
		Collections.shuffle(colorList);
		return new HashSet<>(colorList.subList(0, maxColorCount));
	}
}
