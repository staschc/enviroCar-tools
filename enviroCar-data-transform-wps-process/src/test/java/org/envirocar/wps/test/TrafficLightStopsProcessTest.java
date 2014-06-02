/**
 * Copyright (C) 2014
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.envirocar.wps.test;

import static org.junit.Assert.*;

import org.envirocar.wps.TrafficLightStopsProcess;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * Test case for testing whether TrafficLightStopsProcess is working properly.
 * 
 * @author staschc
 *
 */
public class TrafficLightStopsProcessTest {
	
	private Geometry tlPoint;
	private double bufferSize;
	private double maxSpeed;
	
	@Before
	public void setUp() throws Exception {
		this.tlPoint = new GeometryFactory().createPoint(new Coordinate(51.96517,7.60162));
		this.bufferSize=50;
		this.maxSpeed=5;
		
	}

	@Test
	public void test() {
		try {
			FeatureCollection result = new TrafficLightStopsProcess().getStops4PointOfInterest(this.tlPoint, bufferSize, maxSpeed);
			assertEquals(result.size(),1);
			FeatureIterator featIter = result.features();
			Feature feature = featIter.next();
			assertEquals(feature.getProperty("totalNumberOfStops").getValue(),"11");
		} catch (Exception e) {
			fail(e.getMessage());
		}
			
	}

}
