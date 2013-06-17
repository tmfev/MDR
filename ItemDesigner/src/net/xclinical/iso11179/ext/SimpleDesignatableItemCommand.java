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

import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.Definition;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.Item;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.Types;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.service.RemoteException;

public class SimpleDesignatableItemCommand extends AbstractCommand {

	protected TreeSource itemSource;
	
	protected Key key;
	
	protected Designation designation;
	
	protected Definition definition;
	
	protected Context parent;

	protected LanguageIdentification language;

	private String designationSign;

	private String definitionText;
	
	protected Item item;
	
	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {
		itemSource = source.subNode("item");
		key = Key.parse(itemSource.getString("id"));
		
		item = TreeBinding.bind(itemSource);
		
		if (item == null) {
			item = Types.newInstance(key.getName());
			PMF.get().persist(item);
		}
		
		designation = TreeBinding.bind(itemSource.subNode("designation"));
		definition = TreeBinding.bind(itemSource.subNode("definition"));
		parent = TreeBinding.bind(itemSource.subNode("parent"));
		language = TreeBinding.bind(itemSource.subNode("language"));
		
		if (language == null) {
			language = LanguageIdentification.findOrCreate("en-US");
		}
		
		designationSign = itemSource.getString("designationSign");
		definitionText = itemSource.getString("definitionText");

		if (designation != null) {
			designation.setSign(designationSign);
			
			if (language != null) {
				designation.setLanguage(language);
			}			
		}
		else {
			parent.designate(item, designationSign, language);			
		}
		
		if (definition != null) {
			definition.setText(definitionText);			
		}
		else {
			parent.define(item, definitionText);
		}
		
		FlatJsonExporter.copy(writer, item);				
	}
}
