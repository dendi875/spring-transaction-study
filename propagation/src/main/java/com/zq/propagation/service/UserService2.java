package com.zq.propagation.service;

import com.zq.propagation.model.User;

/**
 * 
 * @author <a href="mailto:quanzhang875@gmail.com">quanzhang875</a>
 * @since  2023-11-08 18:43:08
 */
public interface UserService2 {

	void addUser(User user);

	void addUserException(User user) throws Exception;

	void addUserRequired(User user);

	void addUserRequiresNew(User user);

	void addUserSupports(User user);

	void addUserMandatory(User user);

	void addUserNotSupports(User user);

	void addUserNever(User user);

	void addUserNested(User user);
}
