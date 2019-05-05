package cz.mg.resourcemanager;

import cz.mg.collections.list.chainlist.ChainList;


public class ResourceManager<O, R extends AutoCloseable> {
    private final ChainList<ResourceOwnerReference<O, R>> references = new ChainList<>();
    private final ChainList<ResourceController<R>> controllers = new ChainList<>();
    private final ResourceTrash trash = new ResourceTrash();

    public ResourceManager() {
    }

    public synchronized int getReferenceCount(){
        return references.count();
    }

    public synchronized int getResourceCount(){
        return controllers.count();
    }

    public synchronized R getResource(int i){
        return controllers.get(i).getResource();
    }

    public synchronized O getReference(int i){
        return references.get(i).get();
    }

    public synchronized void add(O owner, R resource){
        if(owner == null || resource == null || owner == resource) throw new IllegalArgumentException();
        ResourceController<R> controller = addResource(resource);
        references.addLast(new ResourceOwnerReference(owner, controller, trash));
        controller.increaseReferenceCount();
    }

    private ResourceController<R> addResource(R resource){
        ResourceController<R> controller = find(resource);
        if(controller == null) controllers.addLast(controller = new ResourceController<>(resource));
        return controller;
    }

    private ResourceController<R> find(R resource){
        for(ResourceController<R> controller : controllers) if(controller.getResource() == resource) return controller;
        return null;
    }

    public synchronized boolean free(){
        boolean change = false;
        ResourceOwnerReference<O, R> reference;
        while((reference = (ResourceOwnerReference<O, R>) trash.poll()) != null){
            change = true;
            references.remove(reference);

            ResourceController<R> controller = reference.getController();
            controller.decreaseReferenceCount();
            if(controller.getReferenceCount() <= 0){
                controller.free();
                controllers.remove(controller);
            }
        }
        return change;
    }

    public void waitFreeAll(){
        while(getResourceCount() > 0){
            free();
            try {
                System.gc(); // optional
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
