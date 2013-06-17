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
package net.xclinical.iso11179;

import java.util.concurrent.Callable;

import junit.framework.TestCase;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

public class DataElementTest extends TestCase {

	private Key mdr;
	
	public void test() throws Exception {
		PMF.run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				LanguageIdentification de = LanguageIdentification.create("de");
				Context ctx = Context.create("mdr", de);
				Context ctx2 = Context.create(ctx, "catalog", de);				
				DataElement e1 = DataElement.create(ctx2, "element", de);
				assertNotNull(e1);
				
				mdr = ctx.getKey();
				return null;
			}
		});

		PMF.run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				LanguageIdentification de = LanguageIdentification.create("de");
				Context ctx = (Context)PMF.get().find(Context.class, mdr.getValue());				
				Context ctx3 = Context.create(ctx, "another catalog", de);				
				DataElement.create(ctx3, "another element", de);
				
				return null;
			}
		});
		
		PMF.run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Context ctx = (Context)PMF.get().find(Context.class, mdr.getValue());
								
				assertEquals(3, ctx.getRelevantDesignations().size());
				assertEquals("catalog", ctx.getRelevantDesignation("catalog").getSign());
				Context catalog = (Context)ctx.getRelevantDesignation("catalog").getItem();
				assertNotNull(catalog);
				DataElement elm = (DataElement)catalog.getRelevantDesignation("element").getItem();
				
				for (DesignationContext ct : ctx.getRelevantDesignations()) {
					Key k1 = ct.getContext().getKey();
					Key k2 = ctx.getKey();
					assertEquals(k1, k2);
					
					Designation des = ct.getDesignation();
					String sign = des.getSign();
					
					Item item = (Item)des.getItem();
					assertEquals(1, item.getDesignations().size());
				}
				return null;
			}
		});
	}
}
