package cz.mg.resourcemanager;

import java.util.LinkedList;


public class ResourceController<R extends AutoCloseable> {
    private final LinkedList<ResourceController<R>> parentControllers = new LinkedList<>();
    private R resource;
    private int childrenCount = 0;
    private int referenceCount = 0;

    ResourceController(R resource) {
        this.resource = resource;
    }

    void associateParent(ResourceController<R> parentController){
        if(parentController == null) return;
        if(!parentControllers.contains(parentController)){
            parentControllers.add(parentController);
            parentController.increaseChildrenCount();
        }
    }

    public R getResource() {
        return resource;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    private void increaseChildrenCount(){
        childrenCount++;
    }

    private void decreaseChildrenCount(){
        childrenCount--;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    void increaseReferenceCount(){
        referenceCount++;
    }

    void decreaseReferenceCount(){
        referenceCount--;
    }

    void free(boolean force){
        if(childrenCount > 0) throw new RuntimeException("Cannot free parent resource until all of its children are freed.");
        if(referenceCount > 0) throw new RuntimeException("Cannot free owned resource.");
        if(resource != null) {
            close(force);
            resource = null;
            for(ResourceController<R> parentController : parentControllers) parentController.decreaseChildrenCount();
            parentControllers.clear();
        }
    }

    private void close(boolean force){
        try {
            resource.close();
        } catch (Exception e) {
            if(!force) throw new RuntimeException(e);
        }
    }
}
