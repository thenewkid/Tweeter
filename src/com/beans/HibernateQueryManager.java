package com.beans;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cooksys.hibernate.Tweets;
import com.cooksys.hibernate.Users;

@Component
public class HibernateQueryManager {
	
	@Autowired
	SessionFactory sf;
	
	@Transactional
	public Users getUserObject(String username) {
		Session session = getSession();
		Query userQuery = session.createQuery("from Users where username = :un");
		userQuery.setString("un", username);
		Users user = (Users) userQuery.uniqueResult();
		return user;
	}
	
	@Transactional
	public boolean usernameExists(String username) {
		if (getUserObject(username) == null)
			return false;
		return true;
	}
	
	@Transactional
	public ArrayList<Tweets> retrieveAllTweets() {
		Session session = getSession();
		ArrayList<Tweets> tweets = (ArrayList<Tweets>) session.createQuery("from Tweets order by tweetid desc").list();
		return tweets;
	}
	@Transactional
	public void storeMessage(String message, String username) {
		Tweets tweet = new Tweets();
		tweet.setMessage(message);
		tweet.setUsername(username);
		getSession().save(tweet);
	}
	
	@Transactional
	public String getFollowers(String username) {
		Users user = getUserObject(username);
		return user.getFollowers();
	}
	//takes in a username and password, returns true if the pair exist in the db
	
	@Transactional
	public boolean userAuthenticated(String username, String password) {
		if (usernameExists(username)) {
			Users user = getUserObject(username);
			if (user.getPassword().equals(password))
				return true;
		}
		return false;
	}
	
	@Transactional
	public void storeUser(String username, String password) {
		Users user = new Users();
		user.setUsername(username);
		user.setPassword(password);
		getSession().save(user);
	}
	
	@Transactional
	public ArrayList<Users> retrieveUsers() {
		return (ArrayList<Users>) getSession().createQuery("from Users").list();
	}
	
	@Transactional
	public ArrayList<Tweets> retrieveFollowingTweets(String username) {
		ArrayList<Tweets> tweets = new ArrayList<Tweets>();
		Session session = getSession();
		if (getUserObject(username).getFollowing() != null) {
			Users user = getUserObject(username);
			String[] following = user.getFollowing().split(",");
			String[] followingPlusMe = new String[following.length+1];
			for (int i = 0; i < following.length;i++) {
				followingPlusMe[i] = following[i];
			}
			followingPlusMe[followingPlusMe.length-1] = username;
			for (String name : followingPlusMe) {
				Query tweetQuery = session.createQuery("from Tweets where username = :name");
				tweetQuery.setString("name", name);
				ArrayList<Tweets> twts = (ArrayList<Tweets>) tweetQuery.list();
				tweets.addAll(twts);
			}
		}
		else {
			Query queer = session.createQuery("from Tweets where username = :name");
			queer.setString("name", username);
			ArrayList<Tweets> tweeters = (ArrayList<Tweets>) queer.list();
			tweets.addAll(tweeters);
		}
		return tweets;
	}
	
	@Transactional
	public ArrayList<Tweets> retrieveTweets(String username) {
		Session session = getSession();
		Query TweetsQuery = session.createQuery("from Tweets where username = :name");
		TweetsQuery.setString("name", username);
		return (ArrayList<Tweets>) TweetsQuery.list();
		
	}
	@Transactional 
	public ArrayList<String> retrieveUsernames() {
		ArrayList<String> usernames = new ArrayList<String>();
		ArrayList<Users> users = retrieveUsers();
		for (Users user : users)
			usernames.add(user.getUsername());
		return usernames;
		
	}
	@Transactional 
	public String getFollowing(String username) {
		Users user = getUserObject(username);
		return user.getFollowing();
	}
	
	@Transactional
	public int followingLength(String username) {
		if (getFollowing(username) == null) {
			return 0;
		}
		return getFollowing(username).length();
	}
	
	@Transactional
	public ArrayList<Users> retrievePotentialFollowers(String searchData, String username) {
		
		//this function returns a list of all users whose names are like searchData
		//and arent in the usernames list of following
		String following = getFollowing(username);
		Session session = getSession();
		Query likeSearchData = session.createQuery("from Users where username like CONCAT('%', :userSearched, '%')");
		likeSearchData.setString("userSearched", searchData);
		ArrayList<Users> likeUsers = (ArrayList<Users>) likeSearchData.list();
		ArrayList<Users> filteredUsers = new ArrayList<Users>();
		
		for (Users user : likeUsers) {
			if (following != null) {
				if (following.indexOf(user.getUsername()) == -1)
					filteredUsers.add(user);
			}	
			else if (following == null)
				filteredUsers.add(user);
		}
		return filteredUsers;
	}
	
	@Transactional
	public void storeFollowing(String[] usernames, String username) {
		Users user = getUserObject(username);
		storeMeAsFollower(usernames, username);
		ArrayList<String> followings = new ArrayList<String>();
		if (user.getFollowing() != null) {
			String[] currentFollowing = user.getFollowing().split(",");
			for (String name : currentFollowing)
				followings.add(name);
			for (String newFollowing : usernames)
				followings.add(newFollowing);
			
			String[] finalFollowing = new String[followings.size()];
			for (int i = 0; i < finalFollowing.length; i++) 
				finalFollowing[i] = followings.get(i);
			
			user.setFollowing(String.join(",", finalFollowing));
			getSession().update(user);
		}
		else {
			user.setFollowing(String.join(",", usernames));
			getSession().update(user);
		}
		
	}
	
	public void storeMeAsFollower(String[] usernames, String username) {
		for (String name : usernames) {
			Users cu = getUserObject(name);
			ArrayList<String> followers = new ArrayList<String>();
			if (cu.getFollowers() != null) {
				for (String s : cu.getFollowers().split(","))
					followers.add(s);
				followers.add(username);
				String[] finalFollowers = new String[followers.size()];
				for (int a = 0; a < finalFollowers.length;a++)
					finalFollowers[a] = followers.get(a);
				cu.setFollowers(String.join(",", finalFollowers));
				getSession().update(cu);
			}
			else {
				cu.setFollowers(username);
				getSession().update(cu);
			}
		}
		
	}

	@Transactional
	public void removeFollowers(String username) {
		String followers = getFollowers(username);
		if (followers != null) {
			//removeMeAsFollowing(followers, username);
			Users userObj = getUserObject(username);
			userObj.setFollowers(null);
			getSession().update(userObj);
		}
	}
	@Transactional
	public void removeMeAsFollowing(String followers, String username) {
		// TODO Auto-generated method stub
		String[] peopleFollowingMe = followers.split(",");
		for (String n : peopleFollowingMe) {
			Users cu = getUserObject(n);
			if (cu.getFollowing()!=null) {
				String[] cf = cu.getFollowing().split(",");
				ArrayList<String> nf = new ArrayList<String>();
				for (String es : cf) {
					if (!es.equals(username)) {
						nf.add(es);
					}
				}
				cu.setFollowing(String.join(",", nf));
				getSession().update(cu);
			}
		}
	}

	@Transactional
	public void removeFollowing(String username) {
		
		//this function removes all the people that this person is following
		//therefore, this username has to be removed as a follower for all the people this user isnt following anymore
		String following = getFollowing(username);
		if (following != null) {
			removeMeAsFollower(following, username);
			Users userObj = getUserObject(username);
			userObj.setFollowing(null);
			getSession().update(userObj);
		}
	}

	@Transactional
	public void removeMeAsFollower(String following, String username) {
		String[] peopleImFollowing = following.split(",");
		for (String name : peopleImFollowing) {
			Users cu = getUserObject(name);
			if (cu.getFollowers() != null) {
				String[] cf = cu.getFollowers().split(",");
				ArrayList<String> nf = new ArrayList<String>();
				for (String s : cf) {
					if (!s.equals(username)) {
						nf.add(s);
					}
				}
				cu.setFollowers(String.join(",", nf));
				getSession().update(cu);
			}
		
		}
		
	}
	public Session getSession() {
		return sf.getCurrentSession();
	}
}
