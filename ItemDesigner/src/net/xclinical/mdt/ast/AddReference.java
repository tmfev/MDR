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
package net.xclinical.mdt.ast;

import net.xclinical.mdt.ProcessingException;

public class AddReference implements Reference {

	private Reference left, right;
	
	public AddReference(Reference left, Reference right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public Object get(ExecutionContext context) {
		Object l = left.get(context);
		Object r = right.get(context);

		if (l instanceof String) {
			StringBuilder sb = new StringBuilder((String)l);
			sb.append(r);
			return sb.toString();
		}
		else if (l instanceof Long) {
			if (r instanceof Long) {
				return (Long)l + (Long)r;
			}
			else if (r instanceof Float) {
				return (Long)l + (Float)r;
			}
		}
		else if (l instanceof Float) {
			if (r instanceof Long) {
				return (Float)l + (Long)r;
			}
			else if (r instanceof Float) {
				return (Float)l + (Float)r;
			}
		}
			
		throw new ProcessingException("Incompatible types");
	}

}
