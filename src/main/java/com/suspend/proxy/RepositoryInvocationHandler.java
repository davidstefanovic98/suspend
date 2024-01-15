package com.suspend.proxy;


import com.suspend.repository.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RepositoryInvocationHandler implements InvocationHandler {

    private final Repository target;

    public RepositoryInvocationHandler(Repository target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return method.invoke(target, objects);
    }
}
