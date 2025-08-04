/**
 * Provides a lightweight pipeline abstraction with middleware-style tasks and consumers.
 * <p>
 * This module is designed to unify synchronous and asynchronous execution flows
 * using clearly separated types for sync, async, and unified tasks.
 * It builds on top of {@code jfunc}, {@code jsync}, and {@code juni} modules to offer
 * flexible and composable task chains.
 *
 * <p>Main features include:
 * <ul>
 *   <li>Middleware-based task consumers</li>
 *   <li>Support for sync, async, and unified task types</li>
 *   <li>Composable pipeline execution semantics</li>
 * </ul>
 */
module com.github.romanqed.jconv {
    // Imports
    requires com.github.romanqed.jfunc;
    requires com.github.romanqed.jsync;
    requires com.github.romanqed.juni;
    // Exports
    exports com.github.romanqed.jconv;
}
