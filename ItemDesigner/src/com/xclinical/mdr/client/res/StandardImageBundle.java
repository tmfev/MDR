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
package com.xclinical.mdr.client.res;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface StandardImageBundle extends ClientBundle {
	StandardImageBundle INSTANCE = GWT.create(StandardImageBundle.class);

	@Source("add.png")
	ImageResource add();

	@Source("add_down.png")
	ImageResource addDown();
	
	@Source("remove.png")
	ImageResource remove();

	@Source("remove_down.png")
	ImageResource removeDown();
	
	@Source("close.png")
	ImageResource close();
	
	@Source("downArrow.png")
	ImageResource downArrow();

	@Source("upArrow.png")
	ImageResource upArrow();
	
	@Source("leftArrow.png")
	ImageResource leftArrow();

	@Source("rightArrow.png")
	ImageResource rightArrow();
	
	@Source("spin16ffffffff.gif")
	ImageResource spin16White();
	
	@Source("spin16ffd0e4f6.gif")
	ImageResource spin16Background();
	
	@Source("spin32ffd0e4f6.gif")
	ImageResource spin32Background();

	@Source("spin32ffffffff.gif")
	ImageResource spin32White();

	@Source("pinDown.png")
	ImageResource pinDown();

	@Source("pinUp.png")
	ImageResource pinUp();
	
	@Source("star.png")
	ImageResource star();

	@Source("star_half.png")
	ImageResource starHalf();
	
	@Source("star_hollow.png")
	ImageResource starHollow();

	@Source("star_1.png")
	ImageResource star1();

	@Source("star_2.png")
	ImageResource star2();
	
	@Source("star_3.png")
	ImageResource star3();
	
	@Source("star_4.png")
	ImageResource star4();
	
	@Source("dropDown.png")
	ImageResource dropDown();

	@Source("dropDown_hot.png")
	ImageResource dropDownHot();

	@Source("dropDown_light.png")
	ImageResource dropDownLight();

	@Source("window_min.png")
	ImageResource windowMin();

	@Source("window_max.png")
	ImageResource windowMax();
	
	@Source("window_close.png")
	ImageResource windowClose();
	
	@Source("xclinical_small.png")
	ImageResource xclinicalSmall();

	@Source("bmf_logo.png")
	ImageResource bmfLogo();

	@Source("lmu_logo.png")
	ImageResource lmuLogo();
	
	@Source("tmf_logo.png")
	ImageResource tmfLogo();
	
	@Source("imise_logo.png")
	ImageResource imiseLogo();
	
	@Source("xclinical_logo.png")
	ImageResource xclinicalLogo();	
}
