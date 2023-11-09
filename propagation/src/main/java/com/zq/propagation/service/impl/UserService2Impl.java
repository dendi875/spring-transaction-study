package com.zq.propagation.service.impl;

import com.zq.propagation.mapper.UserMapper;
import com.zq.propagation.model.User;
import com.zq.propagation.service.UserService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService2Impl implements UserService2 {

	@Autowired
	private UserMapper userMapper;

	@Override
	public void addUser(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
	}

	@Override
	public void addUserException(User user) throws Exception {
		int i = 1 / 0;
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addUserRequired(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
		throw new RuntimeException("异常测试");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void addUserRequiresNew(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
		throw new RuntimeException("异常测试");
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addUserSupports(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
		throw new RuntimeException("异常测试");
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
	public void addUserMandatory(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
		//throw new RuntimeException("异常测试");
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class)
	public void addUserNotSupports(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
		throw new RuntimeException("异常测试");
	}

	@Override
	@Transactional(propagation = Propagation.NEVER, rollbackFor = Exception.class)
	public void addUserNever(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
	}

	@Override
	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	public void addUserNested(User user) {
		userMapper.insert(User.builder().name(user.getName() + "-2").age(31).build());
		throw new RuntimeException("异常测试");
	}
}
