/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.persistence;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;

/**
* Utility class which shows how the dao's are injected and used.
*/
@Component
public class Persistence {

        private Dao<Table, String> tableDao;

	private TransactionManager transactionManager;

        
        @Required
        public void setTableDao(Dao<Table, String> tableDao) {
                this.tableDao = tableDao;
        }

        @Required
        public void setTransactionManager(TransactionManager transactionManager) {
                this.transactionManager = transactionManager;
        }
        
        public TransactionManager getTransactionManager() {
			return transactionManager;
		}

		public int create(Table table) throws SQLException{
			return tableDao.create(table);
        }
		
		public int update(Table table) throws SQLException{
			return tableDao.update(table);
        }
        
        public void delete(Table table) throws SQLException{
			tableDao.delete(table);
        }
        
        public List<Table> findAll() throws SQLException{
			return tableDao.queryForAll();
        }
        
        public Table findById(String id) throws SQLException{
			return tableDao.queryForId(id);
        }
        
        public boolean checkConnectivity (){
            try {
		tableDao.queryBuilder().selectRaw("SELECT 1");
		return true;
	    } catch (Exception e) {
		return false;
	    }
        }
        
}
