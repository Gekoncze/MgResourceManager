package cz.mg.resourcemanager;


class ResourceController<R extends AutoCloseable> {
    private R resource;
    private int referenceCount = 0;

    ResourceController(R resource) {
        this.resource = resource;
    }

    R getResource() {
        return resource;
    }

    int getReferenceCount() {
        return referenceCount;
    }

    void increaseReferenceCount(){
        referenceCount++;
    }

    void decreaseReferenceCount(){
        referenceCount--;
    }

    void free(){
        if(resource != null) {
            close();
            resource = null;
        }
    }

    private void close(){
        try {
            resource.close();
        } catch (Exception e) {}
    }
}
