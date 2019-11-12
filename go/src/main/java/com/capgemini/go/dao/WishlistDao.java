package com.capgemini.go.dao;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import com.capgemini.go.dto.ProductDTO;
import com.capgemini.go.dto.WishlistDTO;
import com.capgemini.go.exception.UserException;
import com.capgemini.go.exception.WishlistException;



@Component
public interface WishlistDao {

	/*******************************************************************************************************
	 *- Function Name : addProductToWishlist 
	 *- Input Parameters : Product List
	 *- Return Type : boolean
	 *- Throws : RetailerException
	 *- Author : CAPGEMINI 
	 *- Creation Date : 21/9/2019
	 *- Description : To add products to Wishlist database
	 *@throws ConnectException 
	 * @throws IOException 
	 * @throws HibernateException 
	 *@throws SQLException 
	 ********************************************************************************************************/
	boolean addProductToWishlist(WishlistDTO wishlist) throws WishlistException;

	
	}
