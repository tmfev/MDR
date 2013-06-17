#! /usr/bin/env python

import os

def getCamelCase(s):
	r = ''.join([t.title() for t in s.split('_')])
	return r[0].lower() + r[1:]

out_file_name="SilkImageBundle.java"
o = open(out_file_name, 'w')

print("Writing to " + out_file_name);

o.write("""/* GENERATED FILE -- MODIFY AT YOUR OWN RISK! */
package com.xclinical.mdr.client.silk;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface SilkImageBundle extends ClientBundle {
	SilkImageBundle INSTANCE = GWT.create(SilkImageBundle.class);
			
""")

dirList = os.listdir('.')
count = 0
for f in dirList:
	sp = os.path.splitext(f)
	basename = sp[0]
	if sp[1] == ".png":
		ccname = getCamelCase(basename)
		if ccname in ['new', 'package']:
			ccname = ccname + "Image"
			 
		o.write("""
	/**
	 * Retrieves an image resource for the image 
	 * @return the requested image resource
	 */
""")
		o.write('\t@Source("{0}")\n'.format(f))
		o.write('\tImageResource {0}();\n'.format(ccname))
		count += 1
		
o.write("}\n")

print('written {0} elements'.format(count))
