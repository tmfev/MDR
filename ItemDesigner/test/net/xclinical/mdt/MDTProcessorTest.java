/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package net.xclinical.mdt;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

import junit.framework.TestCase;
import net.xclinical.iso11179.Namespace;

import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.util.Logger;

public class MDTProcessorTest extends TestCase {

	private static final Logger LOG = Logger.get(MDTProcessorTest.class);
	
	public static final Source ODM13 = new ResourceSource(MDTProcessorTest.class, "odm13.rules");

	public void testMeta() throws Exception {
		LOG.debug("running meta test");
		
		MDTProcessor p = new MDTProcessor();
		Map<String, Object> props = p.getMetaInfo(ODM13);
		
		assertEquals(5, props.size());
	}

	public void notestSelector() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				MDTProcessor p = new MDTProcessor();
				p.process(new ResourceSource(MDTProcessorTest.class, "Selector.rules"), new ResourceSource(MDTProcessorTest.class, "odm130.xml"));
				
				Collection<Namespace> all = Namespace.findAll();
				
				return null;
			}
		});
		
	}
	
	public void notestContext() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				MDTProcessor p = new MDTProcessor();
				p.process(new ResourceSource(MDTProcessorTest.class, "Context.rules"), new ResourceSource(MDTProcessorTest.class, "odm130.xml"));
				
				Collection<Namespace> all = Namespace.findAll();
				
				return null;
			}
		});
		
	}
	
	public void testDesignation() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				MDTProcessor p = new MDTProcessor();
				p.process(new ResourceSource(MDTProcessorTest.class, "Designation.rules"), new ResourceSource(MDTProcessorTest.class, "odm130.xml"));
				
				Collection<Namespace> all = Namespace.findAll();
				
				return null;
			}
		});
		
	}
	
	public void notestODM() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				MDTProcessor p = new MDTProcessor();
				p.process(ODM13, new ResourceSource(MDTProcessorTest.class, "cdash.xml"));
				
				Collection<Namespace> all = Namespace.findAll();
				assertEquals(4, all.size());
				
				return null;
			}
		});
		
	}	
}
