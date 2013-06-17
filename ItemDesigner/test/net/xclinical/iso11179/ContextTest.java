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

import com.xclinical.mdr.server.PMF;

public class ContextTest extends TestCase {

	public void testSimple() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Context ctx = new Context();
				PMF.get().persist(ctx);

				return null;
			}
		});
	}

	public void testCreate() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Context ctx = new Context();
				PMF.get().persist(ctx);

				LanguageIdentification de = LanguageIdentification.create("de");
				Designation designation = new Designation("hello", de);
				PMF.get().persist(designation);

				DesignationContext designationCtx = new DesignationContext(designation, ctx);
				PMF.get().persist(designationCtx);

				ctx.addDesignation(designation);

				return null;
			}
		});
	}

	public void testCreateShort() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				LanguageIdentification de = LanguageIdentification.create("de");
				Context ctx = Context.create("hello", de);
				assertNotNull(ctx);
				return null;
			}
		});
	}

	public void testFindRelevantDesignations() throws Exception {
		PMF.runTest(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				LanguageIdentification de = LanguageIdentification.create("de");
				Context ctx = Context.create("root", de);

				DataElement e1 = DataElement.create(ctx, "e1", de);
				DataElement.create(ctx, "e2", de);

				Designation des = ctx.getRelevantDesignation("e1");
				assertEquals(e1.getDesignations().iterator().next().getKey(), des.getKey());
				
				return null;
			}
		});
	}
}
