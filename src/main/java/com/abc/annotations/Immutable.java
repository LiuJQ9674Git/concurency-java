package com.abc.annotations;
import java.lang.annotation.*;

/**
 * The class to which this annotation is applied is immutable.  This means that
 * its state cannot be seen to change by callers, which implies that
 * <p/>
 * 应用此注释的类是不可变的。这意味着d调用者看不到其状态发生变化，这意味着
 * <ul>
 * <li> all public fields are final, </li>
 * 所有公共字段都是最终字段，
 * <li> all public final reference fields refer to other immutable objects, and </li>
 * 所有公共最终引用字段都引用其他不可变的对象
 * <li> constructors and methods do not publish references to any internal state
 *      which is potentially mutable by the implementation. </li>
 *  构造函数和方法不发布对任何内部状态的引用它可能会因实现而变化
 * </ul>
 * Immutable objects may still have internal mutable state for purposes of performance
 * optimization; some state variables may be lazily computed, so long as they are computed
 * from immutable state and that callers cannot tell the difference.
 * <p/>
 * 出于性能目的，不可变对象可能仍然具有内部可变状态优化；一些状态变量可能是延迟计算的，只要它们是计算的
 * 来自不可变状态，并且调用方无法分辨其中的区别。
 * <p>
 * Immutable objects are inherently thread-safe; they may be passed between threads or
 * published without synchronization.
 * <p/>
 * 不可变对象本质上是线程安全的；它们可以在线程之间传递，或者在没有同步的情况下发布。
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Immutable {
}
