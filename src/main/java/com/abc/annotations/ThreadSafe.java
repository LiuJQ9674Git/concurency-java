package com.abc.annotations;
import java.lang.annotation.*;

/**
 * The class to which this annotation is applied is thread-safe.  This means that
 * no sequences of accesses (reads and writes to public fields, calls to public methods)
 * may put the object into an invalid state, regardless of the interleaving of those actions
 * by the runtime, and without requiring any additional synchronization or coordination on the
 * part of the caller.
 * <p/>
 * 应用此注释的类是线程安全的。这意味着没有访问序列（对公共字段的读写、对公共方法的调用）
 * <p/>可能会使对象处于无效状态，而不管这些操作的交互情况如何
 * <p/>这些由运行时执行，并且不需要对调用方的同步或者协调。
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {
}
