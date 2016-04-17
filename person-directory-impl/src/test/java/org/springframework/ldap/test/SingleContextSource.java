/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.ldap.test;

import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextProxy;

import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class SingleContextSource implements ContextSource {
    private final DirContext ctx;

    public SingleContextSource(final DirContext ctx) {
        this.ctx = ctx;
    }

    /*
     * @see org.springframework.ldap.ContextSource#getReadOnlyContext()
     */
    @Override
    public DirContext getReadOnlyContext() throws NamingException {
        return getNonClosingDirContextProxy(ctx);
    }

    /*
     * @see org.springframework.ldap.ContextSource#getReadWriteContext()
     */
    @Override
    public DirContext getReadWriteContext() throws NamingException {
        return getNonClosingDirContextProxy(ctx);
    }

    private DirContext getNonClosingDirContextProxy(final DirContext context) {
        return (DirContext) Proxy.newProxyInstance(DirContextProxy.class.getClassLoader(),
                new Class[]{getActualTargetClass(context), DirContextProxy.class},
                new NonClosingDirContextInvocationHandler(context));

    }

    public static class NonClosingDirContextInvocationHandler implements InvocationHandler {
        private final DirContext target;

        public NonClosingDirContextInvocationHandler(final DirContext target) {
            this.target = target;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

            final String methodName = method.getName();
            if (methodName.equals("getTargetContext")) {
                return target;
            } else if (methodName.equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
            } else if (methodName.equals("hashCode")) {
                // Use hashCode of Connection proxy.
                return new Integer(proxy.hashCode());
            } else if (methodName.equals("close")) {
                // Never close the target context, as this class will only be
                // used for operations concerning the compensating transactions.
                return null;
            }

            try {
                return method.invoke(target, args);
            } catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    /**
     * Get the actual class of the supplied DirContext instance; LdapContext or
     * DirContext.
     *
     * @param context
     *            the DirContext instance to check.
     * @return LdapContext.class if context is an LdapContext, DirContext.class
     *         otherwise.
     */
    private static Class getActualTargetClass(final DirContext context) {
        if (context instanceof LdapContext) {
            return LdapContext.class;
        }

        return DirContext.class;
    }

    @Override
    public DirContext getContext(final String principal, final String credentials) throws NamingException {
        throw new UnsupportedOperationException();
    }
}
