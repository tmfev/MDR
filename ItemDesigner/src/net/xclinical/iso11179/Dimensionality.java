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
package net.xclinical.iso11179;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Dimensionality Characteristic (11.4.2.2).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name="DIMENSIONALITY")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({
	@NamedQuery(name = com.xclinical.mdr.client.iso11179.model.Dimensionality.APPLICABLE_UNITS, query = "select u from UnitOfMeasure u where u.coordinateDimensionality = ?1 or u.nonCoordinateDimensionality = ?1")
})
public class Dimensionality extends Concept {
	
	private boolean coordinateIndicator;
	
	@OneToMany(targetEntity=UnitOfMeasure.class)
	private List<UnitOfMeasure> applicableUnits = new ArrayList<UnitOfMeasure>();
	
	public Dimensionality() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.Dimensionality.URN);
	}
	
	public static Dimensionality create(Context ctx, String sign, LanguageIdentification language, boolean coordinateIndicator) {
		Dimensionality element = new Dimensionality();
		element.setCoordinateIndicator(coordinateIndicator);
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}
	
	public void setCoordinateIndicator(boolean coordinateIndicator) {
		this.coordinateIndicator = coordinateIndicator;
	}

	public boolean isCoordinateIndicator() {
		return coordinateIndicator;
	}
	
	void addApplicableUnit(UnitOfMeasure unitOfMeasure) {
		applicableUnits.add(unitOfMeasure);
	}
	
	public List<UnitOfMeasure> getApplicableUnits() {
		return applicableUnits;
	}
}
