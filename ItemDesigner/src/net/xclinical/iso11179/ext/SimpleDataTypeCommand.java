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

import net.xclinical.iso11179.DataType;

import com.xclinical.mdr.server.PMF;
import com.xclinical.mdr.server.command.AbstractCommand;
import com.xclinical.mdr.server.tree.TreeBinding;
import com.xclinical.mdr.server.tree.TreeException;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.server.tree.TreeWriter;
import com.xclinical.mdr.service.RemoteException;

public final class SimpleDataTypeCommand extends AbstractCommand {

	@Override
	public void invoke(TreeWriter writer, TreeSource source) throws RemoteException {
		TreeSource item = source.subNode("item");
		DataType dataType = TreeBinding.bind(item);
		read(item, dataType);			
		PMF.get().persist(dataType);

		User.addLink(dataType.getKey(), ItemLink.TAG_UPDATE);
		
		FlatJsonExporter.copy(writer, dataType);
	}
	
	public static void read(TreeSource item, DataType dataType) throws TreeException {
		dataType.setName(item.getString("name"));
		dataType.setDescription(item.getString("description"));
		dataType.setSchemeReference(item.getString("schemeReference"));		
	}
}
