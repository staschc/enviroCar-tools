/**
 * Copyright (C) 2013
 * by Matthes Rieke
 *
 * Contact: http://matthesrieke.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.envirocar.harvest;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
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
