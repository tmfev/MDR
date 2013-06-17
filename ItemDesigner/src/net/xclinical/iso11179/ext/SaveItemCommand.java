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

import net.xclinical.iso11179.Assertion;
import net.xclinical.iso11179.ConceptualDomain;
import net.xclinical.iso11179.DataElement;
import net.xclinical.iso11179.DataType;
import net.xclinical.iso11179.Definition;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.LanguageIdentification;
import net.xclinical.iso11179.Namespace;
import net.xclinical.iso11179.PermissibleValue;
import net.xclinical.iso11179.ReferenceDocument;
import net.xclinical.iso11179.Types;
import net.xclinical.iso11179.UnitOfMeasure;
import net.xclinical.iso11179.ValueDomain;

import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.service.RemoteException;

public class SaveItemCommand extends AbstractCommand {

	protected TreeSource itemSource;
	
	protected Key key;

	protected HasKey item;
	
	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {

		itemSource = source.subNode("item");
		key = Key.parse(itemSource.getString("id"));
		
		item = TreeBinding.bind(itemSource);
		
		if (item == null) {
			item = Types.newInstance(key.getName());
		}
		
		((Visitable)item).accept(new JsonReader());
		
		PMF.get().persist(item);
		
		User.addLink(item.getKey(), ItemLink.TAG_UPDATE);
		
		FlatJsonExporter.copy(writer, (Visitable)item);
	}
	
	private class JsonReader extends AbstractVisitor {
		@Override
		public void visit(Assertion assertion) {
			assertion.setFormula(itemSource.getString("formula"));
		}

		@Override
		public void visit(ConceptualDomain conceptualDomain) {
			conceptualDomain.setDescription(itemSource.getString("description"));
		}
		
		@Override
		public void visit(DataElement dataElement) {
			dataElement.setPrecision(itemSource.getInt("precision"));
			dataElement.setDomain(TreeBinding.<ValueDomain>bind(itemSource.subNode("domain")));
		}
		
		@Override
		public void visit(Designation designation) {
			designation.setSign(itemSource.getString("sign"));
			designation.setLanguage(TreeBinding.<LanguageIdentification>bind(itemSource.subNode("language")));
			designation.setItem(TreeBinding.<LanguageIdentification>bind(itemSource.subNode("item")));
		}

		@Override
		public void visit(Definition definition) {
			definition.setText(itemSource.getString("text"));
		}
		
		@Override
		public void visit(Namespace namespace) {
			namespace.setNamespaceSchemeReference(itemSource.getString("namespaceSchemeReference"));
		}
		
		@Override
		public void visit(PermissibleValue permissibleValue) {
			permissibleValue.setPermittedValue(itemSource.getString("permittedValue"));
		}

		@Override
		public void visit(ReferenceDocument referenceDocument) {
			referenceDocument.setIdentifier(itemSource.getString("identifier"));
			referenceDocument.setTypeDescription(itemSource.getString("typeDescription"));
			referenceDocument.setTitle(itemSource.getString("title"));
			referenceDocument.setUri(itemSource.getString("uri"));
		}

		@Override
		public void visit(ValueDomain valueDomain) {
			valueDomain.setDataType(TreeBinding.<DataType>bind(itemSource.subNode("dataType")));
			valueDomain.setMeaning(TreeBinding.<ConceptualDomain>bind(itemSource.subNode("meaning")));
			valueDomain.setUnitOfMeasure(TreeBinding.<UnitOfMeasure>bind(itemSource.subNode("unitOfMeasure")));
			valueDomain.setMaximumCharacterQuantity(itemSource.getInt("maximumCharacterQuantity"));
			valueDomain.setFormat(itemSource.getString("format"));
		}
		
		@Override
		public void visit(LanguageIdentification languageIdentification) {
			languageIdentification.setLanguageIdentifier(itemSource.getString("languageIdentifier"));
		}
	}
}
