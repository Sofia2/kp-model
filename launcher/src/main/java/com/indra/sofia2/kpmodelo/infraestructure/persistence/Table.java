/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
* Example TablaKp object that is persisted to disk by the DAO.
*/
@DatabaseTable(tableName = "table")
public class Table {

	 // for QueryBuilder to be able to find the fields
    public static final String DATA_FIELD_NAME = "ssapMesssage";

    @DatabaseField(id =true, columnName = DATA_FIELD_NAME, canBeNull = false, width = 2048)
    private String ssapMesssage;

    Table() {
            // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Table(String ssapMesssage) {
            this.ssapMesssage = ssapMesssage;
    }

    public String getSsapMesssage() {
		return ssapMesssage;
	}

	@Override
    public int hashCode() {
            return ssapMesssage.hashCode();
    }

    @Override
    public boolean equals(Object other) {
            if (other == null || other.getClass() != getClass()) {
                    return false;
            }
            return ssapMesssage.equals(((Table) other).ssapMesssage);
    }
}
