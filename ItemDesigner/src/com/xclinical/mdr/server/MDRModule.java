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
package com.xclinical.mdr.server;

import net.xclinical.iso11179.ext.AddChildCommand;
import net.xclinical.iso11179.ext.AddFavouriteCommand;
import net.xclinical.iso11179.ext.InvokeCommand;
import net.xclinical.iso11179.ext.ClearFavouritesCommand;
import net.xclinical.iso11179.ext.DataTypeListCommand;
import net.xclinical.iso11179.ext.ExportDocumentCommand;
import net.xclinical.iso11179.ext.FindFirstCommand;
import net.xclinical.iso11179.ext.ImportDocumentCommand;
import net.xclinical.iso11179.ext.InvalidCommand;
import net.xclinical.iso11179.ext.LoadEditableItemCommand;
import net.xclinical.iso11179.ext.LoadListCommand;
import net.xclinical.iso11179.ext.LoadPreviewCommand;
import net.xclinical.iso11179.ext.LoadRootCommand;
import net.xclinical.iso11179.ext.LoginCommand;
import net.xclinical.iso11179.ext.RateCommand;
import net.xclinical.iso11179.ext.RefreshSessionCommand;
import net.xclinical.iso11179.ext.RemoveChildCommand;
import net.xclinical.iso11179.ext.SaveItemCommand;
import net.xclinical.iso11179.ext.SearchCommand;
import net.xclinical.iso11179.ext.SimpleDataElementCommand;
import net.xclinical.iso11179.ext.SimpleDataTypeCommand;
import net.xclinical.iso11179.ext.SimpleDesignatableItemCommand;
import net.xclinical.iso11179.ext.SimpleValueDomainCommand;
import net.xclinical.iso11179.ext.UnitOfMeasureListCommand;

import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.xclinical.mdr.rest.RestfulEntityServlet;
import com.xclinical.mdr.server.command.JsonCommandServlet;

/**
 * Registers all servlets and their paths.
 * 
 * @see MDRServletConfig
 * @author michael@mictale.com
 *
 */
public class MDRModule extends ServletModule {

	private static final String ROOT = "/itemdesigner/mdr/";

	@Override
	protected void configureServlets() {
		install(new JpaPersistModule("prod"));

		filter(ROOT + "*").through(PersistFilter.class);

		serve("/mdr/entities/*").with(RestfulEntityServlet.class);		
		
		serve(ROOT + "backup").with(BackupServlet.class);		
		serve(ROOT + "update/*").with(UpdateServlet.class);		
		serve(ROOT + "document", ROOT + "document/*").with(DocumentServlet.class);
		
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.LoginCommand.PATH).with(
				new JsonCommandServlet(LoginCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.RefreshSessionCommand.PATH).with(
				new JsonCommandServlet(RefreshSessionCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.LoadRootCommand.PATH).with(
				new JsonCommandServlet(LoadRootCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.FindFirstCommand.PATH).with(
				new JsonCommandServlet(FindFirstCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.SearchCommand.PATH).with(
				new JsonCommandServlet(SearchCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.ImportDocumentCommand.PATH).with(
				new JsonCommandServlet(ImportDocumentCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.ExportDocumentCommand.PATH).with(
				new JsonCommandServlet(ExportDocumentCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.RateCommand.PATH).with(
				new JsonCommandServlet(RateCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.AddChildCommand.PATH).with(
				new JsonCommandServlet(AddChildCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.RemoveChildCommand.PATH).with(
				new JsonCommandServlet(RemoveChildCommand.class));

		serve(ROOT + com.xclinical.mdr.client.iso11179.model.SaveItemCommand.PATH).with(
				new JsonCommandServlet(SaveItemCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.SimpleDataTypeCommand.PATH).with(
				new JsonCommandServlet(SimpleDataTypeCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand.PATH).with(
				new JsonCommandServlet(SimpleDesignatableItemCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.SaveDataElementCommand.PATH).with(
				new JsonCommandServlet(SimpleDataElementCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.SaveValueDomainCommand.PATH).with(
				new JsonCommandServlet(SimpleValueDomainCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.DataTypeListCommand.PATH).with(
				new JsonCommandServlet(DataTypeListCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.UnitOfMeasureListCommand.PATH).with(
				new JsonCommandServlet(UnitOfMeasureListCommand.class));

		serve(ROOT + com.xclinical.mdr.client.iso11179.model.LoadListCommand.PATH).with(
				new JsonCommandServlet(LoadListCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.InvokeCommand.PATH).with(
				new JsonCommandServlet(InvokeCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.LoadEditableItemCommand.PATH).with(
				new JsonCommandServlet(LoadEditableItemCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.LoadPreviewCommand.PATH).with(
				new JsonCommandServlet(LoadPreviewCommand.class));

		serve(ROOT + com.xclinical.mdr.client.iso11179.model.AddFavouriteCommand.PATH).with(
				new JsonCommandServlet(AddFavouriteCommand.class));
		serve(ROOT + com.xclinical.mdr.client.iso11179.model.ClearFavouritesCommand.PATH).with(
				new JsonCommandServlet(ClearFavouritesCommand.class));
		
		// Fallback command.
		serve(ROOT + "*").with(new JsonCommandServlet(InvalidCommand.class));
	}
}
