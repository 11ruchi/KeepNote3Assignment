package com.stackroute.keepnote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.exception.UserNotFoundException;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.service.UserService;
import javax.servlet.http.HttpSession;

import static com.stackroute.keepnote.util.KeepNoteUtil.*;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation.
 * Annotate class with @SessionAttributes this  annotation is used to store the model attribute in the session.
 */
@RestController
public class UserAuthenticationController {

	/*
	 * Autowiring should be implemented for the UserService. (Use Constructor-based
	 * autowiring) Please note that we should not create any object using the new
	 * keyword
	 */
    private UserService userService;
    
    @Autowired
	public UserAuthenticationController(UserService userService) {
       this.userService = userService;
	}

	/*
	 * Define a handler method which will authenticate a user by reading the
	 * Serialized user object from request body containing the userId and password
	 * and validating the same. Post login, the userId will have to be stored into
	 * session object, so that we can check whether the user is logged in for all
	 * other services handle UserNotFoundException as well. This handler method
	 * should return any one of the status messages basis on different situations:
	 * 1. 200(OK) - If login is successful. 2. 401(UNAUTHORIZED) - If login is not
	 * successful
	 * 
	 * This handler method should map to the URL "/login" using HTTP POST method
	 */
    @RequestMapping(path="/login",method=RequestMethod.POST,headers = "Accept = application/json")
     public ResponseEntity<User> authenticateUser(@RequestBody final User user, final HttpStatus session){
    	 boolean isValidUser = false;
    	 try {
    		 System.out.println("User name N Password :"+user.getUserId()+":"+ user.getUserPassword());
    	     isValidUser = userService.validateUser(user.getUserId(), user.getUserPassword());
    	     session.setAttribute(LOGGED_IN_USER_ID, user.getUserId()) 
    	                ;    	     
    	 } catch (UserNotFoundException e) {
    		 e.printStackTrace();
    		 return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
    	 }
    	 if(!isValidUser)
    		 return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
    		 return new ResponseEntity<User>(HttpStatus.OK);
     }
	/*
	 * Define a handler method which will perform logout. Post logout, the user
	 * session is to be destroyed. This handler method should return any one of the
	 * status messages basis on different situations: 1. 200(OK) - If logout is
	 * successful 2. 400(BAD REQUEST) - If logout has failed
	 * 
	 * This handler method should map to the URL "/logout" using HTTP GET method
	 */
    @RequestMapping(path="/logout",method=RequestMethod.GET)
    public ResponseEntity<User> logoutUser(HttpSession session){
   	 if(KeepNoteUtil.isUserLoggedIn(session)) {
   		 session.invalidate();
   		 return new ResponseEntity<User>(user, HttpStatus.OK);
   	 } else {
   		 return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
   	 }
   	 
    }
}
   