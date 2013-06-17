/*
 * Copyright (c) 2003 The Visigoth Software Society. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Visigoth Software Society (http://www.visigoths.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. Neither the name "FreeMarker", "Visigoth", nor any of the names of the 
 *    project contributors may be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact visigoths@visigoths.org.
 *
 * 5. Products derived from this software may not be called "FreeMarker" or "Visigoth"
 *    nor may "FreeMarker" or "Visigoth" appear in their names
 *    without prior written permission of the Visigoth Software Society.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE VISIGOTH SOFTWARE SOCIETY OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Visigoth Software Society. For more
 * information on the Visigoth Software Society, please see
 * http://www.visigoths.org/
 */

package freemarker.ext.xml;
import freemarker.template.TemplateMethodModel;
import java.lang.String;
import java.util.List;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateModelException;
import java.util.HashMap;

/**
 * @author Attila Szegedi
 * @version $Id: Namespaces.java,v 1.4 2003/10/13 11:57:50 szegedia Exp $
 */
class Namespaces
implements
    TemplateMethodModel,
    Cloneable
{
    static final Factory FACTORY = new Factory()
    {
        public Namespaces create()
        {
            return new Namespaces();
        }
    };
    
    private HashMap namespaces;
    private boolean shared;
        
    Namespaces() {
        namespaces = new HashMap();
        namespaces.put("", "");
        namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
        shared = false;
    }
        
    public Object clone() {
        try {
            Namespaces clone = (Namespaces)super.clone();
            clone.namespaces = (HashMap)namespaces.clone();
            clone.shared = false;
            return clone;
        }
        catch(CloneNotSupportedException e) {
            throw new Error(); // Cannot happen
        }
    }
    
    public String translateNamespacePrefixToUri(String prefix) {
        synchronized(namespaces) {
            return (String)namespaces.get(prefix);
        }   
    }
    
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 2) {
            throw new TemplateModelException("_registerNamespace(prefix, uri) requires two arguments");
        }
        registerNamespace((String)arguments.get(0), (String)arguments.get(1));
        return TemplateScalarModel.EMPTY_STRING;
    }
    
    void registerNamespace(String prefix, String uri) {
        synchronized(namespaces) {
            namespaces.put(prefix, uri);
        }   
    }
    
    void markShared() {
        if(!shared) {
            shared = true;
        }
    }
    
    boolean isShared() {
        return shared;
    }
    
    interface Factory
    {
        Namespaces create();
    }
    
    
}