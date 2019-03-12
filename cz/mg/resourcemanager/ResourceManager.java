package cz.mg.resourcemanager;

import java.util.LinkedList;


public class ResourceManager<O, R extends AutoCloseable> {
    private final LinkedList<ResourceOwnerReference<O, R>> references = new LinkedList<>();
    private final LinkedList<ResourceController<R>> controllers = new LinkedList<>();
    private final Trash trash = new Trash();

    public synchronized int count(){
        return controllers.size();
    }

    public synchronized void add(O owner, R resource){
        add(owner, resource, null);
    }

    public synchronized void add(O owner, R resource, R parent){
        if(owner == null || resource == null || owner == resource || resource == parent || owner == parent) throw new IllegalArgumentException();

        ResourceController<R> parentController = find(parent);
        if(parent != null && parentController == null) throw new RuntimeException("Could not find parent resource.");

        ResourceController<R> controller = addResource(resource);
        controller.associateParent(parentController);
        controller.increaseReferenceCount();

        references.addLast(new ResourceOwnerReference(owner, controller, trash));
    }

    public synchronized void free(boolean force){
        boolean restart = true;
        while(restart){
            restart = false;

            ResourceOwnerReference<O, R> reference;
            while((reference = (ResourceOwnerReference<O, R>) trash.poll()) != null){
                restart = true;
                references.remove(reference);
                reference.getController().decreaseReferenceCount();
            }

            LinkedList<ResourceController<R>> freedControllers = new LinkedList<>();
            for(ResourceController<R> controller : controllers){
                if(controller.getChildrenCount() <= 0 && controller.getReferenceCount() <= 0){
                    restart = true;
                    controller.free(force);
                    freedControllers.add(controller);
                }
            }

            for(ResourceController<R> controller : freedControllers){
                controllers.remove(controller);
            }
        }
    }

    private ResourceController<R> find(R resource){
        for(ResourceController<R> controller : controllers) if(controller.getResource() == resource) return controller;
        return null;
    }

    private ResourceController<R> addResource(R resource){
        ResourceController<R> controller = find(resource);
        if(controller == null) controllers.addLast(controller = new ResourceController<>(resource));
        return controller;
    }
}
