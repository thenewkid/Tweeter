package com.beans;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cooksys.hibernate.Tweets;
import com.cooksys.hibernate.Users;

@Component

public class RandomProfileBean {

	@Autowired
	HibernateQueryManager hbm;

	String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ArrayList<Users> getFollowers() {
		if (hbm.getFollowers(username) != null) {
			String[] followers = hbm.getFollowers(username).split(",");

			ArrayList<Users> users = new ArrayList<Users>();

			for (String f : followers)
				users.add(hbm.getUserObject(f));
			return users;
		}
		return new ArrayList<Users>();
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
	public ArrayList<Tweets> retrieveTweets() {
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
	public String doit(String username) {
		setUsername(username);
		return "randomProfile.xhtml";
	}

}
