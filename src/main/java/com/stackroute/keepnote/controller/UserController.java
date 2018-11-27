package com.stackroute.keepnote.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.exception.CategoryNotFoundException;
import com.stackroute.keepnote.exception.UserNotFoundException;
import com.stackroute.keepnote.model.Category;
import com.stackroute.keepnote.model.Note;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.service.UserService;

/*
 * As in this assignment, we are working on creating RESTful web service, hence annotate
 * the class with @RestController annotation. A class annotated with the @Controller annotation
 * has handler methods which return a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 */
@RestController
public class UserController {

	/*
	 * Autowiring should be implemented for the UserService. (Use Constructor-based
	 * autowiring) Please note that we should not create an object using the new
	 * keyword
	 */
    
	private UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
	  this.userService = userService;
	}

	/*
	 * Define a handler method which will create a specific user by reading the
	 * Serialized object from request body and save the user details in a User table
	 * in the database. This handler method should return any one of the status
	 * messages basis on different situations: 1. 201(CREATED) - If the user created
	 * successfully. 2. 409(CONFLICT) - If the userId conflicts with any existing
	 * user
	 * 
	 * Note: ------ This method can be called without being logged in as well as
	 * when a new user will use the app, he will register himself first before
	 * login.
	 * 
	 * This handler method should map to the URL "/user/register" using HTTP POST
	 * method
	 */
          @RequestMapping(path="/user/register",method = RequestMethod.POST)
          public ResponseEntity<User> registerUser(@RequestBody final User user, HttpSession session){
        	  boolean isUserCreated = false;
        	  
        	  try {
    			  user.setUserAddedDate(new Date());
    			  
    			  isUserCreated = userService.registerUser(user);
    		  } catch ( Exception e) {
    			  e.printStackTrace();
    			  return new ResponseEntity<User>(HttpStatus.CONFLICT);	  
    		  }
    			  return new ResponseEntity<User>(HttpStatus.CREATED);
          }
        	  
          
        	  
        	  
     /*
          }
	 * Define a handler method which will update a specific user by reading the
	 * Serialized object from request body and save the updated user details in a
	 * user table in database handle exception as well. This handler method should
	 * return any one of the status messages basis on different situations: 1.
	 * 200(OK) - If the user updated successfully. 2. 404(NOT FOUND) - If the user
	 * with specified userId is not found. 3. 401(UNAUTHORIZED) - If the user trying
	 * to perform the action has not logged in.
	 * 
	 * This handler method should map to the URL "/user/{id}" using HTTP PUT method.
	 */
        	  @RequestMapping(path="/user/{id}",method = RequestMethod.PUT)
              public ResponseEntity<User> updateUser(@RequestBody final User user, @PathVariable("id") String userId, HttpSession session){
            	  User updatedUser = null;
            	  String userName = (String) session.getAttribute(LOGGED_IN_USER_ID);
        		  
        		  if   (userName== null) {
        			  return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
        		  }
        		  
        		  try {
        			  user.setUserAddedDate(new Date());
        			  user.setUserId(userName);
        			  updatedUser= userService.updateUser(user, userId);
        		  } catch ( Exception e) {
        			  e.printStackTrace();
        			  return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        			  
        		  }
        		  if(updatedUser == null) {
        			  return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        	  }else {
        			  return new ResponseEntity<User>(HttpStatus.OK);
        	} 
        	  
} 	  
	/*
	 * Define a handler method which will delete a user from a database.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the user deleted successfully from
	 * database. 2. 404(NOT FOUND) - If the user with specified userId is not found.
	 * 3. 401(UNAUTHORIZED) - If the user trying to perform the action has not
	 * logged in.
	 * 
	 * This handler method should map to the URL "/user/{id}" using HTTP Delete
	 * method" where "id" should 
	 * be replaced by a valid userId without {}
	 */
        	  @RequestMapping(path="/user/{id}",method = RequestMethod.DELETE)
              public ResponseEntity<User> deleteUser(@PathVariable("id") final String userId,HttpSession session){
            	  boolean isUserDeleted = false;
            	  if(KeepNoteUtil.isUserLoggedIn(session)) {
            			  isUserDeleted = userService.deleteUser(userId);
            			  if(isUserDeleted)
            			  return new ResponseEntity<User>( HttpStatus.OK);
            			  else
            			  return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            		  }
            	  else {
            			  return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED); 
            		  }
            		   
            	  }
	/*
	 * Define a handler method which will show details of a specific user handle
	 * UserNotFoundException as well. This handler method should return any one of
	 * the status messages basis on different situations: 1. 200(OK) - If the user
	 * found successfully. 2. 401(UNAUTHORIZED) - If the user trying to perform the
	 * action has not logged in. 3. 404(NOT FOUND) - If the user with specified
	 * userId is not found. This handler method should map to the URL "/user/{id}"
	 * using HTTP GET method where "id" should be replaced by a valid userId without
	 * {}
	 */

        	  @RequestMapping(path="/user/{id}", method=RequestMethod.GET)
        	    public ResponseEntity<User> getUserByUserId(@PathVariable("id") String userId, HttpSession session){
        	    	User user = null;
        	    	String userName = (String) session.getAttribute(LOGGED_IN_USER_ID);
        			  
        			  if   (userName== null)         {
        				  return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
        			  }
        			  
        			  try {
        				  user = userService.getUserById(userId);
        			  }
        			   catch ( UserNotFoundException e) {
        				  e.printStackTrace();
        				  return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        				  
        			  }
        			  if(user == null)
        				  return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        			  
        				  return new ResponseEntity<User>(HttpStatus.OK);
        		} 
}