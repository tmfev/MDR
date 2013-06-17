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

import java.util.Iterator;

import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.DataElement;
import net.xclinical.iso11179.DataType;
import net.xclinical.iso11179.Definition;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.Item;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.PermissibleValue;
import net.xclinical.iso11179.UnitOfMeasure;
import net.xclinical.iso11179.ValueDomain;
import net.xclinical.iso11179.ValueMeaning;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.ListWriter;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.server.util.Logger;
import com.xclinical.mdr.service.RemoteException;

public class LoadEditableItemCommand extends AbstractCommand {

	private static final Logger LOG = Logger.get(LoadEditableItemCommand.class);
	
	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {
		Key key = Key.parse(source.getString("id"));
		LOG.debug("Retrieving {0}", key);
		Item item = (Item) PMF.find(key);
		
		FlatJsonExporter.copy(writer, item);
		
		Iterator<Designation> designations = item.getDesignations().iterator();
		if (designations.hasNext()) {
			Designation d = designations.next();
			
			writer.put("designation", d);
			writer.put("designationSign", d.getSign());
			
			LanguageIdentification lang = d.getLanguage();
			FlatJsonExporter.copy(writer.subTree("language"), lang);
			
			if (!d.getScopes().isEmpty()) {
				Context parent = d.getScopes().iterator().next().getContext();
				FlatJsonExporter.copy(writer.subTree("parent"), parent);
			}
		}

		Iterator<Definition> definitions = item.getDefinitions().iterator();
		if (definitions.hasNext()) {
			Definition d = definitions.next();
			writer.put("definition", d);
			writer.put("definitionText", d.getText());
		}
		
		EditableItemVisitor v = new EditableItemVisitor(writer);
		item.accept(v);
	}
	
	private static class EditableItemVisitor extends AbstractVisitor {
		private final TreeWriter writer;
		
		public EditableItemVisitor(TreeWriter writer) {
			this.writer = writer;
		}
		
		@Override
		public void visit(DataElement dataElement) {
			ValueDomain domain = dataElement.getDomain();
			if (domain != null) {
				FlatJsonExporter.copy(writer.subTree("domain"), domain);
			}
		}
		
		@Override
		public void visit(ValueDomain valueDomain) {
			DataType dataType = valueDomain.getDataType();
			if (dataType != null) {
				FlatJsonExporter.copy(writer.subTree("dataType"), dataType);				
			}
			writer.put("maximumCharacterQuantity", valueDomain.getMaximumCharacterQuantity());
			
			UnitOfMeasure unitOfMeasure = valueDomain.getUnitOfMeasure();
			if (unitOfMeasure != null) {
				FlatJsonExporter.copy(writer.subTree("unitOfMeasure"), unitOfMeasure);								
			}
			
			ListWriter list = writer.subList("codeList");
			
			for (PermissibleValue permissibleValue : valueDomain.getMembers()) {
				TreeWriter elementWriter = list.add();
				
				elementWriter.put("id", permissibleValue.getKey());
				elementWriter.put("code", permissibleValue.getPermittedValue());
				ValueMeaning meaning = permissibleValue.getMeaning();
				
				Iterator<Designation> designations = meaning.getDesignations().iterator();
				if (designations.hasNext()) {
					Designation d = designations.next();
					elementWriter.put("text", d.getSign());
				}				
			}
		}
	}
}
