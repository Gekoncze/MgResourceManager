package cz.mg.resourcemanager;

import java.lang.ref.PhantomReference;


class ResourceOwnerReference<O, R extends AutoCloseable> extends PhantomReference<O> {
    private final ResourceController<R> controller;

    ResourceOwnerReference(O owner, ResourceController<R> controller, ResourceTrash queue) {
        super(owner, queue);
        this.controller = controller;
    }

    ResourceController<R> getController() {
        return controller;
    }
}