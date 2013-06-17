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
package com.xclinical.mdr.server.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.xclinical.mdr.server.tree.JsonTreeSource;
import com.xclinical.mdr.server.tree.JsonTreeWriter;
import com.xclinical.mdr.server.tree.TreeException;
import com.xclinical.mdr.server.tree.TreeSource;
import com.xclinical.mdr.service.RemoteException;

public class JsonCommandServlet extends HttpServlet {

	private static final long serialVersionUID = -741718167676144792L;
	
	private final Class<? extends Command> commandType;
	
	public JsonCommandServlet(Class<? extends Command> commandType) {
		this.commandType = commandType;
	}
	
	protected static JSONObject parseRequestObject(HttpServletRequest req) throws JSONException, IOException {
		InputStream ins = req.getInputStream();
		Reader reader = new InputStreamReader(ins, "UTF-8");
		JSONTokener t = new JSONTokener(reader);
		Object next = t.nextValue();
		if (next instanceof JSONObject) {
			return (JSONObject)next;
		}
		
		throw new JSONException("The request " + next + " of type " + next.getClass() + " was unexpected in this context (an object has been expected)");
	}

	protected static void writeResponse(HttpServletResponse resp, JSONArray obj) throws JSONException, IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		Writer w = resp.getWriter();
		obj.write(w);
		w.write('\n');
	}

	public static void writeResponse(HttpServletResponse resp, JSONObject obj) throws JSONException, IOException {
		writeResponse(resp, obj, HttpServletResponse.SC_OK);
	}
	
	protected static void writeResponse(HttpServletResponse resp, JSONObject obj, int status) throws JSONException, IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");		
		resp.setCharacterEncoding("UTF-8");
		OutputStream out = resp.getOutputStream();
		Writer w = new OutputStreamWriter(out, "UTF-8");		
		obj.write(w);
		w.write('\n');
		w.flush();
	}

	public static void writeError(HttpServletResponse resp, String message, String details) throws JSONException, IOException {
		JSONObject error = new JSONObject();
		error.put("message", message);
		error.put("details", details);
		writeResponse(resp, error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Command command = commandType.newInstance();
			JSONObject obj = parseRequestObject(req);
			TreeSource source = new JsonTreeSource(obj);
			JsonTreeWriter writer = new JsonTreeWriter();
			command.invoke(writer, source);

			writeResponse(resp, writer.getObject());
		} catch (JSONException e) {
			throw new ServletException(e);
		} catch (TreeException e) {
			throw new ServletException(e);
		} catch (RemoteException e) {
			throw new ServletException(e);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}	
}
