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
import java.util.Iterator;
import java.util.List;

import net.xclinical.iso11179.DataType;
import net.xclinical.iso11179.Designation;
import net.xclinical.iso11179.PermissibleValue;
import net.xclinical.iso11179.UnitOfMeasure;
import net.xclinical.iso11179.ValueDomain;
import net.xclinical.iso11179.ValueMeaning;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.service.RemoteException;

public class SimpleValueDomainCommand extends SimpleDesignatableItemCommand {

	private DataType dataType;
	
	private int maximumCharacterQuantity;

	private String format;
	
	private UnitOfMeasure unitOfMeasure;
	
	private List<CodeListElement> codeList;

	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {
		super.invoke(writer, source);
		
		dataType = TreeBinding.bind(itemSource.subNode("dataType"));
		maximumCharacterQuantity = itemSource.getInt("maximumCharacterQuantity");
		format = itemSource.getString("format");
		unitOfMeasure = TreeBinding.bind(itemSource.subNode("unitOfMeasure"));
		
		codeList = new ArrayList<CodeListElement>();
		
		for (TreeSource item : itemSource.subList("codeList")) {
			CodeListElement elm = new CodeListElement();
			String id = item.getString("id");
			if (id.length() > 0) {
				elm.setKey(Key.parse(id));
			}
			elm.setCode(item.getString("code"));
			elm.setText(item.getString("text"));
			
			codeList.add(elm);
		}

		ValueDomain valueDomain = (ValueDomain)item;
		
		valueDomain.setDataType(dataType);
		valueDomain.setMaximumCharacterQuantity(maximumCharacterQuantity);
		valueDomain.setFormat(format);
		valueDomain.setUnitOfMeasure(unitOfMeasure);

		for (CodeListElement elm : codeList) {
			if (elm.getKey() == null) {
				// New element.
				PermissibleValue permissibleValue = new PermissibleValue();
				PMF.get().persist(permissibleValue);
				elm.setKey(permissibleValue.getKey());
				permissibleValue.setPermittedValue(elm.getCode());
				
				ValueMeaning meaning = new ValueMeaning();
				PMF.get().persist(meaning);
				
				parent.designate(meaning, elm.getText(), language);
				permissibleValue.setMeaning(meaning);
				
				valueDomain.addMember(permissibleValue);
			}
			else {
				// Update existing element.
				PermissibleValue permissibleValue = (PermissibleValue) PMF.find(elm.getKey());
				permissibleValue.setPermittedValue(elm.getCode());

				// Try existing designation, else create a new one.
				boolean overwritten = false;
				
				ValueMeaning meaning = permissibleValue.getMeaning();
				if (meaning == null) {
					meaning = new ValueMeaning();
					PMF.get().persist(meaning);
					User.addLink(meaning.getKey(), ItemLink.TAG_UPDATE);					
				}
				else {
					Iterator<Designation> iterator = meaning.getDesignations().iterator();
					if (iterator.hasNext()) {
						while (iterator.hasNext()) {
							Designation d = iterator.next();
							if (d.getLanguage().equals(language)) {
								// Overwrite this one.
								d.setSign(elm.getText());
								PMF.get().persist(d);								
								overwritten = true;
								break;
							}
						}
					}
				}
				
				if (!overwritten) {
					parent.designate(meaning, elm.getText(), language);										
				}
			}
		}
		
		outer: for (PermissibleValue permissibleValue : valueDomain.getMembers()) {
			for (CodeListElement elm : codeList) {
				if (permissibleValue.getKey().equals(elm.getKey())) {
					continue outer;
				}
			}
			
			valueDomain.getMembers().remove(permissibleValue);
			PMF.get().remove(permissibleValue);
		}

		User.addLink(item.getKey(), ItemLink.TAG_UPDATE);
		
		FlatJsonExporter.copy(writer, valueDomain);		
	}
	
}
