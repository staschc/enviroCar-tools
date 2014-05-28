package org.envirocar.wps;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Test;

public class DataTransformProcessTest {

	@Test
	public void testDataTransform(){
		try {
			
			URL url = new URL("https://envirocar.org/api/stable/tracks/53433169e4b09d7b34fa824a");
			
			SimpleFeatureCollection sft = new DataTransformProcess().createFeaturesFromJSON(url);
			
			assertTrue(sft.size() == 449);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
	}
	
}
