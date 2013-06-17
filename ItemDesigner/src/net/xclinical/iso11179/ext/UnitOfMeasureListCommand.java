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
package net.xclinical.iso11179.ext;

import java.util.ArrayList;
import java.util.List;

import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.UnitOfMeasure;

import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.service.RemoteException;

public class UnitOfMeasureListCommand extends AbstractCommand {

	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {
		List<UnitOfMeasure> items = new ArrayList<UnitOfMeasure>();
		
		Context parent = TreeBinding.bind(source.subNode("parent"));
		LanguageIdentification language = TreeBinding.bind(source.subNode("language"));

		if (parent == null) throw new IllegalArgumentException("No parent specified");
		if (language == null) throw new IllegalArgumentException("No language specified");
		
		for (TreeSource element : source.subList("elements")) {
			String designation = element.getString("designationSign");
			String definition = element.getString("definitionText");

			UnitOfMeasure u = new UnitOfMeasure();
			PMF.get().persist(u);
			parent.designate(u, designation, language);
			parent.define(u, definition);
			
			items.add(u);			
		}

		writer.put("items", items);		
	}
	
}
