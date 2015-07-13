package com.beans;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cooksys.hibernate.Tweets;
import com.cooksys.hibernate.Users;

@Component
@Scope("session")
public class UserBean {

	@Autowired
	HibernateQueryManager hbm;

	String username;
	String password;
	String error;
	String[] nowFollowing;
	String userSearch;
	String currentMessage;

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public String[] getNowFollowing() {
		return nowFollowing;
	}

	public void setNowFollowing(String[] nowFollowing) {
		this.nowFollowing = nowFollowing;
		// hbm.storeFollowing(nowFollowing);
	}

	public void storeFollowing() {
		// hbm.storeFollowing(nowFollowing, username);
		hbm.storeFollowing(nowFollowing, username);
	}

	public int followingLength() {
		return hbm.followingLength(username);
	}

	public String getUserSearch() {
		return userSearch;
	}

	public void setUserSearch(String userSearch) {
		if (userSearch.length() == 0)
			this.userSearch = null;
		else
			this.userSearch = userSearch;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.toLowerCase();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public HibernateQueryManager getHbm() {
		return hbm;
	}

	public void setHbm(HibernateQueryManager hbm) {
		this.hbm = hbm;
	}

	// past the getters and setters
	public String sayHello() {
		return "Hello World";
	}

	public void resetUserBean() {
		username = null;
		password = null;
		error = null;
	}

	public String resetOnLogin() {
		resetUserBean();
		return "login.xhtml";
	}

	public String resetOnRegister() {
		resetUserBean();
		return "register.xhtml";

	}

	public String authenticateUser() {
		if (username.length() == 0 || password.length() == 0) {
			setError("You must enter a username and password");
			return "login.html";
		}
		if (getHbm().userAuthenticated(username, password)) {
			return "profile.xhtml";
		}

		setError("Login credentials were incorrect");
		return "login.xhtml";
	}

	public String signUpUser() {
		if (username.length() == 0 || password.length() == 0) {
			setError("You must enter a username and password");
			return "register.xhtml";
		}
		if (getHbm().usernameExists(username)) {
			setError("Username already exists");
			return "register.xhtml";
		}

		getHbm().storeUser(username, password);
		return "profile.xhtml";
	}

	public ArrayList<String> retrievePotentialFollowers() {

		// gets all the possible user objects that are similiar to the letter
		// typed in the box
		ArrayList<Users> users = hbm.retrievePotentialFollowers(userSearch,
				username);

		// init our list of usernames
		ArrayList<String> usernames = new ArrayList<String>();

		// loop through all possible user objects and add their name to the list
		// to return
		// as long as it doesnt equal this users name
		// you can not follow yourself!!!!
		for (Users user : users)
			if (!user.getUsername().equals(username))
				usernames.add(user.getUsername());

		return usernames;
	}

	public ArrayList<Users> getFollowers() {
		if (hbm.getFollowers(username) != null) {
			String[] followers = hbm.getFollowers(username).split(",");

			ArrayList<Users> users = new ArrayList<Users>();

			for (String f : followers)
				users.add(hbm.getUserObject(f));
			return users;
		}
		return null;
	}

	public String getFollowingString() {
		if (hbm.getFollowing(username) != null)
			return hbm.getFollowing(username);
		return "";
	}

	public String getFollowerString() {

		if (hbm.getFollowers(username) != null)
			return hbm.getFollowers(username);
		return "";
	}

	public ArrayList<Users> getFollowing() {

		// all the people this user is following
		if (hbm.getFollowing(username) != null) {
			String[] usernames = hbm.getFollowing(username).split(",");

			// init arraylist of user object
			ArrayList<Users> users = new ArrayList<Users>();

			// loop through all the username we are following and get their
			// object and add it to our list to return
			for (String uname : usernames)
				users.add(hbm.getUserObject(uname));
			return users;
		}
		return null;
	}

	public String[] getFollowingStrings() {
		// all the people this user is following
		if (hbm.getFollowing(username) != null) {
			String[] usernames = hbm.getFollowing(username).split(",");
			return usernames;
		}
		return null;
	}

	public String[] getFollowersStrings() {
		// all the people this user is following
		if (hbm.getFollowers(username) != null) {
			String[] usernames = hbm.getFollowers(username).split(",");
			return usernames;
		}
		return null;
	}

	public void removeFollowing() {
		hbm.removeFollowing(username);
	}

	public void removeFollowers() {
		hbm.removeFollowers(username);
	}

	public void storeMessage() {
		hbm.storeMessage(currentMessage, username);
	}

	public ArrayList<Tweets> retrieveFollowingTweets() {
		ArrayList<Tweets> tweets = hbm.retrieveFollowingTweets(username);
		ArrayList<Tweets> ordered = new ArrayList<Tweets>();

		Tweets tee = null;
		while (tweets.size() > 0) {
			int great = 0;
			for (Tweets t : tweets) {
				if (t.getTweetid() > great) {
					great = t.getTweetid();
					tee = t;
				}
			}
			ordered.add(tee);
			tweets.remove(tee);
		}
		return ordered;
	}

}
