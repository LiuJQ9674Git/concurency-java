
package com.abc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The field or method to which this annotation is applied can only be accessed
 * when holding a particular lock, which may be a built-in (synchronization) lock,
 * or may be an explicit java.util.concurrent.Lock.
 * <p/>
 * 只能访问应用此注释的字段或方法当持有可以是内置（同步）锁的特定锁时，或者可以是显式java.util.concurrent.Lock。
 * The argument determines which lock guards the annotated field or method:
 * <p/>
 * 该参数确定哪个锁保护带注释的字段或方法：
 * <ul>
 * <li>
 * <code>this</code> : The intrinsic lock of the object in whose class the field is defined.
 * 在其类中定义字段的对象的内部锁。
 * </li>
 * <li>
 * <code>class-name.this</code> : For inner classes, it may be necessary to disambiguate 'this';
 * the <em>class-name.this</em> designation allows you to specify which 'this' reference is intended
 * <code>类名this</code>：对于内部类，可能需要消除“this”的歧义；
 * <em>类名。此</em>名称允许您指定要使用的“this”引用
 * </li>
 * <li>
 * <code>itself</code> : For reference fields only; the object to which the field refers.
 * </li>
 * <li>
 * <code>field-name</code> : The lock object is referenced by the (instance or static) field
 * 锁定对象由（实例或静态）字段引用
 * specified by <em>field-name</em>.
 * </li>
 * <li>
 * <code>class-name.field-name</code> : The lock object is reference by the static field specified
 * 锁定对象由指定的静态字段引用
 * by <em>class-name.field-name</em>.
 * </li>
 * <li>
 * <code>method-name()</code> : The lock object is returned by calling the named nil-ary method.
 * 锁对象是通过调用命名的nil-ary方法返回的。
 * </li>
 * <li>
 * <code>class-name.class</code> : The Class object for the specified class should be used as the lock object.
 * 指定类的Class对象应用作锁对象。
 * </li>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GuardedBy {
    String value();
}
