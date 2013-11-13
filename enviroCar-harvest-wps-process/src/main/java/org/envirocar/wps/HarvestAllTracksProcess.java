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
package org.envirocar.wps;

import java.io.IOException;

import org.envirocar.harvest.ProgressListener;
import org.envirocar.harvest.TrackHarvester;
import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.algorithm.annotation.LiteralDataOutput;
import org.n52.wps.algorithm.descriptor.AlgorithmDescriptor;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Track Harvester for enviroCar API
 * 
 * @author matthes rieke
 *
 */
@Algorithm(version = "1.0.0", abstrakt="Track Harvester for enviroCar API")
public class HarvestAllTracksProcess extends AbstractAnnotatedAlgorithm implements ProgressListener {

    private static Logger LOGGER = LoggerFactory.getLogger(HarvestAllTracksProcess.class);
	private String apiUrl;
	private String result = "ongoing";
	private String consumerUrl;

    public HarvestAllTracksProcess() {
        super();
    }
    
    @Override
    protected AlgorithmDescriptor createAlgorithmDescriptor() {
    	return super.createAlgorithmDescriptor();
    }
    
    @LiteralDataOutput(identifier = "result", binding = LiteralStringBinding.class)
    public String getResult() {
        return result;
    }

    @LiteralDataInput(identifier = "enviroCar-track-url", binding = LiteralStringBinding.class, minOccurs=1)
    public void setApiUrl(String u) {
        this.apiUrl = u;
    }
    
    @LiteralDataInput(identifier = "consumer-url", binding = LiteralStringBinding.class, minOccurs=1)
    public void setConsumerUrl(String u) {
        this.consumerUrl = u;
    }
    
    @Execute
    public void runAlgorithm() {
    	LOGGER.info("Starting track harvester");
    	TrackHarvester harvester = new TrackHarvester(consumerUrl, this, apiUrl);
    	try {
			harvester.harvestTracks();
		} catch (IOException e) {
			LOGGER.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		}
    	result = "finished";
    }

	@Override
	public void onProgressUpdate(float progressPercent) {
		update((int) progressPercent);
	}
}