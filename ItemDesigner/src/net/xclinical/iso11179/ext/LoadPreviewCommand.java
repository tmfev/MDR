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
import java.util.List;

import javax.persistence.Query;

import net.xclinical.iso11179.Context;
import net.xclinical.iso11179.DataElement;
import net.xclinical.iso11179.Definition;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.DesignationContext;
import net.xclinical.iso11179.Item;
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

public class LoadPreviewCommand extends AbstractCommand {

	private static final Logger LOG = Logger.get(LoadPreviewCommand.class);
	
	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {
		Key key = Key.parse(source.getString("id"));
		LOG.debug("Retrieving {0}", key);
		Item item = (Item) PMF.find(key);
				
		PreviewItemVisitor v = new PreviewItemVisitor(writer);
		item.accept(v);
	}
	
	private static class PreviewItemVisitor extends AbstractVisitor {
		private final TreeWriter writer;
		
		public PreviewItemVisitor(TreeWriter writer) {
			this.writer = writer;
		}
		
		@Override
		public void visit(Context context) {
			FlatJsonExporter.copy(writer, context);

			Iterator<Designation> designations = context.getDesignations().iterator();
			if (designations.hasNext()) {
				Designation d = designations.next();
				
				writer.put("designationSign", d.getSign());				
			}

			Iterator<Definition> definitions = context.getDefinitions().iterator();
			if (definitions.hasNext()) {
				Definition d = definitions.next();
				writer.put("definitionText", d.getText());
			}

			ListWriter childWriter = writer.subList("children");
			ListWriter dataElementWriter = writer.subList("dataElements");
			
			for (DesignationContext child : context.getRelevantDesignations()) {
				Item item = child.getDesignation().getItem();
				if (item instanceof Context) {
					PreviewItemVisitor visitor = new PreviewItemVisitor(childWriter.add());
					visitor.visit((Context)item); 
				}
				
				if (item instanceof DataElement) {
					PreviewItemVisitor visitor = new PreviewItemVisitor(dataElementWriter.add());
					visitor.visit((DataElement)item); 
				}
			}			
		}
		
		@Override
		public void visit(DataElement dataElement) {
			FlatJsonExporter.copy(writer, dataElement);

			Iterator<Designation> designations = dataElement.getDesignations().iterator();
			if (designations.hasNext()) {
				Designation d = designations.next();
				
				writer.put("designationSign", d.getSign());				
			}

			Iterator<Definition> definitions = dataElement.getDefinitions().iterator();
			if (definitions.hasNext()) {
				Definition d = definitions.next();
				writer.put("definitionText", d.getText());
			}

			TreeWriter domainWriter = writer.subTree("valueDomain");
			ListWriter list = writer.subList("codeList");
			
			ValueDomain domain = dataElement.getDomain();
			if (domain != null) {
				FlatJsonExporter.copy(domainWriter, domain);
				
				writer.put("format", domain.getFormat());
				writer.put("maximumCharacterQuantity", domain.getMaximumCharacterQuantity());
				
				UnitOfMeasure unitOfMeasure = domain.getUnitOfMeasure();
				if (unitOfMeasure != null) {
					writer.put("unitOfMeasure", unitOfMeasure.getDesignations().iterator().next().getSign());
				}
						
				Query permissibleValues = PMF.get().createNamedQuery(com.xclinical.mdr.client.iso11179.model.ValueDomain.MEMBERS).setParameter(1, domain);
				@SuppressWarnings("unchecked")
				List<PermissibleValue> resultList = permissibleValues.getResultList();
				
				for (PermissibleValue permissibleValue : resultList) {
					TreeWriter elementWriter = list.add();
					
					elementWriter.put("id", permissibleValue.getKey());
					elementWriter.put("code", permissibleValue.getPermittedValue());
					ValueMeaning meaning = permissibleValue.getMeaning();
					
					designations = meaning.getDesignations().iterator();
					if (designations.hasNext()) {
						Designation d = designations.next();
						elementWriter.put("text", d.getSign());
					}				
				}
			}			
		}
		
	}
}
