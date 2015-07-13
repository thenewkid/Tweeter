package com.cooksys.hibernate;

// Generated May 11, 2015 3:02:23 PM by Hibernate Tools 4.3.1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Users generated by hbm2java
 */
@Entity
@Table(name = "users", catalog = "twitterbaby", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Users implements java.io.Serializable {

	private Integer userId;
	private String username;
	private String password;
	private String messages;
	private String followers;
	private String following;

	public Users() {
	}

	public Users(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public Users(String username, String password, String messages,
			String followers, String following) {
		this.username = username;
		this.password = password;
		this.messages = messages;
		this.followers = followers;
		this.following = following;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "username", unique = true, nullable = false, length = 45)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false, length = 45)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "messages")
	public String getMessages() {
		return this.messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	@Column(name = "followers")
	public String getFollowers() {
		return this.followers;
	}

	public void setFollowers(String followers) {
		this.followers = followers;
	}

	@Column(name = "following")
	public String getFollowing() {
		return this.following;
	}

	public void setFollowing(String following) {
		this.following = following;
	}

}
