package com.abc.test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import java.util.Collection;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * 算法说明
 *
 */
/**
 * This is a modification of the Michael & Scott algorithm,
 * adapted for a garbage-collected environment, with support for
 * interior node deletion (to support e.g. remove(Object)).  For
 * explanation, read the paper.
 *
 * Note that like most non-blocking algorithms in this package,
 * this implementation relies on the fact that in garbage
 * collected systems, there is no possibility of ABA problems due
 * to recycled nodes, so there is no need to use "counted
 * pointers" or related techniques seen in versions used in
 * non-GC'ed settings.
 *
 *注意，像该包中的大多数非阻塞算法一样，该实现依赖于在垃圾中收集的系统，
 *不存在ABA问题的可能性到回收节点，因此无需使用“计数指针”或在中使用的版本中看
 *到的相关技术非GC’ed设置。
 * The fundamental invariants are:基本不变量是：
 * - There is exactly one (last) Node with a null next reference,
 *   which is CASed when enqueueing.  This last Node can be
 *   reached in O(1) time from tail, but tail is merely an
 *   optimization - it can always be reached in O(N) time from
 *   head as well.
 *
 *   恰好有一个（最后一个）节点具有空的下一个引用，其在排队时被CASed。
 *   最后一个节点可以在O（1）时间内从尾部到达，
 *   但尾部只是一个优化——它也总是可以在0（N）时间内从头到达。
 *
 * - The elements contained in the queue are the non-null items in
 *   Nodes that are reachable from head.  CASing the item
 *   reference of a Node to null atomically removes it from the
 *   queue.  Reachability of all elements from head must remain
 *   true even in the case of concurrent modifications that cause
 *   head to advance.  A dequeued Node may remain in use
 *   indefinitely due to creation of an Iterator or simply a
 *   poll() that has lost its time slice.
 *
 * The above might appear to imply that all Nodes are GC-reachable
 * from a predecessor dequeued Node.  That would cause two problems:
 * - allow a rogue Iterator to cause unbounded memory retention
 * - cause cross-generational linking of old Nodes to new Nodes if
 *   a Node was tenured while live, which generational GCs have a
 *   hard time dealing with, causing repeated major collections.
 * However, only non-deleted Nodes need to be reachable from
 * dequeued Nodes, and reachability does not necessarily have to
 * be of the kind understood by the GC.  We use the trick of
 * linking a Node that has just been dequeued to itself.  Such a
 * self-link implicitly means to advance to head.
 *
 * Both head and tail are permitted to lag.  In fact, failing to
 * update them every time one could is a significant optimization
 * (fewer CASes). As with LinkedTransferQueue (see the internal
 * documentation for that class), we use a slack threshold of two;
 * that is, we update head/tail when the current pointer appears
 * to be two or more steps away from the first/last node.
 *
 * 头部和尾部都允许滞后。事实上，每次都不能更新它们是一个显著的优化（更少的CAS）。
 * 我们使用两个松弛阈值；也就是说，当当前指针看起来距离第一个/最后一个节点两步或两步以上时，
 * 我们更新head/tail。
 *
 * Since head and tail are updated concurrently and independently,
 * it is possible for tail to lag behind head (why not)?
 * 既然头部和尾部是同时独立更新的，那么尾部有可能落后于头部？
 *
 * CASing a Node's item reference to null atomically removes the
 * element from the queue, leaving a "dead" node that should later
 * be unlinked (but unlinking is merely an optimization).
 * Interior element removal methods (other than Iterator.remove())
 * keep track of the predecessor node during traversal so that the
 * node can be CAS-unlinked.  Some traversal methods try to unlink
 * any deleted nodes encountered during traversal.  See comments
 * in bulkRemove.
 *
 * CASing将Node的item引用原子化地设置为null，来删除队列中的元素，
 * 留下一个稍后应该下链unlinking的“死”节点（但unlinking只是一种优化）。
 * 内部元素删除方法（而不是Iterator.remove（））在遍历期间跟踪前置任务（线程）节点，
 * 以便可以节点以CAS-unlinked断开该节点。
 * 一些遍历方法试图取消遍历过程中遇到的任何已删除节点的链接。
 *
 * When constructing a Node (before enqueuing it) we avoid paying
 * for a volatile write to item.  This allows the cost of enqueue
 * to be "one-and-a-half" CASes.
 *
 * Both head and tail may or may not point to a Node with a
 * non-null item.  If the queue is empty, all items must of course
 * be null.  Upon creation, both head and tail refer to a dummy
 * Node with null item.  Both head and tail are only updated using
 * CAS, so they never regress, although again this is merely an
 * optimization.
 *
 * 头和尾可以指向也可以不指向具有非空的数据item的节点。如果队列为空，
 * 那么所有item当然都必须为空。创建时，头和尾都引用了一个具有null的item的虚拟节点。
 * 头部和尾部都只使用CAS更新，所以它们永远不会回收，尽管这只是一个优化。
 */

/**
 * 具体算法同LTQ相似
 * @param <E>
 */

/**
 * *** Overview of Dual Queues with Slack ***
 *
 * Dual Queues, introduced by Scherer and Scott
 * (http://www.cs.rochester.edu/~scott/papers/2004_DISC_dual_DS.pdf)
 * are (linked) queues in which nodes may represent either data or
 * requests.  When a thread tries to enqueue a data node, but
 * encounters a request node, it instead "matches" and removes it;
 * and vice versa for enqueuing requests. Blocking Dual Queues
 * arrange that threads enqueuing unmatched requests block until
 * other threads provide the match.
 *
 *  双队列，由Scherer和Scott介绍是（链接的）队列，其中节点可以表示数据或请求。
 *  当线程试图将数据节点排入队列，但遇到请求节点时，它会“匹配”并删除它；
 *  反之亦然。阻塞双队列安排将不匹配的请求排入队列的线程阻塞，直到其他线程提供匹配为止。
 *
 * Dual Synchronous Queues (see
 * Scherer, Lea, & Scott
 * http://www.cs.rochester.edu/u/scott/papers/2009_Scherer_CACM_SSQ.pdf)
 * additionally arrange that threads enqueuing unmatched data also
 * block.  Dual Transfer Queues support all of these modes, as
 * dictated by callers.
 *
 * 双同步队列另外安排将不匹配的数据入队列的线程也阻塞。根据调用方的指示，
 * 双传输队列支持所有这些模式。
 *
 * A FIFO dual queue may be implemented using a variation of the
 * Michael & Scott (M&S) lock-free queue algorithm
 * (http://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf).
 * It maintains two pointer fields, "head", pointing to a
 * (matched) node that in turn points to the first actual
 * (unmatched) queue node (or null if empty); and "tail" that
 * points to the last node on the queue (or again null if
 * empty). For example, here is a possible queue with four data
 * elements:
 *
 * 指针字段“head”，指向一个（匹配的）节点，
 * 该节点又指向第一个实际（不匹配的）队列节点（如果为空，则为null）；
 *
 * 指向队列上最后一个节点的“tail”（如果为空，则为null）。
 * 例如，这里有一个可能包含四个数据元素的队列：
 *
 *  head                tail
 *    |                   |
 *    v                   v
 *    M -> U -> U -> U -> U
 *
 * The M&S queue algorithm is known to be prone to scalability and
 * overhead limitations when maintaining (via CAS) these head and
 * tail pointers. This has led to the development of
 * contention-reducing variants such as elimination arrays.
 * However, the nature of dual queues enables a simpler tactic for
 * improving M&S-style implementations when dual-ness is needed.
 *
 * 众所周知，M&S队列算法在维护（通过CAS）这些头和尾指针时容易受到可伸缩性和开销限制。
 * 这导致了诸如消除数组之类的减少争用的变体的发展。
 * 然而，当需要双重性时，双重队列的性质使改进M&S风格实现的策略变得更简单。
 *
 * In a dual queue, each node must atomically maintain its match
 * status. While there are other possible variants, we implement
 * this here as: for a data-mode node, matching entails CASing an
 * "item" field from a non-null data value to null upon match, and
 * vice-versa for request nodes, CASing from null to a data
 * value. (Note that the linearization properties of this style of
 * queue are easy to verify -- elements are made available by
 * linking, and unavailable by matching.) Compared to plain M&S
 * queues, this property of dual queues requires one additional
 * successful atomic operation per enq/deq pair. But it also
 * enables lower cost variants of queue maintenance mechanics. (A
 * variation of this idea applies even for non-dual queues that
 * support deletion of interior elements, such as
 * j.u.c.ConcurrentLinkedQueue.)
 *
 * 在双队列中，每个节点必须原子地保持其匹配状态。虽然还有其他可能的变体，
 * 但我们在这里实现为：对于数据模式节点，匹配需要在匹配时将“项itm”字段
 * 从非null数据值CASing为null，反之亦然，请求节点从null CASing为数据值。
 * （请注意，这种类型队列的线性化属性很容易验证——元素通过链接可用，而通过匹配不可用。）
 * 与普通M&S队列相比，双队列的这种属性需要每个enq/deq对额外进行一次成功的原子操作。
 * 但它也实现了队列维护机制的低成本变体。
 * （这个想法的变体甚至适用于支持删除内部元素的非双重队列，
 * 如*j.u.c.ConcurrentLinkedQueue。）
 *
 * Once a node is matched, its match status can never again
 * change.  We may thus arrange that the linked list of them
 * contain a prefix of zero or more matched nodes, followed by a
 * suffix of zero or more unmatched nodes. (Note that we allow
 * both the prefix and suffix to be zero length, which in turn
 * means that we do not use a dummy header.)  If we were not
 * concerned with either time or space efficiency, we could
 * correctly perform enqueue and dequeue operations by traversing
 * from a pointer to the initial node; CASing the item of the
 * first unmatched node on match and CASing the next field of the
 * trailing node on appends.  While this would be a terrible idea
 * in itself, it does have the benefit of not requiring ANY atomic
 * updates on head/tail fields.
 *
 * 一旦节点匹配，其匹配状态就不会再出现改变因此，
 * 我们可以安排他们的链接列表linked list包含零个或多个匹配节点的前缀，
 * 后跟零个或更多个不匹配节点的后缀。
 * （请注意，我们允许前缀和后缀都为零长度，这反过来意味着我们不使用伪标头。）
 * 如果我们不关心时间或空间效率，我们可以通过从指针遍历到初始节点来正确执行入队和出队操作；
 * 在匹配时对第一个不匹配节点的项item进行CAS处理，在追加时对后面节点的下一个字段进行CAS处理。
 * 虽然这本身就是一个糟糕的想法，但它的好处是不需要对头/尾字段进行任何原子更新。
 *
 * We introduce here an approach that lies between the extremes of
 * never versus always updating queue (head and tail) pointers.
 * This offers a tradeoff between sometimes requiring extra
 * traversal steps to locate the first and/or last unmatched
 * nodes, versus the reduced overhead and contention of fewer
 * updates to queue pointers. For example, a possible snapshot of
 * a queue is:
 *
 * 我们在这里介绍了一种介于从不更新队列（头和尾）指针与始终更新队列指针之间的方法。
 * 这在有时需要额外的遍历步骤来定位第一个和/或最后一个不匹配的节点，
 * 与减少队列指针更新的开销overhead和contention竞争之间提供了一种折衷。例如，队列的可能快照是：
 *
 *  head           tail
 *    |              |
 *    v              v
 *    M -> M -> U -> U -> U -> U
 *
 * The best value for this "slack" (the targeted maximum distance
 * between the value of "head" and the first unmatched node, and
 * similarly for "tail") is an empirical matter. We have found
 * that using very small constants in the range of 1-3 work best
 * over a range of platforms. Larger values introduce increasing
 * costs of cache misses and risks of long traversal chains, while
 * smaller values increase CAS contention and overhead.
 *
 * 这个“松弛”的最佳值（“头部”的值和第一个不匹配节点之间的目标最大距离，类似于“尾部”）
 * 是一个经验问题。我们发现，使用1-3范围内的非常小的常数在一系列平台上效果最好。
 * 较大的值会增加缓存未命中的成本和长遍历链的风险，而较小的值则会增加CAS争用和开销。
 *
 * Dual queues with slack differ from plain M&S dual queues by
 * virtue of only sometimes updating head or tail pointers when
 * matching, appending, or even traversing nodes; in order to
 * maintain a targeted slack.  The idea of "sometimes" may be
 * operationalized in several ways. The simplest is to use a
 * per-operation counter incremented on each traversal step, and
 * to try (via CAS) to update the associated queue pointer
 * whenever the count exceeds a threshold. Another, that requires
 * more overhead, is to use random number generators to update
 * with a given probability per traversal step.
 *
 * 具有松弛的双队列与普通M&S双队列的不同之处在于，在匹配、附加甚至遍历节点时，
 * 有时只更新头指针或尾指针；以便保持目标松弛。
 * “有时”的概念可以通过几种方式加以实施。
 * 最简单的方法是在每个遍历步骤上使用递增的per-operation计数器，
 * 并在计数超过阈值时尝试（通过CAS）更新相关的队列指针。
 * 另一个需要更多开销的方法是使用随机数生成器在每个遍历步骤中以给定的概率进行更新。
 *
 *
 * In any strategy along these lines, because CASes updating
 * fields may fail, the actual slack may exceed targeted slack.
 * However, they may be retried at any time to maintain targets.
 * Even when using very small slack values, this approach works
 * well for dual queues because it allows all operations up to the
 * point of matching or appending an item (hence potentially
 * allowing progress by another thread) to be read-only, thus not
 * introducing any further contention.  As described below, we
 * implement this by performing slack maintenance retries only
 * after these points.
 *
 * 在沿着这些路线的任何策略中，由于CAS更新字段可能会失败，因此实际松弛可能会超过目标松弛。
 * 但是，可以随时重试它们以维护目标。即使在使用非常小的松弛值时，这种方法也适用于双队列，
 * 因为它允许直到匹配或附加项目（因此可能允许另一个线程进行）的所有操作都是只读的，
 * 因此不会引入任何进一步的争用。
 * 如下所述，我们通过仅在这些点之后执行松弛维护重试来实现这一点.
 *
 * As an accompaniment to such techniques, traversal overhead can
 * be further reduced without increasing contention of head
 * pointer updates: Threads may sometimes shortcut the "next" link
 * path from the current "head" node to be closer to the currently
 * known first unmatched node, and similarly for tail. Again, this
 * may be triggered with using thresholds or randomization.
 *
 * 伴随着这些技术，遍历开销可以进一步减少，而不会增加头指针更新的争用：
 * 线程有时可能会从当前的“头”节点缩短“下一个”链接路径，
 * 使其更接近当前已知的第一个不匹配节点，尾部也是如此。
 * 同样，这可以通过使用阈值或随机化来触发.
 *
 *
 * These ideas must be further extended to avoid unbounded amounts
 * of costly-to-reclaim garbage caused by the sequential "next"
 * links of nodes starting at old forgotten head nodes:
 *
 * 必须进一步扩展这些思想，以避免从旧的被遗忘的头节点开始的节点的
 * 顺序“下一个”链接所导致的无限制的垃圾回收成本：
 *
 * if a GC
 * delays noticing that any arbitrarily old node has become
 * garbage, all newer dead nodes will also be unreclaimed.
 * (Similar issues arise in non-GC environments.)  To cope with
 * this in our implementation, upon CASing to advance the head
 * pointer, we set the "next" link of the previous head to point
 * only to itself; thus limiting the length of chains of dead nodes.
 * (We also take similar care to wipe out possibly garbage
 * retaining values held in other Node fields.)  However, doing so
 * adds some further complexity to traversal: If any "next"
 * pointer links to itself, it indicates that the current thread
 * has lagged behind a head-update, and so the traversal must
 * continue from the "head".  Traversals trying to find the
 * current tail starting from "tail" may also encounter
 * self-links, in which case they also continue at "head".
 *
 * 如果GC延迟注意到任何任意旧的节点都变成了垃圾，那么所有新的死节点也将不被回收。
 * （在非GC环境中也会出现类似的问题。）
 * 为了在我们的实现中解决这个问题，在CASing推进头指针时，
 * 我们将前一个头的“下一个”链接设置为仅指向它自己；
 * 从而限制了死节点的链的长度。
 * （我们也会采取类似的措施来清除其他Node字段中可能存在的垃圾保留值。）
 * 然而，这样做会增加遍历的复杂性：如果任何“下一个”指针链接到它自己，
 * 则表明当前线程落后于头部更新，因此遍历必须从“头部”开始继续。
 * 试图从“尾部”开始寻找当前尾部的遍历也可能遇到自链接，
 * 在这种情况下，它们也会在“头部”继续。
 *
 * It is tempting in slack-based scheme to not even use CAS for
 * updates (similarly to Ladan-Mozes & Shavit). However, this
 * cannot be done for head updates under the above link-forgetting
 * mechanics because an update may leave head at a detached node.
 * And while direct writes are possible for tail updates, they
 * increase the risk of long retraversals, and hence long garbage
 * chains, which can be much more costly than is worthwhile
 * considering that the cost difference of performing a CAS vs
 * write is smaller when they are not triggered on each operation
 * (especially considering that writes and CASes equally require
 * additional GC bookkeeping ("write barriers") that are sometimes
 * more costly than the writes themselves because of contention).
 *
 * 如果GC延迟注意到任何任意旧的节点都变成了垃圾，那么所有新的死节点也将不被回收。
 * （在非GC环境中也会出现类似的问题。）为了在我们的实现中解决这个问题，
 * 在CASing推进头指针时，我们将前一个头的“下一个”链接设置为仅指向它自己；
 * 从而限制了死节点的链的长度。
 * （我们也会采取类似的措施来清除其他Node字段中可能存在的垃圾保留值。）
 * 然而，这样做会增加遍历的复杂性：如果任何“下一个”指针链接到它自己，
 * 则表明当前线程落后于头部更新，因此遍历必须从“头部”开始继续。
 * 试图从“尾部”开始寻找当前尾部的遍历也可能遇到自链接，
 * 在这种情况下，它们也会在“头部”继续。
 *
 * *** Overview of implementation ***
 *
 * We use a threshold-based approach to updates, with a slack
 * threshold of two -- that is, we update head/tail when the
 * current pointer appears to be two or more steps away from the
 * first/last node. The slack value is hard-wired: a path greater
 * than one is naturally implemented by checking equality of
 * traversal pointers except when the list has only one element,
 * in which case we keep slack threshold at one. Avoiding tracking
 * explicit counts across method calls slightly simplifies an
 * already-messy implementation. Using randomization would
 * probably work better if there were a low-quality dirt-cheap
 * per-thread one available, but even ThreadLocalRandom is too
 * heavy for these purposes.
 *
 * 我们使用基于阈值的方法进行更新，两个松弛阈值——也就是说，
 * 当当前指针看起来距离第一个/最后一个节点两步或更多时，
 * 我们更新头/尾。松弛值是硬连接的：大于1的路径自然是通过检查遍历指针的相等性来实现的，
 * 除非列表只有一个元素，在这种情况下，我们将松弛阈值保持为1。
 * 避免在方法调用之间跟踪显式计数会稍微简化本已混乱的实现。
 * 如果有低质量、低成本的每线程，使用随机化可能会更好，
 * 但即使ThreadLocalRandom对于这些目的来说也太重了。
 *
 * With such a small slack threshold value, it is not worthwhile
 * to augment this with path short-circuiting (i.e., unsplicing
 * interior nodes) except in the case of cancellation/removal (see
 * below).
 *
 * 在这样一个小的松弛阈值的情况下，除了在取消/移除的情况下（见下文），
 * 不值得用路径短路（即，解开内部节点，从节点下链）来增加这一点。
 *
 * All enqueue/dequeue operations are handled by the single method
 * "xfer" with parameters indicating whether to act as some form
 * of offer, put, poll, take, or transfer (each possibly with
 * timeout). The relative complexity of using one monolithic
 * method outweighs the code bulk and maintenance problems of
 * using separate methods for each case.
 *
 * 所有入队/出队操作都由单一方法“xfer”处理，参数指示是否作为某种形式的
 * offer、put、poll、take或transfer（每个可能都有超时）。
 * 使用单一方法的相对复杂性超过了在每种情况下使用单独方法的代码量和维护问题。
 *
 * Operation consists of up to two phases. The first is implemented
 * in method xfer, the second in method awaitMatch.
 * 操作最多包括两个阶段。第一个在xfer方法中实现，第二个在awaitMatch方法中实现。
 *
 * 1. Traverse until matching or appending (method xfer)
 *    遍历直到匹配或追加（方法xfer）
 *
 *    Conceptually, we simply traverse all nodes starting from head.
 *    If we encounter an unmatched node of opposite mode, we match
 *    it and return, also updating head (by at least 2 hops) to
 *    one past the matched node (or the node itself if it's the
 *    pinned trailing node).  Traversals also check for the
 *    possibility of falling off-list, in which case they restart.
 *
 *    从概念上讲，我们只是从头开始遍历所有节点。如果我们遇到相反模式的不匹配节点，
 *    我们将其匹配并返回，同时将head（至少2跳）更新为一个超过匹配节点的节点
 *    （或节点本身，如果它是固定的尾随节点）。
 *    遍历还检查从列表中掉下来的可能性，在这种情况下，它们会重新启动
 *
 *    If the trailing node of the list is reached, a match is not
 *    possible.  If this call was untimed poll or tryTransfer
 *    (argument "how" is NOW), return empty-handed immediately.
 *    Else a new node is CAS-appended.  On successful append, if
 *    this call was ASYNC (e.g. offer), an element was
 *    successfully added to the end of the queue and we return.
 *
 *    如果已到达列表的尾部节点，则无法进行匹配。
 *    如果此调用是无计时轮询或tryTransfer（参数“how”是NOW），请立即空手而归。
 *    否则，将附加一个新节点CAS。在成功追加时，如果此调用是ASYNC（例如offer），
 *    则会将一个元素成功添加到队列的末尾，然后返回
 *
 *    Of course, this naive traversal is O(n) when no match is
 *    possible.  We optimize the traversal by maintaining a tail
 *    pointer, which is expected to be "near" the end of the list.
 *    It is only safe to fast-forward to tail (in the presence of
 *    arbitrary concurrent changes) if it is pointing to a node of
 *    the same mode, even if it is dead (in this case no preceding
 *    node could still be matchable by this traversal).  If we
 *    need to restart due to falling off-list, we can again
 *    fast-forward to tail, but only if it has changed since the
 *    last traversal (else we might loop forever).  If tail cannot
 *    be used, traversal starts at head (but in this case we
 *    expect to be able to match near head).  As with head, we
 *    CAS-advance the tail pointer by at least two hops.
 *
 *    当然，当不可能匹配时，这种天真的遍历是O（n）。
 *    我们通过维护一个尾部指针来优化遍历，该指针应该“接近”列表的末尾。
 *    只有当它指向相同模式的节点时（在存在任意并发更改的情况下），
 *    即使它已经死了（在这种情况下，前面的节点仍然无法通过这种遍历进行匹配），
 *    才可以安全地快进到尾部。如果我们因为从列表中掉下来而需要重新启动，
 *    我们可以再次快进到尾部，但前提是它自上次遍历以来发生了变化（
 *    否则我们可能会永远循环）。如果不能使用tail，
 *    遍历从head开始（但在这种情况下，我们希望能够在head附近匹配）。
 *    与head一样，我们CAS将尾部指针向前移动至少两个跳跃。
 *
 * 2. Await match or cancellation (method awaitMatch)
 *    等待匹配或取消（方法awaitMatch）
 *
 *    Wait for another thread to match node; instead cancelling if
 *    the current thread was interrupted or the wait timed out. To
 *    improve performance in common single-source / single-sink
 *    usages when there are more tasks that cores, an initial
 *    Thread.yield is tried when there is apparently only one
 *    waiter.  In other cases, waiters may help with some
 *    bookkeeping, then park/unpark.
 *
 *    等待另一个线程匹配节点；而是在当前线程被中断或等待超时时取消。
 *    当有更多的任务核心化时，为了提高常见的单源/单汇使用的性能，
 *    当显然只有一个等候线程时，会尝试初始的Thread.feld。
 *    在其他情况下，等候线程（waiters）可能会帮忙记账，然后停车/停车。
 *
 * ** Unlinking removed interior nodes **
 *
 * In addition to minimizing garbage retention via self-linking
 * described above, we also unlink removed interior nodes. These
 * may arise due to timed out or interrupted waits, or calls to
 * remove(x) or Iterator.remove.  Normally, given a node that was
 * at one time known to be the predecessor of some node s that is
 * to be removed, we can unsplice s by CASing the next field of
 * its predecessor if it still points to s (otherwise s must
 * already have been removed or is now offlist). But there are two
 * situations in which we cannot guarantee to make node s
 * unreachable in this way:
 *
 * 除了通过上述自链接最大限度地减少垃圾保留外，我们还取消了已删除的内部节点的链接。
 * 这些可能是由于超时或中断的等待，或调用remove（x）或Iterator.remove而导致的。
 * 通常，给定一个节点曾经是要删除的某个节点s的前代节点，如果它仍然指向s，
 * 我们可以通过对其前代节点的下一个字段进行CASing来取消复制s
 *（否则s必须已经被删除或现在处于脱机状态）。
 * 但有两种情况我们不能保证通过这种方式使节点s不可达：
 *
 * (1) If s is the trailing node of list
 * (i.e., with null next), then it is pinned as the target node
 * for appends, so can only be removed later after other nodes are
 * appended.
 * 如果s是列表的尾部节点（即下一个为null），则它被固定为附加的目标节点，
 * 因此只能在附加其他节点后才能删除。
 *
 * (2) We cannot necessarily unlink s given a
 * predecessor node that is matched (including the case of being
 * cancelled): the predecessor may already be unspliced, in which
 * case some previous reachable node may still point to s.
 *.Although, in both
 * cases, we can rule out the need for further action if either s
 * or its predecessor are (or can be made to be) at, or fall off
 * from, the head of list.
 *
 * 给定匹配的前置节点（包括被取消的情况），我们不一定要取消s的链接：
 * 前置节点可能已经被取消了链接，在这种情况下，一些先前可到达的节点可能仍然指向s。
 * 尽管在这两种情况下，如果s或其前置节点位于（或可以被设置为）
 * 列表的头部或从列表的头部脱落，我们可以排除需要进一步操作的可能性。
 *
 * Without taking these into account, it would be possible for an
 * unbounded number of supposedly removed nodes to remain reachable.
 * Situations leading to such buildup are uncommon but can occur
 * in practice; for example when a series of short timed calls to
 * poll repeatedly time out at the trailing node but otherwise
 * never fall off the list because of an untimed call to take() at
 * the front of the queue.
 *
 * 如果不考虑这些，无限数量的假定已删除的节点将有可能保持可达。
 * 导致这种堆积的情况并不常见，但在实践中可能会发生；
 * 例如，当一系列短时间的轮询调用在尾部节点重复超时，
 * 但由于队列前面有一个未计时的take（）调用，因此永远不会从列表中掉出来。
 *
 * When these cases arise, rather than always retraversing the
 * entire list to find an actual predecessor to unlink (which
 * won't help for case (1) anyway), we record the need to sweep the
 * next time any thread would otherwise block in awaitMatch. Also,
 * because traversal operations on the linked list of nodes are a
 * natural opportunity to sweep dead nodes, we generally do so,
 * including all the operations that might remove elements as they
 * traverse, such as removeIf and Iterator.remove.  This largely
 * eliminates long chains of dead interior nodes, except from
 * cancelled or timed out blocking operations.
 *
 * 当出现这些情况时，我们不是总是重新转换整个列表以找到要取消链接的实际前置线程
 * （无论如何，这对情况（1）都没有帮助），
 * 而是记录下下次任何线程在awaitMatch中阻塞时需要清除的情况。
 * 此外，由于节点链表上的遍历操作是清除死节点的自然机会，
 * 我们通常会这样做，包括所有可能在遍历时删除元素的操作，
 * 如removeIf和Iterator.remove。
 * 这在很大程度上消除了死内部节点的长链，除了取消或超时的阻塞操作。
 *
 * Note that we cannot self-link unlinked interior nodes during
 * sweeps. However, the associated garbage chains terminate when
 * some successor ultimately falls off the head of the list and is
 * self-linked.
 * 请注意，我们不能在扫描过程中自链接未链接的内部节点。
 * 然而，当某个后续的垃圾链最终从列表的头上掉下来并自链接时，相关的垃圾链就会终止。
 */

public class ConcurrentLinked<E> implements java.io.Serializable {

    static final class Node<E> {
        volatile E item;
        volatile Node<E> next;

        //LTQ
        final boolean isData;
        volatile Thread waiter; // null when not waiting for a match
        /**
         * Constructs a node holding item.  Uses relaxed write because
         * item can only be seen after piggy-backing publication via CAS.
         */
        Node(E item) {
            ITEM.set(this, item);
            isData = (item != null);
        }

        /** Constructs a dead dummy node. */
        Node() {
            isData = true;
        }

//        boolean casItem(E cmp, E val) {
//            // assert item == cmp || item == null;
//            // assert cmp != null;
//            // assert val == null;
//            return ITEM.compareAndSet(this, cmp, val);
//        }

        final boolean casItem(Object cmp, Object val) {
            // assert isData == (cmp != null);
            // assert isData == (val == null);
            // assert !(cmp instanceof Node);
            return ITEM.compareAndSet(this, cmp, val);
        }

        /** Tries to CAS-match this node; if successful, wakes waiter. */


        final boolean casNext(Node cmp, Node val) {
            // assert val != null;
            return NEXT.compareAndSet(this, cmp, val);
        }

        /**
         * Links node to itself to avoid garbage retention.  Called
         * only after CASing head field, so uses relaxed write.
         */
        final void selfLink() {
            // assert isMatched();
            NEXT.setRelease(this, this);
        }

        final void appendRelaxed(Node next) {
            // assert next != null;
            // assert this.next == null;
            NEXT.setOpaque(this, next);
        }

        /**
         * Returns true if this node has been matched, including the
         * case of artificial matches due to cancellation.
         */
        final boolean isMatched() {
            return isData == (item == null);
        }

        /** Tries to CAS-match this node; if successful, wakes waiter. */
        final boolean tryMatch(Object cmp, Object val) {
            if (casItem(cmp, val)) {
                LockSupport.unpark(waiter);
                return true;
            }
            return false;
        }

    }

    /**
     * A node from which the first live (non-deleted) node (if any)
     * can be reached in O(1) time.
     * Invariants:
     * - all live nodes are reachable from head via succ()
     * - head != null
     * - (tmp = head).next != tmp || tmp != head
     * Non-invariants:
     * - head.item may or may not be null.
     * - it is permitted for tail to lag behind head, that is, for tail
     *   to not be reachable from head!
     */
    transient volatile Node<E> head;

    /**
     * A node from which the last node on list (that is, the unique
     * node with node.next == null) can be reached in O(1) time.
     * Invariants:
     * - the last node is always reachable from tail via succ()
     * - tail != null
     * Non-invariants:
     * - tail.item may or may not be null.
     * - it is permitted for tail to lag behind head, that is, for tail
     *   to not be reachable from head!
     * - tail.next may or may not be self-linked.
     */
    private transient volatile Node<E> tail;

    /**
     * Creates a {@code ConcurrentLinkedQueue} that is initially empty.
     */
    public ConcurrentLinked() {
        head = tail = new Node<E>();
    }

    public void put(E e) {
        xfer(e, true, ASYNC, 0L);
    }
    /**
     * Tries to CAS head to p. If successful, repoint old head to itself
     * as sentinel for succ(), below.
     */
    final void updateHead(Node<E> h, Node<E> p) {
        // assert h != null && p != null && (h == p || h.item == null);
        if (h != p && HEAD.compareAndSet(this, h, p))
            NEXT.setRelease(h, h);
    }

    /**
     * Returns the successor of p, or the head node if p.next has been
     * linked to self, which will only be true if traversing with a
     * stale pointer that is now off the list.
     */
    final Node<E> succ(Node<E> p) {
        if (p == (p = p.next))
            p = head;
        return p;
    }


    /**
     * Inserts the specified element at the tail of this queue.
     * As the queue is unbounded, this method will never return {@code false}.
     *
     * @return {@code true} (as specified by {@link Queue#offer})
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        final Node<E> newNode = new Node<E>(Objects.requireNonNull(e));

        for (Node<E> t = tail, p = t;;) {
            Node<E> q = p.next;
            if (q == null) {
                // p is last node
                if (NEXT.compareAndSet(p, null, newNode)) {
                    // Successful CAS is the linearization point
                    // for e to become an element of this queue,
                    // and for newNode to become "live".
                    if (p != t) // hop two nodes at a time; failure is OK
                        TAIL.weakCompareAndSet(this, t, newNode);
                    return true;
                }
                // Lost CAS race to another thread; re-read next
            }
            else if (p == q)
                // We have fallen off list.  If tail is unchanged, it
                // will also be off-list, in which case we need to
                // jump to head, from which all live nodes are always
                // reachable.  Else the new tail is a better bet.
                p = (t != (t = tail)) ? t : head;
            else
                // Check for tail updates after two hops.
                p = (p != t && t != (t = tail)) ? t : q;
        }
    }

    public boolean add(E e) {
        xfer(e, true, ASYNC, 0L);
        return true;
    }

    private static final int NOW   = 0; // for untimed poll, tryTransfer
    private static final int ASYNC = 1; // for offer, put, add
    private static final int SYNC  = 2; // for transfer, take
    private static final int TIMED = 3; // for timed poll, tryTransfer

    /**
     * Implements all queuing methods. See above for explanation.
     *
     * @param e the item or null for take
     * @param haveData true if this is a put, else a take
     * @param how NOW, ASYNC, SYNC, or TIMED
     * @param nanos timeout in nanosecs, used only if mode is TIMED
     * @return an item if matched, else e
     * @throws NullPointerException if haveData mode but e is null
     */
    @SuppressWarnings("unchecked")
    private E xfer(E e, boolean haveData, int how, long nanos) {
        if (haveData && (e == null))
            throw new NullPointerException();

        restart: for (Node s = null, t = null, h = null;;) {
            for (Node p = (t != (t = tail) && t.isData == haveData) ? t
                    : (h = head);; ) {
                final Node q; final Object item;
                if (p.isData != haveData
                        && haveData == ((item = p.item) == null)) {
                    if (h == null) h = head;
                    if (p.tryMatch(item, e)) {
                        if (h != p) skipDeadNodesNearHead(h, p);
                        return (E) item;
                    }
                }
                //链的操作
                if ((q = p.next) == null) {
                    if (how == NOW) return e;
                    if (s == null) s = new Node(e);
                    if (!p.casNext(null, s)) continue;
                    if (p != t) casTail(t, s);
                    if (how == ASYNC) return e;
                    return awaitMatch(s, p, e, (how == TIMED), nanos);
                }
                if (p == (p = q)) continue restart;
            }
        }


    }
    private E awaitMatch(Node s, Node pred, E e, boolean timed, long nanos){
        //具体实现见LTS
        return null;
    }

    /**
     * Collapses dead (matched) nodes from h (which was once head) to p.
     * Caller ensures all nodes from h up to and including p are dead.
     */
    private void skipDeadNodesNearHead(Node h, Node p) {
        // assert h != null;
        // assert h != p;
        // assert p.isMatched();
        for (;;) {
            final Node q;
            if ((q = p.next) == null) break;
            else if (!q.isMatched()) { p = q; break; }
            else if (p == (p = q)) return;
        }
        if (casHead(h, p))
            h.selfLink();
    }

    private boolean casHead(Node cmp, Node val) {
        return HEAD.compareAndSet(this, cmp, val);
    }

    private boolean casTail(Node cmp, Node val) {
        // assert cmp != null;
        // assert val != null;
        return TAIL.compareAndSet(this, cmp, val);
    }

    // VarHandle mechanics
    private static final VarHandle HEAD;
    private static final VarHandle TAIL;
    static final VarHandle ITEM;
    static final VarHandle NEXT;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            HEAD = l.findVarHandle(ConcurrentLinked.class, "head",
                   Node.class);
            TAIL = l.findVarHandle(ConcurrentLinked.class, "tail",
                    Node.class);
            ITEM = l.findVarHandle(Node.class, "item", Object.class);
            NEXT = l.findVarHandle(Node.class, "next", Node.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
