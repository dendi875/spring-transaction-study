package com.zq.propagation.service.impl;

import com.zq.propagation.mapper.UserMapper;
import com.zq.propagation.model.User;
import com.zq.propagation.service.UserService;
import com.zq.propagation.service.UserService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * REQUIRED 与 NESTED 的区别：
 * 1. 在 REQUIRED 中，method-1 和 method-2 是同一个事务；但是在 NESTED 中 ，method-2 是 method-1 的子事务(嵌套事务)
 * 2. 在 REQUIRED 中，method-2 抛出异常不能被 try catch 捕获，否则会抛出 UnexpectedRollbackException 异常；但是在 NESTED 中，method-2 的异常可以被 try catch 捕获。
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserService2 userService2;

	// 无事务情况下，两条数据正常插入
	@Override
	public void addUser(User user) {
		userMapper.insert(user);
		// method-2
		userService2.addUser(user);
	}

	// 无事务有异常情况下，成功插入一条数据
	@Override
	public void addUserException(User user) throws Exception {
		userMapper.insert(user);
		// method-2
		userService2.addUserException(user);
	}

	/*********************************************
	 * REQUIRED 是默认值
	 * 结论：
	 * method-1 (REQUIRED) 调用 method-2 (REQUIRED)
	 *
	 * 1. 如果 method-1 抛出异常，method-1 和 method-2 中的事务一起回滚
	 * 2. 如果 method-2 抛出异常，method-1 和 method-2 中的事务一起回滚
	 * 3. 如果 method-1 捕获了异常，则抛出异常 UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only，method-1 和 method-2 都插入失败
	 *
	 * REQUIRED 这种默认的传播机制比较常用，符合我们一般的业务场景，多个操作要么一起成功，要么一起失败
	 *
	 * 在 REQUIRED 中，method-1 和 method-2 是同一个事务
	 *********************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addUserRequired(User user) {
		userMapper.insert(user);
		// method-2
		try {
			userService2.addUserRequired(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//throw new RuntimeException("异常测试");
	}

	/*****************************************************************
	 * REQUIRES_NEW
	 * 结论：
	 * method-1（有事务） 调用 method-2（有事务）
	 * 1. 如果 method-1 抛出异常，method-1 事务回滚，而 method-2 不受外部异常影响
	 * 2. 如果 method-2 抛出异常，method-1 和 method-2 中的事务一起回滚
	 *
	 ******************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void addUserRequiresNew(User user) {
		userMapper.insert(user);
		// method-2
		userService2.addUserRequiresNew(user);
		//throw new RuntimeException("异常测试");
	}

	/*********************************************************************
	 * SUPPORTS
	 * 支持当前事务，如果当前事务不存在，则以非事务方式执行
	 *
	 * 1. method-1 无事务，method-2 为 SUPPORTS
	 * 如果 method-2 抛出异常，method-1 和 method-2 都不会回滚
	 * 2. method-2 无事务，method-1 为 SUPPORTS
	 * 如果 method-2 抛出异常，method-1 和 method-2 都不会回滚
	 *
	 * 3. method-1 为 REQUIRED，method-2 为 SUPPORT
	 * 如果 method-2 抛出异常，method-1 和 method-2 一起回滚
	 * 4. method-1 为 SUPPORT，method-2 为 REQUIRED
	 * 如果 method-2 抛出异常，method-2 回滚，method-1 正常插入，因为是事务的传播特性，method-1 的事务(REQUIRED)传到了 method-2（SUPPORT）
	 *
	 **********************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	//@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public void addUserSupports(User user) {
		userMapper.insert(user);
		// method-2
		userService2.addUserSupports(user);
		//throw new RuntimeException("异常测试");
	}

	/****************************************************************************
	 * Mandatory 强制的
	 * 支持当前事务，如果不存在事务，则抛出异常
	 *
	 * 1. method-1 (无事务)，method-2 (MANDATORY)
	 * method-1 插入成功，method-2 插入失败，抛出 IllegalTransactionStateException异常
	 *
	 * 2. method-1 (REQUIRED)，method-2 (MANDATORY)
	 * 如果 method-1 或 method-2 抛出异常，则 method-1 和 method-2 都一起回滚
	 *
	 ****************************************************************************/
	@Override
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addUserMandatory(User user) {
		userMapper.insert(user);
		// method-2
		userService2.addUserMandatory(user);
		//throw new RuntimeException("异常测试");
	}

	/*******************************************************************************
	 * NOT_SUPPORTED
	 * 以非事务方式执行，如果存在当前事务，则挂起当前事务
	 *
	 * 1. method-1（无事务），method-2 (NOT_SUPPORTED)
	 * method-2 抛出异常，但插入成功了，因为以非事务方式执行，method-1 因为没有事务，自然也是插入成功
	 *
	 * 2. method-1（REQUIRED），method-2 (NOT_SUPPORTED)
	 *  2.1. method-2 抛出异常，但插入成功了，因为以非事务方式执行，method-1 因为有事务，获取到了 method-2 的异常，所认进行回滚
	 *  2.1. method-1 抛出异常，因为有事务，进行了回滚，method-2 因为以非事务方式执行，所以不会回滚
	 *
	 ********************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addUserNotSupports(User user) {
		userMapper.insert(user);
		// method-2
		userService2.addUserNotSupports(user);
		//throw new RuntimeException("异常测试");
	}

	/**********************************************************************************
	 * NEVER 以非事务方式执行，如果存在事务则抛出异常
	 *
	 * 1. method-1 (REQUIRED), method-2 (NEVER)
	 *
	 * method-2 抛出异常，插入失败
	 * 如果 method-1 没有 catch 处理异常，则因为 method-1 有事务，且异常是 Exception 的子类，所以 method-1 回滚
	 *
	 * 2. method-2 (无事务)，method-2 (NEVER)
	 * method-1 和 method-2 都以无事务方式执行
	 *
	 ***********************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addUserNever(User user) {
		userMapper.insert(user);
		// method-2
		userService2.addUserNever(user);
	}

	/***************************************************************************************
	 * NESTED
	 * 如果当前事务存在，则在嵌套事务中执行，否则行为类似 REQUIRED
	 *
	 * 1. method-1 (REQUIRED), method-2 (NESTED)
	 * 无论异常在 method-1 还是 method-2 中，都会一起回滚
	 *
	 * 2. method-1 (无事务)，method-2 (NESTED)
	 * method-1 按照无事务的方式执行，method-2 按照默认事务（REQUIRED）方式执行
	 *
	 * 3. method-1 (REQUIRED), method-2 (NESTED)
	 * method-1 捕获了异常，则 method-1 执行成功，method-2 回滚
	 *
	 * 在 NESTED 中 ，method-2 是 method-1 的子事务(嵌套事务)
	 *
	 ****************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addUserNested(User user) {
		userMapper.insert(user);
		// method-2
		try {
			userService2.addUserNested(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//throw new RuntimeException("异常测试");
	}
}
