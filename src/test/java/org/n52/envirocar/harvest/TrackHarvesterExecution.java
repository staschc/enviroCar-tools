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

public class TrackHarvesterExecution {


	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		String consumerUrl = null;
		if (args != null && args.length > 0) {
			consumerUrl = args[0].trim();
		}
		else {
			throw new IllegalArgumentException("consumerUrl needs to be provided");
		}
		
		ProgressListener l = new ProgressListener() {

			@Override
			public void onProgressUpdate(float progressPercent) {
				System.out.println(String.format("%f percent finished", progressPercent));
			}
			
		};
		new TrackHarvester(consumerUrl, l).harvestTracks();
	}
	
}
