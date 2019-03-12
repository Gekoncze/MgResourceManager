package cz.mg.resourcemanager;

import java.lang.ref.PhantomReference;


class ResourceOwnerReference<O, R extends AutoCloseable> extends PhantomReference<O> {
    private final ResourceController<R> controller;

    public ResourceOwnerReference(O owner, ResourceController<R> controller, Trash queue) {
        super(owner, queue);
        this.controller = controller;
    }

    public ResourceController<R> getController() {
        return controller;
    }
}