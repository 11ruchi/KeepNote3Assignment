package com.stackroute.keepnote.controller;

import java.util.List;

import static com.stackroute.keepnote.util.KeepNoteUtil.LOGGED_IN_USER_ID;
import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.stackroute.keepnote.exception.CategoryNotFoundException;
import com.stackroute.keepnote.model.Category;
import com.stackroute.keepnote.service.CategoryService;
import com.stackroute.keepnote.util.KeepNoteUtil;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 */
@RestController
public class CategoryController {

	/*
	 * Autowiring should be implemented for the CategoryService. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword
	 */

	private CategoryController categoryService;
	
	public CategoryController(CategoryService categoryService) {

		this.categoryService= categoryService;
	}

	/*
	 * Define a handler method which will create a category by reading the
	 * Serialized category object from request body and save the category in
	 * category table in database. Please note that the careatorId has to be unique
	 * and the loggedIn userID should be taken as the categoryCreatedBy for the
	 * category. This handler method should return any one of the status messages
	 * basis on different situations: 1. 201(CREATED - In case of successful
	 * creation of the category 2. 409(CONFLICT) - In case of duplicate categoryId
	 * 3. 401(UNAUTHORIZED) - If the user trying to perform the action has not
	 * logged in.
	 * 
	 * This handler method should map to the URL "/category" using HTTP POST
	 * method".
	 */

	@RequestMapping(path = "/category", method= RequestMethod.POST)
	public ResponseEntity<Category> createCategory(@RequestBody final Category category, HttpSession session){
		Category existingCategory = null;
		boolean isCategoryCreated = false;
		String userName = (String) session.getAttribute(LOGGED_IN_USER_ID);
		  
		  if   (userName== null)         {
			  return new ResponseEntity<Category>(HttpStatus.UNAUTHORIZED);
		  }
		  
		  try {
			  category.setCategoryCreationDate(new Date());
			  category.setCategoryCreatedBy(userName);
			  isCategoryCreated = categoryService.createCategory(category);
		  } catch ( Exception e) {
			  e.printStackTrace();
			  return new ResponseEntity<Category>(HttpStatus.INTERNAL_SERVER_ERROR);
			  
		  }
		  if(isCategoryCreated)
			  return new ResponseEntity<Category>(HttpStatus.CREATED);
		  else
			  return new ResponseEntity<Category>(HttpStatus.CONFLICT);
	} 
	/*
	 * Define a handler method which will delete a category from a database.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the category deleted successfully from
	 * database. 2. 404(NOT FOUND) - If the category with specified categoryId is
	 * not found. 3. 401(UNAUTHORIZED) - If the user trying to perform the action
	 * has not logged in.
	 * 
	 * This handler method should map to the URL "/category/{id}" using HTTP Delete
	 * method" where "id" should be replaced by a valid categoryId without {}
	 */
	@RequestMapping(path = "/category/{id}", method= RequestMethod.DELETE)
	public ResponseEntity<Category> deleteCategory(@PathVariable("id") final int categoryId, HttpSession session){
		Category existingCategory = null;
		boolean isCategoryDeleted = false;
		     if(KeepNoteUtil.isUserLoggedIn(session)) {
		    	 isCategoryDeleted = categoryService.deleteCategory(categoryId);
		     
		  if(isCategoryDeleted)
			  return new ResponseEntity<Category>(HttpStatus.OK);
		  else
			  return new ResponseEntity<Category>(HttpStatus.NOT_FOUND);
	} else {
		return new ResponseEntity<Category>(HttpStatus.UNAUTHORIZED);
	}
	}
	/*
	 * Define a handler method which will update a specific category by reading the
	 * Serialized object from request body and save the updated category details in
	 * a category table in database handle CategoryNotFoundException as well. please
	 * note that the loggedIn userID should be taken as the categoryCreatedBy for
	 * the category. This handler method should return any one of the status
	 * messages basis on different situations: 1. 200(OK) - If the category updated
	 * successfully. 2. 404(NOT FOUND) - If the category with specified categoryId
	 * is not found. 3. 401(UNAUTHORIZED) - If the user trying to perform the action
	 * has not logged in.
	 * 
	 * This handler method should map to the URL "/category/{id}" using HTTP PUT
	 * method.
	 */
	@RequestMapping(path = "/category/{id}", method= RequestMethod.PUT)
	public ResponseEntity<Category> updateCategory(@RequestBody final Category category, @PathVariable("id") final int categoryId, HttpSession session){
		Category updateCategory = null;
		
		String userName = (String) session.getAttribute(LOGGED_IN_USER_ID);
		  
		  if   (userName== null)         {
			  return new ResponseEntity<Category>(HttpStatus.UNAUTHORIZED);
		  }
		  
		  try {
			  category.setCategoryCreationDate(new Date());
			  category.setCategoryCreatedBy(userName);
			  updateCategory = categoryService.updateCategory(category, categoryId);
		  } catch ( CategoryNotFoundException e) {
			  e.printStackTrace();
			  return new ResponseEntity<Category>(HttpStatus.NOT_FOUND);
			  
		  }
		  if(updateCategory == null)
			  return new ResponseEntity<Category>(HttpStatus.NOT_FOUND);
		  
			  return new ResponseEntity<Category>(HttpStatus.OK);
	} 
	/*
	 * Define a handler method which will get us the category by a userId.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the category found successfully. 2.
	 * 401(UNAUTHORIZED) -If the user trying to perform the action has not logged
	 * in.
	 * 
	 * 
	 * This handler method should map to the URL "/category" using HTTP GET method
	 */
	@RequestMapping(path = "/category", method= RequestMethod.GET)
	public ResponseEntity<Category> getCategoryByUser(HttpStatus session){
		List<Category> categoryList = null;
		String userId = (String) session.getAttribute(LOGGED_IN_USER_ID);
		if(userId == null) {
			return new ResponseEntity<List<Category>>(HttpStatus.UNAUTHORIZED);
		}
		categoryList = categoryService.getAllCategoryByUserId(userId);
		
		Category existingCategory = null;
		     
		  if(categoryList.size() !=0)
			  return new ResponseEntity<List<Category>>(HttpStatus.OK);
		  else
			  return new ResponseEntity<List<Category>>(HttpStatus.NOT_FOUND);
	} 
		
}