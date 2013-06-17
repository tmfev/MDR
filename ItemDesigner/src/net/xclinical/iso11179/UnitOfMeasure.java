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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.xclinical.mdr.repository.Key;
import com.xclinical.mdr.server.PMF;

/**
 * Represents a Unit_of_Measure (11.4.2.1).
 * 
 * @author ms@xclinical.com
 */
@Entity
@Table(name = "UNIT_OF_MEASURE")
@NamedQueries({ @NamedQuery(name = com.xclinical.mdr.client.iso11179.model.UnitOfMeasure.DIMENSIONALITIES, query = "select d from Dimensionality d where ?1 in elements(d.applicableUnits)") })
public class UnitOfMeasure extends Concept {

	@ManyToOne(optional = true)
	private Dimensionality coordinateDimensionality;

	@ManyToOne(optional = true)
	private Dimensionality nonCoordinateDimensionality;

	public UnitOfMeasure() {
	}

	@Override
	public Key getKey() {
		return buildKey(com.xclinical.mdr.client.iso11179.model.UnitOfMeasure.URN);
	}

	public static UnitOfMeasure create(Context ctx, String sign, LanguageIdentification language) {
		UnitOfMeasure element = new UnitOfMeasure();
		PMF.get().persist(element);
		ctx.designate(element, sign, language);
		return element;
	}

	public void addDimensionality(Dimensionality dimensionality) {
		if (dimensionality.isCoordinateIndicator()) {
			coordinateDimensionality = dimensionality;
		} else {
			nonCoordinateDimensionality = dimensionality;
		}

		dimensionality.addApplicableUnit(this);
	}

	public List<Dimensionality> getDimensionalities() {
		List<Dimensionality> d = new ArrayList<Dimensionality>();
		if (coordinateDimensionality != null) {
			d.add(coordinateDimensionality);
		}
		if (nonCoordinateDimensionality != null) {
			d.add(nonCoordinateDimensionality);
		}
		return d;
	}
}
