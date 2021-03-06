package org.dc.jdbc.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.dc.jdbc.config.JDBCConfig;
import org.dc.jdbc.core.ConnectionManager;
import org.dc.jdbc.core.sqlhandler.PrintSqlLogHandler;
import org.dc.jdbc.core.sqlhandler.XmlSqlHandler;
import org.dc.jdbc.entity.SqlContext;

public class DataBaseOperateProxy implements InvocationHandler{
	// 目标对象   
	private Object target;  

	/** 
	 * 构造方法 
	 * @param target 目标对象  
	 */  
	public DataBaseOperateProxy(Object target) {  
		super();  
		this.target = target;  
	}  

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		SqlContext context = SqlContext.getContext();
		if(args[0].toString()==null || args[0].toString().trim().length()==0){
			throw new Throwable("connection is null");
		}
		XmlSqlHandler.getInstance().handleRequest(args[1].toString(), (Object[])args[3]);		
		args[1] = context.getSql();
		args[3] = context.getParams();
		
		if(JDBCConfig.isPrintSqlLog){
			PrintSqlLogHandler.getInstance().handleRequest(args[1].toString() , (Object[])args[3]);
		}
		Object rt = method.invoke(target, args);
		if(!context.getTransaction()){
			ConnectionManager.closeConnection();
		}
		return rt;
	}
	/** 
	 * 获取目标对象的代理对象 
	 * @return 代理对象 
	 */  
	public Object getProxy() {  
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),   
				target.getClass().getInterfaces(), this);  
	}
}
