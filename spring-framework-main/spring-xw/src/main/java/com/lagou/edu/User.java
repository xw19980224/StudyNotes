package com.lagou.edu;

/**
 * @ClassName: User
 * @Author: MaxWell
 * @Description:
 * @Date: 2021/12/23 10:48
 * @Version: 1.0
 */
public class User {
	private String name;

	public User(String name) {
		this.name = name;
	}

	public User() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
