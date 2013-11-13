/**
 * Copyright (C) 2013
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
package org.envirocar.harvest;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.envirocar.harvest.TrackPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyTrackCreator extends TrackPublisher {
	
	private static final Logger logger = LoggerFactory.getLogger(DummyTrackCreator.class);

	private String featureTemplate;
	private String startTemplate;
	private String endTemplate;
	

	public DummyTrackCreator(String url) throws IOException {
		super(url);
		featureTemplate = readTemplate("track-template_loop.jsonfrag");
		startTemplate = readTemplate("track-template_start.jsonfrag");
		endTemplate = readTemplate("track-template_end.jsonfrag");
	}

	private String readTemplate(String res) throws IOException {
		return readContent(getClass().getResourceAsStream(res));
	}
	
	
	public static void main(String[] args) throws IOException {
		String consumerUrl = null;
		if (args != null && args.length > 0) {
			consumerUrl = args[0].trim();
		}
		else {
			throw new IllegalArgumentException("consumerUrl needs to be provided");
		}
		
		new DummyTrackCreator(consumerUrl).publishTracks();
	}

	private void publishTracks() throws ClientProtocolException, IOException {
		int count = 0;
		Double lat = -89.195;
		Double lon = -179.195;
		
		StringBuilder sb;
		Integer lonCount = 10000;
		while (lon < 180) {
			sb = new StringBuilder();
			sb.append(startTemplate.replace("${trackId}", lonCount.toString()));
			
			while (lat < 90) {
				sb.append(featureTemplate.replace("${lat}", lat.toString()).replace("${lon}",
						lon.toString()));
				count++;
				lat += 0.15;
			}
			
			sb.deleteCharAt(sb.length()-1);
			sb.append(endTemplate);
			
			pushToConsumer(sb.toString());
			
			lat = -89.125;
			lon += 0.15;
			lonCount++;
			
			logger.info("finished longitude {}; pushed track #: {} ", lon, (lonCount-10000));
		}
		
		logger.info("Total feature count: {}", count);
	}

}
