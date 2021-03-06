package com.capgemini.go.dao;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.capgemini.go.dto.RetailerInventoryDTO;
import com.capgemini.go.exception.ExceptionConstants;
import com.capgemini.go.exception.RetailerInventoryException;

@Repository(value = "retailerInventoryDao")
public class RetailerInventoryDaoImpl implements RetailerInventoryDao {
	
	private Logger logger = Logger.getRootLogger();
	
	@Autowired	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	// Functions for Retailer Inventory Manipulation
	/*******************************************************************************************************
	 * - Function Name : getItemListByRetailer <br>
	 * - Description : to get List of all Items by retailer ID <br>
	 * 
	 * @param queryArguments (retailerId)
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public List<RetailerInventoryDTO> getItemListByRetailer(RetailerInventoryDTO queryArguments)
			throws RetailerInventoryException {
		logger.info("getItemListByRetailer - " + "function called");
		List<RetailerInventoryDTO> result = null;
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		
		try {
			transaction = session.beginTransaction();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<RetailerInventoryDTO> criteriaQuery = builder.createQuery(RetailerInventoryDTO.class);
			Root<RetailerInventoryDTO> retailerInventory = criteriaQuery.from(RetailerInventoryDTO.class);
			criteriaQuery.select(retailerInventory);
			criteriaQuery.where(builder.equal(retailerInventory.get("retailerId"), queryArguments.getRetailerId()));
			result = session.createQuery(criteriaQuery).getResultList();
			transaction.commit();
			logger.info("getItemListByRetailer - " + "Data extracted from database");
		} catch (IllegalStateException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"getItemListByRetailer - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (IllegalArgumentException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"getItemListByRetailer - " + ExceptionConstants.INAPPROPRIATE_ARGUMENT_PASSED);
		} finally {
			session.close();
		}
		if (result == null || result.size() == 0) {
			logger.error(ExceptionConstants.NO_DATA_FOUND);
			throw new RetailerInventoryException("getItemListByRetailer - " + ExceptionConstants.NO_DATA_FOUND);
		}
		logger.info("getItemListByRetailer - " + "Sent requested data");
		return result;
	}

	/*******************************************************************************************************
	 * - Function Name : getListOfRetailers <br>
	 * - Description : to get List of all Retailer ID's in database <br>
	 * 
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public List<RetailerInventoryDTO> getListOfRetailers() throws RetailerInventoryException {
		logger.info("getListOfRetailers - " + "function called");
		List<RetailerInventoryDTO> result = null;
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		
		try {
			transaction = session.beginTransaction();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<RetailerInventoryDTO> criteriaQuery = builder.createQuery(RetailerInventoryDTO.class);
			Root<RetailerInventoryDTO> retailerInventory = criteriaQuery.from(RetailerInventoryDTO.class);
			criteriaQuery.select(retailerInventory.get("retailerId"));
			criteriaQuery.groupBy(retailerInventory.get("retailerId"));
			result = session.createQuery(criteriaQuery).getResultList();
			transaction.commit();
			logger.info("getListOfRetailers - " + "Data extracted from database");
		} catch (IllegalStateException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"getListOfRetailers - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (IllegalArgumentException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"getListOfRetailers - " + ExceptionConstants.INAPPROPRIATE_ARGUMENT_PASSED);
		} catch (PersistenceException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException("getListOfRetailers - " + ExceptionConstants.PERSISTENCE_ERROR);
		} finally {
			session.close();
		}
		if (result == null || result.size() == 0) {
			logger.error("getListOfRetailers - " + ExceptionConstants.NO_DATA_FOUND);
			throw new RetailerInventoryException("getListOfRetailers - " + ExceptionConstants.NO_DATA_FOUND);
		}
		logger.info("getListOfRetailers - " + "Sent requested data");
		return result;
	}

	/*******************************************************************************************************
	 * - Function Name : updateProductReceiveTimeStamp <br>
	 * - Description : to update receive time stamp of a particular product for a particular retailer <br>
	 * 
	 * @param queryArguments (retailerId, productUin, productRecieveTime)
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public boolean updateProductReceiveTimeStamp(RetailerInventoryDTO queryArguments)
			throws RetailerInventoryException {
		logger.info("updateProductReceiveTimeStamp - " + "function called");
		boolean receiveTimestampUpdated = false;
		/*
		 * required arguments in `queryArguments` productUIN, productRecieveTime
		 * 
		 * un-required productDispatchTime, productShelfTimeOut, productCategory,
		 * retailerUserId
		 */
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		try {
			transaction = session.getTransaction();
			transaction.begin();
			RetailerInventoryDTO existingItem = session
					.find(RetailerInventoryDTO.class, queryArguments.getProductUniqueId());
			if (existingItem == null) {
				logger.debug(ExceptionConstants.PRODUCT_NOT_IN_INVENTORY);
				throw new RetailerInventoryException(
						"updateProductReceiveTimeStamp - " + ExceptionConstants.PRODUCT_NOT_IN_INVENTORY);
			}
			existingItem.setProductReceiveTimestamp(queryArguments.getProductReceiveTimestamp());
			transaction.commit();
		} catch (IllegalStateException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"updateProductReceiveTimeStamp - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (RollbackException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"updateProductReceiveTimeStamp - " + ExceptionConstants.FAILURE_COMMIT_CHANGES);
		} finally {
			session.close();
		}
		receiveTimestampUpdated = true;
		return receiveTimestampUpdated;
	}

	/*******************************************************************************************************
	 * - Function Name : updateProductSaleTimeStamp <br>
	 * - Description : to update sale time stamp of a particular product for a particular retailer <br>
	 * 
	 * @param queryArguments (retailerId, productUin, productSaleTime)
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public boolean updateProductSaleTimeStamp(RetailerInventoryDTO queryArguments) throws RetailerInventoryException {
		boolean saleTimestampUpdated = false;
		logger.info("updateProductSaleTimeStamp - " + "function called");
		/*
		 * required arguments in `queryArguments` productUIN, productSaleTime
		 * 
		 * un-required productDispatchTime, productReceiveTime, productCategory,
		 * retailerUserId
		 */
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		try {
			transaction = session.getTransaction();
			transaction.begin();
			RetailerInventoryDTO existingItem = session
					.find(RetailerInventoryDTO.class, queryArguments.getProductUniqueId());
			if (existingItem == null) {
				logger.debug(ExceptionConstants.PRODUCT_NOT_IN_INVENTORY);
				throw new RetailerInventoryException(
						"updateProductSaleTimeStamp - " + ExceptionConstants.PRODUCT_NOT_IN_INVENTORY);
			}
			existingItem.setProductSaleTimestamp(queryArguments.getProductSaleTimestamp());
			transaction.commit();
		} catch (IllegalStateException error) {
			logger.error("updateProductSaleTimeStamp - " + error.getMessage());
			throw new RetailerInventoryException(
					"updateProductSaleTimeStamp - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (RollbackException error) {
			logger.error("updateProductSaleTimeStamp - " + error.getMessage());
			throw new RetailerInventoryException(
					"updateProductSaleTimeStamp - " + ExceptionConstants.FAILURE_COMMIT_CHANGES);
		} finally {
			session.close();
		}
		saleTimestampUpdated = true;
		return saleTimestampUpdated;
	}

	/*******************************************************************************************************
	 * - Function Name : insertItemInRetailerInventory <br>
	 * - Description : to insert an item into inventory <br>
	 * 
	 * @param queryArguments (retailerId, productUin, productCategory, productDispatchTime)
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public boolean insertItemInRetailerInventory(RetailerInventoryDTO queryArguments)
			throws RetailerInventoryException {
		logger.info("insertItemInRetailerInventory - " + "function called");
		boolean productInserted = false;
		/*
		 * required arguments in `queryArguments` retailerUserId, productCategory, productId
		 * productUIN, productDispatchTime
		 * 
		 * un-required productRecieveTime, productShelfTimeOut
		 * 
		 * in any case, if the arguments are supplied, they will be stored in the
		 * database
		 */
		RetailerInventoryDTO newItem = new RetailerInventoryDTO();
		newItem.setRetailerId(queryArguments.getRetailerId());
		newItem.setProductCategory((byte) queryArguments.getProductCategory());
		newItem.setProductId(queryArguments.getProductId());
		newItem.setProductUniqueId(queryArguments.getProductUniqueId());
		newItem.setProductDispatchTimestamp(queryArguments.getProductDispatchTimestamp());
		newItem.setProductReceiveTimestamp(queryArguments.getProductReceiveTimestamp());
		newItem.setProductSaleTimestamp(queryArguments.getProductSaleTimestamp());

		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		try {
			transaction = session.beginTransaction();
			session.save(newItem);
			transaction.commit();
			logger.info("insertItemInRetailerInventory - " + "Data inserted into database");
		} catch (IllegalStateException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"insertItemInRetailerInventory - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (RollbackException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"insertItemInRetailerInventory - " + ExceptionConstants.FAILURE_COMMIT_CHANGES);
		} catch (PersistenceException error) {
			logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"insertItemInRetailerInventory - " + ExceptionConstants.PRODUCT_ALREADY_PRESENT_IN_INVENTORY);
		} finally {
			session.close();
		}
		productInserted = true;
		return productInserted;
	}

	/*******************************************************************************************************
	 * - Function Name : deleteItemInRetailerInventory <br>
	 * - Description : to delete an item in inventory <br>
	 * 
	 * @param queryArguments (retailerId, productUin)
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public boolean deleteItemInRetailerInventory(RetailerInventoryDTO queryArguments)
			throws RetailerInventoryException {
		logger.info("deleteItemInRetailerInventory - " + "function called");
		boolean itemDeleted = false;
		/*
		 * required arguments in `queryArguments` productUIN, retailerUserId
		 * 
		 * un-required productRecieveTime, productShelfTimeOut, productCategory,
		 * productDispatchTime
		 * 
		 * in any case, if the arguments are supplied, they will be stored in the
		 * database
		 */
		RetailerInventoryDTO newItem = new RetailerInventoryDTO();
		newItem.setRetailerId(queryArguments.getRetailerId());
		newItem.setProductCategory((byte) queryArguments.getProductCategory());
		newItem.setProductUniqueId(queryArguments.getProductUniqueId());
		newItem.setProductDispatchTimestamp(queryArguments.getProductDispatchTimestamp());

		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		try {
			transaction = session.beginTransaction();
			session.remove(newItem);
			transaction.commit();
			logger.info("deleteItemInRetailerInventory - " + "Data removed from database");
		} catch (IllegalStateException error) {
			logger.error("deleteItemInRetailerInventory - " + error.getMessage());
			throw new RetailerInventoryException(
					"deleteItemInRetailerInventory - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (RollbackException error) {
			logger.error("deleteItemInRetailerInventory - " + error.getMessage());
			throw new RetailerInventoryException(
					"deleteItemInRetailerInventory - " + ExceptionConstants.FAILURE_COMMIT_CHANGES);
		} finally {
			session.close();
		}
		itemDeleted = true;
		return itemDeleted;
	}

	// Retailer Inventory Data Access Functions
	/*******************************************************************************************************
	 * - Function Name : getSoldItemsDetails <br>
	 * - Description : to get List of all Sold Items By retailer ID <br>
	 * 
	 * @param queryArguments (retailerId)
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	@Override
	public List<RetailerInventoryDTO> getSoldItemsDetails(RetailerInventoryDTO queryArguments)
			throws RetailerInventoryException {
		logger.info("getSoldItemsDetails - " + "function called");
		/*
		 * Required Arguments : retailer Id
		 */
		List<RetailerInventoryDTO> result = null; // List reference variable for query result
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		try {
			transaction = session.beginTransaction();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<RetailerInventoryDTO> criteriaQuery = builder.createQuery(RetailerInventoryDTO.class);
			Root<RetailerInventoryDTO> retialerInventory = criteriaQuery.from(RetailerInventoryDTO.class);
			criteriaQuery.select(retialerInventory);
			criteriaQuery.where(builder.equal(retialerInventory.get("retailerId"), queryArguments.getRetailerId()), builder.isNotNull(retialerInventory.get("productReceiveTimestamp")), 
					builder.isNotNull(retialerInventory.get("productSaleTimestamp")));
			result = session.createQuery(criteriaQuery).getResultList();
			transaction.commit();
			logger.info("getSoldItemsDetails - " + "Data extracted from database");
		} catch (IllegalStateException error) {
			logger.error("getSoldItemsDetails - " + error.getMessage());
			throw new RetailerInventoryException(
					"getSoldItemsDetails - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (IllegalArgumentException error) {
			logger.error("getSoldItemsDetails - " + error.getMessage());
			throw new RetailerInventoryException(
					"getSoldItemsDetails - " + ExceptionConstants.INAPPROPRIATE_ARGUMENT_PASSED);
		} finally {
			session.close();
		}
		if (result == null || result.size() == 0) {
			logger.error("getSoldItemsDetails - " + ExceptionConstants.NO_DATA_FOUND);
			throw new RetailerInventoryException("getSoldItemsDetails - " + ExceptionConstants.NO_DATA_FOUND);
		}
		logger.info("getSoldItemsDetails - " + "function return");
		return result;
	}

	/*******************************************************************************************************
	 * - Function Name : getDeliveredItemsDetails <br>
	 * - Description : to get List of all Items Delivered to retailer ID <br>
	 * 
	 * @param queryArguments (retailerId)
	 * @return List<RetailerInventoryDTO>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	@Override
	public List<RetailerInventoryDTO> getDeliveredItemsDetails(RetailerInventoryDTO queryArguments)
			throws RetailerInventoryException {
		logger.info("getDeliveredItemsDetails - " + "function called");
		/*
		 * Required Arguments : retailer Id
		 */
		List<RetailerInventoryDTO> result = null; // List reference variable for query result
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		try {
			transaction = session.beginTransaction();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<RetailerInventoryDTO> criteriaQuery = builder.createQuery(RetailerInventoryDTO.class);
			Root<RetailerInventoryDTO> retialerInventory = criteriaQuery.from(RetailerInventoryDTO.class);
			criteriaQuery.select(retialerInventory);
			criteriaQuery.where(builder.equal(retialerInventory.get("retailerId"), queryArguments.getRetailerId()),
					builder.isNotNull(retialerInventory.get("productReceiveTimestamp")));
			result = session.createQuery(criteriaQuery).getResultList();
			transaction.commit();
			logger.info("getDeliveredItemsDetails - " + "Data extracted from database");
		} catch (IllegalStateException error) {
			logger.error("getDeliveredItemsDetails - " + error.getMessage());
			throw new RetailerInventoryException(
					"getDeliveredItemsDetails - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (IllegalArgumentException error) {
			logger.error("getDeliveredItemsDetails - " + error.getMessage());
			throw new RetailerInventoryException(
					"getDeliveredItemsDetails - " + ExceptionConstants.INAPPROPRIATE_ARGUMENT_PASSED);
		} finally {
			session.close();
		}
		if (result == null || result.size() == 0) {
			logger.error("getDeliveredItemsDetails - " + ExceptionConstants.NO_DATA_FOUND);
			throw new RetailerInventoryException(
					"getDeliveredItemsDetails - " + ExceptionConstants.NO_DATA_FOUND);
		}
		logger.info("getDeliveredItemsDetails - " + "function return");
		return result;
	}
	// END OF Retailer Inventory Data Access Functions
}
